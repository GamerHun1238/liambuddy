package com.neovisionaries.ws.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


















class HandshakeBuilder
{
  private static final String[] CONNECTION_HEADER = { "Connection", "Upgrade" };
  private static final String[] UPGRADE_HEADER = { "Upgrade", "websocket" };
  private static final String[] VERSION_HEADER = { "Sec-WebSocket-Version", "13" };
  
  private static final String RN = "\r\n";
  
  private boolean mSecure;
  
  private String mUserInfo;
  private final String mHost;
  private final String mPath;
  private final URI mUri;
  private String mKey;
  private Set<String> mProtocols;
  private List<WebSocketExtension> mExtensions;
  private List<String[]> mHeaders;
  
  public HandshakeBuilder(boolean secure, String userInfo, String host, String path)
  {
    mSecure = secure;
    mUserInfo = userInfo;
    mHost = host;
    mPath = path;
    


    mUri = URI.create(String.format("%s://%s%s", new Object[] { secure ? "wss" : "ws", host, path }));
  }
  


  public HandshakeBuilder(HandshakeBuilder source)
  {
    mSecure = mSecure;
    mUserInfo = mUserInfo;
    mHost = mHost;
    mPath = mPath;
    mUri = mUri;
    mKey = mKey;
    mProtocols = copyProtocols(mProtocols);
    mExtensions = copyExtensions(mExtensions);
    mHeaders = copyHeaders(mHeaders);
  }
  

  public void addProtocol(String protocol)
  {
    if (!isValidProtocol(protocol))
    {
      throw new IllegalArgumentException("'protocol' must be a non-empty string with characters in the range U+0021 to U+007E not including separator characters.");
    }
    


    synchronized (this)
    {
      if (mProtocols == null)
      {



        mProtocols = new LinkedHashSet();
      }
      
      mProtocols.add(protocol);
    }
  }
  

  public void removeProtocol(String protocol)
  {
    if (protocol == null)
    {
      return;
    }
    
    synchronized (this)
    {
      if (mProtocols == null)
      {
        return;
      }
      
      mProtocols.remove(protocol);
      
      if (mProtocols.size() == 0)
      {
        mProtocols = null;
      }
    }
  }
  

  public void clearProtocols()
  {
    synchronized (this)
    {
      mProtocols = null;
    }
  }
  

  private static boolean isValidProtocol(String protocol)
  {
    if ((protocol == null) || (protocol.length() == 0))
    {
      return false;
    }
    
    int len = protocol.length();
    
    for (int i = 0; i < len; i++)
    {
      char ch = protocol.charAt(i);
      
      if ((ch < '!') || ('~' < ch) || (Token.isSeparator(ch)))
      {
        return false;
      }
    }
    
    return true;
  }
  

  public boolean containsProtocol(String protocol)
  {
    synchronized (this)
    {
      if (mProtocols == null)
      {
        return false;
      }
      
      return mProtocols.contains(protocol);
    }
  }
  

  public void addExtension(WebSocketExtension extension)
  {
    if (extension == null)
    {
      return;
    }
    
    synchronized (this)
    {
      if (mExtensions == null)
      {
        mExtensions = new ArrayList();
      }
      
      mExtensions.add(extension);
    }
  }
  

  public void addExtension(String extension)
  {
    addExtension(WebSocketExtension.parse(extension));
  }
  

  public void removeExtension(WebSocketExtension extension)
  {
    if (extension == null)
    {
      return;
    }
    
    synchronized (this)
    {
      if (mExtensions == null)
      {
        return;
      }
      
      mExtensions.remove(extension);
      
      if (mExtensions.size() == 0)
      {
        mExtensions = null;
      }
    }
  }
  

  public void removeExtensions(String name)
  {
    if (name == null)
    {
      return;
    }
    
    synchronized (this)
    {
      if (mExtensions == null)
      {
        return;
      }
      
      List<WebSocketExtension> extensionsToRemove = new ArrayList();
      
      for (WebSocketExtension extension : mExtensions)
      {
        if (extension.getName().equals(name))
        {
          extensionsToRemove.add(extension);
        }
      }
      
      for (WebSocketExtension extension : extensionsToRemove)
      {
        mExtensions.remove(extension);
      }
      
      if (mExtensions.size() == 0)
      {
        mExtensions = null;
      }
    }
  }
  

  public void clearExtensions()
  {
    synchronized (this)
    {
      mExtensions = null;
    }
  }
  

  public boolean containsExtension(WebSocketExtension extension)
  {
    if (extension == null)
    {
      return false;
    }
    
    synchronized (this)
    {
      if (mExtensions == null)
      {
        return false;
      }
      
      return mExtensions.contains(extension);
    }
  }
  

