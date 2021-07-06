package okio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import javax.annotation.Nullable;

public abstract interface BufferedSource
  extends Source, ReadableByteChannel
{
  @Deprecated
  public abstract Buffer buffer();
  
  public abstract Buffer getBuffer();
  
  public abstract boolean exhausted()
    throws IOException;
  
  public abstract void require(long paramLong)
    throws IOException;
  
  public abstract boolean request(long paramLong)
    throws IOException;
  
  public abstract byte readByte()
    throws IOException;
  
  public abstract short readShort()
    throws IOException;
  
  public abstract short readShortLe()
    throws IOException;
  
  public abstract int readInt()
    throws IOException;
  
  public abstract int readIntLe()
    throws IOException;
  
  public abstract long readLong()
    throws IOException;
  
  public abstract long readLongLe()
    throws IOException;
  
  public abstract long readDecimalLong()
    throws IOException;
  
  public abstract long readHexadecimalUnsignedLong()
    throws IOException;
  
  public abstract void skip(long paramLong)
    throws IOException;
  
  public abstract ByteString readByteString()
    throws IOException;
  
  public abstract ByteString readByteString(long paramLong)
    throws IOException;
  
  public abstract int select(Options paramOptions)
    throws IOException;
  
  public abstract byte[] readByteArray()
    throws IOException;
  
  public abstract byte[] readByteArray(long paramLong)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract void readFully(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void readFully(Buffer paramBuffer, long paramLong)
    throws IOException;
  
  public abstract long readAll(Sink paramSink)
    throws IOException;
  
  public abstract String readUtf8()
    throws IOException;
  
  public abstract String readUtf8(long paramLong)
    throws IOException;
  
  @Nullable
  public abstract String readUtf8Line()
    throws IOException;
  
  public abstract String readUtf8LineStrict()
    throws IOException;
  
  public abstract String readUtf8LineStrict(long paramLong)
    throws IOException;
  
  public abstract int readUtf8CodePoint()
    throws IOException;
  
  public abstract String readString(Charset paramCharset)
    throws IOException;
  
  public abstract String readString(long paramLong, Charset paramCharset)
    throws IOException;
  
  public abstract long indexOf(byte paramByte)
    throws IOException;
  
  public abstract long indexOf(byte paramByte, long paramLong)
    throws IOException;
  
  public abstract long indexOf(byte paramByte, long paramLong1, long paramLong2)
    throws IOException;
  
  public abstract long indexOf(ByteString paramByteString)
    throws IOException;
  
  public abstract long indexOf(ByteString paramByteString, long paramLong)
    throws IOException;
  
  public abstract long indexOfElement(ByteString paramByteString)
    throws IOException;
  
  public abstract long indexOfElement(ByteString paramByteString, long paramLong)
    throws IOException;
  
  public abstract boolean rangeEquals(long paramLong, ByteString paramByteString)
    throws IOException;
  
  public abstract boolean rangeEquals(long paramLong, ByteString paramByteString, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract BufferedSource peek();
  
  public abstract InputStream inputStream();
}
