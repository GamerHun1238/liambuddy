package okhttp3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Source;
















public abstract class RequestBody
{
  public RequestBody() {}
  
  @Nullable
  public abstract MediaType contentType();
  
  public long contentLength()
    throws IOException
  {
    return -1L;
  }
  


  public abstract void writeTo(BufferedSink paramBufferedSink)
    throws IOException;
  

  public static RequestBody create(@Nullable MediaType contentType, String content)
  {
    Charset charset = StandardCharsets.UTF_8;
    if (contentType != null) {
      charset = contentType.charset();
      if (charset == null) {
        charset = StandardCharsets.UTF_8;
        contentType = MediaType.parse(contentType + "; charset=utf-8");
      }
    }
    byte[] bytes = content.getBytes(charset);
    return create(contentType, bytes);
  }
  

  public static RequestBody create(@Nullable MediaType contentType, final ByteString content)
  {
    new RequestBody() {
      @Nullable
      public MediaType contentType() { return RequestBody.this; }
      
      public long contentLength() throws IOException
      {
        return content.size();
      }
      
      public void writeTo(BufferedSink sink) throws IOException {
        sink.write(content);
      }
    };
  }
  
  public static RequestBody create(@Nullable MediaType contentType, byte[] content)
  {
    return create(contentType, content, 0, content.length);
  }
  

  public static RequestBody create(@Nullable MediaType contentType, final byte[] content, final int offset, final int byteCount)
  {
    if (content == null) throw new NullPointerException("content == null");
    Util.checkOffsetAndCount(content.length, offset, byteCount);
    new RequestBody() {
      @Nullable
      public MediaType contentType() { return RequestBody.this; }
      
      public long contentLength()
      {
        return byteCount;
      }
      
      public void writeTo(BufferedSink sink) throws IOException {
        sink.write(content, offset, byteCount);
      }
    };
  }
  
  public static RequestBody create(@Nullable MediaType contentType, final File file)
  {
    if (file == null) { throw new NullPointerException("file == null");
    }
    new RequestBody() {
      @Nullable
      public MediaType contentType() { return RequestBody.this; }
      
      public long contentLength()
      {
        return file.length();
      }
      
      public void writeTo(BufferedSink sink) throws IOException {
        Source source = Okio.source(file);Throwable localThrowable3 = null;
        try { sink.writeAll(source);
        }
        catch (Throwable localThrowable1)
        {
          localThrowable3 = localThrowable1;throw localThrowable1;
        } finally {
          if (source != null) if (localThrowable3 != null) try { source.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else source.close();
        }
      }
    };
  }
}
