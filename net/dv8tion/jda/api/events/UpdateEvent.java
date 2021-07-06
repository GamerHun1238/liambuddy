package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;






























public abstract interface UpdateEvent<E, T>
  extends GenericEvent
{
  @Nonnull
  public Class<E> getEntityType()
  {
    return getEntity().getClass();
  }
  
  @Nonnull
  public abstract String getPropertyIdentifier();
  
  @Nonnull
  public abstract E getEntity();
  
  @Nullable
  public abstract T getOldValue();
  
  @Nullable
  public abstract T getNewValue();
}
