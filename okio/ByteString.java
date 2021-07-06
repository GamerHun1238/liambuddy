package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;






























public class ByteString
  implements Serializable, Comparable<ByteString>
{
  static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  

  private static final long serialVersionUID = 1L;
  
  public static final ByteString EMPTY = of(new byte[0]);
  final byte[] data;
  transient int hashCode;
  transient String utf8;
  
  ByteString(byte[] data)
  {
    this.data = data;
  }
  


  public static ByteString of(byte... data)
  {
    if (data == null) throw new IllegalArgumentException("data == null");
    return new ByteString((byte[])data.clone());
  }
  



  public static ByteString of(byte[] data, int offset, int byteCount)
  {
    if (data == null) throw new IllegalArgumentException("data == null");
    Util.checkOffsetAndCount(data.length, offset, byteCount);
    
    byte[] copy = new byte[byteCount];
    System.arraycopy(data, offset, copy, 0, byteCount);
    return new ByteString(copy);
  }
  
  public static ByteString of(ByteBuffer data) {
    if (data == null) { throw new IllegalArgumentException("data == null");
    }
    byte[] copy = new byte[data.remaining()];
    data.get(copy);
    return new ByteString(copy);
  }
  
  public static ByteString encodeUtf8(String s)
  {
    if (s == null) throw new IllegalArgumentException("s == null");
    ByteString byteString = new ByteString(s.getBytes(Util.UTF_8));
    utf8 = s;
    return byteString;
  }
  
  public static ByteString encodeString(String s, Charset charset)
  {
    if (s == null) throw new IllegalArgumentException("s == null");
    if (charset == null) throw new IllegalArgumentException("charset == null");
    return new ByteString(s.getBytes(charset));
  }
  
  public String utf8()
  {
    String result = utf8;
    
    return this.utf8 = new String(data, Util.UTF_8);
  }
  
  public String string(Charset charset)
  {
    if (charset == null) throw new IllegalArgumentException("charset == null");
    return new String(data, charset);
  }
  




  public String base64()
  {
    return Base64.encode(data);
  }
  
  public ByteString md5()
  {
    return digest("MD5");
  }
  
  public ByteString sha1()
  {
    return digest("SHA-1");
  }
  
  public ByteString sha256()
  {
    return digest("SHA-256");
  }
  
  public ByteString sha512()
  {
    return digest("SHA-512");
  }
  
  private ByteString digest(String algorithm) {
    try {
      return of(MessageDigest.getInstance(algorithm).digest(data));
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }
  
  public ByteString hmacSha1(ByteString key)
  {
    return hmac("HmacSHA1", key);
  }
  
  public ByteString hmacSha256(ByteString key)
  {
    return hmac("HmacSHA256", key);
  }
  
  public ByteString hmacSha512(ByteString key)
  {
    return hmac("HmacSHA512", key);
  }
  
  private ByteString hmac(String algorithm, ByteString key) {
    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
      return of(mac.doFinal(data));
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }
  



  public String base64Url()
  {
    return Base64.encodeUrl(data);
  }
  


  @Nullable
  public static ByteString decodeBase64(String base64)
  {
    if (base64 == null) throw new IllegalArgumentException("base64 == null");
    byte[] decoded = Base64.decode(base64);
    return decoded != null ? new ByteString(decoded) : null;
  }
  
  public String hex()
  {
    char[] result = new char[data.length * 2];
    int c = 0;
    for (byte b : data) {
      result[(c++)] = HEX_DIGITS[(b >> 4 & 0xF)];
      result[(c++)] = HEX_DIGITS[(b & 0xF)];
    }
    return new String(result);
  }
  
  public static ByteString decodeHex(String hex)
  {
    if (hex == null) throw new IllegalArgumentException("hex == null");
    if (hex.length() % 2 != 0) { throw new IllegalArgumentException("Unexpected hex string: " + hex);
    }
    byte[] result = new byte[hex.length() / 2];
    for (int i = 0; i < result.length; i++) {
      int d1 = decodeHexDigit(hex.charAt(i * 2)) << 4;
      int d2 = decodeHexDigit(hex.charAt(i * 2 + 1));
      result[i] = ((byte)(d1 + d2));
    }
    return of(result);
  }
  
  private static int decodeHexDigit(char c) {
    if ((c >= '0') && (c <= '9')) return c - '0';
    if ((c >= 'a') && (c <= 'f')) return c - 'a' + 10;
    if ((c >= 'A') && (c <= 'F')) return c - 'A' + 10;
    throw new IllegalArgumentException("Unexpected hex digit: " + c);
  }
  




  public static ByteString read(InputStream in, int byteCount)
    throws IOException
  {
    if (in == null) throw new IllegalArgumentException("in == null");
    if (byteCount < 0) { throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    }
    byte[] result = new byte[byteCount];
    int read; for (int offset = 0; offset < byteCount; offset += read) {
      read = in.read(result, offset, byteCount - offset);
      if (read == -1) throw new EOFException();
    }
    return new ByteString(result);
  }
  





  public ByteString toAsciiLowercase()
  {
    for (int i = 0; i < data.length; i++) {
      byte c = data[i];
      if ((c >= 65) && (c <= 90))
      {


        byte[] lowercase = (byte[])data.clone();
        lowercase[(i++)] = ((byte)(c - -32));
        for (; i < lowercase.length; i++) {
          c = lowercase[i];
          if ((c >= 65) && (c <= 90))
            lowercase[i] = ((byte)(c - -32));
        }
        return new ByteString(lowercase);
      } }
    return this;
  }
  





  public ByteString toAsciiUppercase()
  {
    for (int i = 0; i < data.length; i++) {
      byte c = data[i];
      if ((c >= 97) && (c <= 122))
      {


        byte[] lowercase = (byte[])data.clone();
        lowercase[(i++)] = ((byte)(c - 32));
        for (; i < lowercase.length; i++) {
          c = lowercase[i];
          if ((c >= 97) && (c <= 122))
            lowercase[i] = ((byte)(c - 32));
        }
        return new ByteString(lowercase);
      } }
    return this;
  }
  



  public ByteString substring(int beginIndex)
  {
    return substring(beginIndex, data.length);
  }
  




  public ByteString substring(int beginIndex, int endIndex)
  {
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0");
    if (endIndex > data.length) {
      throw new IllegalArgumentException("endIndex > length(" + data.length + ")");
    }
    
    int subLen = endIndex - beginIndex;
    if (subLen < 0) { throw new IllegalArgumentException("endIndex < beginIndex");
    }
    if ((beginIndex == 0) && (endIndex == data.length)) {
      return this;
    }
    
    byte[] copy = new byte[subLen];
    System.arraycopy(data, beginIndex, copy, 0, subLen);
    return new ByteString(copy);
  }
  
  public byte getByte(int pos)
  {
    return data[pos];
  }
  


  public int size()
  {
    return data.length;
  }
  


  public byte[] toByteArray()
  {
    return (byte[])data.clone();
  }
  
  byte[] internalArray()
  {
    return data;
  }
  


  public ByteBuffer asByteBuffer()
  {
    return ByteBuffer.wrap(data).asReadOnlyBuffer();
  }
  
  public void write(OutputStream out) throws IOException
  {
    if (out == null) throw new IllegalArgumentException("out == null");
    out.write(data);
  }
  
  void write(Buffer buffer)
  {
    buffer.write(data, 0, data.length);
  }
  




  public boolean rangeEquals(int offset, ByteString other, int otherOffset, int byteCount)
  {
    return other.rangeEquals(otherOffset, data, offset, byteCount);
  }
  




  public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount)
  {
    if ((offset >= 0) && (offset <= data.length - byteCount) && (otherOffset >= 0) && (otherOffset <= other.length - byteCount)) {} return 
    
      Util.arrayRangeEquals(data, offset, other, otherOffset, byteCount);
  }
  
  public final boolean startsWith(ByteString prefix) {
    return rangeEquals(0, prefix, 0, prefix.size());
  }
  
  public final boolean startsWith(byte[] prefix) {
    return rangeEquals(0, prefix, 0, prefix.length);
  }
  
  public final boolean endsWith(ByteString suffix) {
    return rangeEquals(size() - suffix.size(), suffix, 0, suffix.size());
  }
  
  public final boolean endsWith(byte[] suffix) {
    return rangeEquals(size() - suffix.length, suffix, 0, suffix.length);
  }
  
  public final int indexOf(ByteString other) {
    return indexOf(other.internalArray(), 0);
  }
  
  public final int indexOf(ByteString other, int fromIndex) {
    return indexOf(other.internalArray(), fromIndex);
  }
  
  public final int indexOf(byte[] other) {
    return indexOf(other, 0);
  }
  
  public int indexOf(byte[] other, int fromIndex) {
    fromIndex = Math.max(fromIndex, 0);
    int i = fromIndex; for (int limit = data.length - other.length; i <= limit; i++) {
      if (Util.arrayRangeEquals(data, i, other, 0, other.length)) {
        return i;
      }
    }
    return -1;
  }
  
  public final int lastIndexOf(ByteString other) {
    return lastIndexOf(other.internalArray(), size());
  }
  
  public final int lastIndexOf(ByteString other, int fromIndex) {
    return lastIndexOf(other.internalArray(), fromIndex);
  }
  
  public final int lastIndexOf(byte[] other) {
    return lastIndexOf(other, size());
  }
  
  public int lastIndexOf(byte[] other, int fromIndex) {
    fromIndex = Math.min(fromIndex, data.length - other.length);
    for (int i = fromIndex; i >= 0; i--) {
      if (Util.arrayRangeEquals(data, i, other, 0, other.length)) {
        return i;
      }
    }
    return -1;
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    return ((o instanceof ByteString)) && 
      (((ByteString)o).size() == data.length) && 
      (((ByteString)o).rangeEquals(0, data, 0, data.length));
  }
  
  public int hashCode() {
    int result = hashCode;
    return this.hashCode = Arrays.hashCode(data);
  }
  
  public int compareTo(ByteString byteString) {
    int sizeA = size();
    int sizeB = byteString.size();
    int i = 0; for (int size = Math.min(sizeA, sizeB); i < size; i++) {
      int byteA = getByte(i) & 0xFF;
      int byteB = byteString.getByte(i) & 0xFF;
      if (byteA != byteB)
        return byteA < byteB ? -1 : 1;
    }
    if (sizeA == sizeB) return 0;
    return sizeA < sizeB ? -1 : 1;
  }
  



  public String toString()
  {
    if (data.length == 0) {
      return "[size=0]";
    }
    
    String text = utf8();
    int i = codePointIndexToCharIndex(text, 64);
    
    if (i == -1) {
      return 
      
        "[size=" + data.length + " hex=" + substring(0, 64).hex() + "…]";
    }
    



    String safeText = text.substring(0, i).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
    return 
    
      "[text=" + safeText + "]";
  }
  
  static int codePointIndexToCharIndex(String s, int codePointCount) {
    int i = 0;int j = 0; int c; for (int length = s.length(); i < length; i += Character.charCount(c)) {
      if (j == codePointCount) {
        return i;
      }
      c = s.codePointAt(i);
      if (((Character.isISOControl(c)) && (c != 10) && (c != 13)) || (c == 65533))
      {
        return -1;
      }
      j++;
    }
    return s.length();
  }
  
  private void readObject(ObjectInputStream in) throws IOException {
    int dataLength = in.readInt();
    ByteString byteString = read(in, dataLength);
    try {
      Field field = ByteString.class.getDeclaredField("data");
      field.setAccessible(true);
      field.set(this, data);
    } catch (NoSuchFieldException e) {
      throw new AssertionError();
    } catch (IllegalAccessException e) {
      throw new AssertionError();
    }
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeInt(data.length);
    out.write(data);
  }
}
