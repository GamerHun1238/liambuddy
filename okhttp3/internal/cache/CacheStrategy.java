package okhttp3.internal.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;
































public final class CacheStrategy
{
  @Nullable
  public final Request networkRequest;
  @Nullable
  public final Response cacheResponse;
  
  CacheStrategy(Request networkRequest, Response cacheResponse)
  {
    this.networkRequest = networkRequest;
    this.cacheResponse = cacheResponse;
  }
  


  public static boolean isCacheable(Response response, Request request)
  {
    switch (response.code())
    {
    case 200: 
    case 203: 
    case 204: 
    case 300: 
    case 301: 
    case 308: 
    case 404: 
    case 405: 
    case 410: 
    case 414: 
    case 501: 
      break;
    



    case 302: 
    case 307: 
      if ((response.header("Expires") != null) || 
        (response.cacheControl().maxAgeSeconds() != -1) || 
        (response.cacheControl().isPublic()) || 
        (response.cacheControl().isPrivate())) {
        break;
      }
    


    default: 
      return false;
    }
    
    
    return (!response.cacheControl().noStore()) && (!request.cacheControl().noStore());
  }
  


  public static class Factory
  {
    final long nowMillis;
    

    final Request request;
    

    final Response cacheResponse;
    

    private Date servedDate;
    

    private String servedDateString;
    

    private Date lastModified;
    

    private String lastModifiedString;
    

    private Date expires;
    

    private long sentRequestMillis;
    

    private long receivedResponseMillis;
    
    private String etag;
    
    private int ageSeconds = -1;
    
    public Factory(long nowMillis, Request request, Response cacheResponse) {
      this.nowMillis = nowMillis;
      this.request = request;
      this.cacheResponse = cacheResponse;
      
      if (cacheResponse != null) {
        sentRequestMillis = cacheResponse.sentRequestAtMillis();
        receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
        Headers headers = cacheResponse.headers();
        int i = 0; for (int size = headers.size(); i < size; i++) {
          String fieldName = headers.name(i);
          String value = headers.value(i);
          if ("Date".equalsIgnoreCase(fieldName)) {
            servedDate = HttpDate.parse(value);
            servedDateString = value;
          } else if ("Expires".equalsIgnoreCase(fieldName)) {
            expires = HttpDate.parse(value);
          } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
            lastModified = HttpDate.parse(value);
            lastModifiedString = value;
          } else if ("ETag".equalsIgnoreCase(fieldName)) {
            etag = value;
          } else if ("Age".equalsIgnoreCase(fieldName)) {
            ageSeconds = HttpHeaders.parseSeconds(value, -1);
          }
        }
      }
    }
    


    public CacheStrategy get()
    {
      CacheStrategy candidate = getCandidate();
      
      if ((networkRequest != null) && (request.cacheControl().onlyIfCached()))
      {
        return new CacheStrategy(null, null);
      }
      
      return candidate;
    }
    

    private CacheStrategy getCandidate()
    {
      if (cacheResponse == null) {
        return new CacheStrategy(request, null);
      }
      

      if ((request.isHttps()) && (cacheResponse.handshake() == null)) {
        return new CacheStrategy(request, null);
      }
      



      if (!CacheStrategy.isCacheable(cacheResponse, request)) {
        return new CacheStrategy(request, null);
      }
      
      CacheControl requestCaching = request.cacheControl();
      if ((requestCaching.noCache()) || (hasConditions(request))) {
        return new CacheStrategy(request, null);
      }
      
      CacheControl responseCaching = cacheResponse.cacheControl();
      
      long ageMillis = cacheResponseAge();
      long freshMillis = computeFreshnessLifetime();
      
      if (requestCaching.maxAgeSeconds() != -1) {
        freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis(requestCaching.maxAgeSeconds()));
      }
      
      long minFreshMillis = 0L;
      if (requestCaching.minFreshSeconds() != -1) {
        minFreshMillis = TimeUnit.SECONDS.toMillis(requestCaching.minFreshSeconds());
      }
      
      long maxStaleMillis = 0L;
      if ((!responseCaching.mustRevalidate()) && (requestCaching.maxStaleSeconds() != -1)) {
        maxStaleMillis = TimeUnit.SECONDS.toMillis(requestCaching.maxStaleSeconds());
      }
      
      if ((!responseCaching.noCache()) && (ageMillis + minFreshMillis < freshMillis + maxStaleMillis)) {
        Response.Builder builder = cacheResponse.newBuilder();
        if (ageMillis + minFreshMillis >= freshMillis) {
          builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
        }
        long oneDayMillis = 86400000L;
        if ((ageMillis > oneDayMillis) && (isFreshnessLifetimeHeuristic())) {
          builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
        }
        return new CacheStrategy(null, builder.build());
      }
      

      String conditionValue;
      

      if (etag != null) {
        String conditionName = "If-None-Match";
        conditionValue = etag; } else { String conditionValue;
        if (lastModified != null) {
          String conditionName = "If-Modified-Since";
          conditionValue = lastModifiedString; } else { String conditionValue;
          if (servedDate != null) {
            String conditionName = "If-Modified-Since";
            conditionValue = servedDateString;
          } else {
            return new CacheStrategy(request, null); } } }
      String conditionValue;
      String conditionName;
      Headers.Builder conditionalRequestHeaders = request.headers().newBuilder();
      Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);
      


      Request conditionalRequest = request.newBuilder().headers(conditionalRequestHeaders.build()).build();
      return new CacheStrategy(conditionalRequest, cacheResponse);
    }
    



    private long computeFreshnessLifetime()
    {
      CacheControl responseCaching = cacheResponse.cacheControl();
      if (responseCaching.maxAgeSeconds() != -1)
        return TimeUnit.SECONDS.toMillis(responseCaching.maxAgeSeconds());
      if (expires != null)
      {

        long servedMillis = servedDate != null ? servedDate.getTime() : receivedResponseMillis;
        long delta = expires.getTime() - servedMillis;
        return delta > 0L ? delta : 0L; }
      if ((lastModified != null) && 
        (cacheResponse.request().url().query() == null))
      {





        long servedMillis = servedDate != null ? servedDate.getTime() : sentRequestMillis;
        long delta = servedMillis - lastModified.getTime();
        return delta > 0L ? delta / 10L : 0L;
      }
      return 0L;
    }
    





    private long cacheResponseAge()
    {
      long apparentReceivedAge = servedDate != null ? Math.max(0L, receivedResponseMillis - servedDate.getTime()) : 0L;
      

      long receivedAge = ageSeconds != -1 ? Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis(ageSeconds)) : apparentReceivedAge;
      long responseDuration = receivedResponseMillis - sentRequestMillis;
      long residentDuration = nowMillis - receivedResponseMillis;
      return receivedAge + responseDuration + residentDuration;
    }
    



    private boolean isFreshnessLifetimeHeuristic()
    {
      return (cacheResponse.cacheControl().maxAgeSeconds() == -1) && (expires == null);
    }
    




    private static boolean hasConditions(Request request)
    {
      return (request.header("If-Modified-Since") != null) || (request.header("If-None-Match") != null);
    }
  }
}
