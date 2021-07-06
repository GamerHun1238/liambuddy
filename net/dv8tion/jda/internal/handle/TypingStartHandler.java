package net.dv8tion.jda.internal.handle;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
















public class TypingStartHandler
  extends SocketHandler
{
  public TypingStartHandler(JDAImpl api)
  {
    super(api);
  }
  

  protected Long handleInternally(DataObject content)
  {
    GuildImpl guild = null;
    if (!content.isNull("guild_id"))
    {
      long guildId = content.getUnsignedLong("guild_id");
      guild = (GuildImpl)getJDA().getGuildById(guildId);
      if (getJDA().getGuildSetupController().isLocked(guildId))
        return Long.valueOf(guildId);
      if (guild == null) {
        return null;
      }
    }
    long channelId = content.getLong("channel_id");
    MessageChannel channel = (MessageChannel)getJDA().getTextChannelsView().get(channelId);
    if (channel == null)
      channel = (MessageChannel)getJDA().getPrivateChannelsView().get(channelId);
    if (channel == null) {
      return null;
    }
    

    long userId = content.getLong("user_id");
    
    MemberImpl member = null;
    User user; User user; if ((channel instanceof PrivateChannel)) {
      user = ((PrivateChannel)channel).getUser();
    } else
      user = (User)getJDA().getUsersView().get(userId);
    if (!content.isNull("member"))
    {

      EntityBuilder entityBuilder = getJDA().getEntityBuilder();
      member = entityBuilder.createMember(guild, content.getObject("member"));
      entityBuilder.updateMemberCache(member);
      user = member.getUser();
    }
    
    if (user == null) {
      return null;
    }
    
    OffsetDateTime timestamp = Instant.ofEpochSecond(content.getInt("timestamp")).atOffset(ZoneOffset.UTC);
    getJDA().handleEvent(new UserTypingEvent(
    
      getJDA(), responseNumber, user, channel, timestamp, member));
    
    return null;
  }
}
