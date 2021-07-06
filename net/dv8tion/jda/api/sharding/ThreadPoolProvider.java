package net.dv8tion.jda.api.sharding;

import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;









































public abstract interface ThreadPoolProvider<T extends ExecutorService>
{
  @Nullable
  public abstract T provide(int paramInt);
  
  public boolean shouldShutdownAutomatically(int shardId)
  {
    return false;
  }
}
