package okhttp3;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSocket;
import okhttp3.internal.Util;








































public final class ConnectionSpec
{
  private static final CipherSuite[] RESTRICTED_CIPHER_SUITES = { CipherSuite.TLS_AES_128_GCM_SHA256, CipherSuite.TLS_AES_256_GCM_SHA384, CipherSuite.TLS_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_AES_128_CCM_SHA256, CipherSuite.TLS_AES_256_CCM_8_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256 };
  

















  private static final CipherSuite[] APPROVED_CIPHER_SUITES = { CipherSuite.TLS_AES_128_GCM_SHA256, CipherSuite.TLS_AES_256_GCM_SHA384, CipherSuite.TLS_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_AES_128_CCM_SHA256, CipherSuite.TLS_AES_256_CCM_8_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA };
  


























  public static final ConnectionSpec RESTRICTED_TLS = new Builder(true)
    .cipherSuites(RESTRICTED_CIPHER_SUITES)
    .tlsVersions(new TlsVersion[] { TlsVersion.TLS_1_3, TlsVersion.TLS_1_2 })
    .supportsTlsExtensions(true)
    .build();
  




  public static final ConnectionSpec MODERN_TLS = new Builder(true)
    .cipherSuites(APPROVED_CIPHER_SUITES)
    .tlsVersions(new TlsVersion[] { TlsVersion.TLS_1_3, TlsVersion.TLS_1_2 })
    .supportsTlsExtensions(true)
    .build();
  





  public static final ConnectionSpec COMPATIBLE_TLS = new Builder(true)
    .cipherSuites(APPROVED_CIPHER_SUITES)
    .tlsVersions(new TlsVersion[] { TlsVersion.TLS_1_3, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0 })
    .supportsTlsExtensions(true)
    .build();
  

  public static final ConnectionSpec CLEARTEXT = new Builder(false).build();
  final boolean tls;
  final boolean supportsTlsExtensions;
  @Nullable
  final String[] cipherSuites;
  @Nullable
  final String[] tlsVersions;
  
  ConnectionSpec(Builder builder) { tls = tls;
    cipherSuites = cipherSuites;
    tlsVersions = tlsVersions;
    supportsTlsExtensions = supportsTlsExtensions;
  }
  
  public boolean isTls() {
    return tls;
  }
  


  @Nullable
  public List<CipherSuite> cipherSuites()
  {
    return cipherSuites != null ? CipherSuite.forJavaNames(cipherSuites) : null;
  }
  


  @Nullable
  public List<TlsVersion> tlsVersions()
  {
    return tlsVersions != null ? TlsVersion.forJavaNames(tlsVersions) : null;
  }
  
  public boolean supportsTlsExtensions() {
    return supportsTlsExtensions;
  }
  
  void apply(SSLSocket sslSocket, boolean isFallback)
  {
    ConnectionSpec specToApply = supportedSpec(sslSocket, isFallback);
    
    if (tlsVersions != null) {
      sslSocket.setEnabledProtocols(tlsVersions);
    }
    if (cipherSuites != null) {
      sslSocket.setEnabledCipherSuites(cipherSuites);
    }
  }
  





  private ConnectionSpec supportedSpec(SSLSocket sslSocket, boolean isFallback)
  {
    String[] cipherSuitesIntersection = cipherSuites != null ? Util.intersect(CipherSuite.ORDER_BY_NAME, sslSocket.getEnabledCipherSuites(), cipherSuites) : sslSocket.getEnabledCipherSuites();
    

    String[] tlsVersionsIntersection = tlsVersions != null ? Util.intersect(Util.NATURAL_ORDER, sslSocket.getEnabledProtocols(), tlsVersions) : sslSocket.getEnabledProtocols();
    


    String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
    int indexOfFallbackScsv = Util.indexOf(CipherSuite.ORDER_BY_NAME, supportedCipherSuites, "TLS_FALLBACK_SCSV");
    
    if ((isFallback) && (indexOfFallbackScsv != -1)) {
      cipherSuitesIntersection = Util.concat(cipherSuitesIntersection, supportedCipherSuites[indexOfFallbackScsv]);
    }
    

    return 
    

      new Builder(this).cipherSuites(cipherSuitesIntersection).tlsVersions(tlsVersionsIntersection).build();
  }
  










