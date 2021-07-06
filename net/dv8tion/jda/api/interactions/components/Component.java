package net.dv8tion.jda.api.interactions.components;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.SerializableData;










































public abstract interface Component
  extends SerializableData
{
  @Nonnull
  public abstract Type getType();
  
  @Nullable
  public abstract String getId();
  
  public int getMaxPerRow()
  {
    return getType().getMaxPerRow();
  }
  



  public static enum Type
  {
    UNKNOWN(-1, 0), 
    
    ACTION_ROW(1, 0), 
    
    BUTTON(2, 5), 
    
    SELECTION_MENU(3, 1);
    

    private final int key;
    private final int maxPerRow;
    
    private Type(int key, int maxPerRow)
    {
      this.key = key;
      this.maxPerRow = maxPerRow;
    }
    





    public int getMaxPerRow()
    {
      return maxPerRow;
    }
    








    @Nonnull
    public static Type fromKey(int type)
    {
      for (Type t : )
      {
        if (key == type)
          return t;
      }
      return UNKNOWN;
    }
  }
}
