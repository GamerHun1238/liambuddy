package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;


































public class FactoryTransformer<I, O>
  implements Transformer<I, O>, Serializable
{
  private static final long serialVersionUID = -6817674502475353160L;
  private final Factory<? extends O> iFactory;
  
  public static <I, O> Transformer<I, O> factoryTransformer(Factory<? extends O> factory)
  {
    if (factory == null) {
      throw new NullPointerException("Factory must not be null");
    }
    return new FactoryTransformer(factory);
  }
  






  public FactoryTransformer(Factory<? extends O> factory)
  {
    iFactory = factory;
  }
  






  public O transform(I input)
  {
    return iFactory.create();
  }
  





  public Factory<? extends O> getFactory()
  {
    return iFactory;
  }
}
