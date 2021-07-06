package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.StageInstance.PrivacyLevel;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.StageInstances;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;












public class StageInstanceActionImpl
  extends RestActionImpl<StageInstance>
  implements StageInstanceAction
{
  private final StageChannel channel;
  private String topic;
  private StageInstance.PrivacyLevel level = StageInstance.PrivacyLevel.GUILD_ONLY;
  
  public StageInstanceActionImpl(StageChannel channel)
  {
    super(channel.getJDA(), Route.StageInstances.CREATE_INSTANCE.compile(new String[0]));
    this.channel = channel;
  }
  

  @Nonnull
  public StageInstanceAction setCheck(BooleanSupplier checks)
  {
    return (StageInstanceAction)super.setCheck(checks);
  }
  

  @Nonnull
  public StageInstanceAction timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (StageInstanceAction)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public StageInstanceAction deadline(long timestamp)
  {
    return (StageInstanceAction)super.deadline(timestamp);
  }
  

  @Nonnull
  public StageInstanceAction setTopic(@Nonnull String topic)
  {
    Checks.notBlank(topic, "Topic");
    Checks.notLonger(topic, 120, "Topic");
    this.topic = topic;
    return this;
  }
  

  @Nonnull
  public StageInstanceAction setPrivacyLevel(@Nonnull StageInstance.PrivacyLevel level)
  {
    Checks.notNull(level, "PrivacyLevel");
    Checks.check(level != StageInstance.PrivacyLevel.UNKNOWN, "The PrivacyLevel must not be UNKNOWN!");
    this.level = level;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject body = DataObject.empty();
    body.put("channel_id", channel.getId());
    body.put("topic", topic);
    body.put("privacy_level", Integer.valueOf(level.getKey()));
    return getRequestBody(body);
  }
  

  protected void handleSuccess(Response response, Request<StageInstance> request)
  {
    StageInstance instance = api.getEntityBuilder().createStageInstance((GuildImpl)channel.getGuild(), response.getObject());
    request.onSuccess(instance);
  }
}
