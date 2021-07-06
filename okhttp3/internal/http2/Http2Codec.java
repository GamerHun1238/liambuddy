package okhttp3.internal.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;























public final class Http2Codec
  implements HttpCodec
{
  private static final String CONNECTION = "connection";
  private static final String HOST = "host";
  private static final String KEEP_ALIVE = "keep-alive";
  private static final String PROXY_CONNECTION = "proxy-connection";
  private static final String TRANSFER_ENCODING = "transfer-encoding";
  private static final String TE = "te";
  private static final String ENCODING = "encoding";
  private static final String UPGRADE = "upgrade";
  private static final List<String> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(new String[] { "connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade", ":method", ":path", ":scheme", ":authority" });
  











  private static final List<String> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(new String[] { "connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade" });
  

  private final Interceptor.Chain chain;
  

  final StreamAllocation streamAllocation;
  
  private final Http2Connection connection;
  
  private volatile Http2Stream stream;
  
  private final Protocol protocol;
  
  private volatile boolean canceled;
  

  public Http2Codec(OkHttpClient client, Interceptor.Chain chain, StreamAllocation streamAllocation, Http2Connection connection)
  {
    this.chain = chain;
    this.streamAllocation = streamAllocation;
    this.connection = connection;
    

    protocol = (client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE) ? Protocol.H2_PRIOR_KNOWLEDGE : Protocol.HTTP_2);
  }
  
  public Sink createRequestBody(Request request, long contentLength) {
    return stream.getSink();
  }
  
  public void writeRequestHeaders(Request request) throws IOException {
    if (stream != null) { return;
    }
    boolean hasRequestBody = request.body() != null;
    List<Header> requestHeaders = http2HeadersList(request);
    stream = connection.newStream(requestHeaders, hasRequestBody);
    

    if (canceled) {
      stream.closeLater(ErrorCode.CANCEL);
      throw new IOException("Canceled");
    }
    stream.readTimeout().timeout(chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    stream.writeTimeout().timeout(chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
  }
  
  public void flushRequest() throws IOException {
    connection.flush();
  }
  
  public void finishRequest() throws IOException {
    stream.getSink().close();
  }
  
  public Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
    Headers headers = stream.takeHeaders();
    Response.Builder responseBuilder = readHttp2HeadersList(headers, protocol);
    if ((expectContinue) && (Internal.instance.code(responseBuilder) == 100)) {
      return null;
    }
    return responseBuilder;
  }
  
  public static List<Header> http2HeadersList(Request request) {
    Headers headers = request.headers();
    List<Header> result = new ArrayList(headers.size() + 4);
    result.add(new Header(Header.TARGET_METHOD, request.method()));
    result.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
    String host = request.header("Host");
    if (host != null) {
      result.add(new Header(Header.TARGET_AUTHORITY, host));
    }
    result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
    
    int i = 0; for (int size = headers.size(); i < size; i++)
    {
      String name = headers.name(i).toLowerCase(Locale.US);
      if ((!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name)) || (
        (name.equals("te")) && (headers.value(i).equals("trailers")))) {
        result.add(new Header(name, headers.value(i)));
      }
    }
    return result;
  }
  
  public static Response.Builder readHttp2HeadersList(Headers headerBlock, Protocol protocol)
    throws IOException
  {
    StatusLine statusLine = null;
    Headers.Builder headersBuilder = new Headers.Builder();
    int i = 0; for (int size = headerBlock.size(); i < size; i++) {
      String name = headerBlock.name(i);
      String value = headerBlock.value(i);
      if (name.equals(":status")) {
        statusLine = StatusLine.parse("HTTP/1.1 " + value);
      } else if (!HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
        Internal.instance.addLenient(headersBuilder, name, value);
      }
    }
    if (statusLine == null) { throw new ProtocolException("Expected ':status' header not present");
    }
    return 
    


      new Response.Builder().protocol(protocol).code(code).message(message).headers(headersBuilder.build());
  }
  
  public ResponseBody openResponseBody(Response response) throws IOException {
    streamAllocation.eventListener.responseBodyStart(streamAllocation.call);
    String contentType = response.header("Content-Type");
    long contentLength = HttpHeaders.contentLength(response);
    Source source = new StreamFinishingSource(stream.getSource());
    return new RealResponseBody(contentType, contentLength, Okio.buffer(source));
  }
  
  public Headers trailers() throws IOException {
    return stream.trailers();
  }
  
  public void cancel() {
    canceled = true;
    if (stream != null) stream.closeLater(ErrorCode.CANCEL);
  }
  
  class StreamFinishingSource extends ForwardingSource {
    boolean completed = false;
    long bytesRead = 0L;
    
    StreamFinishingSource(Source delegate) {
      super();
    }
    
    public long read(Buffer sink, long byteCount) throws IOException {
      try {
        long read = delegate().read(sink, byteCount);
        if (read > 0L) {
          bytesRead += read;
        }
        return read;
      } catch (IOException e) {
        endOfInput(e);
        throw e;
      }
    }
    
    public void close() throws IOException {
      super.close();
      endOfInput(null);
    }
    
    private void endOfInput(IOException e) {
      if (completed) return;
      completed = true;
      streamAllocation.streamFinished(false, Http2Codec.this, bytesRead, e);
    }
  }
}
