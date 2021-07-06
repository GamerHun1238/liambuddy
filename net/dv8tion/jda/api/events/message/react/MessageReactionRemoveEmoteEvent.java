package net.dv8tion.jda.api.events.message.react;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;































public class MessageReactionRemoveEmoteEvent
  extends GenericMessageEvent
{
  private final MessageReaction reaction;
  
  public MessageReactionRemoveEmoteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel, @Nonnull MessageReaction reaction)
  {
    super(api, responseNumber, messageId, channel);
    this.reaction = reaction;
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
}
