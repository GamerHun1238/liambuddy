package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Activity.Emoji;
import net.dv8tion.jda.api.entities.Activity.Timestamps;
import net.dv8tion.jda.api.entities.ActivityFlag;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.RichPresence.Image;
import net.dv8tion.jda.api.entities.RichPresence.Party;















public class RichPresenceImpl
  extends ActivityImpl
  implements RichPresence
{
  protected final long applicationId;
  protected final RichPresence.Party party;
  protected final String details;
  protected final String state;
  protected final RichPresence.Image largeImage;
  protected final RichPresence.Image smallImage;
  protected final String sessionId;
  protected final String syncId;
  protected final int flags;
  
  protected RichPresenceImpl(Activity.ActivityType type, String name, String url, long applicationId, Activity.Emoji emoji, RichPresence.Party party, String details, String state, Activity.Timestamps timestamps, String syncId, String sessionId, int flags, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText)
  {
    super(name, url, type, timestamps, emoji);
    this.applicationId = applicationId;
    this.party = party;
    this.details = details;
    this.state = state;
    this.sessionId = sessionId;
    this.syncId = syncId;
    this.flags = flags;
    largeImage = (largeImageKey != null ? new RichPresence.Image(applicationId, largeImageKey, largeImageText) : null);
    smallImage = (smallImageKey != null ? new RichPresence.Image(applicationId, smallImageKey, smallImageText) : null);
  }
  

  public boolean isRich()
  {
    return true;
  }
  

  public RichPresence asRichPresence()
  {
    return this;
  }
  

  public long getApplicationIdLong()
  {
    return applicationId;
  }
  

  @Nonnull
  public String getApplicationId()
  {
    return Long.toUnsignedString(applicationId);
  }
  

  @Nullable
  public String getSessionId()
  {
    return sessionId;
  }
  

  @Nullable
  public String getSyncId()
  {
    return syncId;
  }
  

  public int getFlags()
  {
    return flags;
  }
  

  public EnumSet<ActivityFlag> getFlagSet()
  {
    return ActivityFlag.getFlags(getFlags());
  }
  

  @Nullable
  public String getState()
  {
    return state;
  }
  

  @Nullable
  public String getDetails()
  {
    return details;
  }
  

  @Nullable
  public RichPresence.Party getParty()
  {
    return party;
  }
  

  @Nullable
  public RichPresence.Image getLargeImage()
  {
    return largeImage;
  }
  

  @Nullable
  public RichPresence.Image getSmallImage()
  {
    return smallImage;
  }
  

  public String toString()
  {
    return String.format("RichPresence(%s / %s)", new Object[] { name, getApplicationId() });
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { Long.valueOf(applicationId), state, details, party, sessionId, syncId, Integer.valueOf(flags), timestamps, largeImage, smallImage });
  }
  

  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof RichPresenceImpl))
      return false;
    RichPresenceImpl p = (RichPresenceImpl)o;
    return (applicationId == applicationId) && 
      (Objects.equals(name, name)) && 
      (Objects.equals(url, url)) && 
      (Objects.equals(type, type)) && 
      (Objects.equals(state, state)) && 
      (Objects.equals(details, details)) && 
      (Objects.equals(party, party)) && 
      (Objects.equals(sessionId, sessionId)) && 
      (Objects.equals(syncId, syncId)) && 
      (Objects.equals(Integer.valueOf(flags), Integer.valueOf(flags))) && 
      (Objects.equals(timestamps, timestamps)) && 
      (Objects.equals(largeImage, largeImage)) && 
      (Objects.equals(smallImage, smallImage));
  }
}
