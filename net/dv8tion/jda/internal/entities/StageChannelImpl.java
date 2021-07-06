package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.internal.requests.restaction.StageInstanceActionImpl;















public class StageChannelImpl
  extends VoiceChannelImpl
  implements StageChannel
{
  private StageInstance instance;
  
  public StageChannelImpl(long id, GuildImpl guild)
  {
    super(id, guild);
  }
  

  @Nonnull
  public ChannelType getType()
  {
    return ChannelType.STAGE;
  }
  

  @Nullable
  public StageInstance getStageInstance()
  {
    return instance;
  }
  

  @Nonnull
  public StageInstanceAction createStageInstance(@Nonnull String topic)
  {
    EnumSet<Permission> permissions = getGuild().getSelfMember().getPermissions(this);
    EnumSet<Permission> required = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS);
    for (Permission perm : required)
    {
      if (!permissions.contains(perm)) {
        throw new InsufficientPermissionException(this, perm, "You must be a stage moderator to create a stage instance! Missing Permission: " + perm);
      }
    }
    return new StageInstanceActionImpl(this).setTopic(topic);
  }
  
  public StageChannelImpl setStageInstance(StageInstance instance)
  {
    this.instance = instance;
    return this;
  }
}
