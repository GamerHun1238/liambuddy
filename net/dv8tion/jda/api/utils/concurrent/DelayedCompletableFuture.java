package net.dv8tion.jda.api.utils.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;






































public class DelayedCompletableFuture<T>
  extends CompletableFuture<T>
  implements ScheduledFuture<T>
{
  private ScheduledFuture<?> future;
  
  private DelayedCompletableFuture() {}
  
  @Nonnull
  public static <E> DelayedCompletableFuture<E> make(@Nonnull ScheduledExecutorService executor, long delay, @Nonnull TimeUnit unit, @Nonnull Function<? super DelayedCompletableFuture<E>, ? extends Runnable> mapping)
  {
    DelayedCompletableFuture<E> handle = new DelayedCompletableFuture();
    ScheduledFuture<?> future = executor.schedule((Runnable)mapping.apply(handle), delay, unit);
    handle.initProxy(future);
    return handle;
  }
  












  private void initProxy(ScheduledFuture<?> future)
  {
    if (this.future == null) {
      this.future = future;
    } else {
      throw new IllegalStateException("Cannot initialize twice");
    }
  }
  
  public boolean cancel(boolean mayInterruptIfRunning)
  {
    if ((future != null) && (!future.isDone()))
      future.cancel(mayInterruptIfRunning);
    return super.cancel(mayInterruptIfRunning);
  }
  

  public long getDelay(@Nonnull TimeUnit unit)
  {
    return future.getDelay(unit);
  }
  

  public int compareTo(@Nonnull Delayed o)
  {
    return future.compareTo(o);
  }
}
