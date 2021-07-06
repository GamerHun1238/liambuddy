package okhttp3;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpMethod;

















public final class Request
{
  final HttpUrl url;
  final String method;
  final Headers headers;
  @Nullable
  final RequestBody body;
  final Map<Class<?>, Object> tags;
  @Nullable
  private volatile CacheControl cacheControl;
  
  Request(Builder builder)
  {
    url = url;
    method = method;
    headers = headers.build();
    body = body;
    tags = Util.immutableMap(tags);
  }
  
  public HttpUrl url() {
    return url;
  }
  
  public String method() {
    return method;
  }
  
  public Headers headers() {
    return headers;
  }
  
  @Nullable
  public String header(String name) { return headers.get(name); }
  
  public List<String> headers(String name)
  {
    return headers.values(name);
  }
  
  @Nullable
  public RequestBody body() { return body; }
  







  @Nullable
  public Object tag()
  {
    return tag(Object.class);
  }
  


  @Nullable
  public <T> T tag(Class<? extends T> type)
  {
    return type.cast(tags.get(type));
  }
  
  public Builder newBuilder() {
    return new Builder(this);
  }
  



  public CacheControl cacheControl()
  {
    CacheControl result = cacheControl;
    return this.cacheControl = CacheControl.parse(headers);
  }
  
  public boolean isHttps() {
    return url.isHttps();
  }
  
  public String toString() {
    return "Request{method=" + method + ", url=" + url + ", tags=" + tags + '}';
  }
  

  public static class Builder
  {
    @Nullable
    HttpUrl url;
    
    String method;
    
    Headers.Builder headers;
    
    @Nullable
    RequestBody body;
    
    Map<Class<?>, Object> tags = Collections.emptyMap();
    
    public Builder() {
      method = "GET";
      headers = new Headers.Builder();
    }
    
    Builder(Request request) {
      url = url;
      method = method;
      body = body;
      

      tags = (tags.isEmpty() ? Collections.emptyMap() : new LinkedHashMap(tags));
      headers = headers.newBuilder();
    }
    
    public Builder url(HttpUrl url) {
      if (url == null) throw new NullPointerException("url == null");
      this.url = url;
      return this;
    }
    





    public Builder url(String url)
    {
      if (url == null) { throw new NullPointerException("url == null");
      }
      
      if (url.regionMatches(true, 0, "ws:", 0, 3)) {
        url = "http:" + url.substring(3);
      } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
        url = "https:" + url.substring(4);
      }
      
      return url(HttpUrl.get(url));
    }
    





    public Builder url(URL url)
    {
      if (url == null) throw new NullPointerException("url == null");
      return url(HttpUrl.get(url.toString()));
    }
    



    public Builder header(String name, String value)
    {
      headers.set(name, value);
      return this;
    }
    






    public Builder addHeader(String name, String value)
    {
      headers.add(name, value);
      return this;
    }
    
    public Builder removeHeader(String name)
    {
      headers.removeAll(name);
      return this;
    }
    
    public Builder headers(Headers headers)
    {
      this.headers = headers.newBuilder();
      return this;
    }
    




    public Builder cacheControl(CacheControl cacheControl)
    {
      String value = cacheControl.toString();
      if (value.isEmpty()) return removeHeader("Cache-Control");
      return header("Cache-Control", value);
    }
    
    public Builder get() {
      return method("GET", null);
    }
    
    public Builder head() {
      return method("HEAD", null);
    }
    
    public Builder post(RequestBody body) {
      return method("POST", body);
    }
    
    public Builder delete(@Nullable RequestBody body) {
      return method("DELETE", body);
    }
    
    public Builder delete() {
      return delete(Util.EMPTY_REQUEST);
    }
    
    public Builder put(RequestBody body) {
      return method("PUT", body);
    }
    
    public Builder patch(RequestBody body) {
      return method("PATCH", body);
    }
    
    public Builder method(String method, @Nullable RequestBody body) {
      if (method == null) throw new NullPointerException("method == null");
      if (method.length() == 0) throw new IllegalArgumentException("method.length() == 0");
      if ((body != null) && (!HttpMethod.permitsRequestBody(method))) {
        throw new IllegalArgumentException("method " + method + " must not have a request body.");
      }
      if ((body == null) && (HttpMethod.requiresRequestBody(method))) {
        throw new IllegalArgumentException("method " + method + " must have a request body.");
      }
      this.method = method;
      this.body = body;
      return this;
    }
    
    public Builder tag(@Nullable Object tag)
    {
      return tag(Object.class, tag);
    }
    







    public <T> Builder tag(Class<? super T> type, @Nullable T tag)
    {
      if (type == null) { throw new NullPointerException("type == null");
      }
      if (tag == null) {
        tags.remove(type);
      } else {
        if (tags.isEmpty()) tags = new LinkedHashMap();
        tags.put(type, type.cast(tag));
      }
      
      return this;
    }
    
    public Request build() {
      if (url == null) throw new IllegalStateException("url == null");
      return new Request(this);
    }
  }
}
