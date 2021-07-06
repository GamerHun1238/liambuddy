package okhttp3.internal.http2;

import okhttp3.internal.Util;
import okio.ByteString;

















public final class Header
{
  public static final ByteString PSEUDO_PREFIX = ByteString.encodeUtf8(":");
  
  public static final String RESPONSE_STATUS_UTF8 = ":status";
  
  public static final String TARGET_METHOD_UTF8 = ":method";
  public static final String TARGET_PATH_UTF8 = ":path";
  public static final String TARGET_SCHEME_UTF8 = ":scheme";
  public static final String TARGET_AUTHORITY_UTF8 = ":authority";
  public static final ByteString RESPONSE_STATUS = ByteString.encodeUtf8(":status");
  public static final ByteString TARGET_METHOD = ByteString.encodeUtf8(":method");
  public static final ByteString TARGET_PATH = ByteString.encodeUtf8(":path");
  public static final ByteString TARGET_SCHEME = ByteString.encodeUtf8(":scheme");
  public static final ByteString TARGET_AUTHORITY = ByteString.encodeUtf8(":authority");
  
  public final ByteString name;
  
  public final ByteString value;
  
  final int hpackSize;
  

  public Header(String name, String value)
  {
    this(ByteString.encodeUtf8(name), ByteString.encodeUtf8(value));
  }
  
  public Header(ByteString name, String value) {
    this(name, ByteString.encodeUtf8(value));
  }
  
  public Header(ByteString name, ByteString value) {
    this.name = name;
    this.value = value;
    hpackSize = (32 + name.size() + value.size());
  }
  
  public boolean equals(Object other) {
    if ((other instanceof Header)) {
      Header that = (Header)other;
      return (name.equals(name)) && 
        (value.equals(value));
    }
    return false;
  }
  
  public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }
  
  public String toString() {
    return Util.format("%s: %s", new Object[] { name.utf8(), value.utf8() });
  }
}
