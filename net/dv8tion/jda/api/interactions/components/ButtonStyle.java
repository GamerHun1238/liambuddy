package net.dv8tion.jda.api.interactions.components;

import javax.annotation.Nonnull;


























public enum ButtonStyle
{
  UNKNOWN(-1), 
  
  PRIMARY(1), 
  
  SECONDARY(2), 
  
  SUCCESS(3), 
  
  DANGER(4), 
  
  LINK(5);
  

  private final int key;
  
  private ButtonStyle(int key)
  {
    this.key = key;
  }
  





  public int getKey()
  {
    return key;
  }
  








  @Nonnull
  public static ButtonStyle fromKey(int key)
  {
    for (ButtonStyle style : )
    {
      if (key == key)
        return style;
    }
    return UNKNOWN;
  }
}
