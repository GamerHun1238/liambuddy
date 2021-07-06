package okhttp3.internal.http;

import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

















public final class RealResponseBody
  extends ResponseBody
{
  @Nullable
  private final String contentTypeString;
  private final long contentLength;
  private final BufferedSource source;
  
  public RealResponseBody(@Nullable String contentTypeString, long contentLength, BufferedSource source)
  {
    this.contentTypeString = contentTypeString;
    this.contentLength = contentLength;
    this.source = source;
  }
  
  public MediaType contentType() {
    return contentTypeString != null ? MediaType.parse(contentTypeString) : null;
  }
  
  public long contentLength() {
    return contentLength;
  }
  
  public BufferedSource source() {
    return source;
  }
}
