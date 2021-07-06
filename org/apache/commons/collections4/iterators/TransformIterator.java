package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.Transformer;







































public class TransformIterator<I, O>
  implements Iterator<O>
{
  private Iterator<? extends I> iterator;
  private Transformer<? super I, ? extends O> transformer;
  
  public TransformIterator() {}
  
  public TransformIterator(Iterator<? extends I> iterator)
  {
    this.iterator = iterator;
  }
  









  public TransformIterator(Iterator<? extends I> iterator, Transformer<? super I, ? extends O> transformer)
  {
    this.iterator = iterator;
    this.transformer = transformer;
  }
  
  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  







  public O next()
  {
    return transform(iterator.next());
  }
  
  public void remove() {
    iterator.remove();
  }
  





  public Iterator<? extends I> getIterator()
  {
    return iterator;
  }
  





  public void setIterator(Iterator<? extends I> iterator)
  {
    this.iterator = iterator;
  }
  





  public Transformer<? super I, ? extends O> getTransformer()
  {
    return transformer;
  }
  





  public void setTransformer(Transformer<? super I, ? extends O> transformer)
  {
    this.transformer = transformer;
  }
  







  protected O transform(I source)
  {
    return transformer.transform(source);
  }
}
