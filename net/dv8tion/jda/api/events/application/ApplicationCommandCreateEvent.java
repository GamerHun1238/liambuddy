package net.dv8tion.jda.api.events.application;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;






















public class ApplicationCommandCreateEvent
  extends GenericApplicationCommandEvent
{
  public ApplicationCommandCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Command command, @Nullable Guild guild)
  {
    super(api, responseNumber, command, guild);
  }
}
