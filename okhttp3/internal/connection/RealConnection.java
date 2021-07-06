package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket.Streams;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;




























public final class RealConnection
  extends Http2Connection.Listener
  implements Connection
{
  private static final String NPE_THROW_WITH_NULL = "throw with null exception";
  private static final int MAX_TUNNEL_ATTEMPTS = 21;
  private final ConnectionPool connectionPool;
  private final Route route;
  private Socket rawSocket;
  private Socket socket;
  private Handshake handshake;
  private Protocol protocol;
  private Http2Connection http2Connection;
  private BufferedSource source;
  private BufferedSink sink;
  public boolean noNewStreams;
  public int successCount;
  public int allocationLimit = 1;
  

  public final List<Reference<StreamAllocation>> allocations = new ArrayList();
  

  public long idleAtNanos = Long.MAX_VALUE;
  
  public RealConnection(ConnectionPool connectionPool, Route route) {
    this.connectionPool = connectionPool;
    this.route = route;
  }
  
  public static RealConnection testConnection(ConnectionPool connectionPool, Route route, Socket socket, long idleAtNanos)
  {
    RealConnection result = new RealConnection(connectionPool, route);
    socket = socket;
    idleAtNanos = idleAtNanos;
    return result;
  }
  

  public void connect(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled, Call call, EventListener eventListener)
  {
    if (protocol != null) { throw new IllegalStateException("already connected");
    }
    RouteException routeException = null;
    List<ConnectionSpec> connectionSpecs = route.address().connectionSpecs();
    ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);
    
    if (route.address().sslSocketFactory() == null) {
      if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
        throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
      }
      
      String host = route.address().url().host();
      if (!Platform.get().isCleartextTrafficPermitted(host)) {
        throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
      }
      
    }
    else if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
      throw new RouteException(new UnknownServiceException("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
    }
    
    for (;;)
    {
      try
      {
        if (route.requiresTunnel()) {
          connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
          if (rawSocket == null) {
            break;
          }
        }
        else {
          connectSocket(connectTimeout, readTimeout, call, eventListener);
        }
        establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
        eventListener.connectEnd(call, route.socketAddress(), route.proxy(), protocol);
      }
      catch (IOException e) {
        Util.closeQuietly(socket);
        Util.closeQuietly(rawSocket);
        socket = null;
        rawSocket = null;
        source = null;
        sink = null;
        handshake = null;
        protocol = null;
        http2Connection = null;
        
        eventListener.connectFailed(call, route.socketAddress(), route.proxy(), null, e);
        
        if (routeException == null) {
          routeException = new RouteException(e);
        } else {
          routeException.addConnectException(e);
        }
        
        if ((!connectionRetryEnabled) || (!connectionSpecSelector.connectionFailed(e))) {
          throw routeException;
        }
      }
    }
    
    if ((route.requiresTunnel()) && (rawSocket == null)) {
      ProtocolException exception = new ProtocolException("Too many tunnel connections attempted: 21");
      
      throw new RouteException(exception);
    }
    
    if (http2Connection != null) {
      synchronized (connectionPool) {
        allocationLimit = http2Connection.maxConcurrentStreams();
      }
    }
  }
  



  private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener)
    throws IOException
  {
    Request tunnelRequest = createTunnelRequest();
    HttpUrl url = tunnelRequest.url();
    for (int i = 0; i < 21; i++) {
      connectSocket(connectTimeout, readTimeout, call, eventListener);
      tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
      
      if (tunnelRequest == null) {
        break;
      }
      
      Util.closeQuietly(rawSocket);
      rawSocket = null;
      sink = null;
      source = null;
      eventListener.connectEnd(call, route.socketAddress(), route.proxy(), null);
    }
  }
  
  private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener)
    throws IOException
  {
    Proxy proxy = route.proxy();
    Address address = route.address();
    


    rawSocket = ((proxy.type() == Proxy.Type.DIRECT) || (proxy.type() == Proxy.Type.HTTP) ? address.socketFactory().createSocket() : new Socket(proxy));
    
    eventListener.connectStart(call, route.socketAddress(), proxy);
    rawSocket.setSoTimeout(readTimeout);
    try {
      Platform.get().connectSocket(rawSocket, route.socketAddress(), connectTimeout);
    } catch (ConnectException e) {
      ConnectException ce = new ConnectException("Failed to connect to " + route.socketAddress());
      ce.initCause(e);
      throw ce;
    }
    



    try
    {
      source = Okio.buffer(Okio.source(rawSocket));
      sink = Okio.buffer(Okio.sink(rawSocket));
    } catch (NullPointerException npe) {
      if ("throw with null exception".equals(npe.getMessage())) {
        throw new IOException(npe);
      }
    }
  }
  
  private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException
  {
    if (route.address().sslSocketFactory() == null) {
      if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
        socket = rawSocket;
        protocol = Protocol.H2_PRIOR_KNOWLEDGE;
        startHttp2(pingIntervalMillis);
        return;
      }
      
      socket = rawSocket;
      protocol = Protocol.HTTP_1_1;
      return;
    }
    
    eventListener.secureConnectStart(call);
    connectTls(connectionSpecSelector);
    eventListener.secureConnectEnd(call, handshake);
    
    if (protocol == Protocol.HTTP_2) {
      startHttp2(pingIntervalMillis);
    }
  }
  
  private void startHttp2(int pingIntervalMillis) throws IOException {
    socket.setSoTimeout(0);
    



    http2Connection = new Http2Connection.Builder(true).socket(socket, route.address().url().host(), source, sink).listener(this).pingIntervalMillis(pingIntervalMillis).build();
    http2Connection.start();
  }
  
  private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
    Address address = route.address();
    SSLSocketFactory sslSocketFactory = address.sslSocketFactory();
    boolean success = false;
    SSLSocket sslSocket = null;
    try
    {
      sslSocket = (SSLSocket)sslSocketFactory.createSocket(rawSocket, address
        .url().host(), address.url().port(), true);
      

      ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
      if (connectionSpec.supportsTlsExtensions()) {
        Platform.get().configureTlsExtensions(sslSocket, address
          .url().host(), address.protocols());
      }
      

      sslSocket.startHandshake();
      
      SSLSession sslSocketSession = sslSocket.getSession();
      Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
      

      if (!address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
        List<Certificate> peerCertificates = unverifiedHandshake.peerCertificates();
        if (!peerCertificates.isEmpty()) {
          X509Certificate cert = (X509Certificate)peerCertificates.get(0);
          



          throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
        }
        
        throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified (no certificates)");
      }
      


      address.certificatePinner().check(address.url().host(), unverifiedHandshake
        .peerCertificates());
      



      String maybeProtocol = connectionSpec.supportsTlsExtensions() ? Platform.get().getSelectedProtocol(sslSocket) : null;
      socket = sslSocket;
      source = Okio.buffer(Okio.source(socket));
      sink = Okio.buffer(Okio.sink(socket));
      handshake = unverifiedHandshake;
      

      protocol = (maybeProtocol != null ? Protocol.get(maybeProtocol) : Protocol.HTTP_1_1);
      success = true;
    } catch (AssertionError e) {
      if (Util.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    } finally {
      if (sslSocket != null) {
        Platform.get().afterHandshake(sslSocket);
      }
      if (!success) {
        Util.closeQuietly(sslSocket);
      }
    }
  }
  




  private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url)
    throws IOException
  {
    String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";
    for (;;) {
      Http1Codec tunnelConnection = new Http1Codec(null, null, source, sink);
      source.timeout().timeout(readTimeout, TimeUnit.MILLISECONDS);
      sink.timeout().timeout(writeTimeout, TimeUnit.MILLISECONDS);
      tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
      tunnelConnection.finishRequest();
      

      Response response = tunnelConnection.readResponseHeaders(false).request(tunnelRequest).build();
      

      long contentLength = HttpHeaders.contentLength(response);
      if (contentLength == -1L) {
        contentLength = 0L;
      }
      Source body = tunnelConnection.newFixedLengthSource(contentLength);
      Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
      body.close();
      
      switch (response.code())
      {



      case 200: 
        if ((!source.getBuffer().exhausted()) || (!sink.buffer().exhausted())) {
          throw new IOException("TLS tunnel buffered too many bytes!");
        }
        return null;
      
      case 407: 
        tunnelRequest = route.address().proxyAuthenticator().authenticate(route, response);
        if (tunnelRequest == null) { throw new IOException("Failed to authenticate with proxy");
        }
        if ("close".equalsIgnoreCase(response.header("Connection"))) {
          return tunnelRequest;
        }
        

        break;
      default: 
        throw new IOException("Unexpected response code for CONNECT: " + response.code());
      }
      
    }
  }
  












  private Request createTunnelRequest()
    throws IOException
  {
    Request proxyConnectRequest = new Request.Builder().url(route.address().url()).method("CONNECT", null).header("Host", Util.hostHeader(route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    









    Response fakeAuthChallengeResponse = new Response.Builder().request(proxyConnectRequest).protocol(Protocol.HTTP_1_1).code(407).message("Preemptive Authenticate").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(-1L).header("Proxy-Authenticate", "OkHttp-Preemptive").build();
    

    Request authenticatedRequest = route.address().proxyAuthenticator().authenticate(route, fakeAuthChallengeResponse);
    
    return authenticatedRequest != null ? 
      authenticatedRequest : 
      proxyConnectRequest;
  }
  




  public boolean isEligible(Address address, @Nullable Route route)
  {
    if ((allocations.size() >= allocationLimit) || (noNewStreams)) { return false;
    }
    
    if (!Internal.instance.equalsNonHost(this.route.address(), address)) { return false;
    }
    
    if (address.url().host().equals(route().address().url().host())) {
      return true;
    }
    






    if (http2Connection == null) { return false;
    }
    


    if (route == null) return false;
    if (route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (this.route.proxy().type() != Proxy.Type.DIRECT) return false;
    if (!this.route.socketAddress().equals(route.socketAddress())) { return false;
    }
    
    if (route.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE) return false;
    if (!supportsUrl(address.url())) { return false;
    }
    try
    {
      address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
    } catch (SSLPeerUnverifiedException e) {
      return false;
    }
    
    return true;
  }
  
  public boolean supportsUrl(HttpUrl url) {
    if (url.port() != route.address().url().port()) {
      return false;
    }
    
    if (!url.host().equals(route.address().url().host()))
    {
      return (handshake != null) && (OkHostnameVerifier.INSTANCE.verify(url
        .host(), (X509Certificate)handshake.peerCertificates().get(0)));
    }
    
    return true;
  }
  
  public HttpCodec newCodec(OkHttpClient client, Interceptor.Chain chain, StreamAllocation streamAllocation) throws SocketException
  {
    if (http2Connection != null) {
      return new Http2Codec(client, chain, streamAllocation, http2Connection);
    }
    socket.setSoTimeout(chain.readTimeoutMillis());
    source.timeout().timeout(chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    sink.timeout().timeout(chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
    return new Http1Codec(client, streamAllocation, source, sink);
  }
  
  public RealWebSocket.Streams newWebSocketStreams(final StreamAllocation streamAllocation)
  {
    new RealWebSocket.Streams(true, source, sink) {
      public void close() throws IOException {
        streamAllocation.streamFinished(true, streamAllocation.codec(), -1L, null);
      }
    };
  }
  
  public Route route() {
    return route;
  }
  
  public void cancel()
  {
    Util.closeQuietly(rawSocket);
  }
  
  public Socket socket() {
    return socket;
  }
  
  public boolean isHealthy(boolean doExtensiveChecks)
  {
    if ((socket.isClosed()) || (socket.isInputShutdown()) || (socket.isOutputShutdown())) {
      return false;
    }
    
    if (http2Connection != null) {
      return !http2Connection.isShutdown();
    }
    
    if (doExtensiveChecks) {
      try {
        int readTimeout = socket.getSoTimeout();
        try {
          socket.setSoTimeout(1);
          boolean bool; if (source.exhausted()) {
            return false;
          }
          return true;
        } finally {
          socket.setSoTimeout(readTimeout);
        }
        






        return true;
      }
      catch (SocketTimeoutException localSocketTimeoutException) {}catch (IOException e)
      {
        return false;
      }
    }
  }
  

  public void onStream(Http2Stream stream)
    throws IOException
  {
    stream.close(ErrorCode.REFUSED_STREAM);
  }
  
  public void onSettings(Http2Connection connection)
  {
    synchronized (connectionPool) {
      allocationLimit = connection.maxConcurrentStreams();
    }
  }
  
  public Handshake handshake() {
    return handshake;
  }
  



  public boolean isMultiplexed()
  {
    return http2Connection != null;
  }
  
  public Protocol protocol() {
    return protocol;
  }
  
  public String toString() {
    return 
    





      "Connection{" + route.address().url().host() + ":" + route.address().url().port() + ", proxy=" + route.proxy() + " hostAddress=" + route.socketAddress() + " cipherSuite=" + (handshake != null ? handshake.cipherSuite() : "none") + " protocol=" + protocol + '}';
  }
}
