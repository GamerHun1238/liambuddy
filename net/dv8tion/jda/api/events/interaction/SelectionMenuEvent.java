package net.dv8tion.jda.api.events.interaction;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenuInteraction;

























public class SelectionMenuEvent
  extends GenericComponentInteractionCreateEvent
  implements SelectionMenuInteraction
{
  private final SelectionMenuInteraction menuInteraction;
  
  public SelectionMenuEvent(@Nonnull JDA api, long responseNumber, @Nonnull SelectionMenuInteraction interaction)
  {
    super(api, responseNumber, interaction);
    menuInteraction = interaction;
  }
  

  @Nonnull
  public SelectionMenuInteraction getInteraction()
  {
    return menuInteraction;
  }
  

  @Nullable
  public SelectionMenu getComponent()
  {
    return menuInteraction.getComponent();
  }
  

  @Nonnull
  public List<String> getValues()
  {
    return menuInteraction.getValues();
  }
}
