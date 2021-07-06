package net.dv8tion.jda.api.utils;

import java.io.IOException;

@FunctionalInterface
public abstract interface IOConsumer<T>
{
  public abstract void accept(T paramT)
    throws IOException;
}
