package gnu.trove.impl.sync;

import gnu.trove.set.TLongSet;














































public class TSynchronizedLongSet
  extends TSynchronizedLongCollection
  implements TLongSet
{
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedLongSet(TLongSet s)
  {
    super(s);
  }
  
  public TSynchronizedLongSet(TLongSet s, Object mutex) { super(s, mutex); }
  
  public boolean equals(Object o)
  {
    synchronized (mutex) { return c.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return c.hashCode();
    }
  }
}
