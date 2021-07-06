package net.dv8tion.jda.api.events.guild.member.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;







































@Incubating
public class GuildMemberUpdatePendingEvent
  extends GenericGuildMemberUpdateEvent<Boolean>
{
  public static final String IDENTIFIER = "pending";
  
  public GuildMemberUpdatePendingEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean previous)
  {
    super(api, responseNumber, member, Boolean.valueOf(previous), Boolean.valueOf(member.isPending()), "pending");
  }
  





  public boolean getOldPending()
  {
    return ((Boolean)getOldValue()).booleanValue();
  }
  





  public boolean getNewPending()
  {
    return ((Boolean)getNewValue()).booleanValue();
  }
}
