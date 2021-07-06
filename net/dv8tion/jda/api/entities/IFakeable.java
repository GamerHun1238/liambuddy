package net.dv8tion.jda.api.entities;

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;

@Deprecated
@DeprecatedSince("4.2.1")
@ForRemoval(deadline="4.3.0")
public abstract interface IFakeable
{
  @Deprecated
  @DeprecatedSince("4.2.1")
  @ForRemoval(deadline="4.3.0")
  public abstract boolean isFake();
}
