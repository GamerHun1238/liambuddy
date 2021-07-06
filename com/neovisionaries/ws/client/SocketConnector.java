package com.neovisionaries.ws.client;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;






















class SocketConnector
{
  private final SocketFactory mSocketFactory;
  private final Address mAddress;
  private final int mConnectionTimeout;
  private final int mSocketTimeout;
  private final String[] mServerNames;
  private final ProxyHandshaker mProxyHandshaker;
  private final SSLSocketFactory mSSLSocketFactory;
  private final String mHost;
  private final int mPort;
  private DualStackMode mDualStackMode = DualStackMode.BOTH;
  private int mDualStackFallbackDelay = 250;
  private boolean mVerifyHostname;
  private Socket mSocket;
  
  SocketConnector(SocketFactory socketFactory, Address address, int timeout, String[] serverNames, int socketTimeout)
  {
    this(socketFactory, address, timeout, socketTimeout, serverNames, null, null, null, 0);
  }
  




  SocketConnector(SocketFactory socketFactory, Address address, int timeout, int socketTimeout, String[] serverNames, ProxyHandshaker handshaker, SSLSocketFactory sslSocketFactory, String host, int port)
  {
    mSocketFactory = socketFactory;
    mAddress = address;
    mConnectionTimeout = timeout;
    mSocketTimeout = socketTimeout;
    mServerNames = serverNames;
    mProxyHandshaker = handshaker;
    mSSLSocketFactory = sslSocketFactory;
    mHost = host;
    mPort = port;
  }
  

  public int getConnectionTimeout()
  {
    return mConnectionTimeout;
  }
  

  public Socket getSocket()
  {
    return mSocket;
  }
  

  public Socket getConnectedSocket()
    throws WebSocketException
  {
    if (mSocket == null)
    {
      connectSocket();
    }
    
    return mSocket;
  }
  

  private void connectSocket()
    throws WebSocketException
  {
    SocketInitiator socketInitiator = new SocketInitiator(mSocketFactory, mAddress, mConnectionTimeout, mServerNames, mDualStackMode, mDualStackFallbackDelay);
    



    InetAddress[] addresses = resolveHostname();
    


    try
    {
      mSocket = socketInitiator.establish(addresses);

    }
    catch (Exception e)
    {
      boolean proxied = mProxyHandshaker != null;
      

      String message = String.format("Failed to connect to %s'%s': %s", new Object[] { proxied ? "the proxy " : "", mAddress, e
        .getMessage() });
      

      throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, message, e);
    }
  }
  
  private InetAddress[] resolveHostname()
    throws WebSocketException
  {
    InetAddress[] addresses = null;
    UnknownHostException exception = null;
    

    try
    {
      addresses = InetAddress.getAllByName(mAddress.getHostname());
      

      Arrays.sort(addresses, new Comparator() {
        public int compare(InetAddress left, InetAddress right) {
          if (left.getClass() == right.getClass())
          {
            return 0;
          }
          if ((left instanceof Inet6Address))
          {
            return -1;
          }
          

          return 1;
        }
        
      });
    }
    catch (UnknownHostException e)
    {
      exception = e;
    }
    

    if ((addresses != null) && (addresses.length > 0))
    {
      return addresses;
    }
    
    if (exception == null)
    {
      exception = new UnknownHostException("No IP addresses found");
    }
    

    String message = String.format("Failed to resolve hostname %s: %s", new Object[] { mAddress, exception
      .getMessage() });
    

    throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, message, exception);
  }
  

  public Socket connect()
    throws WebSocketException
  {
    try
    {
      doConnect();
      assert (mSocket != null);
      return mSocket;

    }
    catch (WebSocketException e)
    {

      if (mSocket != null)
      {
        try
        {

          mSocket.close();
        }
        catch (IOException localIOException) {}
      }
      



      throw e;
    }
  }
  

  SocketConnector setDualStackSettings(DualStackMode mode, int fallbackDelay)
  {
    mDualStackMode = mode;
    mDualStackFallbackDelay = fallbackDelay;
    
    return this;
  }
  

  SocketConnector setVerifyHostname(boolean verifyHostname)
  {
    mVerifyHostname = verifyHostname;
    
    return this;
  }
  

  private void doConnect()
    throws WebSocketException
  {
    boolean proxied = mProxyHandshaker != null;
    

    connectSocket();
    assert (mSocket != null);
    
    if (mSocketTimeout > 0)
    {

      setSoTimeout(mSocketTimeout);
    }
    
    if ((mSocket instanceof SSLSocket))
    {


      verifyHostname((SSLSocket)mSocket, mAddress.getHostname());
    }
    

    if (proxied)
    {


      handshake();
    }
  }
  

  private void setSoTimeout(int timeout)
    throws WebSocketException
  {
    assert (mSocket != null);
    try
    {
      mSocket.setSoTimeout(timeout);

    }
    catch (SocketException e)
    {
      String message = String.format("Failed to set SO_TIMEOUT: %s", new Object[] {e
        .getMessage() });
      throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, message, e);
    }
  }
  
  private void verifyHostname(SSLSocket socket, String hostname)
    throws HostnameUnverifiedException
  {
    if (!mVerifyHostname)
    {

      return;
    }
    

    OkHostnameVerifier verifier = OkHostnameVerifier.INSTANCE;
    

    SSLSession session = socket.getSession();
    

    if (verifier.verify(hostname, session))
    {

      return;
    }
    

    throw new HostnameUnverifiedException(socket, hostname);
  }
  




  private void handshake()
    throws WebSocketException
  {
    assert (mSocket != null);
    

    try
    {
      mProxyHandshaker.perform(mSocket);

    }
    catch (IOException e)
    {
      String message = String.format("Handshake with the proxy server (%s) failed: %s", new Object[] { mAddress, e
        .getMessage() });
      

      throw new WebSocketException(WebSocketError.PROXY_HANDSHAKE_ERROR, message, e);
    }
    
    if (mSSLSocketFactory == null)
    {

      return;
    }
    

    try
    {
      mSocket = mSSLSocketFactory.createSocket(mSocket, mHost, mPort, true);

    }
    catch (IOException e)
    {
      String message = "Failed to overlay an existing socket: " + e.getMessage();
      

      throw new WebSocketException(WebSocketError.SOCKET_OVERLAY_ERROR, message, e);
    }
    


    try
    {
      ((SSLSocket)mSocket).startHandshake();
      


      verifyHostname((SSLSocket)mSocket, mProxyHandshaker.getProxiedHostname());

    }
    catch (IOException e)
    {
      String message = String.format("SSL handshake with the WebSocket endpoint (%s) failed: %s", new Object[] { mAddress, e
        .getMessage() });
      

      throw new WebSocketException(WebSocketError.SSL_HANDSHAKE_ERROR, message, e);
    }
  }
  

  void closeSilently()
  {
    if (mSocket != null)
    {
      try
      {
        mSocket.close();
      }
      catch (Throwable localThrowable) {}
    }
  }
}
