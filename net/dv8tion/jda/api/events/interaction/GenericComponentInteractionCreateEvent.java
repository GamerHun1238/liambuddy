package net.dv8tion.jda.api.events.interaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.Component.Type;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;























public class GenericComponentInteractionCreateEvent
  extends GenericInteractionCreateEvent
  implements ComponentInteraction
{
  private final ComponentInteraction interaction;
  
  public GenericComponentInteractionCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull ComponentInteraction interaction)
  {
    super(api, responseNumber, interaction);
    this.interaction = interaction;
  }
  

  @Nonnull
  public ComponentInteraction getInteraction()
  {
    return interaction;
  }
  

  @Nonnull
  public MessageChannel getChannel()
  {
    return interaction.getChannel();
  }
  

  @Nonnull
  public String getComponentId()
  {
    return interaction.getComponentId();
  }
  

  @Nullable
  public Component getComponent()
  {
    return interaction.getComponent();
  }
  

  @Nullable
  public Message getMessage()
  {
    return interaction.getMessage();
  }
  

  public long getMessageIdLong()
  {
    return interaction.getMessageIdLong();
  }
  

  @Nonnull
  public Component.Type getComponentType()
  {
    return interaction.getComponentType();
  }
  

  @Nonnull
  public UpdateInteractionAction deferEdit()
  {
    return interaction.deferEdit();
  }
}
