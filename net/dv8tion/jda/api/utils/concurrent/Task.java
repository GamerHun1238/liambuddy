package net.dv8tion.jda.api.utils.concurrent;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract interface Task<T>
{
  public abstract boolean isStarted();
  
  @Nonnull
  public abstract Task<T> onError(@Nonnull Consumer<? super Throwable> paramConsumer);
  
  @Nonnull
  public abstract Task<T> onSuccess(@Nonnull Consumer<? super T> paramConsumer);
  
  @Nonnull
  public abstract T get();
  
  public abstract void cancel();
}
