package net.dv8tion.jda.internal.audio;

import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.slf4j.Logger;
import tomp2p.opuswrapper.Opus;





















public class Decoder
{
  protected int ssrc;
  protected char lastSeq;
  protected int lastTimestamp;
  protected PointerByReference opusDecoder;
  
  protected Decoder(int ssrc)
  {
    this.ssrc = ssrc;
    lastSeq = 65535;
    lastTimestamp = -1;
    
    IntBuffer error = IntBuffer.allocate(1);
    opusDecoder = Opus.INSTANCE.opus_decoder_create(48000, 2, error);
    if ((error.get() != 0) && (opusDecoder == null)) {
      throw new IllegalStateException("Received error code from opus_decoder_create(...): " + error.get());
    }
  }
  
  public boolean isInOrder(char newSeq) {
    return (lastSeq == 65535) || (newSeq > lastSeq) || (lastSeq - newSeq > 10);
  }
  
  public boolean wasPacketLost(char newSeq)
  {
    return newSeq > lastSeq + '\001';
  }
  

  public short[] decodeFromOpus(AudioPacket decryptedPacket)
  {
    ShortBuffer decoded = ShortBuffer.allocate(4096);
    int result; if (decryptedPacket == null)
    {
      int result = Opus.INSTANCE.opus_decode(opusDecoder, null, 0, decoded, 960, 0);
      lastSeq = 65535;
      lastTimestamp = -1;
    }
    else
    {
      lastSeq = decryptedPacket.getSequence();
      lastTimestamp = decryptedPacket.getTimestamp();
      
      ByteBuffer encodedAudio = decryptedPacket.getEncodedAudio();
      int length = encodedAudio.remaining();
      int offset = encodedAudio.arrayOffset() + encodedAudio.position();
      byte[] buf = new byte[length];
      byte[] data = encodedAudio.array();
      System.arraycopy(data, offset, buf, 0, length);
      result = Opus.INSTANCE.opus_decode(opusDecoder, buf, buf.length, decoded, 960, 0);
    }
    

    if (result < 0)
    {
      handleDecodeError(result);
      return null;
    }
    
    short[] audio = new short[result * 2];
    decoded.get(audio);
    return audio;
  }
  
  private void handleDecodeError(int result)
  {
    StringBuilder b = new StringBuilder("Decoder failed to decode audio from user with code ");
    switch (result)
    {
    case -1: 
      b.append("OPUS_BAD_ARG");
      break;
    case -2: 
      b.append("OPUS_BUFFER_TOO_SMALL");
      break;
    case -3: 
      b.append("OPUS_INTERNAL_ERROR");
      break;
    case -4: 
      b.append("OPUS_INVALID_PACKET");
      break;
    case -5: 
      b.append("OPUS_UNIMPLEMENTED");
      break;
    case -6: 
      b.append("OPUS_INVALID_STATE");
      break;
    case -7: 
      b.append("OPUS_ALLOC_FAIL");
      break;
    default: 
      b.append(result);
    }
    AudioConnection.LOG.debug("{}", b);
  }
  
  protected synchronized void close()
  {
    if (opusDecoder != null)
    {
      Opus.INSTANCE.opus_decoder_destroy(opusDecoder);
      opusDecoder = null;
    }
  }
  

  protected void finalize()
    throws Throwable
  {
    super.finalize();
    close();
  }
}
