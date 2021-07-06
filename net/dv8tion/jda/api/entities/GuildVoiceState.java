package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface GuildVoiceState
  extends ISnowflake
{
  @Nonnull
  public abstract JDA getJDA();
  
  public abstract boolean isSelfMuted();
  
  public abstract boolean isSelfDeafened();
  
  public abstract boolean isMuted();
  
  public abstract boolean isDeafened();
  
  public abstract boolean isGuildMuted();
  
  public abstract boolean isGuildDeafened();
  
  public abstract boolean isSuppressed();
  
  public abstract boolean isStream();
  
  @Nullable
  public abstract VoiceChannel getChannel();
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract Member getMember();
  
  public abstract boolean inVoiceChannel();
  
  @Nullable
  public abstract String getSessionId();
  
  @Nullable
  public abstract OffsetDateTime getRequestToSpeakTimestamp();
  
  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Void> approveSpeaker();
  
  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Void> declineSpeaker();
  
  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Void> inviteSpeaker();
}
