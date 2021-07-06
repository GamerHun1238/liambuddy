package net.dv8tion.jda.api.utils;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.internal.utils.AllowedMentionsImpl;
import net.dv8tion.jda.internal.utils.Checks;











































public abstract interface AllowedMentions<R>
{
  public static void setDefaultMentions(@Nullable Collection<Message.MentionType> allowedMentions)
  {
    AllowedMentionsImpl.setDefaultMentions(allowedMentions);
  }
  






  @Nonnull
  public static EnumSet<Message.MentionType> getDefaultMentions()
  {
    return AllowedMentionsImpl.getDefaultMentions();
  }
  








  public static void setDefaultMentionRepliedUser(boolean mention)
  {
    AllowedMentionsImpl.setDefaultMentionRepliedUser(mention);
  }
  









  public static boolean isDefaultMentionRepliedUser()
  {
    return AllowedMentionsImpl.isDefaultMentionRepliedUser();
  }
  

















  @Nonnull
  @CheckReturnValue
  public abstract R mentionRepliedUser(boolean paramBoolean);
  

















  @Nonnull
  @CheckReturnValue
  public abstract R allowedMentions(@Nullable Collection<Message.MentionType> paramCollection);
  

















  @Nonnull
  @CheckReturnValue
  public abstract R mention(@Nonnull IMentionable... paramVarArgs);
  

















  @Nonnull
  @CheckReturnValue
  public R mention(@Nonnull Collection<? extends IMentionable> mentions)
  {
    Checks.noneNull(mentions, "Mention");
    return mention((IMentionable[])mentions.toArray(new IMentionable[0]));
  }
  



















  @Nonnull
  @CheckReturnValue
  public abstract R mentionUsers(@Nonnull String... paramVarArgs);
  



















  @Nonnull
  @CheckReturnValue
  public R mentionUsers(@Nonnull long... userIds)
  {
    Checks.notNull(userIds, "UserId array");
    String[] stringIds = new String[userIds.length];
    for (int i = 0; i < userIds.length; i++)
    {
      stringIds[i] = Long.toUnsignedString(userIds[i]);
    }
    return mentionUsers(stringIds);
  }
  



















  @Nonnull
  @CheckReturnValue
  public abstract R mentionRoles(@Nonnull String... paramVarArgs);
  



















  @Nonnull
  @CheckReturnValue
  public R mentionRoles(@Nonnull long... roleIds)
  {
    Checks.notNull(roleIds, "RoleId array");
    String[] stringIds = new String[roleIds.length];
    for (int i = 0; i < roleIds.length; i++)
    {
      stringIds[i] = Long.toUnsignedString(roleIds[i]);
    }
    return mentionRoles(stringIds);
  }
}
