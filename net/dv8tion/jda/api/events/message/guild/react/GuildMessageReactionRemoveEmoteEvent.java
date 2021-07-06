package net.dv8tion.jda.api.events.message.guild.react;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;



























public class GuildMessageReactionRemoveEmoteEvent
  extends GenericGuildMessageEvent
{
  private final MessageReaction reaction;
  
  public GuildMessageReactionRemoveEmoteEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull MessageReaction reaction, long messageId)
  {
    super(api, responseNumber, messageId, channel);
    
    this.reaction = reaction;
  }
  





  @Nonnull
  public TextChannel getChannel()
  {
    return channel;
  }
  





  @Nonnull
  public MessageReaction getReaction()
  {
    return reaction;
  }
  






  @Nonnull
  public MessageReaction.ReactionEmote getReactionEmote()
  {
    return reaction.getReactionEmote();
  }
  





  public long getMessageIdLong()
  {
    return messageId;
  }
  





  @Nonnull
  public String getMessageId()
  {
    return Long.toUnsignedString(messageId);
  }
}
