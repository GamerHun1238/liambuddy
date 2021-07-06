package net.dv8tion.jda.internal.utils.config.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;
import net.dv8tion.jda.internal.utils.config.flags.ShardingConfigFlag;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;





















public class ShardingSessionConfig
  extends SessionConfig
{
  private final OkHttpClient.Builder builder;
  private final IAudioSendFactory audioSendFactory;
  private final EnumSet<ShardingConfigFlag> shardingFlags;
  
  public ShardingSessionConfig(@Nullable SessionController sessionController, @Nullable VoiceDispatchInterceptor interceptor, @Nullable OkHttpClient httpClient, @Nullable OkHttpClient.Builder httpClientBuilder, @Nullable WebSocketFactory webSocketFactory, @Nullable IAudioSendFactory audioSendFactory, EnumSet<ConfigFlag> flags, EnumSet<ShardingConfigFlag> shardingFlags, int maxReconnectDelay, int largeThreshold)
  {
    super(sessionController, httpClient, webSocketFactory, interceptor, flags, maxReconnectDelay, largeThreshold);
    if (httpClient == null) {
      builder = (httpClientBuilder == null ? IOUtil.newHttpClientBuilder() : httpClientBuilder);
    } else
      builder = null;
    this.audioSendFactory = audioSendFactory;
    this.shardingFlags = shardingFlags;
  }
  
  public SessionConfig toSessionConfig(OkHttpClient client)
  {
    return new SessionConfig(getSessionController(), client, getWebSocketFactory(), getVoiceDispatchInterceptor(), getFlags(), getMaxReconnectDelay(), getLargeThreshold());
  }
  
  public EnumSet<ShardingConfigFlag> getShardingFlags()
  {
    return shardingFlags;
  }
  
  @Nullable
  public OkHttpClient.Builder getHttpBuilder()
  {
    return builder;
  }
  
  @Nullable
  public IAudioSendFactory getAudioSendFactory()
  {
    return audioSendFactory;
  }
  
  @Nonnull
  public static ShardingSessionConfig getDefault()
  {
    return new ShardingSessionConfig(null, null, new OkHttpClient(), null, null, null, ConfigFlag.getDefault(), ShardingConfigFlag.getDefault(), 900, 250);
  }
}
