package net.dv8tion.jda.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;





















public enum Region
{
  AMSTERDAM("amsterdam", "Amsterdam", "🇳🇱", false), 
  BRAZIL("brazil", "Brazil", "🇧🇷", false), 
  EUROPE("europe", "Europe", "🇪🇺", false), 
  EU_CENTRAL("eu-central", "EU Central", "🇪🇺", false), 
  EU_WEST("eu-west", "EU West", "🇪🇺", false), 
  FRANKFURT("frankfurt", "Frankfurt", "🇩🇪", false), 
  HONG_KONG("hongkong", "Hong Kong", "🇭🇰", false), 
  JAPAN("japan", "Japan", "🇯🇵", false), 
  SOUTH_KOREA("south-korea", "South Korea", "🇰🇷", false), 
  LONDON("london", "London", "🇬🇧", false), 
  RUSSIA("russia", "Russia", "🇷🇺", false), 
  INDIA("india", "India", "🇮🇳", false), 
  SINGAPORE("singapore", "Singapore", "🇸🇬", false), 
  SOUTH_AFRICA("southafrica", "South Africa", "🇿🇦", false), 
  SYDNEY("sydney", "Sydney", "🇦🇺", false), 
  US_CENTRAL("us-central", "US Central", "🇺🇸", false), 
  US_EAST("us-east", "US East", "🇺🇸", false), 
  US_SOUTH("us-south", "US South", "🇺🇸", false), 
  US_WEST("us-west", "US West", "🇺🇸", false), 
  
  VIP_AMSTERDAM("vip-amsterdam", "Amsterdam (VIP)", "🇳🇱", true), 
  VIP_BRAZIL("vip-brazil", "Brazil (VIP)", "🇧🇷", true), 
  VIP_EU_CENTRAL("vip-eu-central", "EU Central (VIP)", "🇪🇺", true), 
  VIP_EU_WEST("vip-eu-west", "EU West (VIP)", "🇪🇺", true), 
  VIP_FRANKFURT("vip-frankfurt", "Frankfurt (VIP)", "🇩🇪", true), 
  VIP_JAPAN("vip-japan", "Japan (VIP)", "🇯🇵", true), 
  VIP_SOUTH_KOREA("vip-south-korea", "South Korea (VIP)", "🇰🇷", true), 
  VIP_LONDON("vip-london", "London (VIP)", "🇬🇧", true), 
  VIP_SINGAPORE("vip-singapore", "Singapore (VIP)", "🇸🇬", true), 
  VIP_SOUTH_AFRICA("vip-southafrica", "South Africa (VIP)", "🇿🇦", true), 
  VIP_SYDNEY("vip-sydney", "Sydney (VIP)", "🇦🇺", true), 
  VIP_US_CENTRAL("vip-us-central", "US Central (VIP)", "🇺🇸", true), 
  VIP_US_EAST("vip-us-east", "US East (VIP)", "🇺🇸", true), 
  VIP_US_SOUTH("vip-us-south", "US South (VIP)", "🇺🇸", true), 
  VIP_US_WEST("vip-us-west", "US West (VIP)", "🇺🇸", true), 
  
  UNKNOWN("", "Unknown Region", null, false), 
  
  AUTOMATIC("automatic", "Automatic", null, false);
  




  public static final Set<Region> VOICE_CHANNEL_REGIONS = Collections.unmodifiableSet(EnumSet.of(AUTOMATIC, new Region[] { US_WEST, US_EAST, US_CENTRAL, US_SOUTH, SINGAPORE, SOUTH_AFRICA, SYDNEY, EUROPE, INDIA, SOUTH_KOREA, BRAZIL, JAPAN, RUSSIA }));
  
  private final String key;
  private final String name;
  private final String emoji;
  private final boolean vip;
  
  private Region(String key, String name, String emoji, boolean vip)
  {
    this.key = key;
    this.name = name;
    this.emoji = emoji;
    this.vip = vip;
  }
  





  @Nonnull
  public String getName()
  {
    return name;
  }
  





  @Nonnull
  public String getKey()
  {
    return key;
  }
  





  @Nonnull
  public String getEmoji()
  {
    return emoji;
  }
  







  public boolean isVip()
  {
    return vip;
  }
  









  @Nonnull
  public static Region fromKey(@Nullable String key)
  {
    for (Region region : )
    {
      if (region.getKey().equals(key))
      {
        return region;
      }
    }
    return UNKNOWN;
  }
  

  public String toString()
  {
    return getName();
  }
}
