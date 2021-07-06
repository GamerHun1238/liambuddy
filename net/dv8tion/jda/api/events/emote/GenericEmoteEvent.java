package net.dv8tion.jda.api.events.emote;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;



























public abstract class GenericEmoteEvent
  extends Event
{
  protected final Emote emote;
  
  public GenericEmoteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
  {
    super(api, responseNumber);
    this.emote = emote;
  }
  





  @Nonnull
  public Guild getGuild()
  {
    return emote.getGuild();
  }
  





  @Nonnull
  public Emote getEmote()
  {
    return emote;
  }
  





  public boolean isManaged()
  {
    return emote.isManaged();
  }
}
