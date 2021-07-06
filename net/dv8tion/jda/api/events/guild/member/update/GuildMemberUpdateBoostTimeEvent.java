package net.dv8tion.jda.api.events.guild.member.update;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;




































public class GuildMemberUpdateBoostTimeEvent
  extends GenericGuildMemberUpdateEvent<OffsetDateTime>
{
  public static final String IDENTIFIER = "boost_time";
  
  public GuildMemberUpdateBoostTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable OffsetDateTime previous)
  {
    super(api, responseNumber, member, previous, member.getTimeBoosted(), "boost_time");
  }
  





  @Nullable
  public OffsetDateTime getOldTimeBoosted()
  {
    return (OffsetDateTime)getOldValue();
  }
  





  @Nullable
  public OffsetDateTime getNewTimeBoosted()
  {
    return (OffsetDateTime)getNewValue();
  }
}
