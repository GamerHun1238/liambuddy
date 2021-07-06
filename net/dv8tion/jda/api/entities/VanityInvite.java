package net.dv8tion.jda.api.entities;

import java.util.Objects;
import javax.annotation.Nonnull;






















public class VanityInvite
{
  private final String code;
  private final int uses;
  
  public VanityInvite(@Nonnull String code, int uses)
  {
    this.code = code;
    this.uses = uses;
  }
  





  @Nonnull
  public String getCode()
  {
    return code;
  }
  






  public int getUses()
  {
    return uses;
  }
  





  @Nonnull
  public String getUrl()
  {
    return "https://discord.gg/" + getCode();
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof VanityInvite))
      return false;
    VanityInvite other = (VanityInvite)obj;
    return (uses == uses) && (code.equals(code));
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { code, Integer.valueOf(uses) });
  }
  

  public String toString()
  {
    return "VanityInvite(" + code + ")";
  }
}
