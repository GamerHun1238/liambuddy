package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections4.Predicate;




































































public class ComparatorPredicate<T>
  implements Predicate<T>, Serializable
{
  private static final long serialVersionUID = -1863209236504077399L;
  private final T object;
  private final Comparator<T> comparator;
  private final Criterion criterion;
  
  public static enum Criterion
  {
    EQUAL,  GREATER,  LESS,  GREATER_OR_EQUAL,  LESS_OR_EQUAL;
    









    private Criterion() {}
  }
  








  public static <T> Predicate<T> comparatorPredicate(T object, Comparator<T> comparator)
  {
    return comparatorPredicate(object, comparator, Criterion.EQUAL);
  }
  










  public static <T> Predicate<T> comparatorPredicate(T object, Comparator<T> comparator, Criterion criterion)
  {
    if (comparator == null) {
      throw new NullPointerException("Comparator must not be null.");
    }
    if (criterion == null) {
      throw new NullPointerException("Criterion must not be null.");
    }
    return new ComparatorPredicate(object, comparator, criterion);
  }
  








  public ComparatorPredicate(T object, Comparator<T> comparator, Criterion criterion)
  {
    this.object = object;
    this.comparator = comparator;
    this.criterion = criterion;
  }
  


















  public boolean evaluate(T target)
  {
    boolean result = false;
    int comparison = comparator.compare(object, target);
    switch (1.$SwitchMap$org$apache$commons$collections4$functors$ComparatorPredicate$Criterion[criterion.ordinal()]) {
    case 1: 
      result = comparison == 0;
      break;
    case 2: 
      result = comparison > 0;
      break;
    case 3: 
      result = comparison < 0;
      break;
    case 4: 
      result = comparison >= 0;
      break;
    case 5: 
      result = comparison <= 0;
      break;
    default: 
      throw new IllegalStateException("The current criterion '" + criterion + "' is invalid.");
    }
    
    return result;
  }
}
