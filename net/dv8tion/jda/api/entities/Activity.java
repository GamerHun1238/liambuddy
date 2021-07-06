package net.dv8tion.jda.api.entities;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import net.dv8tion.jda.internal.utils.Helpers;
































public abstract interface Activity
{
  public static final Pattern STREAMING_URL = Pattern.compile("https?://(www\\.)?(twitch\\.tv/|youtube\\.com/watch\\?v=).+", 2);
  







  public abstract boolean isRich();
  







  @Nullable
  public abstract RichPresence asRichPresence();
  







  @Nonnull
  public abstract String getName();
  







  @Nullable
  public abstract String getUrl();
  






  @Nonnull
  public abstract ActivityType getType();
  






  @Nullable
  public abstract Timestamps getTimestamps();
  






  @Nullable
  public abstract Emoji getEmoji();
  






  @Nonnull
  public static Activity playing(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notLonger(name, 128, "Name");
    return EntityBuilder.createActivity(name, null, ActivityType.DEFAULT);
  }
  

















  @Nonnull
  public static Activity streaming(@Nonnull String name, @Nullable String url)
  {
    Checks.notEmpty(name, "Provided game name");
    name = Helpers.isBlank(name) ? name : name.trim();
    Checks.notLonger(name, 128, "Name");
    ActivityType type;
    ActivityType type; if (isValidStreamingUrl(url)) {
      type = ActivityType.STREAMING;
    } else
      type = ActivityType.DEFAULT;
    return EntityBuilder.createActivity(name, url, type);
  }
  












  @Nonnull
  public static Activity listening(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notLonger(name, 128, "Name");
    return EntityBuilder.createActivity(name, null, ActivityType.LISTENING);
  }
  














  @Nonnull
  @Incubating
  public static Activity watching(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notLonger(name, 128, "Name");
    return EntityBuilder.createActivity(name, null, ActivityType.WATCHING);
  }
  














  @Nonnull
  public static Activity competing(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notLonger(name, 128, "Name");
    return EntityBuilder.createActivity(name, null, ActivityType.COMPETING);
  }
  
















  @Nonnull
  public static Activity of(@Nonnull ActivityType type, @Nonnull String name)
  {
    return of(type, name, null);
  }
  






















  @Nonnull
  public static Activity of(@Nonnull ActivityType type, @Nonnull String name, @Nullable String url)
  {
    Checks.notNull(type, "Type");
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$Activity$ActivityType[type.ordinal()])
    {
    case 1: 
      return playing(name);
    case 2: 
      return streaming(name, url);
    case 3: 
      return listening(name);
    case 4: 
      return watching(name);
    case 5: 
      return competing(name);
    }
    throw new IllegalArgumentException("ActivityType " + type + " is not supported!");
  }
  









  public static boolean isValidStreamingUrl(@Nullable String url)
  {
    return (url != null) && (STREAMING_URL.matcher(url).matches());
  }
  






  public static enum ActivityType
  {
    DEFAULT(0), 
    



    STREAMING(1), 
    



    LISTENING(2), 
    





    WATCHING(3), 
    






    CUSTOM_STATUS(4), 
    







    COMPETING(5);
    
    private final int key;
    
    private ActivityType(int key)
    {
      this.key = key;
    }
    





    public int getKey()
    {
      return key;
    }
    









    @Nonnull
    public static ActivityType fromKey(int key)
    {
      switch (key)
      {
      case 0: 
      default: 
        return DEFAULT;
      case 1: 
        return STREAMING;
      case 2: 
        return LISTENING;
      case 3: 
        return WATCHING;
      case 4: 
        return CUSTOM_STATUS;
      }
      return COMPETING;
    }
  }
  


  public static class Timestamps
  {
    protected final long start;
    

    protected final long end;
    

    public Timestamps(long start, long end)
    {
      this.start = start;
      this.end = end;
    }
    





    public long getStart()
    {
      return start;
    }
    





    @Nullable
    public Instant getStartTime()
    {
      return start <= 0L ? null : Instant.ofEpochMilli(start);
    }
    





    public long getEnd()
    {
      return end;
    }
    





    @Nullable
    public Instant getEndTime()
    {
      return end <= 0L ? null : Instant.ofEpochMilli(end);
    }
    





















    public long getRemainingTime(TemporalUnit unit)
    {
      Checks.notNull(unit, "TemporalUnit");
      Instant end = getEndTime();
      return end != null ? Instant.now().until(end, unit) : -1L;
    }
    





















    public long getElapsedTime(TemporalUnit unit)
    {
      Checks.notNull(unit, "TemporalUnit");
      Instant start = getStartTime();
      return start != null ? start.until(Instant.now(), unit) : -1L;
    }
    

    public String toString()
    {
      return Helpers.format("RichPresenceTimestamp(%d-%d)", new Object[] { Long.valueOf(start), Long.valueOf(end) });
    }
    

    public boolean equals(Object obj)
    {
      if (!(obj instanceof Timestamps))
        return false;
      Timestamps t = (Timestamps)obj;
      return (start == start) && (end == end);
    }
    

    public int hashCode()
    {
      return Objects.hash(new Object[] { Long.valueOf(start), Long.valueOf(end) });
    }
  }
  

  public static class Emoji
    implements ISnowflake, IMentionable
  {
    private final String name;
    
    private final long id;
    
    private final boolean animated;
    
    public Emoji(String name, long id, boolean animated)
    {
      this.name = name;
      this.id = id;
      this.animated = animated;
    }
    
    public Emoji(String name)
    {
      this(name, 0L, false);
    }
    








    @Nonnull
    public String getName()
    {
      return name;
    }
    











    @Nonnull
    public String getAsCodepoints()
    {
      if (!isEmoji())
        throw new IllegalStateException("Cannot convert custom emote to codepoints");
      return EncodingUtil.encodeCodepoints(name);
    }
    









    public long getIdLong()
    {
      if (!isEmote())
        throw new IllegalStateException("Cannot get id for unicode emoji");
      return id;
    }
    






    public boolean isAnimated()
    {
      return animated;
    }
    





    public boolean isEmoji()
    {
      return id == 0L;
    }
    





    public boolean isEmote()
    {
      return id != 0L;
    }
    

    @Nonnull
    public String getAsMention()
    {
      if (isEmoji()) {
        return name;
      }
      return String.format("<%s:%s:%s>", new Object[] { isAnimated() ? "a" : "", name, getId() });
    }
    

    public int hashCode()
    {
      return id == 0L ? name.hashCode() : Long.hashCode(id);
    }
    

    public boolean equals(Object obj)
    {
      if (obj == this)
        return true;
      if (!(obj instanceof Emoji))
        return false;
      Emoji other = (Emoji)obj;
      return 
        id == id ? true : id == 0L ? name.equals(name) : false;
    }
    

    public String toString()
    {
      if (isEmoji())
        return "ActivityEmoji(" + getAsCodepoints() + ')';
      return "ActivityEmoji(" + Long.toUnsignedString(id) + " / " + name + ')';
    }
  }
}
