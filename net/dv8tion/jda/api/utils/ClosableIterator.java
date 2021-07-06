package net.dv8tion.jda.api.utils;

import java.util.Iterator;

public abstract interface ClosableIterator<T>
  extends Iterator<T>, AutoCloseable
{
  public abstract void close();
}
