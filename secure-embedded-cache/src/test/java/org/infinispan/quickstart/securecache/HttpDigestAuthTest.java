package org.infinispan.quickstart.securecache;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Arquillian.class)
public class HttpDigestAuthTest {

   private static HttpHost target;

   private enum HTTP_METHOD {GET, PUT, DELETE}

   ;

   private enum HTTP_RESPONSE {CODE, TEXT}

   ;

   private CloseableHttpClient adminClient;
   private CloseableHttpClient readerClient;

   //properties defined in pom.xml
   private static final String ARCHIVE_NAME = System.getProperty("quickstart.war.file");
   private static final String BUILD_DIRECTORY = System.getProperty("quickstart.war.directory");

   @Deployment(testable = false)
   public static WebArchive createDeployment() {
      WebArchive archive = ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
            .as(WebArchive.class);

      return archive;
   }

   @Before
   public void setUp() {
      target = new HttpHost("127.0.0.1", 8080, "http");
      adminClient = getHttpClient("adminUser", "adminUserPass9!");
      readerClient = getHttpClient("readerUser", "readerUserPass9!");
   }

   @Test
   public void test1AdminPut() throws Exception {
      String response = getReponseString(adminClient, "/rest/cache/put?key=K1&value=V1", HTTP_METHOD.PUT, HTTP_RESPONSE.TEXT);
      assertTrue(!response.contains("Unauthorized access"));
   }

   @Test
   public void test2ReaderPut() throws Exception {
      String response = getReponseString(readerClient, "/rest/cache/put?key=K1&value=V1", HTTP_METHOD.PUT, HTTP_RESPONSE.TEXT);
      assertTrue(response.contains("Unauthorized access"));

   }

   @Test
   public void test3ReaderGet() throws Exception {
      String response = getReponseString(readerClient, "/rest/cache/get?key=K1", HTTP_METHOD.GET, HTTP_RESPONSE.TEXT);
      assertTrue(!response.contains("Unauthorized access"));
   }

   @Test
   public void test4ReaderDelete() throws Exception {
      String response = getReponseString(readerClient, "/rest/cache/remove?key=K1&value=V1", HTTP_METHOD.DELETE, HTTP_RESPONSE.TEXT);
      assertTrue(response.contains("Unauthorized access"));
   }

   @Test
   public void test5AdminDelete() throws Exception {
      String response = getReponseString(adminClient, "/rest/cache/remove?key=K1&value=V1", HTTP_METHOD.DELETE, HTTP_RESPONSE.TEXT);
      assertTrue(!response.contains("Unauthorized access"));
   }

   private static CloseableHttpClient getHttpClient(String username, String password) {
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(
            new AuthScope(target.getHostName(), target.getPort()),
            new UsernamePasswordCredentials(username, password));
      return HttpClients.custom()
            .setDefaultCredentialsProvider(credsProvider).build();

   }

   private static String getReponseString(CloseableHttpClient httpClient, String urlAdd, HTTP_METHOD httpMethod, HTTP_RESPONSE httpResponse) throws IOException {
      String baseContextPath = "/jboss-secure-embedded-cache-quickstart";
      AuthCache authCache = new BasicAuthCache();
      DigestScheme digestAuth = new DigestScheme();
      digestAuth.overrideParamter("realm", "ApplicationRealm");
      authCache.put(target, digestAuth);

      HttpClientContext localContext = HttpClientContext.create();
      localContext.setAuthCache(authCache);
      CloseableHttpResponse response;

      switch (httpMethod) {
         case PUT:
            HttpPut httpput = new HttpPut(baseContextPath + urlAdd);
            System.out.println("Executing request " + httpput.getRequestLine() + " to target " + target);
            response = httpClient.execute(target, httpput, localContext);
            break;
         case DELETE:
            HttpDelete httpdel = new HttpDelete(baseContextPath + urlAdd);
            System.out.println("Executing request " + httpdel.getRequestLine() + " to target " + target);
            response = httpClient.execute(target, httpdel, localContext);
            break;
         case GET:
         default:
            HttpGet httpget = new HttpGet(baseContextPath + urlAdd);
            System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
            response = httpClient.execute(target, httpget, localContext);
            break;
      }

      try {
         switch (httpResponse) {
            case TEXT:
               return EntityUtils.toString(response.getEntity());
            case CODE:
            default:
               return response.getStatusLine().toString();
         }
      } finally {
         response.close();
      }
   }
}
