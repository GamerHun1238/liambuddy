package net.dv8tion.jda.api.utils;

import java.io.IOException;

@FunctionalInterface
public abstract interface IOBiConsumer<T, R>
{
  public abstract void accept(T paramT, R paramR)
    throws IOException;
}
