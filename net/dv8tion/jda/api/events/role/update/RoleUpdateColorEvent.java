package net.dv8tion.jda.api.events.role.update;

import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
























public class RoleUpdateColorEvent
  extends GenericRoleUpdateEvent<Integer>
{
  public static final String IDENTIFIER = "color";
  
  public RoleUpdateColorEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldColor)
  {
    super(api, responseNumber, role, Integer.valueOf(oldColor), Integer.valueOf(role.getColorRaw()), "color");
  }
  





  @Nullable
  public Color getOldColor()
  {
    return ((Integer)previous).intValue() != 536870911 ? new Color(((Integer)previous).intValue()) : null;
  }
  





  public int getOldColorRaw()
  {
    return getOldValue().intValue();
  }
  





  @Nullable
  public Color getNewColor()
  {
    return ((Integer)next).intValue() != 536870911 ? new Color(((Integer)next).intValue()) : null;
  }
  





  public int getNewColorRaw()
  {
    return getNewValue().intValue();
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
