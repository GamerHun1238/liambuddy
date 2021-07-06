package net.dv8tion.jda.api.events.channel.voice.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;























public class VoiceChannelUpdatePositionEvent
  extends GenericVoiceChannelUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "position";
  
  public VoiceChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldPosition)
  {
    super(api, responseNumber, channel, Integer.valueOf(oldPosition), Integer.valueOf(channel.getPositionRaw()), "position");
  }
  





  public int getOldPosition()
  {
    return ((Integer)getOldValue()).intValue();
  }
  





  public int getNewPosition()
  {
    return ((Integer)getNewValue()).intValue();
  }
}
