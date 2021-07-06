package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.MiscUtil;











































public abstract interface SnowflakeCacheView<T extends ISnowflake>
  extends CacheView<T>
{
  @Nullable
  public abstract T getElementById(long paramLong);
  
  @Nullable
  public T getElementById(@Nonnull String id)
  {
    return getElementById(MiscUtil.parseSnowflake(id));
  }
}
