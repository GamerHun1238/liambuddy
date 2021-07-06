package net.dv8tion.jda.internal.utils.tuple;

import java.io.Serializable;
import java.util.Objects;









































public abstract class Pair<L, R>
  implements Serializable
{
  public Pair() {}
  
  public static <L, R> Pair<L, R> of(L left, R right)
  {
    return new ImmutablePair(left, right);
  }
  








  public abstract L getLeft();
  







  public abstract R getRight();
  







  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if ((obj instanceof Pair)) {
      Pair<?, ?> other = (Pair)obj;
      return (Objects.equals(getLeft(), other.getLeft())) && 
        (Objects.equals(getRight(), other.getRight()));
    }
    return false;
  }
  







  public int hashCode()
  {
    return Objects.hashCode(getLeft()) ^ Objects.hashCode(getRight());
  }
  





  public String toString()
  {
    return "(" + getLeft() + ',' + getRight() + ')';
  }
}
