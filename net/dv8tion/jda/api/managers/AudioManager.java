package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

























public abstract interface AudioManager
{
  public static final long DEFAULT_CONNECTION_TIMEOUT = 10000L;
  public static final Logger LOG = JDALogger.getLog(AudioManager.class);
  

















  public abstract void openAudioConnection(VoiceChannel paramVoiceChannel);
  

















  public abstract void closeAudioConnection();
  
















  @Incubating
  public abstract void setSpeakingMode(@Nonnull Collection<SpeakingMode> paramCollection);
  
















  @Incubating
  public void setSpeakingMode(@Nonnull SpeakingMode... mode)
  {
    Checks.notNull(mode, "Speaking Mode");
    setSpeakingMode(Arrays.asList(mode));
  }
  
  @Nonnull
  @Incubating
  public abstract EnumSet<SpeakingMode> getSpeakingMode();
  
  public abstract void setSpeakingDelay(int paramInt);
  
  @Nonnull
  public abstract JDA getJDA();
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Deprecated
  @ForRemoval
  @DeprecatedSince("4.2.0")
  public abstract boolean isAttemptingToConnect();
  
  @Nullable
  @Deprecated
  @ForRemoval
  @DeprecatedSince("4.2.0")
  public abstract VoiceChannel getQueuedAudioConnection();
  
  @Nullable
  public abstract VoiceChannel getConnectedChannel();
  
  public abstract boolean isConnected();
  
  public abstract void setConnectTimeout(long paramLong);
  
  public abstract long getConnectTimeout();
  
  public abstract void setSendingHandler(@Nullable AudioSendHandler paramAudioSendHandler);
  
  @Nullable
  public abstract AudioSendHandler getSendingHandler();
  
  public abstract void setReceivingHandler(@Nullable AudioReceiveHandler paramAudioReceiveHandler);
  
  @Nullable
  public abstract AudioReceiveHandler getReceivingHandler();
  
  public abstract void setConnectionListener(@Nullable ConnectionListener paramConnectionListener);
  
  @Nullable
  public abstract ConnectionListener getConnectionListener();
  
  @Nonnull
  public abstract ConnectionStatus getConnectionStatus();
  
  public abstract void setAutoReconnect(boolean paramBoolean);
  
  public abstract boolean isAutoReconnect();
  
  public abstract void setSelfMuted(boolean paramBoolean);
  
  public abstract boolean isSelfMuted();
  
  public abstract void setSelfDeafened(boolean paramBoolean);
  
  public abstract boolean isSelfDeafened();
}
