package net.dv8tion.jda.api.requests.restaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;

















































public abstract interface CommandListUpdateAction
  extends RestAction<List<Command>>
{
  @Nonnull
  public abstract CommandListUpdateAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract CommandListUpdateAction deadline(long paramLong);
  
  @Nonnull
  public abstract CommandListUpdateAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract CommandListUpdateAction addCheck(@Nonnull BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  @CheckReturnValue
  public abstract CommandListUpdateAction addCommands(@Nonnull Collection<? extends CommandData> paramCollection);
  
  @Nonnull
  @CheckReturnValue
  public CommandListUpdateAction addCommands(@Nonnull CommandData... commands)
  {
    Checks.noneNull(commands, "Command");
    return addCommands(Arrays.asList(commands));
  }
}
