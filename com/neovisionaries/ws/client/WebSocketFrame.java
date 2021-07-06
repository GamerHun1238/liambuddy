package com.neovisionaries.ws.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



































public class WebSocketFrame
{
  private boolean mFin;
  private boolean mRsv1;
  private boolean mRsv2;
  private boolean mRsv3;
  private int mOpcode;
  private boolean mMask;
  private byte[] mPayload;
  
  public WebSocketFrame() {}
  
  public boolean getFin()
  {
    return mFin;
  }
  










  public WebSocketFrame setFin(boolean fin)
  {
    mFin = fin;
    
    return this;
  }
  







  public boolean getRsv1()
  {
    return mRsv1;
  }
  










  public WebSocketFrame setRsv1(boolean rsv1)
  {
    mRsv1 = rsv1;
    
    return this;
  }
  







  public boolean getRsv2()
  {
    return mRsv2;
  }
  










  public WebSocketFrame setRsv2(boolean rsv2)
  {
    mRsv2 = rsv2;
    
    return this;
  }
  







  public boolean getRsv3()
  {
    return mRsv3;
  }
  










  public WebSocketFrame setRsv3(boolean rsv3)
  {
    mRsv3 = rsv3;
    
    return this;
  }
  





















































  public int getOpcode()
  {
    return mOpcode;
  }
  












  public WebSocketFrame setOpcode(int opcode)
  {
    mOpcode = opcode;
    
    return this;
  }
  













  public boolean isContinuationFrame()
  {
    return mOpcode == 0;
  }
  













  public boolean isTextFrame()
  {
    return mOpcode == 1;
  }
  













  public boolean isBinaryFrame()
  {
    return mOpcode == 2;
  }
  













  public boolean isCloseFrame()
  {
    return mOpcode == 8;
  }
  













  public boolean isPingFrame()
  {
    return mOpcode == 9;
  }
  













  public boolean isPongFrame()
  {
    return mOpcode == 10;
  }
  













  public boolean isDataFrame()
  {
    return (1 <= mOpcode) && (mOpcode <= 7);
  }
  













  public boolean isControlFrame()
  {
    return (8 <= mOpcode) && (mOpcode <= 15);
  }
  







  boolean getMask()
  {
    return mMask;
  }
  










  WebSocketFrame setMask(boolean mask)
  {
    mMask = mask;
    
    return this;
  }
  







  public boolean hasPayload()
  {
    return mPayload != null;
  }
  







  public int getPayloadLength()
  {
    if (mPayload == null)
    {
      return 0;
    }
    
    return mPayload.length;
  }
  







  public byte[] getPayload()
  {
    return mPayload;
  }
  








  public String getPayloadText()
  {
    if (mPayload == null)
    {
      return null;
    }
    
    return Misc.toStringUTF8(mPayload);
  }
  

















  public WebSocketFrame setPayload(byte[] payload)
  {
    if ((payload != null) && (payload.length == 0))
    {
      payload = null;
    }
    
    mPayload = payload;
    
    return this;
  }
  


















  public WebSocketFrame setPayload(String payload)
  {
    if ((payload == null) || (payload.length() == 0))
    {
      return setPayload((byte[])null);
    }
    
    return setPayload(Misc.getBytesUTF8(payload));
  }
  

































  public WebSocketFrame setCloseFramePayload(int closeCode, String reason)
  {
    byte[] encodedCloseCode = { (byte)(closeCode >> 8 & 0xFF), (byte)(closeCode & 0xFF) };
    




    if ((reason == null) || (reason.length() == 0))
    {

      return setPayload(encodedCloseCode);
    }
    

    byte[] encodedReason = Misc.getBytesUTF8(reason);
    

    byte[] payload = new byte[2 + encodedReason.length];
    System.arraycopy(encodedCloseCode, 0, payload, 0, 2);
    System.arraycopy(encodedReason, 0, payload, 2, encodedReason.length);
    

    return setPayload(payload);
  }
  






















