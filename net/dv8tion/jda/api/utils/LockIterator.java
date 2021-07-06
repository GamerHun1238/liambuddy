package net.dv8tion.jda.api.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;










































public class LockIterator<T>
  implements ClosableIterator<T>
{
  private static final Logger log = JDALogger.getLog(ClosableIterator.class);
  private final Iterator<? extends T> it;
  private Lock lock;
  
  public LockIterator(@Nonnull Iterator<? extends T> it, Lock lock)
  {
    this.it = it;
    this.lock = lock;
  }
  

  public void close()
  {
    if (lock != null)
      lock.unlock();
    lock = null;
  }
  

  public boolean hasNext()
  {
    if (lock == null)
      return false;
    boolean hasNext = it.hasNext();
    if (!hasNext)
      close();
    return hasNext;
  }
  

  @Nonnull
  public T next()
  {
    if (lock == null)
      throw new NoSuchElementException();
    return it.next();
  }
  

  @Deprecated
  protected void finalize()
  {
    if (lock != null)
    {
      log.error("Finalizing without closing, performing force close on lock");
      close();
    }
  }
}
