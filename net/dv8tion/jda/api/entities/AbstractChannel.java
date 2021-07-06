package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public abstract interface AbstractChannel
  extends ISnowflake
{
  @Nonnull
  public abstract String getName();
  
  @Nonnull
  public abstract ChannelType getType();
  
  @Nonnull
  public abstract JDA getJDA();
}
