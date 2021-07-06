package net.dv8tion.jda.internal.managers;

import java.util.Collections;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.Checks;





















public class PresenceImpl
  implements Presence
{
  private final JDAImpl api;
  private boolean idle = false;
  private Activity activity = null;
  private OnlineStatus status = OnlineStatus.ONLINE;
  






  public PresenceImpl(JDAImpl jda)
  {
    api = jda;
  }
  





  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  @Nonnull
  public OnlineStatus getStatus()
  {
    return status;
  }
  

  public Activity getActivity()
  {
    return activity;
  }
  

  public boolean isIdle()
  {
    return idle;
  }
  





  public void setStatus(OnlineStatus status)
  {
    setPresence(status, activity, idle);
  }
  

  public void setActivity(Activity game)
  {
    setPresence(status, game);
  }
  

  public void setIdle(boolean idle)
  {
    setPresence(status, idle);
  }
  

  public void setPresence(OnlineStatus status, Activity activity, boolean idle)
  {
    Checks.check(status != OnlineStatus.UNKNOWN, "Cannot set the presence status to an unknown OnlineStatus!");
    
    if ((status == OnlineStatus.OFFLINE) || (status == null)) {
      status = OnlineStatus.INVISIBLE;
    }
    this.idle = idle;
    this.status = status;
    this.activity = activity;
    update();
  }
  

  public void setPresence(OnlineStatus status, Activity activity)
  {
    setPresence(status, activity, idle);
  }
  

  public void setPresence(OnlineStatus status, boolean idle)
  {
    setPresence(status, activity, idle);
  }
  

  public void setPresence(Activity game, boolean idle)
  {
    setPresence(status, game, idle);
  }
  




  public PresenceImpl setCacheStatus(OnlineStatus status)
  {
    if (status == null)
      throw new NullPointerException("Null OnlineStatus is not allowed.");
    if (status == OnlineStatus.OFFLINE)
      status = OnlineStatus.INVISIBLE;
    this.status = status;
    return this;
  }
  
  public PresenceImpl setCacheActivity(Activity game)
  {
    activity = game;
    return this;
  }
  
  public PresenceImpl setCacheIdle(boolean idle)
  {
    this.idle = idle;
    return this;
  }
  




  public DataObject getFullPresence()
  {
    DataObject activity = getGameJson(this.activity);
    return DataObject.empty()
      .put("afk", Boolean.valueOf(idle))
      .put("since", Long.valueOf(System.currentTimeMillis()))
      .put("activities", DataArray.fromCollection(activity == null ? 
      Collections.emptyList() : 
      Collections.singletonList(activity)))
      .put("status", getStatus().getKey());
  }
  
  private DataObject getGameJson(Activity activity)
  {
    if ((activity == null) || (activity.getName() == null) || (activity.getType() == null))
      return null;
    DataObject gameObj = DataObject.empty();
    gameObj.put("name", activity.getName());
    gameObj.put("type", Integer.valueOf(activity.getType().getKey()));
    if (activity.getUrl() != null) {
      gameObj.put("url", activity.getUrl());
    }
    return gameObj;
  }
  




  protected void update()
  {
    DataObject data = getFullPresence();
    JDA.Status status = api.getStatus();
    if ((status == JDA.Status.RECONNECT_QUEUED) || (status == JDA.Status.SHUTDOWN) || (status == JDA.Status.SHUTTING_DOWN))
      return;
    api.getClient().send(DataObject.empty()
      .put("d", data)
      .put("op", Integer.valueOf(3)));
  }
}
