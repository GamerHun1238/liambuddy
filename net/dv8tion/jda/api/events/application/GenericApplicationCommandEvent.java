package net.dv8tion.jda.api.events.application;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.Command;

























public abstract class GenericApplicationCommandEvent
  extends Event
{
  private final Command command;
  private final Guild guild;
  
  public GenericApplicationCommandEvent(@Nonnull JDA api, long responseNumber, @Nonnull Command command, @Nullable Guild guild)
  {
    super(api, responseNumber);
    this.command = command;
    this.guild = guild;
  }
  





  @Nonnull
  public Command getCommand()
  {
    return command;
  }
  





  @Nullable
  public Guild getGuild()
  {
    return guild;
  }
}
