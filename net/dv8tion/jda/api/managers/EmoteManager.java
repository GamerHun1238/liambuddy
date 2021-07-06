package net.dv8tion.jda.api.managers;

import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;












































































public abstract interface EmoteManager
  extends Manager<EmoteManager>
{
  public static final long NAME = 1L;
  public static final long ROLES = 2L;
  
  @Nonnull
  public abstract EmoteManager reset(long paramLong);
  
  @Nonnull
  public abstract EmoteManager reset(long... paramVarArgs);
  
  @Nonnull
  public Guild getGuild()
  {
    return getEmote().getGuild();
  }
  
  @Nonnull
  public abstract Emote getEmote();
  
  @Nonnull
  @CheckReturnValue
  public abstract EmoteManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract EmoteManager setRoles(@Nullable Set<Role> paramSet);
}
