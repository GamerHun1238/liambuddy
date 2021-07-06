package net.dv8tion.jda.api.entities.templates;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.ISnowflake;






















public class TemplateGuild
  implements ISnowflake
{
  private final long id;
  private final String name;
  private final String description;
  private final String region;
  private final String iconId;
  private final Guild.VerificationLevel verificationLevel;
  private final Guild.NotificationLevel notificationLevel;
  private final Guild.ExplicitContentLevel explicitContentLevel;
  private final Locale locale;
  private final Guild.Timeout afkTimeout;
  private final TemplateChannel afkChannel;
  private final TemplateChannel systemChannel;
  private final List<TemplateRole> roles;
  private final List<TemplateChannel> channels;
  
  public TemplateGuild(long id, String name, String description, String region, String iconId, Guild.VerificationLevel verificationLevel, Guild.NotificationLevel notificationLevel, Guild.ExplicitContentLevel explicitContentLevel, Locale locale, Guild.Timeout afkTimeout, TemplateChannel afkChannel, TemplateChannel systemChannel, List<TemplateRole> roles, List<TemplateChannel> channels)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.region = region;
    this.iconId = iconId;
    this.verificationLevel = verificationLevel;
    this.notificationLevel = notificationLevel;
    this.explicitContentLevel = explicitContentLevel;
    this.locale = locale;
    this.afkTimeout = afkTimeout;
    this.afkChannel = afkChannel;
    this.systemChannel = systemChannel;
    this.roles = Collections.unmodifiableList(roles);
    this.channels = Collections.unmodifiableList(channels);
  }
  

  public long getIdLong()
  {
    return id;
  }
  





  @Nonnull
  public String getName()
  {
    return name;
  }
  






  @Nullable
  public String getDescription()
  {
    return description;
  }
  







  @Nonnull
  public Region getRegion()
  {
    return Region.fromKey(region);
  }
  






  @Nonnull
  public String getRegionRaw()
  {
    return region;
  }
  







  @Nullable
  public String getIconId()
  {
    return iconId;
  }
  







  @Nullable
  public String getIconUrl()
  {
    return iconId == null ? null : 
      String.format("https://cdn.discordapp.com/icons/%s/%s.%s", new Object[] { Long.valueOf(id), iconId, iconId.startsWith("a_") ? "gif" : "png" });
  }
  





  @Nonnull
  public Guild.VerificationLevel getVerificationLevel()
  {
    return verificationLevel;
  }
  





  @Nonnull
  public Guild.NotificationLevel getDefaultNotificationLevel()
  {
    return notificationLevel;
  }
  





  @Nonnull
  public Guild.ExplicitContentLevel getExplicitContentLevel()
  {
    return explicitContentLevel;
  }
  





  @Nonnull
  public Locale getLocale()
  {
    return locale;
  }
  





  @Nonnull
  public Guild.Timeout getAfkTimeout()
  {
    return afkTimeout;
  }
  








  @Nullable
  public TemplateChannel getAfkChannel()
  {
    return afkChannel;
  }
  







  @Nullable
  public TemplateChannel getSystemChannel()
  {
    return systemChannel;
  }
  





  @Nonnull
  public List<TemplateRole> getRoles()
  {
    return roles;
  }
  





  @Nonnull
  public List<TemplateChannel> getChannels()
  {
    return channels;
  }
}
