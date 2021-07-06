package net.dv8tion.jda.api.requests.restaction;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.requests.restaction.PermOverrideData;
import net.dv8tion.jda.internal.utils.Checks;































































































































































































public abstract interface GuildAction
  extends RestAction<Void>
{
  @Nonnull
  public abstract GuildAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract GuildAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract GuildAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  @Deprecated
  @ReplaceWith("ChannelManager.setRegion()")
  @DeprecatedSince("4.3.0")
  public abstract GuildAction setRegion(@Nullable Region paramRegion);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction setIcon(@Nullable Icon paramIcon);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction setVerificationLevel(@Nullable Guild.VerificationLevel paramVerificationLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction setNotificationLevel(@Nullable Guild.NotificationLevel paramNotificationLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction setExplicitContentLevel(@Nullable Guild.ExplicitContentLevel paramExplicitContentLevel);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction addChannel(@Nonnull ChannelData paramChannelData);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelData getChannel(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelData removeChannel(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract GuildAction removeChannel(@Nonnull ChannelData paramChannelData);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelData newChannel(@Nonnull ChannelType paramChannelType, @Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleData getPublicRole();
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleData getRole(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleData newRole();
  
  public static class RoleData
    implements SerializableData
  {
    protected final long id;
    protected final boolean isPublicRole;
    protected Long permissions;
    protected String name;
    protected Integer color;
    protected Integer position;
    protected Boolean mentionable;
    protected Boolean hoisted;
    
    public RoleData(long id)
    {
      this.id = id;
      isPublicRole = (id == 0L);
    }
    








    @Nonnull
    public RoleData setPermissionsRaw(@Nullable Long rawPermissions)
    {
      permissions = rawPermissions;
      return this;
    }
    











    @Nonnull
    public RoleData addPermissions(@Nonnull Permission... permissions)
    {
      Checks.notNull(permissions, "Permissions");
      for (Permission perm : permissions)
        Checks.notNull(perm, "Permissions");
      if (this.permissions == null)
        this.permissions = Long.valueOf(0L);
      this.permissions = Long.valueOf(this.permissions.longValue() | Permission.getRaw(permissions));
      return this;
    }
    











    @Nonnull
    public RoleData addPermissions(@Nonnull Collection<Permission> permissions)
    {
      Checks.noneNull(permissions, "Permissions");
      if (this.permissions == null)
        this.permissions = Long.valueOf(0L);
      this.permissions = Long.valueOf(this.permissions.longValue() | Permission.getRaw(permissions));
      return this;
    }
    











    @Nonnull
    public RoleData setName(@Nullable String name)
    {
      checkPublic("name");
      this.name = name;
      return this;
    }
    











    @Nonnull
    public RoleData setColor(@Nullable Color color)
    {
      checkPublic("color");
      this.color = (color == null ? null : Integer.valueOf(color.getRGB()));
      return this;
    }
    











    @Nonnull
    public RoleData setColor(@Nullable Integer color)
    {
      checkPublic("color");
      this.color = color;
      return this;
    }
    











    @Nonnull
    public RoleData setPosition(@Nullable Integer position)
    {
      checkPublic("position");
      this.position = position;
      return this;
    }
    











    @Nonnull
    public RoleData setMentionable(@Nullable Boolean mentionable)
    {
      checkPublic("mentionable");
      this.mentionable = mentionable;
      return this;
    }
    











    @Nonnull
    public RoleData setHoisted(@Nullable Boolean hoisted)
    {
      checkPublic("hoisted");
      this.hoisted = hoisted;
      return this;
    }
    

    @Nonnull
    public DataObject toData()
    {
      DataObject o = DataObject.empty().put("id", Long.toUnsignedString(id));
      if (permissions != null)
        o.put("permissions", permissions);
      if (position != null)
        o.put("position", position);
      if (name != null)
        o.put("name", name);
      if (color != null)
        o.put("color", Integer.valueOf(color.intValue() & 0xFFFFFF));
      if (mentionable != null)
        o.put("mentionable", mentionable);
      if (hoisted != null)
        o.put("hoist", hoisted);
      return o;
    }
    
    protected void checkPublic(String comment)
    {
      if (isPublicRole) {
        throw new IllegalStateException("Cannot modify " + comment + " for the public role!");
      }
    }
  }
  


  public static class ChannelData
    implements SerializableData
  {
    protected final ChannelType type;
    

    protected final String name;
    
    protected final Set<PermOverrideData> overrides = new HashSet();
    



    protected Integer position;
    



    protected String topic;
    



    protected Boolean nsfw;
    



    protected Integer bitrate;
    


    protected Integer userlimit;
    



    public ChannelData(ChannelType type, String name)
    {
      Checks.notBlank(name, "Name");
      Checks.check((type == ChannelType.TEXT) || (type == ChannelType.VOICE) || (type == ChannelType.STAGE), "Can only create channels of type TEXT, STAGE, or VOICE in GuildAction!");
      
      Checks.check((name.length() >= 2) && (name.length() <= 100), "Channel name has to be between 2-100 characters long!");
      
      Checks.check((type == ChannelType.VOICE) || (type == ChannelType.STAGE) || (name.matches("[a-zA-Z0-9-_]+")), "Channels of type TEXT must have a name in alphanumeric with underscores!");
      

      this.type = type;
      this.name = name;
    }
    












    @Nonnull
    public ChannelData setTopic(@Nullable String topic)
    {
      if ((topic != null) && (topic.length() > 1024))
        throw new IllegalArgumentException("Channel Topic must not be greater than 1024 in length!");
      this.topic = topic;
      return this;
    }
    









    @Nonnull
    public ChannelData setNSFW(@Nullable Boolean nsfw)
    {
      this.nsfw = nsfw;
      return this;
    }
    












    @Nonnull
    public ChannelData setBitrate(@Nullable Integer bitrate)
    {
      if (bitrate != null)
      {
        Checks.check(bitrate.intValue() >= 8000, "Bitrate must be greater than 8000.");
        Checks.check(bitrate.intValue() <= 96000, "Bitrate must be less than 96000.");
      }
      this.bitrate = bitrate;
      return this;
    }
    












    @Nonnull
    public ChannelData setUserlimit(@Nullable Integer userlimit)
    {
      if ((userlimit != null) && ((userlimit.intValue() < 0) || (userlimit.intValue() > 99)))
        throw new IllegalArgumentException("Userlimit must be between 0-99!");
      this.userlimit = userlimit;
      return this;
    }
    








    @Nonnull
    public ChannelData setPosition(@Nullable Integer position)
    {
      this.position = position;
      return this;
    }
    

















    @Nonnull
    public ChannelData addPermissionOverride(@Nonnull GuildAction.RoleData role, long allow, long deny)
    {
      Checks.notNull(role, "Role");
      overrides.add(new PermOverrideData(0, id, allow, deny));
      return this;
    }
    




















    @Nonnull
    public ChannelData addPermissionOverride(@Nonnull GuildAction.RoleData role, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
    {
      long allowRaw = 0L;
      long denyRaw = 0L;
      if (allow != null)
      {
        Checks.noneNull(allow, "Granted Permissions");
        allowRaw = Permission.getRaw(allow);
      }
      if (deny != null)
      {
        Checks.noneNull(deny, "Denied Permissions");
        denyRaw = Permission.getRaw(deny);
      }
      return addPermissionOverride(role, allowRaw, denyRaw);
    }
    

    @Nonnull
    public DataObject toData()
    {
      DataObject o = DataObject.empty();
      o.put("name", name);
      o.put("type", Integer.valueOf(type.getId()));
      if (topic != null)
        o.put("topic", topic);
      if (nsfw != null)
        o.put("nsfw", nsfw);
      if (bitrate != null)
        o.put("bitrate", bitrate);
      if (userlimit != null)
        o.put("user_limit", userlimit);
      if (position != null)
        o.put("position", position);
      if (!overrides.isEmpty())
        o.put("permission_overwrites", overrides);
      return o;
    }
  }
}
