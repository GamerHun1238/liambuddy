package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;








































public class Timeout
{
  public static final Timeout NONE = new Timeout() {
    public Timeout timeout(long timeout, TimeUnit unit) {
      return this;
    }
    
    public Timeout deadlineNanoTime(long deadlineNanoTime) {
      return this;
    }
    


    public void throwIfReached()
      throws IOException
    {}
  };
  

  private boolean hasDeadline;
  

  private long deadlineNanoTime;
  

  private long timeoutNanos;
  


  public Timeout() {}
  


  public Timeout timeout(long timeout, TimeUnit unit)
  {
    if (timeout < 0L) throw new IllegalArgumentException("timeout < 0: " + timeout);
    if (unit == null) throw new IllegalArgumentException("unit == null");
    timeoutNanos = unit.toNanos(timeout);
    return this;
  }
  
  public long timeoutNanos()
  {
    return timeoutNanos;
  }
  
  public boolean hasDeadline()
  {
    return hasDeadline;
  }
  





  public long deadlineNanoTime()
  {
    if (!hasDeadline) throw new IllegalStateException("No deadline");
    return deadlineNanoTime;
  }
  




  public Timeout deadlineNanoTime(long deadlineNanoTime)
  {
    hasDeadline = true;
    this.deadlineNanoTime = deadlineNanoTime;
    return this;
  }
  
  public final Timeout deadline(long duration, TimeUnit unit)
  {
    if (duration <= 0L) throw new IllegalArgumentException("duration <= 0: " + duration);
    if (unit == null) throw new IllegalArgumentException("unit == null");
    return deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
  }
  
  public Timeout clearTimeout()
  {
    timeoutNanos = 0L;
    return this;
  }
  
  public Timeout clearDeadline()
  {
    hasDeadline = false;
    return this;
  }
  



  public void throwIfReached()
    throws IOException
  {
    if (Thread.interrupted()) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException("interrupted");
    }
    
    if ((hasDeadline) && (deadlineNanoTime - System.nanoTime() <= 0L)) {
      throw new InterruptedIOException("deadline reached");
    }
  }
  

































  public final void waitUntilNotified(Object monitor)
    throws InterruptedIOException
  {
    try
    {
      boolean hasDeadline = hasDeadline();
      long timeoutNanos = timeoutNanos();
      
      if ((!hasDeadline) && (timeoutNanos == 0L)) {
        monitor.wait();
        return;
      }
      


      long start = System.nanoTime();
      long waitNanos; long waitNanos; if ((hasDeadline) && (timeoutNanos != 0L)) {
        long deadlineNanos = deadlineNanoTime() - start;
        waitNanos = Math.min(timeoutNanos, deadlineNanos); } else { long waitNanos;
        if (hasDeadline) {
          waitNanos = deadlineNanoTime() - start;
        } else {
          waitNanos = timeoutNanos;
        }
      }
      
      long elapsedNanos = 0L;
      if (waitNanos > 0L) {
        long waitMillis = waitNanos / 1000000L;
        monitor.wait(waitMillis, (int)(waitNanos - waitMillis * 1000000L));
        elapsedNanos = System.nanoTime() - start;
      }
      

      if (elapsedNanos >= waitNanos) {
        throw new InterruptedIOException("timeout");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException("interrupted");
    }
  }
  
  static long minTimeout(long aNanos, long bNanos) {
    if (aNanos == 0L) return bNanos;
    if (bNanos == 0L) return aNanos;
    if (aNanos < bNanos) return aNanos;
    return bNanos;
  }
}
