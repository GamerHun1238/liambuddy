package com.neovisionaries.ws.client;

import java.nio.ByteBuffer;
































class ByteArray
{
  private static final int ADDITIONAL_BUFFER_SIZE = 1024;
  private ByteBuffer mBuffer;
  private int mLength;
  
  public ByteArray(int capacity)
  {
    mBuffer = ByteBuffer.allocate(capacity);
    mLength = 0;
  }
  








  public ByteArray(byte[] data)
  {
    mBuffer = ByteBuffer.wrap(data);
    mLength = data.length;
  }
  




  public int length()
  {
    return mLength;
  }
  



  public byte get(int index)
    throws IndexOutOfBoundsException
  {
    if ((index < 0) || (mLength <= index))
    {


      throw new IndexOutOfBoundsException(String.format("Bad index: index=%d, length=%d", new Object[] {Integer.valueOf(index), Integer.valueOf(mLength) }));
    }
    
    return mBuffer.get(index);
  }
  





  private void expandBuffer(int newBufferSize)
  {
    ByteBuffer newBuffer = ByteBuffer.allocate(newBufferSize);
    

    int oldPosition = mBuffer.position();
    mBuffer.position(0);
    newBuffer.put(mBuffer);
    newBuffer.position(oldPosition);
    

    mBuffer = newBuffer;
  }
  





  public void put(int data)
  {
    if (mBuffer.capacity() < mLength + 1)
    {
      expandBuffer(mLength + 1024);
    }
    
    mBuffer.put((byte)data);
    mLength += 1;
  }
  








  public void put(byte[] source)
  {
    if (mBuffer.capacity() < mLength + source.length)
    {
      expandBuffer(mLength + source.length + 1024);
    }
    
    mBuffer.put(source);
    mLength += source.length;
  }
  














  public void put(byte[] source, int index, int length)
  {
    if (mBuffer.capacity() < mLength + length)
    {
      expandBuffer(mLength + length + 1024);
    }
    
    mBuffer.put(source, index, length);
    mLength += length;
  }
  













  public void put(ByteArray source, int index, int length)
  {
    put(mBuffer.array(), index, length);
  }
  




  public byte[] toBytes()
  {
    return toBytes(0);
  }
  

  public byte[] toBytes(int beginIndex)
  {
    return toBytes(beginIndex, length());
  }
  

  public byte[] toBytes(int beginIndex, int endIndex)
  {
    int len = endIndex - beginIndex;
    
    if ((len < 0) || (beginIndex < 0) || (mLength < endIndex))
    {

      throw new IllegalArgumentException(String.format("Bad range: beginIndex=%d, endIndex=%d, length=%d", new Object[] {
        Integer.valueOf(beginIndex), Integer.valueOf(endIndex), Integer.valueOf(mLength) }));
    }
    
    byte[] bytes = new byte[len];
    
    if (len != 0)
    {
      System.arraycopy(mBuffer.array(), beginIndex, bytes, 0, len);
    }
    
    return bytes;
  }
  

  public void clear()
  {
    mBuffer.clear();
    mBuffer.position(0);
    mLength = 0;
  }
  

  public void shrink(int size)
  {
    if (mBuffer.capacity() <= size)
    {
      return;
    }
    
    int endIndex = mLength;
    int beginIndex = mLength - size;
    
    byte[] bytes = toBytes(beginIndex, endIndex);
    
    mBuffer = ByteBuffer.wrap(bytes);
    mBuffer.position(bytes.length);
    mLength = bytes.length;
  }
  

  public boolean getBit(int bitIndex)
  {
    int index = bitIndex / 8;
    int shift = bitIndex % 8;
    int value = get(index);
    

    return (value & 1 << shift) != 0;
  }
  

  public int getBits(int bitIndex, int nBits)
  {
    int number = 0;
    int weight = 1;
    

    for (int i = 0; i < nBits; weight *= 2)
    {

      if (getBit(bitIndex + i))
      {
        number += weight;
      }
      i++;
    }
    






    return number;
  }
  

  public int getHuffmanBits(int bitIndex, int nBits)
  {
    int number = 0;
    int weight = 1;
    









    for (int i = nBits - 1; 0 <= i; weight *= 2)
    {

      if (getBit(bitIndex + i))
      {
        number += weight;
      }
      i--;
    }
    






    return number;
  }
  

  public boolean readBit(int[] bitIndex)
  {
    boolean result = getBit(bitIndex[0]);
    
    bitIndex[0] += 1;
    
    return result;
  }
  

  public int readBits(int[] bitIndex, int nBits)
  {
    int number = getBits(bitIndex[0], nBits);
    
    bitIndex[0] += nBits;
    
    return number;
  }
  

  public void setBit(int bitIndex, boolean bit)
  {
    int index = bitIndex / 8;
    int shift = bitIndex % 8;
    int value = get(index);
    
    if (bit)
    {
      value |= 1 << shift;
    }
    else
    {
      value &= (1 << shift ^ 0xFFFFFFFF);
    }
    
    mBuffer.put(index, (byte)value);
  }
  

  public void clearBit(int bitIndex)
  {
    setBit(bitIndex, false);
  }
}
