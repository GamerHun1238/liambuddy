package okhttp3.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Route;
import okhttp3.internal.Util;


















public final class RouteSelector
{
  private final Address address;
  private final RouteDatabase routeDatabase;
  private final Call call;
  private final EventListener eventListener;
  private List<Proxy> proxies = Collections.emptyList();
  
  private int nextProxyIndex;
  
  private List<InetSocketAddress> inetSocketAddresses = Collections.emptyList();
  

  private final List<Route> postponedRoutes = new ArrayList();
  
  public RouteSelector(Address address, RouteDatabase routeDatabase, Call call, EventListener eventListener)
  {
    this.address = address;
    this.routeDatabase = routeDatabase;
    this.call = call;
    this.eventListener = eventListener;
    
    resetNextProxy(address.url(), address.proxy());
  }
  


  public boolean hasNext()
  {
    return (hasNextProxy()) || (!postponedRoutes.isEmpty());
  }
  
  public Selection next() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    

    List<Route> routes = new ArrayList();
    while (hasNextProxy())
    {


      Proxy proxy = nextProxy();
      int i = 0; for (int size = inetSocketAddresses.size(); i < size; i++) {
        Route route = new Route(address, proxy, (InetSocketAddress)inetSocketAddresses.get(i));
        if (routeDatabase.shouldPostpone(route)) {
          postponedRoutes.add(route);
        } else {
          routes.add(route);
        }
      }
      
      if (!routes.isEmpty()) {
        break;
      }
    }
    
    if (routes.isEmpty())
    {
      routes.addAll(postponedRoutes);
      postponedRoutes.clear();
    }
    
    return new Selection(routes);
  }
  



  public void connectFailed(Route failedRoute, IOException failure)
  {
    if ((failedRoute.proxy().type() != Proxy.Type.DIRECT) && (address.proxySelector() != null))
    {
      address.proxySelector().connectFailed(address
        .url().uri(), failedRoute.proxy().address(), failure);
    }
    
    routeDatabase.failed(failedRoute);
  }
  
  private void resetNextProxy(HttpUrl url, Proxy proxy)
  {
    if (proxy != null)
    {
      proxies = Collections.singletonList(proxy);
    }
    else {
      List<Proxy> proxiesOrNull = address.proxySelector().select(url.uri());
      

      proxies = ((proxiesOrNull != null) && (!proxiesOrNull.isEmpty()) ? Util.immutableList(proxiesOrNull) : Util.immutableList(new Proxy[] { Proxy.NO_PROXY }));
    }
    nextProxyIndex = 0;
  }
  
  private boolean hasNextProxy()
  {
    return nextProxyIndex < proxies.size();
  }
  
  private Proxy nextProxy() throws IOException
  {
    if (!hasNextProxy()) {
      throw new SocketException("No route to " + address.url().host() + "; exhausted proxy configurations: " + proxies);
    }
    
    Proxy result = (Proxy)proxies.get(nextProxyIndex++);
    resetNextInetSocketAddress(result);
    return result;
  }
  
  private void resetNextInetSocketAddress(Proxy proxy)
    throws IOException
  {
    inetSocketAddresses = new ArrayList();
    int socketPort;
    String socketHost;
    int socketPort;
    if ((proxy.type() == Proxy.Type.DIRECT) || (proxy.type() == Proxy.Type.SOCKS)) {
      String socketHost = address.url().host();
      socketPort = address.url().port();
    } else {
      SocketAddress proxyAddress = proxy.address();
      if (!(proxyAddress instanceof InetSocketAddress))
      {
        throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + proxyAddress.getClass());
      }
      InetSocketAddress proxySocketAddress = (InetSocketAddress)proxyAddress;
      socketHost = getHostString(proxySocketAddress);
      socketPort = proxySocketAddress.getPort();
    }
    
    if ((socketPort < 1) || (socketPort > 65535)) {
      throw new SocketException("No route to " + socketHost + ":" + socketPort + "; port is out of range");
    }
    

    if (proxy.type() == Proxy.Type.SOCKS) {
      inetSocketAddresses.add(InetSocketAddress.createUnresolved(socketHost, socketPort));
    } else {
      eventListener.dnsStart(call, socketHost);
      

      List<InetAddress> addresses = address.dns().lookup(socketHost);
      if (addresses.isEmpty()) {
        throw new UnknownHostException(address.dns() + " returned no addresses for " + socketHost);
      }
      
      eventListener.dnsEnd(call, socketHost, addresses);
      
      int i = 0; for (int size = addresses.size(); i < size; i++) {
        InetAddress inetAddress = (InetAddress)addresses.get(i);
        inetSocketAddresses.add(new InetSocketAddress(inetAddress, socketPort));
      }
    }
  }
  




  static String getHostString(InetSocketAddress socketAddress)
  {
    InetAddress address = socketAddress.getAddress();
    if (address == null)
    {


      return socketAddress.getHostName();
    }
    

    return address.getHostAddress();
  }
  
  public static final class Selection
  {
    private final List<Route> routes;
    private int nextRouteIndex = 0;
    
    Selection(List<Route> routes) {
      this.routes = routes;
    }
    
    public boolean hasNext() {
      return nextRouteIndex < routes.size();
    }
    
    public Route next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return (Route)routes.get(nextRouteIndex++);
    }
    
    public List<Route> getAll() {
      return new ArrayList(routes);
    }
  }
}
