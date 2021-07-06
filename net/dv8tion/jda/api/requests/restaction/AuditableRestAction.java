package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface AuditableRestAction<T>
  extends RestAction<T>
{
  @Nonnull
  public abstract AuditableRestAction<T> reason(@Nullable String paramString);
  
  @Nonnull
  public abstract AuditableRestAction<T> setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract AuditableRestAction<T> timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract AuditableRestAction<T> deadline(long paramLong);
}
