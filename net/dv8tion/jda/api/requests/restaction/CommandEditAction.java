package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;















































































































public abstract interface CommandEditAction
  extends RestAction<Command>
{
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction addCheck(@Nonnull BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction apply(@Nonnull CommandData paramCommandData);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction setDefaultEnabled(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction setName(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction setDescription(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction clearOptions();
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction addOptions(@Nonnull OptionData... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public CommandEditAction addOptions(@Nonnull Collection<? extends OptionData> options)
  {
    Checks.noneNull(options, "Options");
    return addOptions((OptionData[])options.toArray(new OptionData[0]));
  }
  


























  @Nonnull
  @CheckReturnValue
  public CommandEditAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required)
  {
    return addOptions(new OptionData[] { new OptionData(type, name, description).setRequired(required) });
  }
  
























  @Nonnull
  @CheckReturnValue
  public CommandEditAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description)
  {
    return addOption(type, name, description, false);
  }
  













  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction addSubcommands(@Nonnull SubcommandData... paramVarArgs);
  













  @Nonnull
  @CheckReturnValue
  public CommandEditAction addSubcommands(@Nonnull Collection<? extends SubcommandData> subcommands)
  {
    Checks.noneNull(subcommands, "Subcommands");
    return addSubcommands((SubcommandData[])subcommands.toArray(new SubcommandData[0]));
  }
  













  @Nonnull
  @CheckReturnValue
  public abstract CommandEditAction addSubcommandGroups(@Nonnull SubcommandGroupData... paramVarArgs);
  













  @Nonnull
  @CheckReturnValue
  public CommandEditAction addSubcommandGroups(@Nonnull Collection<? extends SubcommandGroupData> groups)
  {
    Checks.noneNull(groups, "SubcommandGroups");
    return addSubcommandGroups((SubcommandGroupData[])groups.toArray(new SubcommandGroupData[0]));
  }
}
