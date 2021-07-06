package net.dv8tion.jda.api.audio;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.audio.AudioPacket;
import net.dv8tion.jda.internal.audio.Decoder;






























public final class OpusPacket
  implements Comparable<OpusPacket>
{
  public static final int OPUS_SAMPLE_RATE = 48000;
  public static final int OPUS_FRAME_SIZE = 960;
  public static final int OPUS_FRAME_TIME_AMOUNT = 20;
  public static final int OPUS_CHANNEL_COUNT = 2;
  private final long userId;
  private final byte[] opusAudio;
  private final Decoder decoder;
  private final AudioPacket rawPacket;
  private short[] decoded;
  private boolean triedDecode;
  
  public OpusPacket(@Nonnull AudioPacket packet, long userId, @Nullable Decoder decoder)
  {
    rawPacket = packet;
    this.userId = userId;
    this.decoder = decoder;
    opusAudio = packet.getEncodedAudio().array();
  }
  











  public char getSequence()
  {
    return rawPacket.getSequence();
  }
  







  public int getTimestamp()
  {
    return rawPacket.getTimestamp();
  }
  







  public int getSSRC()
  {
    return rawPacket.getSSRC();
  }
  





  public long getUserId()
  {
    return userId;
  }
  





  public boolean canDecode()
  {
    return (decoder != null) && (decoder.isInOrder(getSequence()));
  }
  






  @Nonnull
  public byte[] getOpusAudio()
  {
    return Arrays.copyOf(opusAudio, opusAudio.length);
  }
  















  @Nullable
  public synchronized short[] decode()
  {
    if (triedDecode)
      return decoded;
    if (decoder == null)
      throw new IllegalStateException("No decoder available");
    if (!decoder.isInOrder(getSequence()))
      throw new IllegalStateException("Packet is not in order");
    triedDecode = true;
    return this.decoded = decoder.decodeFromOpus(rawPacket);
  }
  














  @Nonnull
  public byte[] getAudioData(double volume)
  {
    return getAudioData(decode(), volume);
  }
  
















  @Nonnull
  public static byte[] getAudioData(@Nonnull short[] decoded, double volume)
  {
    if (decoded == null)
      throw new IllegalArgumentException("Cannot get audio data from null");
    int byteIndex = 0;
    byte[] audio = new byte[decoded.length * 2];
    for (short s : decoded)
    {
      if (volume != 1.0D) {
        s = (short)(int)(s * volume);
      }
      byte leftByte = (byte)(s >>> 8 & 0xFF);
      byte rightByte = (byte)(s & 0xFF);
      audio[byteIndex] = leftByte;
      audio[(byteIndex + 1)] = rightByte;
      byteIndex += 2;
    }
    return audio;
  }
  

  public int compareTo(@Nonnull OpusPacket o)
  {
    return getSequence() - o.getSequence();
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { Character.valueOf(getSequence()), Integer.valueOf(getTimestamp()), getOpusAudio() });
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof OpusPacket))
      return false;
    OpusPacket other = (OpusPacket)obj;
    return (getSequence() == other.getSequence()) && 
      (getTimestamp() == other.getTimestamp()) && 
      (getSSRC() == other.getSSRC());
  }
}
