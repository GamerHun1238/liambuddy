package net.dv8tion.jda.internal.requests;

import java.util.Iterator;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.internal.utils.JDALogger;
import okhttp3.Response;
import org.slf4j.Logger;



















public abstract class RateLimiter
{
  protected static final Logger log = JDALogger.getLog(RateLimiter.class);
  protected final Requester requester;
  protected volatile boolean isShutdown = false; protected volatile boolean isStopped = false;
  
  protected RateLimiter(Requester requester)
  {
    this.requester = requester;
  }
  
  protected boolean isSkipped(Iterator<Request> it, Request request)
  {
    if (request.isSkipped())
    {
      cancel(it, request);
      return true;
    }
    return false;
  }
  
  private void cancel(Iterator<Request> it, Request request)
  {
    request.onCancelled();
    it.remove();
  }
  

  public abstract Long getRateLimit(Route.CompiledRoute paramCompiledRoute);
  

  protected abstract void queueRequest(Request paramRequest);
  
  protected abstract Long handleResponse(Route.CompiledRoute paramCompiledRoute, Response paramResponse);
  
  public boolean isRateLimited(Route.CompiledRoute route)
  {
    Long rateLimit = getRateLimit(route);
    return (rateLimit != null) && (rateLimit.longValue() > 0L);
  }
  

  public abstract int cancelRequests();
  
  public void init() {}
  
  protected boolean stop()
  {
    isStopped = true;
    return true;
  }
  
  protected void shutdown()
  {
    isShutdown = true;
    stop();
  }
}
