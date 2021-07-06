package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
















public abstract class SocketHandler
{
  protected final JDAImpl api;
  protected long responseNumber;
  protected DataObject allContent;
  
  public SocketHandler(JDAImpl api)
  {
    this.api = api;
  }
  
  public final synchronized void handle(long responseTotal, DataObject o)
  {
    allContent = o;
    responseNumber = responseTotal;
    Long guildId = handleInternally(o.getObject("d"));
    if (guildId != null)
      getJDA().getGuildSetupController().cacheEvent(guildId.longValue(), o);
    allContent = null;
  }
  
  protected JDAImpl getJDA()
  {
    return api;
  }
  



  protected abstract Long handleInternally(DataObject paramDataObject);
  



  public static class NOPHandler
    extends SocketHandler
  {
    public NOPHandler(JDAImpl api)
    {
      super();
    }
    

    protected Long handleInternally(DataObject content)
    {
      return null;
    }
  }
}
