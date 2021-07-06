package net.dv8tion.jda.api.entities;

import java.awt.Color;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;












































































































































































public abstract interface Role
  extends IMentionable, IPermissionHolder, Comparable<Role>
{
  public static final int DEFAULT_COLOR_RAW = 536870911;
  
  public abstract int getPosition();
  
  public abstract int getPositionRaw();
  
  @Nonnull
  public abstract String getName();
  
  public abstract boolean isManaged();
  
  public abstract boolean isHoisted();
  
  public abstract boolean isMentionable();
  
  public abstract long getPermissionsRaw();
  
  @Nullable
  public abstract Color getColor();
  
  public abstract int getColorRaw();
  
  public abstract boolean isPublicRole();
  
  public abstract boolean canInteract(@Nonnull Role paramRole);
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  @CheckReturnValue
  public abstract RoleAction createCopy(@Nonnull Guild paramGuild);
  
  @Nonnull
  @CheckReturnValue
  public RoleAction createCopy()
  {
    return createCopy(getGuild());
  }
  












  @Nonnull
  public abstract RoleManager getManager();
  












  @Nonnull
  @CheckReturnValue
  public abstract AuditableRestAction<Void> delete();
  











  @Nonnull
  public abstract JDA getJDA();
  











  @Nonnull
  public abstract RoleTags getTags();
  











  public static abstract interface RoleTags
  {
    public abstract boolean isBot();
    











    public abstract long getBotIdLong();
    











    @Nullable
    public String getBotId()
    {
      return isBot() ? Long.toUnsignedString(getBotIdLong()) : null;
    }
    







    public abstract boolean isBoost();
    






    public abstract boolean isIntegration();
    






    public abstract long getIntegrationIdLong();
    






    @Nullable
    public String getIntegrationId()
    {
      return isIntegration() ? Long.toUnsignedString(getIntegrationIdLong()) : null;
    }
  }
}
