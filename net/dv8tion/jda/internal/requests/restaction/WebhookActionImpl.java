package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Channels;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

















public class WebhookActionImpl
  extends AuditableRestActionImpl<Webhook>
  implements WebhookAction
{
  protected final TextChannel channel;
  protected String name;
  protected Icon avatar = null;
  
  public WebhookActionImpl(JDA api, TextChannel channel, String name)
  {
    super(api, Route.Channels.CREATE_WEBHOOK.compile(new String[] { channel.getId() }));
    this.channel = channel;
    this.name = name;
  }
  

  @Nonnull
  public WebhookActionImpl setCheck(BooleanSupplier checks)
  {
    return (WebhookActionImpl)super.setCheck(checks);
  }
  

  @Nonnull
  public WebhookActionImpl timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (WebhookActionImpl)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public WebhookActionImpl deadline(long timestamp)
  {
    return (WebhookActionImpl)super.deadline(timestamp);
  }
  

  @Nonnull
  public TextChannel getChannel()
  {
    return channel;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookActionImpl setName(@Nonnull String name)
  {
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 100, "Name");
    
    this.name = name;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookActionImpl setAvatar(Icon icon)
  {
    avatar = icon;
    return this;
  }
  

  public RequestBody finalizeData()
  {
    DataObject object = DataObject.empty();
    object.put("name", name);
    object.put("avatar", avatar != null ? avatar.getEncoding() : null);
    
    return getRequestBody(object);
  }
  

  protected void handleSuccess(Response response, Request<Webhook> request)
  {
    DataObject json = response.getObject();
    Webhook webhook = api.getEntityBuilder().createWebhook(json);
    
    request.onSuccess(webhook);
  }
}
