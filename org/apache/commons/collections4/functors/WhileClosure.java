package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;









































public class WhileClosure<E>
  implements Closure<E>
{
  private final Predicate<? super E> iPredicate;
  private final Closure<? super E> iClosure;
  private final boolean iDoLoop;
  
  public static <E> Closure<E> whileClosure(Predicate<? super E> predicate, Closure<? super E> closure, boolean doLoop)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    if (closure == null) {
      throw new NullPointerException("Closure must not be null");
    }
    return new WhileClosure(predicate, closure, doLoop);
  }
  








  public WhileClosure(Predicate<? super E> predicate, Closure<? super E> closure, boolean doLoop)
  {
    iPredicate = predicate;
    iClosure = closure;
    iDoLoop = doLoop;
  }
  





  public void execute(E input)
  {
    if (iDoLoop) {
      iClosure.execute(input);
    }
    while (iPredicate.evaluate(input)) {
      iClosure.execute(input);
    }
  }
  





  public Predicate<? super E> getPredicate()
  {
    return iPredicate;
  }
  





  public Closure<? super E> getClosure()
  {
    return iClosure;
  }
  





  public boolean isDoLoop()
  {
    return iDoLoop;
  }
}
