package net.conjur.config;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import java.nio.file.Paths;
import java.nio.file.Files;
import net.conjur.api.Credentials;
import net.conjur.api.Endpoints;
import net.conjur.api.Conjur;
import org.yaml.snakeyaml.Yaml;
import org.eclipse.jgit.transport.NetRC;
import org.eclipse.jgit.transport.NetRC.NetRCEntry;

// ConjurBuilder
// Helper class to construct a Conjur API object
public class ConjurBuilder {

  private String _identity;
  private String _apiKey;
  private Endpoints _endpoints;

  public ConjurBuilder() {

  }

  // apiKey
  // API key used to authenticate as the <identity> role.
  public ConjurBuilder apiKey(String _apiKey) {
    this._apiKey = _apiKey;
    return this;
  }

  // identity
  // Name of the role to authenticate as. If the role is a host, the identity
  // string must begin with "host/".
  // Ex:
  //  - "annie"
  //  - "host/my-application"
  public ConjurBuilder identity(String _identity) {
    if(this._identity == null) {
      this._identity = _identity;
    }
    return this;
  }

  // applianceUrl
  // Takes the Conjur appliance URL as a string.
  // Ex: https://conjur.myorg.com/api
  public ConjurBuilder applianceUrl(String applianceUrl) {
    if(applianceUrl == null || applianceUrl.isEmpty()) {
      return this;
    }

    if(this._endpoints == null) {
      _endpoints = Endpoints.getApplianceEndpoints(applianceUrl);
    }
    return this;
  }

  // certificate
  // Adds a string containing an x509 certificate to a temporary key store
  public ConjurBuilder certificate(String cert) {
    if (cert == null || cert.isEmpty()) {
      return this;
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(cert.getBytes()); 
         BufferedInputStream bis = new BufferedInputStream(bais)) {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      Certificate certificate = null;
      while (bis.available() > 0) {
          certificate = cf.generateCertificate(bis);
      }

      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null);
      ks.setCertificateEntry(UUID.randomUUID().toString(), certificate);
      String password = UUID.randomUUID().toString();
      File trustStoreOutputFile = File.createTempFile("truststore", null);
      trustStoreOutputFile.deleteOnExit();
      ks.store(new FileOutputStream(trustStoreOutputFile), 
        password.toCharArray());

      System.setProperty("javax.net.ssl.trustStore", 
        trustStoreOutputFile.getAbsolutePath());
      System.setProperty("javax.net.ssl.trustStorePassword", password);
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    return this;
  }

  // certificatePath
  // Reads a file containing an x509 certificate and adds it to a temporary
  // key store.
  public ConjurBuilder certificatePath(String path) {
    if(path == null || path.isEmpty()) {
      return this;
    }

    try {
      this.certificate(new String(Files.readAllBytes(Paths.get(path))));
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return this; 
  }

  // mergeEnvironment
  // Reads the process environment and retrieves (if not exists):
  //  - appliance url
  //  - certificate
  //  - username
  //  - api key
  public ConjurBuilder mergeEnvironment() {
    applianceUrl(System.getenv("CONJUR_APPLIANCE_URL"))
    .certificatePath(System.getenv("CONJUR_CERT_FILE"))
    .certificate(System.getenv("CONJUR_SSL_CERTIFICATE"))
    .apiKey(System.getenv("CONJUR_AUTHN_API_KEY"))
    .identity(System.getenv("CONJUR_AUTHN_LOGIN"));

    return this;
  }
  // mergeConfigurationPath
  // Reads a Conjur configuration YAML file and retrieves (if not exists):
  //  - appliance url
  //  - certificate
  //  - username (from netrc)
  //  - api key (from netrc)
  public ConjurBuilder mergeConfigurationPath(String path, String netrcPath) {
    try { 
      path = ConjurUtil.expandHome(path);

      String config = new String(Files.readAllBytes(Paths.get(path)));
      Yaml yaml = new Yaml();
      Map<String, String> configYaml = (Map)yaml.load(config);

      String netrc = netrcPath;
      if (netrc == null || netrc.isEmpty()) {
        netrc = configYaml.get("netrc_path");
      }

      applianceUrl(configYaml.get("appliance_url"))
        .certificatePath(configYaml.get("cert_file"))
        .identityPath(ConjurUtil.expandHome(netrc));
    }
    catch(IOException e) {
      // File doesn't exist, it may have come from somewhere else
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    return this;
  }

  // identityPath
  // Reads a netrc file containing a Conjur API key.
  // Requires an appliance url is previously known to the builder.
  public ConjurBuilder identityPath(String path) {

    if(path == null || path.isEmpty()) {
      return this;
    }

    NetRC netrc = new NetRC(new File(path));
    NetRCEntry entry = netrc.getEntry(_endpoints.getAuthnUri().toString());
    return identity(entry.login)
      .apiKey(new String(entry.password));
  }

  // build
  // Returns a Conjur API object
  public Conjur build() {
    return new Conjur(new Credentials(_identity, _apiKey), _endpoints);
  }
}