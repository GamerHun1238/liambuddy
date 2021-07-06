package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Invites;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;













public class InviteActionImpl
  extends AuditableRestActionImpl<Invite>
  implements InviteAction
{
  private Integer maxAge = null;
  private Integer maxUses = null;
  private Boolean temporary = null;
  private Boolean unique = null;
  
  public InviteActionImpl(JDA api, String channelId)
  {
    super(api, Route.Invites.CREATE_INVITE.compile(new String[] { channelId }));
  }
  

  @Nonnull
  public InviteActionImpl setCheck(BooleanSupplier checks)
  {
    return (InviteActionImpl)super.setCheck(checks);
  }
  

  @Nonnull
  public InviteActionImpl timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (InviteActionImpl)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public InviteActionImpl deadline(long timestamp)
  {
    return (InviteActionImpl)super.deadline(timestamp);
  }
  

  @Nonnull
  @CheckReturnValue
  public InviteActionImpl setMaxAge(Integer maxAge)
  {
    if (maxAge != null) {
      Checks.notNegative(maxAge.intValue(), "maxAge");
    }
    this.maxAge = maxAge;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public InviteActionImpl setMaxAge(Long maxAge, @Nonnull TimeUnit timeUnit)
  {
    if (maxAge == null) {
      return setMaxAge(null);
    }
    Checks.notNegative(maxAge.longValue(), "maxAge");
    Checks.notNull(timeUnit, "timeUnit");
    
    return setMaxAge(Integer.valueOf(Math.toIntExact(timeUnit.toSeconds(maxAge.longValue()))));
  }
  

  @Nonnull
  @CheckReturnValue
  public InviteActionImpl setMaxUses(Integer maxUses)
  {
    if (maxUses != null) {
      Checks.notNegative(maxUses.intValue(), "maxUses");
    }
    this.maxUses = maxUses;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public InviteActionImpl setTemporary(Boolean temporary)
  {
    this.temporary = temporary;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public InviteActionImpl setUnique(Boolean unique)
  {
    this.unique = unique;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject object = DataObject.empty();
    
    if (maxAge != null)
      object.put("max_age", maxAge);
    if (maxUses != null)
      object.put("max_uses", maxUses);
    if (temporary != null)
      object.put("temporary", temporary);
    if (unique != null) {
      object.put("unique", unique);
    }
    return getRequestBody(object);
  }
  

  protected void handleSuccess(Response response, Request<Invite> request)
  {
    request.onSuccess(api.getEntityBuilder().createInvite(response.getObject()));
  }
}
