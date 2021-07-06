package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateMaxMembersEvent
  extends GenericGuildUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "max_members";
  
  public GuildUpdateMaxMembersEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
  {
    super(api, responseNumber, guild, Integer.valueOf(previous), Integer.valueOf(guild.getMaxMembers()), "max_members");
  }
  





  public int getOldMaxMembers()
  {
    return getOldValue().intValue();
  }
  





  public int getNewMaxMembers()
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
