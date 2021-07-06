package net.dv8tion.jda.api.utils.cache;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.MiscUtil;














































public abstract interface MemberCacheView
  extends SnowflakeCacheView<Member>
{
  @Nullable
  public abstract Member getElementById(long paramLong);
  
  @Nullable
  public Member getElementById(@Nonnull String id)
  {
    return getElementById(MiscUtil.parseSnowflake(id));
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
