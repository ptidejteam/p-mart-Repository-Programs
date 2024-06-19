package com.taursys.html;

import java.text.Format;

import com.taursys.xml.ComponentFactory;

public class NodeDescriptor {
  private String id;
  private String valueHolderAlias;
  private String propertyName;
  private String formatDescriptor;
  private Format format;
  private String formatPattern;

  public NodeDescriptor() {
  }
  
  public boolean isTemplateNode() {
    return valueHolderAlias != null &&
      ComponentFactory.TEMPLATE_NODE.equals(propertyName);
  }
  
  /**
   * @return Returns the formatDescriptor.
   */
  public String getFormatDescriptor() {
    return formatDescriptor;
  }

  /**
   * @param formatDescriptor The formatDescriptor to set.
   */
  public void setFormatDescriptor(String formatDescriptor) {
    this.formatDescriptor = formatDescriptor;
  }

  /**
   * @return Returns the propertyName.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * @param propertyName The propertyName to set.
   */
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  /**
   * @return Returns the valueHolderAlias.
   */
  public String getValueHolderAlias() {
    return valueHolderAlias;
  }

  /**
   * @param valueHolderAlias The valueHolderAlias to set.
   */
  public void setValueHolderAlias(String valueHolderAlias) {
    this.valueHolderAlias = valueHolderAlias;
  }

  /**
   * @return Returns the format.
   */
  public Format getFormat() {
    return format;
  }

  /**
   * @param format The format to set.
   */
  public void setFormat(Format format) {
    this.format = format;
  }

  /**
   * @return Returns the pattern.
   */
  public String getFormatPattern() {
    return formatPattern;
  }

  /**
   * @param pattern The pattern to set.
   */
  public void setFormatPattern(String pattern) {
    this.formatPattern = pattern;
  }

  /**
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }

}
