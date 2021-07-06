package net.dv8tion.jda.api.events.stage;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StageInstance;






















public class StageInstanceCreateEvent
  extends GenericStageInstanceEvent
{
  public StageInstanceCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull StageInstance stageInstance)
  {
    super(api, responseNumber, stageInstance);
  }
}
