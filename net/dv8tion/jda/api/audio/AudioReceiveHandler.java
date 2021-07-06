package net.dv8tion.jda.api.audio;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import net.dv8tion.jda.api.entities.User;
























public abstract interface AudioReceiveHandler
{
  public static final AudioFormat OUTPUT_FORMAT = new AudioFormat(48000.0F, 16, 2, true, true);
  






  public boolean canReceiveCombined()
  {
    return false;
  }
  





  public boolean canReceiveUser()
  {
    return false;
  }
  











  public boolean canReceiveEncoded()
  {
    return false;
  }
  


















  public void handleEncodedAudio(@Nonnull OpusPacket packet) {}
  


















  public void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {}
  


















  public void handleUserAudio(@Nonnull UserAudio userAudio) {}
  

















  public boolean includeUserInCombinedAudio(@Nonnull User user)
  {
    return true;
  }
}
