package net.dv8tion.jda.api.utils.cache;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.GatewayIntent;































public enum CacheFlag
{
  ACTIVITY(GatewayIntent.GUILD_PRESENCES), 
  





  VOICE_STATE(GatewayIntent.GUILD_VOICE_STATES), 
  




  EMOTE(GatewayIntent.GUILD_EMOJIS), 
  




  CLIENT_STATUS(GatewayIntent.GUILD_PRESENCES), 
  


  MEMBER_OVERRIDES, 
  


  ROLE_TAGS, 
  







  ONLINE_STATUS(GatewayIntent.GUILD_PRESENCES);
  

  private static final EnumSet<CacheFlag> privileged = EnumSet.of(ACTIVITY, CLIENT_STATUS, ONLINE_STATUS);
  private final GatewayIntent requiredIntent;
  
  private CacheFlag()
  {
    this(null);
  }
  
  private CacheFlag(GatewayIntent requiredIntent)
  {
    this.requiredIntent = requiredIntent;
  }
  





  @Nullable
  public GatewayIntent getRequiredIntent()
  {
    return requiredIntent;
  }
  





  public boolean isPresence()
  {
    return requiredIntent == GatewayIntent.GUILD_PRESENCES;
  }
  





  @Nonnull
  public static EnumSet<CacheFlag> getPrivileged()
  {
    return EnumSet.copyOf(privileged);
  }
}
