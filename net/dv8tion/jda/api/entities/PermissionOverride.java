package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public abstract interface PermissionOverride
  extends ISnowflake
{
  public abstract long getAllowedRaw();
  
  public abstract long getInheritRaw();
  
  public abstract long getDeniedRaw();
  
  @Nonnull
  public abstract EnumSet<Permission> getAllowed();
  
  @Nonnull
  public abstract EnumSet<Permission> getInherit();
  
  @Nonnull
  public abstract EnumSet<Permission> getDenied();
  
  @Nonnull
  public abstract JDA getJDA();
  
  @Nullable
  public abstract IPermissionHolder getPermissionHolder();
  
  @Nullable
  public abstract Member getMember();
  
  @Nullable
  public abstract Role getRole();
  
  @Nonnull
  public abstract GuildChannel getChannel();
  
  @Nonnull
  public abstract Guild getGuild();
  
  public abstract boolean isMemberOverride();
  
  public abstract boolean isRoleOverride();
  
  @Nonnull
  public abstract PermissionOverrideAction getManager();
  
  @Nonnull
  @CheckReturnValue
  public abstract AuditableRestAction<Void> delete();
}
