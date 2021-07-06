package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import org.slf4j.Logger;















public class ChannelCreateHandler
  extends SocketHandler
{
  public ChannelCreateHandler(JDAImpl api)
  {
    super(api);
  }
  

  protected Long handleInternally(DataObject content)
  {
    ChannelType type = ChannelType.fromId(content.getInt("type"));
    
    long guildId = 0L;
    JDAImpl jda = getJDA();
    if (type.isGuild())
    {
      guildId = content.getLong("guild_id");
      if (jda.getGuildSetupController().isLocked(guildId)) {
        return Long.valueOf(guildId);
      }
    }
    EntityBuilder builder = jda.getEntityBuilder();
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$ChannelType[type.ordinal()])
    {

    case 1: 
      builder.createStoreChannel(content, guildId);
      jda.handleEvent(new StoreChannelCreateEvent(jda, responseNumber, builder
      

        .createStoreChannel(content, guildId)));
      break;
    

    case 2: 
      jda.handleEvent(new TextChannelCreateEvent(jda, responseNumber, builder
      

        .createTextChannel(content, guildId)));
      break;
    

    case 3: 
    case 4: 
      jda.handleEvent(new VoiceChannelCreateEvent(jda, responseNumber, builder
      

        .createVoiceChannel(content, guildId)));
      break;
    

    case 5: 
      jda.handleEvent(new CategoryCreateEvent(jda, responseNumber, builder
      

        .createCategory(content, guildId)));
      break;
    
    case 6: 
      WebSocketClient.LOG.warn("Received a CREATE_CHANNEL for a group which is not supported");
      return null;
    default: 
      WebSocketClient.LOG.debug("Discord provided an CREATE_CHANNEL event with an unknown channel type! JSON: {}", content); }
    
    return null;
  }
}
