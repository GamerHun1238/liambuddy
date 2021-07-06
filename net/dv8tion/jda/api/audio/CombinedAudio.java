package net.dv8tion.jda.api.audio;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;





















public class CombinedAudio
{
  protected List<User> users;
  protected short[] audioData;
  
  public CombinedAudio(@Nonnull List<User> users, @Nonnull short[] audioData)
  {
    this.users = Collections.unmodifiableList(users);
    this.audioData = audioData;
  }
  








  @Nonnull
  public List<User> getUsers()
  {
    return users;
  }
  














  @Nonnull
  public byte[] getAudioData(double volume)
  {
    return OpusPacket.getAudioData(audioData, volume);
  }
}
