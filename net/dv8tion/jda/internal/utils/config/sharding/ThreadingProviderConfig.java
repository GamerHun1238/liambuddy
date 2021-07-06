package net.dv8tion.jda.internal.utils.config.sharding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.sharding.ThreadPoolProvider;
























public class ThreadingProviderConfig
{
  private final ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitPoolProvider;
  private final ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider;
  private final ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider;
  private final ThreadPoolProvider<? extends ExecutorService> eventPoolProvider;
  private final ThreadPoolProvider<? extends ScheduledExecutorService> audioPoolProvider;
  private final ThreadFactory threadFactory;
  
  public ThreadingProviderConfig(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitPoolProvider, @Nullable ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider, @Nullable ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider, @Nullable ThreadPoolProvider<? extends ExecutorService> eventPoolProvider, @Nullable ThreadPoolProvider<? extends ScheduledExecutorService> audioPoolProvider, @Nullable ThreadFactory threadFactory)
  {
    this.rateLimitPoolProvider = rateLimitPoolProvider;
    this.gatewayPoolProvider = gatewayPoolProvider;
    this.callbackPoolProvider = callbackPoolProvider;
    this.eventPoolProvider = eventPoolProvider;
    this.audioPoolProvider = audioPoolProvider;
    this.threadFactory = threadFactory;
  }
  
  @Nullable
  public ThreadFactory getThreadFactory()
  {
    return threadFactory;
  }
  
  @Nullable
  public ThreadPoolProvider<? extends ScheduledExecutorService> getRateLimitPoolProvider()
  {
    return rateLimitPoolProvider;
  }
  
  @Nullable
  public ThreadPoolProvider<? extends ScheduledExecutorService> getGatewayPoolProvider()
  {
    return gatewayPoolProvider;
  }
  
  @Nullable
  public ThreadPoolProvider<? extends ExecutorService> getCallbackPoolProvider()
  {
    return callbackPoolProvider;
  }
  
  @Nullable
  public ThreadPoolProvider<? extends ExecutorService> getEventPoolProvider()
  {
    return eventPoolProvider;
  }
  
  @Nullable
  public ThreadPoolProvider<? extends ScheduledExecutorService> getAudioPoolProvider()
  {
    return audioPoolProvider;
  }
  
  @Nonnull
  public static ThreadingProviderConfig getDefault()
  {
    return new ThreadingProviderConfig(null, null, null, null, null, null);
  }
}
