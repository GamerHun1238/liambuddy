package net.dv8tion.jda.api.entities;

import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Messages;
import net.dv8tion.jda.internal.requests.restaction.pagination.ReactionPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;









































public class MessageReaction
{
  private final MessageChannel channel;
  private final ReactionEmote emote;
  private final long messageId;
  private final boolean self;
  private final int count;
  
  public MessageReaction(@Nonnull MessageChannel channel, @Nonnull ReactionEmote emote, long messageId, boolean self, int count)
  {
    this.channel = channel;
    this.emote = emote;
    this.messageId = messageId;
    this.self = self;
    this.count = count;
  }
  





  @Nonnull
  public JDA getJDA()
  {
    return channel.getJDA();
  }
  








  public boolean isSelf()
  {
    return self;
  }
  









  public boolean hasCount()
  {
    return count >= 0;
  }
  













  public int getCount()
  {
    if (!hasCount())
      throw new IllegalStateException("Cannot retrieve count for this MessageReaction!");
    return count;
  }
  






  @Nonnull
  public ChannelType getChannelType()
  {
    return channel.getType();
  }
  









  public boolean isFromType(@Nonnull ChannelType type)
  {
    return getChannelType() == type;
  }
  







  @Nullable
  public Guild getGuild()
  {
    TextChannel channel = getTextChannel();
    return channel != null ? channel.getGuild() : null;
  }
  






  @Nullable
  public TextChannel getTextChannel()
  {
    return (getChannel() instanceof TextChannel) ? (TextChannel)getChannel() : null;
  }
  






  @Nullable
  public PrivateChannel getPrivateChannel()
  {
    return (getChannel() instanceof PrivateChannel) ? (PrivateChannel)getChannel() : null;
  }
  






  @Nonnull
  public MessageChannel getChannel()
  {
    return channel;
  }
  






  @Nonnull
  public ReactionEmote getReactionEmote()
  {
    return emote;
  }
  





  @Nonnull
  public String getMessageId()
  {
    return Long.toUnsignedString(messageId);
  }
  





  public long getMessageIdLong()
  {
    return messageId;
  }
  


















  @Nonnull
  @CheckReturnValue
  public ReactionPaginationAction retrieveUsers()
  {
    return new ReactionPaginationActionImpl(this);
  }
  




















  @Nonnull
  @CheckReturnValue
  public RestAction<Void> removeReaction()
  {
    return removeReaction(getJDA().getSelfUser());
  }
  


































  @Nonnull
  @CheckReturnValue
  public RestAction<Void> removeReaction(@Nonnull User user)
  {
    Checks.notNull(user, "User");
    boolean self = user.equals(getJDA().getSelfUser());
    if (!self)
    {
      if (this.channel.getType() == ChannelType.TEXT)
      {
        GuildChannel channel = (GuildChannel)this.channel;
        if (!channel.getGuild().getSelfMember().hasPermission(channel, new Permission[] { Permission.MESSAGE_MANAGE })) {
          throw new InsufficientPermissionException(channel, Permission.MESSAGE_MANAGE);
        }
      }
      else {
        throw new PermissionException("Unable to remove Reaction of other user in non-text channel!");
      }
    }
    
    String code = getReactionCode();
    String target = self ? "@me" : user.getId();
    Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(new String[] { this.channel.getId(), getMessageId(), code, target });
    return new RestActionImpl(getJDA(), route);
  }
  




























  @Nonnull
  @CheckReturnValue
  public RestAction<Void> clearReactions()
  {
    if (!getChannelType().isGuild())
      throw new UnsupportedOperationException("Cannot clear reactions on a message sent from a private channel");
    TextChannel guildChannel = (TextChannel)Objects.requireNonNull(getTextChannel());
    if (!guildChannel.getGuild().getSelfMember().hasPermission(guildChannel, new Permission[] { Permission.MESSAGE_MANAGE })) {
      throw new InsufficientPermissionException(guildChannel, Permission.MESSAGE_MANAGE);
    }
    String code = getReactionCode();
    Route.CompiledRoute route = Route.Messages.CLEAR_EMOTE_REACTIONS.compile(new String[] { channel.getId(), getMessageId(), code });
    return new RestActionImpl(getJDA(), route);
  }
  
  private String getReactionCode()
  {
    return emote.isEmote() ? 
      emote.getName() + ":" + emote.getId() : 
      EncodingUtil.encodeUTF8(emote.getName());
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof MessageReaction))
      return false;
    MessageReaction r = (MessageReaction)obj;
    return (emote.equals(emote)) && (self == self) && (messageId == messageId);
  }
  



  public String toString()
  {
    return "MR:(M:(" + messageId + ") / " + emote + ")";
  }
  

  public static class ReactionEmote
    implements ISnowflake
  {
    private final JDA api;
    
    private final String name;
    
    private final long id;
    private final Emote emote;
    
    private ReactionEmote(@Nonnull String name, @Nonnull JDA api)
    {
      this.name = name;
      this.api = api;
      id = 0L;
      emote = null;
    }
    
    private ReactionEmote(@Nonnull Emote emote)
    {
      api = emote.getJDA();
      name = emote.getName();
      id = emote.getIdLong();
      this.emote = emote;
    }
    
    @Nonnull
    public static ReactionEmote fromUnicode(@Nonnull String name, @Nonnull JDA api)
    {
      return new ReactionEmote(name, api);
    }
    
    @Nonnull
    public static ReactionEmote fromCustom(@Nonnull Emote emote)
    {
      return new ReactionEmote(emote);
    }
    








    public boolean isEmote()
    {
      return emote != null;
    }
    








    public boolean isEmoji()
    {
      return emote == null;
    }
    











    @Nonnull
    public String getName()
    {
      return name;
    }
    








    @Nonnull
    public String getAsCodepoints()
    {
      if (!isEmoji())
        throw new IllegalStateException("Cannot get codepoint for custom emote reaction");
      return EncodingUtil.encodeCodepoints(name);
    }
    

    public long getIdLong()
    {
      if (!isEmote())
        throw new IllegalStateException("Cannot get id for emoji reaction");
      return id;
    }
    







    @Nonnull
    public String getAsReactionCode()
    {
      return emote != null ? 
        name + ":" + id : 
        name;
    }
    








    @Nonnull
    public String getEmoji()
    {
      if (!isEmoji())
        throw new IllegalStateException("Cannot get emoji code for custom emote reaction");
      return getName();
    }
    









    @Nonnull
    public Emote getEmote()
    {
      if (!isEmote())
        throw new IllegalStateException("Cannot get custom emote for emoji reaction");
      return emote;
    }
    





    @Nonnull
    public JDA getJDA()
    {
      return api;
    }
    

    public boolean equals(Object obj)
    {
      return ((obj instanceof ReactionEmote)) && 
        (Objects.equals(Long.valueOf(id), Long.valueOf(id))) && 
        (((ReactionEmote)obj).getName().equals(name));
    }
    

    public String toString()
    {
      if (isEmoji())
        return "RE:" + getAsCodepoints();
      return "RE:" + getName() + "(" + getId() + ")";
    }
  }
}
