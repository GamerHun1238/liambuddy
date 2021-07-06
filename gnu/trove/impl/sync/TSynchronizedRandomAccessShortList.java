package gnu.trove.impl.sync;

import gnu.trove.list.TShortList;
import java.util.RandomAccess;













































public class TSynchronizedRandomAccessShortList
  extends TSynchronizedShortList
  implements RandomAccess
{
  static final long serialVersionUID = 1530674583602358482L;
  
  public TSynchronizedRandomAccessShortList(TShortList list)
  {
    super(list);
  }
  
  public TSynchronizedRandomAccessShortList(TShortList list, Object mutex) {
    super(list, mutex);
  }
  
  public TShortList subList(int fromIndex, int toIndex) {
    synchronized (mutex) {
      return new TSynchronizedRandomAccessShortList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  






  private Object writeReplace()
  {
    return new TSynchronizedShortList(list);
  }
}
