package org.apache.commons.collections4.sequence;

public abstract interface CommandVisitor<T>
{
  public abstract void visitInsertCommand(T paramT);
  
  public abstract void visitKeepCommand(T paramT);
  
  public abstract void visitDeleteCommand(T paramT);
}
