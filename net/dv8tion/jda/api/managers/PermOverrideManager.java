package net.dv8tion.jda.api.managers;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.internal.utils.Checks;















































































public abstract interface PermOverrideManager
  extends Manager<PermOverrideManager>
{
  public static final long DENIED = 1L;
  public static final long ALLOWED = 2L;
  public static final long PERMISSIONS = 3L;
  
  @Nonnull
  public abstract PermOverrideManager reset(long paramLong);
  
  @Nonnull
  public abstract PermOverrideManager reset(long... paramVarArgs);
  
  @Nonnull
  public Guild getGuild()
  {
    return getPermissionOverride().getGuild();
  }
  







  @Nonnull
  public GuildChannel getChannel()
  {
    return getPermissionOverride().getChannel();
  }
  










  @Nonnull
  public abstract PermissionOverride getPermissionOverride();
  










  @Nonnull
  @CheckReturnValue
  public abstract PermOverrideManager grant(long paramLong);
  









  @Nonnull
  @CheckReturnValue
  public PermOverrideManager grant(@Nonnull Permission... permissions)
  {
    Checks.notNull(permissions, "Permissions");
    return grant(Permission.getRaw(permissions));
  }
  















  @Nonnull
  @CheckReturnValue
  public PermOverrideManager grant(@Nonnull Collection<Permission> permissions)
  {
    return grant(Permission.getRaw(permissions));
  }
  












  @Nonnull
  @CheckReturnValue
  public abstract PermOverrideManager deny(long paramLong);
  











  @Nonnull
  @CheckReturnValue
  public PermOverrideManager deny(@Nonnull Permission... permissions)
  {
    Checks.notNull(permissions, "Permissions");
    return deny(Permission.getRaw(permissions));
  }
  















  @Nonnull
  @CheckReturnValue
  public PermOverrideManager deny(@Nonnull Collection<Permission> permissions)
  {
    return deny(Permission.getRaw(permissions));
  }
  












  @Nonnull
  @CheckReturnValue
  public abstract PermOverrideManager clear(long paramLong);
  











  @Nonnull
  @CheckReturnValue
  public PermOverrideManager clear(@Nonnull Permission... permissions)
  {
    Checks.notNull(permissions, "Permissions");
    return clear(Permission.getRaw(permissions));
  }
  
















  @Nonnull
  @CheckReturnValue
  public PermOverrideManager clear(@Nonnull Collection<Permission> permissions)
  {
    return clear(Permission.getRaw(permissions));
  }
}
