package net.dv8tion.jda.api.events.channel.voice.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;























public class VoiceChannelUpdateBitrateEvent
  extends GenericVoiceChannelUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "bitrate";
  
  public VoiceChannelUpdateBitrateEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldBitrate)
  {
    super(api, responseNumber, channel, Integer.valueOf(oldBitrate), Integer.valueOf(channel.getBitrate()), "bitrate");
  }
  





  public int getOldBitrate()
  {
    return ((Integer)getOldValue()).intValue();
  }
  





  public int getNewBitrate()
  {
    return ((Integer)getNewValue()).intValue();
  }
}
