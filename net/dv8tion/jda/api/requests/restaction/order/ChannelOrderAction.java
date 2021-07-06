package net.dv8tion.jda.api.requests.restaction.order;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;





















































public abstract interface ChannelOrderAction
  extends OrderAction<GuildChannel, ChannelOrderAction>
{
  @Nonnull
  public abstract Guild getGuild();
  
  public abstract int getSortBucket();
  
  @Nonnull
  public EnumSet<ChannelType> getChannelTypes()
  {
    return ChannelType.fromSortBucket(getSortBucket());
  }
}
