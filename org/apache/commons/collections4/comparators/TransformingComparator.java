package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;









































public class TransformingComparator<I, O>
  implements Comparator<I>, Serializable
{
  private static final long serialVersionUID = 3456940356043606220L;
  private final Comparator<O> decorated;
  private final Transformer<? super I, ? extends O> transformer;
  
  public TransformingComparator(Transformer<? super I, ? extends O> transformer)
  {
    this(transformer, ComparatorUtils.NATURAL_COMPARATOR);
  }
  






  public TransformingComparator(Transformer<? super I, ? extends O> transformer, Comparator<O> decorated)
  {
    this.decorated = decorated;
    this.transformer = transformer;
  }
  








  public int compare(I obj1, I obj2)
  {
    O value1 = transformer.transform(obj1);
    O value2 = transformer.transform(obj2);
    return decorated.compare(value1, value2);
  }
  







  public int hashCode()
  {
    int total = 17;
    total = total * 37 + (decorated == null ? 0 : decorated.hashCode());
    total = total * 37 + (transformer == null ? 0 : transformer.hashCode());
    return total;
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
      TransformingComparator<?, ?> comp = (TransformingComparator)object;
      return (null == decorated ? null == decorated : decorated.equals(decorated)) && (null == transformer ? null == transformer : transformer.equals(transformer));
    }
    
    return false;
  }
}
