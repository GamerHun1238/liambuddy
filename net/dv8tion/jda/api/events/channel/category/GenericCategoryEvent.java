package net.dv8tion.jda.api.events.channel.category;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;























public abstract class GenericCategoryEvent
  extends Event
{
  protected final Category category;
  
  public GenericCategoryEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category)
  {
    super(api, responseNumber);
    this.category = category;
  }
  





  @Nonnull
  public Category getCategory()
  {
    return category;
  }
  





  @Nonnull
  public String getId()
  {
    return Long.toUnsignedString(getIdLong());
  }
  





  public long getIdLong()
  {
    return category.getIdLong();
  }
  






  @Nonnull
  public Guild getGuild()
  {
    return category.getGuild();
  }
}
