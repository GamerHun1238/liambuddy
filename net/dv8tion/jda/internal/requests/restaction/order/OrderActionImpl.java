package net.dv8tion.jda.internal.requests.restaction.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.order.OrderAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.utils.Checks;


















public abstract class OrderActionImpl<T, M extends OrderAction<T, M>>
  extends RestActionImpl<Void>
  implements OrderAction<T, M>
{
  protected final List<T> orderList;
  protected final boolean ascendingOrder;
  protected int selectedPosition = -1;
  










  public OrderActionImpl(JDA api, Route.CompiledRoute route)
  {
    this(api, true, route);
  }
  












  public OrderActionImpl(JDA api, boolean ascendingOrder, Route.CompiledRoute route)
  {
    super(api, route);
    orderList = new ArrayList();
    this.ascendingOrder = ascendingOrder;
  }
  


  @Nonnull
  public M setCheck(BooleanSupplier checks)
  {
    return (OrderAction)super.setCheck(checks);
  }
  


  @Nonnull
  public M timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (OrderAction)super.timeout(timeout, unit);
  }
  


  @Nonnull
  public M deadline(long timestamp)
  {
    return (OrderAction)super.deadline(timestamp);
  }
  

  public boolean isAscendingOrder()
  {
    return ascendingOrder;
  }
  

  @Nonnull
  public List<T> getCurrentOrder()
  {
    return Collections.unmodifiableList(orderList);
  }
  


  @Nonnull
  public M selectPosition(int selectedPosition)
  {
    Checks.notNegative(selectedPosition, "Provided selectedPosition");
    Checks.check(selectedPosition < orderList.size(), "Provided selectedPosition is too big and is out of bounds. selectedPosition: " + selectedPosition);
    
    this.selectedPosition = selectedPosition;
    
    return this;
  }
  

  @Nonnull
  public M selectPosition(@Nonnull T selectedEntity)
  {
    Checks.notNull(selectedEntity, "Channel");
    validateInput(selectedEntity);
    
    return selectPosition(orderList.indexOf(selectedEntity));
  }
  

  public int getSelectedPosition()
  {
    return selectedPosition;
  }
  

  @Nonnull
  public T getSelectedEntity()
  {
    if (selectedPosition == -1) {
      throw new IllegalStateException("No position has been selected yet");
    }
    return orderList.get(selectedPosition);
  }
  

  @Nonnull
  public M moveUp(int amount)
  {
    Checks.notNegative(amount, "Provided amount");
    if (selectedPosition == -1)
      throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
    if (ascendingOrder)
    {
      Checks.check(selectedPosition - amount >= 0, "Amount provided to move up is too large and would be out of bounds.Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList
      
        .size());
    }
    else
    {
      Checks.check(selectedPosition + amount < orderList.size(), "Amount provided to move up is too large and would be out of bounds.Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList
      
        .size());
    }
    
    if (ascendingOrder) {
      return moveTo(selectedPosition - amount);
    }
    return moveTo(selectedPosition + amount);
  }
  

  @Nonnull
  public M moveDown(int amount)
  {
    Checks.notNegative(amount, "Provided amount");
    if (selectedPosition == -1) {
      throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
    }
    if (ascendingOrder)
    {
      Checks.check(selectedPosition + amount < orderList.size(), "Amount provided to move down is too large and would be out of bounds.Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList
      
        .size());
    }
    else
    {
      Checks.check(selectedPosition - amount >= orderList.size(), "Amount provided to move down is too large and would be out of bounds.Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList
      
        .size());
    }
    
    if (ascendingOrder) {
      return moveTo(selectedPosition + amount);
    }
    return moveTo(selectedPosition - amount);
  }
  


  @Nonnull
  public M moveTo(int position)
  {
    Checks.notNegative(position, "Provided position");
    Checks.check(position < orderList.size(), "Provided position is too big and is out of bounds.");
    
    T selectedItem = orderList.remove(selectedPosition);
    orderList.add(position, selectedItem);
    
    return this;
  }
  


  @Nonnull
  public M swapPosition(int swapPosition)
  {
    Checks.notNegative(swapPosition, "Provided swapPosition");
    Checks.check(swapPosition < orderList.size(), "Provided swapPosition is too big and is out of bounds. swapPosition: " + swapPosition);
    

    T selectedItem = orderList.get(selectedPosition);
    T swapItem = orderList.get(swapPosition);
    orderList.set(swapPosition, selectedItem);
    orderList.set(selectedPosition, swapItem);
    
    return this;
  }
  


  @Nonnull
  public M swapPosition(@Nonnull T swapEntity)
  {
    Checks.notNull(swapEntity, "Provided swapEntity");
    validateInput(swapEntity);
    
    return swapPosition(orderList.indexOf(swapEntity));
  }
  


  @Nonnull
  public M reverseOrder()
  {
    Collections.reverse(orderList);
    return this;
  }
  


  @Nonnull
  public M shuffleOrder()
  {
    Collections.shuffle(orderList);
    return this;
  }
  


  @Nonnull
  public M sortOrder(@Nonnull Comparator<T> comparator)
  {
    Checks.notNull(comparator, "Provided comparator");
    
    orderList.sort(comparator);
    return this;
  }
  
  protected abstract void validateInput(T paramT);
}
