package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;











































public class FixedOrderComparator<T>
  implements Comparator<T>, Serializable
{
  private static final long serialVersionUID = 82794675842863201L;
  public FixedOrderComparator() {}
  
  public static enum UnknownObjectBehavior
  {
    BEFORE,  AFTER,  EXCEPTION;
    
    private UnknownObjectBehavior() {} }
  
  private final Map<T, Integer> map = new HashMap();
  

  private int counter = 0;
  

  private boolean isLocked = false;
  

  private UnknownObjectBehavior unknownObjectBehavior = UnknownObjectBehavior.EXCEPTION;
  


















  public FixedOrderComparator(T... items)
  {
    if (items == null) {
      throw new NullPointerException("The list of items must not be null");
    }
    for (T item : items) {
      add(item);
    }
  }
  









  public FixedOrderComparator(List<T> items)
  {
    if (items == null) {
      throw new NullPointerException("The list of items must not be null");
    }
    for (T t : items) {
      add(t);
    }
  }
  








  public boolean isLocked()
  {
    return isLocked;
  }
  




  protected void checkLocked()
  {
    if (isLocked()) {
      throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
    }
  }
  




  public UnknownObjectBehavior getUnknownObjectBehavior()
  {
    return unknownObjectBehavior;
  }
  







  public void setUnknownObjectBehavior(UnknownObjectBehavior unknownObjectBehavior)
  {
    checkLocked();
    if (unknownObjectBehavior == null) {
      throw new NullPointerException("Unknown object behavior must not be null");
    }
    this.unknownObjectBehavior = unknownObjectBehavior;
  }
  











  public boolean add(T obj)
  {
    checkLocked();
    Integer position = (Integer)map.put(obj, Integer.valueOf(counter++));
    return position == null;
  }
  












  public boolean addAsEqual(T existingObj, T newObj)
  {
    checkLocked();
    Integer position = (Integer)map.get(existingObj);
    if (position == null) {
      throw new IllegalArgumentException(existingObj + " not known to " + this);
    }
    Integer result = (Integer)map.put(newObj, position);
    return result == null;
  }
  
















  public int compare(T obj1, T obj2)
  {
    isLocked = true;
    Integer position1 = (Integer)map.get(obj1);
    Integer position2 = (Integer)map.get(obj2);
    if ((position1 == null) || (position2 == null)) {
      switch (1.$SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior[unknownObjectBehavior.ordinal()]) {
      case 1: 
        return position1 == null ? -1 : position2 == null ? 0 : 1;
      case 2: 
        return position1 == null ? 1 : position2 == null ? 0 : -1;
      case 3: 
        Object unknownObj = position1 == null ? obj1 : obj2;
        throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
      }
      
      throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + unknownObjectBehavior);
    }
    

    return position1.compareTo(position2);
  }
  







  public int hashCode()
  {
    int total = 17;
    total = total * 37 + (map == null ? 0 : map.hashCode());
    total = total * 37 + (unknownObjectBehavior == null ? 0 : unknownObjectBehavior.hashCode());
    total = total * 37 + counter;
    total = total * 37 + (isLocked ? 0 : 1);
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
      FixedOrderComparator<?> comp = (FixedOrderComparator)object;
      return (null == map ? null == map : map.equals(map)) && (null == unknownObjectBehavior ? null == unknownObjectBehavior : (unknownObjectBehavior == unknownObjectBehavior) && (counter == counter) && (isLocked == isLocked) && (unknownObjectBehavior == unknownObjectBehavior));
    }
    




    return false;
  }
}
