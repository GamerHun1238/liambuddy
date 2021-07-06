package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.internal.Util;



























public final class Address
{
  final HttpUrl url;
  final Dns dns;
  final SocketFactory socketFactory;
  final Authenticator proxyAuthenticator;
  final List<Protocol> protocols;
  final List<ConnectionSpec> connectionSpecs;
  final ProxySelector proxySelector;
  @Nullable
  final Proxy proxy;
  @Nullable
  final SSLSocketFactory sslSocketFactory;
  @Nullable
  final HostnameVerifier hostnameVerifier;
  @Nullable
  final CertificatePinner certificatePinner;
  
  public Address(String uriHost, int uriPort, Dns dns, SocketFactory socketFactory, @Nullable SSLSocketFactory sslSocketFactory, @Nullable HostnameVerifier hostnameVerifier, @Nullable CertificatePinner certificatePinner, Authenticator proxyAuthenticator, @Nullable Proxy proxy, List<Protocol> protocols, List<ConnectionSpec> connectionSpecs, ProxySelector proxySelector)
  {
    url = new HttpUrl.Builder().scheme(sslSocketFactory != null ? "https" : "http").host(uriHost).port(uriPort).build();
    
    if (dns == null) throw new NullPointerException("dns == null");
    this.dns = dns;
    
    if (socketFactory == null) throw new NullPointerException("socketFactory == null");
    this.socketFactory = socketFactory;
    
    if (proxyAuthenticator == null) {
      throw new NullPointerException("proxyAuthenticator == null");
    }
    this.proxyAuthenticator = proxyAuthenticator;
    
    if (protocols == null) throw new NullPointerException("protocols == null");
    this.protocols = Util.immutableList(protocols);
    
    if (connectionSpecs == null) throw new NullPointerException("connectionSpecs == null");
    this.connectionSpecs = Util.immutableList(connectionSpecs);
    
    if (proxySelector == null) throw new NullPointerException("proxySelector == null");
    this.proxySelector = proxySelector;
    
    this.proxy = proxy;
    this.sslSocketFactory = sslSocketFactory;
    this.hostnameVerifier = hostnameVerifier;
    this.certificatePinner = certificatePinner;
  }
  



  public HttpUrl url()
  {
    return url;
  }
  
  public Dns dns()
  {
    return dns;
  }
  
  public SocketFactory socketFactory()
  {
    return socketFactory;
  }
  
  public Authenticator proxyAuthenticator()
  {
    return proxyAuthenticator;
  }
  



  public List<Protocol> protocols()
  {
    return protocols;
  }
  
  public List<ConnectionSpec> connectionSpecs() {
    return connectionSpecs;
  }
  



  public ProxySelector proxySelector()
  {
    return proxySelector;
  }
  


  @Nullable
  public Proxy proxy()
  {
    return proxy;
  }
  
  @Nullable
  public SSLSocketFactory sslSocketFactory() {
    return sslSocketFactory;
  }
  
  @Nullable
  public HostnameVerifier hostnameVerifier() {
    return hostnameVerifier;
  }
  
  @Nullable
  public CertificatePinner certificatePinner() {
    return certificatePinner;
  }
  
  public boolean equals(@Nullable Object other) {
    return ((other instanceof Address)) && 
      (url.equals(url)) && 
      (equalsNonHost((Address)other));
  }
  
  public int hashCode() {
    int result = 17;
    result = 31 * result + url.hashCode();
    result = 31 * result + dns.hashCode();
    result = 31 * result + proxyAuthenticator.hashCode();
    result = 31 * result + protocols.hashCode();
    result = 31 * result + connectionSpecs.hashCode();
    result = 31 * result + proxySelector.hashCode();
    result = 31 * result + Objects.hashCode(proxy);
    result = 31 * result + Objects.hashCode(sslSocketFactory);
    result = 31 * result + Objects.hashCode(hostnameVerifier);
    result = 31 * result + Objects.hashCode(certificatePinner);
    return result;
  }
  
  boolean equalsNonHost(Address that) {
    return (dns.equals(dns)) && 
      (proxyAuthenticator.equals(proxyAuthenticator)) && 
      (protocols.equals(protocols)) && 
      (connectionSpecs.equals(connectionSpecs)) && 
      (proxySelector.equals(proxySelector)) && 
      (Objects.equals(proxy, proxy)) && 
      (Objects.equals(sslSocketFactory, sslSocketFactory)) && 
      (Objects.equals(hostnameVerifier, hostnameVerifier)) && 
      (Objects.equals(certificatePinner, certificatePinner)) && 
      (url().port() == that.url().port());
  }
  

  public String toString()
  {
    StringBuilder result = new StringBuilder().append("Address{").append(url.host()).append(":").append(url.port());
    
    if (proxy != null) {
      result.append(", proxy=").append(proxy);
    } else {
      result.append(", proxySelector=").append(proxySelector);
    }
    
    result.append("}");
    return result.toString();
  }
}
