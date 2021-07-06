package net.dv8tion.jda.internal.utils.cache;

import gnu.trove.map.TLongObjectMap;
import java.util.function.Function;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;















public class SnowflakeCacheViewImpl<T extends ISnowflake>
  extends AbstractCacheView<T>
  implements SnowflakeCacheView<T>
{
  public SnowflakeCacheViewImpl(Class<T> type, Function<T, String> nameMapper)
  {
    super(type, nameMapper);
  }
  

  public T getElementById(long id)
  {
    if (elements.isEmpty())
      return null;
    return (ISnowflake)get(id);
  }
}
