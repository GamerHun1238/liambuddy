package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.RestAction;

public abstract interface PrivateChannel
  extends MessageChannel
{
  @Nonnull
  public abstract User getUser();
  
  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Void> close();
}
