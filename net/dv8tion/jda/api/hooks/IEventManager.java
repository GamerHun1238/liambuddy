package net.dv8tion.jda.api.hooks;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract interface IEventManager
{
  public abstract void register(@Nonnull Object paramObject);
  
  public abstract void unregister(@Nonnull Object paramObject);
  
  public abstract void handle(@Nonnull GenericEvent paramGenericEvent);
  
  @Nonnull
  public abstract List<Object> getRegisteredListeners();
}
