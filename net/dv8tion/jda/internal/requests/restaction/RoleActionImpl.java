package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Roles;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;












public class RoleActionImpl
  extends AuditableRestActionImpl<Role>
  implements RoleAction
{
  protected final Guild guild;
  protected Long permissions;
  protected String name = null;
  protected Integer color = null;
  protected Boolean hoisted = null;
  protected Boolean mentionable = null;
  






  public RoleActionImpl(Guild guild)
  {
    super(guild.getJDA(), Route.Roles.CREATE_ROLE.compile(new String[] { guild.getId() }));
    this.guild = guild;
  }
  

  @Nonnull
  public RoleActionImpl setCheck(BooleanSupplier checks)
  {
    return (RoleActionImpl)super.setCheck(checks);
  }
  

  @Nonnull
  public RoleActionImpl timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (RoleActionImpl)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public RoleActionImpl deadline(long timestamp)
  {
    return (RoleActionImpl)super.deadline(timestamp);
  }
  

  @Nonnull
  public Guild getGuild()
  {
    return guild;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleActionImpl setName(String name)
  {
    if (name != null)
    {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
    }
    this.name = name;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleActionImpl setHoisted(Boolean hoisted)
  {
    this.hoisted = hoisted;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleActionImpl setMentionable(Boolean mentionable)
  {
    this.mentionable = mentionable;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleActionImpl setColor(Integer rgb)
  {
    color = rgb;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public RoleActionImpl setPermissions(Long permissions)
  {
    if (permissions != null)
    {
      for (Permission p : Permission.getPermissions(permissions.longValue()))
        checkPermission(p);
    }
    this.permissions = permissions;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject object = DataObject.empty();
    if (name != null)
      object.put("name", name);
    if (color != null)
      object.put("color", Integer.valueOf(color.intValue() & 0xFFFFFF));
    if (permissions != null)
      object.put("permissions", permissions);
    if (hoisted != null)
      object.put("hoist", hoisted);
    if (mentionable != null) {
      object.put("mentionable", mentionable);
    }
    return getRequestBody(object);
  }
  

  protected void handleSuccess(Response response, Request<Role> request)
  {
    request.onSuccess(api.getEntityBuilder().createRole((GuildImpl)guild, response.getObject(), guild.getIdLong()));
  }
  
  private void checkPermission(Permission permission)
  {
    if (!guild.getSelfMember().hasPermission(new Permission[] { permission })) {
      throw new InsufficientPermissionException(guild, permission);
    }
  }
}
