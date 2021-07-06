package net.dv8tion.jda.api.events.user;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;






















public class UserTypingEvent
  extends GenericUserEvent
{
  private final Member member;
  private final MessageChannel channel;
  private final OffsetDateTime timestamp;
  
  public UserTypingEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageChannel channel, @Nonnull OffsetDateTime timestamp, @Nullable Member member)
  {
    super(api, responseNumber, user);
    this.member = member;
    this.channel = channel;
    this.timestamp = timestamp;
  }
  





  @Nonnull
  public OffsetDateTime getTimestamp()
  {
    return timestamp;
  }
  





  @Nonnull
  public MessageChannel getChannel()
  {
    return channel;
  }
  








  public boolean isFromType(@Nonnull ChannelType type)
  {
    return channel.getType() == type;
  }
  





  @Nonnull
  public ChannelType getType()
  {
    return channel.getType();
  }
  






  @Nullable
  public PrivateChannel getPrivateChannel()
  {
    return isFromType(ChannelType.PRIVATE) ? (PrivateChannel)channel : null;
  }
  






  @Nullable
  public TextChannel getTextChannel()
  {
    return isFromType(ChannelType.TEXT) ? (TextChannel)channel : null;
  }
  






  @Nullable
  public Guild getGuild()
  {
    return isFromType(ChannelType.TEXT) ? member.getGuild() : null;
  }
  





  @Nullable
  public Member getMember()
  {
    return member;
  }
}
