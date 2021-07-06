package com.sun.jna;







public abstract class IntegerType
  extends Number
  implements NativeMapped
{
  private static final long serialVersionUID = 1L;
  





  private int size;
  





  private Number number;
  





  private boolean unsigned;
  





  private long value;
  





  public IntegerType(int size)
  {
    this(size, 0L, false);
  }
  
  public IntegerType(int size, boolean unsigned)
  {
    this(size, 0L, unsigned);
  }
  
  public IntegerType(int size, long value)
  {
    this(size, value, false);
  }
  
  public IntegerType(int size, long value, boolean unsigned)
  {
    this.size = size;
    this.unsigned = unsigned;
    setValue(value);
  }
  


  public void setValue(long value)
  {
    long truncated = value;
    this.value = value;
    switch (size) {
    case 1: 
      if (unsigned) this.value = (value & 0xFF);
      truncated = (byte)(int)value;
      number = Byte.valueOf((byte)(int)value);
      break;
    case 2: 
      if (unsigned) this.value = (value & 0xFFFF);
      truncated = (short)(int)value;
      number = Short.valueOf((short)(int)value);
      break;
    case 4: 
      if (unsigned) this.value = (value & 0xFFFFFFFF);
      truncated = (int)value;
      number = Integer.valueOf((int)value);
      break;
    case 8: 
      number = Long.valueOf(value);
      break;
    case 3: case 5: case 6: case 7: default: 
      throw new IllegalArgumentException("Unsupported size: " + size);
    }
    if (size < 8) {
      long mask = (1L << size * 8) - 1L ^ 0xFFFFFFFFFFFFFFFF;
      if (((value < 0L) && (truncated != value)) || ((value >= 0L) && ((mask & value) != 0L)))
      {


        throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + size + " bytes) mask=0x" + Long.toHexString(mask));
      }
    }
  }
  
  public Object toNative()
  {
    return number;
  }
  


  public Object fromNative(Object nativeValue, FromNativeContext context)
  {
    long value = nativeValue == null ? 0L : ((Number)nativeValue).longValue();
    try {
      IntegerType number = (IntegerType)getClass().newInstance();
      number.setValue(value);
      return number;
    }
    catch (InstantiationException e)
    {
      throw new IllegalArgumentException("Can't instantiate " + getClass());
    }
    catch (IllegalAccessException e)
    {
      throw new IllegalArgumentException("Not allowed to instantiate " + getClass());
    }
  }
  
  public Class<?> nativeType()
  {
    return number.getClass();
  }
  
  public int intValue()
  {
    return (int)value;
  }
  
  public long longValue()
  {
    return value;
  }
  
  public float floatValue()
  {
    return number.floatValue();
  }
  
  public double doubleValue()
  {
    return number.doubleValue();
  }
  
  public boolean equals(Object rhs)
  {
    return ((rhs instanceof IntegerType)) && 
      (number.equals(number));
  }
  
  public String toString()
  {
    return number.toString();
  }
  
  public int hashCode()
  {
    return number.hashCode();
  }
  













  public static <T extends IntegerType> int compare(T v1, T v2)
  {
    if (v1 == v2)
      return 0;
    if (v1 == null)
      return 1;
    if (v2 == null) {
      return -1;
    }
    return compare(v1.longValue(), v2.longValue());
  }
  











  public static int compare(IntegerType v1, long v2)
  {
    if (v1 == null) {
      return 1;
    }
    return compare(v1.longValue(), v2);
  }
  

  public static final int compare(long v1, long v2)
  {
    if (v1 == v2)
      return 0;
    if (v1 < v2) {
      return -1;
    }
    return 1;
  }
}
