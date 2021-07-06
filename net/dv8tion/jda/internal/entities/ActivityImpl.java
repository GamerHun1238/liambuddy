package net.dv8tion.jda.internal.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Activity.Emoji;
import net.dv8tion.jda.api.entities.Activity.Timestamps;
import net.dv8tion.jda.api.entities.RichPresence;













public class ActivityImpl
  implements Activity
{
  protected final String name;
  protected final String url;
  protected final Activity.ActivityType type;
  protected final Activity.Timestamps timestamps;
  protected final Activity.Emoji emoji;
  
  protected ActivityImpl(String name)
  {
    this(name, null, Activity.ActivityType.DEFAULT);
  }
  
  protected ActivityImpl(String name, String url)
  {
    this(name, url, Activity.ActivityType.STREAMING);
  }
  
  protected ActivityImpl(String name, String url, Activity.ActivityType type)
  {
    this(name, url, type, null, null);
  }
  
  protected ActivityImpl(String name, String url, Activity.ActivityType type, Activity.Timestamps timestamps, Activity.Emoji emoji)
  {
    this.name = name;
    this.url = url;
    this.type = type;
    this.timestamps = timestamps;
    this.emoji = emoji;
  }
  

  public boolean isRich()
  {
    return false;
  }
  

  public RichPresence asRichPresence()
  {
    return null;
  }
  

  @Nonnull
  public String getName()
  {
    return name;
  }
  

  public String getUrl()
  {
    return url;
  }
  

  @Nonnull
  public Activity.ActivityType getType()
  {
    return type;
  }
  
  @Nullable
  public Activity.Timestamps getTimestamps()
  {
    return timestamps;
  }
  

  @Nullable
  public Activity.Emoji getEmoji()
  {
    return emoji;
  }
  

  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ActivityImpl)) {
      return false;
    }
    ActivityImpl oGame = (ActivityImpl)o;
    return (oGame.getType() == type) && 
      (Objects.equals(name, oGame.getName())) && 
      (Objects.equals(url, oGame.getUrl())) && 
      (Objects.equals(timestamps, timestamps));
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { name, type, url, timestamps });
  }
  

  public String toString()
  {
    if (url != null) {
      return String.format("Activity(%s | %s)", new Object[] { name, url });
    }
    return String.format("Activity(%s)", new Object[] { name });
  }
}
