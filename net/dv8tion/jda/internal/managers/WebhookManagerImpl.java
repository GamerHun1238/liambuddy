package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Webhooks;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;




















public class WebhookManagerImpl
  extends ManagerBase<WebhookManager>
  implements WebhookManager
{
  protected final Webhook webhook;
  protected String name;
  protected String channel;
  protected Icon avatar;
  
  public WebhookManagerImpl(Webhook webhook)
  {
    super(webhook.getJDA(), Route.Webhooks.MODIFY_WEBHOOK.compile(new String[] { webhook.getId() }));
    this.webhook = webhook;
    if (isPermissionChecksEnabled()) {
      checkPermissions();
    }
  }
  
  @Nonnull
  public Webhook getWebhook()
  {
    return webhook;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl reset(long fields)
  {
    super.reset(fields);
    if ((fields & 1L) == 1L)
      name = null;
    if ((fields & 0x2) == 2L)
      channel = null;
    if ((fields & 0x4) == 4L)
      avatar = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl reset()
  {
    super.reset();
    name = null;
    channel = null;
    avatar = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl setName(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    this.name = name;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl setAvatar(Icon icon)
  {
    avatar = icon;
    set |= 0x4;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public WebhookManagerImpl setChannel(@Nonnull TextChannel channel)
  {
    Checks.notNull(channel, "Channel");
    Checks.check(channel.getGuild().equals(getGuild()), "Channel is not from the same guild");
    this.channel = channel.getId();
    set |= 0x2;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject data = DataObject.empty();
    if (shouldUpdate(1L))
      data.put("name", name);
    if (shouldUpdate(2L))
      data.put("channel_id", channel);
    if (shouldUpdate(4L)) {
      data.put("avatar", avatar == null ? null : avatar.getEncoding());
    }
    return getRequestBody(data);
  }
  

  protected boolean checkPermissions()
  {
    Member selfMember = getGuild().getSelfMember();
    TextChannel channel = getChannel();
    if (!selfMember.hasAccess(channel))
      throw new MissingAccessException(channel, Permission.VIEW_CHANNEL);
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.MANAGE_WEBHOOKS }))
      throw new InsufficientPermissionException(channel, Permission.MANAGE_WEBHOOKS);
    return super.checkPermissions();
  }
}
