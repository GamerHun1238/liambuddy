package net.dv8tion.jda.api.events.channel.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;





















public class VoiceChannelDeleteEvent
  extends GenericVoiceChannelEvent
{
  public VoiceChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel)
  {
    super(api, responseNumber, channel);
  }
}
