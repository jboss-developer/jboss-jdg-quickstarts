package org.infinispan.demo.online;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;

/**
 * DEPRECATED. Use the OpenShift quickstarts for Data Grid 7.3 instead. 
 */

/**
 * This is a helper class for parsing a CRT file and creating a java truststore
 */
public class TrustStore {

   public static void createFromCmdLine(String path, char[] password) throws GeneralSecurityException, IOException {
      String oauthToken = getStringFromCommand("oc whoami -t");
      String currentContext = getStringFromCommand("oc config current-context");
      String[] contextArray = currentContext.split("/");
      String namespace = contextArray[0];
      String url = "https://" + contextArray[1].split(":")[0];

      String certificate = getCertificateSecret(url, namespace, oauthToken);
      create(certificate, path, password);
   }

   private static String getStringFromCommand(String cmd) throws IOException {
      Process process = Runtime.getRuntime().exec(cmd);
      try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
         return input.readLine();
      }
   }

   private static String getCertificateSecret(String url, String namespace, String oauthTok) {
      OpenShiftConfig config = new OpenShiftConfigBuilder()
            .withOpenShiftUrl(url)
            .withNamespace(namespace)
            .withOauthToken(oauthTok)
            .build();

      OpenShiftClient client = new DefaultOpenShiftClient(config);
      return client.secrets().withName("service-certs").get().getData().get("tls.crt");
   }

   /**
    * @param crtPath  the path to the crt file to be passed
    * @param tsPath   the path of the truststore to be created
    * @param password the password for accessing the created truststore
    */
   public static void createFromCrtFile(String crtPath, String tsPath, char[] password)
         throws GeneralSecurityException, IOException {
      create(parseCrtFile(crtPath), tsPath, password);
   }

   /**
    * @param cert     a certificate to be stored in the truststore
    * @param path     the path to the truststore
    * @param password the password for accessing the generated truststore
    */
   public static void create(String cert, String path, char[] password) throws GeneralSecurityException, IOException {
      create(Collections.singletonList(cert), path, password);
   }

   /**
    * @param certs    a list of certificates to be store in the truststore
    * @param path     the path to the truststore
    * @param password the password for accessing the generated truststore
    */
   public static void create(List<String> certs, String path, char[] password) throws GeneralSecurityException, IOException {
      try (FileOutputStream output = new FileOutputStream(path)) {
         KeyStore trustStore = KeyStore.getInstance("PKCS12");
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         trustStore.load(null, null);

         for (int i = 0; i < certs.size(); i++) {
            String alias = i < 10 ? "service-crt-0" : "service-crt-";
            String cert = certs.get(i);
            try (InputStream input = Base64.getDecoder().wrap(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)))) {
               Certificate certificate = cf.generateCertificate(input);
               trustStore.setCertificateEntry(alias + i, certificate);
            }
         }
         trustStore.store(output, password);
      }
   }

   /**
    * Parse the parsed crt file to return an array of String certificates
    *
    * @param path to the crt file to be parsed
    * @return a list of certificates in String format
    */
   private static List<String> parseCrtFile(String path) throws IOException {
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
   }
}
