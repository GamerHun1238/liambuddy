package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;






























public abstract interface ApplicationTeam
  extends ISnowflake
{
  public static final String ICON_URL = "https://cdn.discordapp.com/team-icons/%s/%s.png";
  
  @Nullable
  public TeamMember getOwner()
  {
    return getMemberById(getOwnerIdLong());
  }
  





  @Nonnull
  public String getOwnerId()
  {
    return Long.toUnsignedString(getOwnerIdLong());
  }
  






  public abstract long getOwnerIdLong();
  






  @Nullable
  public abstract String getIconId();
  





  @Nullable
  public String getIconUrl()
  {
    String iconId = getIconId();
    return iconId == null ? null : String.format("https://cdn.discordapp.com/team-icons/%s/%s.png", new Object[] { getId(), iconId });
  }
  








  @Nonnull
  public abstract List<TeamMember> getMembers();
  








  public boolean isMember(@Nonnull User user)
  {
    return getMember(user) != null;
  }
  












  @Nullable
  public TeamMember getMember(@Nonnull User user)
  {
    Checks.notNull(user, "User");
    return getMemberById(user.getIdLong());
  }
  












  @Nullable
  public TeamMember getMemberById(@Nonnull String userId)
  {
    return getMemberById(MiscUtil.parseSnowflake(userId));
  }
  









  @Nullable
  public TeamMember getMemberById(long userId)
  {
    for (TeamMember member : getMembers())
    {
      if (member.getUser().getIdLong() == userId)
        return member;
    }
    return null;
  }
}
