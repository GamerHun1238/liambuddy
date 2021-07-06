package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;



































public class GuildVoiceSuppressEvent
  extends GenericGuildVoiceEvent
{
  protected final boolean suppressed;
  
  public GuildVoiceSuppressEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
  {
    super(api, responseNumber, member);
    suppressed = member.getVoiceState().isSuppressed();
  }
  






  public boolean isSuppressed()
  {
    return suppressed;
  }
}
