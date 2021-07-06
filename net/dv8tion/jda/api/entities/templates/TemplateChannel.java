package net.dv8tion.jda.api.entities.templates;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.ISnowflake;




























public class TemplateChannel
  implements ISnowflake
{
  private final long id;
  private final ChannelType channelType;
  private final String name;
  private final String topic;
  private final int rawPosition;
  private final long parentId;
  private final boolean isNews;
  private final List<PermissionOverride> permissionOverrides;
  private final boolean nsfw;
  private final int slowmode;
  private final int bitrate;
  private final int userLimit;
  
  public TemplateChannel(long id, ChannelType channelType, String name, String topic, int rawPosition, long parentId, boolean news, List<PermissionOverride> permissionOverrides, boolean nsfw, int slowmode, int bitrate, int userLimit)
  {
    this.id = id;
    this.channelType = channelType;
    this.name = name;
    this.topic = topic;
    this.rawPosition = rawPosition;
    this.parentId = parentId;
    isNews = news;
    this.permissionOverrides = Collections.unmodifiableList(permissionOverrides);
    
    this.nsfw = nsfw;
    this.slowmode = slowmode;
    
    this.bitrate = bitrate;
    this.userLimit = userLimit;
  }
  






  public long getIdLong()
  {
    return id;
  }
  







  public OffsetDateTime getTimeCreated()
  {
    throw new UnsupportedOperationException("The date of creation cannot be calculated");
  }
  





  @Nonnull
  public ChannelType getType()
  {
    return channelType;
  }
  






  @Nonnull
  public String getName()
  {
    return name;
  }
  







  @Nullable
  public String getTopic()
  {
    return topic;
  }
  








  public int getPositionRaw()
  {
    return rawPosition;
  }
  







  public long getParentId()
  {
    return parentId;
  }
  







  public boolean isNSFW()
  {
    return nsfw;
  }
  












  public int getSlowmode()
  {
    return slowmode;
  }
  








  public int getBitrate()
  {
    return bitrate;
  }
  







  public int getUserLimit()
  {
    return userLimit;
  }
  






  public boolean isNews()
  {
    return isNews;
  }
  








  @Nonnull
  public List<PermissionOverride> getPermissionOverrides()
  {
    return permissionOverrides;
  }
  


  public static class PermissionOverride
    implements ISnowflake
  {
    private final long id;
    
    private final long allow;
    
    private final long deny;
    

    public PermissionOverride(long id, long allow, long deny)
    {
      this.id = id;
      this.allow = allow;
      this.deny = deny;
    }
    






    public long getAllowedRaw()
    {
      return allow;
    }
    






    public long getInheritRaw()
    {
      return (allow | deny) ^ 0xFFFFFFFFFFFFFFFF;
    }
    






    public long getDeniedRaw()
    {
      return deny;
    }
    






    @Nonnull
    public EnumSet<Permission> getAllowed()
    {
      return Permission.getPermissions(allow);
    }
    






    @Nonnull
    public EnumSet<Permission> getInherit()
    {
      return Permission.getPermissions(getInheritRaw());
    }
    






    @Nonnull
    public EnumSet<Permission> getDenied()
    {
      return Permission.getPermissions(deny);
    }
    






    public long getIdLong()
    {
      return id;
    }
    







    public OffsetDateTime getTimeCreated()
    {
      throw new UnsupportedOperationException("The date of creation cannot be calculated");
    }
  }
}
