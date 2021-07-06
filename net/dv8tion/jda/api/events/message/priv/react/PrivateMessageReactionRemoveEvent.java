package net.dv8tion.jda.api.events.message.priv.react;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;


























public class PrivateMessageReactionRemoveEvent
  extends GenericPrivateMessageReactionEvent
{
  public PrivateMessageReactionRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull MessageReaction reaction, long userId)
  {
    super(api, responseNumber, reaction, userId);
  }
}
