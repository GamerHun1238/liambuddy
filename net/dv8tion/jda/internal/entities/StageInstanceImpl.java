package net.dv8tion.jda.internal.entities;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.StageInstance.PrivacyLevel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.StageInstanceManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.managers.StageInstanceManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import net.dv8tion.jda.internal.requests.Route.StageInstances;











public class StageInstanceImpl
  implements StageInstance
{
  private final long id;
  private StageChannel channel;
  private StageInstanceManager manager;
  private String topic;
  private StageInstance.PrivacyLevel privacyLevel;
  private boolean discoverable;
  
  public StageInstanceImpl(long id, StageChannel channel)
  {
    this.id = id;
    this.channel = channel;
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  @Nonnull
  public Guild getGuild()
  {
    return getChannel().getGuild();
  }
  

  @Nonnull
  public StageChannel getChannel()
  {
    StageChannel real = channel.getJDA().getStageChannelById(channel.getIdLong());
    if (real != null)
      channel = real;
    return channel;
  }
  

  @Nonnull
  public String getTopic()
  {
    return topic;
  }
  

  @Nonnull
  public StageInstance.PrivacyLevel getPrivacyLevel()
  {
    return privacyLevel;
  }
  

  public boolean isDiscoverable()
  {
    return discoverable;
  }
  

  @Nonnull
  public RestAction<Void> delete()
  {
    checkPermissions();
    Route.CompiledRoute route = Route.StageInstances.DELETE_INSTANCE.compile(new String[] { channel.getId() });
    return new RestActionImpl(channel.getJDA(), route);
  }
  

  @Nonnull
  public RestAction<Void> requestToSpeak()
  {
    Guild guild = getGuild();
    Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(new String[] { guild.getId(), "@me" });
    DataObject body = DataObject.empty().put("channel_id", channel.getId());
    
    if (guild.getSelfMember().hasPermission(getChannel(), new Permission[] { Permission.VOICE_MUTE_OTHERS })) {
      body.putNull("request_to_speak_timestamp").put("suppress", Boolean.valueOf(false));
    } else {
      body.put("request_to_speak_timestamp", OffsetDateTime.now().toString());
    }
    if (!channel.equals(guild.getSelfMember().getVoiceState().getChannel()))
      throw new IllegalStateException("Cannot request to speak without being connected to the stage channel!");
    return new RestActionImpl(channel.getJDA(), route, body);
  }
  

  @Nonnull
  public RestAction<Void> cancelRequestToSpeak()
  {
    Guild guild = getGuild();
    Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(new String[] { guild.getId(), "@me" });
    


    DataObject body = DataObject.empty().putNull("request_to_speak_timestamp").put("suppress", Boolean.valueOf(true)).put("channel_id", channel.getId());
    
    if (!channel.equals(guild.getSelfMember().getVoiceState().getChannel()))
      throw new IllegalStateException("Cannot cancel request to speak without being connected to the stage channel!");
    return new RestActionImpl(channel.getJDA(), route, body);
  }
  

  @Nonnull
  public StageInstanceManager getManager()
  {
    checkPermissions();
    if (manager == null)
      manager = new StageInstanceManagerImpl(this);
    return manager;
  }
  
  public StageInstanceImpl setTopic(String topic)
  {
    this.topic = topic;
    return this;
  }
  
  public StageInstanceImpl setPrivacyLevel(StageInstance.PrivacyLevel privacyLevel)
  {
    this.privacyLevel = privacyLevel;
    return this;
  }
  
  public StageInstanceImpl setDiscoverable(boolean discoverable)
  {
    this.discoverable = discoverable;
    return this;
  }
  
  private void checkPermissions()
  {
    EnumSet<Permission> permissions = getGuild().getSelfMember().getPermissions(getChannel());
    EnumSet<Permission> required = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS);
    for (Permission perm : required)
    {
      if (!permissions.contains(perm)) {
        throw new InsufficientPermissionException(getChannel(), perm, "You must be a stage moderator to manage a stage instance! Missing Permission: " + perm);
      }
    }
  }
}
