package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface MemberAction
  extends RestAction<Void>
{
  @Nonnull
  public abstract MemberAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract MemberAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract MemberAction deadline(long paramLong);
  
  @Nonnull
  public abstract String getAccessToken();
  
  @Nonnull
  public abstract String getUserId();
  
  @Nullable
  public abstract User getUser();
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  @CheckReturnValue
  public abstract MemberAction setNickname(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract MemberAction setRoles(@Nullable Collection<Role> paramCollection);
  
  @Nonnull
  @CheckReturnValue
  public abstract MemberAction setRoles(@Nullable Role... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public abstract MemberAction setMute(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract MemberAction setDeafen(boolean paramBoolean);
}
