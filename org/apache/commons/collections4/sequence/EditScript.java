package org.apache.commons.collections4.sequence;

import java.util.ArrayList;
import java.util.List;
















































public class EditScript<T>
{
  private final List<EditCommand<T>> commands;
  private int lcsLength;
  private int modifications;
  
  public EditScript()
  {
    commands = new ArrayList();
    lcsLength = 0;
    modifications = 0;
  }
  




  public void append(KeepCommand<T> command)
  {
    commands.add(command);
    lcsLength += 1;
  }
  




  public void append(InsertCommand<T> command)
  {
    commands.add(command);
    modifications += 1;
  }
  




  public void append(DeleteCommand<T> command)
  {
    commands.add(command);
    modifications += 1;
  }
  








  public void visit(CommandVisitor<T> visitor)
  {
    for (EditCommand<T> command : commands) {
      command.accept(visitor);
    }
  }
  






  public int getLCSLength()
  {
    return lcsLength;
  }
  






  public int getModifications()
  {
    return modifications;
  }
}
