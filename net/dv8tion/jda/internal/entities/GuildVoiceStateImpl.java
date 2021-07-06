package net.dv8tion.jda.internal.entities;

import java.time.Instant;
import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import net.dv8tion.jda.internal.utils.Helpers;











public class GuildVoiceStateImpl
  implements GuildVoiceState
{
  private final JDA api;
  private Guild guild;
  private Member member;
  private VoiceChannel connectedChannel;
  private String sessionId;
  private long requestToSpeak;
  private boolean selfMuted = false;
  private boolean selfDeafened = false;
  private boolean guildMuted = false;
  private boolean guildDeafened = false;
  private boolean suppressed = false;
  private boolean stream = false;
  
  public GuildVoiceStateImpl(Member member)
  {
    api = member.getJDA();
    guild = member.getGuild();
    this.member = member;
  }
  

  public boolean isSelfMuted()
  {
    return selfMuted;
  }
  

  public boolean isSelfDeafened()
  {
    return selfDeafened;
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  public String getSessionId()
  {
    return sessionId;
  }
  
  public long getRequestToSpeak()
  {
    return requestToSpeak;
  }
  

  public OffsetDateTime getRequestToSpeakTimestamp()
  {
    return requestToSpeak == 0L ? null : Helpers.toOffset(requestToSpeak);
  }
  

  @Nonnull
  public RestAction<Void> approveSpeaker()
  {
    return update(false);
  }
  

  @Nonnull
  public RestAction<Void> declineSpeaker()
  {
    return update(true);
  }
  
  private RestAction<Void> update(boolean suppress)
  {
    if ((requestToSpeak == 0L) || (!(connectedChannel instanceof StageChannel)))
      return new CompletedRestAction(api, null);
    Member selfMember = getGuild().getSelfMember();
    boolean isSelf = selfMember.equals(member);
    if (!isSelf) if (!selfMember.hasPermission(connectedChannel, new Permission[] { Permission.VOICE_MUTE_OTHERS })) {
        throw new InsufficientPermissionException(connectedChannel, Permission.VOICE_MUTE_OTHERS);
      }
    Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(new String[] { guild.getId(), isSelf ? "@me" : getId() });
    

    DataObject body = DataObject.empty().put("channel_id", connectedChannel.getId()).put("suppress", Boolean.valueOf(suppress));
    return new RestActionImpl(getJDA(), route, body);
  }
  

  @Nonnull
  public RestAction<Void> inviteSpeaker()
  {
    if (!(connectedChannel instanceof StageChannel))
      return new CompletedRestAction(api, null);
    if (!getGuild().getSelfMember().hasPermission(connectedChannel, new Permission[] { Permission.VOICE_MUTE_OTHERS })) {
      throw new InsufficientPermissionException(connectedChannel, Permission.VOICE_MUTE_OTHERS);
    }
    Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(new String[] { guild.getId(), getId() });
    


    DataObject body = DataObject.empty().put("channel_id", connectedChannel.getId()).put("suppress", Boolean.valueOf(false)).put("request_to_speak_timestamp", OffsetDateTime.now().toString());
    return new RestActionImpl(getJDA(), route, body);
  }
  

  public boolean isMuted()
  {
    return (isSelfMuted()) || (isGuildMuted());
  }
  

  public boolean isDeafened()
  {
    return (isSelfDeafened()) || (isGuildDeafened());
  }
  

  public boolean isGuildMuted()
  {
    return guildMuted;
  }
  

  public boolean isGuildDeafened()
  {
    return guildDeafened;
  }
  

  public boolean isSuppressed()
  {
    return suppressed;
  }
  

  public boolean isStream()
  {
    return stream;
  }
  

  public VoiceChannel getChannel()
  {
    return connectedChannel;
  }
  

  @Nonnull
  public Guild getGuild()
  {
    Guild realGuild = api.getGuildById(guild.getIdLong());
    if (realGuild != null)
      guild = realGuild;
    return guild;
  }
  

  @Nonnull
  public Member getMember()
  {
    Member realMember = getGuild().getMemberById(member.getIdLong());
    if (realMember != null)
      member = realMember;
    return member;
  }
  

  public boolean inVoiceChannel()
  {
    return getChannel() != null;
  }
  

  public long getIdLong()
  {
    return member.getIdLong();
  }
  

  public int hashCode()
  {
    return member.hashCode();
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof GuildVoiceState))
      return false;
    GuildVoiceState oStatus = (GuildVoiceState)obj;
    return member.equals(oStatus.getMember());
  }
  

  public String toString()
  {
    return "VS:" + getGuild().getName() + '(' + getId() + ')';
  }
  


  public GuildVoiceStateImpl setConnectedChannel(VoiceChannel connectedChannel)
  {
    this.connectedChannel = connectedChannel;
    return this;
  }
  
  public GuildVoiceStateImpl setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
    return this;
  }
  
  public GuildVoiceStateImpl setSelfMuted(boolean selfMuted)
  {
    this.selfMuted = selfMuted;
    return this;
  }
  
  public GuildVoiceStateImpl setSelfDeafened(boolean selfDeafened)
  {
    this.selfDeafened = selfDeafened;
    return this;
  }
  
  public GuildVoiceStateImpl setGuildMuted(boolean guildMuted)
  {
    this.guildMuted = guildMuted;
    return this;
  }
  
  public GuildVoiceStateImpl setGuildDeafened(boolean guildDeafened)
  {
    this.guildDeafened = guildDeafened;
    return this;
  }
  
  public GuildVoiceStateImpl setSuppressed(boolean suppressed)
  {
    this.suppressed = suppressed;
    return this;
  }
  
  public GuildVoiceStateImpl setStream(boolean stream)
  {
    this.stream = stream;
    return this;
  }
  
  public GuildVoiceStateImpl setRequestToSpeak(OffsetDateTime timestamp)
  {
    requestToSpeak = (timestamp == null ? 0L : timestamp.toInstant().toEpochMilli());
    return this;
  }
}
