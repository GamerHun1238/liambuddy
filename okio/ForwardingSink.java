package okio;

import java.io.IOException;














public abstract class ForwardingSink
  implements Sink
{
  private final Sink delegate;
  
  public ForwardingSink(Sink delegate)
  {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }
  
  public final Sink delegate()
  {
    return delegate;
  }
  
  public void write(Buffer source, long byteCount) throws IOException {
    delegate.write(source, byteCount);
  }
  
  public void flush() throws IOException {
    delegate.flush();
  }
  
  public Timeout timeout() {
    return delegate.timeout();
  }
  
  public void close() throws IOException {
    delegate.close();
  }
  
  public String toString() {
    return getClass().getSimpleName() + "(" + delegate.toString() + ")";
  }
}
