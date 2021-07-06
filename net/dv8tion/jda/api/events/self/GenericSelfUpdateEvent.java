package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.UpdateEvent;
























public abstract class GenericSelfUpdateEvent<T>
  extends Event
  implements UpdateEvent<SelfUser, T>
{
  protected final T previous;
  protected final T next;
  protected final String identifier;
  
  public GenericSelfUpdateEvent(@Nonnull JDA api, long responseNumber, @Nullable T previous, @Nullable T next, @Nonnull String identifier)
  {
    super(api, responseNumber);
    this.previous = previous;
    this.next = next;
    this.identifier = identifier;
  }
  





  @Nonnull
  public SelfUser getSelfUser()
  {
    return api.getSelfUser();
  }
  

  @Nonnull
  public SelfUser getEntity()
  {
    return getSelfUser();
  }
  

  @Nonnull
  public String getPropertyIdentifier()
  {
    return identifier;
  }
  

  @Nullable
  public T getOldValue()
  {
    return previous;
  }
  

  @Nullable
  public T getNewValue()
  {
    return next;
  }
  

  public String toString()
  {
    return "SelfUserUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
  }
}
