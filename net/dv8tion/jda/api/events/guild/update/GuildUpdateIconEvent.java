package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateIconEvent
  extends GenericGuildUpdateEvent<String>
{
  public static final String IDENTIFIER = "icon";
  
  public GuildUpdateIconEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldIconId)
  {
    super(api, responseNumber, guild, oldIconId, guild.getIconId(), "icon");
  }
  





  @Nullable
  public String getOldIconId()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldIconUrl()
  {
    return previous == null ? null : String.format("https://cdn.discordapp.com/icons/%s/%s.%s", new Object[] { guild.getId(), previous, ((String)previous).startsWith("a_") ? "gif" : "png" });
  }
  





  @Nullable
  public String getNewIconId()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewIconUrl()
  {
    return next == null ? null : String.format("https://cdn.discordapp.com/icons/%s/%s.%s", new Object[] { guild.getId(), next, ((String)next).startsWith("a_") ? "gif" : "png" });
  }
}
