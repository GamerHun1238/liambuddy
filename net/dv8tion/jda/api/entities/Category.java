package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;

public abstract interface Category
  extends GuildChannel
{
  @Nonnull
  public abstract List<GuildChannel> getChannels();
  
  @Nonnull
  public abstract List<StoreChannel> getStoreChannels();
  
  @Nonnull
  public abstract List<TextChannel> getTextChannels();
  
  @Nonnull
  public abstract List<VoiceChannel> getVoiceChannels();
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<TextChannel> createTextChannel(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<StageChannel> createStageChannel(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract CategoryOrderAction modifyTextChannelPositions();
  
  @Nonnull
  @CheckReturnValue
  public abstract CategoryOrderAction modifyVoiceChannelPositions();
  
  @Nonnull
  public abstract ChannelAction<Category> createCopy(@Nonnull Guild paramGuild);
  
  @Nonnull
  public abstract ChannelAction<Category> createCopy();
}
