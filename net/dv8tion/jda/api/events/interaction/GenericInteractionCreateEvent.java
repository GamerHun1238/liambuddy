package net.dv8tion.jda.api.events.interaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
























@Incubating
public class GenericInteractionCreateEvent
  extends Event
  implements Interaction
{
  private final Interaction interaction;
  
  public GenericInteractionCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Interaction interaction)
  {
    super(api, responseNumber);
    this.interaction = interaction;
  }
  






  @Nonnull
  public Interaction getInteraction()
  {
    return interaction;
  }
  

  @Nonnull
  public String getToken()
  {
    return interaction.getToken();
  }
  

  public int getTypeRaw()
  {
    return interaction.getTypeRaw();
  }
  

  @Nullable
  public Guild getGuild()
  {
    return interaction.getGuild();
  }
  

  @Nullable
  public AbstractChannel getChannel()
  {
    return interaction.getChannel();
  }
  

  @Nonnull
  public InteractionHook getHook()
  {
    return interaction.getHook();
  }
  

  @Nullable
  public Member getMember()
  {
    return interaction.getMember();
  }
  

  @Nonnull
  public User getUser()
  {
    return interaction.getUser();
  }
  

  public long getIdLong()
  {
    return interaction.getIdLong();
  }
  

  public boolean isAcknowledged()
  {
    return interaction.isAcknowledged();
  }
  

  @Nonnull
  public ReplyAction deferReply()
  {
    return interaction.deferReply();
  }
}
