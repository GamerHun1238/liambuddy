package okio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;














public class ForwardingTimeout
  extends Timeout
{
  private Timeout delegate;
  
  public ForwardingTimeout(Timeout delegate)
  {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }
  
  public final Timeout delegate()
  {
    return delegate;
  }
  
  public final ForwardingTimeout setDelegate(Timeout delegate) {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
    return this;
  }
  
  public Timeout timeout(long timeout, TimeUnit unit) {
    return delegate.timeout(timeout, unit);
  }
  
  public long timeoutNanos() {
    return delegate.timeoutNanos();
  }
  
  public boolean hasDeadline() {
    return delegate.hasDeadline();
  }
  
  public long deadlineNanoTime() {
    return delegate.deadlineNanoTime();
  }
  
  public Timeout deadlineNanoTime(long deadlineNanoTime) {
    return delegate.deadlineNanoTime(deadlineNanoTime);
  }
  
  public Timeout clearTimeout() {
    return delegate.clearTimeout();
  }
  
  public Timeout clearDeadline() {
    return delegate.clearDeadline();
  }
  
  public void throwIfReached() throws IOException {
    delegate.throwIfReached();
  }
}
