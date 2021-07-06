package net.dv8tion.jda.internal.entities;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Channels;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.utils.Checks;










public class PrivateChannelImpl
  implements PrivateChannel
{
  private final long id;
  private User user;
  private long lastMessageId;
  
  public PrivateChannelImpl(long id, User user)
  {
    this.id = id;
    this.user = user;
  }
  

  private void updateUser()
  {
    User realUser = getJDA().getUserById(user.getIdLong());
    if (realUser != null) {
      user = realUser;
    }
  }
  
  @Nonnull
  public User getUser()
  {
    updateUser();
    return user;
  }
  

  public long getLatestMessageIdLong()
  {
    long messageId = lastMessageId;
    if (messageId < 0L)
      throw new IllegalStateException("No last message id found.");
    return messageId;
  }
  

  public boolean hasLatestMessage()
  {
    return lastMessageId > 0L;
  }
  

  @Nonnull
  public String getName()
  {
    return getUser().getName();
  }
  

  @Nonnull
  public ChannelType getType()
  {
    return ChannelType.PRIVATE;
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return user.getJDA();
  }
  

  @Nonnull
  public RestAction<Void> close()
  {
    Route.CompiledRoute route = Route.Channels.DELETE_CHANNEL.compile(new String[] { getId() });
    return new RestActionImpl(getJDA(), route);
  }
  

  @Nonnull
  public List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages)
  {
    if ((messages == null) || (messages.isEmpty()))
      return Collections.emptyList();
    for (Message m : messages)
    {
      if (!m.getAuthor().equals(getJDA().getSelfUser()))
      {
        throw new IllegalArgumentException("Cannot delete messages of other users in a private channel"); }
    }
    return super.purgeMessages(messages);
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  @Nonnull
  public MessageAction sendMessage(@Nonnull CharSequence text)
  {
    checkBot();
    return super.sendMessage(text);
  }
  

  @Nonnull
  public MessageAction sendMessage(@Nonnull MessageEmbed embed)
  {
    checkBot();
    return super.sendMessage(embed);
  }
  

  @Nonnull
  public MessageAction sendMessage(@Nonnull Message msg)
  {
    checkBot();
    return super.sendMessage(msg);
  }
  

  @Nonnull
  public MessageAction sendFile(@Nonnull InputStream data, @Nonnull String fileName, @Nonnull AttachmentOption... options)
  {
    checkBot();
    return super.sendFile(data, fileName, options);
  }
  

  @Nonnull
  public MessageAction sendFile(@Nonnull File file, @Nonnull String fileName, @Nonnull AttachmentOption... options)
  {
    checkBot();
    long maxSize = getJDA().getSelfUser().getAllowedFileSize();
    Checks.check((file == null) || (file.length() <= maxSize), "File may not exceed the maximum file length of %d bytes!", 
      Long.valueOf(maxSize));
    return super.sendFile(file, fileName, options);
  }
  

  @Nonnull
  public MessageAction sendFile(@Nonnull byte[] data, @Nonnull String fileName, @Nonnull AttachmentOption... options)
  {
    checkBot();
    long maxSize = getJDA().getSelfUser().getAllowedFileSize();
    Checks.check((data == null) || (data.length <= maxSize), "File is too big! Max file-size is %d bytes", Long.valueOf(maxSize));
    return super.sendFile(data, fileName, options);
  }
  
  public PrivateChannelImpl setLastMessageId(long id)
  {
    lastMessageId = id;
    return this;
  }
  




  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof PrivateChannelImpl))
      return false;
    PrivateChannelImpl impl = (PrivateChannelImpl)obj;
    return id == id;
  }
  

  public String toString()
  {
    return "PC:" + getUser().getName() + '(' + getId() + ')';
  }
  
  private void checkBot()
  {
    if ((getUser().isBot()) && (getJDA().getAccountType() == AccountType.BOT)) {
      throw new UnsupportedOperationException("Cannot send a private message between bots.");
    }
  }
}
