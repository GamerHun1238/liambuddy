package net.dv8tion.jda.api.utils.data;

import javax.annotation.Nonnull;

public abstract interface SerializableData
{
  @Nonnull
  public abstract DataObject toData();
}
