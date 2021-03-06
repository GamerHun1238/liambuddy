package okio;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public abstract interface Sink
  extends Closeable, Flushable
{
  public abstract void write(Buffer paramBuffer, long paramLong)
    throws IOException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract Timeout timeout();
  
  public abstract void close()
    throws IOException;
}
