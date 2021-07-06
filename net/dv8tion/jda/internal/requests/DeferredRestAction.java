package net.dv8tion.jda.internal.requests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Nullable;


















public class DeferredRestAction<T, R extends RestAction<T>>
  implements AuditableRestAction<T>
{
  private final JDA api;
  private final Class<T> type;
  private final Supplier<T> valueSupplier;
  private final Supplier<R> actionSupplier;
  private String reason;
  private long deadline = -1L;
  private BooleanSupplier isAction;
  private BooleanSupplier transitiveChecks;
  
  public DeferredRestAction(JDA api, Supplier<R> actionSupplier)
  {
    this(api, null, null, actionSupplier);
  }
  


  public DeferredRestAction(JDA api, Class<T> type, Supplier<T> valueSupplier, Supplier<R> actionSupplier)
  {
    this.api = api;
    this.type = type;
    this.valueSupplier = valueSupplier;
    this.actionSupplier = actionSupplier;
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  @Nonnull
  public AuditableRestAction<T> reason(String reason)
  {
    this.reason = reason;
    return this;
  }
  

  @Nonnull
  public AuditableRestAction<T> setCheck(BooleanSupplier checks)
  {
    transitiveChecks = checks;
    return this;
  }
  

  @Nullable
  public BooleanSupplier getCheck()
  {
    return transitiveChecks;
  }
  

  @Nonnull
  public AuditableRestAction<T> timeout(long timeout, @Nonnull TimeUnit unit)
  {
    Checks.notNull(unit, "TimeUnit");
    return deadline(timeout <= 0L ? 0L : System.currentTimeMillis() + unit.toMillis(timeout));
  }
  

  @Nonnull
  public AuditableRestAction<T> deadline(long timestamp)
  {
    deadline = timestamp;
    return this;
  }
  
  public AuditableRestAction<T> setCacheCheck(BooleanSupplier checks)
  {
    isAction = checks;
    return this;
  }
  
  public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure)
  {
    Consumer<? super T> finalSuccess;
    Consumer<? super T> finalSuccess;
    if (success != null) {
      finalSuccess = success;
    } else {
      finalSuccess = RestAction.getDefaultSuccess();
    }
    if (type == null)
    {
      BooleanSupplier checks = isAction;
      if ((checks != null) && (checks.getAsBoolean())) {
        getAction().queue(success, failure);
      } else
        finalSuccess.accept(null);
      return;
    }
    
    T value = valueSupplier.get();
    if (value == null)
    {
      getAction().queue(success, failure);
    }
    else
    {
      finalSuccess.accept(value);
    }
  }
  

  @Nonnull
  public CompletableFuture<T> submit(boolean shouldQueue)
  {
    if (type == null)
    {
      BooleanSupplier checks = isAction;
      if ((checks != null) && (checks.getAsBoolean()))
        return getAction().submit(shouldQueue);
      return CompletableFuture.completedFuture(null);
    }
    T value = valueSupplier.get();
    if (value != null)
      return CompletableFuture.completedFuture(value);
    return getAction().submit(shouldQueue);
  }
  
  public T complete(boolean shouldQueue)
    throws RateLimitedException
  {
    if (type == null)
    {
      BooleanSupplier checks = isAction;
      if ((checks != null) && (checks.getAsBoolean()))
        return getAction().complete(shouldQueue);
      return null;
    }
    T value = valueSupplier.get();
    if (value != null)
      return value;
    return getAction().complete(shouldQueue);
  }
  
  private R getAction()
  {
    R action = (RestAction)actionSupplier.get();
    action.setCheck(transitiveChecks);
    if (deadline >= 0L)
      action.deadline(deadline);
    if (((action instanceof AuditableRestAction)) && (reason != null))
      ((AuditableRestAction)action).reason(reason);
    return action;
  }
}
