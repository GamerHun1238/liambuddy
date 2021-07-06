package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
























public class SelfUpdateVerifiedEvent
  extends GenericSelfUpdateEvent<Boolean>
{
  public static final String IDENTIFIER = "verified";
  
  public SelfUpdateVerifiedEvent(@Nonnull JDA api, long responseNumber, boolean wasVerified)
  {
    super(api, responseNumber, Boolean.valueOf(wasVerified), Boolean.valueOf(!wasVerified), "verified");
  }
  





  public boolean wasVerified()
  {
    return getOldValue().booleanValue();
  }
  

  @Nonnull
  public Boolean getOldValue()
  {
    return (Boolean)super.getOldValue();
  }
  

  @Nonnull
  public Boolean getNewValue()
  {
    return (Boolean)super.getNewValue();
  }
}
