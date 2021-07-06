package net.dv8tion.jda.api.events.channel.voice.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;























public class VoiceChannelUpdateRegionEvent
  extends GenericVoiceChannelUpdateEvent<String>
{
  public static final String IDENTIFIER = "region";
  
  public VoiceChannelUpdateRegionEvent(@NotNull JDA api, long responseNumber, @NotNull VoiceChannel channel, @Nullable String oldRegion)
  {
    super(api, responseNumber, channel, oldRegion, channel.getRegionRaw(), "region");
  }
  





  @Nonnull
  public Region getOldRegion()
  {
    return getOldValue() == null ? Region.AUTOMATIC : Region.fromKey((String)getOldValue());
  }
  





  @Nonnull
  public Region getNewRegion()
  {
    return getNewValue() == null ? Region.AUTOMATIC : Region.fromKey((String)getNewValue());
  }
  





  @Nullable
  public String getOldRegionRaw()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getNewRegionRaw()
  {
    return (String)getNewValue();
  }
}
