package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;





















public enum EmbedType
{
  IMAGE("image"), 
  VIDEO("video"), 
  LINK("link"), 
  RICH("rich"), 
  UNKNOWN("");
  
  private final String key;
  
  private EmbedType(String key) {
    this.key = key;
  }
  











  @Nonnull
  public static EmbedType fromKey(String key)
  {
    for (EmbedType type : )
    {
      if (key.equals(key))
        return type;
    }
    return UNKNOWN;
  }
}
