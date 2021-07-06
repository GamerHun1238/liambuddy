package net.dv8tion.jda.internal.utils;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

















public class BufferedRequestBody
  extends RequestBody
{
  private final Source source;
  private final MediaType type;
  private byte[] data;
  
  public BufferedRequestBody(Source source, MediaType type)
  {
    this.source = source;
    this.type = type;
  }
  

  @Nullable
  public MediaType contentType()
  {
    return type;
  }
  
  public void writeTo(@Nonnull BufferedSink sink)
    throws IOException
  {
    if (data != null)
    {
      sink.write(data);
      return;
    }
    
    BufferedSource s = Okio.buffer(source);
    try {
      data = s.readByteArray();
      sink.write(data);
      if (s == null) return; s.close();
    }
    catch (Throwable localThrowable)
    {
      if (s == null) break label83; } try { s.close(); } catch (Throwable localThrowable1) { localThrowable.addSuppressed(localThrowable1); } label83: throw localThrowable;
  }
}
