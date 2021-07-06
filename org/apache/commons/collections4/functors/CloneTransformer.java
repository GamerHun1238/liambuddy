package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;






























public class CloneTransformer<T>
  implements Transformer<T, T>
{
  public static final Transformer INSTANCE = new CloneTransformer();
  







  public static <T> Transformer<T, T> cloneTransformer()
  {
    return INSTANCE;
  }
  






  private CloneTransformer() {}
  





  public T transform(T input)
  {
    if (input == null) {
      return null;
    }
    return PrototypeFactory.prototypeFactory(input).create();
  }
}
