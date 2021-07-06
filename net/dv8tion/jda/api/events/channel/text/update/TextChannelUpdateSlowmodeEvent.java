package net.dv8tion.jda.api.events.channel.text.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;























public class TextChannelUpdateSlowmodeEvent
  extends GenericTextChannelUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "slowmode";
  
  public TextChannelUpdateSlowmodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, int oldSlowmode)
  {
    super(api, responseNumber, channel, Integer.valueOf(oldSlowmode), Integer.valueOf(channel.getSlowmode()), "slowmode");
  }
  





  public int getOldSlowmode()
  {
    return ((Integer)getOldValue()).intValue();
  }
  





  public int getNewSlowmode()
  {
    return ((Integer)getNewValue()).intValue();
  }
}
