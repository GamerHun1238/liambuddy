package gnu.trove.decorator;

import gnu.trove.list.TCharList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

















































public class TCharListDecorator
  extends AbstractList<Character>
  implements List<Character>, Externalizable, Cloneable
{
  static final long serialVersionUID = 1L;
  protected TCharList list;
  
  public TCharListDecorator() {}
  
  public TCharListDecorator(TCharList list)
  {
    this.list = list;
  }
  





  public TCharList getList()
  {
    return list;
  }
  

  public int size()
  {
    return list.size();
  }
  

  public Character get(int index)
  {
    char value = list.get(index);
    if (value == list.getNoEntryValue()) return null;
    return Character.valueOf(value);
  }
  

  public Character set(int index, Character value)
  {
    char previous_value = list.set(index, value.charValue());
    if (previous_value == list.getNoEntryValue()) return null;
    return Character.valueOf(previous_value);
  }
  

  public void add(int index, Character value)
  {
    list.insert(index, value.charValue());
  }
  

  public Character remove(int index)
  {
    char previous_value = list.removeAt(index);
    if (previous_value == list.getNoEntryValue()) return null;
    return Character.valueOf(previous_value);
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    list = ((TCharList)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(list);
  }
}
