package net.dv8tion.jda.api.interactions.commands.privileges;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
































public class CommandPrivilege
  implements ISnowflake, SerializableData
{
  private final Type type;
  private final boolean enabled;
  private final long id;
  
  public CommandPrivilege(@Nonnull Type type, boolean enabled, long id)
  {
    Checks.notNull(type, "Type");
    this.type = type;
    this.enabled = enabled;
    this.id = id;
  }
  








  @Nonnull
  public static CommandPrivilege enable(@Nonnull Role role)
  {
    Checks.notNull(role, "Role");
    return new CommandPrivilege(Type.ROLE, true, role.getIdLong());
  }
  








  @Nonnull
  public static CommandPrivilege enable(@Nonnull User user)
  {
    Checks.notNull(user, "User");
    return new CommandPrivilege(Type.USER, true, user.getIdLong());
  }
  








  @Nonnull
  public static CommandPrivilege enableUser(@Nonnull String userId)
  {
    return enableUser(MiscUtil.parseSnowflake(userId));
  }
  








  @Nonnull
  public static CommandPrivilege enableUser(long userId)
  {
    return new CommandPrivilege(Type.USER, true, userId);
  }
  








  @Nonnull
  public static CommandPrivilege enableRole(@Nonnull String roleId)
  {
    return enableRole(MiscUtil.parseSnowflake(roleId));
  }
  








  @Nonnull
  public static CommandPrivilege enableRole(long roleId)
  {
    return new CommandPrivilege(Type.ROLE, true, roleId);
  }
  








  @Nonnull
  public static CommandPrivilege disable(@Nonnull Role role)
  {
    Checks.notNull(role, "Role");
    return new CommandPrivilege(Type.ROLE, false, role.getIdLong());
  }
  








  @Nonnull
  public static CommandPrivilege disable(@Nonnull User user)
  {
    Checks.notNull(user, "User");
    return new CommandPrivilege(Type.USER, false, user.getIdLong());
  }
  








  @Nonnull
  public static CommandPrivilege disableUser(@Nonnull String userId)
  {
    return disableUser(MiscUtil.parseSnowflake(userId));
  }
  








  @Nonnull
  public static CommandPrivilege disableUser(long userId)
  {
    return new CommandPrivilege(Type.USER, false, userId);
  }
  








  @Nonnull
  public static CommandPrivilege disableRole(@Nonnull String roleId)
  {
    return disableRole(MiscUtil.parseSnowflake(roleId));
  }
  








  @Nonnull
  public static CommandPrivilege disableRole(long roleId)
  {
    return new CommandPrivilege(Type.ROLE, false, roleId);
  }
  


  public long getIdLong()
  {
    return id;
  }
  





  @Nonnull
  public Type getType()
  {
    return type;
  }
  





  public boolean isEnabled()
  {
    return enabled;
  }
  





  public boolean isDisabled()
  {
    return !enabled;
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof CommandPrivilege))
      return false;
    return id == id;
  }
  

  @Nonnull
  public DataObject toData()
  {
    return 
    

      DataObject.empty().put("id", Long.valueOf(id)).put("type", Integer.valueOf(type.key)).put("permission", Boolean.valueOf(enabled));
  }
  



  public static enum Type
  {
    UNKNOWN(-1), 
    ROLE(1), 
    USER(2);
    
    private final int key;
    
    private Type(int key)
    {
      this.key = key;
    }
    








    @Nonnull
    public static Type fromKey(int key)
    {
      for (Type type : )
      {
        if (key == key)
          return type;
      }
      return UNKNOWN;
    }
  }
}
