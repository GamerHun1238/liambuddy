package okhttp3.internal.http;

import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okio.Sink;

public abstract interface HttpCodec
{
  public static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;
  
  public abstract Sink createRequestBody(Request paramRequest, long paramLong);
  
  public abstract void writeRequestHeaders(Request paramRequest)
    throws IOException;
  
  public abstract void flushRequest()
    throws IOException;
  
  public abstract void finishRequest()
    throws IOException;
  
  public abstract Response.Builder readResponseHeaders(boolean paramBoolean)
    throws IOException;
  
  public abstract ResponseBody openResponseBody(Response paramResponse)
    throws IOException;
  
  public abstract Headers trailers()
    throws IOException;
  
  public abstract void cancel();
}
