package net.dv8tion.jda.api.audio;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;





















public class UserAudio
{
  protected User user;
  protected short[] audioData;
  
  public UserAudio(@Nonnull User user, @Nonnull short[] audioData)
  {
    this.user = user;
    this.audioData = audioData;
  }
  





  @Nonnull
  public User getUser()
  {
    return user;
  }
  












  @Nonnull
  public byte[] getAudioData(double volume)
  {
    return OpusPacket.getAudioData(audioData, volume);
  }
}
