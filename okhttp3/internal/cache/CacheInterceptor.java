package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;















public final class CacheInterceptor
  implements Interceptor
{
  @Nullable
  final InternalCache cache;
  
  public CacheInterceptor(@Nullable InternalCache cache)
  {
    this.cache = cache;
  }
  
  public Response intercept(Interceptor.Chain chain)
    throws IOException
  {
    Response cacheCandidate = cache != null ? cache.get(chain.request()) : null;
    
    long now = System.currentTimeMillis();
    
    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();
    Request networkRequest = networkRequest;
    Response cacheResponse = cacheResponse;
    
    if (cache != null) {
      cache.trackResponse(strategy);
    }
    
    if ((cacheCandidate != null) && (cacheResponse == null)) {
      Util.closeQuietly(cacheCandidate.body());
    }
    

    if ((networkRequest == null) && (cacheResponse == null)) {
      return 
      






        new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(System.currentTimeMillis()).build();
    }
    

    if (networkRequest == null) {
      return 
      
        cacheResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).build();
    }
    
    Response networkResponse = null;
    try {
      networkResponse = chain.proceed(networkRequest);
    }
    finally {
      if ((networkResponse == null) && (cacheCandidate != null)) {
        Util.closeQuietly(cacheCandidate.body());
      }
    }
    

    if (cacheResponse != null) {
      if (networkResponse.code() == 304)
      {





        Response response = cacheResponse.newBuilder().headers(combine(cacheResponse.headers(), networkResponse.headers())).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
        networkResponse.body().close();
        


        cache.trackConditionalCacheHit();
        cache.update(cacheResponse, response);
        return response;
      }
      Util.closeQuietly(cacheResponse.body());
    }
    




    Response response = networkResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
    
    if (cache != null) {
      if ((HttpHeaders.hasBody(response)) && (CacheStrategy.isCacheable(response, networkRequest)))
      {
        CacheRequest cacheRequest = cache.put(response);
        return cacheWritingResponse(cacheRequest, response);
      }
      
      if (HttpMethod.invalidatesCache(networkRequest.method())) {
        try {
          cache.remove(networkRequest);
        }
        catch (IOException localIOException) {}
      }
    }
    

    return response;
  }
  
  private static Response stripBody(Response response) {
    return (response != null) && (response.body() != null) ? 
      response.newBuilder().body(null).build() : 
      response;
  }
  





  private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response)
    throws IOException
  {
    if (cacheRequest == null) return response;
    Sink cacheBodyUnbuffered = cacheRequest.body();
    if (cacheBodyUnbuffered == null) { return response;
    }
    final BufferedSource source = response.body().source();
    final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);
    
    Source cacheWritingSource = new Source()
    {
      boolean cacheRequestClosed;
      
      public long read(Buffer sink, long byteCount) throws IOException {
        try {
          bytesRead = source.read(sink, byteCount);
        } catch (IOException e) { long bytesRead;
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheRequest.abort();
          }
          throw e;
        }
        long bytesRead;
        if (bytesRead == -1L) {
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheBody.close();
          }
          return -1L;
        }
        
        sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
        cacheBody.emitCompleteSegments();
        return bytesRead;
      }
      
      public Timeout timeout() {
        return source.timeout();
      }
      
      public void close() throws IOException {
        if ((!cacheRequestClosed) && 
          (!Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
          cacheRequestClosed = true;
          cacheRequest.abort();
        }
        source.close();
      }
      
    };
    String contentType = response.header("Content-Type");
    long contentLength = response.body().contentLength();
    return response.newBuilder()
      .body(new RealResponseBody(contentType, contentLength, Okio.buffer(cacheWritingSource)))
      .build();
  }
  
  private static Headers combine(Headers cachedHeaders, Headers networkHeaders)
  {
    Headers.Builder result = new Headers.Builder();
    
    int i = 0; for (int size = cachedHeaders.size(); i < size; i++) {
      String fieldName = cachedHeaders.name(i);
      String value = cachedHeaders.value(i);
      if ((!"Warning".equalsIgnoreCase(fieldName)) || (!value.startsWith("1")))
      {

        if ((isContentSpecificHeader(fieldName)) || 
          (!isEndToEnd(fieldName)) || 
          (networkHeaders.get(fieldName) == null)) {
          Internal.instance.addLenient(result, fieldName, value);
        }
      }
    }
    int i = 0; for (int size = networkHeaders.size(); i < size; i++) {
      String fieldName = networkHeaders.name(i);
      if ((!isContentSpecificHeader(fieldName)) && (isEndToEnd(fieldName))) {
        Internal.instance.addLenient(result, fieldName, networkHeaders.value(i));
      }
    }
    
    return result.build();
  }
  



  static boolean isEndToEnd(String fieldName)
  {
    return (!"Connection".equalsIgnoreCase(fieldName)) && 
      (!"Keep-Alive".equalsIgnoreCase(fieldName)) && 
      (!"Proxy-Authenticate".equalsIgnoreCase(fieldName)) && 
      (!"Proxy-Authorization".equalsIgnoreCase(fieldName)) && 
      (!"TE".equalsIgnoreCase(fieldName)) && 
      (!"Trailers".equalsIgnoreCase(fieldName)) && 
      (!"Transfer-Encoding".equalsIgnoreCase(fieldName)) && 
      (!"Upgrade".equalsIgnoreCase(fieldName));
  }
  



  static boolean isContentSpecificHeader(String fieldName)
  {
    return ("Content-Length".equalsIgnoreCase(fieldName)) || 
      ("Content-Encoding".equalsIgnoreCase(fieldName)) || 
      ("Content-Type".equalsIgnoreCase(fieldName));
  }
}
