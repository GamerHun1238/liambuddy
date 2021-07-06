package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;




















public final class MultipartBody
  extends RequestBody
{
  public static final MediaType MIXED = MediaType.get("multipart/mixed");
  





  public static final MediaType ALTERNATIVE = MediaType.get("multipart/alternative");
  





  public static final MediaType DIGEST = MediaType.get("multipart/digest");
  




  public static final MediaType PARALLEL = MediaType.get("multipart/parallel");
  





  public static final MediaType FORM = MediaType.get("multipart/form-data");
  
  private static final byte[] COLONSPACE = { 58, 32 };
  private static final byte[] CRLF = { 13, 10 };
  private static final byte[] DASHDASH = { 45, 45 };
  
  private final ByteString boundary;
  private final MediaType originalType;
  private final MediaType contentType;
  private final List<Part> parts;
  private long contentLength = -1L;
  
  MultipartBody(ByteString boundary, MediaType type, List<Part> parts) {
    this.boundary = boundary;
    originalType = type;
    contentType = MediaType.get(type + "; boundary=" + boundary.utf8());
    this.parts = Util.immutableList(parts);
  }
  
  public MediaType type() {
    return originalType;
  }
  
  public String boundary() {
    return boundary.utf8();
  }
  
  public int size()
  {
    return parts.size();
  }
  
  public List<Part> parts() {
    return parts;
  }
  
  public Part part(int index) {
    return (Part)parts.get(index);
  }
  
  public MediaType contentType()
  {
    return contentType;
  }
  
  public long contentLength() throws IOException {
    long result = contentLength;
    if (result != -1L) return result;
    return this.contentLength = writeOrCountBytes(null, true);
  }
  
  public void writeTo(BufferedSink sink) throws IOException {
    writeOrCountBytes(sink, false);
  }
  





  private long writeOrCountBytes(@Nullable BufferedSink sink, boolean countBytes)
    throws IOException
  {
    long byteCount = 0L;
    
    Buffer byteCountBuffer = null;
    if (countBytes) {
      sink = byteCountBuffer = new Buffer();
    }
    
    int p = 0; for (int partCount = parts.size(); p < partCount; p++) {
      Part part = (Part)parts.get(p);
      Headers headers = headers;
      RequestBody body = body;
      
      sink.write(DASHDASH);
      sink.write(boundary);
      sink.write(CRLF);
      
      if (headers != null) {
        int h = 0; for (int headerCount = headers.size(); h < headerCount; h++)
        {


          sink.writeUtf8(headers.name(h)).write(COLONSPACE).writeUtf8(headers.value(h)).write(CRLF);
        }
      }
      
      MediaType contentType = body.contentType();
      if (contentType != null)
      {

        sink.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).write(CRLF);
      }
      
      long contentLength = body.contentLength();
      if (contentLength != -1L)
      {

        sink.writeUtf8("Content-Length: ").writeDecimalLong(contentLength).write(CRLF);
      } else if (countBytes)
      {
        byteCountBuffer.clear();
        return -1L;
      }
      
      sink.write(CRLF);
      
      if (countBytes) {
        byteCount += contentLength;
      } else {
        body.writeTo(sink);
      }
      
      sink.write(CRLF);
    }
    
    sink.write(DASHDASH);
    sink.write(boundary);
    sink.write(DASHDASH);
    sink.write(CRLF);
    
    if (countBytes) {
      byteCount += byteCountBuffer.size();
      byteCountBuffer.clear();
    }
    
    return byteCount;
  }
  









  static void appendQuotedString(StringBuilder target, String key)
  {
    target.append('"');
    int i = 0; for (int len = key.length(); i < len; i++) {
      char ch = key.charAt(i);
      switch (ch) {
      case '\n': 
        target.append("%0A");
        break;
      case '\r': 
        target.append("%0D");
        break;
      case '"': 
        target.append("%22");
        break;
      default: 
        target.append(ch);
      }
      
    }
    target.append('"'); }
  
  public static final class Part { @Nullable
    final Headers headers;
    
    public static Part create(RequestBody body) { return create(null, body); }
    
    public static Part create(@Nullable Headers headers, RequestBody body)
    {
      if (body == null) {
        throw new NullPointerException("body == null");
      }
      if ((headers != null) && (headers.get("Content-Type") != null)) {
        throw new IllegalArgumentException("Unexpected header: Content-Type");
      }
      if ((headers != null) && (headers.get("Content-Length") != null)) {
        throw new IllegalArgumentException("Unexpected header: Content-Length");
      }
      return new Part(headers, body);
    }
    
    public static Part createFormData(String name, String value) {
      return createFormData(name, null, RequestBody.create(null, value));
    }
    
    public static Part createFormData(String name, @Nullable String filename, RequestBody body) {
      if (name == null) {
        throw new NullPointerException("name == null");
      }
      StringBuilder disposition = new StringBuilder("form-data; name=");
      MultipartBody.appendQuotedString(disposition, name);
      
      if (filename != null) {
        disposition.append("; filename=");
        MultipartBody.appendQuotedString(disposition, filename);
      }
      


      Headers headers = new Headers.Builder().addUnsafeNonAscii("Content-Disposition", disposition.toString()).build();
      
      return create(headers, body);
    }
    

    final RequestBody body;
    private Part(@Nullable Headers headers, RequestBody body)
    {
      this.headers = headers;
      this.body = body;
    }
    
    @Nullable
    public Headers headers() { return headers; }
    
    public RequestBody body()
    {
      return body;
    }
  }
  
  public static final class Builder {
    private final ByteString boundary;
    private MediaType type = MultipartBody.MIXED;
    private final List<MultipartBody.Part> parts = new ArrayList();
    
    public Builder() {
      this(UUID.randomUUID().toString());
    }
    
    public Builder(String boundary) {
      this.boundary = ByteString.encodeUtf8(boundary);
    }
    



    public Builder setType(MediaType type)
    {
      if (type == null) {
        throw new NullPointerException("type == null");
      }
      if (!type.type().equals("multipart")) {
        throw new IllegalArgumentException("multipart != " + type);
      }
      this.type = type;
      return this;
    }
    
    public Builder addPart(RequestBody body)
    {
      return addPart(MultipartBody.Part.create(body));
    }
    
    public Builder addPart(@Nullable Headers headers, RequestBody body)
    {
      return addPart(MultipartBody.Part.create(headers, body));
    }
    
    public Builder addFormDataPart(String name, String value)
    {
      return addPart(MultipartBody.Part.createFormData(name, value));
    }
    
    public Builder addFormDataPart(String name, @Nullable String filename, RequestBody body)
    {
      return addPart(MultipartBody.Part.createFormData(name, filename, body));
    }
    
    public Builder addPart(MultipartBody.Part part)
    {
      if (part == null) throw new NullPointerException("part == null");
      parts.add(part);
      return this;
    }
    
    public MultipartBody build()
    {
      if (parts.isEmpty()) {
        throw new IllegalStateException("Multipart body must have at least one part.");
      }
      return new MultipartBody(boundary, type, parts);
    }
  }
}
