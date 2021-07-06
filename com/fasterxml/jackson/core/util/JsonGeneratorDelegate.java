package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;





public class JsonGeneratorDelegate
  extends JsonGenerator
{
  protected JsonGenerator delegate;
  protected boolean delegateCopyMethods;
  
  public JsonGeneratorDelegate(JsonGenerator d)
  {
    this(d, true);
  }
  




  public JsonGeneratorDelegate(JsonGenerator d, boolean delegateCopyMethods)
  {
    delegate = d;
    this.delegateCopyMethods = delegateCopyMethods;
  }
  
  public Object getCurrentValue()
  {
    return delegate.getCurrentValue();
  }
  
  public void setCurrentValue(Object v)
  {
    delegate.setCurrentValue(v);
  }
  




  public JsonGenerator getDelegate()
  {
    return delegate;
  }
  





  public ObjectCodec getCodec() { return delegate.getCodec(); }
  
  public JsonGenerator setCodec(ObjectCodec oc) {
    delegate.setCodec(oc);
    return this;
  }
  
  public void setSchema(FormatSchema schema) { delegate.setSchema(schema); }
  public FormatSchema getSchema() { return delegate.getSchema(); }
  public Version version() { return delegate.version(); }
  public Object getOutputTarget() { return delegate.getOutputTarget(); }
  public int getOutputBuffered() { return delegate.getOutputBuffered(); }
  





  public boolean canUseSchema(FormatSchema schema)
  {
    return delegate.canUseSchema(schema);
  }
  
  public boolean canWriteTypeId() { return delegate.canWriteTypeId(); }
  
  public boolean canWriteObjectId() {
    return delegate.canWriteObjectId();
  }
  
  public boolean canWriteBinaryNatively() { return delegate.canWriteBinaryNatively(); }
  
  public boolean canOmitFields() {
    return delegate.canOmitFields();
  }
  





  public JsonGenerator enable(JsonGenerator.Feature f)
  {
    delegate.enable(f);
    return this;
  }
  
  public JsonGenerator disable(JsonGenerator.Feature f)
  {
    delegate.disable(f);
    return this;
  }
  
  public boolean isEnabled(JsonGenerator.Feature f) {
    return delegate.isEnabled(f);
  }
  

  public int getFeatureMask()
  {
    return delegate.getFeatureMask();
  }
  
  @Deprecated
  public JsonGenerator setFeatureMask(int mask) {
    delegate.setFeatureMask(mask);
    return this;
  }
  
  public JsonGenerator overrideStdFeatures(int values, int mask)
  {
    delegate.overrideStdFeatures(values, mask);
    return this;
  }
  
  public JsonGenerator overrideFormatFeatures(int values, int mask)
  {
    delegate.overrideFormatFeatures(values, mask);
    return this;
  }
  






  public JsonGenerator setPrettyPrinter(PrettyPrinter pp)
  {
    delegate.setPrettyPrinter(pp);
    return this;
  }
  

  public PrettyPrinter getPrettyPrinter() { return delegate.getPrettyPrinter(); }
  
  public JsonGenerator useDefaultPrettyPrinter() {
    delegate.useDefaultPrettyPrinter();
    return this;
  }
  
  public JsonGenerator setHighestNonEscapedChar(int charCode) { delegate.setHighestNonEscapedChar(charCode);
    return this;
  }
  
  public int getHighestEscapedChar() { return delegate.getHighestEscapedChar(); }
  

  public CharacterEscapes getCharacterEscapes() { return delegate.getCharacterEscapes(); }
  
  public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
    delegate.setCharacterEscapes(esc);
    return this;
  }
  
  public JsonGenerator setRootValueSeparator(SerializableString sep) { delegate.setRootValueSeparator(sep);
    return this;
  }
  



  public void writeStartArray()
    throws IOException
  {
    delegate.writeStartArray();
  }
  
  public void writeStartArray(int size) throws IOException { delegate.writeStartArray(size); }
  
  public void writeStartArray(Object forValue) throws IOException {
    delegate.writeStartArray(forValue);
  }
  
  public void writeStartArray(Object forValue, int size) throws IOException { delegate.writeStartArray(forValue, size); }
  
  public void writeEndArray() throws IOException {
    delegate.writeEndArray();
  }
  
  public void writeStartObject() throws IOException { delegate.writeStartObject(); }
  
  public void writeStartObject(Object forValue) throws IOException {
    delegate.writeStartObject(forValue);
  }
  
  public void writeStartObject(Object forValue, int size) throws IOException {
    delegate.writeStartObject(forValue, size);
  }
  
  public void writeEndObject() throws IOException {
    delegate.writeEndObject();
  }
  
  public void writeFieldName(String name) throws IOException {
    delegate.writeFieldName(name);
  }
  
  public void writeFieldName(SerializableString name) throws IOException
  {
    delegate.writeFieldName(name);
  }
  
  public void writeFieldId(long id) throws IOException
  {
    delegate.writeFieldId(id);
  }
  
  public void writeArray(int[] array, int offset, int length) throws IOException
  {
    delegate.writeArray(array, offset, length);
  }
  
  public void writeArray(long[] array, int offset, int length) throws IOException
  {
    delegate.writeArray(array, offset, length);
  }
  
  public void writeArray(double[] array, int offset, int length) throws IOException
  {
    delegate.writeArray(array, offset, length);
  }
  




  public void writeString(String text)
    throws IOException
  {
    delegate.writeString(text);
  }
  
  public void writeString(Reader reader, int len) throws IOException {
    delegate.writeString(reader, len);
  }
  
  public void writeString(char[] text, int offset, int len) throws IOException {
    delegate.writeString(text, offset, len);
  }
  
  public void writeString(SerializableString text) throws IOException { delegate.writeString(text); }
  
  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
    delegate.writeRawUTF8String(text, offset, length);
  }
  
  public void writeUTF8String(byte[] text, int offset, int length) throws IOException { delegate.writeUTF8String(text, offset, length); }
  




  public void writeRaw(String text)
    throws IOException
  {
    delegate.writeRaw(text);
  }
  
  public void writeRaw(String text, int offset, int len) throws IOException { delegate.writeRaw(text, offset, len); }
  
  public void writeRaw(SerializableString raw) throws IOException {
    delegate.writeRaw(raw);
  }
  
  public void writeRaw(char[] text, int offset, int len) throws IOException { delegate.writeRaw(text, offset, len); }
  
  public void writeRaw(char c) throws IOException {
    delegate.writeRaw(c);
  }
  
  public void writeRawValue(String text) throws IOException { delegate.writeRawValue(text); }
  
  public void writeRawValue(String text, int offset, int len) throws IOException {
    delegate.writeRawValue(text, offset, len);
  }
  
  public void writeRawValue(char[] text, int offset, int len) throws IOException { delegate.writeRawValue(text, offset, len); }
  
  public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
    delegate.writeBinary(b64variant, data, offset, len);
  }
  
  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException { return delegate.writeBinary(b64variant, data, dataLength); }
  




  public void writeNumber(short v)
    throws IOException
  {
    delegate.writeNumber(v);
  }
  
  public void writeNumber(int v) throws IOException { delegate.writeNumber(v); }
  
  public void writeNumber(long v) throws IOException {
    delegate.writeNumber(v);
  }
  
  public void writeNumber(BigInteger v) throws IOException { delegate.writeNumber(v); }
  
  public void writeNumber(double v) throws IOException {
    delegate.writeNumber(v);
  }
  
  public void writeNumber(float v) throws IOException { delegate.writeNumber(v); }
  
  public void writeNumber(BigDecimal v) throws IOException {
    delegate.writeNumber(v);
  }
  
  public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException { delegate.writeNumber(encodedValue); }
  
  public void writeBoolean(boolean state) throws IOException {
    delegate.writeBoolean(state);
  }
  
  public void writeNull() throws IOException { delegate.writeNull(); }
  




  public void writeOmittedField(String fieldName)
    throws IOException
  {
    delegate.writeOmittedField(fieldName);
  }
  



  public void writeObjectId(Object id)
    throws IOException
  {
    delegate.writeObjectId(id);
  }
  
  public void writeObjectRef(Object id) throws IOException { delegate.writeObjectRef(id); }
  
  public void writeTypeId(Object id) throws IOException {
    delegate.writeTypeId(id);
  }
  
  public void writeEmbeddedObject(Object object) throws IOException { delegate.writeEmbeddedObject(object); }
  





  public void writeObject(Object pojo)
    throws IOException
  {
    if (delegateCopyMethods) {
      delegate.writeObject(pojo);
      return;
    }
    if (pojo == null) {
      writeNull();
    } else {
      ObjectCodec c = getCodec();
      if (c != null) {
        c.writeValue(this, pojo);
        return;
      }
      _writeSimpleObject(pojo);
    }
  }
  
  public void writeTree(TreeNode tree) throws IOException
  {
    if (delegateCopyMethods) {
      delegate.writeTree(tree);
      return;
    }
    
    if (tree == null) {
      writeNull();
    } else {
      ObjectCodec c = getCodec();
      if (c == null) {
        throw new IllegalStateException("No ObjectCodec defined");
      }
      c.writeTree(this, tree);
    }
  }
  













  public void copyCurrentEvent(JsonParser p)
    throws IOException
  {
    if (delegateCopyMethods) delegate.copyCurrentEvent(p); else {
      super.copyCurrentEvent(p);
    }
  }
  
  public void copyCurrentStructure(JsonParser p) throws IOException {
    if (delegateCopyMethods) delegate.copyCurrentStructure(p); else {
      super.copyCurrentStructure(p);
    }
  }
  



  public JsonStreamContext getOutputContext()
  {
    return delegate.getOutputContext();
  }
  




  public void flush()
    throws IOException { delegate.flush(); }
  public void close() throws IOException { delegate.close(); }
  




  public boolean isClosed()
  {
    return delegate.isClosed();
  }
}
