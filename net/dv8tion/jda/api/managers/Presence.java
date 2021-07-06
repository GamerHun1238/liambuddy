package net.dv8tion.jda.api.managers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public abstract interface Presence
{
  @Nonnull
  public abstract JDA getJDA();
  
  @Nonnull
  public abstract OnlineStatus getStatus();
  
  @Nullable
  public abstract Activity getActivity();
  
  public abstract boolean isIdle();
  
  public abstract void setStatus(@Nullable OnlineStatus paramOnlineStatus);
  
  public abstract void setActivity(@Nullable Activity paramActivity);
  
  public abstract void setIdle(boolean paramBoolean);
  
  public abstract void setPresence(@Nullable OnlineStatus paramOnlineStatus, @Nullable Activity paramActivity, boolean paramBoolean);
  
  public abstract void setPresence(@Nullable OnlineStatus paramOnlineStatus, @Nullable Activity paramActivity);
  
  public abstract void setPresence(@Nullable OnlineStatus paramOnlineStatus, boolean paramBoolean);
  
  public abstract void setPresence(@Nullable Activity paramActivity, boolean paramBoolean);
}
