package org.apache.commons.collections4.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.PredicatedBag;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.set.PredicatedSet;














































public class PredicatedCollection<E>
  extends AbstractCollectionDecorator<E>
{
  private static final long serialVersionUID = -5259182142076705162L;
  protected final Predicate<? super E> predicate;
  
  public static <E> Builder<E> builder(Predicate<? super E> predicate)
  {
    return new Builder(predicate);
  }
  






  public static <E> Builder<E> notNullBuilder()
  {
    return new Builder(NotNullPredicate.notNullPredicate());
  }
  














  public static <T> PredicatedCollection<T> predicatedCollection(Collection<T> coll, Predicate<? super T> predicate)
  {
    return new PredicatedCollection(coll, predicate);
  }
  











  protected PredicatedCollection(Collection<E> coll, Predicate<? super E> predicate)
  {
    super(coll);
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null.");
    }
    this.predicate = predicate;
    for (E item : coll) {
      validate(item);
    }
  }
  








  protected void validate(E object)
  {
    if (!predicate.evaluate(object)) {
      throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate '" + predicate + "' rejected it");
    }
  }
  










  public boolean add(E object)
  {
    validate(object);
    return decorated().add(object);
  }
  









  public boolean addAll(Collection<? extends E> coll)
  {
    for (E item : coll) {
      validate(item);
    }
    return decorated().addAll(coll);
  }
  















  public static class Builder<E>
  {
    private final Predicate<? super E> predicate;
    













    private final List<E> accepted = new ArrayList();
    

    private final List<E> rejected = new ArrayList();
    






    public Builder(Predicate<? super E> predicate)
    {
      if (predicate == null) {
        throw new NullPointerException("Predicate must not be null");
      }
      this.predicate = predicate;
    }
    








    public Builder<E> add(E item)
    {
      if (predicate.evaluate(item)) {
        accepted.add(item);
      } else {
        rejected.add(item);
      }
      return this;
    }
    








    public Builder<E> addAll(Collection<? extends E> items)
    {
      if (items != null) {
        for (E item : items) {
          add(item);
        }
      }
      return this;
    }
    







    public List<E> createPredicatedList()
    {
      return createPredicatedList(new ArrayList());
    }
    











    public List<E> createPredicatedList(List<E> list)
    {
      if (list == null) {
        throw new NullPointerException("List must not be null.");
      }
      List<E> predicatedList = PredicatedList.predicatedList(list, predicate);
      predicatedList.addAll(accepted);
      return predicatedList;
    }
    







    public Set<E> createPredicatedSet()
    {
      return createPredicatedSet(new HashSet());
    }
    











    public Set<E> createPredicatedSet(Set<E> set)
    {
      if (set == null) {
        throw new NullPointerException("Set must not be null.");
      }
      PredicatedSet<E> predicatedSet = PredicatedSet.predicatedSet(set, predicate);
      predicatedSet.addAll(accepted);
      return predicatedSet;
    }
    







    public MultiSet<E> createPredicatedMultiSet()
    {
      return createPredicatedMultiSet(new HashMultiSet());
    }
    











    public MultiSet<E> createPredicatedMultiSet(MultiSet<E> multiset)
    {
      if (multiset == null) {
        throw new NullPointerException("MultiSet must not be null.");
      }
      PredicatedMultiSet<E> predicatedMultiSet = PredicatedMultiSet.predicatedMultiSet(multiset, predicate);
      
      predicatedMultiSet.addAll(accepted);
      return predicatedMultiSet;
    }
    







    public Bag<E> createPredicatedBag()
    {
      return createPredicatedBag(new HashBag());
    }
    











    public Bag<E> createPredicatedBag(Bag<E> bag)
    {
      if (bag == null) {
        throw new NullPointerException("Bag must not be null.");
      }
      PredicatedBag<E> predicatedBag = PredicatedBag.predicatedBag(bag, predicate);
      predicatedBag.addAll(accepted);
      return predicatedBag;
    }
    







    public Queue<E> createPredicatedQueue()
    {
      return createPredicatedQueue(new LinkedList());
    }
    











    public Queue<E> createPredicatedQueue(Queue<E> queue)
    {
      if (queue == null) {
        throw new NullPointerException("queue must not be null");
      }
      PredicatedQueue<E> predicatedQueue = PredicatedQueue.predicatedQueue(queue, predicate);
      predicatedQueue.addAll(accepted);
      return predicatedQueue;
    }
    




    public Collection<E> rejectedElements()
    {
      return Collections.unmodifiableCollection(rejected);
    }
  }
}
