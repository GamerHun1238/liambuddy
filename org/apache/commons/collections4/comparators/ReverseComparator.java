package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections4.ComparatorUtils;





































public class ReverseComparator<E>
  implements Comparator<E>, Serializable
{
  private static final long serialVersionUID = 2858887242028539265L;
  private final Comparator<? super E> comparator;
  
  public ReverseComparator()
  {
    this(null);
  }
  








  public ReverseComparator(Comparator<? super E> comparator)
  {
    this.comparator = (comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator);
  }
  








  public int compare(E obj1, E obj2)
  {
    return comparator.compare(obj2, obj1);
  }
  








  public int hashCode()
  {
    return "ReverseComparator".hashCode() ^ comparator.hashCode();
  }
  
















  public boolean equals(Object object)
  {
    if (this == object) {
      return true;
    }
    if (null == object) {
      return false;
    }
    if (object.getClass().equals(getClass())) {
      ReverseComparator<?> thatrc = (ReverseComparator)object;
      return comparator.equals(comparator);
    }
    return false;
  }
}
