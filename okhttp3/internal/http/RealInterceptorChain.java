package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;



















public final class RealInterceptorChain
  implements Interceptor.Chain
{
  private final List<Interceptor> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  private final int index;
  private final Request request;
  private final Call call;
  private final EventListener eventListener;
  private final int connectTimeout;
  private final int readTimeout;
  private final int writeTimeout;
  private int calls;
  
  public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation, HttpCodec httpCodec, RealConnection connection, int index, Request request, Call call, EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout)
  {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.request = request;
    this.call = call;
    this.eventListener = eventListener;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
  }
  
  public Connection connection() {
    return connection;
  }
  
  public int connectTimeoutMillis() {
    return connectTimeout;
  }
  
  public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
    int millis = Util.checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index, request, call, eventListener, millis, readTimeout, writeTimeout);
  }
  
  public int readTimeoutMillis()
  {
    return readTimeout;
  }
  
  public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
    int millis = Util.checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index, request, call, eventListener, connectTimeout, millis, writeTimeout);
  }
  
  public int writeTimeoutMillis()
  {
    return writeTimeout;
  }
  
  public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
    int millis = Util.checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index, request, call, eventListener, connectTimeout, readTimeout, millis);
  }
  
  public StreamAllocation streamAllocation()
  {
    return streamAllocation;
  }
  
  public HttpCodec httpStream() {
    return httpCodec;
  }
  
  public Call call() {
    return call;
  }
  
  public EventListener eventListener() {
    return eventListener;
  }
  
  public Request request() {
    return request;
  }
  
  public Response proceed(Request request) throws IOException {
    return proceed(request, streamAllocation, httpCodec, connection);
  }
  
  public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec, RealConnection connection) throws IOException
  {
    if (index >= interceptors.size()) { throw new AssertionError();
    }
    calls += 1;
    

    if ((this.httpCodec != null) && (!this.connection.supportsUrl(request.url()))) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1) + " must retain the same host and port");
    }
    


    if ((this.httpCodec != null) && (calls > 1)) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1) + " must call proceed() exactly once");
    }
    


    RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index + 1, request, call, eventListener, connectTimeout, readTimeout, writeTimeout);
    

    Interceptor interceptor = (Interceptor)interceptors.get(index);
    Response response = interceptor.intercept(next);
    

    if ((httpCodec != null) && (index + 1 < interceptors.size()) && (calls != 1)) {
      throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
    }
    


    if (response == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }
    
    if (response.body() == null) {
      throw new IllegalStateException("interceptor " + interceptor + " returned a response with no body");
    }
    

    return response;
  }
}
