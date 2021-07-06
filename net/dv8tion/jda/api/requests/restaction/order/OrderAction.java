package net.dv8tion.jda.api.requests.restaction.order;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface OrderAction<T, M extends OrderAction<T, M>>
  extends RestAction<Void>
{
  @Nonnull
  public abstract M setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract M timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract M deadline(long paramLong);
  
  public abstract boolean isAscendingOrder();
  
  @Nonnull
  public abstract List<T> getCurrentOrder();
  
  @Nonnull
  public abstract M selectPosition(int paramInt);
  
  @Nonnull
  public abstract M selectPosition(@Nonnull T paramT);
  
  public abstract int getSelectedPosition();
  
  @Nonnull
  public abstract T getSelectedEntity();
  
  @Nonnull
  public abstract M moveUp(int paramInt);
  
  @Nonnull
  public abstract M moveDown(int paramInt);
  
  @Nonnull
  public abstract M moveTo(int paramInt);
  
  @Nonnull
  public abstract M swapPosition(int paramInt);
  
  @Nonnull
  public abstract M swapPosition(@Nonnull T paramT);
  
  @Nonnull
  public abstract M reverseOrder();
  
  @Nonnull
  public abstract M shuffleOrder();
  
  @Nonnull
  public abstract M sortOrder(@Nonnull Comparator<T> paramComparator);
}
