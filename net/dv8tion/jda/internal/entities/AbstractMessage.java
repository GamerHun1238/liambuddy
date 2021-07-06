package net.dv8tion.jda.internal.entities;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.MessageSticker;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.apache.commons.collections4.Bag;

public abstract class AbstractMessage implements Message
{
  protected static final String UNSUPPORTED = "This operation is not supported for Messages of this type!";
  protected final String content;
  protected final String nonce;
  protected final boolean isTTS;
  
  public AbstractMessage(String content, String nonce, boolean isTTS)
  {
    this.content = content;
    this.nonce = nonce;
    this.isTTS = isTTS;
  }
  

  @Nonnull
  public String getContentRaw()
  {
    return content;
  }
  

  public String getNonce()
  {
    return nonce;
  }
  

  public boolean isTTS()
  {
    return isTTS;
  }
  

  protected abstract void unsupported();
  
  public void formatTo(Formatter formatter, int flags, int width, int precision)
  {
    boolean upper = (flags & 0x2) == 2;
    boolean leftJustified = (flags & 0x1) == 1;
    
    String out = content;
    
    if (upper) {
      out = out.toUpperCase(formatter.locale());
    }
    appendFormat(formatter, width, precision, leftJustified, out);
  }
  
  protected void appendFormat(Formatter formatter, int width, int precision, boolean leftJustified, String out)
  {
    try
    {
      Appendable appendable = formatter.out();
      if ((precision > -1) && (out.length() > precision))
      {
        appendable.append(Helpers.truncate(out, precision - 3)).append("...");
        return;
      }
      
      if (leftJustified) {
        appendable.append(Helpers.rightPad(out, width));
      } else {
        appendable.append(Helpers.leftPad(out, width));
      }
    }
    catch (IOException e) {
      throw new java.io.UncheckedIOException(e);
    }
  }
  

  public Message getReferencedMessage()
  {
    return null;
  }
  

  @Nonnull
  public Bag<User> getMentionedUsersBag()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public Bag<TextChannel> getMentionedChannelsBag()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public Bag<Role> getMentionedRolesBag()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<User> getMentionedUsers()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<TextChannel> getMentionedChannels()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Role> getMentionedRoles()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Member> getMentionedMembers(@Nonnull Guild guild)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Member> getMentionedMembers()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<IMentionable> getMentions(@Nonnull Message.MentionType... types)
  {
    unsupported();
    return null;
  }
  

  public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull Message.MentionType... types)
  {
    unsupported();
    return false;
  }
  

  public boolean mentionsEveryone()
  {
    unsupported();
    return false;
  }
  

  public boolean isEdited()
  {
    unsupported();
    return false;
  }
  

  public OffsetDateTime getTimeEdited()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public User getAuthor()
  {
    unsupported();
    return null;
  }
  

  public Member getMember()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getJumpUrl()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getContentDisplay()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getContentStripped()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<String> getInvites()
  {
    unsupported();
    return null;
  }
  

  public boolean isFromType(@Nonnull ChannelType type)
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public ChannelType getChannelType()
  {
    unsupported();
    return null;
  }
  

  public boolean isWebhookMessage()
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public MessageChannel getChannel()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public PrivateChannel getPrivateChannel()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public TextChannel getTextChannel()
  {
    unsupported();
    return null;
  }
  

  public Category getCategory()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public Guild getGuild()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Message.Attachment> getAttachments()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<MessageEmbed> getEmbeds()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<ActionRow> getActionRows()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Emote> getEmotes()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public Bag<Emote> getEmotesBag()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<net.dv8tion.jda.api.entities.MessageReaction> getReactions()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<MessageSticker> getStickers()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public MessageAction editMessage(@Nonnull CharSequence newContent)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public MessageAction editMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> newContent)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public MessageAction editMessageFormat(@Nonnull String format, @Nonnull Object... args)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public MessageAction editMessage(@Nonnull Message newContent)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public AuditableRestAction<Void> delete()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public net.dv8tion.jda.api.JDA getJDA()
  {
    unsupported();
    return null;
  }
  

  public boolean isPinned()
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public RestAction<Void> pin()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> unpin()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> addReaction(@Nonnull Emote emote)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> addReaction(@Nonnull String unicode)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> clearReactions()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> clearReactions(@Nonnull String unicode)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> clearReactions(@Nonnull Emote emote)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> removeReaction(@Nonnull Emote emote)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> removeReaction(@Nonnull Emote emote, @Nonnull User user)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> removeReaction(@Nonnull String unicode)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Void> removeReaction(@Nonnull String unicode, @Nonnull User user)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public ReactionPaginationAction retrieveReactionUsers(@Nonnull Emote emote)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public ReactionPaginationAction retrieveReactionUsers(@Nonnull String unicode)
  {
    unsupported();
    return null;
  }
  

  public MessageReaction.ReactionEmote getReactionByUnicode(@Nonnull String unicode)
  {
    unsupported();
    return null;
  }
  

  public MessageReaction.ReactionEmote getReactionById(@Nonnull String id)
  {
    unsupported();
    return null;
  }
  

  public MessageReaction.ReactionEmote getReactionById(long id)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public AuditableRestAction<Void> suppressEmbeds(boolean suppressed)
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public RestAction<Message> crosspost()
  {
    unsupported();
    return null;
  }
  

  public boolean isSuppressedEmbeds()
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public EnumSet<net.dv8tion.jda.api.entities.Message.MessageFlag> getFlags()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public MessageType getType()
  {
    unsupported();
    return null;
  }
}
