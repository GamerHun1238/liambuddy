package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;












































public class IfTransformer<I, O>
  implements Transformer<I, O>, Serializable
{
  private static final long serialVersionUID = 8069309411242014252L;
  private final Predicate<? super I> iPredicate;
  private final Transformer<? super I, ? extends O> iTrueTransformer;
  private final Transformer<? super I, ? extends O> iFalseTransformer;
  
  public static <I, O> Transformer<I, O> ifTransformer(Predicate<? super I> predicate, Transformer<? super I, ? extends O> trueTransformer, Transformer<? super I, ? extends O> falseTransformer)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    if ((trueTransformer == null) || (falseTransformer == null)) {
      throw new NullPointerException("Transformers must not be null");
    }
    
    return new IfTransformer(predicate, trueTransformer, falseTransformer);
  }
  














  public static <T> Transformer<T, T> ifTransformer(Predicate<? super T> predicate, Transformer<? super T, ? extends T> trueTransformer)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    if (trueTransformer == null) {
      throw new NullPointerException("Transformer must not be null");
    }
    
    return new IfTransformer(predicate, trueTransformer, NOPTransformer.nopTransformer());
  }
  











  public IfTransformer(Predicate<? super I> predicate, Transformer<? super I, ? extends O> trueTransformer, Transformer<? super I, ? extends O> falseTransformer)
  {
    iPredicate = predicate;
    iTrueTransformer = trueTransformer;
    iFalseTransformer = falseTransformer;
  }
  





  public O transform(I input)
  {
    if (iPredicate.evaluate(input)) {
      return iTrueTransformer.transform(input);
    }
    return iFalseTransformer.transform(input);
  }
  





  public Predicate<? super I> getPredicate()
  {
    return iPredicate;
  }
  




  public Transformer<? super I, ? extends O> getTrueTransformer()
  {
    return iTrueTransformer;
  }
  




  public Transformer<? super I, ? extends O> getFalseTransformer()
  {
    return iFalseTransformer;
  }
}
