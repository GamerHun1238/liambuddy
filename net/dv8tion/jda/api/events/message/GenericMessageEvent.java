package net.dv8tion.jda.api.events.message;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;


























public abstract class GenericMessageEvent
  extends Event
{
  protected final long messageId;
  protected final MessageChannel channel;
  
  public GenericMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel)
  {
    super(api, responseNumber);
    this.messageId = messageId;
    this.channel = channel;
  }
  





  @Nonnull
  public MessageChannel getChannel()
  {
    return channel;
  }
  





  @Nonnull
  public String getMessageId()
  {
    return Long.toUnsignedString(messageId);
  }
  





  public long getMessageIdLong()
  {
    return messageId;
  }
  








  public boolean isFromType(@Nonnull ChannelType type)
  {
    return channel.getType() == type;
  }
  






  public boolean isFromGuild()
  {
    return getChannelType().isGuild();
  }
  





  @Nonnull
  public ChannelType getChannelType()
  {
    return channel.getType();
  }
  














  @Nonnull
  public Guild getGuild()
  {
    return getTextChannel().getGuild();
  }
  














  @Nonnull
  public TextChannel getTextChannel()
  {
    if (!isFromType(ChannelType.TEXT))
      throw new IllegalStateException("This message event did not happen in a text channel");
    return (TextChannel)channel;
  }
  














  @Nonnull
  public PrivateChannel getPrivateChannel()
  {
    if (!isFromType(ChannelType.PRIVATE))
      throw new IllegalStateException("This message event did not happen in a private channel");
    return (PrivateChannel)channel;
  }
}
