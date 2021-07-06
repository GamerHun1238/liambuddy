package net.dv8tion.jda.internal.managers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.StageInstance.PrivacyLevel;
import net.dv8tion.jda.api.managers.StageInstanceManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.StageInstances;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;














public class StageInstanceManagerImpl
  extends ManagerBase<StageInstanceManager>
  implements StageInstanceManager
{
  private final StageInstance instance;
  private String topic;
  private StageInstance.PrivacyLevel privacyLevel;
  
  public StageInstanceManagerImpl(StageInstance instance)
  {
    super(instance.getChannel().getJDA(), Route.StageInstances.UPDATE_INSTANCE.compile(new String[] { instance.getChannel().getId() }));
    this.instance = instance;
  }
  

  @Nonnull
  public StageInstance getStageInstance()
  {
    return instance;
  }
  

  @Nonnull
  public StageInstanceManager setTopic(@Nullable String topic)
  {
    if (topic != null)
    {
      topic = topic.trim();
      Checks.notLonger(topic, 120, "Topic");
      if (topic.isEmpty())
        topic = null;
    }
    this.topic = topic;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  public StageInstanceManager setPrivacyLevel(@Nonnull StageInstance.PrivacyLevel level)
  {
    Checks.notNull(level, "PrivacyLevel");
    Checks.check(level != StageInstance.PrivacyLevel.UNKNOWN, "PrivacyLevel must not be UNKNOWN!");
    privacyLevel = level;
    set |= 0x2;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject body = DataObject.empty();
    if ((shouldUpdate(1L)) && (topic != null))
      body.put("topic", topic);
    if (shouldUpdate(2L))
      body.put("privacy_level", Integer.valueOf(privacyLevel.getKey()));
    return getRequestBody(body);
  }
}
