package okhttp3.internal.cache;

import java.io.IOException;
import okio.Buffer;
import okio.ForwardingSink;
import okio.Sink;














class FaultHidingSink
  extends ForwardingSink
{
  private boolean hasErrors;
  
  FaultHidingSink(Sink delegate)
  {
    super(delegate);
  }
  
  public void write(Buffer source, long byteCount) throws IOException {
    if (hasErrors) {
      source.skip(byteCount);
      return;
    }
    try {
      super.write(source, byteCount);
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }
  
  public void flush() throws IOException {
    if (hasErrors) return;
    try {
      super.flush();
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }
  
  public void close() throws IOException {
    if (hasErrors) return;
    try {
      super.close();
    } catch (IOException e) {
      hasErrors = true;
      onException(e);
    }
  }
  
  protected void onException(IOException e) {}
}
