package gnu.trove.list;




public abstract class TLinkableAdapter<T extends TLinkable>
  implements TLinkable<T>
{
  private volatile T next;
  


  private volatile T prev;
  



  public TLinkableAdapter() {}
  



  public T getNext()
  {
    return next;
  }
  
  public void setNext(T next)
  {
    this.next = next;
  }
  
  public T getPrevious()
  {
    return prev;
  }
  
  public void setPrevious(T prev)
  {
    this.prev = prev;
  }
}
