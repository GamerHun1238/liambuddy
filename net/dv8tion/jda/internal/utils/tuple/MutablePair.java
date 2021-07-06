package net.dv8tion.jda.internal.utils.tuple;














public class MutablePair<L, R>
  extends Pair<L, R>
{
  public L left;
  












  public R right;
  













  public static <L, R> MutablePair<L, R> of(L left, R right)
  {
    return new MutablePair(left, right);
  }
  






  public MutablePair() {}
  





  public MutablePair(L left, R right)
  {
    this.left = left;
    this.right = right;
  }
  
  public L getLeft()
  {
    return left;
  }
  




  public void setLeft(L left)
  {
    this.left = left;
  }
  
  public R getRight()
  {
    return right;
  }
  




  public void setRight(R right)
  {
    this.right = right;
  }
}
