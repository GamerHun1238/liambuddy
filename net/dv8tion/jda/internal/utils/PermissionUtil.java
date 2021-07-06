package net.dv8tion.jda.internal.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.apache.commons.collections4.CollectionUtils;





















public class PermissionUtil
{
  public PermissionUtil() {}
  
  public static boolean canInteract(Member issuer, Member target)
  {
    Checks.notNull(issuer, "Issuer Member");
    Checks.notNull(target, "Target Member");
    
    Guild guild = issuer.getGuild();
    if (!guild.equals(target.getGuild()))
      throw new IllegalArgumentException("Provided members must both be Member objects of the same Guild!");
    if (issuer.isOwner())
      return true;
    if (target.isOwner())
      return false;
    List<Role> issuerRoles = issuer.getRoles();
    List<Role> targetRoles = target.getRoles();
    return (!issuerRoles.isEmpty()) && ((targetRoles.isEmpty()) || (canInteract((Role)issuerRoles.get(0), (Role)targetRoles.get(0))));
  }
  















  public static boolean canInteract(Member issuer, Role target)
  {
    Checks.notNull(issuer, "Issuer Member");
    Checks.notNull(target, "Target Role");
    
    Guild guild = issuer.getGuild();
    if (!guild.equals(target.getGuild()))
      throw new IllegalArgumentException("Provided Member issuer and Role target must be from the same Guild!");
    if (issuer.isOwner())
      return true;
    List<Role> issuerRoles = issuer.getRoles();
    return (!issuerRoles.isEmpty()) && (canInteract((Role)issuerRoles.get(0), target));
  }
  















  public static boolean canInteract(Role issuer, Role target)
  {
    Checks.notNull(issuer, "Issuer Role");
    Checks.notNull(target, "Target Role");
    
    if (!issuer.getGuild().equals(target.getGuild()))
      throw new IllegalArgumentException("The 2 Roles are not from same Guild!");
    return target.getPosition() < issuer.getPosition();
  }
  

























  public static boolean canInteract(Member issuer, Emote emote)
  {
    Checks.notNull(issuer, "Issuer Member");
    Checks.notNull(emote, "Target Emote");
    
    if (!issuer.getGuild().equals(emote.getGuild())) {
      throw new IllegalArgumentException("The issuer and target are not in the same Guild");
    }
    return (emote.canProvideRoles()) && ((emote.getRoles().isEmpty()) || 
      (CollectionUtils.containsAny(issuer.getRoles(), emote.getRoles())));
  }
  



















