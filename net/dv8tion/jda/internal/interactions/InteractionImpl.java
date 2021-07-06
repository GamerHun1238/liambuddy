package net.dv8tion.jda.internal.interactions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl;












public class InteractionImpl
  implements Interaction
{
  protected final InteractionHookImpl hook;
  protected final long id;
  protected final int type;
  protected final String token;
  protected final Guild guild;
  protected final Member member;
  protected final User user;
  protected final AbstractChannel channel;
  protected final JDAImpl api;
  
  public InteractionImpl(JDAImpl jda, DataObject data)
  {
    api = jda;
    id = data.getUnsignedLong("id");
    token = data.getString("token");
    type = data.getInt("type");
    guild = jda.getGuildById(data.getUnsignedLong("guild_id", 0L));
    hook = new InteractionHookImpl(this, jda);
    if (guild != null)
    {
      member = jda.getEntityBuilder().createMember((GuildImpl)guild, data.getObject("member"));
      jda.getEntityBuilder().updateMemberCache((MemberImpl)member);
      user = member.getUser();
      this.channel = guild.getGuildChannelById(data.getUnsignedLong("channel_id"));
    }
    else
    {
      member = null;
      long channelId = data.getUnsignedLong("channel_id");
      PrivateChannel channel = jda.getPrivateChannelById(channelId);
      if (channel == null)
      {
        channel = jda.getEntityBuilder().createPrivateChannel(
          DataObject.empty()
          .put("id", Long.valueOf(channelId))
          .put("recipient", data.getObject("user")));
      }
      
      this.channel = channel;
      user = channel.getUser();
    }
  }
  
  public InteractionImpl(long id, int type, String token, Guild guild, Member member, User user, AbstractChannel channel)
  {
    this.id = id;
    this.type = type;
    this.token = token;
    this.guild = guild;
    this.member = member;
    this.user = user;
    this.channel = channel;
    api = ((JDAImpl)user.getJDA());
    hook = new InteractionHookImpl(this, api);
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  public int getTypeRaw()
  {
    return type;
  }
  

  @Nonnull
  public String getToken()
  {
    return token;
  }
  

  @Nullable
  public Guild getGuild()
  {
    return guild;
  }
  

  @Nullable
  public AbstractChannel getChannel()
  {
    return channel;
  }
  

  @Nonnull
  public InteractionHook getHook()
  {
    return hook;
  }
  

  @Nonnull
  public User getUser()
  {
    return user;
  }
  

  @Nullable
  public Member getMember()
  {
    return member;
  }
  

  public boolean isAcknowledged()
  {
    return hook.isAck();
  }
  

  @Nonnull
  public ReplyActionImpl deferReply()
  {
    return new ReplyActionImpl(hook);
  }
}
