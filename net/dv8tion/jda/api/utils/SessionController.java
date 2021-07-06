package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.ShardInfo;
import net.dv8tion.jda.internal.utils.tuple.Pair;































































































































































public abstract interface SessionController
{
  public static final int IDENTIFY_DELAY = 5;
  
  public void setConcurrency(int level) {}
  
  public abstract void appendSession(@Nonnull SessionConnectNode paramSessionConnectNode);
  
  public abstract void removeSession(@Nonnull SessionConnectNode paramSessionConnectNode);
  
  public abstract long getGlobalRatelimit();
  
  public abstract void setGlobalRatelimit(long paramLong);
  
  @Nonnull
  public abstract String getGateway(@Nonnull JDA paramJDA);
  
  @Nonnull
  @Deprecated
  @ForRemoval(deadline="4.4.0")
  @DeprecatedSince("4.0.0")
  @ReplaceWith("getShardedGateway(api)")
  public abstract Pair<String, Integer> getGatewayBot(@Nonnull JDA paramJDA);
  
  @Nonnull
  public ShardedGateway getShardedGateway(@Nonnull JDA api)
  {
    Pair<String, Integer> tuple = getGatewayBot(api);
    return new ShardedGateway((String)tuple.getLeft(), ((Integer)tuple.getRight()).intValue());
  }
  
  public static abstract interface SessionConnectNode
  {
    public abstract boolean isReconnect();
    
    @Nonnull
    public abstract JDA getJDA();
    
    @Nonnull
    public abstract JDA.ShardInfo getShardInfo();
    
    public abstract void run(boolean paramBoolean) throws InterruptedException;
  }
  
  public static class ShardedGateway {
    private final String url;
    private final int shardTotal;
    private final int concurrency;
    
    public ShardedGateway(String url, int shardTotal) {
      this(url, shardTotal, 1);
    }
    
    public ShardedGateway(String url, int shardTotal, int concurrency)
    {
      this.url = url;
      this.shardTotal = shardTotal;
      this.concurrency = concurrency;
    }
    





    public String getUrl()
    {
      return url;
    }
    





    public int getShardTotal()
    {
      return shardTotal;
    }
    









    public int getConcurrency()
    {
      return concurrency;
    }
  }
}
