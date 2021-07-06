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
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Interactions;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;












public class CommandCreateActionImpl
  extends RestActionImpl<Command>
  implements CommandCreateAction
{
  private final Guild guild;
  private CommandData data;
  
  public CommandCreateActionImpl(JDAImpl api, CommandData command)
  {
    super(api, Route.Interactions.CREATE_COMMAND.compile(new String[] { api.getSelfUser().getApplicationId() }));
    guild = null;
    data = command;
  }
  
  public CommandCreateActionImpl(Guild guild, CommandData command)
  {
    super(guild.getJDA(), Route.Interactions.CREATE_GUILD_COMMAND.compile(new String[] { guild.getJDA().getSelfUser().getApplicationId(), guild.getId() }));
    this.guild = guild;
    data = command;
  }
  

  @Nonnull
  public CommandCreateAction addCheck(@Nonnull BooleanSupplier checks)
  {
    return (CommandCreateAction)super.addCheck(checks);
  }
  

  @Nonnull
  public CommandCreateAction setCheck(BooleanSupplier checks)
  {
    return (CommandCreateAction)super.setCheck(checks);
  }
  

  @Nonnull
  public CommandCreateAction deadline(long timestamp)
  {
    return (CommandCreateAction)super.deadline(timestamp);
  }
  

  @Nonnull
  public CommandCreateAction setDefaultEnabled(boolean enabled)
  {
    data.setDefaultEnabled(enabled);
    return this;
  }
  

  @Nonnull
  public CommandCreateAction timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (CommandCreateAction)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public CommandCreateAction setName(@Nonnull String name)
  {
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 32, "Name");
    Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
    data.setName(name);
    return this;
  }
  

  @Nonnull
  public CommandCreateAction setDescription(@Nonnull String description)
  {
    Checks.notEmpty(description, "Description");
    Checks.notLonger(description, 100, "Description");
    data.setDescription(description);
    return this;
  }
  

  @Nonnull
  public CommandCreateAction addOptions(@Nonnull OptionData... options)
  {
    data.addOptions(options);
    return this;
  }
  

  @Nonnull
  public CommandCreateAction addSubcommands(@Nonnull SubcommandData subcommand)
  {
    data.addSubcommands(new SubcommandData[] { subcommand });
    return this;
  }
  

  @Nonnull
  public CommandCreateAction addSubcommandGroups(@Nonnull SubcommandGroupData group)
  {
    data.addSubcommandGroups(new SubcommandGroupData[] { group });
    return this;
  }
  

  public RequestBody finalizeData()
  {
    return getRequestBody(data.toData());
  }
  

  protected void handleSuccess(Response response, Request<Command> request)
  {
    DataObject json = response.getObject();
    request.onSuccess(new Command(api, guild, json));
  }
}
