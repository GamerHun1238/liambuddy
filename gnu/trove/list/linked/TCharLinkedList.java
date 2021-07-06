package gnu.trove.list.linked;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Random;




























public class TCharLinkedList
  implements TCharList, Externalizable
{
  char no_entry_value;
  int size;
  TCharLink head = null;
  TCharLink tail = head;
  
  public TCharLinkedList() {}
  
  public TCharLinkedList(char no_entry_value)
  {
    this.no_entry_value = no_entry_value;
  }
  
  public TCharLinkedList(TCharList list) {
    no_entry_value = list.getNoEntryValue();
    
    for (TCharIterator iterator = list.iterator(); iterator.hasNext();) {
      char next = iterator.next();
      add(next);
    }
  }
  
  public char getNoEntryValue()
  {
    return no_entry_value;
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean add(char val)
  {
    TCharLink l = new TCharLink(val);
    if (no(head)) {
      head = l;
      tail = l;
    } else {
      l.setPrevious(tail);
      tail.setNext(l);
      
      tail = l;
    }
    
    size += 1;
    return true;
  }
  
  public void add(char[] vals)
  {
    for (char val : vals) {
      add(val);
    }
  }
  
  public void add(char[] vals, int offset, int length)
  {
    for (int i = 0; i < length; i++) {
      char val = vals[(offset + i)];
      add(val);
    }
  }
  
  public void insert(int offset, char value)
  {
    TCharLinkedList tmp = new TCharLinkedList();
    tmp.add(value);
    insert(offset, tmp);
  }
  
  public void insert(int offset, char[] values)
  {
    insert(offset, link(values, 0, values.length));
  }
  
  public void insert(int offset, char[] values, int valOffset, int len)
  {
    insert(offset, link(values, valOffset, len));
  }
  
  void insert(int offset, TCharLinkedList tmp) {
    TCharLink l = getLinkAt(offset);
    
    size += size;
    
    if (l == head)
    {
      tail.setNext(head);
      head.setPrevious(tail);
      head = head;
      
      return;
    }
    
    if (no(l)) {
      if (size == 0)
      {
        head = head;
        tail = tail;
      }
      else {
        tail.setNext(head);
        head.setPrevious(tail);
        tail = tail;
      }
    } else {
      TCharLink prev = l.getPrevious();
      l.getPrevious().setNext(head);
      

      tail.setNext(l);
      l.setPrevious(tail);
      
      head.setPrevious(prev);
    }
  }
  
  static TCharLinkedList link(char[] values, int valOffset, int len) {
    TCharLinkedList ret = new TCharLinkedList();
    
    for (int i = 0; i < len; i++) {
      ret.add(values[(valOffset + i)]);
    }
    
    return ret;
  }
  
  public char get(int offset)
  {
    if (offset > size) {
      throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + size);
    }
    TCharLink l = getLinkAt(offset);
    
    if (no(l)) {
      return no_entry_value;
    }
    return l.getValue();
  }
  









  public TCharLink getLinkAt(int offset)
  {
    if (offset >= size()) {
      return null;
    }
    if (offset <= size() >>> 1) {
      return getLink(head, 0, offset, true);
    }
    return getLink(tail, size() - 1, offset, false);
  }
  







  private static TCharLink getLink(TCharLink l, int idx, int offset)
  {
    return getLink(l, idx, offset, true);
  }
  







  private static TCharLink getLink(TCharLink l, int idx, int offset, boolean next)
  {
    int i = idx;
    
    while (got(l)) {
      if (i == offset) {
        return l;
      }
      
      i += (next ? 1 : -1);
      l = next ? l.getNext() : l.getPrevious();
    }
    
    return null;
  }
  

  public char set(int offset, char val)
  {
    if (offset > size) {
      throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + size);
    }
    TCharLink l = getLinkAt(offset);
    
    if (no(l)) {
      throw new IndexOutOfBoundsException("at offset " + offset);
    }
    char prev = l.getValue();
    l.setValue(val);
    return prev;
  }
  
  public void set(int offset, char[] values)
  {
    set(offset, values, 0, values.length);
  }
  
  public void set(int offset, char[] values, int valOffset, int length)
  {
    for (int i = 0; i < length; i++) {
      char value = values[(valOffset + i)];
      set(offset + i, value);
    }
  }
  
  public char replace(int offset, char val)
  {
    return set(offset, val);
  }
  
  public void clear()
  {
    size = 0;
    
    head = null;
    tail = null;
  }
  
  public boolean remove(char value)
  {
    boolean changed = false;
    for (TCharLink l = head; got(l); l = l.getNext())
    {
      if (l.getValue() == value) {
        changed = true;
        
        removeLink(l);
      }
    }
    
    return changed;
  }
  




  private void removeLink(TCharLink l)
  {
    if (no(l)) {
      return;
    }
    size -= 1;
    
    TCharLink prev = l.getPrevious();
    TCharLink next = l.getNext();
    
    if (got(prev)) {
      prev.setNext(next);
    }
    else {
      head = next;
    }
    
    if (got(next)) {
      next.setPrevious(prev);
    }
    else {
      tail = prev;
    }
    
    l.setNext(null);
    l.setPrevious(null);
  }
  
  public boolean containsAll(Collection<?> collection)
  {
    if (isEmpty()) {
      return false;
    }
    for (Object o : collection) {
      if ((o instanceof Character)) {
        Character i = (Character)o;
        if (!contains(i.charValue()))
          return false;
      } else {
        return false;
      }
    }
    return true;
  }
  
  public boolean containsAll(TCharCollection collection)
  {
    if (isEmpty()) {
      return false;
    }
    for (TCharIterator it = collection.iterator(); it.hasNext();) {
      char i = it.next();
      if (!contains(i))
        return false;
    }
    return true;
  }
  
  public boolean containsAll(char[] array)
  {
    if (isEmpty()) {
      return false;
    }
    for (char i : array) {
      if (!contains(i))
        return false;
    }
    return true;
  }
  
  public boolean addAll(Collection<? extends Character> collection)
  {
    boolean ret = false;
    for (Character v : collection) {
      if (add(v.charValue())) {
        ret = true;
      }
    }
    return ret;
  }
  
  public boolean addAll(TCharCollection collection)
  {
    boolean ret = false;
    for (TCharIterator it = collection.iterator(); it.hasNext();) {
      char i = it.next();
      if (add(i)) {
        ret = true;
      }
    }
    return ret;
  }
  
  public boolean addAll(char[] array)
  {
    boolean ret = false;
    for (char i : array) {
      if (add(i)) {
        ret = true;
      }
    }
    return ret;
  }
  
  public boolean retainAll(Collection<?> collection)
  {
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(Character.valueOf(iter.next()))) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public boolean retainAll(TCharCollection collection)
  {
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(iter.next())) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public boolean retainAll(char[] array)
  {
    Arrays.sort(array);
    
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (Arrays.binarySearch(array, iter.next()) < 0) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public boolean removeAll(Collection<?> collection)
  {
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (collection.contains(Character.valueOf(iter.next()))) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public boolean removeAll(TCharCollection collection)
  {
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (collection.contains(iter.next())) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public boolean removeAll(char[] array)
  {
    Arrays.sort(array);
    
    boolean modified = false;
    TCharIterator iter = iterator();
    while (iter.hasNext()) {
      if (Arrays.binarySearch(array, iter.next()) >= 0) {
        iter.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public char removeAt(int offset)
  {
    TCharLink l = getLinkAt(offset);
    if (no(l)) {
      throw new ArrayIndexOutOfBoundsException("no elemenet at " + offset);
    }
    char prev = l.getValue();
    removeLink(l);
    return prev;
  }
  
  public void remove(int offset, int length)
  {
    for (int i = 0; i < length; i++) {
      removeAt(offset);
    }
  }
  
  public void transformValues(TCharFunction function)
  {
    for (TCharLink l = head; got(l);)
    {
      l.setValue(function.execute(l.getValue()));
      
      l = l.getNext();
    }
  }
  
  public void reverse()
  {
    TCharLink h = head;
    TCharLink t = tail;
    


    TCharLink l = head;
    while (got(l)) {
      TCharLink next = l.getNext();
      TCharLink prev = l.getPrevious();
      
      TCharLink tmp = l;
      l = l.getNext();
      
      tmp.setNext(prev);
      tmp.setPrevious(next);
    }
    

    head = t;
    tail = h;
  }
  
  public void reverse(int from, int to)
  {
    if (from > to) {
      throw new IllegalArgumentException("from > to : " + from + ">" + to);
    }
    TCharLink start = getLinkAt(from);
    TCharLink stop = getLinkAt(to);
    
    TCharLink tmp = null;
    
    TCharLink tmpHead = start.getPrevious();
    

    TCharLink l = start;
    while (l != stop) {
      TCharLink next = l.getNext();
      TCharLink prev = l.getPrevious();
      
      tmp = l;
      l = l.getNext();
      
      tmp.setNext(prev);
      tmp.setPrevious(next);
    }
    

    if (got(tmp)) {
      tmpHead.setNext(tmp);
      stop.setPrevious(tmpHead);
    }
    start.setNext(stop);
    stop.setPrevious(start);
  }
  
  public void shuffle(Random rand)
  {
    for (int i = 0; i < size; i++) {
      TCharLink l = getLinkAt(rand.nextInt(size()));
      removeLink(l);
      add(l.getValue());
    }
  }
  
  public TCharList subList(int begin, int end)
  {
    if (end < begin) {
      throw new IllegalArgumentException("begin index " + begin + " greater than end index " + end);
    }
    
    if (size < begin) {
      throw new IllegalArgumentException("begin index " + begin + " greater than last index " + size);
    }
    
    if (begin < 0) {
      throw new IndexOutOfBoundsException("begin index can not be < 0");
    }
    if (end > size) {
      throw new IndexOutOfBoundsException("end index < " + size);
    }
    
    TCharLinkedList ret = new TCharLinkedList();
    TCharLink tmp = getLinkAt(begin);
    for (int i = begin; i < end; i++) {
      ret.add(tmp.getValue());
      tmp = tmp.getNext();
    }
    
    return ret;
  }
  
  public char[] toArray()
  {
    return toArray(new char[size], 0, size);
  }
  
  public char[] toArray(int offset, int len)
  {
    return toArray(new char[len], offset, 0, len);
  }
  
  public char[] toArray(char[] dest)
  {
    return toArray(dest, 0, size);
  }
  
  public char[] toArray(char[] dest, int offset, int len)
  {
    return toArray(dest, offset, 0, len);
  }
  
  public char[] toArray(char[] dest, int source_pos, int dest_pos, int len)
  {
    if (len == 0) {
      return dest;
    }
    if ((source_pos < 0) || (source_pos >= size())) {
      throw new ArrayIndexOutOfBoundsException(source_pos);
    }
    
    TCharLink tmp = getLinkAt(source_pos);
    for (int i = 0; i < len; i++) {
      dest[(dest_pos + i)] = tmp.getValue();
      tmp = tmp.getNext();
    }
    
    return dest;
  }
  
  public boolean forEach(TCharProcedure procedure)
  {
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (!procedure.execute(l.getValue()))
        return false;
    }
    return true;
  }
  
  public boolean forEachDescending(TCharProcedure procedure)
  {
    for (TCharLink l = tail; got(l); l = l.getPrevious()) {
      if (!procedure.execute(l.getValue()))
        return false;
    }
    return true;
  }
  
  public void sort()
  {
    sort(0, size);
  }
  
  public void sort(int fromIndex, int toIndex)
  {
    TCharList tmp = subList(fromIndex, toIndex);
    char[] vals = tmp.toArray();
    Arrays.sort(vals);
    set(fromIndex, vals);
  }
  
  public void fill(char val)
  {
    fill(0, size, val);
  }
  
  public void fill(int fromIndex, int toIndex, char val)
  {
    if (fromIndex < 0) {
      throw new IndexOutOfBoundsException("begin index can not be < 0");
    }
    

    TCharLink l = getLinkAt(fromIndex);
    if (toIndex > size) {
      for (int i = fromIndex; i < size; i++) {
        l.setValue(val);
        l = l.getNext();
      }
      for (int i = size; i < toIndex; i++) {
        add(val);
      }
    } else {
      for (int i = fromIndex; i < toIndex; i++) {
        l.setValue(val);
        l = l.getNext();
      }
    }
  }
  

  public int binarySearch(char value)
  {
    return binarySearch(value, 0, size());
  }
  
  public int binarySearch(char value, int fromIndex, int toIndex)
  {
    if (fromIndex < 0) {
      throw new IndexOutOfBoundsException("begin index can not be < 0");
    }
    
    if (toIndex > size) {
      throw new IndexOutOfBoundsException("end index > size: " + toIndex + " > " + size);
    }
    

    if (toIndex < fromIndex) {
      return -(fromIndex + 1);
    }
    


    int from = fromIndex;
    TCharLink fromLink = getLinkAt(fromIndex);
    int to = toIndex;
    
    while (from < to) {
      int mid = from + to >>> 1;
      TCharLink middle = getLink(fromLink, from, mid);
      if (middle.getValue() == value) {
        return mid;
      }
      if (middle.getValue() < value) {
        from = mid + 1;
        fromLink = next;
      } else {
        to = mid - 1;
      }
    }
    
    return -(from + 1);
  }
  
  public int indexOf(char value)
  {
    return indexOf(0, value);
  }
  
  public int indexOf(int offset, char value)
  {
    int count = offset;
    for (TCharLink l = getLinkAt(offset); got(l.getNext()); l = l.getNext()) {
      if (l.getValue() == value) {
        return count;
      }
      count++;
    }
    
    return -1;
  }
  
  public int lastIndexOf(char value)
  {
    return lastIndexOf(0, value);
  }
  
  public int lastIndexOf(int offset, char value)
  {
    if (isEmpty()) {
      return -1;
    }
    int last = -1;
    int count = offset;
    for (TCharLink l = getLinkAt(offset); got(l.getNext()); l = l.getNext()) {
      if (l.getValue() == value) {
        last = count;
      }
      count++;
    }
    
    return last;
  }
  
  public boolean contains(char value)
  {
    if (isEmpty()) {
      return false;
    }
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (l.getValue() == value)
        return true;
    }
    return false;
  }
  

  public TCharIterator iterator()
  {
    new TCharIterator() {
      TCharLinkedList.TCharLink l = head;
      TCharLinkedList.TCharLink current;
      
      public char next() {
        if (TCharLinkedList.no(l)) {
          throw new NoSuchElementException();
        }
        char ret = l.getValue();
        current = l;
        l = l.getNext();
        
        return ret;
      }
      
      public boolean hasNext() {
        return TCharLinkedList.got(l);
      }
      
      public void remove() {
        if (current == null) {
          throw new IllegalStateException();
        }
        TCharLinkedList.this.removeLink(current);
        current = null;
      }
    };
  }
  
  public TCharList grep(TCharProcedure condition)
  {
    TCharList ret = new TCharLinkedList();
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (condition.execute(l.getValue()))
        ret.add(l.getValue());
    }
    return ret;
  }
  
  public TCharList inverseGrep(TCharProcedure condition)
  {
    TCharList ret = new TCharLinkedList();
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (!condition.execute(l.getValue()))
        ret.add(l.getValue());
    }
    return ret;
  }
  
  public char max()
  {
    char ret = '\000';
    
    if (isEmpty()) {
      throw new IllegalStateException();
    }
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (ret < l.getValue()) {
        ret = l.getValue();
      }
    }
    return ret;
  }
  
  public char min()
  {
    char ret = 65535;
    
    if (isEmpty()) {
      throw new IllegalStateException();
    }
    for (TCharLink l = head; got(l); l = l.getNext()) {
      if (ret > l.getValue()) {
        ret = l.getValue();
      }
    }
    return ret;
  }
  
  public char sum()
  {
    char sum = '\000';
    
    for (TCharLink l = head; got(l); l = l.getNext()) {
      sum = (char)(sum + l.getValue());
    }
    
    return sum;
  }
  

  static class TCharLink
  {
    char value;
    TCharLink previous;
    TCharLink next;
    
    TCharLink(char value)
    {
      this.value = value;
    }
    
    public char getValue() {
      return value;
    }
    
    public void setValue(char value) {
      this.value = value;
    }
    
    public TCharLink getPrevious() {
      return previous;
    }
    
    public void setPrevious(TCharLink previous) {
      this.previous = previous;
    }
    
    public TCharLink getNext() {
      return next;
    }
    
    public void setNext(TCharLink next) {
      this.next = next;
    }
  }
  
  class RemoveProcedure implements TCharProcedure {
    boolean changed = false;
    



    RemoveProcedure() {}
    



    public boolean execute(char value)
    {
      if (remove(value)) {
        changed = true;
      }
      return true;
    }
    
    public boolean isChanged() {
      return changed;
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeChar(no_entry_value);
    

    out.writeInt(size);
    for (TCharIterator iterator = iterator(); iterator.hasNext();) {
      char next = iterator.next();
      out.writeChar(next);
    }
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    no_entry_value = in.readChar();
    

    int len = in.readInt();
    for (int i = 0; i < len; i++) {
      add(in.readChar());
    }
  }
  
  static boolean got(Object ref) {
    return ref != null;
  }
  
  static boolean no(Object ref) {
    return ref == null;
  }
  
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if ((o == null) || (getClass() != o.getClass())) { return false;
    }
    TCharLinkedList that = (TCharLinkedList)o;
    
    if (no_entry_value != no_entry_value) return false;
    if (size != size) { return false;
    }
    TCharIterator iterator = iterator();
    TCharIterator thatIterator = that.iterator();
    while (iterator.hasNext()) {
      if (!thatIterator.hasNext()) {
        return false;
      }
      if (iterator.next() != thatIterator.next()) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = HashFunctions.hash(no_entry_value);
    result = 31 * result + size;
    for (TCharIterator iterator = iterator(); iterator.hasNext();) {
      result = 31 * result + HashFunctions.hash(iterator.next());
    }
    
    return result;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("{");
    TCharIterator it = iterator();
    while (it.hasNext()) {
      char next = it.next();
      buf.append(next);
      if (it.hasNext())
        buf.append(", ");
    }
    buf.append("}");
    return buf.toString();
  }
}
