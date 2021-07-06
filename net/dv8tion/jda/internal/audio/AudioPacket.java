package net.dv8tion.jda.internal.audio;

import com.iwebpp.crypto.TweetNaclFast.SecretBox;
import java.net.DatagramPacket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import net.dv8tion.jda.internal.utils.IOUtil;
import org.slf4j.Logger;









































public class AudioPacket
{
  public static final int RTP_HEADER_BYTE_LENGTH = 12;
  public static final byte RTP_VERSION_PAD_EXTEND = -128;
  public static final byte RTP_PAYLOAD_TYPE = 120;
  public static final short RTP_DISCORD_EXTENSION = -16674;
  public static final int PT_INDEX = 1;
  public static final int SEQ_INDEX = 2;
  public static final int TIMESTAMP_INDEX = 4;
  public static final int SSRC_INDEX = 8;
  private final byte type;
  private final char seq;
  private final int timestamp;
  private final int ssrc;
  private final byte[] rawPacket;
  private final ByteBuffer encodedAudio;
  
  public AudioPacket(DatagramPacket packet)
  {
    this(Arrays.copyOf(packet.getData(), packet.getLength()));
  }
  
  public AudioPacket(byte[] rawPacket)
  {
    this.rawPacket = rawPacket;
    
    ByteBuffer buffer = ByteBuffer.wrap(rawPacket);
    seq = buffer.getChar(2);
    timestamp = buffer.getInt(4);
    ssrc = buffer.getInt(8);
    type = buffer.get(1);
    
    byte profile = buffer.get(0);
    byte[] data = buffer.array();
    boolean hasExtension = (profile & 0x10) != 0;
    byte cc = (byte)(profile & 0xF);
    int csrcLength = cc * 4;
    
    short extension = hasExtension ? IOUtil.getShortBigEndian(data, 12 + csrcLength) : 0;
    
    int offset = 12 + csrcLength;
    if ((hasExtension) && (extension == 48862)) {
      offset = getPayloadOffset(data, csrcLength);
    }
    encodedAudio = ByteBuffer.allocate(data.length - offset);
    encodedAudio.put(data, offset, encodedAudio.capacity());
    encodedAudio.flip();
  }
  
  public AudioPacket(ByteBuffer buffer, char seq, int timestamp, int ssrc, ByteBuffer encodedAudio)
  {
    this.seq = seq;
    this.ssrc = ssrc;
    this.timestamp = timestamp;
    this.encodedAudio = encodedAudio;
    type = 120;
    rawPacket = generateRawPacket(buffer, seq, timestamp, ssrc, encodedAudio);
  }
  

  private int getPayloadOffset(byte[] data, int csrcLength)
  {
    short headerLength = IOUtil.getShortBigEndian(data, 14 + csrcLength);
    int i = 16 + csrcLength + headerLength * 4;
    




    while (data[i] == 0)
      i++;
    return i;
  }
  


  public byte[] getHeader()
  {
    return Arrays.copyOf(rawPacket, 12);
  }
  
  public byte[] getNoncePadded()
  {
    byte[] nonce = new byte[24];
    
    System.arraycopy(rawPacket, 0, nonce, 0, 12);
    return nonce;
  }
  
  public byte[] getRawPacket()
  {
    return rawPacket;
  }
  
  public ByteBuffer getEncodedAudio()
  {
    return encodedAudio;
  }
  
  public char getSequence()
  {
    return seq;
  }
  
  public int getSSRC()
  {
    return ssrc;
  }
  
  public int getTimestamp()
  {
    return timestamp;
  }
  



  protected ByteBuffer asEncryptedPacket(TweetNaclFast.SecretBox boxer, ByteBuffer buffer, byte[] nonce, int nlen)
  {
    byte[] extendedNonce = nonce;
    if (nlen == 0) {
      extendedNonce = getNoncePadded();
    }
    
    byte[] array = encodedAudio.array();
    int offset = encodedAudio.arrayOffset() + encodedAudio.position();
    int length = encodedAudio.remaining();
    byte[] encryptedAudio = boxer.box(array, offset, length, extendedNonce);
    
    buffer.clear();
    int capacity = 12 + encryptedAudio.length + nlen;
    if (capacity > buffer.remaining())
      buffer = ByteBuffer.allocate(capacity);
    populateBuffer(seq, timestamp, ssrc, ByteBuffer.wrap(encryptedAudio), buffer);
    if (nlen > 0) {
      buffer.put(nonce, 0, nlen);
    }
    buffer.flip();
    return buffer;
  }
  
  protected static AudioPacket decryptAudioPacket(AudioEncryption encryption, DatagramPacket packet, byte[] secretKey)
  {
    TweetNaclFast.SecretBox boxer = new TweetNaclFast.SecretBox(secretKey);
    AudioPacket encryptedPacket = new AudioPacket(packet);
    if (type != 120) {
      return null;
    }
    
    byte[] rawPacket = encryptedPacket.getRawPacket();
    byte[] extendedNonce; switch (1.$SwitchMap$net$dv8tion$jda$internal$audio$AudioEncryption[encryption.ordinal()])
    {
    case 1: 
      extendedNonce = encryptedPacket.getNoncePadded();
      break;
    case 2: 
      byte[] extendedNonce = new byte[24];
      System.arraycopy(rawPacket, rawPacket.length - extendedNonce.length, extendedNonce, 0, extendedNonce.length);
      break;
    case 3: 
      byte[] extendedNonce = new byte[24];
      System.arraycopy(rawPacket, rawPacket.length - 4, extendedNonce, 0, 4);
      break;
    default: 
      AudioConnection.LOG.debug("Failed to decrypt audio packet, unsupported encryption mode!");
      return null;
    }
    byte[] extendedNonce;
    ByteBuffer encodedAudio = encodedAudio;
    int length = encodedAudio.remaining();
    int offset = encodedAudio.arrayOffset() + encodedAudio.position();
    switch (1.$SwitchMap$net$dv8tion$jda$internal$audio$AudioEncryption[encryption.ordinal()])
    {
    case 1: 
      break;
    
    case 3: 
      length -= 4;
      break;
    case 2: 
      length -= 24;
      break;
    default: 
      AudioConnection.LOG.debug("Failed to decrypt audio packet, unsupported encryption mode!");
      return null;
    }
    
    byte[] decryptedAudio = boxer.open(encodedAudio.array(), offset, length, extendedNonce);
    if (decryptedAudio == null)
    {
      AudioConnection.LOG.trace("Failed to decrypt audio packet");
      return null;
    }
    byte[] decryptedRawPacket = new byte[12 + decryptedAudio.length];
    


    System.arraycopy(rawPacket, 0, decryptedRawPacket, 0, 12);
    System.arraycopy(decryptedAudio, 0, decryptedRawPacket, 12, decryptedAudio.length);
    
    return new AudioPacket(decryptedRawPacket);
  }
  
  private static byte[] generateRawPacket(ByteBuffer buffer, char seq, int timestamp, int ssrc, ByteBuffer data)
  {
    if (buffer == null)
      buffer = ByteBuffer.allocate(12 + data.remaining());
    populateBuffer(seq, timestamp, ssrc, data, buffer);
    return buffer.array();
  }
  
  private static void populateBuffer(char seq, int timestamp, int ssrc, ByteBuffer data, ByteBuffer buffer)
  {
    buffer.put((byte)Byte.MIN_VALUE);
    buffer.put((byte)120);
    buffer.putChar(seq);
    buffer.putInt(timestamp);
    buffer.putInt(ssrc);
    buffer.put(data);
    data.flip();
  }
}
