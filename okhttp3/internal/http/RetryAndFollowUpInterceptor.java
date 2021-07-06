package okhttp3.internal.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpRetryException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteException;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http2.ConnectionShutdownException;




























public final class RetryAndFollowUpInterceptor
  implements Interceptor
{
  private static final int MAX_FOLLOW_UPS = 20;
  private final OkHttpClient client;
  private volatile StreamAllocation streamAllocation;
  private Object callStackTrace;
  private volatile boolean canceled;
  
  public RetryAndFollowUpInterceptor(OkHttpClient client)
  {
    this.client = client;
  }
  








  public void cancel()
  {
    canceled = true;
    StreamAllocation streamAllocation = this.streamAllocation;
    if (streamAllocation != null) streamAllocation.cancel();
  }
  
  public boolean isCanceled() {
    return canceled;
  }
  
  public void setCallStackTrace(Object callStackTrace) {
    this.callStackTrace = callStackTrace;
  }
  
  public StreamAllocation streamAllocation() {
    return streamAllocation;
  }
  
  public Response intercept(Interceptor.Chain chain) throws IOException {
    Request request = chain.request();
    RealInterceptorChain realChain = (RealInterceptorChain)chain;
    Call call = realChain.call();
    EventListener eventListener = realChain.eventListener();
    

    StreamAllocation streamAllocation = new StreamAllocation(client.connectionPool(), createAddress(request.url()), call, eventListener, callStackTrace);
    this.streamAllocation = streamAllocation;
    
    int followUpCount = 0;
    Response priorResponse = null;
    for (;;) {
      if (canceled) {
        streamAllocation.release(true);
        throw new IOException("Canceled");
      }
      

      boolean releaseConnection = true;
      try {
        Response response = realChain.proceed(request, streamAllocation, null, null);
        releaseConnection = false;
      }
      catch (RouteException e) {
        if (!recover(e.getLastConnectException(), streamAllocation, false, request)) {
          throw e.getFirstConnectException();
        }
        releaseConnection = false;
        








        if (!releaseConnection) continue;
        streamAllocation.streamFailed(null);
        streamAllocation.release(true); continue;
      }
      catch (IOException e)
      {
        boolean requestSendStarted = !(e instanceof ConnectionShutdownException);
        if (!recover(e, streamAllocation, requestSendStarted, request)) throw e;
        releaseConnection = false;
        


        if (!releaseConnection) continue;
        streamAllocation.streamFailed(null);
        streamAllocation.release(true); continue;
      }
      finally
      {
        if (releaseConnection) {
          streamAllocation.streamFailed(null);
          streamAllocation.release(true);
        }
      }
      
      Response response;
      if (priorResponse != null)
      {



        response = response.newBuilder().priorResponse(priorResponse.newBuilder().body(null).build()).build();
      }
      
      try
      {
        followUp = followUpRequest(response, streamAllocation.route());
      } catch (IOException e) { Request followUp;
        streamAllocation.release(true);
        throw e;
      }
      Request followUp;
      if (followUp == null) {
        streamAllocation.release(true);
        return response;
      }
      
      Util.closeQuietly(response.body());
      
      followUpCount++; if (followUpCount > 20) {
        streamAllocation.release(true);
        throw new ProtocolException("Too many follow-up requests: " + followUpCount);
      }
      
      if ((followUp.body() instanceof UnrepeatableRequestBody)) {
        streamAllocation.release(true);
        throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
      }
      
      if (!sameConnection(response, followUp.url())) {
        streamAllocation.release(false);
        
        streamAllocation = new StreamAllocation(client.connectionPool(), createAddress(followUp.url()), call, eventListener, callStackTrace);
        this.streamAllocation = streamAllocation;
      } else if (streamAllocation.codec() != null) {
        throw new IllegalStateException("Closing the body of " + response + " didn't close its backing stream. Bad interceptor?");
      }
      

      request = followUp;
      priorResponse = response;
    }
  }
  
  private Address createAddress(HttpUrl url) {
    SSLSocketFactory sslSocketFactory = null;
    HostnameVerifier hostnameVerifier = null;
    CertificatePinner certificatePinner = null;
    if (url.isHttps()) {
      sslSocketFactory = client.sslSocketFactory();
      hostnameVerifier = client.hostnameVerifier();
      certificatePinner = client.certificatePinner();
    }
    
    return new Address(url.host(), url.port(), client.dns(), client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, client
      .proxyAuthenticator(), client
      .proxy(), client.protocols(), client.connectionSpecs(), client.proxySelector());
  }
  






  private boolean recover(IOException e, StreamAllocation streamAllocation, boolean requestSendStarted, Request userRequest)
  {
    streamAllocation.streamFailed(e);
    

    if (!client.retryOnConnectionFailure()) { return false;
    }
    
    if ((requestSendStarted) && (requestIsUnrepeatable(e, userRequest))) { return false;
    }
    
    if (!isRecoverable(e, requestSendStarted)) { return false;
    }
    
    if (!streamAllocation.hasMoreRoutes()) { return false;
    }
    
    return true;
  }
  
  private boolean requestIsUnrepeatable(IOException e, Request userRequest) {
    return ((userRequest.body() instanceof UnrepeatableRequestBody)) || ((e instanceof FileNotFoundException));
  }
  

  private boolean isRecoverable(IOException e, boolean requestSendStarted)
  {
    if ((e instanceof ProtocolException)) {
      return false;
    }
    


    if ((e instanceof InterruptedIOException)) {
      return ((e instanceof SocketTimeoutException)) && (!requestSendStarted);
    }
    


    if ((e instanceof SSLHandshakeException))
    {

      if ((e.getCause() instanceof CertificateException)) {
        return false;
      }
    }
    if ((e instanceof SSLPeerUnverifiedException))
    {
      return false;
    }
    



    return true;
  }
  



  private Request followUpRequest(Response userResponse, Route route)
    throws IOException
  {
    if (userResponse == null) throw new IllegalStateException();
    int responseCode = userResponse.code();
    
    String method = userResponse.request().method();
    switch (responseCode)
    {

    case 407: 
      Proxy selectedProxy = route != null ? route.proxy() : client.proxy();
      if (selectedProxy.type() != Proxy.Type.HTTP) {
        throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
      }
      return client.proxyAuthenticator().authenticate(route, userResponse);
    
    case 401: 
      return client.authenticator().authenticate(route, userResponse);
    


    case 307: 
    case 308: 
      if ((!method.equals("GET")) && (!method.equals("HEAD"))) {
        return null;
      }
    

    case 300: 
    case 301: 
    case 302: 
    case 303: 
      if (!client.followRedirects()) { return null;
      }
      String location = userResponse.header("Location");
      if (location == null) return null;
      HttpUrl url = userResponse.request().url().resolve(location);
      

      if (url == null) { return null;
      }
      
      boolean sameScheme = url.scheme().equals(userResponse.request().url().scheme());
      if ((!sameScheme) && (!client.followSslRedirects())) { return null;
      }
      
      Request.Builder requestBuilder = userResponse.request().newBuilder();
      if (HttpMethod.permitsRequestBody(method)) {
        boolean maintainBody = HttpMethod.redirectsWithBody(method);
        if (HttpMethod.redirectsToGet(method)) {
          requestBuilder.method("GET", null);
        } else {
          RequestBody requestBody = maintainBody ? userResponse.request().body() : null;
          requestBuilder.method(method, requestBody);
        }
        if (!maintainBody) {
          requestBuilder.removeHeader("Transfer-Encoding");
          requestBuilder.removeHeader("Content-Length");
          requestBuilder.removeHeader("Content-Type");
        }
      }
      



      if (!sameConnection(userResponse, url)) {
        requestBuilder.removeHeader("Authorization");
      }
      
      return requestBuilder.url(url).build();
    



    case 408: 
      if (!client.retryOnConnectionFailure())
      {
        return null;
      }
      
      if ((userResponse.request().body() instanceof UnrepeatableRequestBody)) {
        return null;
      }
      
      if ((userResponse.priorResponse() != null) && 
        (userResponse.priorResponse().code() == 408))
      {
        return null;
      }
      
      if (retryAfter(userResponse, 0) > 0) {
        return null;
      }
      
      return userResponse.request();
    
    case 503: 
      if ((userResponse.priorResponse() != null) && 
        (userResponse.priorResponse().code() == 503))
      {
        return null;
      }
      
      if (retryAfter(userResponse, Integer.MAX_VALUE) == 0)
      {
        return userResponse.request();
      }
      
      return null;
    }
    
    return null;
  }
  
  private int retryAfter(Response userResponse, int defaultDelay)
  {
    String header = userResponse.header("Retry-After");
    
    if (header == null) {
      return defaultDelay;
    }
    


    if (header.matches("\\d+")) {
      return Integer.valueOf(header).intValue();
    }
    
    return Integer.MAX_VALUE;
  }
  



  private boolean sameConnection(Response response, HttpUrl followUp)
  {
    HttpUrl url = response.request().url();
    return (url.host().equals(followUp.host())) && 
      (url.port() == followUp.port()) && 
      (url.scheme().equals(followUp.scheme()));
  }
}
