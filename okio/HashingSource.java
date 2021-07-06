package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;






























public final class HashingSource
  extends ForwardingSource
{
  private final MessageDigest messageDigest;
  private final Mac mac;
  
  public static HashingSource md5(Source source)
  {
    return new HashingSource(source, "MD5");
  }
  
  public static HashingSource sha1(Source source)
  {
    return new HashingSource(source, "SHA-1");
  }
  
  public static HashingSource sha256(Source source)
  {
    return new HashingSource(source, "SHA-256");
  }
  
  public static HashingSource hmacSha1(Source source, ByteString key)
  {
    return new HashingSource(source, key, "HmacSHA1");
  }
  
  public static HashingSource hmacSha256(Source source, ByteString key)
  {
    return new HashingSource(source, key, "HmacSHA256");
  }
  
  private HashingSource(Source source, String algorithm) {
    super(source);
    try {
      messageDigest = MessageDigest.getInstance(algorithm);
      mac = null;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    }
  }
  
  private HashingSource(Source source, ByteString key, String algorithm) {
    super(source);
    try {
      mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
      messageDigest = null;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public long read(Buffer sink, long byteCount) throws IOException {
    long result = super.read(sink, byteCount);
    
    if (result != -1L) {
      long start = size - result;
      

      long offset = size;
      Segment s = head;
      while (offset > start) {
        s = prev;
        offset -= limit - pos;
      }
      

      while (offset < size) {
        int pos = (int)(pos + start - offset);
        if (messageDigest != null) {
          messageDigest.update(data, pos, limit - pos);
        } else {
          mac.update(data, pos, limit - pos);
        }
        offset += limit - pos;
        start = offset;
        s = next;
      }
    }
    
    return result;
  }
  





  public final ByteString hash()
  {
    byte[] result = messageDigest != null ? messageDigest.digest() : mac.doFinal();
    return ByteString.of(result);
  }
}
