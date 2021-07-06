package net.dv8tion.jda.api.events.guild.override;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;





















public class PermissionOverrideUpdateEvent
  extends GenericPermissionOverrideEvent
{
  private final long oldAllow;
  private final long oldDeny;
  
  public PermissionOverrideUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull GuildChannel channel, @Nonnull PermissionOverride override, long oldAllow, long oldDeny)
  {
    super(api, responseNumber, channel, override);
    this.oldAllow = oldAllow;
    this.oldDeny = oldDeny;
  }
  





  public long getOldAllowRaw()
  {
    return oldAllow;
  }
  





  public long getOldDenyRaw()
  {
    return oldDeny;
  }
  





  public long getOldInheritedRaw()
  {
    return (oldAllow | oldDeny) ^ 0xFFFFFFFFFFFFFFFF;
  }
  





  @Nonnull
  public EnumSet<Permission> getOldAllow()
  {
    return Permission.getPermissions(oldAllow);
  }
  





  @Nonnull
  public EnumSet<Permission> getOldDeny()
  {
    return Permission.getPermissions(oldDeny);
  }
  





  @Nonnull
  public EnumSet<Permission> getOldInherited()
  {
    return Permission.getPermissions(getOldInheritedRaw());
  }
}
