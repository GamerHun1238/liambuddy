package net.dv8tion.jda.api.events.guild.member;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;


























public abstract class GenericGuildMemberEvent
  extends GenericGuildEvent
{
  private final Member member;
  
  public GenericGuildMemberEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
  {
    super(api, responseNumber, member.getGuild());
    this.member = member;
  }
  






  @Nonnull
  public User getUser()
  {
    return getMember().getUser();
  }
  





  @Nonnull
  public Member getMember()
  {
    return member;
  }
}
