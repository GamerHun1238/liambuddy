package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;








































public class ForClosure<E>
  implements Closure<E>
{
  private final int iCount;
  private final Closure<? super E> iClosure;
  
  public static <E> Closure<E> forClosure(int count, Closure<? super E> closure)
  {
    if ((count <= 0) || (closure == null)) {
      return NOPClosure.nopClosure();
    }
    if (count == 1) {
      return closure;
    }
    return new ForClosure(count, closure);
  }
  







  public ForClosure(int count, Closure<? super E> closure)
  {
    iCount = count;
    iClosure = closure;
  }
  





  public void execute(E input)
  {
    for (int i = 0; i < iCount; i++) {
      iClosure.execute(input);
    }
  }
  





  public Closure<? super E> getClosure()
  {
    return iClosure;
  }
  





  public int getCount()
  {
    return iCount;
  }
}
