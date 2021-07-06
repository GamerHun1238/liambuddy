package net.dv8tion.jda.api.managers;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.managers.ManagerBase;






























public abstract interface Manager<M extends Manager<M>>
  extends AuditableRestAction<Void>
{
  public static void setPermissionChecksEnabled(boolean enable)
  {
    ManagerBase.setPermissionChecksEnabled(enable);
  }
  











  public static boolean isPermissionChecksEnabled()
  {
    return ManagerBase.isPermissionChecksEnabled();
  }
  
  @Nonnull
  public abstract M setCheck(BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract M timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract M deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract M reset(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract M reset(long... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public abstract M reset();
}
