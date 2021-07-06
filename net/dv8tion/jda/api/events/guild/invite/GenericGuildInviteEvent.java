package net.dv8tion.jda.api.events.guild.invite;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;






















public class GenericGuildInviteEvent
  extends GenericGuildEvent
{
  private final String code;
  private final GuildChannel channel;
  
  public GenericGuildInviteEvent(@Nonnull JDA api, long responseNumber, @Nonnull String code, @Nonnull GuildChannel channel)
  {
    super(api, responseNumber, channel.getGuild());
    this.code = code;
    this.channel = channel;
  }
  






  @Nonnull
  public String getCode()
  {
    return code;
  }
  






  @Nonnull
  public String getUrl()
  {
    return "https://discord.gg/" + code;
  }
  





  @Nonnull
  public GuildChannel getChannel()
  {
    return channel;
  }
  





  @Nonnull
  public ChannelType getChannelType()
  {
    return channel.getType();
  }
  











  @Nonnull
  public TextChannel getTextChannel()
  {
    if (getChannelType() != ChannelType.TEXT)
      throw new IllegalStateException("The channel is not of type TEXT");
    return (TextChannel)getChannel();
  }
  











  @Nonnull
  public VoiceChannel getVoiceChannel()
  {
    if (!(channel instanceof VoiceChannel))
      throw new IllegalStateException("The channel is not of type VOICE or STAGE");
    return (VoiceChannel)getChannel();
  }
  











  @Nonnull
  public StageChannel getStageChannel()
  {
    if (getChannelType() != ChannelType.STAGE)
      throw new IllegalStateException("The channel is not of type STAGE");
    return (StageChannel)getChannel();
  }
  











  @Nonnull
  public StoreChannel getStoreChannel()
  {
    if (getChannelType() != ChannelType.STORE)
      throw new IllegalStateException("The channel is not of type STORE");
    return (StoreChannel)getChannel();
  }
  











  @Nonnull
  public Category getCategory()
  {
    if (getChannelType() != ChannelType.CATEGORY)
      throw new IllegalStateException("The channel is not of type CATEGORY");
    return (Category)getChannel();
  }
}
