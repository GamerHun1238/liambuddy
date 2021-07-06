package javax.annotation.meta;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;

public abstract interface TypeQualifierValidator<A extends Annotation>
{
  @Nonnull
  public abstract When forConstantValue(@Nonnull A paramA, Object paramObject);
}
