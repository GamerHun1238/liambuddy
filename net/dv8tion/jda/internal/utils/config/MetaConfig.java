package net.dv8tion.jda.internal.utils.config;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;


















public class MetaConfig
{
  private static final MetaConfig defaultConfig = new MetaConfig(2048, null, EnumSet.allOf(CacheFlag.class), ConfigFlag.getDefault());
  
  private final ConcurrentMap<String, String> mdcContextMap;
  
  private final EnumSet<CacheFlag> cacheFlags;
  
  private final boolean enableMDC;
  private final boolean useShutdownHook;
  private final int maxBufferSize;
  
  public MetaConfig(int maxBufferSize, @Nullable ConcurrentMap<String, String> mdcContextMap, @Nullable EnumSet<CacheFlag> cacheFlags, EnumSet<ConfigFlag> flags)
  {
    this.maxBufferSize = maxBufferSize;
    this.cacheFlags = (cacheFlags == null ? EnumSet.allOf(CacheFlag.class) : cacheFlags);
    enableMDC = flags.contains(ConfigFlag.MDC_CONTEXT);
    if (enableMDC) {
      this.mdcContextMap = (mdcContextMap == null ? new ConcurrentHashMap() : null);
    } else
      this.mdcContextMap = null;
    useShutdownHook = flags.contains(ConfigFlag.SHUTDOWN_HOOK);
  }
  
  @Nullable
  public ConcurrentMap<String, String> getMdcContextMap()
  {
    return mdcContextMap;
  }
  
  @Nonnull
  public EnumSet<CacheFlag> getCacheFlags()
  {
    return cacheFlags;
  }
  
  public boolean isEnableMDC()
  {
    return enableMDC;
  }
  
  public boolean isUseShutdownHook()
  {
    return useShutdownHook;
  }
  
  public int getMaxBufferSize()
  {
    return maxBufferSize;
  }
  
  @Nonnull
  public static MetaConfig getDefault()
  {
    return defaultConfig;
  }
}
