package net.dv8tion.jda.api.utils.cache;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.MiscUtil;














































public abstract interface UnifiedMemberCacheView
  extends CacheView<Member>
{
  @Nonnull
  public abstract List<Member> getElementsById(long paramLong);
  
  @Nonnull
  public List<Member> getElementsById(@Nonnull String id)
  {
    return getElementsById(MiscUtil.parseSnowflake(id));
  }
  













  @Nonnull
  public abstract List<Member> getElementsByUsername(@Nonnull String paramString, boolean paramBoolean);
  













  @Nonnull
  public List<Member> getElementsByUsername(@Nonnull String name)
  {
    return getElementsByUsername(name, false);
  }
  












  @Nonnull
  public abstract List<Member> getElementsByNickname(@Nullable String paramString, boolean paramBoolean);
  












  @Nonnull
  public List<Member> getElementsByNickname(@Nullable String name)
  {
    return getElementsByNickname(name, false);
  }
  
  @Nonnull
  public abstract List<Member> getElementsWithRoles(@Nonnull Role... paramVarArgs);
  
  @Nonnull
  public abstract List<Member> getElementsWithRoles(@Nonnull Collection<Role> paramCollection);
}
