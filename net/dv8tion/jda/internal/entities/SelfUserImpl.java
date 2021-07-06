package net.dv8tion.jda.internal.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.AccountManagerImpl;



















public class SelfUserImpl
  extends UserImpl
  implements SelfUser
{
  protected AccountManager manager;
  private boolean verified;
  private boolean mfaEnabled;
  private long applicationId;
  private String email;
  private String phoneNumber;
  private boolean mobile;
  private boolean nitro;
  
  public SelfUserImpl(long id, JDAImpl api)
  {
    super(id, api);
    applicationId = id;
  }
  

  public boolean hasPrivateChannel()
  {
    return false;
  }
  

  public PrivateChannel getPrivateChannel()
  {
    throw new UnsupportedOperationException("You cannot get a PrivateChannel with yourself (SelfUser)");
  }
  

  @Nonnull
  public RestAction<PrivateChannel> openPrivateChannel()
  {
    throw new UnsupportedOperationException("You cannot open a PrivateChannel with yourself (SelfUser)");
  }
  

  public long getApplicationIdLong()
  {
    return applicationId;
  }
  

  public boolean isVerified()
  {
    return verified;
  }
  

  public boolean isMfaEnabled()
  {
    return mfaEnabled;
  }
  

  public long getAllowedFileSize()
  {
    if (nitro) {
      return 52428800L;
    }
    return 8388608L;
  }
  

  @Nonnull
  public AccountManager getManager()
  {
    if (manager == null)
      return this.manager = new AccountManagerImpl(this);
    return manager;
  }
  
  public SelfUserImpl setVerified(boolean verified)
  {
    this.verified = verified;
    return this;
  }
  
  public SelfUserImpl setMfaEnabled(boolean enabled)
  {
    mfaEnabled = enabled;
    return this;
  }
  
  public SelfUserImpl setEmail(String email)
  {
    this.email = email;
    return this;
  }
  
  public SelfUserImpl setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
    return this;
  }
  
  public SelfUserImpl setMobile(boolean mobile)
  {
    this.mobile = mobile;
    return this;
  }
  
  public SelfUserImpl setNitro(boolean nitro)
  {
    this.nitro = nitro;
    return this;
  }
  
  public SelfUserImpl setApplicationId(long id)
  {
    applicationId = id;
    return this;
  }
  
  public static SelfUserImpl copyOf(SelfUserImpl other, JDAImpl jda)
  {
    SelfUserImpl selfUser = new SelfUserImpl(id, jda);
    selfUser.setName(name)
      .setAvatarId(avatarId)
      .setDiscriminator(other.getDiscriminator())
      .setBot(bot);
    return selfUser
      .setVerified(verified)
      .setMfaEnabled(mfaEnabled)
      .setEmail(email)
      .setPhoneNumber(phoneNumber)
      .setMobile(mobile)
      .setNitro(nitro)
      .setApplicationId(applicationId);
  }
}
