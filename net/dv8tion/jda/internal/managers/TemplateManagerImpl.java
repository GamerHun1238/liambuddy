package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.entities.templates.TemplateGuild;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.TemplateManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Templates;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;




















public class TemplateManagerImpl
  extends ManagerBase<TemplateManager>
  implements TemplateManager
{
  protected final Template template;
  protected final JDA api;
  protected String name;
  protected String description;
  
  public TemplateManagerImpl(Template template)
  {
    super(template.getJDA(), Route.Templates.MODIFY_TEMPLATE.compile(new String[] { template.getGuild().getId(), template.getCode() }));
    this.template = template;
    api = template.getJDA();
    if (isPermissionChecksEnabled()) {
      checkPermissions();
    }
  }
  
  @Nonnull
  @CheckReturnValue
  public TemplateManagerImpl reset(long fields)
  {
    super.reset(fields);
    if ((fields & 1L) == 1L)
      name = null;
    if ((fields & 0x2) == 2L)
      description = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public TemplateManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public TemplateManagerImpl reset()
  {
    super.reset();
    name = null;
    description = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public TemplateManagerImpl setName(@Nonnull String name)
  {
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 100, "Name");
    this.name = name;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public TemplateManagerImpl setDescription(@Nullable String description)
  {
    if (description != null)
      Checks.notLonger(name, 120, "Description");
    this.description = description;
    set |= 0x2;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject body = DataObject.empty();
    if (shouldUpdate(1L))
      body.put("name", name);
    if (shouldUpdate(2L)) {
      body.put("description", name);
    }
    reset();
    return getRequestBody(body);
  }
  

  protected boolean checkPermissions()
  {
    Guild guild = api.getGuildById(template.getGuild().getIdLong());
    
    if (guild == null)
      return true;
    if (!guild.getSelfMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER }))
      throw new InsufficientPermissionException(guild, Permission.MANAGE_SERVER);
    return super.checkPermissions();
  }
}
