package okhttp3.internal.connection;

import java.io.IOException;
import okhttp3.internal.Util;


















public final class RouteException
  extends RuntimeException
{
  private IOException firstException;
  private IOException lastException;
  
  public RouteException(IOException cause)
  {
    super(cause);
    firstException = cause;
    lastException = cause;
  }
  
  public IOException getFirstConnectException() {
    return firstException;
  }
  
  public IOException getLastConnectException() {
    return lastException;
  }
  
  public void addConnectException(IOException e) {
    Util.addSuppressedIfPossible(firstException, e);
    lastException = e;
  }
}
