package org.infinispan.quickstart.openshift.shared.external;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

class ClientConfiguration {

   private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();
   private static final String TRUSTSTORE_PATH = "target/truststore.pkcs12";

   private ClientConfiguration() {
   }

   static ConfigurationBuilder create(String appName, String user, String password) {
      createTruststoreFromCmdLine(TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);

      final ConfigurationBuilder cfg = new ConfigurationBuilder();

      String host = getHostFromAppName(appName);
      System.out.printf("--- Host is: %s%n", host);

      cfg
         .addServer()
            .host(host)
            .port(443)
         .clientIntelligence(ClientIntelligence.BASIC)
         .security().authentication()
            .enable()
            .username(user)
            .password(password)
            .realm("ApplicationRealm")
            .serverName(appName)
            .saslMechanism("DIGEST-MD5")
            .saslQop(SaslQop.AUTH)
         .ssl()
            .enable()
            .sniHostName(host)
            .trustStoreFileName(TRUSTSTORE_PATH)
            .trustStorePassword(TRUSTSTORE_PASSWORD);

      return cfg;
   }

   private static String getHostFromAppName(String appName) {
      String routeName = appName + "-hotrod-route";
      final String cmd = String.format(
         "oc get route %s -o jsonpath=\"{.spec.host}\"",
         routeName);
      return getStringFromCommand(cmd).replace("\"", "");
   }

   private static void createTruststoreFromCmdLine(String tsPath, char[] password) {
      String oauthToken = getStringFromCommand("oc whoami -t");
      String currentContext = getStringFromCommand("oc config current-context");
      String[] contextArray = currentContext.split("/");
      String namespace = contextArray[0];
      String url = "https://" + contextArray[1].split(":")[0];

      String certificate = getCertificateSecret(url, namespace, oauthToken);
      createTruststore(Collections.singletonList(certificate), tsPath, password);
   }

   private static void createTruststore(List<String> certs, String path, char[] password) {
      try {
         try (FileOutputStream output = new FileOutputStream(path)) {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            trustStore.load(null, null);

            for (int i = 0; i < certs.size(); i++) {
               String alias = i < 10 ? "service-crt-0" : "service-crt-";
               String cert = certs.get(i);
               try (InputStream input =
                       Base64.getDecoder().wrap(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)))) {
                  Certificate certificate = cf.generateCertificate(input);
                  trustStore.setCertificateEntry(alias + i, certificate);
               }
            }
            trustStore.store(output, password);
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static String getStringFromCommand(String cmd) {
      Process process = null;
      try {
         process = Runtime.getRuntime().exec(cmd);
         try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return input.readLine();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static String getCertificateSecret(String url, String namespace, String oauthTok) {
      OpenShiftConfig config = new OpenShiftConfigBuilder()
         .withOpenShiftUrl(url)
         .withNamespace(namespace)
         .withOauthToken(oauthTok)
         .build();

      try (OpenShiftClient client = new DefaultOpenShiftClient(config)) {
         return client.secrets().withName("service-certs").get().getData().get("tls.crt");
      }
   }

}
