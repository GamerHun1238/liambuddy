package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateBannerEvent
  extends GenericGuildUpdateEvent<String>
{
  public static final String IDENTIFIER = "banner";
  
  public GuildUpdateBannerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
  {
    super(api, responseNumber, guild, previous, guild.getBannerId(), "banner");
  }
  





  @Nullable
  public String getNewBannerId()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewBannerUrl()
  {
    return next == null ? null : String.format("https://cdn.discordapp.com/banners/%s/%s.png", new Object[] { guild.getId(), next });
  }
  







  @Nullable
  @Deprecated
  @DeprecatedSince("4.2.0")
  @ReplaceWith("getNewBannerUrl()")
  public String getNewBannerIdUrl()
  {
    return getNewBannerUrl();
  }
  





  @Nullable
  public String getOldBannerId()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldBannerUrl()
  {
    return previous == null ? null : String.format("https://cdn.discordapp.com/banners/%s/%s.png", new Object[] { guild.getId(), previous });
  }
}
