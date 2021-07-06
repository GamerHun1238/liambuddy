package net.dv8tion.jda.api.events.channel.store.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;
























public class StoreChannelUpdatePositionEvent
  extends GenericStoreChannelUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "position";
  
  public StoreChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, int prev)
  {
    super(api, responseNumber, channel, Integer.valueOf(prev), Integer.valueOf(channel.getPositionRaw()), "position");
  }
  





  public int getOldPosition()
  {
    return ((Integer)getOldValue()).intValue();
  }
  





  public int getNewPosition()
  {
    return ((Integer)getNewValue()).intValue();
  }
}
