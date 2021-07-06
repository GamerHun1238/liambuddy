package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract interface TemplateManager
  extends Manager<TemplateManager>
{
  public static final long NAME = 1L;
  public static final long DESCRIPTION = 2L;
  
  @Nonnull
  public abstract TemplateManager reset(long paramLong);
  
  @Nonnull
  public abstract TemplateManager reset(long... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public abstract TemplateManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract TemplateManager setDescription(@Nullable String paramString);
}
