package net.dv8tion.jda.api.audio.hooks;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

















public class ListenerProxy
  implements ConnectionListener
{
  private static final Logger log = LoggerFactory.getLogger(ListenerProxy.class);
  private volatile ConnectionListener listener = null;
  
  public ListenerProxy() {}
  
  public void onPing(long ping) {
    if (this.listener == null)
      return;
    ConnectionListener listener = this.listener;
    try
    {
      if (listener != null) {
        listener.onPing(ping);
      }
    }
    catch (Throwable t) {
      log.error("The ConnectionListener encountered and uncaught exception", t);
      if ((t instanceof Error)) {
        throw ((Error)t);
      }
    }
  }
  
  public void onStatusChange(@Nonnull ConnectionStatus status)
  {
    if (this.listener == null)
      return;
    ConnectionListener listener = this.listener;
    try
    {
      if (listener != null) {
        listener.onStatusChange(status);
      }
    }
    catch (Throwable t) {
      log.error("The ConnectionListener encountered and uncaught exception", t);
      if ((t instanceof Error)) {
        throw ((Error)t);
      }
    }
  }
  
  public void onUserSpeaking(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes)
  {
    if (this.listener == null)
      return;
    ConnectionListener listener = this.listener;
    try
    {
      if (listener != null)
      {
        listener.onUserSpeaking(user, modes);
        listener.onUserSpeaking(user, modes.contains(SpeakingMode.VOICE));
        listener.onUserSpeaking(user, modes.contains(SpeakingMode.VOICE), modes.contains(SpeakingMode.SOUNDSHARE));
      }
    }
    catch (Throwable t)
    {
      log.error("The ConnectionListener encountered and uncaught exception", t);
      if ((t instanceof Error)) {
        throw ((Error)t);
      }
    }
  }
  
  public void onUserSpeaking(@Nonnull User user, boolean speaking) {}
  
  public void setListener(ConnectionListener listener)
  {
    this.listener = listener;
  }
  
  public ConnectionListener getListener()
  {
    return listener;
  }
}
