package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateSplashEvent
  extends GenericGuildUpdateEvent<String>
{
  public static final String IDENTIFIER = "splash";
  
  public GuildUpdateSplashEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldSplashId)
  {
    super(api, responseNumber, guild, oldSplashId, guild.getSplashId(), "splash");
  }
  





  @Nullable
  public String getOldSplashId()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldSplashUrl()
  {
    return previous == null ? null : String.format("https://cdn.discordapp.com/splashes/%s/%s.png", new Object[] { guild.getId(), previous });
  }
  





  @Nullable
  public String getNewSplashId()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewSplashUrl()
  {
    return next == null ? null : String.format("https://cdn.discordapp.com/splashes/%s/%s.png", new Object[] { guild.getId(), next });
  }
}
