package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.TimeUtil;




























public abstract interface ISnowflake
{
  @Nonnull
  public String getId()
  {
    return Long.toUnsignedString(getIdLong());
  }
  






  public abstract long getIdLong();
  






  @Nonnull
  public OffsetDateTime getTimeCreated()
  {
    return TimeUtil.getTimeCreated(getIdLong());
  }
}
