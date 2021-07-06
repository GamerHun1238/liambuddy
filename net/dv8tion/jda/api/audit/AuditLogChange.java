package net.dv8tion.jda.api.audit;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;























public class AuditLogChange
{
  protected final Object oldValue;
  protected final Object newValue;
  protected final String key;
  
  public AuditLogChange(Object oldValue, Object newValue, String key)
  {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.key = key;
  }
  













  @Nullable
  public <T> T getOldValue()
  {
    return oldValue;
  }
  













  @Nullable
  public <T> T getNewValue()
  {
    return newValue;
  }
  






  @Nonnull
  public String getKey()
  {
    return key;
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { key, oldValue, newValue });
  }
  

  public boolean equals(Object obj)
  {
    if (!(obj instanceof AuditLogChange))
      return false;
    AuditLogChange other = (AuditLogChange)obj;
    return (key.equals(key)) && 
      (Objects.equals(oldValue, oldValue)) && 
      (Objects.equals(newValue, newValue));
  }
  

  public String toString()
  {
    return String.format("ALC:%s(%s -> %s)", new Object[] { key, oldValue, newValue });
  }
}
