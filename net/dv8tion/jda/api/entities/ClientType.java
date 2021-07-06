package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;























public enum ClientType
{
  DESKTOP("desktop"), 
  
  MOBILE("mobile"), 
  
  WEB("web"), 
  
  UNKNOWN("unknown");
  

  private final String key;
  
  private ClientType(String key)
  {
    this.key = key;
  }
  





  public String getKey()
  {
    return key;
  }
  








  @Nonnull
  public static ClientType fromKey(@Nonnull String key)
  {
    for (ClientType type : )
    {
      if (key.equals(key))
        return type;
    }
    return UNKNOWN;
  }
}
