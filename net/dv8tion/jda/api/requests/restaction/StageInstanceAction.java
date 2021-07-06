package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.StageInstance.PrivacyLevel;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface StageInstanceAction
  extends RestAction<StageInstance>
{
  @Nonnull
  public abstract StageInstanceAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract StageInstanceAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract StageInstanceAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract StageInstanceAction setTopic(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract StageInstanceAction setPrivacyLevel(@Nonnull StageInstance.PrivacyLevel paramPrivacyLevel);
}
