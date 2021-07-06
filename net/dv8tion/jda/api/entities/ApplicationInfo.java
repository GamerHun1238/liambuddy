package net.dv8tion.jda.api.entities;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;




































































public abstract interface ApplicationInfo
  extends ISnowflake
{
  public abstract boolean doesBotRequireCodeGrant();
  
  @Nonnull
  public abstract String getDescription();
  
  @Nullable
  public abstract String getIconId();
  
  @Nullable
  public abstract String getIconUrl();
  
  @Nullable
  public abstract ApplicationTeam getTeam();
  
  @Nonnull
  public ApplicationInfo setRequiredScopes(@Nonnull String... scopes)
  {
    Checks.noneNull(scopes, "Scopes");
    return setRequiredScopes(Arrays.asList(scopes));
  }
  













  @Nonnull
  public abstract ApplicationInfo setRequiredScopes(@Nonnull Collection<String> paramCollection);
  












  @Nonnull
  public String getInviteUrl(@Nullable Collection<Permission> permissions)
  {
    return getInviteUrl(null, permissions);
  }
  












  @Nonnull
  public String getInviteUrl(@Nullable Permission... permissions)
  {
    return getInviteUrl(null, permissions);
  }
  

















  @Nonnull
  public abstract String getInviteUrl(@Nullable String paramString, @Nullable Collection<Permission> paramCollection);
  
















  @Nonnull
  public String getInviteUrl(long guildId, @Nullable Collection<Permission> permissions)
  {
    return getInviteUrl(Long.toUnsignedString(guildId), permissions);
  }
  


















  @Nonnull
  public String getInviteUrl(@Nullable String guildId, @Nullable Permission... permissions)
  {
    return getInviteUrl(guildId, permissions == null ? null : Arrays.asList(permissions));
  }
  















  @Nonnull
  public String getInviteUrl(long guildId, @Nullable Permission... permissions)
  {
    return getInviteUrl(Long.toUnsignedString(guildId), permissions);
  }
  
  @Nonnull
  public abstract JDA getJDA();
  
  @Nonnull
  public abstract String getName();
  
  @Nonnull
  public abstract User getOwner();
  
  public abstract boolean isBotPublic();
}
