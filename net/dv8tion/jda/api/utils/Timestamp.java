package net.dv8tion.jda.api.utils;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

























public class Timestamp
{
  private final TimeFormat format;
  private final long timestamp;
  
  protected Timestamp(TimeFormat format, long timestamp)
  {
    Checks.notNull(format, "TimeFormat");
    this.format = format;
    this.timestamp = timestamp;
  }
  





  @Nonnull
  public TimeFormat getFormat()
  {
    return format;
  }
  







  public long getTimestamp()
  {
    return timestamp;
  }
  





  @Nonnull
  public Instant toInstant()
  {
    return Instant.ofEpochMilli(timestamp);
  }
  










  @Nonnull
  public Timestamp plus(long millis)
  {
    return new Timestamp(format, timestamp + millis);
  }
  













  @Nonnull
  public Timestamp plus(@Nonnull Duration duration)
  {
    Checks.notNull(duration, "Duration");
    return plus(duration.toMillis());
  }
  










  @Nonnull
  public Timestamp minus(long millis)
  {
    return new Timestamp(format, timestamp - millis);
  }
  













  @Nonnull
  public Timestamp minus(@Nonnull Duration duration)
  {
    Checks.notNull(duration, "Duration");
    return minus(duration.toMillis());
  }
  

  public String toString()
  {
    return "<t:" + timestamp / 1000L + ":" + format.getStyle() + ">";
  }
}
