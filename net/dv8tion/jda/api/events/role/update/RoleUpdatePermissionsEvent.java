package net.dv8tion.jda.api.events.role.update;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

























public class RoleUpdatePermissionsEvent
  extends GenericRoleUpdateEvent<EnumSet<Permission>>
{
  public static final String IDENTIFIER = "permission";
  private final long oldPermissionsRaw;
  private final long newPermissionsRaw;
  
  public RoleUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, long oldPermissionsRaw)
  {
    super(api, responseNumber, role, Permission.getPermissions(oldPermissionsRaw), role.getPermissions(), "permission");
    this.oldPermissionsRaw = oldPermissionsRaw;
    newPermissionsRaw = role.getPermissionsRaw();
  }
  





  @Nonnull
  public EnumSet<Permission> getOldPermissions()
  {
    return getOldValue();
  }
  





  public long getOldPermissionsRaw()
  {
    return oldPermissionsRaw;
  }
  





  @Nonnull
  public EnumSet<Permission> getNewPermissions()
  {
    return getNewValue();
  }
  





  public long getNewPermissionsRaw()
  {
    return newPermissionsRaw;
  }
  

  @Nonnull
  public EnumSet<Permission> getOldValue()
  {
    return (EnumSet)super.getOldValue();
  }
  

  @Nonnull
  public EnumSet<Permission> getNewValue()
  {
    return (EnumSet)super.getNewValue();
  }
}
