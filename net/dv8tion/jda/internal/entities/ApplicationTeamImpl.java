package net.dv8tion.jda.internal.entities;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.TeamMember;
















public class ApplicationTeamImpl
  implements ApplicationTeam
{
  private final String iconId;
  private final List<TeamMember> members;
  private final long id;
  private final long ownerId;
  
  public ApplicationTeamImpl(String iconId, List<TeamMember> members, long id, long ownerId)
  {
    this.iconId = iconId;
    this.members = Collections.unmodifiableList(members);
    this.id = id;
    this.ownerId = ownerId;
  }
  

  public long getOwnerIdLong()
  {
    return ownerId;
  }
  

  public String getIconId()
  {
    return iconId;
  }
  

  @Nonnull
  public List<TeamMember> getMembers()
  {
    return members;
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof ApplicationTeamImpl))
      return false;
    ApplicationTeamImpl app = (ApplicationTeamImpl)obj;
    return id == id;
  }
  

  public String toString()
  {
    return "ApplicationTeam(" + getId() + ')';
  }
}
