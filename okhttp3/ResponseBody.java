package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;






















































































public abstract class ResponseBody
  implements Closeable
{
  @Nullable
  private Reader reader;
  
  public ResponseBody() {}
  
  @Nullable
  public abstract MediaType contentType();
  
  public abstract long contentLength();
  
  public final InputStream byteStream()
  {
    return source().inputStream();
  }
  



  public abstract BufferedSource source();
  


  public final byte[] bytes()
    throws IOException
  {
    long contentLength = contentLength();
    if (contentLength > 2147483647L) {
      throw new IOException("Cannot buffer entire body for content length: " + contentLength);
    }
    

    BufferedSource source = source();Throwable localThrowable1 = null;
    try { bytes = source.readByteArray();
    }
    catch (Throwable localThrowable)
    {
      byte[] bytes;
      localThrowable1 = localThrowable;throw localThrowable;
    } finally {
      if (source != null) $closeResource(localThrowable1, source); }
    byte[] bytes; if ((contentLength != -1L) && (contentLength != bytes.length)) {
      throw new IOException("Content-Length (" + contentLength + ") and stream length (" + bytes.length + ") disagree");
    }
    



    return bytes;
  }
  










  public final Reader charStream()
  {
    Reader r = reader;
    return this.reader = new BomAwareReader(source(), charset());
  }
  













  public final String string()
    throws IOException
  {
    BufferedSource source = source();Throwable localThrowable1 = null;
    try { Charset charset = Util.bomAwareCharset(source, charset());
      return source.readString(charset);
    }
    catch (Throwable localThrowable)
    {
      localThrowable1 = localThrowable;throw localThrowable;
    }
    finally {
      if (source != null) $closeResource(localThrowable1, source);
    }
  }
  
  private Charset charset() { MediaType contentType = contentType();
    return contentType != null ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
  }
  
  public void close() {
    Util.closeQuietly(source());
  }
  



  public static ResponseBody create(@Nullable MediaType contentType, String content)
  {
    Charset charset = StandardCharsets.UTF_8;
    if (contentType != null) {
      charset = contentType.charset();
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
        contentType = MediaType.parse(contentType + "; charset=utf-8");
      }
    }
    Buffer buffer = new Buffer().writeString(content, charset);
    return create(contentType, buffer.size(), buffer);
  }
  
  public static ResponseBody create(@Nullable MediaType contentType, byte[] content)
  {
    Buffer buffer = new Buffer().write(content);
    return create(contentType, content.length, buffer);
  }
  
  public static ResponseBody create(@Nullable MediaType contentType, ByteString content)
  {
    Buffer buffer = new Buffer().write(content);
    return create(contentType, content.size(), buffer);
  }
  

  public static ResponseBody create(@Nullable MediaType contentType, final long contentLength, BufferedSource content)
  {
    if (content == null) throw new NullPointerException("source == null");
    new ResponseBody() {
      @Nullable
      public MediaType contentType() { return ResponseBody.this; }
      
      public long contentLength()
      {
        return contentLength;
      }
      
      public BufferedSource source() {
        return val$content;
      }
    };
  }
  
  static final class BomAwareReader extends Reader {
    private final BufferedSource source;
    private final Charset charset;
    private boolean closed;
    @Nullable
    private Reader delegate;
    
    BomAwareReader(BufferedSource source, Charset charset) {
      this.source = source;
      this.charset = charset;
    }
    
    public int read(char[] cbuf, int off, int len) throws IOException {
      if (closed) { throw new IOException("Stream closed");
      }
      Reader delegate = this.delegate;
      if (delegate == null) {
        Charset charset = Util.bomAwareCharset(source, this.charset);
        delegate = this.delegate = new InputStreamReader(source.inputStream(), charset);
      }
      return delegate.read(cbuf, off, len);
    }
    
    public void close() throws IOException {
      closed = true;
      if (delegate != null) {
        delegate.close();
      } else {
        source.close();
      }
    }
  }
}
