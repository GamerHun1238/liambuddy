package net.dv8tion.jda.api.events.message.guild;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;


























public abstract class GenericGuildMessageEvent
  extends GenericGuildEvent
{
  protected final long messageId;
  protected final TextChannel channel;
  
  public GenericGuildMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel)
  {
    super(api, responseNumber, channel.getGuild());
    this.messageId = messageId;
    this.channel = channel;
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
  





  @Nonnull
  public TextChannel getChannel()
  {
    return channel;
  }
}
