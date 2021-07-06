package net.dv8tion.jda.api.requests.restaction;

import java.awt.Color;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;









































































public abstract interface RoleAction
  extends AuditableRestAction<Role>
{
  @Nonnull
  public abstract RoleAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract RoleAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract RoleAction deadline(long paramLong);
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleAction setName(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleAction setHoisted(@Nullable Boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleAction setMentionable(@Nullable Boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public RoleAction setColor(@Nullable Color color)
  {
    return setColor(color != null ? Integer.valueOf(color.getRGB()) : null);
  }
  














  @Nonnull
  @CheckReturnValue
  public abstract RoleAction setColor(@Nullable Integer paramInteger);
  













  @Nonnull
  @CheckReturnValue
  public RoleAction setPermissions(@Nullable Permission... permissions)
  {
    if (permissions != null) {
      Checks.noneNull(permissions, "Permissions");
    }
    return setPermissions(permissions == null ? null : Long.valueOf(Permission.getRaw(permissions)));
  }
  


















  @Nonnull
  @CheckReturnValue
  public RoleAction setPermissions(@Nullable Collection<Permission> permissions)
  {
    if (permissions != null) {
      Checks.noneNull(permissions, "Permissions");
    }
    return setPermissions(permissions == null ? null : Long.valueOf(Permission.getRaw(permissions)));
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleAction setPermissions(@Nullable Long paramLong);
}
