package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.internal.utils.Checks;








































































public abstract interface StageChannel
  extends VoiceChannel
{
  @Nullable
  public abstract StageInstance getStageInstance();
  
  @Nonnull
  @CheckReturnValue
  public abstract StageInstanceAction createStageInstance(@Nonnull String paramString);
  
  public boolean isModerator(@Nonnull Member member)
  {
    Checks.notNull(member, "Member");
    return member.hasPermission(this, new Permission[] { Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS });
  }
}
