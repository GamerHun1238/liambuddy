package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public abstract interface VoiceChannel
  extends GuildChannel
{
  public abstract int getUserLimit();
  
  public abstract int getBitrate();
  
  @Nonnull
  public abstract Region getRegion();
  
  @Nullable
  public abstract String getRegionRaw();
  
  @Nonnull
  public abstract ChannelAction<VoiceChannel> createCopy(@Nonnull Guild paramGuild);
  
  @Nonnull
  public abstract ChannelAction<VoiceChannel> createCopy();
}
