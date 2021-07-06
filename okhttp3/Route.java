package okhttp3;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import javax.annotation.Nullable;




























public final class Route
{
  final Address address;
  final Proxy proxy;
  final InetSocketAddress inetSocketAddress;
  
  public Route(Address address, Proxy proxy, InetSocketAddress inetSocketAddress)
  {
    if (address == null) {
      throw new NullPointerException("address == null");
    }
    if (proxy == null) {
      throw new NullPointerException("proxy == null");
    }
    if (inetSocketAddress == null) {
      throw new NullPointerException("inetSocketAddress == null");
    }
    this.address = address;
    this.proxy = proxy;
    this.inetSocketAddress = inetSocketAddress;
  }
  
  public Address address() {
    return address;
  }
  





  public Proxy proxy()
  {
    return proxy;
  }
  
  public InetSocketAddress socketAddress() {
    return inetSocketAddress;
  }
  



  public boolean requiresTunnel()
  {
    return (address.sslSocketFactory != null) && (proxy.type() == Proxy.Type.HTTP);
  }
  
  public boolean equals(@Nullable Object other) {
    return ((other instanceof Route)) && 
      (address.equals(address)) && 
      (proxy.equals(proxy)) && 
      (inetSocketAddress.equals(inetSocketAddress));
  }
  
  public int hashCode() {
    int result = 17;
    result = 31 * result + address.hashCode();
    result = 31 * result + proxy.hashCode();
    result = 31 * result + inetSocketAddress.hashCode();
    return result;
  }
  
  public String toString() {
    return "Route{" + inetSocketAddress + "}";
  }
}
