package com.neovisionaries.ws.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

















class WebSocketOutputStream
  extends BufferedOutputStream
{
  public WebSocketOutputStream(OutputStream out)
  {
    super(out);
  }
  

  public void write(String string)
    throws IOException
  {
    byte[] bytes = Misc.getBytesUTF8(string);
    
    write(bytes);
  }
  
  public void write(WebSocketFrame frame)
    throws IOException
  {
    writeFrame0(frame);
    writeFrame1(frame);
    writeFrameExtendedPayloadLength(frame);
    

    byte[] maskingKey = Misc.nextBytes(4);
    

    write(maskingKey);
    

    writeFramePayload(frame, maskingKey);
  }
  




  private void writeFrame0(WebSocketFrame frame)
    throws IOException
  {
    int b = (frame.getFin() ? 128 : 0) | (frame.getRsv1() ? 64 : 0) | (frame.getRsv2() ? 32 : 0) | (frame.getRsv3() ? 16 : 0) | frame.getOpcode() & 0xF;
    
    write(b);
  }
  

  private void writeFrame1(WebSocketFrame frame)
    throws IOException
  {
    int b = 128;
    
    int len = frame.getPayloadLength();
    
    if (len <= 125)
    {
      b |= len;
    }
    else if (len <= 65535)
    {
      b |= 0x7E;
    }
    else
    {
      b |= 0x7F;
    }
    
    write(b);
  }
  
  private void writeFrameExtendedPayloadLength(WebSocketFrame frame)
    throws IOException
  {
    int len = frame.getPayloadLength();
    

    if (len <= 125) {
      return;
    }
    
    byte[] buf;
    if (len <= 65535)
    {
      byte[] buf = new byte[2];
      
      buf[1] = ((byte)(len & 0xFF));
      buf[0] = ((byte)(len >> 8 & 0xFF));
    } else {
      buf = new byte[8];
      for (int i = 7; i >= 0; i--) {
        buf[i] = ((byte)(len & 0xFF));
        len >>>= 8;
      }
    }
    write(buf);
  }
  
  private void writeFramePayload(WebSocketFrame frame, byte[] maskingKey)
    throws IOException
  {
    byte[] payload = frame.getPayload();
    
    if (payload == null)
    {
      return;
    }
    
    byte[] masked = new byte[payload.length];
    
    for (int i = 0; i < payload.length; i++)
    {

      masked[i] = ((byte)((payload[i] ^ maskingKey[(i % 4)]) & 0xFF));
    }
    
    write(masked);
  }
}
