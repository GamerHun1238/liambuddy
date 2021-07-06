package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public abstract interface AuditLogPaginationAction
  extends PaginationAction<AuditLogEntry, AuditLogPaginationAction>
{
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract AuditLogPaginationAction type(@Nullable ActionType paramActionType);
  
  @Nonnull
  public abstract AuditLogPaginationAction user(@Nullable User paramUser);
  
  @Nonnull
  public abstract AuditLogPaginationAction user(@Nullable String paramString);
  
  @Nonnull
  public abstract AuditLogPaginationAction user(long paramLong);
}
