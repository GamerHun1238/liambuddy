package net.dv8tion.jda.api.utils.data;

import javax.annotation.Nonnull;

public abstract interface SerializableArray
{
  @Nonnull
  public abstract DataArray toDataArray();
}
