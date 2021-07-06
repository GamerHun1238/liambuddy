package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.MFALevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public abstract interface GuildManager
  extends Manager<GuildManager>
{
  public static final long NAME = 1L;
  public static final long REGION = 2L;
  public static final long ICON = 4L;
  public static final long SPLASH = 8L;
  public static final long AFK_CHANNEL = 16L;
  public static final long AFK_TIMEOUT = 32L;
  public static final long SYSTEM_CHANNEL = 64L;
  public static final long MFA_LEVEL = 128L;
  public static final long NOTIFICATION_LEVEL = 256L;
  public static final long EXPLICIT_CONTENT_LEVEL = 512L;
  public static final long VERIFICATION_LEVEL = 1024L;
  public static final long BANNER = 2048L;
  public static final long VANITY_URL = 4096L;
  public static final long DESCRIPTION = 8192L;
  public static final long RULES_CHANNEL = 16384L;
  public static final long COMMUNITY_UPDATES_CHANNEL = 32768L;
  
  @Nonnull
  public abstract GuildManager reset(long paramLong);
  
  @Nonnull
  public abstract GuildManager reset(long... paramVarArgs);
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  @Deprecated
  @ReplaceWith("ChannelManager.setRegion()")
  @DeprecatedSince("4.3.0")
  public abstract GuildManager setRegion(@Nonnull Region paramRegion);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setIcon(@Nullable Icon paramIcon);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setSplash(@Nullable Icon paramIcon);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setAfkChannel(@Nullable VoiceChannel paramVoiceChannel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setSystemChannel(@Nullable TextChannel paramTextChannel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setRulesChannel(@Nullable TextChannel paramTextChannel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setCommunityUpdatesChannel(@Nullable TextChannel paramTextChannel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setAfkTimeout(@Nonnull Guild.Timeout paramTimeout);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setVerificationLevel(@Nonnull Guild.VerificationLevel paramVerificationLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setDefaultNotificationLevel(@Nonnull Guild.NotificationLevel paramNotificationLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setRequiredMFALevel(@Nonnull Guild.MFALevel paramMFALevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setExplicitContentLevel(@Nonnull Guild.ExplicitContentLevel paramExplicitContentLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setBanner(@Nullable Icon paramIcon);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setVanityCode(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildManager setDescription(@Nullable String paramString);
}
