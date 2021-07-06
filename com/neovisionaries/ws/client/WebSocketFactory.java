package com.neovisionaries.ws.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;






















public class WebSocketFactory
{
  private final SocketFactorySettings mSocketFactorySettings;
  private final ProxySettings mProxySettings;
  private int mConnectionTimeout;
  private int mSocketTimeout;
  private DualStackMode mDualStackMode = DualStackMode.BOTH;
  private int mDualStackFallbackDelay = 250;
  private boolean mVerifyHostname = true;
  

  private String[] mServerNames;
  


  public WebSocketFactory()
  {
    mSocketFactorySettings = new SocketFactorySettings();
    mProxySettings = new ProxySettings(this);
  }
  












  public WebSocketFactory(WebSocketFactory other)
  {
    if (other == null)
    {
      throw new IllegalArgumentException("The given WebSocketFactory is null");
    }
    
    mSocketFactorySettings = new SocketFactorySettings(mSocketFactorySettings);
    mProxySettings = new ProxySettings(this, mProxySettings);
    mConnectionTimeout = mConnectionTimeout;
    mSocketTimeout = mSocketTimeout;
    mDualStackMode = mDualStackMode;
    mDualStackFallbackDelay = mDualStackFallbackDelay;
    mVerifyHostname = mVerifyHostname;
    
    if (mServerNames != null)
    {
      mServerNames = new String[mServerNames.length];
      System.arraycopy(mServerNames, 0, mServerNames, 0, mServerNames.length);
    }
  }
  








  public SocketFactory getSocketFactory()
  {
    return mSocketFactorySettings.getSocketFactory();
  }
  











  public WebSocketFactory setSocketFactory(SocketFactory factory)
  {
    mSocketFactorySettings.setSocketFactory(factory);
    
    return this;
  }
  








  public SSLSocketFactory getSSLSocketFactory()
  {
    return mSocketFactorySettings.getSSLSocketFactory();
  }
  











  public WebSocketFactory setSSLSocketFactory(SSLSocketFactory factory)
  {
    mSocketFactorySettings.setSSLSocketFactory(factory);
    
    return this;
  }
  







  public SSLContext getSSLContext()
  {
    return mSocketFactorySettings.getSSLContext();
  }
  











  public WebSocketFactory setSSLContext(SSLContext context)
  {
    mSocketFactorySettings.setSSLContext(context);
    
    return this;
  }
  











  public ProxySettings getProxySettings()
  {
    return mProxySettings;
  }
  
















  public int getConnectionTimeout()
  {
    return mConnectionTimeout;
  }
  
















  public WebSocketFactory setConnectionTimeout(int timeout)
  {
    if (timeout < 0)
    {
      throw new IllegalArgumentException("timeout value cannot be negative.");
    }
    
    mConnectionTimeout = timeout;
    
    return this;
  }
  
















  public int getSocketTimeout()
  {
    return mSocketTimeout;
  }
  






















  public WebSocketFactory setSocketTimeout(int timeout)
  {
    if (timeout < 0)
    {
      throw new IllegalArgumentException("timeout value cannot be negative.");
    }
    
    mSocketTimeout = timeout;
    
    return this;
  }
  














  public DualStackMode getDualStackMode()
  {
    return mDualStackMode;
  }
  











  public WebSocketFactory setDualStackMode(DualStackMode mode)
  {
    mDualStackMode = mode;
    
    return this;
  }
  
















  public int getDualStackFallbackDelay()
  {
    return mDualStackFallbackDelay;
  }
  












  public WebSocketFactory setDualStackFallbackDelay(int delay)
  {
    if (delay < 0)
    {
      throw new IllegalArgumentException("delay value cannot be negative.");
    }
    
    mDualStackFallbackDelay = delay;
    
    return this;
  }
  













  public boolean getVerifyHostname()
  {
    return mVerifyHostname;
  }
  































  public WebSocketFactory setVerifyHostname(boolean verifyHostname)
  {
    mVerifyHostname = verifyHostname;
    
    return this;
  }
  









  public String[] getServerNames()
  {
    return mServerNames;
  }
  

















  public WebSocketFactory setServerNames(String[] serverNames)
  {
    mServerNames = serverNames;
    
    return this;
  }
  
















  public WebSocketFactory setServerName(String serverName)
  {
    return setServerNames(new String[] { serverName });
  }
  





















  public WebSocket createSocket(String uri)
    throws IOException
  {
    return createSocket(uri, getConnectionTimeout());
  }
  




























  public WebSocket createSocket(String uri, int timeout)
    throws IOException
  {
    if (uri == null)
    {
      throw new IllegalArgumentException("The given URI is null.");
    }
    
    if (timeout < 0)
    {
      throw new IllegalArgumentException("The given timeout value is negative.");
    }
    
    return createSocket(URI.create(uri), timeout);
  }
  





















  public WebSocket createSocket(URL url)
    throws IOException
  {
    return createSocket(url, getConnectionTimeout());
  }
  



























