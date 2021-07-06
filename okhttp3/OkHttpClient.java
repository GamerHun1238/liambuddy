package okhttp3;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.proxy.NullProxySelector;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

























































































public class OkHttpClient
  implements Cloneable, Call.Factory, WebSocket.Factory
{
  static final List<Protocol> DEFAULT_PROTOCOLS = Util.immutableList(new Protocol[] { Protocol.HTTP_2, Protocol.HTTP_1_1 });
  

  static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableList(new ConnectionSpec[] { ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT });
  final Dispatcher dispatcher;
  
  static {
    Internal.instance = new Internal() {
      public void addLenient(Headers.Builder builder, String line) {
        builder.addLenient(line);
      }
      
      public void addLenient(Headers.Builder builder, String name, String value) {
        builder.addLenient(name, value);
      }
      
      public void setCache(OkHttpClient.Builder builder, InternalCache internalCache) {
        builder.setInternalCache(internalCache);
      }
      
      public boolean connectionBecameIdle(ConnectionPool pool, RealConnection connection)
      {
        return pool.connectionBecameIdle(connection);
      }
      
      public void acquire(ConnectionPool pool, Address address, StreamAllocation streamAllocation, @Nullable Route route)
      {
        pool.acquire(address, streamAllocation, route);
      }
      
      public boolean equalsNonHost(Address a, Address b) {
        return a.equalsNonHost(b);
      }
      
      @Nullable
      public Socket deduplicate(ConnectionPool pool, Address address, StreamAllocation streamAllocation) {
        return pool.deduplicate(address, streamAllocation);
      }
      
      public void put(ConnectionPool pool, RealConnection connection) {
        pool.put(connection);
      }
      
      public RouteDatabase routeDatabase(ConnectionPool connectionPool) {
        return routeDatabase;
      }
      
      public int code(Response.Builder responseBuilder) {
        return code;
      }
      
      public void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket, boolean isFallback)
      {
        tlsConfiguration.apply(sslSocket, isFallback);
      }
      
      public boolean isInvalidHttpUrlHost(IllegalArgumentException e) {
        return e.getMessage().startsWith("Invalid URL host");
      }
      
      public StreamAllocation streamAllocation(Call call) {
        return ((RealCall)call).streamAllocation();
      }
      
      @Nullable
      public IOException timeoutExit(Call call, @Nullable IOException e) { return ((RealCall)call).timeoutExit(e); }
      
      public Call newWebSocketCall(OkHttpClient client, Request originalRequest)
      {
        return RealCall.newRealCall(client, originalRequest, true);
      }
      
      public void initCodec(Response.Builder responseBuilder, HttpCodec httpCodec) {
        responseBuilder.initCodec(httpCodec);
      }
    };
  }
  
  @Nullable
  final Proxy proxy;
  final List<Protocol> protocols;
  final List<ConnectionSpec> connectionSpecs;
  final List<Interceptor> interceptors;
  final List<Interceptor> networkInterceptors;
  final EventListener.Factory eventListenerFactory;
  final ProxySelector proxySelector;
  final CookieJar cookieJar;
  @Nullable
  final Cache cache;
  @Nullable
  final InternalCache internalCache;
  final SocketFactory socketFactory;
  final SSLSocketFactory sslSocketFactory;
  final CertificateChainCleaner certificateChainCleaner;
  final HostnameVerifier hostnameVerifier;
  final CertificatePinner certificatePinner;
  final Authenticator proxyAuthenticator;
  final Authenticator authenticator;
  final ConnectionPool connectionPool;
  final Dns dns;
  final boolean followSslRedirects;
  final boolean followRedirects;
  final boolean retryOnConnectionFailure;
  final int callTimeout;
  final int connectTimeout;
  final int readTimeout;
  final int writeTimeout;
  final int pingInterval;
  public OkHttpClient() { this(new Builder()); }
  

  OkHttpClient(Builder builder) {
    dispatcher = dispatcher;
    proxy = proxy;
    protocols = protocols;
    connectionSpecs = connectionSpecs;
    interceptors = Util.immutableList(interceptors);
    networkInterceptors = Util.immutableList(networkInterceptors);
    eventListenerFactory = eventListenerFactory;
    proxySelector = proxySelector;
    cookieJar = cookieJar;
    cache = cache;
    internalCache = internalCache;
    socketFactory = socketFactory;
    
    boolean isTLS = false;
    for (ConnectionSpec spec : connectionSpecs) {
      isTLS = (isTLS) || (spec.isTls());
    }
    
    if ((sslSocketFactory != null) || (!isTLS)) {
      sslSocketFactory = sslSocketFactory;
      certificateChainCleaner = certificateChainCleaner;
    } else {
      X509TrustManager trustManager = Util.platformTrustManager();
      sslSocketFactory = newSslSocketFactory(trustManager);
      certificateChainCleaner = CertificateChainCleaner.get(trustManager);
    }
    
    if (sslSocketFactory != null) {
      Platform.get().configureSslSocketFactory(sslSocketFactory);
    }
    
    hostnameVerifier = hostnameVerifier;
    certificatePinner = certificatePinner.withCertificateChainCleaner(certificateChainCleaner);
    
    proxyAuthenticator = proxyAuthenticator;
    authenticator = authenticator;
    connectionPool = connectionPool;
    dns = dns;
    followSslRedirects = followSslRedirects;
    followRedirects = followRedirects;
    retryOnConnectionFailure = retryOnConnectionFailure;
    callTimeout = callTimeout;
    connectTimeout = connectTimeout;
    readTimeout = readTimeout;
    writeTimeout = writeTimeout;
    pingInterval = pingInterval;
    
    if (interceptors.contains(null)) {
      throw new IllegalStateException("Null interceptor: " + interceptors);
    }
    if (networkInterceptors.contains(null)) {
      throw new IllegalStateException("Null network interceptor: " + networkInterceptors);
    }
  }
  
  private static SSLSocketFactory newSslSocketFactory(X509TrustManager trustManager) {
    try {
      SSLContext sslContext = Platform.get().getSSLContext();
      sslContext.init(null, new TrustManager[] { trustManager }, null);
      return sslContext.getSocketFactory();
    } catch (GeneralSecurityException e) {
      throw new AssertionError("No System TLS", e);
    }
  }
  



  public int callTimeoutMillis()
  {
    return callTimeout;
  }
  
  public int connectTimeoutMillis()
  {
    return connectTimeout;
  }
  
  public int readTimeoutMillis()
  {
    return readTimeout;
  }
  
  public int writeTimeoutMillis()
  {
    return writeTimeout;
  }
  
  public int pingIntervalMillis()
  {
    return pingInterval;
  }
  
  @Nullable
  public Proxy proxy() { return proxy; }
  
  public ProxySelector proxySelector()
  {
    return proxySelector;
  }
  
  public CookieJar cookieJar() {
    return cookieJar;
  }
  
  @Nullable
  public Cache cache() { return cache; }
  
  @Nullable
  InternalCache internalCache() {
    return cache != null ? cache.internalCache : internalCache;
  }
  
  public Dns dns() {
    return dns;
  }
  
  public SocketFactory socketFactory() {
    return socketFactory;
  }
  
  public SSLSocketFactory sslSocketFactory() {
    return sslSocketFactory;
  }
  
  public HostnameVerifier hostnameVerifier() {
    return hostnameVerifier;
  }
  
  public CertificatePinner certificatePinner() {
    return certificatePinner;
  }
  
  public Authenticator authenticator() {
    return authenticator;
  }
  
  public Authenticator proxyAuthenticator() {
    return proxyAuthenticator;
  }
  
  public ConnectionPool connectionPool() {
    return connectionPool;
  }
  
  public boolean followSslRedirects() {
    return followSslRedirects;
  }
  
  public boolean followRedirects() {
    return followRedirects;
  }
  
  public boolean retryOnConnectionFailure() {
    return retryOnConnectionFailure;
  }
  
  public Dispatcher dispatcher() {
    return dispatcher;
  }
  
  public List<Protocol> protocols() {
    return protocols;
  }
  
  public List<ConnectionSpec> connectionSpecs() {
    return connectionSpecs;
  }
  




  public List<Interceptor> interceptors()
  {
    return interceptors;
  }
  




  public List<Interceptor> networkInterceptors()
  {
    return networkInterceptors;
  }
  
  public EventListener.Factory eventListenerFactory() {
    return eventListenerFactory;
  }
  


  public Call newCall(Request request)
  {
    return RealCall.newRealCall(this, request, false);
  }
  


  public WebSocket newWebSocket(Request request, WebSocketListener listener)
  {
    RealWebSocket webSocket = new RealWebSocket(request, listener, new Random(), pingInterval);
    webSocket.connect(this);
    return webSocket;
  }
  

  public Builder newBuilder() { return new Builder(this); }
  
  public static final class Builder {
    Dispatcher dispatcher;
    @Nullable
    Proxy proxy;
    List<Protocol> protocols;
    List<ConnectionSpec> connectionSpecs;
    final List<Interceptor> interceptors = new ArrayList();
    final List<Interceptor> networkInterceptors = new ArrayList();
    
    EventListener.Factory eventListenerFactory;
    
    ProxySelector proxySelector;
    
    CookieJar cookieJar;
    
    @Nullable
    Cache cache;
    
    @Nullable
    InternalCache internalCache;
    
    SocketFactory socketFactory;
    @Nullable
    SSLSocketFactory sslSocketFactory;
    @Nullable
    CertificateChainCleaner certificateChainCleaner;
    HostnameVerifier hostnameVerifier;
    CertificatePinner certificatePinner;
    Authenticator proxyAuthenticator;
    
    public Builder()
    {
      dispatcher = new Dispatcher();
      protocols = OkHttpClient.DEFAULT_PROTOCOLS;
      connectionSpecs = OkHttpClient.DEFAULT_CONNECTION_SPECS;
      eventListenerFactory = EventListener.factory(EventListener.NONE);
      proxySelector = ProxySelector.getDefault();
      if (proxySelector == null) {
        proxySelector = new NullProxySelector();
      }
      cookieJar = CookieJar.NO_COOKIES;
      socketFactory = SocketFactory.getDefault();
      hostnameVerifier = OkHostnameVerifier.INSTANCE;
      certificatePinner = CertificatePinner.DEFAULT;
      proxyAuthenticator = Authenticator.NONE;
      authenticator = Authenticator.NONE;
      connectionPool = new ConnectionPool();
      dns = Dns.SYSTEM;
      followSslRedirects = true;
      followRedirects = true;
      retryOnConnectionFailure = true;
      callTimeout = 0;
      connectTimeout = 10000;
      readTimeout = 10000;
      writeTimeout = 10000;
      pingInterval = 0;
    }
    
    Builder(OkHttpClient okHttpClient) {
      dispatcher = dispatcher;
      proxy = proxy;
      protocols = protocols;
      connectionSpecs = connectionSpecs;
      interceptors.addAll(interceptors);
      networkInterceptors.addAll(networkInterceptors);
      eventListenerFactory = eventListenerFactory;
      proxySelector = proxySelector;
      cookieJar = cookieJar;
      internalCache = internalCache;
      cache = cache;
      socketFactory = socketFactory;
      sslSocketFactory = sslSocketFactory;
      certificateChainCleaner = certificateChainCleaner;
      hostnameVerifier = hostnameVerifier;
      certificatePinner = certificatePinner;
      proxyAuthenticator = proxyAuthenticator;
      authenticator = authenticator;
      connectionPool = connectionPool;
      dns = dns;
      followSslRedirects = followSslRedirects;
      followRedirects = followRedirects;
      retryOnConnectionFailure = retryOnConnectionFailure;
      callTimeout = callTimeout;
      connectTimeout = connectTimeout;
      readTimeout = readTimeout;
      writeTimeout = writeTimeout;
      pingInterval = pingInterval;
    }
    









    public Builder callTimeout(long timeout, TimeUnit unit)
    {
      callTimeout = Util.checkDuration("timeout", timeout, unit);
      return this;
    }
    









    @IgnoreJRERequirement
    public Builder callTimeout(Duration duration)
    {
      callTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
      return this;
    }
    







    public Builder connectTimeout(long timeout, TimeUnit unit)
    {
      connectTimeout = Util.checkDuration("timeout", timeout, unit);
      return this;
    }
    







    @IgnoreJRERequirement
    public Builder connectTimeout(Duration duration)
    {
      connectTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
      return this;
    }
    









    public Builder readTimeout(long timeout, TimeUnit unit)
    {
      readTimeout = Util.checkDuration("timeout", timeout, unit);
      return this;
    }
    









    @IgnoreJRERequirement
    public Builder readTimeout(Duration duration)
    {
      readTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
      return this;
    }
    








    public Builder writeTimeout(long timeout, TimeUnit unit)
    {
      writeTimeout = Util.checkDuration("timeout", timeout, unit);
      return this;
    }
    








    @IgnoreJRERequirement
    public Builder writeTimeout(Duration duration)
    {
      writeTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
      return this;
    }
    












    public Builder pingInterval(long interval, TimeUnit unit)
    {
      pingInterval = Util.checkDuration("interval", interval, unit);
      return this;
    }
    












    @IgnoreJRERequirement
    public Builder pingInterval(Duration duration)
    {
      pingInterval = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
      return this;
    }
    




    public Builder proxy(@Nullable Proxy proxy)
    {
      this.proxy = proxy;
      return this;
    }
    







    public Builder proxySelector(ProxySelector proxySelector)
    {
      if (proxySelector == null) throw new NullPointerException("proxySelector == null");
      this.proxySelector = proxySelector;
      return this;
    }
    





    public Builder cookieJar(CookieJar cookieJar)
    {
      if (cookieJar == null) throw new NullPointerException("cookieJar == null");
      this.cookieJar = cookieJar;
      return this;
    }
    
    void setInternalCache(@Nullable InternalCache internalCache)
    {
      this.internalCache = internalCache;
      cache = null;
    }
    
    public Builder cache(@Nullable Cache cache)
    {
      this.cache = cache;
      internalCache = null;
      return this;
    }
    




    public Builder dns(Dns dns)
    {
      if (dns == null) throw new NullPointerException("dns == null");
      this.dns = dns;
      return this;
    }
    







    public Builder socketFactory(SocketFactory socketFactory)
    {
      if (socketFactory == null) throw new NullPointerException("socketFactory == null");
      if ((socketFactory instanceof SSLSocketFactory)) {
        throw new IllegalArgumentException("socketFactory instanceof SSLSocketFactory");
      }
      this.socketFactory = socketFactory;
      return this;
    }
    





    /**
     * @deprecated
     */
    public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory)
    {
      if (sslSocketFactory == null) throw new NullPointerException("sslSocketFactory == null");
      this.sslSocketFactory = sslSocketFactory;
      certificateChainCleaner = Platform.get().buildCertificateChainCleaner(sslSocketFactory);
      return this;
    }
    






























    public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager)
    {
      if (sslSocketFactory == null) throw new NullPointerException("sslSocketFactory == null");
      if (trustManager == null) throw new NullPointerException("trustManager == null");
      this.sslSocketFactory = sslSocketFactory;
      certificateChainCleaner = CertificateChainCleaner.get(trustManager);
      return this;
    }
    





    public Builder hostnameVerifier(HostnameVerifier hostnameVerifier)
    {
      if (hostnameVerifier == null) throw new NullPointerException("hostnameVerifier == null");
      this.hostnameVerifier = hostnameVerifier;
      return this;
    }
    




    public Builder certificatePinner(CertificatePinner certificatePinner)
    {
      if (certificatePinner == null) throw new NullPointerException("certificatePinner == null");
      this.certificatePinner = certificatePinner;
      return this;
    }
    





    public Builder authenticator(Authenticator authenticator)
    {
      if (authenticator == null) throw new NullPointerException("authenticator == null");
      this.authenticator = authenticator;
      return this;
    }
    





    public Builder proxyAuthenticator(Authenticator proxyAuthenticator)
    {
      if (proxyAuthenticator == null) throw new NullPointerException("proxyAuthenticator == null");
      this.proxyAuthenticator = proxyAuthenticator;
      return this;
    }
    




    public Builder connectionPool(ConnectionPool connectionPool)
    {
      if (connectionPool == null) throw new NullPointerException("connectionPool == null");
      this.connectionPool = connectionPool;
      return this;
    }
    





    public Builder followSslRedirects(boolean followProtocolRedirects)
    {
      followSslRedirects = followProtocolRedirects;
      return this;
    }
    
    public Builder followRedirects(boolean followRedirects)
    {
      this.followRedirects = followRedirects;
      return this;
    }
    

















    public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure)
    {
      this.retryOnConnectionFailure = retryOnConnectionFailure;
      return this;
    }
    


    public Builder dispatcher(Dispatcher dispatcher)
    {
      if (dispatcher == null) throw new IllegalArgumentException("dispatcher == null");
      this.dispatcher = dispatcher;
      return this;
    }
    


    Authenticator authenticator;
    

    ConnectionPool connectionPool;
    

    Dns dns;
    

    boolean followSslRedirects;
    

    boolean followRedirects;
    

    boolean retryOnConnectionFailure;
    

    int callTimeout;
    

    int connectTimeout;
    
    int readTimeout;
    
    int writeTimeout;
    
    int pingInterval;
    
    public Builder protocols(List<Protocol> protocols)
    {
      protocols = new ArrayList(protocols);
      

      if ((!protocols.contains(Protocol.H2_PRIOR_KNOWLEDGE)) && 
        (!protocols.contains(Protocol.HTTP_1_1))) {
        throw new IllegalArgumentException("protocols must contain h2_prior_knowledge or http/1.1: " + protocols);
      }
      
      if ((protocols.contains(Protocol.H2_PRIOR_KNOWLEDGE)) && (protocols.size() > 1)) {
        throw new IllegalArgumentException("protocols containing h2_prior_knowledge cannot use other protocols: " + protocols);
      }
      
      if (protocols.contains(Protocol.HTTP_1_0)) {
        throw new IllegalArgumentException("protocols must not contain http/1.0: " + protocols);
      }
      if (protocols.contains(null)) {
        throw new IllegalArgumentException("protocols must not contain null");
      }
      

      protocols.remove(Protocol.SPDY_3);
      

      this.protocols = Collections.unmodifiableList(protocols);
      return this;
    }
    
    public Builder connectionSpecs(List<ConnectionSpec> connectionSpecs) {
      this.connectionSpecs = Util.immutableList(connectionSpecs);
      return this;
    }
    




    public List<Interceptor> interceptors()
    {
      return interceptors;
    }
    
    public Builder addInterceptor(Interceptor interceptor) {
      if (interceptor == null) throw new IllegalArgumentException("interceptor == null");
      interceptors.add(interceptor);
      return this;
    }
    




    public List<Interceptor> networkInterceptors()
    {
      return networkInterceptors;
    }
    
    public Builder addNetworkInterceptor(Interceptor interceptor) {
      if (interceptor == null) throw new IllegalArgumentException("interceptor == null");
      networkInterceptors.add(interceptor);
      return this;
    }
    





    public Builder eventListener(EventListener eventListener)
    {
      if (eventListener == null) throw new NullPointerException("eventListener == null");
      eventListenerFactory = EventListener.factory(eventListener);
      return this;
    }
    





    public Builder eventListenerFactory(EventListener.Factory eventListenerFactory)
    {
      if (eventListenerFactory == null) {
        throw new NullPointerException("eventListenerFactory == null");
      }
      this.eventListenerFactory = eventListenerFactory;
      return this;
    }
    
    public OkHttpClient build() {
      return new OkHttpClient(this);
    }
  }
}
