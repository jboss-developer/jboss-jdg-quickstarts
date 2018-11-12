package org.infinispan.quickstart.securecache;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientInitialAccessPresentation;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.TestsHelper.deleteClient;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Arquillian.class)
public class HttpKeycloakAuthTest {

   private enum HTTP_METHOD {GET, PUT, DELETE};

   private static final String ARCHIVE_NAME = System.getProperty("quickstart.war.file");
   private static final String BUILD_DIRECTORY = System.getProperty("quickstart.war.directory");

   static {
      TestsHelper.appName = "jboss-secure-embedded-cache-quickstart";
      TestsHelper.baseUrl = "http://localhost:8080/jboss-secure-embedded-cache-quickstart/";
      TestsHelper.testRealm = "demo";
   }

   @Deployment(testable = false)
   public static WebArchive createDeployment() {
      WebArchive archive = ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME)
              .importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
              .as(WebArchive.class);
      TestsHelper.initialAccessCode = getAdminAccessToken(); //required to create the client
      archive.addAsWebInfResource(new StringAsset(TestsHelper.createClient(ClientBuilder.create("test-dga")
              .baseUrl(TestsHelper.baseUrl).accessType(PUBLIC))), "keycloak.json");
      return archive;
   }

   @AfterClass
   public static void afterClass() {
      TestsHelper.initialAccessCode = getAdminAccessToken();
      TestsHelper.deleteClient("test-dga");
   }

   @Test
   public void test1AdminPut() throws Exception {
      String accessToken = getUserAccessToken( "admin", "Strong_password");
      assertFalse(executePut("rest/cache/put?key=K1&value=V1", accessToken)
              .contains("Unauthorized access"));

   }

   @Test
   public void test2ReaderPut() throws Exception {
      String accessToken = getUserAccessToken("reader", "Password_");
      assertTrue(executePut("rest/cache/put?key=K1&value=V1", accessToken)
              .contains("Unauthorized access"));
   }

   @Test
   public void test3ReaderGet() throws Exception {
      String accessToken = getUserAccessToken( "reader", "Password_");
      assertFalse(executeGet("rest/cache/get?key=K1", accessToken)
              .contains("Unauthorized access"));
   }

   @Test
   public void test4ReaderRemove() throws Exception {
      String accessToken = getUserAccessToken( "reader", "Password_");
      assertTrue(executeDelete("rest/cache/remove?key=K1&value=V1", accessToken)
              .contains("Unauthorized access"));
   }

   @Test
   public void test5AdminRemove() throws Exception {
      String accessToken = getUserAccessToken( "admin", "Strong_password");
      assertFalse(executeDelete("rest/cache/remove?key=K1&value=V1", accessToken)
              .contains("Unauthorized access"));
   }

   private static String getAdminAccessToken() {
      Keycloak keycloak = Keycloak.getInstance(TestsHelper.keycloakBaseUrl, "master",
              "johndoe", "password", "admin-cli");
      ClientInitialAccessCreatePresentation rep = new ClientInitialAccessCreatePresentation();
      rep.setCount(Integer.valueOf(2));
      rep.setExpiration(Integer.valueOf(100));
      ClientInitialAccessPresentation initialAccess = keycloak.realms()
              .realm(TestsHelper.testRealm).clientInitialAccess().create(rep);
      return initialAccess.getToken();
   }

   private static String getUserAccessToken(String username, String password) {
      Keycloak keycloak = Keycloak.getInstance(TestsHelper.keycloakBaseUrl, "demo", username, password, "test-dga");
      return keycloak.tokenManager().getAccessTokenString();
   }

   private String executeGet(String contextPath, String token) throws IOException {
      return executeMethod(contextPath, token, HTTP_METHOD.GET);
   }

   private String executePut(String contextPath, String token) throws IOException {
      return executeMethod(contextPath, token, HTTP_METHOD.PUT);
   }

   private String executeDelete(String contextPath, String token) throws IOException {
      return executeMethod(contextPath, token, HTTP_METHOD.DELETE);
   }

   private String executeMethod(String contextPath, String token, HTTP_METHOD method) throws IOException {
      CloseableHttpClient client = HttpClientBuilder.create().build();
      try {
         HttpResponse response = null;
         String uri = TestsHelper.baseUrl + contextPath;
         Header authHeader = new BasicHeader("Authorization", "Bearer " + token);
         switch (method) {
            case PUT:
               HttpPut put = new HttpPut(uri);
               put.addHeader(authHeader);
               response = client.execute(put);
               break;
            case GET:
               HttpGet get = new HttpGet(uri);
               get.addHeader(authHeader);
               response = client.execute(get);
               break;
            case DELETE:
               HttpDelete delete = new HttpDelete(uri);
               delete.addHeader(authHeader);
               response = client.execute(delete);
               break;
         }
         if (response.getStatusLine().getStatusCode() != 200) {
            Assert.fail("Request not successful");
         }
         return EntityUtils.toString(response.getEntity());
      } finally {
         client.close();
      }
   }
}
