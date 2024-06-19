package net.suberic.pooka.gui.filter;
import java.awt.*;


/**
 * A DisplayFilter which changes the color of the given messages in
 * the FolderDisplayPanel to the selected color.
 */
public class ColorDisplayFilter implements DisplayFilter {
  
  Color newColor;
  
  /**
   * Creates a new ColorDisplayFilter.
   */
  public ColorDisplayFilter() {
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
    try {
      newColor = new Color(Integer.parseInt(net.suberic.pooka.Pooka.getProperty(propertyName + ".rgb", "742")));
    } catch (Exception e) {
      newColor = new Color(742);
    }
    
  }
  
  /**
   * Applies the filter to the given component.
   */
  public void apply(java.awt.Component target) {
    target.setForeground(newColor);
  }
}
