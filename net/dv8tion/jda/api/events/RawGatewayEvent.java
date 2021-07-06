package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;




































public class RawGatewayEvent
  extends Event
{
  private final DataObject data;
  
  public RawGatewayEvent(@Nonnull JDA api, long responseNumber, @Nonnull DataObject data)
  {
    super(api, responseNumber);
    this.data = data;
  }
  












  @Nonnull
  public DataObject getPackage()
  {
    return data;
  }
  





  @Nonnull
  public DataObject getPayload()
  {
    return data.getObject("d");
  }
  





  @Nonnull
  public String getType()
  {
    return data.getString("t");
  }
}
