package net.dv8tion.jda.internal.managers;

import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.MFALevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;


public class GuildManagerImpl
  extends ManagerBase<GuildManager>
  implements GuildManager
{
  protected Guild guild;
  protected String name;
  protected String region;
  protected Icon icon;
  protected Icon splash;
  protected Icon banner;
  protected String afkChannel;
  protected String systemChannel;
  protected String rulesChannel;
  protected String communityUpdatesChannel;
  protected String description;
  protected String vanityCode;
  protected int afkTimeout;
  protected int mfaLevel;
  protected int notificationLevel;
  protected int explicitContentLevel;
  protected int verificationLevel;
  
  public GuildManagerImpl(Guild guild)
  {
    super(guild.getJDA(), Route.Guilds.MODIFY_GUILD.compile(new String[] { guild.getId() }));
    JDA api = guild.getJDA();
    this.guild = guild;
    if (isPermissionChecksEnabled()) {
      checkPermissions();
    }
  }
  
  @Nonnull
  public Guild getGuild()
  {
    Guild realGuild = api.getGuildById(guild.getIdLong());
    if (realGuild != null)
      guild = realGuild;
    return guild;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl reset(long fields)
  {
    super.reset(fields);
    if ((fields & 1L) == 1L)
      name = null;
    if ((fields & 0x2) == 2L)
      region = null;
    if ((fields & 0x4) == 4L)
      icon = null;
    if ((fields & 0x8) == 8L)
      splash = null;
    if ((fields & 0x10) == 16L)
      afkChannel = null;
    if ((fields & 0x40) == 64L)
      systemChannel = null;
    if ((fields & 0x4000) == 16384L)
      rulesChannel = null;
    if ((fields & 0x8000) == 32768L)
      communityUpdatesChannel = null;
    if ((fields & 0x2000) == 8192L)
      description = null;
    if ((fields & 0x800) == 2048L)
      banner = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl reset()
  {
    super.reset();
    name = null;
    region = null;
    icon = null;
    splash = null;
    vanityCode = null;
    description = null;
    banner = null;
    afkChannel = null;
    systemChannel = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setName(@Nonnull String name)
  {
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 100, "Name");
    this.name = name;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setRegion(@Nonnull Region region)
  {
    Checks.notNull(region, "Region");
    Checks.check(region != Region.UNKNOWN, "Region must not be UNKNOWN");
    Checks.check((!region.isVip()) || (getGuild().getFeatures().contains("VIP_REGIONS")), "Cannot set a VIP voice region on this guild");
    this.region = region.getKey();
    set |= 0x2;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setIcon(Icon icon)
  {
    this.icon = icon;
    set |= 0x4;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setSplash(Icon splash)
  {
    checkFeature("INVITE_SPLASH");
    this.splash = splash;
    set |= 0x8;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setAfkChannel(VoiceChannel afkChannel)
  {
    Checks.check((afkChannel == null) || (afkChannel.getGuild().equals(getGuild())), "Channel must be from the same guild");
    this.afkChannel = (afkChannel == null ? null : afkChannel.getId());
    set |= 0x10;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setSystemChannel(TextChannel systemChannel)
  {
    Checks.check((systemChannel == null) || (systemChannel.getGuild().equals(getGuild())), "Channel must be from the same guild");
    this.systemChannel = (systemChannel == null ? null : systemChannel.getId());
    set |= 0x40;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setRulesChannel(TextChannel rulesChannel)
  {
    Checks.check((rulesChannel == null) || (rulesChannel.getGuild().equals(getGuild())), "Channel must be from the same guild");
    this.rulesChannel = (rulesChannel == null ? null : rulesChannel.getId());
    set |= 0x4000;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setCommunityUpdatesChannel(TextChannel communityUpdatesChannel)
  {
    Checks.check((communityUpdatesChannel == null) || (communityUpdatesChannel.getGuild().equals(getGuild())), "Channel must be from the same guild");
    this.communityUpdatesChannel = (communityUpdatesChannel == null ? null : communityUpdatesChannel.getId());
    set |= 0x8000;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setAfkTimeout(@Nonnull Guild.Timeout timeout)
  {
    Checks.notNull(timeout, "Timeout");
    afkTimeout = timeout.getSeconds();
    set |= 0x20;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setVerificationLevel(@Nonnull Guild.VerificationLevel level)
  {
    Checks.notNull(level, "Level");
    Checks.check(level != Guild.VerificationLevel.UNKNOWN, "Level must not be UNKNOWN");
    verificationLevel = level.getKey();
    set |= 0x400;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setDefaultNotificationLevel(@Nonnull Guild.NotificationLevel level)
  {
    Checks.notNull(level, "Level");
    Checks.check(level != Guild.NotificationLevel.UNKNOWN, "Level must not be UNKNOWN");
    notificationLevel = level.getKey();
    set |= 0x100;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setRequiredMFALevel(@Nonnull Guild.MFALevel level)
  {
    Checks.notNull(level, "Level");
    Checks.check(level != Guild.MFALevel.UNKNOWN, "Level must not be UNKNOWN");
    mfaLevel = level.getKey();
    set |= 0x80;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildManagerImpl setExplicitContentLevel(@Nonnull Guild.ExplicitContentLevel level)
  {
    Checks.notNull(level, "Level");
    Checks.check(level != Guild.ExplicitContentLevel.UNKNOWN, "Level must not be UNKNOWN");
    explicitContentLevel = level.getKey();
    set |= 0x200;
    return this;
  }
  

  @Nonnull
  public GuildManager setBanner(@Nullable Icon banner)
  {
    checkFeature("BANNER");
    this.banner = banner;
    set |= 0x800;
    return this;
  }
  

  @Nonnull
  public GuildManager setVanityCode(@Nullable String code)
  {
    checkFeature("VANITY_URL");
    vanityCode = code;
    set |= 0x1000;
    return this;
  }
  

  @Nonnull
  public GuildManager setDescription(@Nullable String description)
  {
    checkFeature("VERIFIED");
    this.description = description;
    set |= 0x2000;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject body = DataObject.empty().put("name", getGuild().getName());
    if (shouldUpdate(1L))
      body.put("name", name);
    if (shouldUpdate(2L))
      body.put("region", region);
    if (shouldUpdate(32L))
      body.put("afk_timeout", Integer.valueOf(afkTimeout));
    if (shouldUpdate(4L))
      body.put("icon", icon == null ? null : icon.getEncoding());
    if (shouldUpdate(8L))
      body.put("splash", splash == null ? null : splash.getEncoding());
    if (shouldUpdate(16L))
      body.put("afk_channel_id", afkChannel);
    if (shouldUpdate(64L))
      body.put("system_channel_id", systemChannel);
    if (shouldUpdate(16384L))
      body.put("rules_channel_id", rulesChannel);
    if (shouldUpdate(32768L))
      body.put("public_updates_channel_id", communityUpdatesChannel);
    if (shouldUpdate(1024L))
      body.put("verification_level", Integer.valueOf(verificationLevel));
    if (shouldUpdate(256L))
      body.put("default_message_notifications", Integer.valueOf(notificationLevel));
    if (shouldUpdate(128L))
      body.put("mfa_level", Integer.valueOf(mfaLevel));
    if (shouldUpdate(512L))
      body.put("explicit_content_filter", Integer.valueOf(explicitContentLevel));
    if (shouldUpdate(2048L))
      body.put("banner", banner == null ? null : banner.getEncoding());
    if (shouldUpdate(4096L))
      body.put("vanity_code", vanityCode);
    if (shouldUpdate(8192L)) {
      body.put("description", description);
    }
    reset();
    return getRequestBody(body);
  }
  

  protected boolean checkPermissions()
  {
    if (!getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER }))
      throw new InsufficientPermissionException(getGuild(), Permission.MANAGE_SERVER);
    return super.checkPermissions();
  }
  
  private void checkFeature(String feature)
  {
    if (!getGuild().getFeatures().contains(feature)) {
      throw new IllegalStateException("This guild doesn't have the " + feature + " feature enabled");
    }
  }
}
