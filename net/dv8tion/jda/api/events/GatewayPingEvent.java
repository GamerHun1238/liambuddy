package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;























public class GatewayPingEvent
  extends Event
  implements UpdateEvent<JDA, Long>
{
  public static final String IDENTIFIER = "gateway-ping";
  private final long next;
  private final long prev;
  
  public GatewayPingEvent(@Nonnull JDA api, long old)
  {
    super(api);
    next = api.getGatewayPing();
    prev = old;
  }
  





  public long getNewPing()
  {
    return next;
  }
  





  public long getOldPing()
  {
    return prev;
  }
  

  @Nonnull
  public String getPropertyIdentifier()
  {
    return "gateway-ping";
  }
  

  @Nonnull
  public JDA getEntity()
  {
    return getJDA();
  }
  

  @Nonnull
  public Long getOldValue()
  {
    return Long.valueOf(prev);
  }
  

  @Nonnull
  public Long getNewValue()
  {
    return Long.valueOf(next);
  }
  

  public String toString()
  {
    return "GatewayUpdate[ping](" + getOldValue() + "->" + getNewValue() + ')';
  }
}
