package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;









































public class IfClosure<E>
  implements Closure<E>, Serializable
{
  private static final long serialVersionUID = 3518477308466486130L;
  private final Predicate<? super E> iPredicate;
  private final Closure<? super E> iTrueClosure;
  private final Closure<? super E> iFalseClosure;
  
  public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure)
  {
    return ifClosure(predicate, trueClosure, NOPClosure.nopClosure());
  }
  











  public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    if ((trueClosure == null) || (falseClosure == null)) {
      throw new NullPointerException("Closures must not be null");
    }
    return new IfClosure(predicate, trueClosure, falseClosure);
  }
  










  public IfClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure)
  {
    this(predicate, trueClosure, NOPClosure.nopClosure());
  }
  









  public IfClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure)
  {
    iPredicate = predicate;
    iTrueClosure = trueClosure;
    iFalseClosure = falseClosure;
  }
  




  public void execute(E input)
  {
    if (iPredicate.evaluate(input)) {
      iTrueClosure.execute(input);
    } else {
      iFalseClosure.execute(input);
    }
  }
  





  public Predicate<? super E> getPredicate()
  {
    return iPredicate;
  }
  





  public Closure<? super E> getTrueClosure()
  {
    return iTrueClosure;
  }
  





  public Closure<? super E> getFalseClosure()
  {
    return iFalseClosure;
  }
}
