package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.ButtonInteractionImpl;
import net.dv8tion.jda.internal.interactions.CommandInteractionImpl;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.interactions.SelectionMenuInteractionImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import org.slf4j.Logger;

















public class InteractionCreateHandler
  extends SocketHandler
{
  public InteractionCreateHandler(JDAImpl api)
  {
    super(api);
  }
  

  protected Long handleInternally(DataObject content)
  {
    int type = content.getInt("type");
    if (content.getInt("version", 1) != 1)
    {
      WebSocketClient.LOG.debug("Received interaction with version {}. This version is currently unsupported by this version of JDA. Consider updating!", Integer.valueOf(content.getInt("version", 1)));
      return null;
    }
    
    long guildId = content.getUnsignedLong("guild_id", 0L);
    if (api.getGuildSetupController().isLocked(guildId))
      return Long.valueOf(guildId);
    if ((guildId != 0L) && (api.getGuildById(guildId) == null)) {
      return null;
    }
    switch (net.dv8tion.jda.api.interactions.InteractionType.fromKey(type))
    {
    case SLASH_COMMAND: 
      handleCommand(content);
      break;
    case COMPONENT: 
      handleAction(content);
      break;
    default: 
      api.handleEvent(new GenericInteractionCreateEvent(api, responseNumber, new InteractionImpl(api, content)));
    }
    
    

    return null;
  }
  
  private void handleCommand(DataObject content)
  {
    api.handleEvent(new SlashCommandEvent(api, responseNumber, new CommandInteractionImpl(api, content)));
  }
  


  private void handleAction(DataObject content)
  {
    switch (net.dv8tion.jda.api.interactions.components.Component.Type.fromKey(content.getObject("data").getInt("component_type")))
    {
    case BUTTON: 
      api.handleEvent(new ButtonClickEvent(api, responseNumber, new ButtonInteractionImpl(api, content)));
      

      break;
    case SELECTION_MENU: 
      api.handleEvent(new SelectionMenuEvent(api, responseNumber, new SelectionMenuInteractionImpl(api, content)));
    }
  }
}
