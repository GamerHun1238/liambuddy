package net.dv8tion.jda.api.requests.restaction.order;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public abstract interface RoleOrderAction
  extends OrderAction<Role, RoleOrderAction>
{
  @Nonnull
  public abstract Guild getGuild();
}
