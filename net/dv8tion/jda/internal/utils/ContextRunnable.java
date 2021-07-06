package net.dv8tion.jda.internal.utils;

import java.util.concurrent.Callable;
import net.dv8tion.jda.api.audit.ThreadLocalReason;
import net.dv8tion.jda.api.audit.ThreadLocalReason.Closable;
















public class ContextRunnable<E>
  implements Runnable, Callable<E>
{
  private final String localReason;
  private final Runnable runnable;
  private final Callable<E> callable;
  
  public ContextRunnable(Runnable runnable)
  {
    localReason = ThreadLocalReason.getCurrent();
    this.runnable = runnable;
    callable = null;
  }
  
  public ContextRunnable(Callable<E> callable)
  {
    localReason = ThreadLocalReason.getCurrent();
    runnable = null;
    this.callable = callable;
  }
  

  public void run()
  {
    ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
    try {
      runnable.run();
      if (__ == null) return; __.close();
    }
    catch (Throwable localThrowable)
    {
      if (__ == null) break label46; } try { __.close(); } catch (Throwable localThrowable1) { localThrowable.addSuppressed(localThrowable1); } label46: throw localThrowable;
  }
  



  public E call()
    throws Exception
  {
    ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
    try {
      Object localObject = callable.call();
      if (__ != null) __.close();
      return localObject;
    }
    catch (Throwable localThrowable2)
    {
      if (__ != null) try { __.close(); } catch (Throwable localThrowable1) { localThrowable2.addSuppressed(localThrowable1); } throw localThrowable2;
    }
  }
}
