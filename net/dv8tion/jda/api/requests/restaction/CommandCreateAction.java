package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;































































































public abstract interface CommandCreateAction
  extends RestAction<Command>
{
  @Nonnull
  public abstract CommandCreateAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract CommandCreateAction addCheck(@Nonnull BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract CommandCreateAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract CommandCreateAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction setDefaultEnabled(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction setDescription(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction addOptions(@Nonnull OptionData... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public CommandCreateAction addOptions(@Nonnull Collection<? extends OptionData> options)
  {
    Checks.noneNull(options, "Option");
    return addOptions((OptionData[])options.toArray(new OptionData[0]));
  }
  

























  @Nonnull
  @CheckReturnValue
  public CommandCreateAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description, boolean required)
  {
    return addOptions(new OptionData[] { new OptionData(type, name, description).setRequired(required) });
  }
  
























  @Nonnull
  @CheckReturnValue
  public CommandCreateAction addOption(@Nonnull OptionType type, @Nonnull String name, @Nonnull String description)
  {
    return addOption(type, name, description, false);
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction addSubcommands(@Nonnull SubcommandData paramSubcommandData);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandCreateAction addSubcommandGroups(@Nonnull SubcommandGroupData paramSubcommandGroupData);
}
