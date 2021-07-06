package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract interface GenericUserPresenceEvent
  extends GenericEvent
{
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract Member getMember();
}
