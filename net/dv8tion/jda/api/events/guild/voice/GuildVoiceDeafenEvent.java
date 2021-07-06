package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;



































public class GuildVoiceDeafenEvent
  extends GenericGuildVoiceEvent
{
  protected final boolean deafened;
  
  public GuildVoiceDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
  {
    super(api, responseNumber, member);
    deafened = member.getVoiceState().isDeafened();
  }
  






  public boolean isDeafened()
  {
    return deafened;
  }
}
