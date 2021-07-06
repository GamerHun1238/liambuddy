package net.dv8tion.jda.internal.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.api.entities.TeamMember.MembershipState;
import net.dv8tion.jda.api.entities.User;
















public class TeamMemberImpl
  implements TeamMember
{
  private final User user;
  private final TeamMember.MembershipState state;
  private final long teamId;
  
  public TeamMemberImpl(User user, TeamMember.MembershipState state, long teamId)
  {
    this.user = user;
    this.state = state;
    this.teamId = teamId;
  }
  

  @Nonnull
  public User getUser()
  {
    return user;
  }
  

  @Nonnull
  public TeamMember.MembershipState getMembershipState()
  {
    return state;
  }
  

  public long getTeamIdLong()
  {
    return teamId;
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { user, Long.valueOf(teamId) });
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof TeamMemberImpl))
      return false;
    TeamMemberImpl member = (TeamMemberImpl)obj;
    return (teamId == teamId) && (user.equals(user));
  }
  

  public String toString()
  {
    return "TeamMember(" + getTeamId() + ", " + user + ")";
  }
}
