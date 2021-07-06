package okhttp3.internal.cache2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;





























final class Relay
{
  private static final int SOURCE_UPSTREAM = 1;
  private static final int SOURCE_FILE = 2;
  static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
  static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
  





  private static final long FILE_HEADER_SIZE = 32L;
  





  RandomAccessFile file;
  





  Thread upstreamReader;
  




  Source upstream;
  




  final Buffer upstreamBuffer = new Buffer();
  


  long upstreamPos;
  


  boolean complete;
  


  private final ByteString metadata;
  

  final Buffer buffer = new Buffer();
  


  final long bufferMaxSize;
  


  int sourceCount;
  


  private Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize)
  {
    this.file = file;
    this.upstream = upstream;
    complete = (upstream == null);
    this.upstreamPos = upstreamPos;
    this.metadata = metadata;
    this.bufferMaxSize = bufferMaxSize;
  }
  







  public static Relay edit(File file, Source upstream, ByteString metadata, long bufferMaxSize)
    throws IOException
  {
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    Relay result = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize);
    

    randomAccessFile.setLength(0L);
    result.writeHeader(PREFIX_DIRTY, -1L, -1L);
    
    return result;
  }
  





  public static Relay read(File file)
    throws IOException
  {
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
    

    Buffer header = new Buffer();
    fileOperator.read(0L, header, 32L);
    ByteString prefix = header.readByteString(PREFIX_CLEAN.size());
    if (!prefix.equals(PREFIX_CLEAN)) throw new IOException("unreadable cache file");
    long upstreamSize = header.readLong();
    long metadataSize = header.readLong();
    

    Buffer metadataBuffer = new Buffer();
    fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
    ByteString metadata = metadataBuffer.readByteString();
    

    return new Relay(randomAccessFile, null, upstreamSize, metadata, 0L);
  }
  
  private void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException
  {
    Buffer header = new Buffer();
    header.write(prefix);
    header.writeLong(upstreamSize);
    header.writeLong(metadataSize);
    if (header.size() != 32L) { throw new IllegalArgumentException();
    }
    FileOperator fileOperator = new FileOperator(file.getChannel());
    fileOperator.write(0L, header, 32L);
  }
  
  private void writeMetadata(long upstreamSize) throws IOException {
    Buffer metadataBuffer = new Buffer();
    metadataBuffer.write(metadata);
    
    FileOperator fileOperator = new FileOperator(file.getChannel());
    fileOperator.write(32L + upstreamSize, metadataBuffer, metadata.size());
  }
  
  void commit(long upstreamSize) throws IOException
  {
    writeMetadata(upstreamSize);
    file.getChannel().force(false);
    

    writeHeader(PREFIX_CLEAN, upstreamSize, metadata.size());
    file.getChannel().force(false);
    

    synchronized (this) {
      complete = true;
    }
    
    Util.closeQuietly(upstream);
    upstream = null;
  }
  
  boolean isClosed() {
    return file == null;
  }
  
  public ByteString metadata() {
    return metadata;
  }
  




  public Source newSource()
  {
    synchronized (this) {
      if (file == null) return null;
      sourceCount += 1;
    }
    
    return new RelaySource();
  }
  
  class RelaySource implements Source {
    private final Timeout timeout = new Timeout();
    

    private FileOperator fileOperator = new FileOperator(file.getChannel());
    





    private long sourcePos;
    





    RelaySource() {}
    





    public long read(Buffer sink, long byteCount)
      throws IOException
    {
      if (fileOperator == null) { throw new IllegalStateException("closed");
      }
      



      synchronized (Relay.this) {
        long upstreamPos;
        while (sourcePos == (upstreamPos = Relay.this.upstreamPos))
        {
          if (complete) { return -1L;
          }
          
          if (upstreamReader != null) {
            timeout.waitUntilNotified(Relay.this);

          }
          else
          {
            upstreamReader = Thread.currentThread();
            int source = 1;
            break label196;
          }
        }
        long bufferPos = upstreamPos - buffer.size();
        
        int source;
        if (sourcePos < bufferPos) {
          source = 2;

        }
        else
        {
          long bytesToRead = Math.min(byteCount, upstreamPos - sourcePos);
          buffer.copyTo(sink, sourcePos - bufferPos, bytesToRead);
          sourcePos += bytesToRead;
          return bytesToRead; } }
      label196:
      int source;
      long upstreamPos;
      if (source == 2) {
        long bytesToRead = Math.min(byteCount, upstreamPos - sourcePos);
        fileOperator.read(32L + sourcePos, sink, bytesToRead);
        sourcePos += bytesToRead;
        return bytesToRead;
      }
      

      try
      {
        long upstreamBytesRead = upstream.read(upstreamBuffer, bufferMaxSize);
        

        if (upstreamBytesRead == -1L) {
          commit(upstreamPos);
          return -1L;
        }
        

        long bytesRead = Math.min(upstreamBytesRead, byteCount);
        upstreamBuffer.copyTo(sink, 0L, bytesRead);
        sourcePos += bytesRead;
        

        fileOperator.write(32L + upstreamPos, upstreamBuffer
          .clone(), upstreamBytesRead);
        
        synchronized (Relay.this)
        {
          buffer.write(upstreamBuffer, upstreamBytesRead);
          if (buffer.size() > bufferMaxSize) {
            buffer.skip(buffer.size() - bufferMaxSize);
          }
          

          Relay.this.upstreamPos += upstreamBytesRead;
        }
        
        return bytesRead;
      } finally {
        synchronized (Relay.this) {
          upstreamReader = null;
          notifyAll();
        }
      }
    }
    
    public Timeout timeout() {
      return timeout;
    }
    
    public void close() throws IOException {
      if (fileOperator == null) return;
      fileOperator = null;
      
      RandomAccessFile fileToClose = null;
      synchronized (Relay.this) {
        sourceCount -= 1;
        if (sourceCount == 0) {
          fileToClose = file;
          file = null;
        }
      }
      
      if (fileToClose != null) {
        Util.closeQuietly(fileToClose);
      }
    }
  }
}
