package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;









































public abstract interface TeamMember
{
  @Nonnull
  public abstract User getUser();
  
  @Nonnull
  public abstract MembershipState getMembershipState();
  
  @Nonnull
  public String getTeamId()
  {
    return Long.toUnsignedString(getTeamIdLong());
  }
  





  public abstract long getTeamIdLong();
  




  public static enum MembershipState
  {
    INVITED(1), 
    
    ACCEPTED(2), 
    
    UNKNOWN(-1);
    
    private final int key;
    
    private MembershipState(int key)
    {
      this.key = key;
    }
    





    public int getKey()
    {
      return key;
    }
    








    @Nonnull
    public static MembershipState fromKey(int key)
    {
      for (MembershipState state : )
      {
        if (key == key)
          return state;
      }
      return UNKNOWN;
    }
  }
}
