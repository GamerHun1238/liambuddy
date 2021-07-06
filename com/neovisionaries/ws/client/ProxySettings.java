package com.neovisionaries.ws.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;











































































public class ProxySettings
{
  private final WebSocketFactory mWebSocketFactory;
  private final Map<String, List<String>> mHeaders;
  private final SocketFactorySettings mSocketFactorySettings;
  private boolean mSecure;
  private String mHost;
  private int mPort;
  private String mId;
  private String mPassword;
  private String[] mServerNames;
  
  ProxySettings(WebSocketFactory factory)
  {
    mWebSocketFactory = factory;
    mHeaders = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mSocketFactorySettings = new SocketFactorySettings();
    
    reset();
  }
  












  ProxySettings(WebSocketFactory factory, ProxySettings settings)
  {
    this(factory);
    
    mHeaders.putAll(mHeaders);
    mSecure = mSecure;
    mHost = mHost;
    mPort = mPort;
    mId = mId;
    mPassword = mPassword;
    
    if (mServerNames != null)
    {
      mServerNames = new String[mServerNames.length];
      System.arraycopy(mServerNames, 0, mServerNames, 0, mServerNames.length);
    }
  }
  




  public WebSocketFactory getWebSocketFactory()
  {
    return mWebSocketFactory;
  }
  

























































  public ProxySettings reset()
  {
    mSecure = false;
    mHost = null;
    mPort = -1;
    mId = null;
    mPassword = null;
    mHeaders.clear();
    mServerNames = null;
    
    return this;
  }
  








  public boolean isSecure()
  {
    return mSecure;
  }
  











  public ProxySettings setSecure(boolean secure)
  {
    mSecure = secure;
    
    return this;
  }
  












  public String getHost()
  {
    return mHost;
  }
  














  public ProxySettings setHost(String host)
  {
    mHost = host;
    
    return this;
  }
  














  public int getPort()
  {
    return mPort;
  }
  
















  public ProxySettings setPort(int port)
  {
    mPort = port;
    
    return this;
  }
  
















  public String getId()
  {
    return mId;
  }
  


















  public ProxySettings setId(String id)
  {
    mId = id;
    
    return this;
  }
  







  public String getPassword()
  {
    return mPassword;
  }
  










  public ProxySettings setPassword(String password)
  {
    mPassword = password;
    
    return this;
  }
  
















  public ProxySettings setCredentials(String id, String password)
  {
    return setId(id).setPassword(password);
  }
  















  public ProxySettings setServer(String uri)
  {
    if (uri == null)
    {
      return this;
    }
    
    return setServer(URI.create(uri));
  }
  















  public ProxySettings setServer(URL url)
  {
    if (url == null)
    {
      return this;
    }
    
    try
    {
      return setServer(url.toURI());
    }
    catch (URISyntaxException e)
    {
      throw new IllegalArgumentException(e);
    }
  }
  








































  public ProxySettings setServer(URI uri)
  {
    if (uri == null)
    {
      return this;
    }
    
    String scheme = uri.getScheme();
    String userInfo = uri.getUserInfo();
    String host = uri.getHost();
    int port = uri.getPort();
    
    return setServer(scheme, userInfo, host, port);
  }
  

  private ProxySettings setServer(String scheme, String userInfo, String host, int port)
  {
    setByScheme(scheme);
    setByUserInfo(userInfo);
    mHost = host;
    mPort = port;
    
    return this;
  }
  

  private void setByScheme(String scheme)
  {
    if ("http".equalsIgnoreCase(scheme))
    {
      mSecure = false;
    }
    else if ("https".equalsIgnoreCase(scheme))
    {
      mSecure = true;
    }
  }
  

  private void setByUserInfo(String userInfo)
  {
    if (userInfo == null)
    {
      return;
    }
    
    String[] pair = userInfo.split(":", 2);
    
    String pw;
    String pw;
    switch (pair.length)
    {
    case 2: 
      String id = pair[0];
      pw = pair[1];
      break;
    
    case 1: 
      String id = pair[0];
      pw = null;
      break;
    default: 
      return;
    }
    String pw;
    String id;
    if (id.length() == 0)
    {
      return;
    }
    
    mId = id;
    mPassword = pw;
  }
  









  public Map<String, List<String>> getHeaders()
  {
    return mHeaders;
  }
  















  public ProxySettings addHeader(String name, String value)
  {
    if ((name == null) || (name.length() == 0))
    {
      return this;
    }
    
    List<String> list = (List)mHeaders.get(name);
    
    if (list == null)
    {
      list = new ArrayList();
      mHeaders.put(name, list);
    }
    
    list.add(value);
    
    return this;
  }
  








  public SocketFactory getSocketFactory()
  {
    return mSocketFactorySettings.getSocketFactory();
  }
  










  public ProxySettings setSocketFactory(SocketFactory factory)
  {
    mSocketFactorySettings.setSocketFactory(factory);
    
    return this;
  }
  








  public SSLSocketFactory getSSLSocketFactory()
  {
    return mSocketFactorySettings.getSSLSocketFactory();
  }
  










  public ProxySettings setSSLSocketFactory(SSLSocketFactory factory)
  {
    mSocketFactorySettings.setSSLSocketFactory(factory);
    
    return this;
  }
  







  public SSLContext getSSLContext()
  {
    return mSocketFactorySettings.getSSLContext();
  }
  










  public ProxySettings setSSLContext(SSLContext context)
  {
    mSocketFactorySettings.setSSLContext(context);
    
    return this;
  }
  

  SocketFactory selectSocketFactory()
  {
    return mSocketFactorySettings.selectSocketFactory(mSecure);
  }
  









  public String[] getServerNames()
  {
    return mServerNames;
  }
  

















  public ProxySettings setServerNames(String[] serverNames)
  {
    mServerNames = serverNames;
    
    return this;
  }
  
















  public ProxySettings setServerName(String serverName)
  {
    return setServerNames(new String[] { serverName });
  }
}
