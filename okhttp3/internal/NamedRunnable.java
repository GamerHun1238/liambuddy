package okhttp3.internal;









public abstract class NamedRunnable
  implements Runnable
{
  protected final String name;
  







  public NamedRunnable(String format, Object... args)
  {
    name = Util.format(format, args);
  }
  
  public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
      
      Thread.currentThread().setName(oldName); } finally { Thread.currentThread().setName(oldName);
    }
  }
  
  protected abstract void execute();
}
