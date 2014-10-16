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
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore(value = "This test assumes that JBoss AS/EAP is already running. Not always true. BZ1150949")
public class HttpDigestAuthTest {

	private static HttpHost target;
	private enum HTTP_METHOD { GET, PUT, DELETE};
	private enum HTTP_RESPONSE { CODE, TEXT};
	private CloseableHttpClient adminClient;
	private CloseableHttpClient readerClient;

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
	
	
	private static CloseableHttpClient getHttpClient(String username,String password) {
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
		
		switch(httpMethod) {
		case PUT:
			HttpPut httpput = new HttpPut(baseContextPath+ urlAdd);
			System.out.println("Executing request " + httpput.getRequestLine()+ " to target " + target);
			response = httpClient.execute(target, httpput,localContext);
			break;
		case DELETE:
			HttpDelete httpdel = new HttpDelete(baseContextPath+ urlAdd);
			System.out.println("Executing request " + httpdel.getRequestLine()+ " to target " + target);
			response = httpClient.execute(target, httpdel,localContext);
			break;	
		case GET:
		default:
			HttpGet httpget = new HttpGet(baseContextPath+ urlAdd);
			System.out.println("Executing request " + httpget.getRequestLine()+ " to target " + target);
			response = httpClient.execute(target, httpget,localContext);
			break;
		}

		try {
			switch(httpResponse) {
				case TEXT :
					return EntityUtils.toString(response.getEntity());
				case CODE:
			    default: return response.getStatusLine().toString();
			}
		} finally {
			response.close();
		}
	}
}
