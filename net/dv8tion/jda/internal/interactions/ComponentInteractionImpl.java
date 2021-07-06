package net.dv8tion.jda.internal.interactions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.restaction.interactions.UpdateInteractionActionImpl;















public abstract class ComponentInteractionImpl
  extends InteractionImpl
  implements ComponentInteraction
{
  protected final String customId;
  protected final Message message;
  protected final long messageId;
  
  public ComponentInteractionImpl(JDAImpl jda, DataObject data)
  {
    super(jda, data);
    customId = data.getObject("data").getString("custom_id");
    
    DataObject messageJson = data.getObject("message");
    messageId = messageJson.getUnsignedLong("id");
    
    message = (messageJson.isNull("type") ? null : jda.getEntityBuilder().createMessage(messageJson));
  }
  


  @Nonnull
  public MessageChannel getChannel()
  {
    return (MessageChannel)super.getChannel();
  }
  

  @Nonnull
  public String getComponentId()
  {
    return customId;
  }
  

  @Nullable
  public Message getMessage()
  {
    return message;
  }
  

  public long getMessageIdLong()
  {
    return messageId;
  }
  

  @Nonnull
  public UpdateInteractionActionImpl deferEdit()
  {
    return new UpdateInteractionActionImpl(hook);
  }
}
