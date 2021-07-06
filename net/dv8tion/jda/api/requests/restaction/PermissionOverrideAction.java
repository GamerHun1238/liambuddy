package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;



































public abstract interface PermissionOverrideAction
  extends AuditableRestAction<PermissionOverride>
{
  @Nonnull
  public abstract PermissionOverrideAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract PermissionOverrideAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract PermissionOverrideAction deadline(long paramLong);
  
  @Nonnull
  public PermissionOverrideAction reset()
  {
    return resetAllow().resetDeny();
  }
  






  @Nonnull
  public abstract PermissionOverrideAction resetAllow();
  






  @Nonnull
  public abstract PermissionOverrideAction resetDeny();
  





  @Nonnull
  public abstract GuildChannel getChannel();
  





  @Nullable
  public abstract Role getRole();
  





  @Nullable
  public abstract Member getMember();
  





  @Nonnull
  public Guild getGuild()
  {
    return getChannel().getGuild();
  }
  









  public abstract long getAllow();
  








  @Nonnull
  public EnumSet<Permission> getAllowedPermissions()
  {
    return Permission.getPermissions(getAllow());
  }
  









  public abstract long getDeny();
  








  @Nonnull
  public EnumSet<Permission> getDeniedPermissions()
  {
    return Permission.getPermissions(getDeny());
  }
  











  public abstract long getInherited();
  











  @Nonnull
  public EnumSet<Permission> getInheritedPermissions()
  {
    return Permission.getPermissions(getInherited());
  }
  
















  public abstract boolean isMember();
  















  public abstract boolean isRole();
  















  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction setAllow(long paramLong);
  















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction setAllow(@Nullable Collection<Permission> permissions)
  {
    if ((permissions == null) || (permissions.isEmpty()))
      return setAllow(0L);
    Checks.noneNull(permissions, "Permissions");
    return setAllow(Permission.getRaw(permissions));
  }
  


















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction setAllow(@Nullable Permission... permissions)
  {
    if ((permissions == null) || (permissions.length == 0))
      return setAllow(0L);
    Checks.noneNull(permissions, "Permissions");
    return setAllow(Permission.getRaw(permissions));
  }
  














  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction grant(long paramLong);
  














  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction grant(@Nonnull Collection<Permission> permissions)
  {
    return grant(Permission.getRaw(permissions));
  }
  















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction grant(@Nonnull Permission... permissions)
  {
    return grant(Permission.getRaw(permissions));
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction setDeny(long paramLong);
  























  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction setDeny(@Nullable Collection<Permission> permissions)
  {
    if ((permissions == null) || (permissions.isEmpty()))
      return setDeny(0L);
    Checks.noneNull(permissions, "Permissions");
    return setDeny(Permission.getRaw(permissions));
  }
  


















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction setDeny(@Nullable Permission... permissions)
  {
    if ((permissions == null) || (permissions.length == 0))
      return setDeny(0L);
    Checks.noneNull(permissions, "Permissions");
    return setDeny(Permission.getRaw(permissions));
  }
  














  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction deny(long paramLong);
  














  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction deny(@Nonnull Collection<Permission> permissions)
  {
    return deny(Permission.getRaw(permissions));
  }
  















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction deny(@Nonnull Permission... permissions)
  {
    return deny(Permission.getRaw(permissions));
  }
  















  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction clear(long paramLong);
  















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction clear(@Nonnull Collection<Permission> permissions)
  {
    return clear(Permission.getRaw(permissions));
  }
  
















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction clear(@Nonnull Permission... permissions)
  {
    return clear(Permission.getRaw(permissions));
  }
  
























  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction setPermissions(long paramLong1, long paramLong2);
  























  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction setPermissions(@Nullable Collection<Permission> grantPermissions, @Nullable Collection<Permission> denyPermissions)
  {
    return setAllow(grantPermissions).setDeny(denyPermissions);
  }
}
