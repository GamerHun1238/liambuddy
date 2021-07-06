package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;























public enum MessageType
{
  DEFAULT(0, false), 
  



  RECIPIENT_ADD(1), 
  



  RECIPIENT_REMOVE(2), 
  



  CALL(3), 
  



  CHANNEL_NAME_CHANGE(4), 
  



  CHANNEL_ICON_CHANGE(5), 
  



  CHANNEL_PINNED_ADD(6), 
  



  GUILD_MEMBER_JOIN(7), 
  



  GUILD_MEMBER_BOOST(8), 
  



  GUILD_BOOST_TIER_1(9), 
  



  GUILD_BOOST_TIER_2(10), 
  



  GUILD_BOOST_TIER_3(11), 
  



  CHANNEL_FOLLOW_ADD(12), 
  



  GUILD_DISCOVERY_DISQUALIFIED(14), 
  



  GUILD_DISCOVERY_REQUALIFIED(15), 
  



  GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING(16), 
  



  GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING(17), 
  



  INLINE_REPLY(19, false), 
  




  APPLICATION_COMMAND(20, false), 
  



  UNKNOWN(-1);
  
  private final int id;
  private final boolean system;
  
  private MessageType(int id)
  {
    this(id, true);
  }
  
  private MessageType(int id, boolean system)
  {
    this.id = id;
    this.system = system;
  }
  





  public int getId()
  {
    return id;
  }
  







  public boolean isSystem()
  {
    return system;
  }
  









  @Nonnull
  public static MessageType fromId(int id)
  {
    for (MessageType type : )
    {
      if (id == id)
        return type;
    }
    return UNKNOWN;
  }
}
