package net.dv8tion.jda.internal.requests.restaction;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;

















public class PermOverrideData
  implements SerializableData
{
  public static final int ROLE_TYPE = 0;
  public static final int MEMBER_TYPE = 1;
  public final int type;
  public final long id;
  public final long allow;
  public final long deny;
  
  public PermOverrideData(int type, long id, long allow, long deny)
  {
    this.type = type;
    this.id = id;
    this.allow = allow;
    this.deny = (deny & (allow ^ 0xFFFFFFFFFFFFFFFF));
  }
  
  public PermOverrideData(PermissionOverride override)
  {
    id = override.getIdLong();
    type = (override.isMemberOverride() ? 1 : 0);
    allow = override.getAllowedRaw();
    deny = override.getDeniedRaw();
  }
  

  @Nonnull
  public DataObject toData()
  {
    DataObject o = DataObject.empty();
    o.put("type", Integer.valueOf(type));
    o.put("id", Long.valueOf(id));
    o.put("allow", Long.valueOf(allow));
    o.put("deny", Long.valueOf(deny));
    return o;
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof PermOverrideData)) {
      return false;
    }
    PermOverrideData other = (PermOverrideData)obj;
    return id == id;
  }
}
