package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;




















public enum ActivityFlag
{
  INSTANCE(0), 
  JOIN(1), 
  SPECTATE(2), 
  JOIN_REQUEST(3), 
  SYNC(4), 
  PLAY(5);
  
  private final int offset;
  private final int raw;
  
  private ActivityFlag(int offset)
  {
    this.offset = offset;
    raw = (1 << offset);
  }
  





  public int getOffset()
  {
    return offset;
  }
  





  public int getRaw()
  {
    return raw;
  }
  











  @Nonnull
  public static EnumSet<ActivityFlag> getFlags(int raw)
  {
    EnumSet<ActivityFlag> set = EnumSet.noneOf(ActivityFlag.class);
    if (raw == 0)
      return set;
    for (ActivityFlag flag : values())
    {
      if ((flag.getRaw() & raw) == flag.getRaw())
        set.add(flag);
    }
    return set;
  }
}
