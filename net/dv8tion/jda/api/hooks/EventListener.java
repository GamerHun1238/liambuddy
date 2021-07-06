package net.dv8tion.jda.api.hooks;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;

@FunctionalInterface
public abstract interface EventListener
{
  public abstract void onEvent(@Nonnull GenericEvent paramGenericEvent);
}
