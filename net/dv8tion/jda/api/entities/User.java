package net.dv8tion.jda.api.entities;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.UserById;
import net.dv8tion.jda.internal.utils.Checks;




























































public abstract interface User
  extends IMentionable
{
  public static final Pattern USER_TAG = Pattern.compile("(.{2,32})#(\\d{4})");
  





  public static final String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";
  




  public static final String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%s.png";
  





  @Nonnull
  public static User fromId(long id)
  {
    return new UserById(id);
  }
  
















  @Nonnull
  public static User fromId(@Nonnull String id)
  {
    return fromId(MiscUtil.parseSnowflake(id));
  }
  









  @Nonnull
  public abstract String getName();
  









  @Nonnull
  public abstract String getDiscriminator();
  









  @Nullable
  public abstract String getAvatarId();
  









  @Nullable
  public String getAvatarUrl()
  {
    String avatarId = getAvatarId();
    return avatarId == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s.%s", new Object[] { getId(), avatarId, avatarId.startsWith("a_") ? "gif" : "png" });
  }
  








  @Nonnull
  public abstract String getDefaultAvatarId();
  








  @Nonnull
  public String getDefaultAvatarUrl()
  {
    return String.format("https://cdn.discordapp.com/embed/avatars/%s.png", new Object[] { getDefaultAvatarId() });
  }
  










  @Nonnull
  public String getEffectiveAvatarUrl()
  {
    String avatarUrl = getAvatarUrl();
    return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
  }
  











  @Nonnull
  public abstract String getAsTag();
  











  public abstract boolean hasPrivateChannel();
  











  @Nonnull
  @CheckReturnValue
  public abstract RestAction<PrivateChannel> openPrivateChannel();
  










  @Nonnull
  public abstract List<Guild> getMutualGuilds();
  










  public abstract boolean isBot();
  










  public abstract boolean isSystem();
  










  @Nonnull
  public abstract JDA getJDA();
  










  @Nonnull
  public abstract EnumSet<UserFlag> getFlags();
  










  public abstract int getFlagsRaw();
  










  public static enum UserFlag
  {
    STAFF(0, "Discord Employee"), 
    PARTNER(1, "Partnered Server Owner"), 
    HYPESQUAD(2, "HypeSquad Events"), 
    BUG_HUNTER_LEVEL_1(3, "Bug Hunter Level 1"), 
    

    HYPESQUAD_BRAVERY(6, "HypeSquad Bravery"), 
    HYPESQUAD_BRILLIANCE(7, "HypeSquad Brilliance"), 
    HYPESQUAD_BALANCE(8, "HypeSquad Balance"), 
    
    EARLY_SUPPORTER(9, "Early Supporter"), 
    TEAM_USER(10, "Team User"), 
    SYSTEM(12, "System User"), 
    



    BUG_HUNTER_LEVEL_2(14, "Bug Hunter Level 2"), 
    VERIFIED_BOT(16, "Verified Bot"), 
    VERIFIED_DEVELOPER(17, "Early Verified Bot Developer"), 
    CERTIFIED_MODERATOR(18, "Discord Certified Moderator"), 
    
    UNKNOWN(-1, "Unknown");
    



    public static final UserFlag[] EMPTY_FLAGS = new UserFlag[0];
    
    private final int offset;
    private final int raw;
    private final String name;
    
    private UserFlag(int offset, @Nonnull String name)
    {
      this.offset = offset;
      raw = (1 << offset);
      this.name = name;
    }
    





    @Nonnull
    public String getName()
    {
      return name;
    }
    





    public int getOffset()
    {
      return offset;
    }
    






    public int getRawValue()
    {
      return raw;
    }
    










    @Nonnull
    public static UserFlag getFromOffset(int offset)
    {
      for (UserFlag flag : )
      {
        if (offset == offset)
          return flag;
      }
      return UNKNOWN;
    }
    









    @Nonnull
    public static EnumSet<UserFlag> getFlags(int flags)
    {
      EnumSet<UserFlag> foundFlags = EnumSet.noneOf(UserFlag.class);
      
      if (flags == 0) {
        return foundFlags;
      }
      for (UserFlag flag : values())
      {
        if ((flag != UNKNOWN) && ((flags & raw) == raw)) {
          foundFlags.add(flag);
        }
      }
      return foundFlags;
    }
    











    public static int getRaw(@Nonnull UserFlag... flags)
    {
      Checks.noneNull(flags, "UserFlags");
      
      int raw = 0;
      for (UserFlag flag : flags)
      {
        if ((flag != null) && (flag != UNKNOWN)) {
          raw |= raw;
        }
      }
      return raw;
    }
    















    public static int getRaw(@Nonnull Collection<UserFlag> flags)
    {
      Checks.notNull(flags, "Flag Collection");
      
      return getRaw((UserFlag[])flags.toArray(EMPTY_FLAGS));
    }
  }
}
