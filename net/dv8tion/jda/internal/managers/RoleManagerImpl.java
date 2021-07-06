package net.dv8tion.jda.internal.managers;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Roles;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import okhttp3.RequestBody;




















public class RoleManagerImpl
  extends ManagerBase<RoleManager>
  implements RoleManager
{
  protected Role role;
  protected String name;
  protected int color;
  protected long permissions;
  protected boolean hoist;
  protected boolean mentionable;
  
  public RoleManagerImpl(Role role)
  {
    super(role.getJDA(), Route.Roles.MODIFY_ROLE.compile(new String[] { role.getGuild().getId(), role.getId() }));
    JDA api = role.getJDA();
    this.role = role;
    if (isPermissionChecksEnabled()) {
      checkPermissions();
    }
  }
  
  @Nonnull
  public Role getRole()
  {
    Role realRole = role.getGuild().getRoleById(role.getIdLong());
    if (realRole != null)
      role = realRole;
    return role;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl reset(long fields)
  {
    super.reset(fields);
    if ((fields & 1L) == 1L)
      name = null;
    if ((fields & 0x2) == 2L)
      color = 536870911;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl reset()
  {
    super.reset();
    name = null;
    color = 536870911;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl setName(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 100, "Name");
    this.name = name;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl setPermissions(long perms)
  {
    long selfPermissions = PermissionUtil.getEffectivePermission(getGuild().getSelfMember());
    setupPermissions();
    long missingPerms = perms;
    missingPerms &= (selfPermissions ^ 0xFFFFFFFFFFFFFFFF);
    missingPerms &= (permissions ^ 0xFFFFFFFFFFFFFFFF);
    
    if ((missingPerms != 0L) && (isPermissionChecksEnabled()))
    {
      EnumSet<Permission> permissionList = Permission.getPermissions(missingPerms);
      if (!permissionList.isEmpty())
        throw new InsufficientPermissionException(getGuild(), (Permission)permissionList.iterator().next());
    }
    permissions = perms;
    set |= 0x4;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl setColor(int rgb)
  {
    color = rgb;
    set |= 0x2;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl setHoisted(boolean hoisted)
  {
    hoist = hoisted;
    set |= 0x8;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl setMentionable(boolean mentionable)
  {
    this.mentionable = mentionable;
    set |= 0x10;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl givePermissions(@Nonnull Collection<Permission> perms)
  {
    Checks.noneNull(perms, "Permissions");
    setupPermissions();
    return setPermissions(permissions | Permission.getRaw(perms));
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleManagerImpl revokePermissions(@Nonnull Collection<Permission> perms)
  {
    Checks.noneNull(perms, "Permissions");
    setupPermissions();
    return setPermissions(permissions & (Permission.getRaw(perms) ^ 0xFFFFFFFFFFFFFFFF));
  }
  

  protected RequestBody finalizeData()
  {
    DataObject object = DataObject.empty().put("name", getRole().getName());
    if (shouldUpdate(1L))
      object.put("name", name);
    if (shouldUpdate(4L))
      object.put("permissions", Long.valueOf(permissions));
    if (shouldUpdate(8L))
      object.put("hoist", Boolean.valueOf(hoist));
    if (shouldUpdate(16L))
      object.put("mentionable", Boolean.valueOf(mentionable));
    if (shouldUpdate(2L))
      object.put("color", Integer.valueOf(color == 536870911 ? 0 : color & 0xFFFFFF));
    reset();
    return getRequestBody(object);
  }
  

  protected boolean checkPermissions()
  {
    Member selfMember = getGuild().getSelfMember();
    if (!selfMember.hasPermission(new Permission[] { Permission.MANAGE_ROLES }))
      throw new InsufficientPermissionException(getGuild(), Permission.MANAGE_ROLES);
    if (!selfMember.canInteract(getRole()))
      throw new HierarchyException("Cannot modify a role that is higher or equal in hierarchy");
    return super.checkPermissions();
  }
  
  private void setupPermissions()
  {
    if (!shouldUpdate(4L)) {
      permissions = getRole().getPermissionsRaw();
    }
  }
}
