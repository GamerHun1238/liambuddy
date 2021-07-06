package net.dv8tion.jda.api.events.channel.category.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;
























public abstract class GenericCategoryUpdateEvent<T>
  extends GenericCategoryEvent
  implements UpdateEvent<Category, T>
{
  protected final T previous;
  protected final T next;
  protected final String identifier;
  
  public GenericCategoryUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, @Nullable T previous, @Nullable T next, @Nonnull String identifier)
  {
    super(api, responseNumber, category);
    this.previous = previous;
    this.next = next;
    this.identifier = identifier;
  }
  

  @Nonnull
  public Category getEntity()
  {
    return getCategory();
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
    return "CategoryUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
  }
}
