package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.managers.AccountManager;


































public abstract interface SelfUser
  extends User
{
  public abstract long getApplicationIdLong();
  
  @Nonnull
  public String getApplicationId()
  {
    return Long.toUnsignedString(getApplicationIdLong());
  }
  
  public abstract boolean isVerified();
  
  public abstract boolean isMfaEnabled();
  
  public abstract long getAllowedFileSize();
  
  @Nonnull
  public abstract AccountManager getManager();
}
