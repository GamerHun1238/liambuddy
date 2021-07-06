package net.dv8tion.jda.api.entities;

import java.util.Formattable;
import java.util.Formatter;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;



















































public abstract interface IMentionable
  extends Formattable, ISnowflake
{
  @Nonnull
  public abstract String getAsMention();
  
  public void formatTo(Formatter formatter, int flags, int width, int precision)
  {
    boolean leftJustified = (flags & 0x1) == 1;
    boolean upper = (flags & 0x2) == 2;
    String out = upper ? getAsMention().toUpperCase(formatter.locale()) : getAsMention();
    
    MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
  }
}
