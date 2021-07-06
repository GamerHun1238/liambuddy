package net.dv8tion.jda.api.utils.data.etf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterOutputStream;












































public class ExTermDecoder
{
  public ExTermDecoder() {}
  
  public static Object unpack(ByteBuffer buffer)
  {
    if (buffer.get() != -125) {
      throw new IllegalArgumentException("Failed header check");
    }
    return unpack0(buffer);
  }
  























  public static Map<String, Object> unpackMap(ByteBuffer buffer)
  {
    byte tag = buffer.get(1);
    if (tag != 116)
      throw new IllegalArgumentException("Cannot unpack map from tag " + tag);
    return (Map)unpack(buffer);
  }
  























  public static List<Object> unpackList(ByteBuffer buffer)
  {
    byte tag = buffer.get(1);
    if (tag != 108) {
      throw new IllegalArgumentException("Cannot unpack list from tag " + tag);
    }
    return (List)unpack(buffer);
  }
  
  private static Object unpack0(ByteBuffer buffer)
  {
    int tag = buffer.get();
    switch (tag) {
    case 80:  return unpackCompressed(buffer);
    case 97:  return Integer.valueOf(unpackSmallInt(buffer));
    case 110:  return Long.valueOf(unpackSmallBigint(buffer));
    case 98:  return Integer.valueOf(unpackInt(buffer));
    case 99: 
      return Double.valueOf(unpackOldFloat(buffer));
    case 70:  return Double.valueOf(unpackFloat(buffer));
    case 119: 
      return unpackSmallAtom(buffer, StandardCharsets.UTF_8);
    case 115:  return unpackSmallAtom(buffer, StandardCharsets.ISO_8859_1);
    case 118:  return unpackAtom(buffer, StandardCharsets.UTF_8);
    case 100:  return unpackAtom(buffer, StandardCharsets.ISO_8859_1);
    case 116: 
      return unpackMap0(buffer);
    case 108:  return unpackList0(buffer);
    case 106:  return Collections.emptyList();
    case 107: 
      return unpackString(buffer);
    case 109:  return unpackBinary(buffer);
    }
    throw new IllegalArgumentException("Unknown tag " + tag);
  }
  

  private static Object unpackCompressed(ByteBuffer buffer)
  {
    int size = buffer.getInt();
    ByteArrayOutputStream decompressed = new ByteArrayOutputStream(size);
    try { InflaterOutputStream inflater = new InflaterOutputStream(decompressed);
      try {
        inflater.write(buffer.array(), buffer.position(), buffer.remaining());
        inflater.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        inflater.close(); } catch (Throwable localThrowable1) { localThrowable.addSuppressed(localThrowable1); } throw localThrowable;

    }
    catch (IOException e)
    {

      throw new UncheckedIOException(e);
    }
    
    buffer = ByteBuffer.wrap(decompressed.toByteArray());
    return unpack0(buffer);
  }
  
  private static double unpackOldFloat(ByteBuffer buffer)
  {
    String bytes = getString(buffer, StandardCharsets.ISO_8859_1, 31);
    return Double.parseDouble(bytes);
  }
  
  private static double unpackFloat(ByteBuffer buffer)
  {
    return buffer.getDouble();
  }
  
  private static long unpackSmallBigint(ByteBuffer buffer)
  {
    int arity = Byte.toUnsignedInt(buffer.get());
    int sign = Byte.toUnsignedInt(buffer.get());
    long sum = 0L;
    long offset = 0L;
    while (arity-- > 0)
    {
      sum += (Byte.toUnsignedLong(buffer.get()) << (int)offset);
      offset += 8L;
    }
    
    return sign == 0 ? sum : -sum;
  }
  
  private static int unpackSmallInt(ByteBuffer buffer)
  {
    return Byte.toUnsignedInt(buffer.get());
  }
  
  private static int unpackInt(ByteBuffer buffer)
  {
    return buffer.getInt();
  }
  
  private static List<Object> unpackString(ByteBuffer buffer)
  {
    int length = Short.toUnsignedInt(buffer.getShort());
    List<Object> bytes = new ArrayList(length);
    while (length-- > 0)
      bytes.add(Byte.valueOf(buffer.get()));
    return bytes;
  }
  
  private static String unpackBinary(ByteBuffer buffer)
  {
    int length = buffer.getInt();
    return getString(buffer, StandardCharsets.UTF_8, length);
  }
  
  private static Object unpackSmallAtom(ByteBuffer buffer, Charset charset)
  {
    int length = Byte.toUnsignedInt(buffer.get());
    return unpackAtom(buffer, charset, length);
  }
  
  private static Object unpackAtom(ByteBuffer buffer, Charset charset)
  {
    int length = Short.toUnsignedInt(buffer.getShort());
    return unpackAtom(buffer, charset, length);
  }
  
  private static Object unpackAtom(ByteBuffer buffer, Charset charset, int length)
  {
    String value = getString(buffer, charset, length);
    switch (value) {
    case "true": 
      return Boolean.valueOf(true);
    case "false":  return Boolean.valueOf(false);
    case "nil":  return null; }
    return value;
  }
  

  private static String getString(ByteBuffer buffer, Charset charset, int length)
  {
    byte[] array = new byte[length];
    buffer.get(array);
    return new String(array, charset);
  }
  
  private static List<Object> unpackList0(ByteBuffer buffer)
  {
    int length = buffer.getInt();
    List<Object> list = new ArrayList(length);
    while (length-- > 0)
    {
      list.add(unpack0(buffer));
    }
    Object tail = unpack0(buffer);
    if (tail != Collections.emptyList())
      throw new IllegalArgumentException("Unexpected tail " + tail);
    return list;
  }
  
  private static Map<String, Object> unpackMap0(ByteBuffer buffer)
  {
    Map<String, Object> map = new HashMap();
    int arity = buffer.getInt();
    while (arity-- > 0)
    {
      String key = (String)unpack0(buffer);
      Object value = unpack0(buffer);
      map.put(key, value);
    }
    return map;
  }
}