  public int getCloseCode()
  {
    if ((mPayload == null) || (mPayload.length < 2))
    {
      return 1005;
    }
    

    int closeCode = (mPayload[0] & 0xFF) << 8 | mPayload[1] & 0xFF;
    
    return closeCode;
  }
  

















  public String getCloseReason()
  {
    if ((mPayload == null) || (mPayload.length < 3))
    {
      return null;
    }
    
    return Misc.toStringUTF8(mPayload, 2, mPayload.length - 2);
  }
  








  public String toString()
  {
    StringBuilder builder = new StringBuilder().append("WebSocketFrame(FIN=").append(mFin ? "1" : "0").append(",RSV1=").append(mRsv1 ? "1" : "0").append(",RSV2=").append(mRsv2 ? "1" : "0").append(",RSV3=").append(mRsv3 ? "1" : "0").append(",Opcode=").append(Misc.toOpcodeName(mOpcode)).append(",Length=").append(getPayloadLength());
    
    switch (mOpcode)
    {
    case 1: 
      appendPayloadText(builder);
      break;
    
    case 2: 
      appendPayloadBinary(builder);
      break;
    
    case 8: 
      appendPayloadClose(builder);
    }
    
    
    return ")";
  }
  

  private boolean appendPayloadCommon(StringBuilder builder)
  {
    builder.append(",Payload=");
    
    if (mPayload == null)
    {
      builder.append("null");
      

      return true;
    }
    
    if (mRsv1)
    {


      builder.append("compressed");
      

      return true;
    }
    

    return false;
  }
  

  private void appendPayloadText(StringBuilder builder)
  {
    if (appendPayloadCommon(builder))
    {

      return;
    }
    
    builder.append("\"");
    builder.append(getPayloadText());
    builder.append("\"");
  }
  



  private void appendPayloadClose(StringBuilder builder)
  {
    builder.append(",CloseCode=").append(getCloseCode()).append(",Reason=");
    
    String reason = getCloseReason();
    
    if (reason == null)
    {
      builder.append("null");
    }
    else
    {
      builder.append("\"").append(reason).append("\"");
    }
  }
  

  private void appendPayloadBinary(StringBuilder builder)
  {
    if (appendPayloadCommon(builder))
    {

      return;
    }
    
    for (int i = 0; i < mPayload.length; i++)
    {
      builder.append(String.format("%02X ", new Object[] { Integer.valueOf(0xFF & mPayload[i]) }));
    }
    
    if (mPayload.length != 0)
    {

      builder.setLength(builder.length() - 1);
    }
  }
  










  public static WebSocketFrame createContinuationFrame()
  {
    return 
      new WebSocketFrame().setOpcode(0);
  }
  













  public static WebSocketFrame createContinuationFrame(byte[] payload)
  {
    return createContinuationFrame().setPayload(payload);
  }
  













  public static WebSocketFrame createContinuationFrame(String payload)
  {
    return createContinuationFrame().setPayload(payload);
  }
  












  public static WebSocketFrame createTextFrame(String payload)
  {
    return 
    

      new WebSocketFrame().setFin(true).setOpcode(1).setPayload(payload);
  }
  












  public static WebSocketFrame createBinaryFrame(byte[] payload)
  {
    return 
    

      new WebSocketFrame().setFin(true).setOpcode(2).setPayload(payload);
  }
  









  public static WebSocketFrame createCloseFrame()
  {
    return 
    
      new WebSocketFrame().setFin(true).setOpcode(8);
  }
  














  public static WebSocketFrame createCloseFrame(int closeCode)
  {
    return createCloseFrame().setCloseFramePayload(closeCode, null);
  }
  




















  public static WebSocketFrame createCloseFrame(int closeCode, String reason)
  {
    return createCloseFrame().setCloseFramePayload(closeCode, reason);
  }
  









