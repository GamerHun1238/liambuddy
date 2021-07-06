package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;






















public enum ChannelType
{
  TEXT(0, 0, true), 
  


  PRIVATE(1, -1), 
  


  VOICE(2, 1, true), 
  


  GROUP(3, -1), 
  


  CATEGORY(4, 2, true), 
  


  STORE(6, 0, true), 
  


  STAGE(13, 1, true), 
  



  UNKNOWN(-1, -2);
  
  private final int sortBucket;
  private final int id;
  private final boolean isGuild;
  
  private ChannelType(int id, int sortBucket)
  {
    this(id, sortBucket, false);
  }
  
  private ChannelType(int id, int sortBucket, boolean isGuild)
  {
    this.id = id;
    this.sortBucket = sortBucket;
    this.isGuild = isGuild;
  }
  





  public int getSortBucket()
  {
    return sortBucket;
  }
  





  public int getId()
  {
    return id;
  }
  





  public boolean isGuild()
  {
    return isGuild;
  }
  





  public boolean isAudio()
  {
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$ChannelType[ordinal()])
    {
    case 1: 
    case 2: 
      return true;
    }
    return false;
  }
  






  public boolean isMessage()
  {
    switch (1.$SwitchMap$net$dv8tion$jda$api$entities$ChannelType[ordinal()])
    {

    case 3: 
    case 4: 
    case 5: 
      return true; }
    
    return false;
  }
  









  @Nonnull
  public static ChannelType fromId(int id)
  {
    if (id == 5)
      return TEXT;
    for (ChannelType type : values())
    {
      if (id == id)
        return type;
    }
    return UNKNOWN;
  }
  








  @Nonnull
  public static EnumSet<ChannelType> fromSortBucket(int bucket)
  {
    EnumSet<ChannelType> types = EnumSet.noneOf(ChannelType.class);
    for (ChannelType type : values())
    {
      if (type.getSortBucket() == bucket)
        types.add(type);
    }
    return types;
  }
}
