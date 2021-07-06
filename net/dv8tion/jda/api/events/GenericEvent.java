package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public abstract interface GenericEvent
{
  @Nonnull
  public abstract JDA getJDA();
  
  public abstract long getResponseNumber();
}
