package gnu.trove.impl.sync;

import gnu.trove.set.TByteSet;














































public class TSynchronizedByteSet
  extends TSynchronizedByteCollection
  implements TByteSet
{
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedByteSet(TByteSet s)
  {
    super(s);
  }
  
  public TSynchronizedByteSet(TByteSet s, Object mutex) { super(s, mutex); }
  
  public boolean equals(Object o)
  {
    synchronized (mutex) { return c.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return c.hashCode();
    }
  }
}