  public static boolean canInteract(User issuer, Emote emote, MessageChannel channel, boolean botOverride)
  {
    Checks.notNull(issuer, "Issuer Member");
    Checks.notNull(emote, "Target Emote");
    Checks.notNull(channel, "Target Channel");
    
    if ((emote.getGuild() == null) || (!emote.getGuild().isMember(issuer)))
      return false;
    Member member = emote.getGuild().getMemberById(issuer.getIdLong());
    if (!canInteract(member, emote)) {
      return false;
    }
    
    boolean external = (emote.isManaged()) || ((issuer.isBot()) && (botOverride));
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$ChannelType[channel.getType().ordinal()])
    {
    case 1: 
      TextChannel text = (TextChannel)channel;
      member = text.getGuild().getMemberById(issuer.getIdLong());
      if (!emote.getGuild().equals(text.getGuild())) if ((!external) || (member == null)) break label217; label217: return member
        .hasPermission(text, new Permission[] { Permission.MESSAGE_EXT_EMOJI });
    }
    return external;
  }
  


















  public static boolean canInteract(User issuer, Emote emote, MessageChannel channel)
  {
    return canInteract(issuer, emote, channel, true);
  }
  




















  public static boolean checkPermission(Member member, Permission... permissions)
  {
    Checks.notNull(member, "Member");
    Checks.notNull(permissions, "Permissions");
    
    long effectivePerms = getEffectivePermission(member);
    return (isApplied(effectivePerms, Permission.ADMINISTRATOR.getRawValue())) || 
      (isApplied(effectivePerms, Permission.getRaw(permissions)));
  }
  
























  public static boolean checkPermission(GuildChannel channel, Member member, Permission... permissions)
  {
    Checks.notNull(channel, "Channel");
    Checks.notNull(member, "Member");
    Checks.notNull(permissions, "Permissions");
    
    GuildImpl guild = (GuildImpl)channel.getGuild();
    checkGuild(guild, member.getGuild(), "Member");
    
    long effectivePerms = getEffectivePermission(channel, member);
    return isApplied(effectivePerms, Permission.getRaw(permissions));
  }
  


















  public static long getEffectivePermission(Member member)
  {
    Checks.notNull(member, "Member");
    
    if (member.isOwner()) {
      return Permission.ALL_PERMISSIONS;
    }
    long permission = member.getGuild().getPublicRole().getPermissionsRaw();
    for (Role role : member.getRoles())
    {
      permission |= role.getPermissionsRaw();
      if (isApplied(permission, Permission.ADMINISTRATOR.getRawValue())) {
        return Permission.ALL_PERMISSIONS;
      }
    }
    return permission;
  }
  



















  public static long getEffectivePermission(GuildChannel channel, Member member)
  {
    Checks.notNull(channel, "Channel");
    Checks.notNull(member, "Member");
    
    Checks.check(channel.getGuild().equals(member.getGuild()), "Provided channel and provided member are not of the same guild!");
    
    if (member.isOwner())
    {

      return Permission.ALL_PERMISSIONS;
    }
    
    long permission = getEffectivePermission(member);
    long admin = Permission.ADMINISTRATOR.getRawValue();
    if (isApplied(permission, admin)) {
      return Permission.ALL_PERMISSIONS;
    }
    if (channel.getParent() != null) { if (checkPermission(channel.getParent(), member, new Permission[] { Permission.MANAGE_CHANNEL })) {
        permission |= Permission.MANAGE_CHANNEL.getRawValue();
      }
    }
    AtomicLong allow = new AtomicLong(0L);
    AtomicLong deny = new AtomicLong(0L);
    getExplicitOverrides(channel, member, allow, deny);
    permission = apply(permission, allow.get(), deny.get());
    long viewChannel = Permission.VIEW_CHANNEL.getRawValue();
    long connectChannel = Permission.VOICE_CONNECT.getRawValue();
    


    boolean hasConnect = ((channel.getType() != ChannelType.VOICE) && (channel.getType() != ChannelType.STAGE)) || (isApplied(permission, connectChannel));
    boolean hasView = isApplied(permission, viewChannel);
    return (hasView) && (hasConnect) ? permission : 0L;
  }
  


















  public static long getEffectivePermission(GuildChannel channel, Role role)
  {
    Checks.notNull(channel, "Channel");
    Checks.notNull(role, "Role");
    
    Guild guild = channel.getGuild();
    if (!guild.equals(role.getGuild())) {
      throw new IllegalArgumentException("Provided channel and role are not of the same guild!");
    }
    long permissions = getExplicitPermission(channel, role);
    if (isApplied(permissions, Permission.ADMINISTRATOR.getRawValue()))
      return Permission.ALL_CHANNEL_PERMISSIONS;
    if (!isApplied(permissions, Permission.VIEW_CHANNEL.getRawValue()))
      return 0L;
    return permissions;
  }
  



















  public static long getExplicitPermission(Member member)
  {
    Checks.notNull(member, "Member");
    
    Guild guild = member.getGuild();
    long permission = guild.getPublicRole().getPermissionsRaw();
    
    for (Role role : member.getRoles()) {
      permission |= role.getPermissionsRaw();
    }
    return permission;
  }
  

























  public static long getExplicitPermission(GuildChannel channel, Member member)
  {
    return getExplicitPermission(channel, member, true);
  }
  



























  public static long getExplicitPermission(GuildChannel channel, Member member, boolean includeRoles)
  {
    Checks.notNull(channel, "Channel");
    Checks.notNull(member, "Member");
    
    Guild guild = member.getGuild();
    checkGuild(channel.getGuild(), guild, "Member");
    
    long permission = includeRoles ? getExplicitPermission(member) : 0L;
    
    AtomicLong allow = new AtomicLong(0L);
    AtomicLong deny = new AtomicLong(0L);
    

    getExplicitOverrides(channel, member, allow, deny);
    
    return apply(permission, allow.get(), deny.get());
  }
  























  public static long getExplicitPermission(GuildChannel channel, Role role)
  {
    return getExplicitPermission(channel, role, true);
  }
  

























  public static long getExplicitPermission(GuildChannel channel, Role role, boolean includeRoles)
  {
    Checks.notNull(channel, "Channel");
    Checks.notNull(role, "Role");
    
    Guild guild = role.getGuild();
    checkGuild(channel.getGuild(), guild, "Role");
    
    long permission = includeRoles ? role.getPermissionsRaw() | guild.getPublicRole().getPermissionsRaw() : 0L;
    PermissionOverride override = channel.getPermissionOverride(guild.getPublicRole());
    if (override != null)
      permission = apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
    if (role.isPublicRole()) {
      return permission;
    }
    override = channel.getPermissionOverride(role);
    
    return override == null ? 
      permission : 
      apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
  }
  
  private static void getExplicitOverrides(GuildChannel channel, Member member, AtomicLong allow, AtomicLong deny)
  {
    PermissionOverride override = channel.getPermissionOverride(member.getGuild().getPublicRole());
    long allowRaw = 0L;
    long denyRaw = 0L;
    if (override != null)
    {
      denyRaw = override.getDeniedRaw();
      allowRaw = override.getAllowedRaw();
    }
    
    long allowRole = 0L;
    long denyRole = 0L;
    
    for (Role role : member.getRoles())
    {
      override = channel.getPermissionOverride(role);
      if (override != null)
      {

        denyRole |= override.getDeniedRaw();
        allowRole |= override.getAllowedRaw();
      }
    }
    
    allowRaw = allowRaw & (denyRole ^ 0xFFFFFFFFFFFFFFFF) | allowRole;
    denyRaw = denyRaw & (allowRole ^ 0xFFFFFFFFFFFFFFFF) | denyRole;
    
    override = channel.getPermissionOverride(member);
    if (override != null)
    {

      long oDeny = override.getDeniedRaw();
      long oAllow = override.getAllowedRaw();
      allowRaw = allowRaw & (oDeny ^ 0xFFFFFFFFFFFFFFFF) | oAllow;
      denyRaw = denyRaw & (oAllow ^ 0xFFFFFFFFFFFFFFFF) | oDeny;
    }
    

    allow.set(allowRaw);
    deny.set(denyRaw);
  }
  



  private static boolean isApplied(long permissions, long perms)
  {
    return (permissions & perms) == perms;
  }
  
  private static long apply(long permission, long allow, long deny)
  {
    permission &= (deny ^ 0xFFFFFFFFFFFFFFFF);
    permission |= allow;
    
    return permission;
  }
  
  private static void checkGuild(Guild o1, Guild o2, String name)
  {
    Checks.check(o1.equals(o2), "Specified %s is not in the same guild! (%s / %s)", new Object[] { name, o1, o2 });
  }
}
