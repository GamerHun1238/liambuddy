package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public abstract interface ReactionPaginationAction
  extends PaginationAction<User, ReactionPaginationAction>
{
  @Nonnull
  public abstract MessageReaction getReaction();
}
