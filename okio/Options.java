package okio;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;













public final class Options
  extends AbstractList<ByteString>
  implements RandomAccess
{
  final ByteString[] byteStrings;
  final int[] trie;
  
  private Options(ByteString[] byteStrings, int[] trie)
  {
    this.byteStrings = byteStrings;
    this.trie = trie;
  }
  
  public static Options of(ByteString... byteStrings) {
    if (byteStrings.length == 0)
    {
      return new Options(new ByteString[0], new int[] { 0, -1 });
    }
    


    List<ByteString> list = new ArrayList(Arrays.asList(byteStrings));
    Collections.sort(list);
    List<Integer> indexes = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      indexes.add(Integer.valueOf(-1));
    }
    for (int i = 0; i < list.size(); i++) {
      int sortedIndex = Collections.binarySearch(list, byteStrings[i]);
      indexes.set(sortedIndex, Integer.valueOf(i));
    }
    if (((ByteString)list.get(0)).size() == 0) {
      throw new IllegalArgumentException("the empty byte string is not a supported option");
    }
    
    ByteString prefix;
    
    int b;
    for (int a = 0; a < list.size(); a++) {
      prefix = (ByteString)list.get(a);
      for (b = a + 1; b < list.size();) {
        ByteString byteString = (ByteString)list.get(b);
        if (!byteString.startsWith(prefix)) break;
        if (byteString.size() == prefix.size()) {
          throw new IllegalArgumentException("duplicate option: " + byteString);
        }
        if (((Integer)indexes.get(b)).intValue() > ((Integer)indexes.get(a)).intValue()) {
          list.remove(b);
          indexes.remove(b);
        } else {
          b++;
        }
      }
    }
    
    Buffer trieBytes = new Buffer();
    buildTrieRecursive(0L, trieBytes, 0, list, 0, list.size(), indexes);
    
    int[] trie = new int[intCount(trieBytes)];
    for (int i = 0; i < trie.length; i++) {
      trie[i] = trieBytes.readInt();
    }
    if (!trieBytes.exhausted()) {
      throw new AssertionError();
    }
    
    return new Options((ByteString[])byteStrings.clone(), trie);
  }
  






























  private static void buildTrieRecursive(long nodeOffset, Buffer node, int byteStringOffset, List<ByteString> byteStrings, int fromIndex, int toIndex, List<Integer> indexes)
  {
    if (fromIndex >= toIndex) throw new AssertionError();
    for (int i = fromIndex; i < toIndex; i++) {
      if (((ByteString)byteStrings.get(i)).size() < byteStringOffset) { throw new AssertionError();
      }
    }
    ByteString from = (ByteString)byteStrings.get(fromIndex);
    ByteString to = (ByteString)byteStrings.get(toIndex - 1);
    int prefixIndex = -1;
    

    if (byteStringOffset == from.size()) {
      prefixIndex = ((Integer)indexes.get(fromIndex)).intValue();
      fromIndex++;
      from = (ByteString)byteStrings.get(fromIndex);
    }
    
    if (from.getByte(byteStringOffset) != to.getByte(byteStringOffset))
    {
      int selectChoiceCount = 1;
      for (int i = fromIndex + 1; i < toIndex; i++)
      {
        if (((ByteString)byteStrings.get(i - 1)).getByte(byteStringOffset) != ((ByteString)byteStrings.get(i)).getByte(byteStringOffset)) {
          selectChoiceCount++;
        }
      }
      

      long childNodesOffset = nodeOffset + intCount(node) + 2L + selectChoiceCount * 2;
      
      node.writeInt(selectChoiceCount);
      node.writeInt(prefixIndex);
      
      for (int i = fromIndex; i < toIndex; i++) {
        byte rangeByte = ((ByteString)byteStrings.get(i)).getByte(byteStringOffset);
        if ((i == fromIndex) || (rangeByte != ((ByteString)byteStrings.get(i - 1)).getByte(byteStringOffset))) {
          node.writeInt(rangeByte & 0xFF);
        }
      }
      
      Buffer childNodes = new Buffer();
      int rangeStart = fromIndex;
      while (rangeStart < toIndex) {
        byte rangeByte = ((ByteString)byteStrings.get(rangeStart)).getByte(byteStringOffset);
        int rangeEnd = toIndex;
        for (int i = rangeStart + 1; i < toIndex; i++) {
          if (rangeByte != ((ByteString)byteStrings.get(i)).getByte(byteStringOffset)) {
            rangeEnd = i;
            break;
          }
        }
        
        if ((rangeStart + 1 == rangeEnd) && 
          (byteStringOffset + 1 == ((ByteString)byteStrings.get(rangeStart)).size()))
        {
          node.writeInt(((Integer)indexes.get(rangeStart)).intValue());
        }
        else {
          node.writeInt((int)(-1L * (childNodesOffset + intCount(childNodes))));
          buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd, indexes);
        }
        







        rangeStart = rangeEnd;
      }
      
      node.write(childNodes, childNodes.size());
    }
    else
    {
      int scanByteCount = 0;
      int i = byteStringOffset; for (int max = Math.min(from.size(), to.size()); i < max; i++) {
        if (from.getByte(i) != to.getByte(i)) break;
        scanByteCount++;
      }
      




      long childNodesOffset = nodeOffset + intCount(node) + 2L + scanByteCount + 1L;
      
      node.writeInt(-scanByteCount);
      node.writeInt(prefixIndex);
      
      for (int i = byteStringOffset; i < byteStringOffset + scanByteCount; i++) {
        node.writeInt(from.getByte(i) & 0xFF);
      }
      
      if (fromIndex + 1 == toIndex)
      {
        if (byteStringOffset + scanByteCount != ((ByteString)byteStrings.get(fromIndex)).size()) {
          throw new AssertionError();
        }
        node.writeInt(((Integer)indexes.get(fromIndex)).intValue());
      }
      else {
        Buffer childNodes = new Buffer();
        node.writeInt((int)(-1L * (childNodesOffset + intCount(childNodes))));
        buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + scanByteCount, byteStrings, fromIndex, toIndex, indexes);
        






        node.write(childNodes, childNodes.size());
      }
    }
  }
  
  public ByteString get(int i) {
    return byteStrings[i];
  }
  
  public final int size() {
    return byteStrings.length;
  }
  
  private static int intCount(Buffer trieBytes) {
    return (int)(trieBytes.size() / 4L);
  }
}
