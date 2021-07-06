package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;



























@Deprecated
@ReplaceWith("VoiceChannelUpdateRegionEvent")
@DeprecatedSince("4.3.0")
public class GuildUpdateRegionEvent
  extends GenericGuildUpdateEvent<Region>
{
  public static final String IDENTIFIER = "region";
  private final String oldRegion;
  private final String newRegion;
  
  public GuildUpdateRegionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull String oldRegion)
  {
    super(api, responseNumber, guild, Region.fromKey(oldRegion), guild.getRegion(), "region");
    this.oldRegion = oldRegion;
    newRegion = guild.getRegionRaw();
  }
  








  @Nonnull
  public Region getOldRegion()
  {
    return getOldValue();
  }
  







  @Nonnull
  public String getOldRegionRaw()
  {
    return oldRegion;
  }
  








  @Nonnull
  public Region getNewRegion()
  {
    return getNewValue();
  }
  







  @Nonnull
  public String getNewRegionRaw()
  {
    return newRegion;
  }
  

  @Nonnull
  public Region getOldValue()
  {
    return (Region)super.getOldValue();
  }
  

  @Nonnull
  public Region getNewValue()
  {
    return (Region)super.getNewValue();
  }
}
