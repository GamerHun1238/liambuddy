package gnu.trove.decorator;

import gnu.trove.list.TByteList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

















































public class TByteListDecorator
  extends AbstractList<Byte>
  implements List<Byte>, Externalizable, Cloneable
{
  static final long serialVersionUID = 1L;
  protected TByteList list;
  
  public TByteListDecorator() {}
  
  public TByteListDecorator(TByteList list)
  {
    this.list = list;
  }
  





  public TByteList getList()
  {
    return list;
  }
  

  public int size()
  {
    return list.size();
  }
  

  public Byte get(int index)
  {
    byte value = list.get(index);
    if (value == list.getNoEntryValue()) return null;
    return Byte.valueOf(value);
  }
  

  public Byte set(int index, Byte value)
  {
    byte previous_value = list.set(index, value.byteValue());
    if (previous_value == list.getNoEntryValue()) return null;
    return Byte.valueOf(previous_value);
  }
  

  public void add(int index, Byte value)
  {
    list.insert(index, value.byteValue());
  }
  

  public Byte remove(int index)
  {
    byte previous_value = list.removeAt(index);
    if (previous_value == list.getNoEntryValue()) return null;
    return Byte.valueOf(previous_value);
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    list = ((TByteList)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(list);
  }
}
