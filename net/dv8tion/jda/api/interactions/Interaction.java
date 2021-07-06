package net.dv8tion.jda.api.interactions;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl;
import net.dv8tion.jda.internal.utils.Checks;































public abstract interface Interaction
  extends ISnowflake
{
  public abstract int getTypeRaw();
  
  @Nonnull
  public InteractionType getType()
  {
    return InteractionType.fromKey(getTypeRaw());
  }
  






  @Nonnull
  public abstract String getToken();
  






  @Nullable
  public abstract Guild getGuild();
  





  public boolean isFromGuild()
  {
    return getGuild() != null;
  }
  






  @Nonnull
  public ChannelType getChannelType()
  {
    AbstractChannel channel = getChannel();
    return channel != null ? channel.getType() : ChannelType.UNKNOWN;
  }
  











  @Nonnull
  public abstract User getUser();
  










  @Nullable
  public abstract Member getMember();
  










  @Nullable
  public abstract AbstractChannel getChannel();
  










  @Nonnull
  public abstract InteractionHook getHook();
  










  public abstract boolean isAcknowledged();
  










  @Nonnull
  @CheckReturnValue
  public abstract ReplyAction deferReply();
  










  @Nonnull
  @CheckReturnValue
  public ReplyAction deferReply(boolean ephemeral)
  {
    return deferReply().setEphemeral(ephemeral);
  }
  


















  @Nonnull
  @CheckReturnValue
  public ReplyAction reply(@Nonnull Message message)
  {
    Checks.notNull(message, "Message");
    ReplyActionImpl action = (ReplyActionImpl)deferReply();
    return action.applyMessage(message);
  }
  


















  @Nonnull
  @CheckReturnValue
  public ReplyAction reply(@Nonnull String content)
  {
    Checks.notNull(content, "Content");
    return deferReply().setContent(content);
  }
  


















  @Nonnull
  @CheckReturnValue
  public ReplyAction replyEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds)
  {
    return deferReply().addEmbeds(embeds);
  }
  




















  @Nonnull
  @CheckReturnValue
  public ReplyAction replyEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... embeds)
  {
    Checks.notNull(embed, "MessageEmbed");
    Checks.noneNull(embeds, "MessageEmbed");
    return deferReply().addEmbeds(new MessageEmbed[] { embed }).addEmbeds(embeds);
  }
  
















































































  @Nonnull
  @CheckReturnValue
  public ReplyAction replyFormat(@Nonnull String format, @Nonnull Object... args)
  {
    Checks.notNull(format, "Format String");
    return reply(String.format(format, args));
  }
  









  @Nonnull
  public GuildChannel getGuildChannel()
  {
    AbstractChannel channel = getChannel();
    if ((channel instanceof GuildChannel))
      return (GuildChannel)channel;
    throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to GuildChannel");
  }
  









  @Nonnull
  public MessageChannel getMessageChannel()
  {
    AbstractChannel channel = getChannel();
    if ((channel instanceof MessageChannel))
      return (MessageChannel)channel;
    throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to MessageChannel");
  }
  









  @Nonnull
  public TextChannel getTextChannel()
  {
    AbstractChannel channel = getChannel();
    if ((channel instanceof TextChannel))
      return (TextChannel)channel;
    throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to TextChannel");
  }
  









  @Nonnull
  public VoiceChannel getVoiceChannel()
  {
    AbstractChannel channel = getChannel();
    if ((channel instanceof VoiceChannel))
      return (VoiceChannel)channel;
    throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to VoiceChannel");
  }
  









  @Nonnull
  public PrivateChannel getPrivateChannel()
  {
    AbstractChannel channel = getChannel();
    if ((channel instanceof PrivateChannel))
      return (PrivateChannel)channel;
    throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to PrivateChannel");
  }
}
