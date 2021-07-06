package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;

public abstract interface ListedEmote
  extends Emote
{
  @Nonnull
  public abstract User getUser();
  
  public abstract boolean hasUser();
}
