package net.dv8tion.jda.api.events.emote.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;




























public abstract class GenericEmoteUpdateEvent<T>
  extends GenericEmoteEvent
  implements UpdateEvent<Emote, T>
{
  protected final T previous;
  protected final T next;
  protected final String identifier;
  
  public GenericEmoteUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote, @Nullable T previous, @Nullable T next, @Nonnull String identifier)
  {
    super(api, responseNumber, emote);
    this.previous = previous;
    this.next = next;
    this.identifier = identifier;
  }
  

  @Nonnull
  public Emote getEntity()
  {
    return getEmote();
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
    return "EmoteUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
  }
}