  public boolean containsExtension(String name)
  {
    if (name == null)
    {
      return false;
    }
    
    synchronized (this)
    {
      if (mExtensions == null)
      {
        return false;
      }
      
      for (WebSocketExtension extension : mExtensions)
      {
        if (extension.getName().equals(name))
        {
          return true;
        }
      }
      
      return false;
    }
  }
  

  public void addHeader(String name, String value)
  {
    if ((name == null) || (name.length() == 0))
    {
      return;
    }
    
    if (value == null)
    {
      value = "";
    }
    
    synchronized (this)
    {
      if (mHeaders == null)
      {
        mHeaders = new ArrayList();
      }
      
      mHeaders.add(new String[] { name, value });
    }
  }
  

  public void removeHeaders(String name)
  {
    if ((name == null) || (name.length() == 0))
    {
      return;
    }
    
    synchronized (this)
    {
      if (mHeaders == null)
      {
        return;
      }
      
      List<String[]> headersToRemove = new ArrayList();
      
      for (String[] header : mHeaders)
      {
        if (header[0].equals(name))
        {
          headersToRemove.add(header);
        }
      }
      
      for (String[] header : headersToRemove)
      {
        mHeaders.remove(header);
      }
      
      if (mHeaders.size() == 0)
      {
        mHeaders = null;
      }
    }
  }
  

  public void clearHeaders()
  {
    synchronized (this)
    {
      mHeaders = null;
    }
  }
  

  public void setUserInfo(String userInfo)
  {
    synchronized (this)
    {
      mUserInfo = userInfo;
    }
  }
  

  public void setUserInfo(String id, String password)
  {
    if (id == null)
    {
      id = "";
    }
    
    if (password == null)
    {
      password = "";
    }
    
    String userInfo = String.format("%s:%s", new Object[] { id, password });
    
    setUserInfo(userInfo);
  }
  

  public void clearUserInfo()
  {
    synchronized (this)
    {
      mUserInfo = null;
    }
  }
  

  public URI getURI()
  {
    return mUri;
  }
  

  public void setKey(String key)
  {
    mKey = key;
  }
  

  public String buildRequestLine()
  {
    return String.format("GET %s HTTP/1.1", new Object[] { mPath });
  }
  

  public List<String[]> buildHeaders()
  {
    List<String[]> headers = new ArrayList();
    

    headers.add(new String[] { "Host", mHost });
    

    headers.add(CONNECTION_HEADER);
    

    headers.add(UPGRADE_HEADER);
    

    headers.add(VERSION_HEADER);
    

    headers.add(new String[] { "Sec-WebSocket-Key", mKey });
    

    if ((mProtocols != null) && (mProtocols.size() != 0))
    {
      headers.add(new String[] { "Sec-WebSocket-Protocol", Misc.join(mProtocols, ", ") });
    }
    

    if ((mExtensions != null) && (mExtensions.size() != 0))
    {
      headers.add(new String[] { "Sec-WebSocket-Extensions", Misc.join(mExtensions, ", ") });
    }
    

    if ((mUserInfo != null) && (mUserInfo.length() != 0))
    {
      headers.add(new String[] { "Authorization", "Basic " + Base64.encode(mUserInfo) });
    }
    

    if ((mHeaders != null) && (mHeaders.size() != 0))
    {
      headers.addAll(mHeaders);
    }
    
    return headers;
  }
  

  public static String build(String requestLine, List<String[]> headers)
  {
    StringBuilder builder = new StringBuilder();
    

    builder.append(requestLine).append("\r\n");
    

    for (String[] header : headers)
    {

      builder.append(header[0]).append(": ").append(header[1]).append("\r\n");
    }
    

    builder.append("\r\n");
    
    return builder.toString();
  }
  

  private static Set<String> copyProtocols(Set<String> protocols)
  {
    if (protocols == null)
    {
      return null;
    }
    
    Set<String> newProtocols = new LinkedHashSet(protocols.size());
    
    newProtocols.addAll(protocols);
    
    return newProtocols;
  }
  

  private static List<WebSocketExtension> copyExtensions(List<WebSocketExtension> extensions)
  {
    if (extensions == null)
    {
      return null;
    }
    

    List<WebSocketExtension> newExtensions = new ArrayList(extensions.size());
    
    for (WebSocketExtension extension : extensions)
    {
      newExtensions.add(new WebSocketExtension(extension));
    }
    
    return newExtensions;
  }
  

  private static List<String[]> copyHeaders(List<String[]> headers)
  {
    if (headers == null)
    {
      return null;
    }
    
    List<String[]> newHeaders = new ArrayList(headers.size());
    
    for (String[] header : headers)
    {
      newHeaders.add(copyHeader(header));
    }
    
    return newHeaders;
  }
  

  private static String[] copyHeader(String[] header)
  {
    String[] newHeader = new String[2];
    
    newHeader[0] = header[0];
    newHeader[1] = header[1];
    
    return newHeader;
  }
}
