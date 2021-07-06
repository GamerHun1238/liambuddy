package okhttp3;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import okhttp3.internal.Util;






















public final class Handshake
{
  private final TlsVersion tlsVersion;
  private final CipherSuite cipherSuite;
  private final List<Certificate> peerCertificates;
  private final List<Certificate> localCertificates;
  
  private Handshake(TlsVersion tlsVersion, CipherSuite cipherSuite, List<Certificate> peerCertificates, List<Certificate> localCertificates)
  {
    this.tlsVersion = tlsVersion;
    this.cipherSuite = cipherSuite;
    this.peerCertificates = peerCertificates;
    this.localCertificates = localCertificates;
  }
  
  public static Handshake get(SSLSession session) throws IOException {
    String cipherSuiteString = session.getCipherSuite();
    if (cipherSuiteString == null) throw new IllegalStateException("cipherSuite == null");
    if ("SSL_NULL_WITH_NULL_NULL".equals(cipherSuiteString)) {
      throw new IOException("cipherSuite == SSL_NULL_WITH_NULL_NULL");
    }
    CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
    
    String tlsVersionString = session.getProtocol();
    if (tlsVersionString == null) throw new IllegalStateException("tlsVersion == null");
    if ("NONE".equals(tlsVersionString)) throw new IOException("tlsVersion == NONE");
    TlsVersion tlsVersion = TlsVersion.forJavaName(tlsVersionString);
    Certificate[] peerCertificates;
    try
    {
      peerCertificates = session.getPeerCertificates();
    } catch (SSLPeerUnverifiedException ignored) { Certificate[] peerCertificates;
      peerCertificates = null;
    }
    

    List<Certificate> peerCertificatesList = peerCertificates != null ? Util.immutableList(peerCertificates) : Collections.emptyList();
    
    Certificate[] localCertificates = session.getLocalCertificates();
    

    List<Certificate> localCertificatesList = localCertificates != null ? Util.immutableList(localCertificates) : Collections.emptyList();
    
    return new Handshake(tlsVersion, cipherSuite, peerCertificatesList, localCertificatesList);
  }
  
  public static Handshake get(TlsVersion tlsVersion, CipherSuite cipherSuite, List<Certificate> peerCertificates, List<Certificate> localCertificates)
  {
    if (tlsVersion == null) throw new NullPointerException("tlsVersion == null");
    if (cipherSuite == null) throw new NullPointerException("cipherSuite == null");
    return new Handshake(tlsVersion, cipherSuite, Util.immutableList(peerCertificates), 
      Util.immutableList(localCertificates));
  }
  



  public TlsVersion tlsVersion()
  {
    return tlsVersion;
  }
  
  public CipherSuite cipherSuite()
  {
    return cipherSuite;
  }
  
  public List<Certificate> peerCertificates()
  {
    return peerCertificates;
  }
  
  @Nullable
  public Principal peerPrincipal() {
    return !peerCertificates.isEmpty() ? 
      ((X509Certificate)peerCertificates.get(0)).getSubjectX500Principal() : 
      null;
  }
  
  public List<Certificate> localCertificates()
  {
    return localCertificates;
  }
  
  @Nullable
  public Principal localPrincipal() {
    return !localCertificates.isEmpty() ? 
      ((X509Certificate)localCertificates.get(0)).getSubjectX500Principal() : 
      null;
  }
  
  public boolean equals(@Nullable Object other) {
    if (!(other instanceof Handshake)) return false;
    Handshake that = (Handshake)other;
    return (tlsVersion.equals(tlsVersion)) && 
      (cipherSuite.equals(cipherSuite)) && 
      (peerCertificates.equals(peerCertificates)) && 
      (localCertificates.equals(localCertificates));
  }
  
  public int hashCode() {
    int result = 17;
    result = 31 * result + tlsVersion.hashCode();
    result = 31 * result + cipherSuite.hashCode();
    result = 31 * result + peerCertificates.hashCode();
    result = 31 * result + localCertificates.hashCode();
    return result;
  }
  
  public String toString() {
    return 
    






      "Handshake{tlsVersion=" + tlsVersion + " cipherSuite=" + cipherSuite + " peerCertificates=" + names(peerCertificates) + " localCertificates=" + names(localCertificates) + '}';
  }
  
  private List<String> names(List<Certificate> certificates)
  {
    ArrayList<String> strings = new ArrayList();
    
    for (Certificate cert : certificates) {
      if ((cert instanceof X509Certificate)) {
        strings.add(String.valueOf(((X509Certificate)cert).getSubjectDN()));
      } else {
        strings.add(cert.getType());
      }
    }
    
    return strings;
  }
}
