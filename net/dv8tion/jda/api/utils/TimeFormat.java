package net.dv8tion.jda.api.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;
































public enum TimeFormat
{
  TIME_SHORT("t"), 
  
  TIME_LONG("T"), 
  
  DATE_SHORT("d"), 
  
  DATE_LONG("D"), 
  
  DATE_TIME_SHORT("f"), 
  
  DATE_TIME_LONG("F"), 
  
  RELATIVE("R");
  




  public static final TimeFormat DEFAULT = DATE_TIME_SHORT;
  






























  public static final Pattern MARKDOWN = Pattern.compile("<t:(?<time>-?\\d{1,17})(?::(?<style>[tTdDfFR]))?>");
  
  private final String style;
  
  private TimeFormat(String style)
  {
    this.style = style;
  }
  






  @Nonnull
  public String getStyle()
  {
    return style;
  }
  











  @Nonnull
  public static TimeFormat fromStyle(@Nonnull String style)
  {
    Checks.notEmpty(style, "Style");
    Checks.notLonger(style, 1, "Style");
    for (TimeFormat format : values())
    {
      if (style.equals(style))
        return format;
    }
    return DEFAULT;
  }
  












  @Nonnull
  public static Timestamp parse(@Nonnull String markdown)
  {
    Checks.notNull(markdown, "Markdown");
    Matcher matcher = MARKDOWN.matcher(markdown.trim());
    if (!matcher.find())
      throw new IllegalArgumentException("Invalid markdown format! Provided: " + markdown);
    String format = matcher.group("style");
    return new Timestamp(format == null ? DEFAULT : fromStyle(format), Long.parseLong(matcher.group("time")) * 1000L);
  }
  















  @Nonnull
  public String format(@Nonnull TemporalAccessor temporal)
  {
    Checks.notNull(temporal, "Temporal");
    long timestamp = Instant.from(temporal).toEpochMilli();
    return format(timestamp);
  }
  









  @Nonnull
  public String format(long timestamp)
  {
    return "<t:" + timestamp / 1000L + ":" + style + ">";
  }
  
















  @Nonnull
  public Timestamp atInstant(@Nonnull Instant instant)
  {
    Checks.notNull(instant, "Instant");
    return new Timestamp(this, instant.toEpochMilli());
  }
  











  @Nonnull
  public Timestamp atTimestamp(long timestamp)
  {
    return new Timestamp(this, timestamp);
  }
  








  @Nonnull
  public Timestamp now()
  {
    return new Timestamp(this, System.currentTimeMillis());
  }
  














  @Nonnull
  public Timestamp after(@Nonnull Duration duration)
  {
    return now().plus(duration);
  }
  











  @Nonnull
  public Timestamp after(long millis)
  {
    return now().plus(millis);
  }
  














  @Nonnull
  public Timestamp before(@Nonnull Duration duration)
  {
    return now().minus(duration);
  }
  











  @Nonnull
  public Timestamp before(long millis)
  {
    return now().minus(millis);
  }
}
