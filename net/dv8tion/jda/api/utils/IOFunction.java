package net.dv8tion.jda.api.utils;

import java.io.IOException;

@FunctionalInterface
public abstract interface IOFunction<T, R>
{
  public abstract R apply(T paramT)
    throws IOException;
}
