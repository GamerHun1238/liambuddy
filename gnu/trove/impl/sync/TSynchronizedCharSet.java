package gnu.trove.impl.sync;

import gnu.trove.set.TCharSet;














































public class TSynchronizedCharSet
  extends TSynchronizedCharCollection
  implements TCharSet
{
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedCharSet(TCharSet s)
  {
    super(s);
  }
  
  public TSynchronizedCharSet(TCharSet s, Object mutex) { super(s, mutex); }
  
  public boolean equals(Object o)
  {
    synchronized (mutex) { return c.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return c.hashCode();
    }
  }
}
