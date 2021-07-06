package net.dv8tion.jda.api.entities;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;











































































































































public abstract interface IPermissionHolder
  extends ISnowflake
{
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract EnumSet<Permission> getPermissions();
  
  @Nonnull
  public abstract EnumSet<Permission> getPermissions(@Nonnull GuildChannel paramGuildChannel);
  
  @Nonnull
  public abstract EnumSet<Permission> getPermissionsExplicit();
  
  @Nonnull
  public abstract EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel paramGuildChannel);
  
  public abstract boolean hasPermission(@Nonnull Permission... paramVarArgs);
  
  public abstract boolean hasPermission(@Nonnull Collection<Permission> paramCollection);
  
  public abstract boolean hasPermission(@Nonnull GuildChannel paramGuildChannel, @Nonnull Permission... paramVarArgs);
  
  public abstract boolean hasPermission(@Nonnull GuildChannel paramGuildChannel, @Nonnull Collection<Permission> paramCollection);
  
  public boolean hasAccess(@Nonnull GuildChannel channel)
  {
    Checks.notNull(channel, "Channel");
    return (channel.getType() == ChannelType.VOICE) || (channel.getType() == ChannelType.STAGE) ? 
      hasPermission(channel, new Permission[] { Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL }) : 
      hasPermission(channel, new Permission[] { Permission.VIEW_CHANNEL });
  }
  
  public abstract boolean canSync(@Nonnull GuildChannel paramGuildChannel1, @Nonnull GuildChannel paramGuildChannel2);
  
  public abstract boolean canSync(@Nonnull GuildChannel paramGuildChannel);
}
