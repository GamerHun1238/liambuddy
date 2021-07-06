package okio;

import java.util.concurrent.TimeUnit;

















final class PushableTimeout
  extends Timeout
{
  private Timeout pushed;
  private boolean originalHasDeadline;
  private long originalDeadlineNanoTime;
  private long originalTimeoutNanos;
  
  PushableTimeout() {}
  
  void push(Timeout pushed)
  {
    this.pushed = pushed;
    originalHasDeadline = pushed.hasDeadline();
    originalDeadlineNanoTime = (originalHasDeadline ? pushed.deadlineNanoTime() : -1L);
    originalTimeoutNanos = pushed.timeoutNanos();
    
    pushed.timeout(minTimeout(originalTimeoutNanos, timeoutNanos()), TimeUnit.NANOSECONDS);
    
    if ((originalHasDeadline) && (hasDeadline())) {
      pushed.deadlineNanoTime(Math.min(deadlineNanoTime(), originalDeadlineNanoTime));
    } else if (hasDeadline()) {
      pushed.deadlineNanoTime(deadlineNanoTime());
    }
  }
  
  void pop() {
    pushed.timeout(originalTimeoutNanos, TimeUnit.NANOSECONDS);
    
    if (originalHasDeadline) {
      pushed.deadlineNanoTime(originalDeadlineNanoTime);
    } else {
      pushed.clearDeadline();
    }
  }
}
