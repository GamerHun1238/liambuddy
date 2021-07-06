package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

































public class GuildVoiceSelfMuteEvent
  extends GenericGuildVoiceEvent
{
  protected final boolean selfMuted;
  
  public GuildVoiceSelfMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
  {
    super(api, responseNumber, member);
    selfMuted = member.getVoiceState().isSelfMuted();
  }
  






  public boolean isSelfMuted()
  {
    return selfMuted;
  }
}
