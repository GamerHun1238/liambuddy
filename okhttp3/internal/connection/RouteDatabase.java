package okhttp3.internal.connection;

import java.util.LinkedHashSet;
import java.util.Set;
import okhttp3.Route;





















public final class RouteDatabase
{
  private final Set<Route> failedRoutes = new LinkedHashSet();
  
  public RouteDatabase() {}
  
  public synchronized void failed(Route failedRoute) { failedRoutes.add(failedRoute); }
  

  public synchronized void connected(Route route)
  {
    failedRoutes.remove(route);
  }
  
  public synchronized boolean shouldPostpone(Route route)
  {
    return failedRoutes.contains(route);
  }
}
