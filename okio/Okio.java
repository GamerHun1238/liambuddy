package okio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;


















public final class Okio
{
  static final Logger logger = Logger.getLogger(Okio.class.getName());
  



  private Okio() {}
  


  public static BufferedSource buffer(Source source)
  {
    return new RealBufferedSource(source);
  }
  




  public static BufferedSink buffer(Sink sink)
  {
    return new RealBufferedSink(sink);
  }
  
  public static Sink sink(OutputStream out)
  {
    return sink(out, new Timeout());
  }
  
  private static Sink sink(final OutputStream out, Timeout timeout) {
    if (out == null) throw new IllegalArgumentException("out == null");
    if (timeout == null) { throw new IllegalArgumentException("timeout == null");
    }
    new Sink() {
      public void write(Buffer source, long byteCount) throws IOException {
        Util.checkOffsetAndCount(size, 0L, byteCount);
        while (byteCount > 0L) {
          throwIfReached();
          Segment head = head;
          int toCopy = (int)Math.min(byteCount, limit - pos);
          out.write(data, pos, toCopy);
          
          pos += toCopy;
          byteCount -= toCopy;
          size -= toCopy;
          
          if (pos == limit) {
            head = head.pop();
            SegmentPool.recycle(head);
          }
        }
      }
      
      public void flush() throws IOException {
        out.flush();
      }
      
      public void close() throws IOException {
        out.close();
      }
      
      public Timeout timeout() {
        return Okio.this;
      }
      
      public String toString() {
        return "sink(" + out + ")";
      }
    };
  }
  



  public static Sink sink(Socket socket)
    throws IOException
  {
    if (socket == null) throw new IllegalArgumentException("socket == null");
    if (socket.getOutputStream() == null) throw new IOException("socket's output stream == null");
    AsyncTimeout timeout = timeout(socket);
    Sink sink = sink(socket.getOutputStream(), timeout);
    return timeout.sink(sink);
  }
  
  public static Source source(InputStream in)
  {
    return source(in, new Timeout());
  }
  
  private static Source source(final InputStream in, Timeout timeout) {
    if (in == null) throw new IllegalArgumentException("in == null");
    if (timeout == null) { throw new IllegalArgumentException("timeout == null");
    }
    new Source() {
      public long read(Buffer sink, long byteCount) throws IOException {
        if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        if (byteCount == 0L) return 0L;
        try {
          throwIfReached();
          Segment tail = sink.writableSegment(1);
          int maxToCopy = (int)Math.min(byteCount, 8192 - limit);
          int bytesRead = in.read(data, limit, maxToCopy);
          if (bytesRead == -1) return -1L;
          limit += bytesRead;
          size += bytesRead;
          return bytesRead;
        } catch (AssertionError e) {
          if (Okio.isAndroidGetsocknameError(e)) throw new IOException(e);
          throw e;
        }
      }
      
      public void close() throws IOException {
        in.close();
      }
      
      public Timeout timeout() {
        return Okio.this;
      }
      
      public String toString() {
        return "source(" + in + ")";
      }
    };
  }
  
  public static Source source(File file) throws FileNotFoundException
  {
    if (file == null) throw new IllegalArgumentException("file == null");
    return source(new FileInputStream(file));
  }
  
  @IgnoreJRERequirement
  public static Source source(Path path, OpenOption... options) throws IOException
  {
    if (path == null) throw new IllegalArgumentException("path == null");
    return source(Files.newInputStream(path, options));
  }
  
  public static Sink sink(File file) throws FileNotFoundException
  {
    if (file == null) throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(file));
  }
  
  public static Sink appendingSink(File file) throws FileNotFoundException
  {
    if (file == null) throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(file, true));
  }
  
  @IgnoreJRERequirement
  public static Sink sink(Path path, OpenOption... options) throws IOException
  {
    if (path == null) throw new IllegalArgumentException("path == null");
    return sink(Files.newOutputStream(path, options));
  }
  
  public static Sink blackhole()
  {
    new Sink() {
      public void write(Buffer source, long byteCount) throws IOException {
        source.skip(byteCount);
      }
      
      public void flush() throws IOException
      {}
      
      public Timeout timeout() {
        return Timeout.NONE;
      }
      

      public void close()
        throws IOException
      {}
    };
  }
  

  public static Source source(Socket socket)
    throws IOException
  {
    if (socket == null) throw new IllegalArgumentException("socket == null");
    if (socket.getInputStream() == null) throw new IOException("socket's input stream == null");
    AsyncTimeout timeout = timeout(socket);
    Source source = source(socket.getInputStream(), timeout);
    return timeout.source(source);
  }
  
  private static AsyncTimeout timeout(Socket socket) {
    new AsyncTimeout() {
      protected IOException newTimeoutException(@Nullable IOException cause) {
        InterruptedIOException ioe = new SocketTimeoutException("timeout");
        if (cause != null) {
          ioe.initCause(cause);
        }
        return ioe;
      }
      
      protected void timedOut() {
        try {
          close();
        } catch (Exception e) {
          Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + Okio.this, e);
        } catch (AssertionError e) {
          if (Okio.isAndroidGetsocknameError(e))
          {

            Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + Okio.this, e);
          } else {
            throw e;
          }
        }
      }
    };
  }
  



  static boolean isAndroidGetsocknameError(AssertionError e)
  {
    return (e.getCause() != null) && (e.getMessage() != null) && 
      (e.getMessage().contains("getsockname failed"));
  }
}
