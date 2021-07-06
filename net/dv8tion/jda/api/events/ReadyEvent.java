package net.dv8tion.jda.api.events;

import gnu.trove.set.TLongSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.handle.GuildSetupController;
import net.dv8tion.jda.internal.handle.GuildSetupController.Status;



















public class ReadyEvent
  extends Event
{
  private final int availableGuilds;
  private final int unavailableGuilds;
  
  public ReadyEvent(@Nonnull JDA api, long responseNumber)
  {
    super(api, responseNumber);
    availableGuilds = ((int)getJDA().getGuildCache().size());
    GuildSetupController setupController = ((JDAImpl)getJDA()).getGuildSetupController();
    unavailableGuilds = (setupController.getSetupNodes(GuildSetupController.Status.UNAVAILABLE).size() + setupController.getUnavailableGuilds().size());
  }
  










  public int getGuildAvailableCount()
  {
    return availableGuilds;
  }
  







  public int getGuildUnavailableCount()
  {
    return unavailableGuilds;
  }
  





  public int getGuildTotalCount()
  {
    return getGuildAvailableCount() + getGuildUnavailableCount();
  }
}
