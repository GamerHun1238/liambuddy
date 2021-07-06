package okhttp3.internal.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;

























final class Hpack
{
  private static final int PREFIX_4_BITS = 15;
  private static final int PREFIX_5_BITS = 31;
  private static final int PREFIX_6_BITS = 63;
  private static final int PREFIX_7_BITS = 127;
  static final Header[] STATIC_HEADER_TABLE = { new Header(Header.TARGET_AUTHORITY, ""), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, "POST"), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", ""), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", ""), new Header("accept-ranges", ""), new Header("accept", ""), new Header("access-control-allow-origin", ""), new Header("age", ""), new Header("allow", ""), new Header("authorization", ""), new Header("cache-control", ""), new Header("content-disposition", ""), new Header("content-encoding", ""), new Header("content-language", ""), new Header("content-length", ""), new Header("content-location", ""), new Header("content-range", ""), new Header("content-type", ""), new Header("cookie", ""), new Header("date", ""), new Header("etag", ""), new Header("expect", ""), new Header("expires", ""), new Header("from", ""), new Header("host", ""), new Header("if-match", ""), new Header("if-modified-since", ""), new Header("if-none-match", ""), new Header("if-range", ""), new Header("if-unmodified-since", ""), new Header("last-modified", ""), new Header("link", ""), new Header("location", ""), new Header("max-forwards", ""), new Header("proxy-authenticate", ""), new Header("proxy-authorization", ""), new Header("range", ""), new Header("referer", ""), new Header("refresh", ""), new Header("retry-after", ""), new Header("server", ""), new Header("set-cookie", ""), new Header("strict-transport-security", ""), new Header("transfer-encoding", ""), new Header("user-agent", ""), new Header("vary", ""), new Header("via", ""), new Header("www-authenticate", "") };
  
































  private Hpack() {}
  
































  static final class Reader
  {
    private final List<Header> headerList = new ArrayList();
    
    private final BufferedSource source;
    
    private final int headerTableSizeSetting;
    
    private int maxDynamicTableByteCount;
    Header[] dynamicTable = new Header[8];
    
    int nextHeaderIndex = dynamicTable.length - 1;
    int headerCount = 0;
    int dynamicTableByteCount = 0;
    
    Reader(int headerTableSizeSetting, Source source) {
      this(headerTableSizeSetting, headerTableSizeSetting, source);
    }
    
    Reader(int headerTableSizeSetting, int maxDynamicTableByteCount, Source source) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      this.maxDynamicTableByteCount = maxDynamicTableByteCount;
      this.source = Okio.buffer(source);
    }
    
    int maxDynamicTableByteCount() {
      return maxDynamicTableByteCount;
    }
    
    private void adjustDynamicTableByteCount() {
      if (maxDynamicTableByteCount < dynamicTableByteCount) {
        if (maxDynamicTableByteCount == 0) {
          clearDynamicTable();
        } else {
          evictToRecoverBytes(dynamicTableByteCount - maxDynamicTableByteCount);
        }
      }
    }
    
    private void clearDynamicTable() {
      Arrays.fill(dynamicTable, null);
      nextHeaderIndex = (dynamicTable.length - 1);
      headerCount = 0;
      dynamicTableByteCount = 0;
    }
    
    private int evictToRecoverBytes(int bytesToRecover)
    {
      int entriesToEvict = 0;
      if (bytesToRecover > 0)
      {
        for (int j = dynamicTable.length - 1; (j >= nextHeaderIndex) && (bytesToRecover > 0); j--) {
          bytesToRecover -= dynamicTable[j].hpackSize;
          dynamicTableByteCount -= dynamicTable[j].hpackSize;
          headerCount -= 1;
          entriesToEvict++;
        }
        System.arraycopy(dynamicTable, nextHeaderIndex + 1, dynamicTable, nextHeaderIndex + 1 + entriesToEvict, headerCount);
        
        nextHeaderIndex += entriesToEvict;
      }
      return entriesToEvict;
    }
    


    void readHeaders()
      throws IOException
    {
      while (!source.exhausted()) {
        int b = source.readByte() & 0xFF;
        if (b == 128)
          throw new IOException("index == 0");
        if ((b & 0x80) == 128) {
          int index = readInt(b, 127);
          readIndexedHeader(index - 1);
        } else if (b == 64) {
          readLiteralHeaderWithIncrementalIndexingNewName();
        } else if ((b & 0x40) == 64) {
          int index = readInt(b, 63);
          readLiteralHeaderWithIncrementalIndexingIndexedName(index - 1);
        } else if ((b & 0x20) == 32) {
          maxDynamicTableByteCount = readInt(b, 31);
          if ((maxDynamicTableByteCount < 0) || (maxDynamicTableByteCount > headerTableSizeSetting))
          {
            throw new IOException("Invalid dynamic table size update " + maxDynamicTableByteCount);
          }
          adjustDynamicTableByteCount();
        } else if ((b == 16) || (b == 0)) {
          readLiteralHeaderWithoutIndexingNewName();
        } else {
          int index = readInt(b, 15);
          readLiteralHeaderWithoutIndexingIndexedName(index - 1);
        }
      }
    }
    
    public List<Header> getAndResetHeaderList() {
      List<Header> result = new ArrayList(headerList);
      headerList.clear();
      return result;
    }
    
    private void readIndexedHeader(int index) throws IOException {
      if (isStaticHeader(index)) {
        Header staticEntry = Hpack.STATIC_HEADER_TABLE[index];
        headerList.add(staticEntry);
      } else {
        int dynamicTableIndex = dynamicTableIndex(index - Hpack.STATIC_HEADER_TABLE.length);
        if ((dynamicTableIndex < 0) || (dynamicTableIndex >= dynamicTable.length)) {
          throw new IOException("Header index too large " + (index + 1));
        }
        headerList.add(dynamicTable[dynamicTableIndex]);
      }
    }
    
    private int dynamicTableIndex(int index)
    {
      return nextHeaderIndex + 1 + index;
    }
    
    private void readLiteralHeaderWithoutIndexingIndexedName(int index) throws IOException {
      ByteString name = getName(index);
      ByteString value = readByteString();
      headerList.add(new Header(name, value));
    }
    
    private void readLiteralHeaderWithoutIndexingNewName() throws IOException {
      ByteString name = Hpack.checkLowercase(readByteString());
      ByteString value = readByteString();
      headerList.add(new Header(name, value));
    }
    
    private void readLiteralHeaderWithIncrementalIndexingIndexedName(int nameIndex) throws IOException
    {
      ByteString name = getName(nameIndex);
      ByteString value = readByteString();
      insertIntoDynamicTable(-1, new Header(name, value));
    }
    
    private void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
      ByteString name = Hpack.checkLowercase(readByteString());
      ByteString value = readByteString();
      insertIntoDynamicTable(-1, new Header(name, value));
    }
    
    private ByteString getName(int index) throws IOException {
      if (isStaticHeader(index)) {
        return STATIC_HEADER_TABLEname;
      }
      int dynamicTableIndex = dynamicTableIndex(index - Hpack.STATIC_HEADER_TABLE.length);
      if ((dynamicTableIndex < 0) || (dynamicTableIndex >= dynamicTable.length)) {
        throw new IOException("Header index too large " + (index + 1));
      }
      
      return dynamicTable[dynamicTableIndex].name;
    }
    
    private boolean isStaticHeader(int index)
    {
      return (index >= 0) && (index <= Hpack.STATIC_HEADER_TABLE.length - 1);
    }
    
    private void insertIntoDynamicTable(int index, Header entry)
    {
      headerList.add(entry);
      
      int delta = hpackSize;
      if (index != -1) {
        delta -= dynamicTable[dynamicTableIndex(index)].hpackSize;
      }
      

      if (delta > maxDynamicTableByteCount) {
        clearDynamicTable();
        return;
      }
      

      int bytesToRecover = dynamicTableByteCount + delta - maxDynamicTableByteCount;
      int entriesEvicted = evictToRecoverBytes(bytesToRecover);
      
      if (index == -1) {
        if (headerCount + 1 > dynamicTable.length) {
          Header[] doubled = new Header[dynamicTable.length * 2];
          System.arraycopy(dynamicTable, 0, doubled, dynamicTable.length, dynamicTable.length);
          nextHeaderIndex = (dynamicTable.length - 1);
          dynamicTable = doubled;
        }
        index = nextHeaderIndex--;
        dynamicTable[index] = entry;
        headerCount += 1;
      } else {
        index += dynamicTableIndex(index) + entriesEvicted;
        dynamicTable[index] = entry;
      }
      dynamicTableByteCount += delta;
    }
    
    private int readByte() throws IOException {
      return source.readByte() & 0xFF;
    }
    
    int readInt(int firstByte, int prefixMask) throws IOException {
      int prefix = firstByte & prefixMask;
      if (prefix < prefixMask) {
        return prefix;
      }
      

      int result = prefixMask;
      int shift = 0;
      for (;;) {
        int b = readByte();
        if ((b & 0x80) != 0) {
          result += ((b & 0x7F) << shift);
          shift += 7;
        } else {
          result += (b << shift);
          break;
        }
      }
      return result;
    }
    
    ByteString readByteString() throws IOException
    {
      int firstByte = readByte();
      boolean huffmanDecode = (firstByte & 0x80) == 128;
      int length = readInt(firstByte, 127);
      
      if (huffmanDecode) {
        return ByteString.of(Huffman.get().decode(source.readByteArray(length)));
      }
      return source.readByteString(length);
    }
  }
  

  static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX = nameToFirstIndex();
  
  private static Map<ByteString, Integer> nameToFirstIndex() {
    Map<ByteString, Integer> result = new LinkedHashMap(STATIC_HEADER_TABLE.length);
    for (int i = 0; i < STATIC_HEADER_TABLE.length; i++) {
      if (!result.containsKey(STATIC_HEADER_TABLEname)) {
        result.put(STATIC_HEADER_TABLEname, Integer.valueOf(i));
      }
    }
    return Collections.unmodifiableMap(result);
  }
  



  static final class Writer
  {
    private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
    

    private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
    

    private final Buffer out;
    

    private final boolean useCompression;
    

    private int smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
    
    private boolean emitDynamicTableSizeUpdate;
    
    int headerTableSizeSetting;
    
    int maxDynamicTableByteCount;
    Header[] dynamicTable = new Header[8];
    
    int nextHeaderIndex = dynamicTable.length - 1;
    int headerCount = 0;
    int dynamicTableByteCount = 0;
    
    Writer(Buffer out) {
      this(4096, true, out);
    }
    
    Writer(int headerTableSizeSetting, boolean useCompression, Buffer out) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      maxDynamicTableByteCount = headerTableSizeSetting;
      this.useCompression = useCompression;
      this.out = out;
    }
    
    private void clearDynamicTable() {
      Arrays.fill(dynamicTable, null);
      nextHeaderIndex = (dynamicTable.length - 1);
      headerCount = 0;
      dynamicTableByteCount = 0;
    }
    
    private int evictToRecoverBytes(int bytesToRecover)
    {
      int entriesToEvict = 0;
      if (bytesToRecover > 0)
      {
        for (int j = dynamicTable.length - 1; (j >= nextHeaderIndex) && (bytesToRecover > 0); j--) {
          bytesToRecover -= dynamicTable[j].hpackSize;
          dynamicTableByteCount -= dynamicTable[j].hpackSize;
          headerCount -= 1;
          entriesToEvict++;
        }
        System.arraycopy(dynamicTable, nextHeaderIndex + 1, dynamicTable, nextHeaderIndex + 1 + entriesToEvict, headerCount);
        
        Arrays.fill(dynamicTable, nextHeaderIndex + 1, nextHeaderIndex + 1 + entriesToEvict, null);
        nextHeaderIndex += entriesToEvict;
      }
      return entriesToEvict;
    }
    
    private void insertIntoDynamicTable(Header entry) {
      int delta = hpackSize;
      

      if (delta > maxDynamicTableByteCount) {
        clearDynamicTable();
        return;
      }
      

      int bytesToRecover = dynamicTableByteCount + delta - maxDynamicTableByteCount;
      evictToRecoverBytes(bytesToRecover);
      
      if (headerCount + 1 > dynamicTable.length) {
        Header[] doubled = new Header[dynamicTable.length * 2];
        System.arraycopy(dynamicTable, 0, doubled, dynamicTable.length, dynamicTable.length);
        nextHeaderIndex = (dynamicTable.length - 1);
        dynamicTable = doubled;
      }
      int index = nextHeaderIndex--;
      dynamicTable[index] = entry;
      headerCount += 1;
      dynamicTableByteCount += delta;
    }
    
    void writeHeaders(List<Header> headerBlock)
      throws IOException
    {
      if (emitDynamicTableSizeUpdate) {
        if (smallestHeaderTableSizeSetting < maxDynamicTableByteCount)
        {
          writeInt(smallestHeaderTableSizeSetting, 31, 32);
        }
        emitDynamicTableSizeUpdate = false;
        smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
        writeInt(maxDynamicTableByteCount, 31, 32);
      }
      
      int i = 0; for (int size = headerBlock.size(); i < size; i++) {
        Header header = (Header)headerBlock.get(i);
        ByteString name = name.toAsciiLowercase();
        ByteString value = value;
        int headerIndex = -1;
        int headerNameIndex = -1;
        
        Integer staticIndex = (Integer)Hpack.NAME_TO_FIRST_INDEX.get(name);
        if (staticIndex != null) {
          headerNameIndex = staticIndex.intValue() + 1;
          if ((headerNameIndex > 1) && (headerNameIndex < 8))
          {



            if (Objects.equals(STATIC_HEADER_TABLE1value, value)) {
              headerIndex = headerNameIndex;
            } else if (Objects.equals(STATIC_HEADER_TABLEvalue, value)) {
              headerIndex = headerNameIndex + 1;
            }
          }
        }
        
        if (headerIndex == -1) {
          int j = nextHeaderIndex + 1; for (int length = dynamicTable.length; j < length; j++) {
            if (Objects.equals(dynamicTable[j].name, name)) {
              if (Objects.equals(dynamicTable[j].value, value)) {
                headerIndex = j - nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
                break; }
              if (headerNameIndex == -1) {
                headerNameIndex = j - nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
              }
            }
          }
        }
        
        if (headerIndex != -1)
        {
          writeInt(headerIndex, 127, 128);
        } else if (headerNameIndex == -1)
        {
          out.writeByte(64);
          writeByteString(name);
          writeByteString(value);
          insertIntoDynamicTable(header);
        } else if ((name.startsWith(Header.PSEUDO_PREFIX)) && (!Header.TARGET_AUTHORITY.equals(name)))
        {

          writeInt(headerNameIndex, 15, 0);
          writeByteString(value);
        }
        else {
          writeInt(headerNameIndex, 63, 64);
          writeByteString(value);
          insertIntoDynamicTable(header);
        }
      }
    }
    

    void writeInt(int value, int prefixMask, int bits)
    {
      if (value < prefixMask) {
        out.writeByte(bits | value);
        return;
      }
      

      out.writeByte(bits | prefixMask);
      value -= prefixMask;
      

      while (value >= 128) {
        int b = value & 0x7F;
        out.writeByte(b | 0x80);
        value >>>= 7;
      }
      out.writeByte(value);
    }
    
    void writeByteString(ByteString data) throws IOException {
      if ((useCompression) && (Huffman.get().encodedLength(data) < data.size())) {
        Buffer huffmanBuffer = new Buffer();
        Huffman.get().encode(data, huffmanBuffer);
        ByteString huffmanBytes = huffmanBuffer.readByteString();
        writeInt(huffmanBytes.size(), 127, 128);
        out.write(huffmanBytes);
      } else {
        writeInt(data.size(), 127, 0);
        out.write(data);
      }
    }
    
    void setHeaderTableSizeSetting(int headerTableSizeSetting) {
      this.headerTableSizeSetting = headerTableSizeSetting;
      int effectiveHeaderTableSize = Math.min(headerTableSizeSetting, 16384);
      

      if (maxDynamicTableByteCount == effectiveHeaderTableSize) { return;
      }
      if (effectiveHeaderTableSize < maxDynamicTableByteCount) {
        smallestHeaderTableSizeSetting = Math.min(smallestHeaderTableSizeSetting, effectiveHeaderTableSize);
      }
      
      emitDynamicTableSizeUpdate = true;
      maxDynamicTableByteCount = effectiveHeaderTableSize;
      adjustDynamicTableByteCount();
    }
    
    private void adjustDynamicTableByteCount() {
      if (maxDynamicTableByteCount < dynamicTableByteCount) {
        if (maxDynamicTableByteCount == 0) {
          clearDynamicTable();
        } else {
          evictToRecoverBytes(dynamicTableByteCount - maxDynamicTableByteCount);
        }
      }
    }
  }
  


  static ByteString checkLowercase(ByteString name)
    throws IOException
  {
    int i = 0; for (int length = name.size(); i < length; i++) {
      byte c = name.getByte(i);
      if ((c >= 65) && (c <= 90)) {
        throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
      }
    }
    return name;
  }
}
