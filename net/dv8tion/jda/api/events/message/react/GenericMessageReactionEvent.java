package net.dv8tion.jda.api.events.message.react;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
























public class GenericMessageReactionEvent
  extends GenericMessageEvent
{
  protected final long userId;
  protected User issuer;
  protected Member member;
  protected MessageReaction reaction;
  
  public GenericMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nullable User user, @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
  {
    super(api, responseNumber, reaction.getMessageIdLong(), reaction.getChannel());
    this.userId = userId;
    issuer = user;
    this.member = member;
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
    return (issuer == null) && (isFromType(ChannelType.PRIVATE)) ? 
      getPrivateChannel().getUser() : 
      issuer;
  }
  














  @Nullable
  public Member getMember()
  {
    return member;
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
    User user = getUser();
    if (user != null)
      return new CompletedRestAction(getJDA(), user);
    return getJDA().retrieveUserById(getUserIdLong());
  }
  















  @Nonnull
  @CheckReturnValue
  public RestAction<Member> retrieveMember()
  {
    if (member != null)
      return new CompletedRestAction(getJDA(), member);
    return getGuild().retrieveMemberById(getUserIdLong());
  }
  











  @Nonnull
  @CheckReturnValue
  public RestAction<Message> retrieveMessage()
  {
    return getChannel().retrieveMessageById(getMessageId());
  }
}
