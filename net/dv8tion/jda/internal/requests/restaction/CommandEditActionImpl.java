package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Interactions;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;














public class CommandEditActionImpl
  extends RestActionImpl<Command>
  implements CommandEditAction
{
  private static final String UNDEFINED = "undefined";
  private static final int NAME_SET = 1;
  private static final int DESCRIPTION_SET = 2;
  private static final int OPTIONS_SET = 4;
  private final Guild guild;
  private int mask = 0;
  private CommandData data = new CommandData("undefined", "undefined");
  
  public CommandEditActionImpl(JDA api, String id)
  {
    super(api, Route.Interactions.EDIT_COMMAND.compile(new String[] { api.getSelfUser().getApplicationId(), id }));
    guild = null;
  }
  
  public CommandEditActionImpl(Guild guild, String id)
  {
    super(guild.getJDA(), Route.Interactions.EDIT_GUILD_COMMAND.compile(new String[] { guild.getJDA().getSelfUser().getApplicationId(), guild.getId(), id }));
    this.guild = guild;
  }
  

  @Nonnull
  public CommandEditAction setCheck(BooleanSupplier checks)
  {
    return (CommandEditAction)super.setCheck(checks);
  }
  

  @Nonnull
  public CommandEditAction deadline(long timestamp)
  {
    return (CommandEditAction)super.deadline(timestamp);
  }
  

  @Nonnull
  public CommandEditAction apply(@Nonnull CommandData commandData)
  {
    Checks.notNull(commandData, "Command Data");
    mask = 7;
    data = commandData;
    return this;
  }
  

  @Nonnull
  public CommandEditAction setDefaultEnabled(boolean enabled)
  {
    data.setDefaultEnabled(enabled);
    return this;
  }
  

  @Nonnull
  public CommandEditAction addCheck(@Nonnull BooleanSupplier checks)
  {
    return (CommandEditAction)super.addCheck(checks);
  }
  

  @Nonnull
  public CommandEditAction timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (CommandEditAction)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public CommandEditAction setName(@Nullable String name)
  {
    if (name == null)
    {
      mask &= 0xFFFFFFFE;
      return this;
    }
    data.setName(name);
    mask |= 0x1;
    return this;
  }
  

  @Nonnull
  public CommandEditAction setDescription(@Nullable String description)
  {
    if (description == null)
    {
      mask &= 0xFFFFFFFD;
      return this;
    }
    data.setDescription(description);
    mask |= 0x2;
    return this;
  }
  

  @Nonnull
  public CommandEditAction clearOptions()
  {
    data = new CommandData(data.getName(), data.getDescription());
    mask &= 0xFFFFFFFB;
    return this;
  }
  

  @Nonnull
  public CommandEditAction addOptions(@Nonnull OptionData... options)
  {
    data.addOptions(options);
    mask |= 0x4;
    return this;
  }
  

  @Nonnull
  public CommandEditAction addSubcommands(@Nonnull SubcommandData... subcommands)
  {
    data.addSubcommands(subcommands);
    mask |= 0x4;
    return this;
  }
  

  @Nonnull
  public CommandEditAction addSubcommandGroups(@Nonnull SubcommandGroupData... groups)
  {
    data.addSubcommandGroups(groups);
    mask |= 0x4;
    return this;
  }
  
  private boolean isUnchanged(int flag)
  {
    return (mask & flag) != flag;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject json = data.toData();
    if (isUnchanged(1))
      json.remove("name");
    if (isUnchanged(2))
      json.remove("description");
    if (isUnchanged(4))
      json.remove("options");
    mask = 0;
    data = new CommandData("undefined", "undefined");
    return getRequestBody(json);
  }
  

  protected void handleSuccess(Response response, Request<Command> request)
  {
    DataObject json = response.getObject();
    request.onSuccess(new Command(api, guild, json));
  }
}