  public WebSocket createSocket(URL url, int timeout)
    throws IOException
  {
    if (url == null)
    {
      throw new IllegalArgumentException("The given URL is null.");
    }
    
    if (timeout < 0)
    {
      throw new IllegalArgumentException("The given timeout value is negative.");
    }
    
    try
    {
      return createSocket(url.toURI(), timeout);
    }
    catch (URISyntaxException e)
    {
      throw new IllegalArgumentException("Failed to convert the given URL into a URI.");
    }
  }
  




















































  public WebSocket createSocket(URI uri)
    throws IOException
  {
    return createSocket(uri, getConnectionTimeout());
  }
  

























































  public WebSocket createSocket(URI uri, int timeout)
    throws IOException
  {
    if (uri == null)
    {
      throw new IllegalArgumentException("The given URI is null.");
    }
    
    if (timeout < 0)
    {
      throw new IllegalArgumentException("The given timeout value is negative.");
    }
    

    String scheme = uri.getScheme();
    String userInfo = uri.getUserInfo();
    String host = Misc.extractHost(uri);
    int port = uri.getPort();
    String path = uri.getRawPath();
    String query = uri.getRawQuery();
    
    return createSocket(scheme, userInfo, host, port, path, query, timeout);
  }
  



  private WebSocket createSocket(String scheme, String userInfo, String host, int port, String path, String query, int timeout)
    throws IOException
  {
    boolean secure = isSecureConnectionRequired(scheme);
    

    if ((host == null) || (host.length() == 0))
    {
      throw new IllegalArgumentException("The host part is empty.");
    }
    

    path = determinePath(path);
    

    SocketConnector connector = createRawSocket(host, port, secure, timeout);
    

    return createWebSocket(secure, userInfo, host, port, path, query, connector);
  }
  

  private static boolean isSecureConnectionRequired(String scheme)
  {
    if ((scheme == null) || (scheme.length() == 0))
    {
      throw new IllegalArgumentException("The scheme part is empty.");
    }
    
    if (("wss".equalsIgnoreCase(scheme)) || ("https".equalsIgnoreCase(scheme)))
    {
      return true;
    }
    
    if (("ws".equalsIgnoreCase(scheme)) || ("http".equalsIgnoreCase(scheme)))
    {
      return false;
    }
    
    throw new IllegalArgumentException("Bad scheme: " + scheme);
  }
  

  private static String determinePath(String path)
  {
    if ((path == null) || (path.length() == 0))
    {
      return "/";
    }
    
    if (path.startsWith("/"))
    {
      return path;
    }
    

    return "/" + path;
  }
  




  private SocketConnector createRawSocket(String host, int port, boolean secure, int timeout)
    throws IOException
  {
    port = determinePort(port, secure);
    

    boolean proxied = mProxySettings.getHost() != null;
    



    if (proxied)
    {

      return createProxiedRawSocket(host, port, secure, timeout);
    }
    


    return createDirectRawSocket(host, port, secure, timeout);
  }
  






  private SocketConnector createProxiedRawSocket(String host, int port, boolean secure, int timeout)
  {
    int proxyPort = determinePort(mProxySettings.getPort(), mProxySettings.isSecure());
    

    SocketFactory factory = mProxySettings.selectSocketFactory();
    

    Address address = new Address(mProxySettings.getHost(), proxyPort);
    

    ProxyHandshaker handshaker = new ProxyHandshaker(host, port, mProxySettings);
    


    SSLSocketFactory sslSocketFactory = secure ? (SSLSocketFactory)mSocketFactorySettings.selectSocketFactory(secure) : null;
    

    return new SocketConnector(factory, address, timeout, mSocketTimeout, mProxySettings
      .getServerNames(), handshaker, sslSocketFactory, host, port)
      
      .setDualStackSettings(mDualStackMode, mDualStackFallbackDelay)
      .setVerifyHostname(mVerifyHostname);
  }
  


  private SocketConnector createDirectRawSocket(String host, int port, boolean secure, int timeout)
  {
    SocketFactory factory = mSocketFactorySettings.selectSocketFactory(secure);
    

    Address address = new Address(host, port);
    

    return new SocketConnector(factory, address, timeout, mServerNames, mSocketTimeout)
      .setDualStackSettings(mDualStackMode, mDualStackFallbackDelay)
      .setVerifyHostname(mVerifyHostname);
  }
  

  private static int determinePort(int port, boolean secure)
  {
    if (0 <= port)
    {
      return port;
    }
    
    if (secure)
    {
      return 443;
    }
    

    return 80;
  }
  





  private WebSocket createWebSocket(boolean secure, String userInfo, String host, int port, String path, String query, SocketConnector connector)
  {
    if (0 <= port)
    {
      host = host + ":" + port;
    }
    

    if (query != null)
    {
      path = path + "?" + query;
    }
    
    return new WebSocket(this, secure, userInfo, host, path, connector);
  }
}
