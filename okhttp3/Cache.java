package okhttp3;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.cache.DiskLruCache.Editor;
import okhttp3.internal.cache.DiskLruCache.Snapshot;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;


































































































public final class Cache
  implements Closeable, Flushable
{
  private static final int VERSION = 201105;
  private static final int ENTRY_METADATA = 0;
  private static final int ENTRY_BODY = 1;
  private static final int ENTRY_COUNT = 2;
  final InternalCache internalCache = new InternalCache() {
    @Nullable
    public Response get(Request request) throws IOException { return Cache.this.get(request); }
    
    @Nullable
    public CacheRequest put(Response response) throws IOException {
      return Cache.this.put(response);
    }
    
    public void remove(Request request) throws IOException {
      Cache.this.remove(request);
    }
    
    public void update(Response cached, Response network) {
      Cache.this.update(cached, network);
    }
    
    public void trackConditionalCacheHit() {
      Cache.this.trackConditionalCacheHit();
    }
    
    public void trackResponse(CacheStrategy cacheStrategy) {
      Cache.this.trackResponse(cacheStrategy);
    }
  };
  
  final DiskLruCache cache;
  
  int writeSuccessCount;
  
  int writeAbortCount;
  
  private int networkCount;
  
  private int hitCount;
  private int requestCount;
  
  public Cache(File directory, long maxSize)
  {
    this(directory, maxSize, FileSystem.SYSTEM);
  }
  
  Cache(File directory, long maxSize, FileSystem fileSystem) {
    cache = DiskLruCache.create(fileSystem, directory, 201105, 2, maxSize);
  }
  

  public static String key(HttpUrl url) { return ByteString.encodeUtf8(url.toString()).md5().hex(); }
  
  @Nullable
  Response get(Request request) {
    String key = key(request.url());
    
    try
    {
      DiskLruCache.Snapshot snapshot = cache.get(key);
      if (snapshot == null) {
        return null;
      }
    }
    catch (IOException e) {
      return null;
    }
    DiskLruCache.Snapshot snapshot;
    try {
      entry = new Entry(snapshot.getSource(0));
    } catch (IOException e) { Entry entry;
      Util.closeQuietly(snapshot);
      return null;
    }
    Entry entry;
    Response response = entry.response(snapshot);
    
    if (!entry.matches(request, response)) {
      Util.closeQuietly(response.body());
      return null;
    }
    
    return response;
  }
  
  @Nullable
  CacheRequest put(Response response) { String requestMethod = response.request().method();
    
    if (HttpMethod.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      }
      catch (IOException localIOException1) {}
      
      return null;
    }
    if (!requestMethod.equals("GET"))
    {


      return null;
    }
    
    if (HttpHeaders.hasVaryAll(response)) {
      return null;
    }
    
    Entry entry = new Entry(response);
    DiskLruCache.Editor editor = null;
    try {
      editor = cache.edit(key(response.request().url()));
      if (editor == null) {
        return null;
      }
      entry.writeTo(editor);
      return new CacheRequestImpl(editor);
    } catch (IOException e) {
      abortQuietly(editor); }
    return null;
  }
  
  void remove(Request request) throws IOException
  {
    cache.remove(key(request.url()));
  }
  
  void update(Response cached, Response network) {
    Entry entry = new Entry(network);
    DiskLruCache.Snapshot snapshot = bodysnapshot;
    DiskLruCache.Editor editor = null;
    try {
      editor = snapshot.edit();
      if (editor != null) {
        entry.writeTo(editor);
        editor.commit();
      }
    } catch (IOException e) {
      abortQuietly(editor);
    }
  }
  
  private void abortQuietly(@Nullable DiskLruCache.Editor editor)
  {
    try {
      if (editor != null) {
        editor.abort();
      }
    }
    catch (IOException localIOException) {}
  }
  









  public void initialize()
    throws IOException
  {
    cache.initialize();
  }
  


  public void delete()
    throws IOException
  {
    cache.delete();
  }
  


  public void evictAll()
    throws IOException
  {
    cache.evictAll();
  }
  







  public Iterator<String> urls()
    throws IOException
  {
    new Iterator() {
      final Iterator<DiskLruCache.Snapshot> delegate = cache.snapshots();
      @Nullable
      String nextUrl;
      boolean canRemove;
      
      public boolean hasNext() {
        if (nextUrl != null) { return true;
        }
        canRemove = false;
        for (;;) { if (delegate.hasNext()) {
            try { DiskLruCache.Snapshot snapshot = (DiskLruCache.Snapshot)delegate.next();Throwable localThrowable3 = null;
              try { BufferedSource metadata = Okio.buffer(snapshot.getSource(0));
                nextUrl = metadata.readUtf8LineStrict();
                return true;
              }
              catch (Throwable localThrowable1)
              {
                localThrowable3 = localThrowable1;throw localThrowable1;
              }
              finally
              {
                if (snapshot != null) if (localThrowable3 != null) try { snapshot.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else snapshot.close();
              }
            }
            catch (IOException localIOException) {}
          }
        }
        return false;
      }
      
      public String next() {
        if (!hasNext()) throw new NoSuchElementException();
        String result = nextUrl;
        nextUrl = null;
        canRemove = true;
        return result;
      }
      
      public void remove() {
        if (!canRemove) throw new IllegalStateException("remove() before next()");
        delegate.remove();
      }
    };
  }
  
  public synchronized int writeAbortCount() {
    return writeAbortCount;
  }
  
  public synchronized int writeSuccessCount() {
    return writeSuccessCount;
  }
  
  public long size() throws IOException {
    return cache.size();
  }
  
  public long maxSize()
  {
    return cache.getMaxSize();
  }
  
  public void flush() throws IOException {
    cache.flush();
  }
  
  public void close() throws IOException {
    cache.close();
  }
  
  public File directory() {
    return cache.getDirectory();
  }
  
  public boolean isClosed() {
    return cache.isClosed();
  }
  
  synchronized void trackResponse(CacheStrategy cacheStrategy) {
    requestCount += 1;
    
    if (networkRequest != null)
    {
      networkCount += 1;
    } else if (cacheResponse != null)
    {
      hitCount += 1;
    }
  }
  
  synchronized void trackConditionalCacheHit() {
    hitCount += 1;
  }
  
  public synchronized int networkCount() {
    return networkCount;
  }
  
  public synchronized int hitCount() {
    return hitCount;
  }
  
  public synchronized int requestCount() {
    return requestCount;
  }
  
  private final class CacheRequestImpl implements CacheRequest {
    private final DiskLruCache.Editor editor;
    private Sink cacheOut;
    private Sink body;
    boolean done;
    
    CacheRequestImpl(final DiskLruCache.Editor editor) {
      this.editor = editor;
      cacheOut = editor.newSink(1);
      body = new ForwardingSink(cacheOut) {
        public void close() throws IOException {
          synchronized (Cache.this) {
            if (done) {
              return;
            }
            done = true;
            writeSuccessCount += 1;
          }
          super.close();
          editor.commit();
        }
      };
    }
    
    public void abort() {
      synchronized (Cache.this) {
        if (done) {
          return;
        }
        done = true;
        writeAbortCount += 1;
      }
      Util.closeQuietly(cacheOut);
      try {
        editor.abort();
      }
      catch (IOException localIOException) {}
    }
    
    public Sink body() {
      return body;
    }
  }
  
  private static final class Entry
  {
    private static final String SENT_MILLIS = Platform.get().getPrefix() + "-Sent-Millis";
    

    private static final String RECEIVED_MILLIS = Platform.get().getPrefix() + "-Received-Millis";
    




    private final String url;
    



    private final Headers varyHeaders;
    



    private final String requestMethod;
    



    private final Protocol protocol;
    



    private final int code;
    



    private final String message;
    



    private final Headers responseHeaders;
    



    @Nullable
    private final Handshake handshake;
    



    private final long sentRequestMillis;
    



    private final long receivedResponseMillis;
    




    Entry(Source in)
      throws IOException
    {
      try
      {
        BufferedSource source = Okio.buffer(in);
        url = source.readUtf8LineStrict();
        requestMethod = source.readUtf8LineStrict();
        Headers.Builder varyHeadersBuilder = new Headers.Builder();
        int varyRequestHeaderLineCount = Cache.readInt(source);
        for (int i = 0; i < varyRequestHeaderLineCount; i++) {
          varyHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        varyHeaders = varyHeadersBuilder.build();
        
        StatusLine statusLine = StatusLine.parse(source.readUtf8LineStrict());
        protocol = protocol;
        code = code;
        message = message;
        Headers.Builder responseHeadersBuilder = new Headers.Builder();
        int responseHeaderLineCount = Cache.readInt(source);
        for (int i = 0; i < responseHeaderLineCount; i++) {
          responseHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        String sendRequestMillisString = responseHeadersBuilder.get(SENT_MILLIS);
        String receivedResponseMillisString = responseHeadersBuilder.get(RECEIVED_MILLIS);
        responseHeadersBuilder.removeAll(SENT_MILLIS);
        responseHeadersBuilder.removeAll(RECEIVED_MILLIS);
        

        sentRequestMillis = (sendRequestMillisString != null ? Long.parseLong(sendRequestMillisString) : 0L);
        

        receivedResponseMillis = (receivedResponseMillisString != null ? Long.parseLong(receivedResponseMillisString) : 0L);
        responseHeaders = responseHeadersBuilder.build();
        
        if (isHttps()) {
          String blank = source.readUtf8LineStrict();
          if (blank.length() > 0) {
            throw new IOException("expected \"\" but was \"" + blank + "\"");
          }
          String cipherSuiteString = source.readUtf8LineStrict();
          CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
          List<Certificate> peerCertificates = readCertificateList(source);
          List<Certificate> localCertificates = readCertificateList(source);
          

          TlsVersion tlsVersion = !source.exhausted() ? TlsVersion.forJavaName(source.readUtf8LineStrict()) : TlsVersion.SSL_3_0;
          handshake = Handshake.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
        } else {
          handshake = null;
        }
      } finally {
        in.close();
      }
    }
    
    Entry(Response response) {
      url = response.request().url().toString();
      varyHeaders = HttpHeaders.varyHeaders(response);
      requestMethod = response.request().method();
      protocol = response.protocol();
      code = response.code();
      message = response.message();
      responseHeaders = response.headers();
      handshake = response.handshake();
      sentRequestMillis = response.sentRequestAtMillis();
      receivedResponseMillis = response.receivedResponseAtMillis();
    }
    
    public void writeTo(DiskLruCache.Editor editor) throws IOException {
      BufferedSink sink = Okio.buffer(editor.newSink(0));
      
      sink.writeUtf8(url)
        .writeByte(10);
      sink.writeUtf8(requestMethod)
        .writeByte(10);
      sink.writeDecimalLong(varyHeaders.size())
        .writeByte(10);
      int i = 0; for (int size = varyHeaders.size(); i < size; i++)
      {


        sink.writeUtf8(varyHeaders.name(i)).writeUtf8(": ").writeUtf8(varyHeaders.value(i)).writeByte(10);
      }
      

      sink.writeUtf8(new StatusLine(protocol, code, message).toString()).writeByte(10);
      sink.writeDecimalLong(responseHeaders.size() + 2)
        .writeByte(10);
      int i = 0; for (int size = responseHeaders.size(); i < size; i++)
      {


        sink.writeUtf8(responseHeaders.name(i)).writeUtf8(": ").writeUtf8(responseHeaders.value(i)).writeByte(10);
      }
      


      sink.writeUtf8(SENT_MILLIS).writeUtf8(": ").writeDecimalLong(sentRequestMillis).writeByte(10);
      sink.writeUtf8(RECEIVED_MILLIS)
        .writeUtf8(": ")
        .writeDecimalLong(receivedResponseMillis)
        .writeByte(10);
      
      if (isHttps()) {
        sink.writeByte(10);
        sink.writeUtf8(handshake.cipherSuite().javaName())
          .writeByte(10);
        writeCertList(sink, handshake.peerCertificates());
        writeCertList(sink, handshake.localCertificates());
        sink.writeUtf8(handshake.tlsVersion().javaName()).writeByte(10);
      }
      sink.close();
    }
    
    private boolean isHttps() {
      return url.startsWith("https://");
    }
    
    private List<Certificate> readCertificateList(BufferedSource source) throws IOException {
      int length = Cache.readInt(source);
      if (length == -1) return Collections.emptyList();
      try
      {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        List<Certificate> result = new ArrayList(length);
        for (int i = 0; i < length; i++) {
          String line = source.readUtf8LineStrict();
          Buffer bytes = new Buffer();
          bytes.write(ByteString.decodeBase64(line));
          result.add(certificateFactory.generateCertificate(bytes.inputStream()));
        }
        return result;
      } catch (CertificateException e) {
        throw new IOException(e.getMessage());
      }
    }
    
    private void writeCertList(BufferedSink sink, List<Certificate> certificates) throws IOException
    {
      try
      {
        sink.writeDecimalLong(certificates.size()).writeByte(10);
        int i = 0; for (int size = certificates.size(); i < size; i++) {
          byte[] bytes = ((Certificate)certificates.get(i)).getEncoded();
          String line = ByteString.of(bytes).base64();
          sink.writeUtf8(line)
            .writeByte(10);
        }
      } catch (CertificateEncodingException e) {
        throw new IOException(e.getMessage());
      }
    }
    
    public boolean matches(Request request, Response response) {
      return (url.equals(request.url().toString())) && 
        (requestMethod.equals(request.method())) && 
        (HttpHeaders.varyMatches(response, varyHeaders, request));
    }
    
    public Response response(DiskLruCache.Snapshot snapshot) {
      String contentType = responseHeaders.get("Content-Type");
      String contentLength = responseHeaders.get("Content-Length");
      



      Request cacheRequest = new Request.Builder().url(url).method(requestMethod, null).headers(varyHeaders).build();
      return new Response.Builder()
        .request(cacheRequest)
        .protocol(protocol)
        .code(code)
        .message(message)
        .headers(responseHeaders)
        .body(new Cache.CacheResponseBody(snapshot, contentType, contentLength))
        .handshake(handshake)
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(receivedResponseMillis)
        .build();
    }
  }
  
  static int readInt(BufferedSource source) throws IOException {
    try {
      long result = source.readDecimalLong();
      String line = source.readUtf8LineStrict();
      if ((result < 0L) || (result > 2147483647L) || (!line.isEmpty())) {
        throw new IOException("expected an int but was \"" + result + line + "\"");
      }
      return (int)result;
    } catch (NumberFormatException e) {
      throw new IOException(e.getMessage());
    }
  }
  
  private static class CacheResponseBody extends ResponseBody {
    final DiskLruCache.Snapshot snapshot;
    private final BufferedSource bodySource;
    @Nullable
    private final String contentType;
    @Nullable
    private final String contentLength;
    
    CacheResponseBody(final DiskLruCache.Snapshot snapshot, String contentType, String contentLength) { this.snapshot = snapshot;
      this.contentType = contentType;
      this.contentLength = contentLength;
      
      Source source = snapshot.getSource(1);
      bodySource = Okio.buffer(new ForwardingSource(source) {
        public void close() throws IOException {
          snapshot.close();
          super.close();
        }
      });
    }
    
    public MediaType contentType() {
      return contentType != null ? MediaType.parse(contentType) : null;
    }
    
    public long contentLength() {
      try {
        return contentLength != null ? Long.parseLong(contentLength) : -1L;
      } catch (NumberFormatException e) {}
      return -1L;
    }
    
    public BufferedSource source()
    {
      return bodySource;
    }
  }
}
