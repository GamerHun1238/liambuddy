package okhttp3;

import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;












































































































public final class CertificatePinner
{
  public static final CertificatePinner DEFAULT = new Builder().build();
  private final Set<Pin> pins;
  @Nullable
  private final CertificateChainCleaner certificateChainCleaner;
  
  CertificatePinner(Set<Pin> pins, @Nullable CertificateChainCleaner certificateChainCleaner) {
    this.pins = pins;
    this.certificateChainCleaner = certificateChainCleaner;
  }
  
  public boolean equals(@Nullable Object other) {
    if (other == this) return true;
    if (((other instanceof CertificatePinner)) && 
      (Objects.equals(certificateChainCleaner, certificateChainCleaner))) {}
    return 
    

      pins.equals(pins);
  }
  
  public int hashCode() {
    int result = Objects.hashCode(certificateChainCleaner);
    result = 31 * result + pins.hashCode();
    return result;
  }
  







  public void check(String hostname, List<Certificate> peerCertificates)
    throws SSLPeerUnverifiedException
  {
    List<Pin> pins = findMatchingPins(hostname);
    if (pins.isEmpty()) { return;
    }
    if (certificateChainCleaner != null) {
      peerCertificates = certificateChainCleaner.clean(peerCertificates, hostname);
    }
    
    int c = 0; for (int certsSize = peerCertificates.size(); c < certsSize; c++) {
      X509Certificate x509Certificate = (X509Certificate)peerCertificates.get(c);
      

      ByteString sha1 = null;
      ByteString sha256 = null;
      
      int p = 0; for (int pinsSize = pins.size(); p < pinsSize; p++) {
        Pin pin = (Pin)pins.get(p);
        if (hashAlgorithm.equals("sha256/")) {
          if (sha256 == null) sha256 = sha256(x509Certificate);
          if (!hash.equals(sha256)) {}
        } else if (hashAlgorithm.equals("sha1/")) {
          if (sha1 == null) sha1 = sha1(x509Certificate);
          if (!hash.equals(sha1)) {}
        } else {
          throw new AssertionError("unsupported hashAlgorithm: " + hashAlgorithm);
        }
      }
    }
    



    StringBuilder message = new StringBuilder().append("Certificate pinning failure!").append("\n  Peer certificate chain:");
    int c = 0; for (int certsSize = peerCertificates.size(); c < certsSize; c++) {
      X509Certificate x509Certificate = (X509Certificate)peerCertificates.get(c);
      message.append("\n    ").append(pin(x509Certificate))
        .append(": ").append(x509Certificate.getSubjectDN().getName());
    }
    message.append("\n  Pinned certificates for ").append(hostname).append(":");
    int p = 0; for (int pinsSize = pins.size(); p < pinsSize; p++) {
      Pin pin = (Pin)pins.get(p);
      message.append("\n    ").append(pin);
    }
    throw new SSLPeerUnverifiedException(message.toString());
  }
  
  /**
   * @deprecated
   */
  public void check(String hostname, Certificate... peerCertificates) throws SSLPeerUnverifiedException { check(hostname, Arrays.asList(peerCertificates)); }
  




  List<Pin> findMatchingPins(String hostname)
  {
    List<Pin> result = Collections.emptyList();
    for (Pin pin : pins) {
      if (pin.matches(hostname)) {
        if (result.isEmpty()) result = new ArrayList();
        result.add(pin);
      }
    }
    return result;
  }
  

  CertificatePinner withCertificateChainCleaner(@Nullable CertificateChainCleaner certificateChainCleaner)
  {
    return Objects.equals(this.certificateChainCleaner, certificateChainCleaner) ? 
      this : 
      new CertificatePinner(pins, certificateChainCleaner);
  }
  





  public static String pin(Certificate certificate)
  {
    if (!(certificate instanceof X509Certificate)) {
      throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }
    return "sha256/" + sha256((X509Certificate)certificate).base64();
  }
  
  static ByteString sha1(X509Certificate x509Certificate) {
    return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha1();
  }
  
  static ByteString sha256(X509Certificate x509Certificate) {
    return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha256();
  }
  

  static final class Pin
  {
    private static final String WILDCARD = "*.";
    
    final String pattern;
    final String canonicalHostname;
    final String hashAlgorithm;
    final ByteString hash;
    
    Pin(String pattern, String pin)
    {
      this.pattern = pattern;
      

      canonicalHostname = (pattern.startsWith("*.") ? HttpUrl.get("http://" + pattern.substring("*.".length())).host() : HttpUrl.get("http://" + pattern).host());
      if (pin.startsWith("sha1/")) {
        hashAlgorithm = "sha1/";
        hash = ByteString.decodeBase64(pin.substring("sha1/".length()));
      } else if (pin.startsWith("sha256/")) {
        hashAlgorithm = "sha256/";
        hash = ByteString.decodeBase64(pin.substring("sha256/".length()));
      } else {
        throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + pin);
      }
      
      if (hash == null) {
        throw new IllegalArgumentException("pins must be base64: " + pin);
      }
    }
    
    boolean matches(String hostname) {
      if (pattern.startsWith("*.")) {
        int firstDot = hostname.indexOf('.');
        return (hostname.length() - firstDot - 1 == canonicalHostname.length()) && 
          (hostname.regionMatches(false, firstDot + 1, canonicalHostname, 0, canonicalHostname
          .length()));
      }
      
      return hostname.equals(canonicalHostname);
    }
    
    public boolean equals(Object other) {
      return ((other instanceof Pin)) && 
        (pattern.equals(pattern)) && 
        (hashAlgorithm.equals(hashAlgorithm)) && 
        (hash.equals(hash));
    }
    
    public int hashCode() {
      int result = 17;
      result = 31 * result + pattern.hashCode();
      result = 31 * result + hashAlgorithm.hashCode();
      result = 31 * result + hash.hashCode();
      return result;
    }
    
    public String toString() {
      return hashAlgorithm + hash.base64();
    }
  }
  
  public static final class Builder
  {
    private final List<CertificatePinner.Pin> pins = new ArrayList();
    


    public Builder() {}
    


    public Builder add(String pattern, String... pins)
    {
      if (pattern == null) { throw new NullPointerException("pattern == null");
      }
      for (String pin : pins) {
        this.pins.add(new CertificatePinner.Pin(pattern, pin));
      }
      
      return this;
    }
    
    public CertificatePinner build() {
      return new CertificatePinner(new LinkedHashSet(pins), null);
    }
  }
}
