package net.dv8tion.jda.internal.interactions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.Component.Type;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;















public class ButtonInteractionImpl
  extends ComponentInteractionImpl
  implements ButtonInteraction
{
  private final Button button;
  
  public ButtonInteractionImpl(JDAImpl jda, DataObject data)
  {
    super(jda, data);
    button = (message != null ? message.getButtonById(customId) : null);
  }
  

  @Nonnull
  public Component.Type getComponentType()
  {
    return Component.Type.BUTTON;
  }
  

  @Nonnull
  public Button getButton()
  {
    return button;
  }
}
