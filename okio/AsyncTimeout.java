package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;







































public class AsyncTimeout
  extends Timeout
{
  private static final int TIMEOUT_WRITE_SIZE = 65536;
  private static final long IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
  private static final long IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);
  

  @Nullable
  static AsyncTimeout head;
  

  private boolean inQueue;
  

  @Nullable
  private AsyncTimeout next;
  

  private long timeoutAt;
  

  public AsyncTimeout() {}
  

  public final void enter()
  {
    if (inQueue) throw new IllegalStateException("Unbalanced enter/exit");
    long timeoutNanos = timeoutNanos();
    boolean hasDeadline = hasDeadline();
    if ((timeoutNanos == 0L) && (!hasDeadline)) {
      return;
    }
    inQueue = true;
    scheduleTimeout(this, timeoutNanos, hasDeadline);
  }
  

  private static synchronized void scheduleTimeout(AsyncTimeout node, long timeoutNanos, boolean hasDeadline)
  {
    if (head == null) {
      head = new AsyncTimeout();
      new Watchdog().start();
    }
    
    long now = System.nanoTime();
    if ((timeoutNanos != 0L) && (hasDeadline))
    {

      timeoutAt = (now + Math.min(timeoutNanos, node.deadlineNanoTime() - now));
    } else if (timeoutNanos != 0L) {
      timeoutAt = (now + timeoutNanos);
    } else if (hasDeadline) {
      timeoutAt = node.deadlineNanoTime();
    } else {
      throw new AssertionError();
    }
    

    long remainingNanos = node.remainingNanos(now);
    for (AsyncTimeout prev = head;; prev = next) {
      if ((next == null) || (remainingNanos < next.remainingNanos(now))) {
        next = next;
        next = node;
        if (prev != head) break;
        AsyncTimeout.class.notify(); break;
      }
    }
  }
  


  public final boolean exit()
  {
    if (!inQueue) return false;
    inQueue = false;
    return cancelScheduledTimeout(this);
  }
  

  private static synchronized boolean cancelScheduledTimeout(AsyncTimeout node)
  {
    for (AsyncTimeout prev = head; prev != null; prev = next) {
      if (next == node) {
        next = next;
        next = null;
        return false;
      }
    }
    

    return true;
  }
  



  private long remainingNanos(long now)
  {
    return timeoutAt - now;
  }
  




  protected void timedOut() {}
  




  public final Sink sink(final Sink sink)
  {
    new Sink() {
      public void write(Buffer source, long byteCount) throws IOException {
        Util.checkOffsetAndCount(size, 0L, byteCount);
        
        while (byteCount > 0L)
        {
          long toWrite = 0L;
          for (Segment s = head; toWrite < 65536L; s = next) {
            int segmentSize = limit - pos;
            toWrite += segmentSize;
            if (toWrite >= byteCount) {
              toWrite = byteCount;
              break;
            }
          }
          

          boolean throwOnTimeout = false;
          enter();
          try {
            sink.write(source, toWrite);
            byteCount -= toWrite;
            throwOnTimeout = true;
          } catch (IOException e) {
            throw exit(e);
          } finally {
            exit(throwOnTimeout);
          }
        }
      }
      
      public void flush() throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          sink.flush();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }
      
      public void close() throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          sink.close();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }
      
      public Timeout timeout() {
        return AsyncTimeout.this;
      }
      
      public String toString() {
        return "AsyncTimeout.sink(" + sink + ")";
      }
    };
  }
  



  public final Source source(final Source source)
  {
    new Source() {
      public long read(Buffer sink, long byteCount) throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          long result = source.read(sink, byteCount);
          throwOnTimeout = true;
          return result;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }
      
      public void close() throws IOException {
        boolean throwOnTimeout = false;
        enter();
        try {
          source.close();
          throwOnTimeout = true;
        } catch (IOException e) {
          throw exit(e);
        } finally {
          exit(throwOnTimeout);
        }
      }
      
      public Timeout timeout() {
        return AsyncTimeout.this;
      }
      
      public String toString() {
        return "AsyncTimeout.source(" + source + ")";
      }
    };
  }
  


  final void exit(boolean throwOnTimeout)
    throws IOException
  {
    boolean timedOut = exit();
    if ((timedOut) && (throwOnTimeout)) { throw newTimeoutException(null);
    }
  }
  


  final IOException exit(IOException cause)
    throws IOException
  {
    if (!exit()) return cause;
    return newTimeoutException(cause);
  }
  




  protected IOException newTimeoutException(@Nullable IOException cause)
  {
    InterruptedIOException e = new InterruptedIOException("timeout");
    if (cause != null) {
      e.initCause(cause);
    }
    return e;
  }
  
  private static final class Watchdog extends Thread {
    Watchdog() {
      super();
      setDaemon(true);
    }
    
    public void run()
    {
      try {
        for (;;) {
          synchronized (AsyncTimeout.class) {
            AsyncTimeout timedOut = AsyncTimeout.awaitTimeout();
            

            if (timedOut == null) {
              continue;
            }
            
            if (timedOut == AsyncTimeout.head) {
              AsyncTimeout.head = null;
              return;
            }
          }
          
          AsyncTimeout timedOut;
          timedOut.timedOut();
        }
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  





  @Nullable
  static AsyncTimeout awaitTimeout()
    throws InterruptedException
  {
    AsyncTimeout node = headnext;
    

    if (node == null) {
      long startNanos = System.nanoTime();
      AsyncTimeout.class.wait(IDLE_TIMEOUT_MILLIS);
      return (headnext == null) && (System.nanoTime() - startNanos >= IDLE_TIMEOUT_NANOS) ? 
        head : 
        null;
    }
    
    long waitNanos = node.remainingNanos(System.nanoTime());
    

    if (waitNanos > 0L)
    {

      long waitMillis = waitNanos / 1000000L;
      waitNanos -= waitMillis * 1000000L;
      AsyncTimeout.class.wait(waitMillis, (int)waitNanos);
      return null;
    }
    

    headnext = next;
    next = null;
    return node;
  }
}
