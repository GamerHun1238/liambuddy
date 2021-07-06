package okhttp3.internal.http2;

import java.io.IOException;
import okhttp3.internal.Util;
import okio.ByteString;

















public final class Http2
{
  static final ByteString CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
  
  static final int INITIAL_MAX_FRAME_SIZE = 16384;
  
  static final byte TYPE_DATA = 0;
  
  static final byte TYPE_HEADERS = 1;
  
  static final byte TYPE_PRIORITY = 2;
  
  static final byte TYPE_RST_STREAM = 3;
  
  static final byte TYPE_SETTINGS = 4;
  static final byte TYPE_PUSH_PROMISE = 5;
  static final byte TYPE_PING = 6;
  static final byte TYPE_GOAWAY = 7;
  static final byte TYPE_WINDOW_UPDATE = 8;
  static final byte TYPE_CONTINUATION = 9;
  static final byte FLAG_NONE = 0;
  static final byte FLAG_ACK = 1;
  static final byte FLAG_END_STREAM = 1;
  static final byte FLAG_END_HEADERS = 4;
  static final byte FLAG_END_PUSH_PROMISE = 4;
  static final byte FLAG_PADDED = 8;
  static final byte FLAG_PRIORITY = 32;
  static final byte FLAG_COMPRESSED = 32;
  private static final String[] FRAME_NAMES = { "DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION" };
  















  static final String[] FLAGS = new String[64];
  static final String[] BINARY = new String['Ā'];
  
  static { for (int i = 0; i < BINARY.length; i++) {
      BINARY[i] = Util.format("%8s", new Object[] { Integer.toBinaryString(i) }).replace(' ', '0');
    }
    
    FLAGS[0] = "";
    FLAGS[1] = "END_STREAM";
    
    int[] prefixFlags = { 1 };
    
    FLAGS[8] = "PADDED";
    int prefixFlag; for (prefixFlag : prefixFlags) {
      FLAGS[(prefixFlag | 0x8)] = (FLAGS[prefixFlag] + "|PADDED");
    }
    
    FLAGS[4] = "END_HEADERS";
    FLAGS[32] = "PRIORITY";
    FLAGS[36] = "END_HEADERS|PRIORITY";
    int[] frameFlags = { 4, 32, 36 };
    


    for (int frameFlag : frameFlags) {
      for (int prefixFlag : prefixFlags) {
        FLAGS[(prefixFlag | frameFlag)] = (FLAGS[prefixFlag] + '|' + FLAGS[frameFlag]);
        FLAGS[(prefixFlag | frameFlag | 0x8)] = (FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED");
      }
    }
    

    for (int i = 0; i < FLAGS.length; i++) {
      if (FLAGS[i] == null) { FLAGS[i] = BINARY[i];
      }
    }
  }
  

  static IllegalArgumentException illegalArgument(String message, Object... args)
  {
    throw new IllegalArgumentException(Util.format(message, args));
  }
  
  static IOException ioException(String message, Object... args) throws IOException {
    throw new IOException(Util.format(message, args));
  }
  

















  static String frameLog(boolean inbound, int streamId, int length, byte type, byte flags)
  {
    String formattedType = type < FRAME_NAMES.length ? FRAME_NAMES[type] : Util.format("0x%02x", new Object[] { Byte.valueOf(type) });
    String formattedFlags = formatFlags(type, flags);
    return Util.format("%s 0x%08x %5d %-13s %s", new Object[] { inbound ? "<<" : ">>", Integer.valueOf(streamId), Integer.valueOf(length), formattedType, formattedFlags });
  }
  





  static String formatFlags(byte type, byte flags)
  {
    if (flags == 0) return "";
    switch (type) {
    case 4: 
    case 6: 
      return flags == 1 ? "ACK" : BINARY[flags];
    case 2: 
    case 3: 
    case 7: 
    case 8: 
      return BINARY[flags];
    }
    String result = flags < FLAGS.length ? FLAGS[flags] : BINARY[flags];
    
    if ((type == 5) && ((flags & 0x4) != 0))
      return result.replace("HEADERS", "PUSH_PROMISE");
    if ((type == 0) && ((flags & 0x20) != 0)) {
      return result.replace("PRIORITY", "COMPRESSED");
    }
    return result;
  }
  
  private Http2() {}
}
