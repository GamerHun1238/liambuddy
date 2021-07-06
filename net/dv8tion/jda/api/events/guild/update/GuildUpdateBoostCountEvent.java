package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateBoostCountEvent
  extends GenericGuildUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "boost_count";
  
  public GuildUpdateBoostCountEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
  {
    super(api, responseNumber, guild, Integer.valueOf(previous), Integer.valueOf(guild.getBoostCount()), "boost_count");
  }
  





  public int getOldBoostCount()
  {
    return getOldValue().intValue();
  }
  





  public int getNewBoostCount()
  {
    return getNewValue().intValue();
  }
  

  @Nonnull
  public Integer getOldValue()
  {
    return (Integer)super.getOldValue();
  }
  

  @Nonnull
  public Integer getNewValue()
  {
    return (Integer)super.getNewValue();
  }
}
