package org.infinispan.quickstart.openshift.shared.helloworld;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

class ClientConfiguration {

   private static final String CRT_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";
   private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();
   private static final String TRUSTSTORE_PATH = "truststore.pkcs12";

   private ClientConfiguration() {
   }

   static ConfigurationBuilder create(String svcName, String saslName, String user, String password) {
      createTruststoreFromCrtFile(CRT_PATH, TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);

      final ConfigurationBuilder cfg = new ConfigurationBuilder();

      cfg
         .addServer()
            .host(svcName)
            .port(11222)
         .security().authentication()
            .enable()
            .username(user)
            .password(password)
            .realm("ApplicationRealm")
            .serverName(saslName)
            .saslMechanism("DIGEST-MD5")
            .saslQop(SaslQop.AUTH)
         .ssl()
            .enable()
            .trustStoreFileName(TRUSTSTORE_PATH)
            .trustStorePassword(TRUSTSTORE_PASSWORD);

      return cfg;
   }

   private static void createTruststoreFromCrtFile(String crtPath, String tsPath, char[] password) {
      createTruststore(parseCrtFile(crtPath), tsPath, password);
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

   private static List<String> parseCrtFile(String path) {
      try {
         List<String> certs = new ArrayList<>();
         StringBuilder sb = new StringBuilder();
         for (String line : Files.readAllLines(Paths.get(path))) {
            if (line.isEmpty() || line.contains("BEGIN CERTIFICATE"))
               continue;

            if (line.contains("END CERTIFICATE")) {
               certs.add(sb.toString());
               sb.setLength(0);
            } else {
               sb.append(line);
            }
         }
         return certs;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}
