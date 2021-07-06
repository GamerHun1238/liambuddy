package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.StageInstance.PrivacyLevel;

public abstract interface StageInstanceManager
  extends Manager<StageInstanceManager>
{
  public static final long TOPIC = 1L;
  public static final long PRIVACY_LEVEL = 2L;
  
  @Nonnull
  public abstract StageInstanceManager reset(long paramLong);
  
  @Nonnull
  public abstract StageInstanceManager reset(long... paramVarArgs);
  
  @Nonnull
  public abstract StageInstance getStageInstance();
  
  @Nonnull
  @CheckReturnValue
  public abstract StageInstanceManager setTopic(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract StageInstanceManager setPrivacyLevel(@Nonnull StageInstance.PrivacyLevel paramPrivacyLevel);
}
