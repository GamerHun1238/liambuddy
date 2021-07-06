package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.Helpers;





























































































public abstract interface RichPresence
  extends Activity
{
  public abstract long getApplicationIdLong();
  
  @Nonnull
  public abstract String getApplicationId();
  
  @Nullable
  public abstract String getSessionId();
  
  @Nullable
  public abstract String getSyncId();
  
  public abstract int getFlags();
  
  public abstract EnumSet<ActivityFlag> getFlagSet();
  
  @Nullable
  public abstract String getState();
  
  @Nullable
  public abstract String getDetails();
  
  @Nullable
  public abstract Party getParty();
  
  @Nullable
  public abstract Image getLargeImage();
  
  @Nullable
  public abstract Image getSmallImage();
  
  public static class Image
  {
    protected final String key;
    protected final String text;
    protected final String applicationId;
    
    public Image(long applicationId, String key, String text)
    {
      this.applicationId = Long.toUnsignedString(applicationId);
      this.key = key;
      this.text = text;
    }
    





    @Nonnull
    public String getKey()
    {
      return key;
    }
    





    @Nullable
    public String getText()
    {
      return text;
    }
    





    @Nonnull
    public String getUrl()
    {
      if (key.startsWith("spotify:"))
        return "https://i.scdn.co/image/" + key.substring("spotify:".length());
      if (key.startsWith("twitch:"))
        return String.format("https://static-cdn.jtvnw.net/previews-ttv/live_user_%s-1920x1080.png", new Object[] { key.substring("twitch:".length()) });
      return "https://cdn.discordapp.com/app-assets/" + applicationId + "/" + key + ".png";
    }
    

    public String toString()
    {
      return String.format("RichPresenceImage(%s | %s)", new Object[] { key, text });
    }
    

    public boolean equals(Object obj)
    {
      if (!(obj instanceof Image))
        return false;
      Image i = (Image)obj;
      return (Objects.equals(key, key)) && (Objects.equals(text, text));
    }
    

    public int hashCode()
    {
      return Objects.hash(new Object[] { key, text });
    }
  }
  

  public static class Party
  {
    protected final String id;
    
    protected final long size;
    
    protected final long max;
    
    public Party(String id, long size, long max)
    {
      this.id = id;
      this.size = size;
      this.max = max;
    }
    





    @Nullable
    public String getId()
    {
      return id;
    }
    





    public long getSize()
    {
      return size;
    }
    





    public long getMax()
    {
      return max;
    }
    

    public String toString()
    {
      return Helpers.format("RichPresenceParty(%s | [%d, %d])", new Object[] { id, Long.valueOf(size), Long.valueOf(max) });
    }
    

    public boolean equals(Object obj)
    {
      if (!(obj instanceof Party))
        return false;
      Party p = (Party)obj;
      return (size == size) && (max == max) && (Objects.equals(id, id));
    }
    

    public int hashCode()
    {
      return Objects.hash(new Object[] { id, Long.valueOf(size), Long.valueOf(max) });
    }
  }
}
