package okhttp3.internal.tls;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.auth.x500.X500Principal;
























public final class BasicCertificateChainCleaner
  extends CertificateChainCleaner
{
  private static final int MAX_SIGNERS = 9;
  private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;
  
  public BasicCertificateChainCleaner(X509Certificate... caCerts)
  {
    subjectToCaCerts = new LinkedHashMap();
    for (X509Certificate caCert : caCerts) {
      X500Principal subject = caCert.getSubjectX500Principal();
      Set<X509Certificate> subjectCaCerts = (Set)subjectToCaCerts.get(subject);
      if (subjectCaCerts == null) {
        subjectCaCerts = new LinkedHashSet(1);
        subjectToCaCerts.put(subject, subjectCaCerts);
      }
      subjectCaCerts.add(caCert);
    }
  }
  






  public List<Certificate> clean(List<Certificate> chain, String hostname)
    throws SSLPeerUnverifiedException
  {
    Deque<Certificate> queue = new ArrayDeque(chain);
    List<Certificate> result = new ArrayList();
    result.add((Certificate)queue.removeFirst());
    boolean foundTrustedCertificate = false;
    
    label233:
    for (int c = 0; c < 9; c++) {
      X509Certificate toVerify = (X509Certificate)result.get(result.size() - 1);
      



      X509Certificate trustedCert = findByIssuerAndSignature(toVerify);
      if (trustedCert != null) {
        if ((result.size() > 1) || (!toVerify.equals(trustedCert))) {
          result.add(trustedCert);
        }
        if (verifySignature(trustedCert, trustedCert)) {
          return result;
        }
        foundTrustedCertificate = true;

      }
      else
      {

        for (Iterator<Certificate> i = queue.iterator(); i.hasNext();) {
          X509Certificate signingCert = (X509Certificate)i.next();
          if (verifySignature(toVerify, signingCert)) {
            i.remove();
            result.add(signingCert);
            
            break label233;
          }
        }
        
        if (foundTrustedCertificate) {
          return result;
        }
        

        throw new SSLPeerUnverifiedException("Failed to find a trusted cert that signed " + toVerify);
      }
    }
    
    throw new SSLPeerUnverifiedException("Certificate chain too long: " + result);
  }
  
  private boolean verifySignature(X509Certificate toVerify, X509Certificate signingCert)
  {
    if (!toVerify.getIssuerDN().equals(signingCert.getSubjectDN())) return false;
    try {
      toVerify.verify(signingCert.getPublicKey());
      return true;
    } catch (GeneralSecurityException verifyFailed) {}
    return false;
  }
  

  private X509Certificate findByIssuerAndSignature(X509Certificate cert)
  {
    X500Principal issuer = cert.getIssuerX500Principal();
    Set<X509Certificate> subjectCaCerts = (Set)subjectToCaCerts.get(issuer);
    if (subjectCaCerts == null) { return null;
    }
    for (X509Certificate caCert : subjectCaCerts) {
      PublicKey publicKey = caCert.getPublicKey();
      try {
        cert.verify(publicKey);
        return caCert;
      }
      catch (Exception localException) {}
    }
    
    return null;
  }
  
  public int hashCode() {
    return subjectToCaCerts.hashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this) return true;
    return ((other instanceof BasicCertificateChainCleaner)) && 
      (subjectToCaCerts.equals(subjectToCaCerts));
  }
}
