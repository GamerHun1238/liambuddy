package net.dv8tion.jda.internal.utils;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.annotation.Nonnull;












public class ClassWalker
  implements Iterable<Class<?>>
{
  private final Class<?> clazz;
  private final Class<?> end;
  
  private ClassWalker(Class<?> clazz)
  {
    this(clazz, Object.class);
  }
  
  private ClassWalker(Class<?> clazz, Class<?> end)
  {
    this.clazz = clazz;
    this.end = end;
  }
  
  public static ClassWalker range(Class<?> start, Class<?> end)
  {
    return new ClassWalker(start, end);
  }
  
  public static ClassWalker walk(Class<?> start)
  {
    return new ClassWalker(start);
  }
  

  @Nonnull
  public Iterator<Class<?>> iterator()
  {
    new Iterator()
    {
      private final Set<Class<?>> done;
      


      private final Deque<Class<?>> work;
      



      public boolean hasNext()
      {
        return !work.isEmpty();
      }
      

      public Class<?> next()
      {
        Class<?> current = (Class)work.removeFirst();
        done.add(current);
        for (Class<?> parent : current.getInterfaces())
        {
          if (!done.contains(parent)) {
            work.addLast(parent);
          }
        }
        Object parent = current.getSuperclass();
        if ((parent != null) && (!done.contains(parent)))
          work.addLast(parent);
        return current;
      }
    };
  }
}