  public boolean isCompatible(SSLSocket socket)
  {
    if (!tls) {
      return false;
    }
    
    if ((tlsVersions != null) && (!Util.nonEmptyIntersection(Util.NATURAL_ORDER, tlsVersions, socket
      .getEnabledProtocols()))) {
      return false;
    }
    
    if ((cipherSuites != null) && (!Util.nonEmptyIntersection(CipherSuite.ORDER_BY_NAME, cipherSuites, socket
      .getEnabledCipherSuites()))) {
      return false;
    }
    
    return true;
  }
  
  public boolean equals(@Nullable Object other) {
    if (!(other instanceof ConnectionSpec)) return false;
    if (other == this) { return true;
    }
    ConnectionSpec that = (ConnectionSpec)other;
    if (tls != tls) { return false;
    }
    if (tls) {
      if (!Arrays.equals(cipherSuites, cipherSuites)) return false;
      if (!Arrays.equals(tlsVersions, tlsVersions)) return false;
      if (supportsTlsExtensions != supportsTlsExtensions) { return false;
      }
    }
    return true;
  }
  
  public int hashCode() {
    int result = 17;
    if (tls) {
      result = 31 * result + Arrays.hashCode(cipherSuites);
      result = 31 * result + Arrays.hashCode(tlsVersions);
      result = 31 * result + (supportsTlsExtensions ? 0 : 1);
    }
    return result;
  }
  
  public String toString() {
    if (!tls) {
      return "ConnectionSpec()";
    }
    
    return 
    
      "ConnectionSpec(cipherSuites=" + Objects.toString(cipherSuites(), "[all enabled]") + ", tlsVersions=" + Objects.toString(tlsVersions(), "[all enabled]") + ", supportsTlsExtensions=" + supportsTlsExtensions + ")";
  }
  
  public static final class Builder {
    boolean tls;
    @Nullable
    String[] cipherSuites;
    @Nullable
    String[] tlsVersions;
    boolean supportsTlsExtensions;
    
    Builder(boolean tls) {
      this.tls = tls;
    }
    
    public Builder(ConnectionSpec connectionSpec) {
      tls = tls;
      cipherSuites = cipherSuites;
      tlsVersions = tlsVersions;
      supportsTlsExtensions = supportsTlsExtensions;
    }
    
    public Builder allEnabledCipherSuites() {
      if (!tls) throw new IllegalStateException("no cipher suites for cleartext connections");
      cipherSuites = null;
      return this;
    }
    
    public Builder cipherSuites(CipherSuite... cipherSuites) {
      if (!tls) { throw new IllegalStateException("no cipher suites for cleartext connections");
      }
      String[] strings = new String[cipherSuites.length];
      for (int i = 0; i < cipherSuites.length; i++) {
        strings[i] = javaName;
      }
      return cipherSuites(strings);
    }
    
    public Builder cipherSuites(String... cipherSuites) {
      if (!tls) { throw new IllegalStateException("no cipher suites for cleartext connections");
      }
      if (cipherSuites.length == 0) {
        throw new IllegalArgumentException("At least one cipher suite is required");
      }
      
      this.cipherSuites = ((String[])cipherSuites.clone());
      return this;
    }
    
    public Builder allEnabledTlsVersions() {
      if (!tls) throw new IllegalStateException("no TLS versions for cleartext connections");
      tlsVersions = null;
      return this;
    }
    
    public Builder tlsVersions(TlsVersion... tlsVersions) {
      if (!tls) { throw new IllegalStateException("no TLS versions for cleartext connections");
      }
      String[] strings = new String[tlsVersions.length];
      for (int i = 0; i < tlsVersions.length; i++) {
        strings[i] = javaName;
      }
      
      return tlsVersions(strings);
    }
    
    public Builder tlsVersions(String... tlsVersions) {
      if (!tls) { throw new IllegalStateException("no TLS versions for cleartext connections");
      }
      if (tlsVersions.length == 0) {
        throw new IllegalArgumentException("At least one TLS version is required");
      }
      
      this.tlsVersions = ((String[])tlsVersions.clone());
      return this;
    }
    

    /**
     * @deprecated
     */
    public Builder supportsTlsExtensions(boolean supportsTlsExtensions)
    {
      if (!tls) throw new IllegalStateException("no TLS extensions for cleartext connections");
      this.supportsTlsExtensions = supportsTlsExtensions;
      return this;
    }
    
    public ConnectionSpec build() {
      return new ConnectionSpec(this);
    }
  }
}
