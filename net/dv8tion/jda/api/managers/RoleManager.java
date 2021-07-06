package net.dv8tion.jda.api.managers;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;



























































































public abstract interface RoleManager
  extends Manager<RoleManager>
{
  public static final long NAME = 1L;
  public static final long COLOR = 2L;
  public static final long PERMISSION = 4L;
  public static final long HOIST = 8L;
  public static final long MENTIONABLE = 16L;
  
  @Nonnull
  public abstract RoleManager reset(long paramLong);
  
  @Nonnull
  public abstract RoleManager reset(long... paramVarArgs);
  
  @Nonnull
  public abstract Role getRole();
  
  @Nonnull
  public Guild getGuild()
  {
    return getRole().getGuild();
  }
  

















  @Nonnull
  @CheckReturnValue
  public abstract RoleManager setName(@Nonnull String paramString);
  

















  @Nonnull
  @CheckReturnValue
  public abstract RoleManager setPermissions(long paramLong);
  
















  @Nonnull
  @CheckReturnValue
  public RoleManager setPermissions(@Nonnull Permission... permissions)
  {
    Checks.notNull(permissions, "Permissions");
    return setPermissions(Arrays.asList(permissions));
  }
  





















  @Nonnull
  @CheckReturnValue
  public RoleManager setPermissions(@Nonnull Collection<Permission> permissions)
  {
    Checks.noneNull(permissions, "Permissions");
    return setPermissions(Permission.getRaw(permissions));
  }
  








  @Nonnull
  @CheckReturnValue
  public RoleManager setColor(@Nullable Color color)
  {
    return setColor(color == null ? 536870911 : color.getRGB());
  }
  











  @Nonnull
  @CheckReturnValue
  public abstract RoleManager setColor(int paramInt);
  











  @Nonnull
  @CheckReturnValue
  public abstract RoleManager setHoisted(boolean paramBoolean);
  











  @Nonnull
  @CheckReturnValue
  public abstract RoleManager setMentionable(boolean paramBoolean);
  











  @Nonnull
  @CheckReturnValue
  public RoleManager givePermissions(@Nonnull Permission... perms)
  {
    Checks.notNull(perms, "Permissions");
    return givePermissions(Arrays.asList(perms));
  }
  



















  @Nonnull
  @CheckReturnValue
  public abstract RoleManager givePermissions(@Nonnull Collection<Permission> paramCollection);
  


















  @Nonnull
  @CheckReturnValue
  public RoleManager revokePermissions(@Nonnull Permission... perms)
  {
    Checks.notNull(perms, "Permissions");
    return revokePermissions(Arrays.asList(perms));
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleManager revokePermissions(@Nonnull Collection<Permission> paramCollection);
}
