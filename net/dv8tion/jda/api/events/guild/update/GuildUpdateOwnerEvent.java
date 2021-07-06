package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
























public class GuildUpdateOwnerEvent
  extends GenericGuildUpdateEvent<Member>
{
  public static final String IDENTIFIER = "owner";
  private final long prevId;
  private final long nextId;
  
  public GuildUpdateOwnerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable Member oldOwner, long prevId, long nextId)
  {
    super(api, responseNumber, guild, oldOwner, guild.getOwner(), "owner");
    this.prevId = prevId;
    this.nextId = nextId;
  }
  





  public long getNewOwnerIdLong()
  {
    return nextId;
  }
  





  @Nonnull
  public String getNewOwnerId()
  {
    return Long.toUnsignedString(nextId);
  }
  





  public long getOldOwnerIdLong()
  {
    return prevId;
  }
  





  @Nonnull
  public String getOldOwnerId()
  {
    return Long.toUnsignedString(prevId);
  }
  





  @Nullable
  public Member getOldOwner()
  {
    return (Member)getOldValue();
  }
  





  @Nullable
  public Member getNewOwner()
  {
    return (Member)getNewValue();
  }
}
