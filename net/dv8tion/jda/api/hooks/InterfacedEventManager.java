package net.dv8tion.jda.api.hooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;





























public class InterfacedEventManager
  implements IEventManager
{
  private final CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList();
  





  public InterfacedEventManager() {}
  





  public void register(@Nonnull Object listener)
  {
    if (!(listener instanceof EventListener))
    {
      throw new IllegalArgumentException("Listener must implement EventListener");
    }
    listeners.add((EventListener)listener);
  }
  

  public void unregister(@Nonnull Object listener)
  {
    if (!(listener instanceof EventListener))
    {

      JDALogger.getLog(getClass()).warn("Trying to remove a listener that does not implement EventListener: {}", 
      
        listener == null ? "null" : listener.getClass().getName());
    }
    

    listeners.remove(listener);
  }
  

  @Nonnull
  public List<Object> getRegisteredListeners()
  {
    return Collections.unmodifiableList(new ArrayList(listeners));
  }
  

  public void handle(@Nonnull GenericEvent event)
  {
    for (EventListener listener : listeners)
    {
      try
      {
        listener.onEvent(event);
      }
      catch (Throwable throwable)
      {
        JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
        if ((throwable instanceof Error)) {
          throw ((Error)throwable);
        }
      }
    }
  }
}
