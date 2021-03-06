package okhttp3.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import okio.Okio;
import okio.Sink;
import okio.Source;































public abstract interface FileSystem
{
  public static final FileSystem SYSTEM = new FileSystem() {
    public Source source(File file) throws FileNotFoundException {
      return Okio.source(file);
    }
    
    public Sink sink(File file) throws FileNotFoundException {
      try {
        return Okio.sink(file);
      }
      catch (FileNotFoundException e) {
        file.getParentFile().mkdirs(); }
      return Okio.sink(file);
    }
    
    public Sink appendingSink(File file) throws FileNotFoundException
    {
      try {
        return Okio.appendingSink(file);
      }
      catch (FileNotFoundException e) {
        file.getParentFile().mkdirs(); }
      return Okio.appendingSink(file);
    }
    
    public void delete(File file)
      throws IOException
    {
      if ((!file.delete()) && (file.exists())) {
        throw new IOException("failed to delete " + file);
      }
    }
    
    public boolean exists(File file) {
      return file.exists();
    }
    
    public long size(File file) {
      return file.length();
    }
    
    public void rename(File from, File to) throws IOException {
      delete(to);
      if (!from.renameTo(to)) {
        throw new IOException("failed to rename " + from + " to " + to);
      }
    }
    
    public void deleteContents(File directory) throws IOException {
      File[] files = directory.listFiles();
      if (files == null) {
        throw new IOException("not a readable directory: " + directory);
      }
      for (File file : files) {
        if (file.isDirectory()) {
          deleteContents(file);
        }
        if (!file.delete()) {
          throw new IOException("failed to delete " + file);
        }
      }
    }
  };
  
  public abstract Source source(File paramFile)
    throws FileNotFoundException;
  
  public abstract Sink sink(File paramFile)
    throws FileNotFoundException;
  
  public abstract Sink appendingSink(File paramFile)
    throws FileNotFoundException;
  
  public abstract void delete(File paramFile)
    throws IOException;
  
  public abstract boolean exists(File paramFile);
  
  public abstract long size(File paramFile);
  
  public abstract void rename(File paramFile1, File paramFile2)
    throws IOException;
  
  public abstract void deleteContents(File paramFile)
    throws IOException;
}