  public static WebSocketFrame createPingFrame()
  {
    return 
    
      new WebSocketFrame().setFin(true).setOpcode(9);
  }
  















  public static WebSocketFrame createPingFrame(byte[] payload)
  {
    return createPingFrame().setPayload(payload);
  }
  















  public static WebSocketFrame createPingFrame(String payload)
  {
    return createPingFrame().setPayload(payload);
  }
  









  public static WebSocketFrame createPongFrame()
  {
    return 
    
      new WebSocketFrame().setFin(true).setOpcode(10);
  }
  















  public static WebSocketFrame createPongFrame(byte[] payload)
  {
    return createPongFrame().setPayload(payload);
  }
  















  public static WebSocketFrame createPongFrame(String payload)
  {
    return createPongFrame().setPayload(payload);
  }
  






















  static byte[] mask(byte[] maskingKey, byte[] payload)
  {
    if ((maskingKey == null) || (maskingKey.length < 4) || (payload == null))
    {
      return payload;
    }
    
    for (int i = 0; i < payload.length; i++)
    {
      int tmp26_25 = i; byte[] tmp26_24 = payload;tmp26_24[tmp26_25] = ((byte)(tmp26_24[tmp26_25] ^ maskingKey[(i % 4)]));
    }
    
    return payload;
  }
  


  static WebSocketFrame compressFrame(WebSocketFrame frame, PerMessageCompressionExtension pmce)
  {
    if (pmce == null)
    {

      return frame;
    }
    

    if ((!frame.isTextFrame()) && 
      (!frame.isBinaryFrame()))
    {

      return frame;
    }
    

    if (!frame.getFin())
    {



      return frame;
    }
    

    if (frame.getRsv1())
    {




      return frame;
    }
    

    byte[] payload = frame.getPayload();
    

    if ((payload == null) || (payload.length == 0))
    {

      return frame;
    }
    

    byte[] compressed = compress(payload, pmce);
    


    if (payload.length <= compressed.length)
    {

      return frame;
    }
    

    frame.setPayload(compressed);
    

    frame.setRsv1(true);
    
    return frame;
  }
  


  private static byte[] compress(byte[] data, PerMessageCompressionExtension pmce)
  {
    try
    {
      return pmce.compress(data);
    }
    catch (WebSocketException e) {}
    



    return data;
  }
  




  static List<WebSocketFrame> splitIfNecessary(WebSocketFrame frame, int maxPayloadSize, PerMessageCompressionExtension pmce)
  {
    if (maxPayloadSize == 0)
    {

      return null;
    }
    


    if (frame.getPayloadLength() <= maxPayloadSize)
    {

      return null;
    }
    

    if ((frame.isBinaryFrame()) || (frame.isTextFrame()))
    {




      frame = compressFrame(frame, pmce);
      


      if (frame.getPayloadLength() <= maxPayloadSize)
      {

        return null;
      }
    }
    else if (!frame.isContinuationFrame())
    {

      return null;
    }
    

    return split(frame, maxPayloadSize);
  }
  


  private static List<WebSocketFrame> split(WebSocketFrame frame, int maxPayloadSize)
  {
    byte[] originalPayload = frame.getPayload();
    boolean originalFin = frame.getFin();
    
    List<WebSocketFrame> frames = new ArrayList();
    


    byte[] payload = Arrays.copyOf(originalPayload, maxPayloadSize);
    frame.setFin(false).setPayload(payload);
    frames.add(frame);
    
    for (int from = maxPayloadSize; from < originalPayload.length; from += maxPayloadSize)
    {

      int to = Math.min(from + maxPayloadSize, originalPayload.length);
      payload = Arrays.copyOfRange(originalPayload, from, to);
      

      WebSocketFrame cont = createContinuationFrame(payload);
      frames.add(cont);
    }
    
    if (originalFin)
    {

      ((WebSocketFrame)frames.get(frames.size() - 1)).setFin(true);
    }
    
    return frames;
  }
}
