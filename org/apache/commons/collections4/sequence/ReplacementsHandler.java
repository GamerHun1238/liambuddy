package org.apache.commons.collections4.sequence;

import java.util.List;

public abstract interface ReplacementsHandler<T>
{
  public abstract void handleReplacement(int paramInt, List<T> paramList1, List<T> paramList2);
}
