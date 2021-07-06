package okhttp3;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;
import okio.AsyncTimeout;
import okio.Timeout;























final class RealCall
  implements Call
{
  final OkHttpClient client;
  final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;
  final AsyncTimeout timeout;
  @Nullable
  private EventListener eventListener;
  final Request originalRequest;
  final boolean forWebSocket;
  private boolean executed;
  
  private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket)
  {
    this.client = client;
    this.originalRequest = originalRequest;
    this.forWebSocket = forWebSocket;
    retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client);
    timeout = new AsyncTimeout() {
      protected void timedOut() {
        cancel();
      }
    };
    timeout.timeout(client.callTimeoutMillis(), TimeUnit.MILLISECONDS);
  }
  
  static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket)
  {
    RealCall call = new RealCall(client, originalRequest, forWebSocket);
    eventListener = client.eventListenerFactory().create(call);
    return call;
  }
  
  public Request request() {
    return originalRequest;
  }
  
  public Response execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    timeout.enter();
    eventListener.callStart(this);
    try {
      client.dispatcher().executed(this);
      Response result = getResponseWithInterceptorChain();
      if (result == null) throw new IOException("Canceled");
      return result;
    } catch (IOException e) {
      e = timeoutExit(e);
      eventListener.callFailed(this, e);
      throw e;
    } finally {
      client.dispatcher().finished(this);
    }
  }
  
  @Nullable
  IOException timeoutExit(@Nullable IOException cause) { if (!timeout.exit()) { return cause;
    }
    InterruptedIOException e = new InterruptedIOException("timeout");
    if (cause != null) {
      e.initCause(cause);
    }
    return e;
  }
  
  private void captureCallStackTrace() {
    Object callStackTrace = Platform.get().getStackTraceForCloseable("response.body().close()");
    retryAndFollowUpInterceptor.setCallStackTrace(callStackTrace);
  }
  
  public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    eventListener.callStart(this);
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }
  
  public void cancel() {
    retryAndFollowUpInterceptor.cancel();
  }
  
  public Timeout timeout() {
    return timeout;
  }
  
  public synchronized boolean isExecuted() {
    return executed;
  }
  
  public boolean isCanceled() {
    return retryAndFollowUpInterceptor.isCanceled();
  }
  
  public RealCall clone()
  {
    return newRealCall(client, originalRequest, forWebSocket);
  }
  
  StreamAllocation streamAllocation() {
    return retryAndFollowUpInterceptor.streamAllocation();
  }
  
  final class AsyncCall extends NamedRunnable {
    private final Callback responseCallback;
    private volatile AtomicInteger callsPerHost = new AtomicInteger(0);
    
    AsyncCall(Callback responseCallback) {
      super(new Object[] { redactedUrl() });
      this.responseCallback = responseCallback;
    }
    
    AtomicInteger callsPerHost() {
      return callsPerHost;
    }
    
    void reuseCallsPerHostFrom(AsyncCall other) {
      callsPerHost = callsPerHost;
    }
    
    String host() {
      return originalRequest.url().host();
    }
    
    Request request() {
      return originalRequest;
    }
    
    RealCall get() {
      return RealCall.this;
    }
    



    void executeOn(ExecutorService executorService)
    {
      assert (!Thread.holdsLock(client.dispatcher()));
      boolean success = false;
      try {
        executorService.execute(this);
        success = true;
      } catch (RejectedExecutionException e) {
        InterruptedIOException ioException = new InterruptedIOException("executor rejected");
        ioException.initCause(e);
        eventListener.callFailed(RealCall.this, ioException);
        responseCallback.onFailure(RealCall.this, ioException);
      } finally {
        if (!success) {
          client.dispatcher().finished(this);
        }
      }
    }
    
    protected void execute() {
      boolean signalledCallback = false;
      timeout.enter();
      try {
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        e = timeoutExit(e);
        if (signalledCallback)
        {
          Platform.get().log(4, "Callback failure for " + toLoggableString(), e);
        } else {
          eventListener.callFailed(RealCall.this, e);
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
  }
  



  String toLoggableString()
  {
    return 
    
      (isCanceled() ? "canceled " : "") + (forWebSocket ? "web socket" : "call") + " to " + redactedUrl();
  }
  
  String redactedUrl() {
    return originalRequest.url().redact();
  }
  
  Response getResponseWithInterceptorChain() throws IOException
  {
    List<Interceptor> interceptors = new ArrayList();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));
    


    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0, originalRequest, this, eventListener, client.connectTimeoutMillis(), client.readTimeoutMillis(), client.writeTimeoutMillis());
    
    return chain.proceed(originalRequest);
  }
}
