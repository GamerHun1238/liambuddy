package org.apache.commons.collections4.sequence;

import java.util.ArrayList;
import java.util.List;



















































public class ReplacementsFinder<T>
  implements CommandVisitor<T>
{
  private final List<T> pendingInsertions;
  private final List<T> pendingDeletions;
  private int skipped;
  private final ReplacementsHandler<T> handler;
  
  public ReplacementsFinder(ReplacementsHandler<T> handler)
  {
    pendingInsertions = new ArrayList();
    pendingDeletions = new ArrayList();
    skipped = 0;
    this.handler = handler;
  }
  




  public void visitInsertCommand(T object)
  {
    pendingInsertions.add(object);
  }
  







  public void visitKeepCommand(T object)
  {
    if ((pendingDeletions.isEmpty()) && (pendingInsertions.isEmpty())) {
      skipped += 1;
    } else {
      handler.handleReplacement(skipped, pendingDeletions, pendingInsertions);
      pendingDeletions.clear();
      pendingInsertions.clear();
      skipped = 1;
    }
  }
  




  public void visitDeleteCommand(T object)
  {
    pendingDeletions.add(object);
  }
}
