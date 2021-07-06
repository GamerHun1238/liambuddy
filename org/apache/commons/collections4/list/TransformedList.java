package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;












































public class TransformedList<E>
  extends TransformedCollection<E>
  implements List<E>
{
  private static final long serialVersionUID = 1077193035000013141L;
  
  public static <E> TransformedList<E> transformingList(List<E> list, Transformer<? super E, ? extends E> transformer)
  {
    return new TransformedList(list, transformer);
  }
  















  public static <E> TransformedList<E> transformedList(List<E> list, Transformer<? super E, ? extends E> transformer)
  {
    TransformedList<E> decorated = new TransformedList(list, transformer);
    if (list.size() > 0)
    {
      E[] values = (Object[])list.toArray();
      list.clear();
      for (E value : values) {
        decorated.decorated().add(transformer.transform(value));
      }
    }
    return decorated;
  }
  










  protected TransformedList(List<E> list, Transformer<? super E, ? extends E> transformer)
  {
    super(list, transformer);
  }
  




  protected List<E> getList()
  {
    return (List)decorated();
  }
  
  public boolean equals(Object object)
  {
    return (object == this) || (decorated().equals(object));
  }
  
  public int hashCode()
  {
    return decorated().hashCode();
  }
  

  public E get(int index)
  {
    return getList().get(index);
  }
  
  public int indexOf(Object object) {
    return getList().indexOf(object);
  }
  
  public int lastIndexOf(Object object) {
    return getList().lastIndexOf(object);
  }
  
  public E remove(int index) {
    return getList().remove(index);
  }
  

  public void add(int index, E object)
  {
    object = transform(object);
    getList().add(index, object);
  }
  
  public boolean addAll(int index, Collection<? extends E> coll) {
    coll = transform(coll);
    return getList().addAll(index, coll);
  }
  
  public ListIterator<E> listIterator() {
    return listIterator(0);
  }
  
  public ListIterator<E> listIterator(int i) {
    return new TransformedListIterator(getList().listIterator(i));
  }
  
  public E set(int index, E object) {
    object = transform(object);
    return getList().set(index, object);
  }
  
  public List<E> subList(int fromIndex, int toIndex) {
    List<E> sub = getList().subList(fromIndex, toIndex);
    return new TransformedList(sub, transformer);
  }
  






  protected class TransformedListIterator
    extends AbstractListIteratorDecorator<E>
  {
    protected TransformedListIterator()
    {
      super();
    }
    
    public void add(E object)
    {
      object = transform(object);
      getListIterator().add(object);
    }
    
    public void set(E object)
    {
      object = transform(object);
      getListIterator().set(object);
    }
  }
}
