package gnu.trove.decorator;

import gnu.trove.list.TDoubleList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

















































public class TDoubleListDecorator
  extends AbstractList<Double>
  implements List<Double>, Externalizable, Cloneable
{
  static final long serialVersionUID = 1L;
  protected TDoubleList list;
  
  public TDoubleListDecorator() {}
  
  public TDoubleListDecorator(TDoubleList list)
  {
    this.list = list;
  }
  





  public TDoubleList getList()
  {
    return list;
  }
  

  public int size()
  {
    return list.size();
  }
  

  public Double get(int index)
  {
    double value = list.get(index);
    if (value == list.getNoEntryValue()) return null;
    return Double.valueOf(value);
  }
  

  public Double set(int index, Double value)
  {
    double previous_value = list.set(index, value.doubleValue());
    if (previous_value == list.getNoEntryValue()) return null;
    return Double.valueOf(previous_value);
  }
  

  public void add(int index, Double value)
  {
    list.insert(index, value.doubleValue());
  }
  

  public Double remove(int index)
  {
    double previous_value = list.removeAt(index);
    if (previous_value == list.getNoEntryValue()) return null;
    return Double.valueOf(previous_value);
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    list = ((TDoubleList)in.readObject());
  }
  

  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeObject(list);
  }
}
