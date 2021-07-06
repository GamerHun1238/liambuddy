package net.dv8tion.jda.api;






















public enum OnlineStatus
{
  ONLINE("online"), 
  


  IDLE("idle"), 
  



  DO_NOT_DISTURB("dnd"), 
  





  INVISIBLE("invisible"), 
  


  OFFLINE("offline"), 
  


  UNKNOWN("");
  
  private final String key;
  
  private OnlineStatus(String key)
  {
    this.key = key;
  }
  







  public String getKey()
  {
    return key;
  }
  









  public static OnlineStatus fromKey(String key)
  {
    for (OnlineStatus onlineStatus : )
    {
      if (key.equalsIgnoreCase(key))
      {
        return onlineStatus;
      }
    }
    return UNKNOWN;
  }
}
