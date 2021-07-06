package net.dv8tion.jda.api.requests.restaction.order;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Category;

public abstract interface CategoryOrderAction
  extends ChannelOrderAction
{
  @Nonnull
  public abstract Category getCategory();
}
