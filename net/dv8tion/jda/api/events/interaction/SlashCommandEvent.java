package net.dv8tion.jda.api.events.interaction;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.interactions.CommandInteractionImpl;























public class SlashCommandEvent
  extends GenericInteractionCreateEvent
  implements CommandInteraction
{
  private final CommandInteractionImpl commandInteraction;
  
  public SlashCommandEvent(@Nonnull JDA api, long responseNumber, @Nonnull CommandInteractionImpl interaction)
  {
    super(api, responseNumber, interaction);
    commandInteraction = interaction;
  }
  

  @Nonnull
  public MessageChannel getChannel()
  {
    return commandInteraction.getChannel();
  }
  

  @Nonnull
  public String getName()
  {
    return commandInteraction.getName();
  }
  

  @Nullable
  public String getSubcommandName()
  {
    return commandInteraction.getSubcommandName();
  }
  

  @Nullable
  public String getSubcommandGroup()
  {
    return commandInteraction.getSubcommandGroup();
  }
  

  public long getCommandIdLong()
  {
    return commandInteraction.getCommandIdLong();
  }
  

  @Nonnull
  public List<OptionMapping> getOptions()
  {
    return commandInteraction.getOptions();
  }
}
