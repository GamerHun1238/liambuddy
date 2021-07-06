package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
























public class GuildUpdateVanityCodeEvent
  extends GenericGuildUpdateEvent<String>
{
  public static final String IDENTIFIER = "vanity_code";
  
  public GuildUpdateVanityCodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
  {
    super(api, responseNumber, guild, previous, guild.getVanityCode(), "vanity_code");
  }
  





  @Nullable
  public String getOldVanityCode()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldVanityUrl()
  {
    return "https://discord.gg/" + getOldVanityCode();
  }
  





  @Nullable
  public String getNewVanityCode()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewVanityUrl()
  {
    return "https://discord.gg/" + getNewVanityCode();
  }
}
