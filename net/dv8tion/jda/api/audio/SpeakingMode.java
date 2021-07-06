package net.dv8tion.jda.api.audio;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;




















public enum SpeakingMode
{
  VOICE(1),  SOUNDSHARE(2),  PRIORITY(4);
  
  private final int raw;
  
  private SpeakingMode(int raw)
  {
    this.raw = raw;
  }
  





  public int getRaw()
  {
    return raw;
  }
  








  @Nonnull
  public static EnumSet<SpeakingMode> getModes(int mask)
  {
    EnumSet<SpeakingMode> modes = EnumSet.noneOf(SpeakingMode.class);
    if (mask == 0)
      return modes;
    SpeakingMode[] values = values();
    for (SpeakingMode mode : values)
    {
      if ((raw & mask) == raw)
        modes.add(mode);
    }
    return modes;
  }
  









  public static int getRaw(@Nullable SpeakingMode... modes)
  {
    if ((modes == null) || (modes.length == 0))
      return 0;
    int mask = 0;
    for (SpeakingMode m : modes)
      mask |= raw;
    return mask;
  }
  









  public static int getRaw(@Nullable Collection<SpeakingMode> modes)
  {
    if (modes == null)
      return 0;
    int raw = 0;
    for (SpeakingMode mode : modes)
      raw |= mode.getRaw();
    return raw;
  }
}
