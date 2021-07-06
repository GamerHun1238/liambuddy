package net.dv8tion.jda.api.events.message.guild.react;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;




















public abstract class GenericGuildMessageReactionEvent
  extends GenericGuildMessageEvent
{
  protected final long userId;
  protected final Member issuer;
  protected final MessageReaction reaction;
  
  public GenericGuildMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nullable Member user, @Nonnull MessageReaction reaction, long userId)
  {
    super(api, responseNumber, reaction.getMessageIdLong(), (TextChannel)reaction.getChannel());
    issuer = user;
    this.reaction = reaction;
    this.userId = userId;
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
    return issuer == null ? getJDA().getUserById(userId) : issuer.getUser();
  }
  







  @Nullable
  public Member getMember()
  {
    return issuer;
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
  








  @Nonnull
  @CheckReturnValue
  public RestAction<User> retrieveUser()
  {
    if (issuer != null)
      return new CompletedRestAction(getJDA(), issuer.getUser());
    return getJDA().retrieveUserById(getUserIdLong());
  }
  












  @Nonnull
  @CheckReturnValue
  public RestAction<Member> retrieveMember()
  {
    if (issuer != null)
      return new CompletedRestAction(getJDA(), issuer);
    return getGuild().retrieveMemberById(getUserIdLong());
  }
  











  @Nonnull
  @CheckReturnValue
  public RestAction<Message> retrieveMessage()
  {
    return getChannel().retrieveMessageById(getMessageId());
  }
}
