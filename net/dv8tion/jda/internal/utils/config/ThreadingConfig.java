package net.dv8tion.jda.internal.utils.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;














public class ThreadingConfig
{
  private final Object audioLock = new Object();
  
  private ScheduledExecutorService rateLimitPool;
  
  private ScheduledExecutorService gatewayPool;
  private ExecutorService callbackPool;
  private ExecutorService eventPool;
  private ScheduledExecutorService audioPool;
  private boolean shutdownRateLimitPool;
  private boolean shutdownGatewayPool;
  private boolean shutdownCallbackPool;
  private boolean shutdownEventPool;
  private boolean shutdownAudioPool;
  
  public ThreadingConfig()
  {
    callbackPool = ForkJoinPool.commonPool();
    
    shutdownRateLimitPool = true;
    shutdownGatewayPool = true;
    shutdownCallbackPool = false;
    shutdownAudioPool = true;
  }
  
  public void setRateLimitPool(@Nullable ScheduledExecutorService executor, boolean shutdown)
  {
    rateLimitPool = executor;
    shutdownRateLimitPool = shutdown;
  }
  
  public void setGatewayPool(@Nullable ScheduledExecutorService executor, boolean shutdown)
  {
    gatewayPool = executor;
    shutdownGatewayPool = shutdown;
  }
  
  public void setCallbackPool(@Nullable ExecutorService executor, boolean shutdown)
  {
    callbackPool = (executor == null ? ForkJoinPool.commonPool() : executor);
    shutdownCallbackPool = shutdown;
  }
  
  public void setEventPool(@Nullable ExecutorService executor, boolean shutdown)
  {
    eventPool = executor;
    shutdownEventPool = shutdown;
  }
  
  public void setAudioPool(@Nullable ScheduledExecutorService executor, boolean shutdown)
  {
    audioPool = executor;
    shutdownAudioPool = shutdown;
  }
  
  public void init(@Nonnull Supplier<String> identifier)
  {
    if (rateLimitPool == null)
      rateLimitPool = newScheduler(5, identifier, "RateLimit", false);
    if (gatewayPool == null) {
      gatewayPool = newScheduler(1, identifier, "Gateway");
    }
  }
  
  public void shutdown() {
    if (shutdownCallbackPool)
      callbackPool.shutdown();
    if (shutdownGatewayPool)
      gatewayPool.shutdown();
    if ((shutdownEventPool) && (eventPool != null))
      eventPool.shutdown();
    if ((shutdownAudioPool) && (audioPool != null))
      audioPool.shutdown();
    if (shutdownRateLimitPool)
    {
      if ((rateLimitPool instanceof ScheduledThreadPoolExecutor))
      {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor)rateLimitPool;
        executor.setKeepAliveTime(5L, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
      }
      else
      {
        rateLimitPool.shutdown();
      }
    }
  }
  
  public void shutdownRequester()
  {
    if (shutdownRateLimitPool) {
      rateLimitPool.shutdown();
    }
  }
  
  public void shutdownNow() {
    if (shutdownCallbackPool)
      callbackPool.shutdownNow();
    if (shutdownGatewayPool)
      gatewayPool.shutdownNow();
    if (shutdownRateLimitPool)
      rateLimitPool.shutdownNow();
    if ((shutdownEventPool) && (eventPool != null))
      eventPool.shutdownNow();
    if ((shutdownAudioPool) && (audioPool != null)) {
      audioPool.shutdownNow();
    }
  }
  
  @Nonnull
  public ScheduledExecutorService getRateLimitPool() {
    return rateLimitPool;
  }
  
  @Nonnull
  public ScheduledExecutorService getGatewayPool()
  {
    return gatewayPool;
  }
  
  @Nonnull
  public ExecutorService getCallbackPool()
  {
    return callbackPool;
  }
  
  @Nullable
  public ExecutorService getEventPool()
  {
    return eventPool;
  }
  
  @Nullable
  public ScheduledExecutorService getAudioPool(@Nonnull Supplier<String> identifier)
  {
    ScheduledExecutorService pool = audioPool;
    if (pool == null)
    {
      synchronized (audioLock)
      {
        pool = audioPool;
        if (pool == null)
          pool = this.audioPool = newScheduler(1, identifier, "AudioLifeCycle");
      }
    }
    return pool;
  }
  
  public boolean isShutdownRateLimitPool()
  {
    return shutdownRateLimitPool;
  }
  
  public boolean isShutdownGatewayPool()
  {
    return shutdownGatewayPool;
  }
  
  public boolean isShutdownCallbackPool()
  {
    return shutdownCallbackPool;
  }
  
  public boolean isShutdownEventPool()
  {
    return shutdownEventPool;
  }
  
  public boolean isShutdownAudioPool()
  {
    return shutdownAudioPool;
  }
  
  @Nonnull
  public static ScheduledThreadPoolExecutor newScheduler(int coreSize, Supplier<String> identifier, String baseName)
  {
    return newScheduler(coreSize, identifier, baseName, true);
  }
  
  @Nonnull
  public static ScheduledThreadPoolExecutor newScheduler(int coreSize, Supplier<String> identifier, String baseName, boolean daemon)
  {
    return new ScheduledThreadPoolExecutor(coreSize, new CountingThreadFactory(identifier, baseName, daemon));
  }
  
  @Nonnull
  public static ThreadingConfig getDefault()
  {
    return new ThreadingConfig();
  }
}
