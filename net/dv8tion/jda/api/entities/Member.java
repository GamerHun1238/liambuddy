package net.dv8tion.jda.api.entities;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;









































































































































































































































































































public abstract interface Member
  extends IMentionable, IPermissionHolder
{
  @Nonnull
  public abstract User getUser();
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract JDA getJDA();
  
  @Nonnull
  public abstract OffsetDateTime getTimeJoined();
  
  public abstract boolean hasTimeJoined();
  
  @Nullable
  public abstract OffsetDateTime getTimeBoosted();
  
  @Nullable
  public abstract GuildVoiceState getVoiceState();
  
  @Nonnull
  public abstract List<Activity> getActivities();
  
  @Nonnull
  public abstract OnlineStatus getOnlineStatus();
  
  @Nonnull
  public abstract OnlineStatus getOnlineStatus(@Nonnull ClientType paramClientType);
  
  @Nonnull
  public abstract EnumSet<ClientType> getActiveClients();
  
  @Nullable
  public abstract String getNickname();
  
  @Nonnull
  public abstract String getEffectiveName();
  
  @Nonnull
  public abstract List<Role> getRoles();
  
  @Nullable
  public abstract Color getColor();
  
  public abstract int getColorRaw();
  
  public abstract boolean canInteract(@Nonnull Member paramMember);
  
  public abstract boolean canInteract(@Nonnull Role paramRole);
  
  public abstract boolean canInteract(@Nonnull Emote paramEmote);
  
  public abstract boolean isOwner();
  
  @Incubating
  public abstract boolean isPending();
  
  @Nullable
  public abstract TextChannel getDefaultChannel();
  
  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> ban(int delDays)
  {
    return getGuild().ban(this, delDays);
  }
  










































  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> ban(int delDays, @Nullable String reason)
  {
    return getGuild().ban(this, delDays, reason);
  }
  



























  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> kick()
  {
    return getGuild().kick(this);
  }
  
































  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> kick(@Nullable String reason)
  {
    return getGuild().kick(this, reason);
  }
  
































  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> mute(boolean mute)
  {
    return getGuild().mute(this, mute);
  }
  































  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> deafen(boolean deafen)
  {
    return getGuild().deafen(this, deafen);
  }
  





































  @Nonnull
  @CheckReturnValue
  public AuditableRestAction<Void> modifyNickname(@Nullable String nickname)
  {
    return getGuild().modifyNickname(this, nickname);
  }
}
