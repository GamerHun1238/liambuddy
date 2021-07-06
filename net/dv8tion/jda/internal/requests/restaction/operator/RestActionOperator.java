package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ContextException.ContextConsumer;
import net.dv8tion.jda.api.requests.RestAction;
















public abstract class RestActionOperator<I, O>
  implements RestAction<O>
{
  protected BooleanSupplier check;
  protected long deadline = -1L;
  protected final RestAction<I> action;
  
  public RestActionOperator(RestAction<I> action)
  {
    this.action = action;
  }
  
  protected static <E> void doSuccess(Consumer<? super E> callback, E value)
  {
    if (callback == null) {
      RestAction.getDefaultSuccess().accept(value);
    } else {
      callback.accept(value);
    }
  }
  
  protected static void doFailure(Consumer<? super Throwable> callback, Throwable throwable) {
    if (callback == null) {
      RestAction.getDefaultFailure().accept(throwable);
    } else
      callback.accept(throwable);
    if ((throwable instanceof Error)) {
      throw ((Error)throwable);
    }
  }
  
  @Nonnull
  public JDA getJDA()
  {
    return action.getJDA();
  }
  

  @Nonnull
  public RestAction<O> setCheck(@Nullable BooleanSupplier checks)
  {
    check = checks;
    action.setCheck(checks);
    return this;
  }
  

  @Nullable
  public BooleanSupplier getCheck()
  {
    return action.getCheck();
  }
  

  @Nonnull
  public RestAction<O> deadline(long timestamp)
  {
    deadline = timestamp;
    action.deadline(timestamp);
    return this;
  }
  
  protected <T> RestAction<T> applyContext(RestAction<T> action)
  {
    if (action == null)
      return null;
    if (check != null)
      action.setCheck(check);
    if (deadline >= 0L)
      action.deadline(deadline);
    return action;
  }
  
  protected Consumer<? super Throwable> contextWrap(@Nullable Consumer<? super Throwable> callback)
  {
    if ((callback instanceof ContextException.ContextConsumer))
      return callback;
    if (RestAction.isPassContext())
      return ContextException.here(callback == null ? RestAction.getDefaultFailure() : callback);
    return callback;
  }
}
