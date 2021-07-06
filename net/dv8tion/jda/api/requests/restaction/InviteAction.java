package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Invite;

public abstract interface InviteAction
  extends AuditableRestAction<Invite>
{
  @Nonnull
  public abstract InviteAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract InviteAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract InviteAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction setMaxAge(@Nullable Integer paramInteger);
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction setMaxAge(@Nullable Long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction setMaxUses(@Nullable Integer paramInteger);
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction setTemporary(@Nullable Boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction setUnique(@Nullable Boolean paramBoolean);
}
