package net.dv8tion.jda.api.events.interaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;

























public class ButtonClickEvent
  extends GenericComponentInteractionCreateEvent
  implements ButtonInteraction
{
  private final ButtonInteraction interaction;
  
  public ButtonClickEvent(@Nonnull JDA api, long responseNumber, @Nonnull ButtonInteraction interaction)
  {
    super(api, responseNumber, interaction);
    this.interaction = interaction;
  }
  

  @Nonnull
  public ButtonInteraction getInteraction()
  {
    return interaction;
  }
  

  @Nullable
  public Button getComponent()
  {
    return interaction.getComponent();
  }
  

  @Nullable
  public Button getButton()
  {
    return interaction.getButton();
  }
}
