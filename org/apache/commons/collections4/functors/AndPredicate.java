package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;




































public final class AndPredicate<T>
  implements PredicateDecorator<T>, Serializable
{
  private static final long serialVersionUID = 4189014213763186912L;
  private final Predicate<? super T> iPredicate1;
  private final Predicate<? super T> iPredicate2;
  
  public static <T> Predicate<T> andPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
  {
    if ((predicate1 == null) || (predicate2 == null)) {
      throw new NullPointerException("Predicate must not be null");
    }
    return new AndPredicate(predicate1, predicate2);
  }
  







  public AndPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
  {
    iPredicate1 = predicate1;
    iPredicate2 = predicate2;
  }
  





  public boolean evaluate(T object)
  {
    return (iPredicate1.evaluate(object)) && (iPredicate2.evaluate(object));
  }
  






  public Predicate<? super T>[] getPredicates()
  {
    return new Predicate[] { iPredicate1, iPredicate2 };
  }
}
