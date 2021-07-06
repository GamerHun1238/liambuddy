package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;
import org.slf4j.Logger;









public class ChannelDeleteHandler
  extends SocketHandler
{
  public ChannelDeleteHandler(JDAImpl api)
  {
    super(api);
  }
  

  protected Long handleInternally(DataObject content)
  {
    ChannelType type = ChannelType.fromId(content.getInt("type"));
    
    long guildId = 0L;
    if (type.isGuild())
    {
      guildId = content.getLong("guild_id");
      if (getJDA().getGuildSetupController().isLocked(guildId)) {
        return Long.valueOf(guildId);
      }
    }
    GuildImpl guild = (GuildImpl)getJDA().getGuildById(guildId);
    long channelId = content.getLong("id");
    
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$ChannelType[type.ordinal()])
    {

    case 1: 
      StoreChannel channel = (StoreChannel)getJDA().getStoreChannelsView().remove(channelId);
      if ((channel == null) || (guild == null))
      {
        WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a store channel that is not yet cached. JSON: {}", content);
        return null;
      }
      
      guild.getStoreChannelView().remove(channelId);
      getJDA().handleEvent(new StoreChannelDeleteEvent(
      
        getJDA(), responseNumber, channel));
      
      break;
    

    case 2: 
      TextChannel channel = (TextChannel)getJDA().getTextChannelsView().remove(channelId);
      if ((channel == null) || (guild == null))
      {
        WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a text channel that is not yet cached. JSON: {}", content);
        return null;
      }
      
      guild.getTextChannelsView().remove(channel.getIdLong());
      getJDA().handleEvent(new TextChannelDeleteEvent(
      
        getJDA(), responseNumber, channel));
      
      break;
    

    case 3: 
    case 4: 
      VoiceChannel channel = (VoiceChannel)getJDA().getVoiceChannelsView().remove(channelId);
      if ((channel == null) || (guild == null))
      {
        WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a voice channel that is not yet cached. JSON: {}", content);
        return null;
      }
      








      guild.getVoiceChannelsView().remove(channel.getIdLong());
      getJDA().handleEvent(new VoiceChannelDeleteEvent(
      
        getJDA(), responseNumber, channel));
      
      break;
    

    case 5: 
      Category category = (Category)getJDA().getCategoriesView().remove(channelId);
      if ((category == null) || (guild == null))
      {
        WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a category channel that is not yet cached. JSON: {}", content);
        return null;
      }
      
      guild.getCategoriesView().remove(channelId);
      getJDA().handleEvent(new CategoryDeleteEvent(
      
        getJDA(), responseNumber, category));
      
      break;
    

    case 6: 
      SnowflakeCacheViewImpl<PrivateChannel> privateView = getJDA().getPrivateChannelsView();
      PrivateChannel channel = (PrivateChannel)privateView.remove(channelId);
      
      if (channel == null)
      {

        WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a private channel that is not yet cached. JSON: {}", content);
        return null;
      }
      

      break;
    case 7: 
      WebSocketClient.LOG.warn("Received a CHANNEL_DELETE for a channel of type GROUP which is not supported!");
      return null;
    default: 
      WebSocketClient.LOG.debug("CHANNEL_DELETE provided an unknown channel type. JSON: {}", content); }
    
    getJDA().getEventCache().clear(EventCache.Type.CHANNEL, channelId);
    return null;
  }
}
