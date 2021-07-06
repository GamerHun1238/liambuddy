package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;























































public final class DiskLruCache
  implements Closeable, Flushable
{
  static final String JOURNAL_FILE = "journal";
  static final String JOURNAL_FILE_TEMP = "journal.tmp";
  static final String JOURNAL_FILE_BACKUP = "journal.bkp";
  static final String MAGIC = "libcore.io.DiskLruCache";
  static final String VERSION_1 = "1";
  static final long ANY_SEQUENCE_NUMBER = -1L;
  static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
  



  private static final String CLEAN = "CLEAN";
  



  private static final String DIRTY = "DIRTY";
  


  private static final String REMOVE = "REMOVE";
  


  private static final String READ = "READ";
  


  final FileSystem fileSystem;
  


  final File directory;
  


  private final File journalFile;
  


  private final File journalFileTmp;
  


  private final File journalFileBackup;
  


  private final int appVersion;
  


  private long maxSize;
  


  final int valueCount;
  


  private long size = 0L;
  BufferedSink journalWriter;
  final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
  

  int redundantOpCount;
  
  boolean hasJournalErrors;
  
  boolean initialized;
  
  boolean closed;
  
  boolean mostRecentTrimFailed;
  
  boolean mostRecentRebuildFailed;
  
  private long nextSequenceNumber = 0L;
  
  private final Executor executor;
  
  private final Runnable cleanupRunnable = new Runnable() {
    public void run() {
      synchronized (DiskLruCache.this) {
        if ((!initialized | closed)) {
          return;
        }
        try
        {
          trimToSize();
        } catch (IOException ignored) {
          mostRecentTrimFailed = true;
        }
        try
        {
          if (journalRebuildRequired()) {
            rebuildJournal();
            redundantOpCount = 0;
          }
        } catch (IOException e) {
          mostRecentRebuildFailed = true;
          journalWriter = Okio.buffer(Okio.blackhole());
        }
      }
    }
  };
  
  DiskLruCache(FileSystem fileSystem, File directory, int appVersion, int valueCount, long maxSize, Executor executor)
  {
    this.fileSystem = fileSystem;
    this.directory = directory;
    this.appVersion = appVersion;
    journalFile = new File(directory, "journal");
    journalFileTmp = new File(directory, "journal.tmp");
    journalFileBackup = new File(directory, "journal.bkp");
    this.valueCount = valueCount;
    this.maxSize = maxSize;
    this.executor = executor;
  }
  
  public synchronized void initialize() throws IOException {
    assert (Thread.holdsLock(this));
    
    if (initialized) {
      return;
    }
    

    if (fileSystem.exists(journalFileBackup))
    {
      if (fileSystem.exists(journalFile)) {
        fileSystem.delete(journalFileBackup);
      } else {
        fileSystem.rename(journalFileBackup, journalFile);
      }
    }
    

    if (fileSystem.exists(journalFile)) {
      try {
        readJournal();
        processJournal();
        initialized = true;
        return;
      } catch (IOException journalIsCorrupt) {
        Platform.get().log(5, "DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt
          .getMessage() + ", removing", journalIsCorrupt);
      }
    }
    
    try
    {
      delete();
      
      closed = false; } finally { closed = false;
    }
    



    initialized = true;
  }
  








  public static DiskLruCache create(FileSystem fileSystem, File directory, int appVersion, int valueCount, long maxSize)
  {
    if (maxSize <= 0L) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    if (valueCount <= 0) {
      throw new IllegalArgumentException("valueCount <= 0");
    }
    


    Executor executor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true));
    
    return new DiskLruCache(fileSystem, directory, appVersion, valueCount, maxSize, executor);
  }
  
  private void readJournal() throws IOException {
    BufferedSource source = Okio.buffer(fileSystem.source(journalFile));Throwable localThrowable1 = null;
    try { String magic = source.readUtf8LineStrict();
      String version = source.readUtf8LineStrict();
      String appVersionString = source.readUtf8LineStrict();
      String valueCountString = source.readUtf8LineStrict();
      String blank = source.readUtf8LineStrict();
      if ((!"libcore.io.DiskLruCache".equals(magic)) || 
        (!"1".equals(version)) || 
        (!Integer.toString(appVersion).equals(appVersionString)) || 
        (!Integer.toString(valueCount).equals(valueCountString)) || 
        (!"".equals(blank))) {
        throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
      }
      

      int lineCount = 0;
      try {
        for (;;) {
          readJournalLine(source.readUtf8LineStrict());
          lineCount++;
        }
        










        $closeResource(localThrowable1, source);
      }
      catch (EOFException endOfJournal)
      {
        redundantOpCount = (lineCount - lruEntries.size());
        

        if (!source.exhausted()) {
          rebuildJournal();
        } else {
          journalWriter = newJournalWriter();
        }
        if (source == null) {}
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable1 = localThrowable;throw localThrowable;














    }
    finally
    {














      if (source != null) $closeResource(localThrowable1, source);
    }
  }
  
  private BufferedSink newJournalWriter() throws FileNotFoundException { Sink fileSink = fileSystem.appendingSink(journalFile);
    Sink faultHidingSink = new FaultHidingSink(fileSink) {
      protected void onException(IOException e) {
        assert (Thread.holdsLock(DiskLruCache.this));
        hasJournalErrors = true;
      }
    };
    return Okio.buffer(faultHidingSink);
  }
  
  private void readJournalLine(String line) throws IOException {
    int firstSpace = line.indexOf(' ');
    if (firstSpace == -1) {
      throw new IOException("unexpected journal line: " + line);
    }
    
    int keyBegin = firstSpace + 1;
    int secondSpace = line.indexOf(' ', keyBegin);
    String key;
    if (secondSpace == -1) {
      String key = line.substring(keyBegin);
      if ((firstSpace == "REMOVE".length()) && (line.startsWith("REMOVE"))) {
        lruEntries.remove(key);
      }
    }
    else {
      key = line.substring(keyBegin, secondSpace);
    }
    
    Entry entry = (Entry)lruEntries.get(key);
    if (entry == null) {
      entry = new Entry(key);
      lruEntries.put(key, entry);
    }
    
    if ((secondSpace != -1) && (firstSpace == "CLEAN".length()) && (line.startsWith("CLEAN"))) {
      String[] parts = line.substring(secondSpace + 1).split(" ");
      readable = true;
      currentEditor = null;
      entry.setLengths(parts);
    } else if ((secondSpace == -1) && (firstSpace == "DIRTY".length()) && (line.startsWith("DIRTY"))) {
      currentEditor = new Editor(entry);
    } else if ((secondSpace != -1) || (firstSpace != "READ".length()) || (!line.startsWith("READ")))
    {

      throw new IOException("unexpected journal line: " + line);
    }
  }
  


  private void processJournal()
    throws IOException
  {
    fileSystem.delete(journalFileTmp);
    for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext();) {
      Entry entry = (Entry)i.next();
      if (currentEditor == null) {
        for (int t = 0; t < valueCount; t++) {
          size += lengths[t];
        }
      } else {
        currentEditor = null;
        for (int t = 0; t < valueCount; t++) {
          fileSystem.delete(cleanFiles[t]);
          fileSystem.delete(dirtyFiles[t]);
        }
        i.remove();
      }
    }
  }
  


  synchronized void rebuildJournal()
    throws IOException
  {
    if (journalWriter != null) {
      journalWriter.close();
    }
    
    BufferedSink writer = Okio.buffer(fileSystem.sink(journalFileTmp));Throwable localThrowable1 = null;
    try { writer.writeUtf8("libcore.io.DiskLruCache").writeByte(10);
      writer.writeUtf8("1").writeByte(10);
      writer.writeDecimalLong(appVersion).writeByte(10);
      writer.writeDecimalLong(valueCount).writeByte(10);
      writer.writeByte(10);
      
      for (Entry entry : lruEntries.values()) {
        if (currentEditor != null) {
          writer.writeUtf8("DIRTY").writeByte(32);
          writer.writeUtf8(key);
          writer.writeByte(10);
        } else {
          writer.writeUtf8("CLEAN").writeByte(32);
          writer.writeUtf8(key);
          entry.writeLengths(writer);
          writer.writeByte(10);
        }
      }
    }
    catch (Throwable localThrowable2)
    {
      localThrowable1 = localThrowable2;throw localThrowable2;








    }
    finally
    {







      if (writer != null) $closeResource(localThrowable1, writer);
    }
    if (fileSystem.exists(journalFile)) {
      fileSystem.rename(journalFile, journalFileBackup);
    }
    fileSystem.rename(journalFileTmp, journalFile);
    fileSystem.delete(journalFileBackup);
    
    journalWriter = newJournalWriter();
    hasJournalErrors = false;
    mostRecentRebuildFailed = false;
  }
  


  public synchronized Snapshot get(String key)
    throws IOException
  {
    initialize();
    
    checkNotClosed();
    validateKey(key);
    Entry entry = (Entry)lruEntries.get(key);
    if ((entry == null) || (!readable)) { return null;
    }
    Snapshot snapshot = entry.snapshot();
    if (snapshot == null) { return null;
    }
    redundantOpCount += 1;
    journalWriter.writeUtf8("READ").writeByte(32).writeUtf8(key).writeByte(10);
    if (journalRebuildRequired()) {
      executor.execute(cleanupRunnable);
    }
    
    return snapshot;
  }
  
  @Nullable
  public Editor edit(String key)
    throws IOException
  {
    return edit(key, -1L);
  }
  
  synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
    initialize();
    
    checkNotClosed();
    validateKey(key);
    Entry entry = (Entry)lruEntries.get(key);
    if ((expectedSequenceNumber != -1L) && ((entry == null) || (sequenceNumber != expectedSequenceNumber)))
    {
      return null;
    }
    if ((entry != null) && (currentEditor != null)) {
      return null;
    }
    if ((mostRecentTrimFailed) || (mostRecentRebuildFailed))
    {




      executor.execute(cleanupRunnable);
      return null;
    }
    

    journalWriter.writeUtf8("DIRTY").writeByte(32).writeUtf8(key).writeByte(10);
    journalWriter.flush();
    
    if (hasJournalErrors) {
      return null;
    }
    
    if (entry == null) {
      entry = new Entry(key);
      lruEntries.put(key, entry);
    }
    Editor editor = new Editor(entry);
    currentEditor = editor;
    return editor;
  }
  
  public File getDirectory()
  {
    return directory;
  }
  


  public synchronized long getMaxSize()
  {
    return maxSize;
  }
  



  public synchronized void setMaxSize(long maxSize)
  {
    this.maxSize = maxSize;
    if (initialized) {
      executor.execute(cleanupRunnable);
    }
  }
  


  public synchronized long size()
    throws IOException
  {
    initialize();
    return size;
  }
  
  synchronized void completeEdit(Editor editor, boolean success) throws IOException {
    Entry entry = entry;
    if (currentEditor != editor) {
      throw new IllegalStateException();
    }
    

    if ((success) && (!readable)) {
      for (int i = 0; i < valueCount; i++) {
        if (written[i] == 0) {
          editor.abort();
          throw new IllegalStateException("Newly created entry didn't create value for index " + i);
        }
        if (!fileSystem.exists(dirtyFiles[i])) {
          editor.abort();
          return;
        }
      }
    }
    
    for (int i = 0; i < valueCount; i++) {
      File dirty = dirtyFiles[i];
      if (success) {
        if (fileSystem.exists(dirty)) {
          File clean = cleanFiles[i];
          fileSystem.rename(dirty, clean);
          long oldLength = lengths[i];
          long newLength = fileSystem.size(clean);
          lengths[i] = newLength;
          size = (size - oldLength + newLength);
        }
      } else {
        fileSystem.delete(dirty);
      }
    }
    
    redundantOpCount += 1;
    currentEditor = null;
    if ((readable | success)) {
      readable = true;
      journalWriter.writeUtf8("CLEAN").writeByte(32);
      journalWriter.writeUtf8(key);
      entry.writeLengths(journalWriter);
      journalWriter.writeByte(10);
      if (success) {
        sequenceNumber = (nextSequenceNumber++);
      }
    } else {
      lruEntries.remove(key);
      journalWriter.writeUtf8("REMOVE").writeByte(32);
      journalWriter.writeUtf8(key);
      journalWriter.writeByte(10);
    }
    journalWriter.flush();
    
    if ((size > maxSize) || (journalRebuildRequired())) {
      executor.execute(cleanupRunnable);
    }
  }
  



  boolean journalRebuildRequired()
  {
    int redundantOpCompactThreshold = 2000;
    return (redundantOpCount >= 2000) && 
      (redundantOpCount >= lruEntries.size());
  }
  




  public synchronized boolean remove(String key)
    throws IOException
  {
    initialize();
    
    checkNotClosed();
    validateKey(key);
    Entry entry = (Entry)lruEntries.get(key);
    if (entry == null) return false;
    boolean removed = removeEntry(entry);
    if ((removed) && (size <= maxSize)) mostRecentTrimFailed = false;
    return removed;
  }
  
  boolean removeEntry(Entry entry) throws IOException {
    if (currentEditor != null) {
      currentEditor.detach();
    }
    
    for (int i = 0; i < valueCount; i++) {
      fileSystem.delete(cleanFiles[i]);
      size -= lengths[i];
      lengths[i] = 0L;
    }
    
    redundantOpCount += 1;
    journalWriter.writeUtf8("REMOVE").writeByte(32).writeUtf8(key).writeByte(10);
    lruEntries.remove(key);
    
    if (journalRebuildRequired()) {
      executor.execute(cleanupRunnable);
    }
    
    return true;
  }
  
  public synchronized boolean isClosed()
  {
    return closed;
  }
  
  private synchronized void checkNotClosed() {
    if (isClosed()) {
      throw new IllegalStateException("cache is closed");
    }
  }
  
  public synchronized void flush() throws IOException
  {
    if (!initialized) { return;
    }
    checkNotClosed();
    trimToSize();
    journalWriter.flush();
  }
  
  public synchronized void close() throws IOException
  {
    if ((!initialized) || (closed)) {
      closed = true;
      return;
    }
    
    for (Entry entry : (Entry[])lruEntries.values().toArray(new Entry[lruEntries.size()])) {
      if (currentEditor != null) {
        currentEditor.abort();
      }
    }
    trimToSize();
    journalWriter.close();
    journalWriter = null;
    closed = true;
  }
  
  void trimToSize() throws IOException {
    while (size > maxSize) {
      Entry toEvict = (Entry)lruEntries.values().iterator().next();
      removeEntry(toEvict);
    }
    mostRecentTrimFailed = false;
  }
  


  public void delete()
    throws IOException
  {
    close();
    fileSystem.deleteContents(directory);
  }
  


  public synchronized void evictAll()
    throws IOException
  {
    initialize();
    
    for (Entry entry : (Entry[])lruEntries.values().toArray(new Entry[lruEntries.size()])) {
      removeEntry(entry);
    }
    mostRecentTrimFailed = false;
  }
  
  private void validateKey(String key) {
    Matcher matcher = LEGAL_KEY_PATTERN.matcher(key);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + key + "\"");
    }
  }
  














  public synchronized Iterator<Snapshot> snapshots()
    throws IOException
  {
    initialize();
    new Iterator()
    {
      final Iterator<DiskLruCache.Entry> delegate = new ArrayList(lruEntries.values()).iterator();
      
      DiskLruCache.Snapshot nextSnapshot;
      
      DiskLruCache.Snapshot removeSnapshot;
      

      public boolean hasNext()
      {
        if (nextSnapshot != null) { return true;
        }
        synchronized (DiskLruCache.this)
        {
          if (closed) { return false;
          }
          while (delegate.hasNext()) {
            DiskLruCache.Entry entry = (DiskLruCache.Entry)delegate.next();
            DiskLruCache.Snapshot snapshot = entry.snapshot();
            if (snapshot != null) {
              nextSnapshot = snapshot;
              return true;
            }
          }
        }
        return false;
      }
      
      public DiskLruCache.Snapshot next() {
        if (!hasNext()) throw new NoSuchElementException();
        removeSnapshot = nextSnapshot;
        nextSnapshot = null;
        return removeSnapshot;
      }
      
      /* Error */
      public void remove()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 18	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //   4: ifnonnull +13 -> 17
        //   7: new 19	java/lang/IllegalStateException
        //   10: dup
        //   11: ldc 20
        //   13: invokespecial 21	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
        //   16: athrow
        //   17: aload_0
        //   18: getfield 1	okhttp3/internal/cache/DiskLruCache$3:this$0	Lokhttp3/internal/cache/DiskLruCache;
        //   21: aload_0
        //   22: getfield 18	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //   25: invokestatic 22	okhttp3/internal/cache/DiskLruCache$Snapshot:access$000	(Lokhttp3/internal/cache/DiskLruCache$Snapshot;)Ljava/lang/String;
        //   28: invokevirtual 23	okhttp3/internal/cache/DiskLruCache:remove	(Ljava/lang/String;)Z
        //   31: pop
        //   32: aload_0
        //   33: aconst_null
        //   34: putfield 18	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //   37: goto +20 -> 57
        //   40: astore_1
        //   41: aload_0
        //   42: aconst_null
        //   43: putfield 18	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //   46: goto +11 -> 57
        //   49: astore_2
        //   50: aload_0
        //   51: aconst_null
        //   52: putfield 18	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //   55: aload_2
        //   56: athrow
        //   57: return
        // Line number table:
        //   Java source line #768	-> byte code offset #0
        //   Java source line #770	-> byte code offset #17
        //   Java source line #775	-> byte code offset #32
        //   Java source line #776	-> byte code offset #37
        //   Java source line #771	-> byte code offset #40
        //   Java source line #775	-> byte code offset #41
        //   Java source line #776	-> byte code offset #46
        //   Java source line #775	-> byte code offset #49
        //   Java source line #777	-> byte code offset #57
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	58	0	this	3
        //   40	1	1	localIOException	IOException
        //   49	7	2	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   17	32	40	java/io/IOException
        //   17	32	49	finally
      }
    };
  }
  
  public final class Snapshot
    implements Closeable
  {
    private final String key;
    private final long sequenceNumber;
    private final Source[] sources;
    private final long[] lengths;
    
    Snapshot(String key, long sequenceNumber, Source[] sources, long[] lengths)
    {
      this.key = key;
      this.sequenceNumber = sequenceNumber;
      this.sources = sources;
      this.lengths = lengths;
    }
    
    public String key() {
      return key;
    }
    

    @Nullable
    public DiskLruCache.Editor edit()
      throws IOException
    {
      return edit(key, sequenceNumber);
    }
    
    public Source getSource(int index)
    {
      return sources[index];
    }
    
    public long getLength(int index)
    {
      return lengths[index];
    }
    
    public void close() {
      for (Source in : sources) {
        Util.closeQuietly(in);
      }
    }
  }
  
  public final class Editor
  {
    final DiskLruCache.Entry entry;
    final boolean[] written;
    private boolean done;
    
    Editor(DiskLruCache.Entry entry) {
      this.entry = entry;
      written = (readable ? null : new boolean[valueCount]);
    }
    





    void detach()
    {
      if (entry.currentEditor == this) {
        for (int i = 0; i < valueCount; i++) {
          try {
            fileSystem.delete(entry.dirtyFiles[i]);
          }
          catch (IOException localIOException) {}
        }
        
        entry.currentEditor = null;
      }
    }
    



    public Source newSource(int index)
    {
      synchronized (DiskLruCache.this) {
        if (done) {
          throw new IllegalStateException();
        }
        if ((!entry.readable) || (entry.currentEditor != this)) {
          return null;
        }
        try {
          return fileSystem.source(entry.cleanFiles[index]);
        } catch (FileNotFoundException e) {
          return null;
        }
      }
    }
    




    public Sink newSink(int index)
    {
      synchronized (DiskLruCache.this) {
        if (done) {
          throw new IllegalStateException();
        }
        if (entry.currentEditor != this) {
          return Okio.blackhole();
        }
        if (!entry.readable) {
          written[index] = true;
        }
        File dirtyFile = entry.dirtyFiles[index];
        try
        {
          sink = fileSystem.sink(dirtyFile);
        } catch (FileNotFoundException e) { Sink sink;
          return Okio.blackhole(); }
        Sink sink;
        new FaultHidingSink(sink) {
          protected void onException(IOException e) {
            synchronized (DiskLruCache.this) {
              detach();
            }
          }
        };
      }
    }
    


    public void commit()
      throws IOException
    {
      synchronized (DiskLruCache.this) {
        if (done) {
          throw new IllegalStateException();
        }
        if (entry.currentEditor == this) {
          completeEdit(this, true);
        }
        done = true;
      }
    }
    


    public void abort()
      throws IOException
    {
      synchronized (DiskLruCache.this) {
        if (done) {
          throw new IllegalStateException();
        }
        if (entry.currentEditor == this) {
          completeEdit(this, false);
        }
        done = true;
      }
    }
    
    public void abortUnlessCommitted() {
      synchronized (DiskLruCache.this) {
        if ((!done) && (entry.currentEditor == this)) {
          try {
            completeEdit(this, false);
          }
          catch (IOException localIOException) {}
        }
      }
    }
  }
  

  private final class Entry
  {
    final String key;
    
    final long[] lengths;
    
    final File[] cleanFiles;
    
    final File[] dirtyFiles;
    
    boolean readable;
    
    DiskLruCache.Editor currentEditor;
    long sequenceNumber;
    
    Entry(String key)
    {
      this.key = key;
      
      lengths = new long[valueCount];
      cleanFiles = new File[valueCount];
      dirtyFiles = new File[valueCount];
      

      StringBuilder fileBuilder = new StringBuilder(key).append('.');
      int truncateTo = fileBuilder.length();
      for (int i = 0; i < valueCount; i++) {
        fileBuilder.append(i);
        cleanFiles[i] = new File(directory, fileBuilder.toString());
        fileBuilder.append(".tmp");
        dirtyFiles[i] = new File(directory, fileBuilder.toString());
        fileBuilder.setLength(truncateTo);
      }
    }
    
    void setLengths(String[] strings) throws IOException
    {
      if (strings.length != valueCount) {
        throw invalidLengths(strings);
      }
      try
      {
        for (int i = 0; i < strings.length; i++) {
          lengths[i] = Long.parseLong(strings[i]);
        }
      } catch (NumberFormatException e) {
        throw invalidLengths(strings);
      }
    }
    
    void writeLengths(BufferedSink writer) throws IOException
    {
      for (long length : lengths) {
        writer.writeByte(32).writeDecimalLong(length);
      }
    }
    
    private IOException invalidLengths(String[] strings) throws IOException {
      throw new IOException("unexpected journal line: " + Arrays.toString(strings));
    }
    




    DiskLruCache.Snapshot snapshot()
    {
      if (!Thread.holdsLock(DiskLruCache.this)) { throw new AssertionError();
      }
      Source[] sources = new Source[valueCount];
      long[] lengths = (long[])this.lengths.clone();
      try {
        for (int i = 0; i < valueCount; i++) {
          sources[i] = fileSystem.source(cleanFiles[i]);
        }
        return new DiskLruCache.Snapshot(DiskLruCache.this, key, sequenceNumber, sources, lengths);
      }
      catch (FileNotFoundException e) {
        for (int i = 0; i < valueCount; i++) {
          if (sources[i] == null) break;
          Util.closeQuietly(sources[i]);
        }
        



        try
        {
          removeEntry(this);
        } catch (IOException localIOException) {}
      }
      return null;
    }
  }
}
