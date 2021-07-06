package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Transformer;



































public class TransformerClosure<E>
  implements Closure<E>, Serializable
{
  private static final long serialVersionUID = -5194992589193388969L;
  private final Transformer<? super E, ?> iTransformer;
  
  public static <E> Closure<E> transformerClosure(Transformer<? super E, ?> transformer)
  {
    if (transformer == null) {
      return NOPClosure.nopClosure();
    }
    return new TransformerClosure(transformer);
  }
  






  public TransformerClosure(Transformer<? super E, ?> transformer)
  {
    iTransformer = transformer;
  }
  




  public void execute(E input)
  {
    iTransformer.transform(input);
  }
  





  public Transformer<? super E, ?> getTransformer()
  {
    return iTransformer;
  }
}
