package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;























public final class Response
  implements Closeable
{
  final Request request;
  final Protocol protocol;
  final int code;
  final String message;
  @Nullable
  final Handshake handshake;
  final Headers headers;
  @Nullable
  final ResponseBody body;
  @Nullable
  final Response networkResponse;
  @Nullable
  final Response cacheResponse;
  @Nullable
  final Response priorResponse;
  final long sentRequestAtMillis;
  final long receivedResponseAtMillis;
  @Nullable
  final HttpCodec httpCodec;
  @Nullable
  private volatile CacheControl cacheControl;
  
  Response(Builder builder)
  {
    request = request;
    protocol = protocol;
    code = code;
    message = message;
    handshake = handshake;
    headers = headers.build();
    body = body;
    networkResponse = networkResponse;
    cacheResponse = cacheResponse;
    priorResponse = priorResponse;
    sentRequestAtMillis = sentRequestAtMillis;
    receivedResponseAtMillis = receivedResponseAtMillis;
    httpCodec = httpCodec;
  }
  










  public Request request()
  {
    return request;
  }
  


  public Protocol protocol()
  {
    return protocol;
  }
  
  public int code()
  {
    return code;
  }
  



  public boolean isSuccessful()
  {
    return (code >= 200) && (code < 300);
  }
  
  public String message()
  {
    return message;
  }
  


  @Nullable
  public Handshake handshake()
  {
    return handshake;
  }
  
  public List<String> headers(String name) {
    return headers.values(name);
  }
  
  @Nullable
  public String header(String name) { return header(name, null); }
  
  @Nullable
  public String header(String name, @Nullable String defaultValue) {
    String result = headers.get(name);
    return result != null ? result : defaultValue;
  }
  
  public Headers headers() {
    return headers;
  }
  


  public Headers trailers()
    throws IOException
  {
    return httpCodec.trailers();
  }
  









  public ResponseBody peekBody(long byteCount)
    throws IOException
  {
    BufferedSource peeked = body.source().peek();
    Buffer buffer = new Buffer();
    peeked.request(byteCount);
    buffer.write(peeked, Math.min(byteCount, peeked.getBuffer().size()));
    return ResponseBody.create(body.contentType(), buffer.size(), buffer);
  }
  






  @Nullable
  public ResponseBody body()
  {
    return body;
  }
  
  public Builder newBuilder() {
    return new Builder(this);
  }
  
  public boolean isRedirect()
  {
    switch (code) {
    case 300: 
    case 301: 
    case 302: 
    case 303: 
    case 307: 
    case 308: 
      return true;
    }
    return false;
  }
  




  @Nullable
  public Response networkResponse()
  {
    return networkResponse;
  }
  



  @Nullable
  public Response cacheResponse()
  {
    return cacheResponse;
  }
  




  @Nullable
  public Response priorResponse()
  {
    return priorResponse;
  }
  





  public List<Challenge> challenges()
  {
    String responseField;
    




    if (code == 401) {
      responseField = "WWW-Authenticate"; } else { String responseField;
      if (code == 407) {
        responseField = "Proxy-Authenticate";
      } else
        return Collections.emptyList(); }
    String responseField;
    return HttpHeaders.parseChallenges(headers(), responseField);
  }
  



  public CacheControl cacheControl()
  {
    CacheControl result = cacheControl;
    return this.cacheControl = CacheControl.parse(headers);
  }
  




  public long sentRequestAtMillis()
  {
    return sentRequestAtMillis;
  }
  




  public long receivedResponseAtMillis()
  {
    return receivedResponseAtMillis;
  }
  






  public void close()
  {
    if (body == null) {
      throw new IllegalStateException("response is not eligible for a body and must not be closed");
    }
    body.close();
  }
  
  public String toString() {
    return 
    





      "Response{protocol=" + protocol + ", code=" + code + ", message=" + message + ", url=" + request.url() + '}';
  }
  
  public static class Builder { @Nullable
    Request request;
    @Nullable
    Protocol protocol;
    int code = -1;
    
    String message;
    @Nullable
    Handshake handshake;
    Headers.Builder headers;
    @Nullable
    ResponseBody body;
    @Nullable
    Response networkResponse;
    
    public Builder()
    {
      headers = new Headers.Builder();
    }
    
    Builder(Response response) {
      request = request;
      protocol = protocol;
      code = code;
      message = message;
      handshake = handshake;
      headers = headers.newBuilder();
      body = body;
      networkResponse = networkResponse;
      cacheResponse = cacheResponse;
      priorResponse = priorResponse;
      sentRequestAtMillis = sentRequestAtMillis;
      receivedResponseAtMillis = receivedResponseAtMillis;
      httpCodec = httpCodec;
    }
    
    public Builder request(Request request) {
      this.request = request;
      return this;
    }
    
    public Builder protocol(Protocol protocol) {
      this.protocol = protocol;
      return this;
    }
    
    public Builder code(int code) {
      this.code = code;
      return this;
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }
    
    public Builder handshake(@Nullable Handshake handshake) {
      this.handshake = handshake;
      return this;
    }
    
    @Nullable
    Response cacheResponse;
    @Nullable
    Response priorResponse;
    
    public Builder header(String name, String value) { headers.set(name, value);
      return this;
    }
    
    long sentRequestAtMillis;
    long receivedResponseAtMillis;
    @Nullable
    HttpCodec httpCodec;
    public Builder addHeader(String name, String value) {
      headers.add(name, value);
      return this;
    }
    
    public Builder removeHeader(String name) {
      headers.removeAll(name);
      return this;
    }
    
    public Builder headers(Headers headers)
    {
      this.headers = headers.newBuilder();
      return this;
    }
    
    public Builder body(@Nullable ResponseBody body) {
      this.body = body;
      return this;
    }
    
    public Builder networkResponse(@Nullable Response networkResponse) {
      if (networkResponse != null) checkSupportResponse("networkResponse", networkResponse);
      this.networkResponse = networkResponse;
      return this;
    }
    
    public Builder cacheResponse(@Nullable Response cacheResponse) {
      if (cacheResponse != null) checkSupportResponse("cacheResponse", cacheResponse);
      this.cacheResponse = cacheResponse;
      return this;
    }
    
    private void checkSupportResponse(String name, Response response) {
      if (body != null)
        throw new IllegalArgumentException(name + ".body != null");
      if (networkResponse != null)
        throw new IllegalArgumentException(name + ".networkResponse != null");
      if (cacheResponse != null)
        throw new IllegalArgumentException(name + ".cacheResponse != null");
      if (priorResponse != null) {
        throw new IllegalArgumentException(name + ".priorResponse != null");
      }
    }
    
    public Builder priorResponse(@Nullable Response priorResponse) {
      if (priorResponse != null) checkPriorResponse(priorResponse);
      this.priorResponse = priorResponse;
      return this;
    }
    
    private void checkPriorResponse(Response response) {
      if (body != null) {
        throw new IllegalArgumentException("priorResponse.body != null");
      }
    }
    
    public Builder sentRequestAtMillis(long sentRequestAtMillis) {
      this.sentRequestAtMillis = sentRequestAtMillis;
      return this;
    }
    
    public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
      this.receivedResponseAtMillis = receivedResponseAtMillis;
      return this;
    }
    
    void initCodec(HttpCodec httpCodec) {
      this.httpCodec = httpCodec;
    }
    
    public Response build() {
      if (request == null) throw new IllegalStateException("request == null");
      if (protocol == null) throw new IllegalStateException("protocol == null");
      if (code < 0) throw new IllegalStateException("code < 0: " + code);
      if (message == null) throw new IllegalStateException("message == null");
      return new Response(this);
    }
  }
}
