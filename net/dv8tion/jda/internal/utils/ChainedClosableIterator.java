package net.dv8tion.jda.internal.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.cache.CacheView;
import org.slf4j.Logger;

















public class ChainedClosableIterator<T>
  implements ClosableIterator<T>
{
  private static final Logger log = JDALogger.getLog(ClosableIterator.class);
  
  private final Set<T> items;
  private final Iterator<? extends CacheView<T>> generator;
  private ClosableIterator<T> currentIterator;
  private T item;
  
  public ChainedClosableIterator(Iterator<? extends CacheView<T>> generator)
  {
    items = new HashSet();
    this.generator = generator;
  }
  
  public Set<T> getItems()
  {
    return items;
  }
  

  public void close()
  {
    if (currentIterator != null)
      currentIterator.close();
    currentIterator = null;
  }
  

  public boolean hasNext()
  {
    if (item != null) {
      return true;
    }
    if (currentIterator != null)
    {
      if (!currentIterator.hasNext())
      {
        currentIterator.close();
        currentIterator = null;
      }
      else
      {
        if (findNext()) return true;
        currentIterator.close();
        currentIterator = null;
      }
    }
    
    return processChain();
  }
  
  private boolean processChain()
  {
    while (item == null)
    {
      CacheView<T> view = null;
      while (generator.hasNext())
      {
        view = (CacheView)generator.next();
        if (!view.isEmpty())
          break;
        view = null;
      }
      if (view == null) {
        return false;
      }
      
      currentIterator = view.lockedIterator();
      if (findNext()) break;
    }
    return true;
  }
  
  private boolean findNext()
  {
    while (currentIterator.hasNext())
    {
      T next = currentIterator.next();
      if (!items.contains(next))
      {
        item = next;
        items.add(item);
        return true;
      } }
    return false;
  }
  

  public T next()
  {
    if (!hasNext())
      throw new NoSuchElementException();
    T tmp = item;
    item = null;
    return tmp;
  }
  

  @Deprecated
  protected void finalize()
  {
    if (currentIterator != null)
    {
      log.error("Finalizing without closing, performing force close on lock");
      close();
    }
  }
}
