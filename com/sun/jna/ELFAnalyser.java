package com.sun.jna;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;








class ELFAnalyser
{
  private static final byte[] ELF_MAGIC = { Byte.MAX_VALUE, 69, 76, 70 };
  
  private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
  
  private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
  
  private static final int EI_DATA_BIG_ENDIAN = 2;
  
  private static final int E_MACHINE_ARM = 40;
  
  private static final int EI_CLASS_64BIT = 2;
  private final String filename;
  
  public static ELFAnalyser analyse(String filename)
    throws IOException
  {
    ELFAnalyser res = new ELFAnalyser(filename);
    res.runDetection();
    return res;
  }
  

  private boolean ELF = false;
  private boolean _64Bit = false;
  private boolean bigEndian = false;
  private boolean armHardFloat = false;
  private boolean armSoftFloat = false;
  private boolean arm = false;
  


  public boolean isELF()
  {
    return ELF;
  }
  



  public boolean is64Bit()
  {
    return _64Bit;
  }
  



  public boolean isBigEndian()
  {
    return bigEndian;
  }
  


  public String getFilename()
  {
    return filename;
  }
  



  public boolean isArmHardFloat()
  {
    return armHardFloat;
  }
  



  public boolean isArmSoftFloat()
  {
    return armSoftFloat;
  }
  



  public boolean isArm()
  {
    return arm;
  }
  
  private ELFAnalyser(String filename) {
    this.filename = filename;
  }
  
  private void runDetection()
    throws IOException
  {
    RandomAccessFile raf = new RandomAccessFile(filename, "r");
    if (raf.length() > 4L) {
      byte[] magic = new byte[4];
      raf.seek(0L);
      raf.read(magic);
      if (Arrays.equals(magic, ELF_MAGIC)) {
        ELF = true;
      }
    }
    if (!ELF) {
      return;
    }
    raf.seek(4L);
    

    byte sizeIndicator = raf.readByte();
    _64Bit = (sizeIndicator == 2);
    raf.seek(0L);
    ByteBuffer headerData = ByteBuffer.allocate(_64Bit ? 64 : 52);
    raf.getChannel().read(headerData, 0L);
    bigEndian = (headerData.get(5) == 2);
    headerData.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    
    arm = (headerData.get(18) == 40);
    
    if (arm) {
      int flags = headerData.getInt(_64Bit ? 48 : 36);
      armSoftFloat = ((flags & 0x200) == 512);
      armHardFloat = ((flags & 0x400) == 1024);
    }
  }
}
