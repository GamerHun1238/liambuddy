package net.dv8tion.jda.internal.utils;

import net.dv8tion.jda.api.utils.data.DataObject;

@FunctionalInterface
public abstract interface CacheConsumer
{
  public abstract void execute(long paramLong, DataObject paramDataObject);
}
