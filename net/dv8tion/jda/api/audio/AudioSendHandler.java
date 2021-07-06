package net.dv8tion.jda.api.audio;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;























public abstract interface AudioSendHandler
{
  public static final AudioFormat INPUT_FORMAT = new AudioFormat(48000.0F, 16, 2, true, true);
  












  public abstract boolean canProvide();
  












  @Nullable
  public abstract ByteBuffer provide20MsAudio();
  












  public boolean isOpus()
  {
    return false;
  }
}
