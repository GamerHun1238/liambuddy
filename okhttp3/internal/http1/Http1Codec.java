package okhttp3.internal.http1;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingTimeout;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;




































public final class Http1Codec
  implements HttpCodec
{
  private static final int STATE_IDLE = 0;
  private static final int STATE_OPEN_REQUEST_BODY = 1;
  private static final int STATE_WRITING_REQUEST_BODY = 2;
  private static final int STATE_READ_RESPONSE_HEADERS = 3;
  private static final int STATE_OPEN_RESPONSE_BODY = 4;
  private static final int STATE_READING_RESPONSE_BODY = 5;
  private static final int STATE_CLOSED = 6;
  private static final int HEADER_LIMIT = 262144;
  final OkHttpClient client;
  final StreamAllocation streamAllocation;
  final BufferedSource source;
  final BufferedSink sink;
  int state = 0;
  private long headerLimit = 262144L;
  


  private Headers trailers;
  


  public Http1Codec(OkHttpClient client, StreamAllocation streamAllocation, BufferedSource source, BufferedSink sink)
  {
    this.client = client;
    this.streamAllocation = streamAllocation;
    this.source = source;
    this.sink = sink;
  }
  
  public Sink createRequestBody(Request request, long contentLength) {
    if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding")))
    {
      return newChunkedSink();
    }
    
    if (contentLength != -1L)
    {
      return newFixedLengthSink(contentLength);
    }
    
    throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
  }
  
  public void cancel()
  {
    RealConnection connection = streamAllocation.connection();
    if (connection != null) { connection.cancel();
    }
  }
  







  public void writeRequestHeaders(Request request)
    throws IOException
  {
    String requestLine = RequestLine.get(request, streamAllocation
      .connection().route().proxy().type());
    writeRequest(request.headers(), requestLine);
  }
  
  public ResponseBody openResponseBody(Response response) throws IOException {
    streamAllocation.eventListener.responseBodyStart(streamAllocation.call);
    String contentType = response.header("Content-Type");
    
    if (!HttpHeaders.hasBody(response)) {
      Source source = newFixedLengthSource(0L);
      return new RealResponseBody(contentType, 0L, Okio.buffer(source));
    }
    
    if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
      Source source = newChunkedSource(response.request().url());
      return new RealResponseBody(contentType, -1L, Okio.buffer(source));
    }
    
    long contentLength = HttpHeaders.contentLength(response);
    if (contentLength != -1L) {
      Source source = newFixedLengthSource(contentLength);
      return new RealResponseBody(contentType, contentLength, Okio.buffer(source));
    }
    
    return new RealResponseBody(contentType, -1L, Okio.buffer(newUnknownLengthSource()));
  }
  
  public Headers trailers() throws IOException {
    if (state != 6) {
      throw new IllegalStateException("too early; can't read the trailers yet");
    }
    return trailers != null ? trailers : Util.EMPTY_HEADERS;
  }
  
  public boolean isClosed()
  {
    return state == 6;
  }
  
  public void flushRequest() throws IOException {
    sink.flush();
  }
  
  public void finishRequest() throws IOException {
    sink.flush();
  }
  
  public void writeRequest(Headers headers, String requestLine) throws IOException
  {
    if (state != 0) throw new IllegalStateException("state: " + state);
    sink.writeUtf8(requestLine).writeUtf8("\r\n");
    int i = 0; for (int size = headers.size(); i < size; i++)
    {


      sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
    }
    sink.writeUtf8("\r\n");
    state = 1;
  }
  
  public Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
    if ((state != 1) && (state != 3)) {
      throw new IllegalStateException("state: " + state);
    }
    try
    {
      StatusLine statusLine = StatusLine.parse(readHeaderLine());
      




      Response.Builder responseBuilder = new Response.Builder().protocol(protocol).code(code).message(message).headers(readHeaders());
      
      if ((expectContinue) && (code == 100))
        return null;
      if (code == 100) {
        state = 3;
        return responseBuilder;
      }
      
      state = 4;
      return responseBuilder;
    }
    catch (EOFException e) {
      throw new IOException("unexpected end of stream on " + streamAllocation, e);
    }
  }
  
  private String readHeaderLine() throws IOException {
    String line = source.readUtf8LineStrict(headerLimit);
    headerLimit -= line.length();
    return line;
  }
  
  public Headers readHeaders() throws IOException
  {
    Headers.Builder headers = new Headers.Builder();
    String line;
    while ((line = readHeaderLine()).length() != 0) {
      Internal.instance.addLenient(headers, line);
    }
    return headers.build();
  }
  
  public Sink newChunkedSink() {
    if (state != 1) throw new IllegalStateException("state: " + state);
    state = 2;
    return new ChunkedSink();
  }
  
  public Sink newFixedLengthSink(long contentLength) {
    if (state != 1) throw new IllegalStateException("state: " + state);
    state = 2;
    return new FixedLengthSink(contentLength);
  }
  
  public Source newFixedLengthSource(long length) throws IOException {
    if (state != 4) throw new IllegalStateException("state: " + state);
    state = 5;
    return new FixedLengthSource(length);
  }
  
  public Source newChunkedSource(HttpUrl url) throws IOException {
    if (state != 4) throw new IllegalStateException("state: " + state);
    state = 5;
    return new ChunkedSource(url);
  }
  
  public Source newUnknownLengthSource() throws IOException {
    if (state != 4) throw new IllegalStateException("state: " + state);
    if (streamAllocation == null) throw new IllegalStateException("streamAllocation == null");
    state = 5;
    streamAllocation.noNewStreams();
    return new UnknownLengthSource();
  }
  




  void detachTimeout(ForwardingTimeout timeout)
  {
    Timeout oldDelegate = timeout.delegate();
    timeout.setDelegate(Timeout.NONE);
    oldDelegate.clearDeadline();
    oldDelegate.clearTimeout();
  }
  
  private final class FixedLengthSink implements Sink
  {
    private final ForwardingTimeout timeout = new ForwardingTimeout(sink.timeout());
    private boolean closed;
    private long bytesRemaining;
    
    FixedLengthSink(long bytesRemaining) {
      this.bytesRemaining = bytesRemaining;
    }
    
    public Timeout timeout() {
      return timeout;
    }
    
    public void write(Buffer source, long byteCount) throws IOException {
      if (closed) throw new IllegalStateException("closed");
      Util.checkOffsetAndCount(source.size(), 0L, byteCount);
      if (byteCount > bytesRemaining) {
        throw new ProtocolException("expected " + bytesRemaining + " bytes but received " + byteCount);
      }
      
      sink.write(source, byteCount);
      bytesRemaining -= byteCount;
    }
    
    public void flush() throws IOException {
      if (closed) return;
      sink.flush();
    }
    
    public void close() throws IOException {
      if (closed) return;
      closed = true;
      if (bytesRemaining > 0L) throw new ProtocolException("unexpected end of stream");
      detachTimeout(timeout);
      state = 3;
    }
  }
  


  private final class ChunkedSink
    implements Sink
  {
    private final ForwardingTimeout timeout = new ForwardingTimeout(sink.timeout());
    private boolean closed;
    
    ChunkedSink() {}
    
    public Timeout timeout()
    {
      return timeout;
    }
    
    public void write(Buffer source, long byteCount) throws IOException {
      if (closed) throw new IllegalStateException("closed");
      if (byteCount == 0L) { return;
      }
      sink.writeHexadecimalUnsignedLong(byteCount);
      sink.writeUtf8("\r\n");
      sink.write(source, byteCount);
      sink.writeUtf8("\r\n");
    }
    
    public synchronized void flush() throws IOException {
      if (closed) return;
      sink.flush();
    }
    
    public synchronized void close() throws IOException {
      if (closed) return;
      closed = true;
      sink.writeUtf8("0\r\n\r\n");
      detachTimeout(timeout);
      state = 3;
    } }
  
  private abstract class AbstractSource implements Source { private AbstractSource() {}
    
    protected final ForwardingTimeout timeout = new ForwardingTimeout(source.timeout());
    protected boolean closed;
    protected long bytesRead = 0L;
    
    public Timeout timeout() {
      return timeout;
    }
    
    public long read(Buffer sink, long byteCount) throws IOException {
      try {
        long read = source.read(sink, byteCount);
        if (read > 0L) {
          bytesRead += read;
        }
        return read;
      } catch (IOException e) {
        endOfInput(false, e);
        throw e;
      }
    }
    


    protected final void endOfInput(boolean reuseConnection, IOException e)
      throws IOException
    {
      if (state == 6) return;
      if (state != 5) { throw new IllegalStateException("state: " + state);
      }
      detachTimeout(timeout);
      
      state = 6;
      if (streamAllocation != null) {
        streamAllocation.streamFinished(!reuseConnection, Http1Codec.this, bytesRead, e);
      }
    }
  }
  
  private class FixedLengthSource extends Http1Codec.AbstractSource {
    private long bytesRemaining;
    
    FixedLengthSource(long length) throws IOException {
      super(null);
      bytesRemaining = length;
      if (bytesRemaining == 0L) {
        endOfInput(true, null);
      }
    }
    
    public long read(Buffer sink, long byteCount) throws IOException {
      if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (bytesRemaining == 0L) { return -1L;
      }
      long read = super.read(sink, Math.min(bytesRemaining, byteCount));
      if (read == -1L) {
        ProtocolException e = new ProtocolException("unexpected end of stream");
        endOfInput(false, e);
        throw e;
      }
      
      bytesRemaining -= read;
      if (bytesRemaining == 0L) {
        endOfInput(true, null);
      }
      return read;
    }
    
    public void close() throws IOException {
      if (closed) { return;
      }
      if ((bytesRemaining != 0L) && (!Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
        endOfInput(false, null);
      }
      
      closed = true;
    }
  }
  
  private class ChunkedSource extends Http1Codec.AbstractSource
  {
    private static final long NO_CHUNK_YET = -1L;
    private final HttpUrl url;
    private long bytesRemainingInChunk = -1L;
    private boolean hasMoreChunks = true;
    
    ChunkedSource(HttpUrl url) { super(null);
      this.url = url;
    }
    
    public long read(Buffer sink, long byteCount) throws IOException {
      if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (!hasMoreChunks) { return -1L;
      }
      if ((bytesRemainingInChunk == 0L) || (bytesRemainingInChunk == -1L)) {
        readChunkSize();
        if (!hasMoreChunks) { return -1L;
        }
      }
      long read = super.read(sink, Math.min(byteCount, bytesRemainingInChunk));
      if (read == -1L) {
        ProtocolException e = new ProtocolException("unexpected end of stream");
        endOfInput(false, e);
        throw e;
      }
      bytesRemainingInChunk -= read;
      return read;
    }
    
    private void readChunkSize() throws IOException
    {
      if (bytesRemainingInChunk != -1L) {
        source.readUtf8LineStrict();
      }
      try {
        bytesRemainingInChunk = source.readHexadecimalUnsignedLong();
        String extensions = source.readUtf8LineStrict().trim();
        if ((bytesRemainingInChunk < 0L) || ((!extensions.isEmpty()) && (!extensions.startsWith(";")))) {
          throw new ProtocolException("expected chunk size and optional extensions but was \"" + bytesRemainingInChunk + extensions + "\"");
        }
      }
      catch (NumberFormatException e) {
        throw new ProtocolException(e.getMessage());
      }
      if (bytesRemainingInChunk == 0L) {
        hasMoreChunks = false;
        trailers = readHeaders();
        HttpHeaders.receiveHeaders(client.cookieJar(), url, trailers);
        endOfInput(true, null);
      }
    }
    
    public void close() throws IOException {
      if (closed) return;
      if ((hasMoreChunks) && (!Util.discard(this, 100, TimeUnit.MILLISECONDS))) {
        endOfInput(false, null);
      }
      closed = true;
    }
  }
  
  private class UnknownLengthSource extends Http1Codec.AbstractSource {
    private boolean inputExhausted;
    
    UnknownLengthSource() {
      super(null);
    }
    
    public long read(Buffer sink, long byteCount) throws IOException
    {
      if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      if (closed) throw new IllegalStateException("closed");
      if (inputExhausted) { return -1L;
      }
      long read = super.read(sink, byteCount);
      if (read == -1L) {
        inputExhausted = true;
        endOfInput(true, null);
        return -1L;
      }
      return read;
    }
    
    public void close() throws IOException {
      if (closed) return;
      if (!inputExhausted) {
        endOfInput(false, null);
      }
      closed = true;
    }
  }
}
