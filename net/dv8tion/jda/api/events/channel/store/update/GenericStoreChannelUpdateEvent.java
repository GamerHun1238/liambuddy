package net.dv8tion.jda.api.events.channel.store.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent;























public abstract class GenericStoreChannelUpdateEvent<T>
  extends GenericStoreChannelEvent
  implements UpdateEvent<StoreChannel, T>
{
  protected final T prev;
  protected final T next;
  protected final String identifier;
  
  public GenericStoreChannelUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, @Nullable T prev, @Nullable T next, @Nonnull String identifier)
  {
    super(api, responseNumber, channel);
    this.prev = prev;
    this.next = next;
    this.identifier = identifier;
  }
  

  @Nonnull
  public String getPropertyIdentifier()
  {
    return identifier;
  }
  

  @Nonnull
  public StoreChannel getEntity()
  {
    return channel;
  }
  

  @Nullable
  public T getOldValue()
  {
    return prev;
  }
  

  @Nullable
  public T getNewValue()
  {
    return next;
  }
  

  public String toString()
  {
    return "StoreChannelUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
  }
}
