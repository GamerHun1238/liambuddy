package net.dv8tion.jda.api.events.message.priv.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.GenericPrivateMessageEvent;
























public class GenericPrivateMessageReactionEvent
  extends GenericPrivateMessageEvent
{
  protected final long userId;
  protected final MessageReaction reaction;
  
  public GenericPrivateMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nonnull MessageReaction reaction, long userId)
  {
    super(api, responseNumber, reaction.getMessageIdLong(), (PrivateChannel)reaction.getChannel());
    this.userId = userId;
    this.reaction = reaction;
  }
  





  @Nonnull
  public String getUserId()
  {
    return Long.toUnsignedString(userId);
  }
  





  public long getUserIdLong()
  {
    return userId;
  }
  






  @Nullable
  public User getUser()
  {
    return userId == getJDA().getSelfUser().getIdLong() ? 
      getJDA().getSelfUser() : 
      getChannel().getUser();
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
