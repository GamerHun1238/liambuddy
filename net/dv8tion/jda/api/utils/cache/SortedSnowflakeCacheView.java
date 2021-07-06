package net.dv8tion.jda.api.utils.cache;

import java.util.NavigableSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;

public abstract interface SortedSnowflakeCacheView<T extends Comparable<? super T>,  extends ISnowflake>
  extends SnowflakeCacheView<T>
{
  public abstract void forEachUnordered(@Nonnull Consumer<? super T> paramConsumer);
  
  @Nonnull
  public abstract NavigableSet<T> asSet();
  
  @Nonnull
  public abstract Stream<T> streamUnordered();
  
  @Nonnull
  public abstract Stream<T> parallelStreamUnordered();
}
