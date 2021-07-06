package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

























public class RoleUpdatePositionEvent
  extends GenericRoleUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "position";
  private final int oldPositionRaw;
  private final int newPositionRaw;
  
  public RoleUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldPosition, int oldPositionRaw)
  {
    super(api, responseNumber, role, Integer.valueOf(oldPosition), Integer.valueOf(role.getPosition()), "position");
    this.oldPositionRaw = oldPositionRaw;
    newPositionRaw = role.getPositionRaw();
  }
  





  public int getOldPosition()
  {
    return getOldValue().intValue();
  }
  





  public int getOldPositionRaw()
  {
    return oldPositionRaw;
  }
  





  public int getNewPosition()
  {
    return getNewValue().intValue();
  }
  





  public int getNewPositionRaw()
  {
    return newPositionRaw;
  }
  

  @Nonnull
  public Integer getOldValue()
  {
    return (Integer)super.getOldValue();
  }
  

  @Nonnull
  public Integer getNewValue()
  {
    return (Integer)super.getNewValue();
  }
}
