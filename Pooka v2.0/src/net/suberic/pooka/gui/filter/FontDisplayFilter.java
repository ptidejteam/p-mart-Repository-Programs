package net.suberic.pooka.gui.filter;
import java.awt.*;

/**
 * This represents a DisplayFilter which modifies the font of the
 * given messages.
 */
public class FontDisplayFilter implements DisplayFilter {
  
  int fontStyle = -1;
  
  java.util.HashMap derivedFontMap;
  
  /**
   * Creates a new FontDisplayFilter.
   */
  public FontDisplayFilter() {
  }
  
  /**
   * a no-op.
   */
  public java.util.List performFilter(java.util.List tmp) {
    return tmp;
  }
  
  /**
   * Configures the filter from the given property.
   */
  public void initializeFilter(String propertyName) {
    derivedFontMap = new java.util.HashMap();
    
    String fontString = net.suberic.pooka.Pooka.getProperty(propertyName + ".style", "bold");
    if (fontString.equalsIgnoreCase("bold"))
      fontStyle = Font.BOLD;
    else if (fontString.equalsIgnoreCase("italic"))
      fontStyle = Font.ITALIC;
    else if (fontString.equalsIgnoreCase("plain"))
      fontStyle = Font.PLAIN;
  }
  
  /**
   * Applies the filter to the given component.
   */
  public void apply(java.awt.Component target) {
    Font currentFont = target.getFont();
    Font derivedFont = (Font) derivedFontMap.get(currentFont);
    if (derivedFont != null) {
      target.setFont(derivedFont);
    } else {
      derivedFont = currentFont.deriveFont(fontStyle);
      derivedFontMap.put(currentFont, derivedFont);
      target.setFont(derivedFont);
    }
  }
}
