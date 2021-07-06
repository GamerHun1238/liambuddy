package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;






















public class StatusChangeEvent
  extends Event
  implements UpdateEvent<JDA, JDA.Status>
{
  public static final String IDENTIFIER = "status";
  protected final JDA.Status newStatus;
  protected final JDA.Status oldStatus;
  
  public StatusChangeEvent(@Nonnull JDA api, @Nonnull JDA.Status newStatus, @Nonnull JDA.Status oldStatus)
  {
    super(api);
    this.newStatus = newStatus;
    this.oldStatus = oldStatus;
  }
  





  @Nonnull
  public JDA.Status getNewStatus()
  {
    return newStatus;
  }
  





  @Nonnull
  public JDA.Status getOldStatus()
  {
    return oldStatus;
  }
  

  @Nonnull
  public String getPropertyIdentifier()
  {
    return "status";
  }
  

  @Nonnull
  public JDA getEntity()
  {
    return getJDA();
  }
  

  @Nonnull
  public JDA.Status getOldValue()
  {
    return oldStatus;
  }
  

  @Nonnull
  public JDA.Status getNewValue()
  {
    return newStatus;
  }
  

  public String toString()
  {
    return "StatusUpdate(" + getOldStatus() + "->" + getNewStatus() + ')';
  }
}
