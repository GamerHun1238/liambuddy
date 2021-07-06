package net.dv8tion.jda.api.utils.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;












































public abstract interface ShardCacheView
  extends CacheView<JDA>
{
  @Nullable
  public abstract JDA getElementById(int paramInt);
  
  @Nullable
  public JDA getElementById(@Nonnull String id)
  {
    return getElementById(Integer.parseUnsignedInt(id));
  }
}
