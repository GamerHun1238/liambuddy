package net.dv8tion.jda.api.events.guild.member.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;



































public class GuildMemberUpdateNicknameEvent
  extends GenericGuildMemberUpdateEvent<String>
{
  public static final String IDENTIFIER = "nick";
  
  public GuildMemberUpdateNicknameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable String oldNick)
  {
    super(api, responseNumber, member, oldNick, member.getNickname(), "nick");
  }
  





  @Nullable
  public String getOldNickname()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getNewNickname()
  {
    return (String)getNewValue();
  }
}
