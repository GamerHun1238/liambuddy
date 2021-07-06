package net.dv8tion.jda.internal.handle;

import java.util.Objects;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.SelfUserImpl;

















public class UserUpdateHandler
  extends SocketHandler
{
  public UserUpdateHandler(JDAImpl api)
  {
    super(api);
  }
  

  protected Long handleInternally(DataObject content)
  {
    SelfUserImpl self = (SelfUserImpl)getJDA().getSelfUser();
    
    String name = content.getString("username");
    String discriminator = content.getString("discriminator");
    String avatarId = content.getString("avatar", null);
    Boolean verified = content.hasKey("verified") ? Boolean.valueOf(content.getBoolean("verified")) : null;
    Boolean mfaEnabled = content.hasKey("mfa_enabled") ? Boolean.valueOf(content.getBoolean("mfa_enabled")) : null;
    

    String email = content.getString("email", null);
    Boolean mobile = content.hasKey("mobile") ? Boolean.valueOf(content.getBoolean("mobile")) : null;
    Boolean nitro = content.hasKey("premium") ? Boolean.valueOf(content.getBoolean("premium")) : null;
    String phoneNumber = content.getString("phone", null);
    
    if ((!Objects.equals(name, self.getName())) || (!Objects.equals(discriminator, self.getDiscriminator())))
    {
      String oldName = self.getName();
      self.setName(name);
      getJDA().handleEvent(new SelfUpdateNameEvent(
      
        getJDA(), responseNumber, oldName));
    }
    

    if (!Objects.equals(avatarId, self.getAvatarId()))
    {
      String oldAvatarId = self.getAvatarId();
      self.setAvatarId(avatarId);
      getJDA().handleEvent(new SelfUpdateAvatarEvent(
      
        getJDA(), responseNumber, oldAvatarId));
    }
    

    if ((verified != null) && (verified.booleanValue() != self.isVerified()))
    {
      boolean wasVerified = self.isVerified();
      self.setVerified(verified.booleanValue());
      getJDA().handleEvent(new SelfUpdateVerifiedEvent(
      
        getJDA(), responseNumber, wasVerified));
    }
    

    if ((mfaEnabled != null) && (mfaEnabled.booleanValue() != self.isMfaEnabled()))
    {
      boolean wasMfaEnabled = self.isMfaEnabled();
      self.setMfaEnabled(mfaEnabled.booleanValue());
      getJDA().handleEvent(new SelfUpdateMFAEvent(
      
        getJDA(), responseNumber, wasMfaEnabled));
    }
    
    return null;
  }
}
