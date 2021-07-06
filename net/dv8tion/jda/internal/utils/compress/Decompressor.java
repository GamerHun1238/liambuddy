package net.dv8tion.jda.internal.utils.compress;

import java.util.zip.DataFormatException;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;


















public abstract interface Decompressor
{
  public static final Logger LOG = JDALogger.getLog(Decompressor.class);
  
  public abstract Compression getType();
  
  public abstract void reset();
  
  public abstract void shutdown();
  
  @Nullable
  public abstract byte[] decompress(byte[] paramArrayOfByte)
    throws DataFormatException;
}
