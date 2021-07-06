package okio;

import java.io.IOException;














public abstract class ForwardingSource
  implements Source
{
  private final Source delegate;
  
  public ForwardingSource(Source delegate)
  {
    if (delegate == null) throw new IllegalArgumentException("delegate == null");
    this.delegate = delegate;
  }
  
  public final Source delegate()
  {
    return delegate;
  }
  
  public long read(Buffer sink, long byteCount) throws IOException {
    return delegate.read(sink, byteCount);
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
