package okhttp3;

import java.io.IOException;
import okio.Timeout;

public abstract interface Call
  extends Cloneable
{
  public abstract Request request();
  
  public abstract Response execute()
    throws IOException;
  
  public abstract void enqueue(Callback paramCallback);
  
  public abstract void cancel();
  
  public abstract boolean isExecuted();
  
  public abstract boolean isCanceled();
  
  public abstract Timeout timeout();
  
  public abstract Call clone();
  
  public static abstract interface Factory
  {
    public abstract Call newCall(Request paramRequest);
  }
}
