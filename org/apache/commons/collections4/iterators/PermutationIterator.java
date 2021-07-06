package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
























































public class PermutationIterator<E>
  implements Iterator<List<E>>
{
  private int[] keys;
  private Map<Integer, E> objectMap;
  private boolean[] direction;
  private List<E> nextPermutation;
  
  public PermutationIterator(Collection<? extends E> coll)
  {
    if (coll == null) {
      throw new NullPointerException("The collection must not be null");
    }
    
    keys = new int[coll.size()];
    direction = new boolean[coll.size()];
    Arrays.fill(direction, false);
    int value = 1;
    objectMap = new HashMap();
    for (E e : coll) {
      objectMap.put(Integer.valueOf(value), e);
      keys[(value - 1)] = value;
      value++;
    }
    nextPermutation = new ArrayList(coll);
  }
  



  public boolean hasNext()
  {
    return nextPermutation != null;
  }
  




  public List<E> next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    

    int indexOfLargestMobileInteger = -1;
    int largestKey = -1;
    for (int i = 0; i < keys.length; i++) {
      if (((direction[i] != 0) && (i < keys.length - 1) && (keys[i] > keys[(i + 1)])) || ((direction[i] == 0) && (i > 0) && (keys[i] > keys[(i - 1)])))
      {
        if (keys[i] > largestKey) {
          largestKey = keys[i];
          indexOfLargestMobileInteger = i;
        }
      }
    }
    if (largestKey == -1) {
      List<E> toReturn = nextPermutation;
      nextPermutation = null;
      return toReturn;
    }
    

    int offset = direction[indexOfLargestMobileInteger] != 0 ? 1 : -1;
    int tmpKey = keys[indexOfLargestMobileInteger];
    keys[indexOfLargestMobileInteger] = keys[(indexOfLargestMobileInteger + offset)];
    keys[(indexOfLargestMobileInteger + offset)] = tmpKey;
    boolean tmpDirection = direction[indexOfLargestMobileInteger];
    direction[indexOfLargestMobileInteger] = direction[(indexOfLargestMobileInteger + offset)];
    direction[(indexOfLargestMobileInteger + offset)] = tmpDirection;
    

    List<E> nextP = new ArrayList();
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > largestKey) {
        direction[i] = (direction[i] == 0 ? 1 : false);
      }
      nextP.add(objectMap.get(Integer.valueOf(keys[i])));
    }
    List<E> result = nextPermutation;
    nextPermutation = nextP;
    return result;
  }
  
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported");
  }
}
