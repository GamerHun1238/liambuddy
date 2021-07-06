package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;

@FunctionalInterface
public abstract interface Procedure<T>
{
  public abstract boolean execute(@Nonnull T paramT);
}
