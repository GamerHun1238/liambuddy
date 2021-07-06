package net.dv8tion.jda.api.events.http;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
























public class HttpRequestEvent
  extends Event
{
  private final net.dv8tion.jda.api.requests.Request<?> request;
  private final net.dv8tion.jda.api.requests.Response response;
  
  public HttpRequestEvent(@Nonnull net.dv8tion.jda.api.requests.Request<?> request, @Nonnull net.dv8tion.jda.api.requests.Response response)
  {
    super(request.getJDA());
    
    this.request = request;
    this.response = response;
  }
  
  @Nonnull
  public net.dv8tion.jda.api.requests.Request<?> getRequest()
  {
    return request;
  }
  
  @Nullable
  public RequestBody getRequestBody()
  {
    return request.getBody();
  }
  
  @Nullable
  public Object getRequestBodyRaw()
  {
    return request.getRawBody();
  }
  
  @Nullable
  public Headers getRequestHeaders()
  {
    return response.getRawResponse() == null ? null : response.getRawResponse().request().headers();
  }
  
  @Nullable
  public okhttp3.Request getRequestRaw()
  {
    return response.getRawResponse() == null ? null : response.getRawResponse().request();
  }
  
  @Nullable
  public net.dv8tion.jda.api.requests.Response getResponse()
  {
    return response;
  }
  
  @Nullable
  public ResponseBody getResponseBody()
  {
    return response.getRawResponse() == null ? null : response.getRawResponse().body();
  }
  
  @Nullable
  public DataArray getResponseBodyAsArray()
  {
    return response.getArray();
  }
  
  @Nullable
  public DataObject getResponseBodyAsObject()
  {
    return response.getObject();
  }
  
  @Nullable
  public String getResponseBodyAsString()
  {
    return response.getString();
  }
  
  @Nullable
  public Headers getResponseHeaders()
  {
    return response.getRawResponse() == null ? null : response.getRawResponse().headers();
  }
  
  @Nullable
  public okhttp3.Response getResponseRaw()
  {
    return response.getRawResponse();
  }
  
  @Nonnull
  public Set<String> getCFRays()
  {
    return response.getCFRays();
  }
  
  @Nonnull
  public RestAction<?> getRestAction()
  {
    return request.getRestAction();
  }
  
  @Nonnull
  public Route.CompiledRoute getRoute()
  {
    return request.getRoute();
  }
  
  public boolean isRateLimit()
  {
    return response.isRateLimit();
  }
}
