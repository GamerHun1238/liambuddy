package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Role.RoleTags;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.RoleManagerImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Roles;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;














public class RoleImpl
  implements Role
{
  private final long id;
  private final JDAImpl api;
  private Guild guild;
  private RoleManager manager;
  private RoleTagsImpl tags;
  private String name;
  private boolean managed;
  private boolean hoisted;
  private boolean mentionable;
  private long rawPermissions;
  private int color;
  private int rawPosition;
  
  public RoleImpl(long id, Guild guild)
  {
    this.id = id;
    api = ((JDAImpl)guild.getJDA());
    this.guild = guild;
    tags = (api.isCacheFlagSet(CacheFlag.ROLE_TAGS) ? new RoleTagsImpl() : null);
  }
  

  public int getPosition()
  {
    Guild guild = getGuild();
    if (equals(guild.getPublicRole())) {
      return -1;
    }
    
    int i = guild.getRoles().size() - 2;
    for (Role r : guild.getRoles())
    {
      if (equals(r))
        return i;
      i--;
    }
    throw new IllegalStateException("Somehow when determining position we never found the role in the Guild's roles? wtf?");
  }
  

  public int getPositionRaw()
  {
    return rawPosition;
  }
  

  @Nonnull
  public String getName()
  {
    return name;
  }
  

  public boolean isManaged()
  {
    return managed;
  }
  

  public boolean isHoisted()
  {
    return hoisted;
  }
  

  public boolean isMentionable()
  {
    return mentionable;
  }
  

  public long getPermissionsRaw()
  {
    return rawPermissions;
  }
  

  @Nonnull
  public EnumSet<Permission> getPermissions()
  {
    return Permission.getPermissions(rawPermissions);
  }
  

  @Nonnull
  public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel)
  {
    return Permission.getPermissions(PermissionUtil.getEffectivePermission(channel, this));
  }
  

  @Nonnull
  public EnumSet<Permission> getPermissionsExplicit()
  {
    return getPermissions();
  }
  

  @Nonnull
  public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel)
  {
    return Permission.getPermissions(PermissionUtil.getExplicitPermission(channel, this));
  }
  

  public Color getColor()
  {
    return color != 536870911 ? new Color(color) : null;
  }
  

  public int getColorRaw()
  {
    return color;
  }
  

  public boolean isPublicRole()
  {
    return equals(getGuild().getPublicRole());
  }
  

  public boolean hasPermission(@Nonnull Permission... permissions)
  {
    long effectivePerms = rawPermissions | getGuild().getPublicRole().getPermissionsRaw();
    for (Permission perm : permissions)
    {
      long rawValue = perm.getRawValue();
      if ((effectivePerms & rawValue) != rawValue)
        return false;
    }
    return true;
  }
  

  public boolean hasPermission(@Nonnull Collection<Permission> permissions)
  {
    Checks.notNull(permissions, "Permission Collection");
    
    return hasPermission((Permission[])permissions.toArray(Permission.EMPTY_PERMISSIONS));
  }
  

  public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions)
  {
    long effectivePerms = PermissionUtil.getEffectivePermission(channel, this);
    for (Permission perm : permissions)
    {
      long rawValue = perm.getRawValue();
      if ((effectivePerms & rawValue) != rawValue)
        return false;
    }
    return true;
  }
  

  public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions)
  {
    Checks.notNull(permissions, "Permission Collection");
    
    return hasPermission(channel, (Permission[])permissions.toArray(Permission.EMPTY_PERMISSIONS));
  }
  

  public boolean canSync(@Nonnull GuildChannel targetChannel, @Nonnull GuildChannel syncSource)
  {
    Checks.notNull(targetChannel, "Channel");
    Checks.notNull(syncSource, "Channel");
    Checks.check(targetChannel.getGuild().equals(getGuild()), "Channels must be from the same guild!");
    Checks.check(syncSource.getGuild().equals(getGuild()), "Channels must be from the same guild!");
    long rolePerms = PermissionUtil.getEffectivePermission(targetChannel, this);
    if ((rolePerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
      return false;
    }
    long channelPermissions = PermissionUtil.getExplicitPermission(targetChannel, this, false);
    
    boolean hasLocalAdmin = (rolePerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
    if (hasLocalAdmin) {
      return true;
    }
    TLongObjectMap<PermissionOverride> existingOverrides = ((AbstractChannelImpl)targetChannel).getOverrideMap();
    for (PermissionOverride override : syncSource.getPermissionOverrides())
    {
      PermissionOverride existing = (PermissionOverride)existingOverrides.get(override.getIdLong());
      long allow = override.getAllowedRaw();
      long deny = override.getDeniedRaw();
      if (existing != null)
      {
        allow ^= existing.getAllowedRaw();
        deny ^= existing.getDeniedRaw();
      }
      
      if (((allow | deny) & (rolePerms ^ 0xFFFFFFFFFFFFFFFF)) != 0L)
        return false;
    }
    return true;
  }
  

  public boolean canSync(@Nonnull GuildChannel channel)
  {
    Checks.notNull(channel, "Channel");
    Checks.check(channel.getGuild().equals(getGuild()), "Channels must be from the same guild!");
    long rolePerms = PermissionUtil.getEffectivePermission(channel, this);
    if ((rolePerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
      return false;
    }
    long channelPermissions = PermissionUtil.getExplicitPermission(channel, this, false);
    
    return (rolePerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
  }
  

  public boolean canInteract(@Nonnull Role role)
  {
    return PermissionUtil.canInteract(this, role);
  }
  

  @Nonnull
  public Guild getGuild()
  {
    Guild realGuild = api.getGuildById(guild.getIdLong());
    if (realGuild != null)
      guild = realGuild;
    return guild;
  }
  

  @Nonnull
  public RoleAction createCopy(@Nonnull Guild guild)
  {
    Checks.notNull(guild, "Guild");
    return guild.createRole()
      .setColor(Integer.valueOf(color))
      .setHoisted(Boolean.valueOf(hoisted))
      .setMentionable(Boolean.valueOf(mentionable))
      .setName(name)
      .setPermissions(Long.valueOf(rawPermissions));
  }
  

  @Nonnull
  public RoleManager getManager()
  {
    if (manager == null)
      return this.manager = new RoleManagerImpl(this);
    return manager;
  }
  

  @Nonnull
  public AuditableRestAction<Void> delete()
  {
    Guild guild = getGuild();
    if (!guild.getSelfMember().hasPermission(new Permission[] { Permission.MANAGE_ROLES }))
      throw new InsufficientPermissionException(guild, Permission.MANAGE_ROLES);
    if (!PermissionUtil.canInteract(guild.getSelfMember(), this))
      throw new HierarchyException("Can't delete role >= highest self-role");
    if (managed) {
      throw new UnsupportedOperationException("Cannot delete a Role that is managed. ");
    }
    Route.CompiledRoute route = Route.Roles.DELETE_ROLE.compile(new String[] { guild.getId(), getId() });
    return new AuditableRestActionImpl(getJDA(), route);
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  @Nonnull
  public Role.RoleTags getTags()
  {
    return tags == null ? RoleTagsImpl.EMPTY : tags;
  }
  

  @Nonnull
  public String getAsMention()
  {
    return "<@&" + getId() + '>';
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof Role))
      return false;
    Role oRole = (Role)o;
    return getIdLong() == oRole.getIdLong();
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public String toString()
  {
    return "R:" + getName() + '(' + id + ')';
  }
  

  public int compareTo(@Nonnull Role r)
  {
    if (this == r)
      return 0;
    if (!(r instanceof RoleImpl))
      throw new IllegalArgumentException("Cannot compare different role implementations");
    RoleImpl impl = (RoleImpl)r;
    
    if (guild.getIdLong() != guild.getIdLong()) {
      throw new IllegalArgumentException("Cannot compare roles that aren't from the same guild!");
    }
    if (getPositionRaw() != r.getPositionRaw()) {
      return getPositionRaw() - r.getPositionRaw();
    }
    OffsetDateTime thisTime = getTimeCreated();
    OffsetDateTime rTime = r.getTimeCreated();
    



    return rTime.compareTo(thisTime);
  }
  


  public RoleImpl setName(String name)
  {
    this.name = name;
    return this;
  }
  
  public RoleImpl setColor(int color)
  {
    this.color = color;
    return this;
  }
  
  public RoleImpl setManaged(boolean managed)
  {
    this.managed = managed;
    return this;
  }
  
  public RoleImpl setHoisted(boolean hoisted)
  {
    this.hoisted = hoisted;
    return this;
  }
  
  public RoleImpl setMentionable(boolean mentionable)
  {
    this.mentionable = mentionable;
    return this;
  }
  
  public RoleImpl setRawPermissions(long rawPermissions)
  {
    this.rawPermissions = rawPermissions;
    return this;
  }
  
  public RoleImpl setRawPosition(int rawPosition)
  {
    SortedSnowflakeCacheViewImpl<Role> roleCache = (SortedSnowflakeCacheViewImpl)getGuild().getRoleCache();
    roleCache.clearCachedLists();
    this.rawPosition = rawPosition;
    return this;
  }
  
  public RoleImpl setTags(DataObject tags)
  {
    if (this.tags == null)
      return this;
    this.tags = new RoleTagsImpl(tags);
    return this;
  }
  
  public static class RoleTagsImpl implements Role.RoleTags
  {
    public static final Role.RoleTags EMPTY = new RoleTagsImpl();
    private final long botId;
    private final long integrationId;
    private final boolean premiumSubscriber;
    
    public RoleTagsImpl()
    {
      botId = 0L;
      integrationId = 0L;
      premiumSubscriber = false;
    }
    
    public RoleTagsImpl(DataObject tags)
    {
      botId = (tags.hasKey("bot_id") ? tags.getUnsignedLong("bot_id") : 0L);
      integrationId = (tags.hasKey("integration_id") ? tags.getUnsignedLong("integration_id") : 0L);
      premiumSubscriber = tags.hasKey("premium_subscriber");
    }
    

    public boolean isBot()
    {
      return botId != 0L;
    }
    

    public long getBotIdLong()
    {
      return botId;
    }
    

    public boolean isBoost()
    {
      return premiumSubscriber;
    }
    

    public boolean isIntegration()
    {
      return integrationId != 0L;
    }
    

    public long getIntegrationIdLong()
    {
      return integrationId;
    }
    

    public int hashCode()
    {
      return Objects.hash(new Object[] { Long.valueOf(botId), Long.valueOf(integrationId), Boolean.valueOf(premiumSubscriber) });
    }
    

    public boolean equals(Object obj)
    {
      if (obj == this)
        return true;
      if (!(obj instanceof RoleTagsImpl))
        return false;
      RoleTagsImpl other = (RoleTagsImpl)obj;
      return (botId == botId) && (integrationId == integrationId) && (premiumSubscriber == premiumSubscriber);
    }
    



    public String toString()
    {
      return "RoleTags(bot=" + getBotId() + ",integration=" + getIntegrationId() + ",boost=" + isBoost() + ")";
    }
  }
}
