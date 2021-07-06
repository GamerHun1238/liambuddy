package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfUser;

public abstract interface AccountManager
  extends Manager<AccountManager>
{
  public static final long NAME = 1L;
  public static final long AVATAR = 2L;
  
  @Nonnull
  public abstract SelfUser getSelfUser();
  
  @Nonnull
  @CheckReturnValue
  public abstract AccountManager reset(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract AccountManager reset(long... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public abstract AccountManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract AccountManager setAvatar(@Nullable Icon paramIcon);
}
