package net.dv8tion.jda.api.events.message.priv;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.Event;


























public abstract class GenericPrivateMessageEvent
  extends Event
{
  protected final long messageId;
  protected final PrivateChannel channel;
  
  public GenericPrivateMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull PrivateChannel channel)
  {
    super(api, responseNumber);
    this.messageId = messageId;
    this.channel = channel;
  }
  





  @Nonnull
  public PrivateChannel getChannel()
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
}
