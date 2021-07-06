package net.dv8tion.jda.api.audio.factory;

import java.util.concurrent.ConcurrentMap;
import javax.annotation.CheckForNull;

public abstract interface IAudioSendSystem
{
  public abstract void start();
  
  public abstract void shutdown();
  
  public void setContextMap(@CheckForNull ConcurrentMap<String, String> contextMap) {}
}
