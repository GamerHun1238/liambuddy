package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Self;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;





















public class AccountManagerImpl
  extends ManagerBase<AccountManager>
  implements AccountManager
{
  protected final SelfUser selfUser;
  protected String name;
  protected Icon avatar;
  
  public AccountManagerImpl(SelfUser selfUser)
  {
    super(selfUser.getJDA(), Route.Self.MODIFY_SELF.compile(new String[0]));
    this.selfUser = selfUser;
  }
  

  @Nonnull
  public SelfUser getSelfUser()
  {
    return selfUser;
  }
  

  @Nonnull
  @CheckReturnValue
  public AccountManagerImpl reset(long fields)
  {
    super.reset(fields);
    if ((fields & 0x2) == 2L)
      avatar = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public AccountManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public AccountManagerImpl reset()
  {
    super.reset();
    avatar = null;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public AccountManagerImpl setName(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 32, "Name");
    this.name = name;
    set |= 1L;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public AccountManagerImpl setAvatar(Icon avatar)
  {
    this.avatar = avatar;
    set |= 0x2;
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject body = DataObject.empty();
    

    body.put("username", getSelfUser().getName());
    body.put("avatar", getSelfUser().getAvatarId());
    
    if (shouldUpdate(1L))
      body.put("username", name);
    if (shouldUpdate(2L)) {
      body.put("avatar", avatar == null ? null : avatar.getEncoding());
    }
    reset();
    return getRequestBody(body);
  }
  

  protected void handleSuccess(Response response, Request<Void> request)
  {
    String newToken = response.getObject().getString("token").replace("Bot ", "");
    api.setToken(newToken);
    request.onSuccess(null);
  }
}
