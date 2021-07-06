package net.dv8tion.jda.api.entities;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.internal.utils.Helpers;




























public class MessageSticker
  implements ISnowflake
{
  private final long id;
  private final String name;
  private final String description;
  private final long packId;
  private final String asset;
  private final StickerFormat formatType;
  private final Set<String> tags;
  @Deprecated
  @ForRemoval
  @ReplaceWith("ICON_URL")
  @DeprecatedSince("4.3.1")
  public static final String ASSET_URL = "https://cdn.discordapp.com/stickers/%s/%s.%s";
  public static final String ICON_URL = "https://cdn.discordapp.com/stickers/%s.%s";
  
  public MessageSticker(long id, String name, String description, long packId, String asset, StickerFormat formatType, Set<String> tags)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.packId = packId;
    this.asset = asset;
    this.formatType = formatType;
    this.tags = tags;
  }
  

  public long getIdLong()
  {
    return id;
  }
  





  @Nonnull
  public String getName()
  {
    return name;
  }
  





  @Nonnull
  public String getDescription()
  {
    return description;
  }
  







  @Nonnull
  public String getPackId()
  {
    return Long.toUnsignedString(getPackIdLong());
  }
  







  public long getPackIdLong()
  {
    return packId;
  }
  








  @Nonnull
  @Deprecated
  @ForRemoval
  @ReplaceWith("getIconUrl()")
  @DeprecatedSince("4.3.1")
  public String getAssetHash()
  {
    return asset;
  }
  










  @Nonnull
  @Deprecated
  @ForRemoval
  @ReplaceWith("getIconUrl()")
  @DeprecatedSince("4.3.1")
  public String getAssetUrl()
  {
    return String.format("https://cdn.discordapp.com/stickers/%s/%s.%s", new Object[] { Long.valueOf(id), asset, formatType.getExtension() });
  }
  








  @Nonnull
  public String getIconUrl()
  {
    return Helpers.format("https://cdn.discordapp.com/stickers/%s.%s", new Object[] { getId(), formatType.getExtension() });
  }
  





  @Nonnull
  public StickerFormat getFormatType()
  {
    return formatType;
  }
  





  @Nonnull
  public Set<String> getTags()
  {
    return tags;
  }
  



  public static enum StickerFormat
  {
    PNG(1, "png"), 
    


    APNG(2, "apng"), 
    





    LOTTIE(3, "json"), 
    


    UNKNOWN(-1, null);
    
    private final int id;
    private final String extension;
    
    private StickerFormat(int id, String extension)
    {
      this.id = id;
      this.extension = extension;
    }
    








    @Nonnull
    public String getExtension()
    {
      if (this == UNKNOWN)
        throw new IllegalStateException("Can only get extension of a known format");
      return extension;
    }
    








    @Nonnull
    public static StickerFormat fromId(int id)
    {
      for (StickerFormat stickerFormat : )
      {
        if (id == id)
          return stickerFormat;
      }
      return UNKNOWN;
    }
  }
}
