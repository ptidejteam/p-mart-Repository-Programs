package net.suberic.util.swing;
import javax.swing.plaf.metal.MetalTheme;

/**
 * A UI which can dynamically set its theme.
 */

public interface ThemeSupporter {

  /**
   * Gets the Theme object from the ThemeManager which is appropriate
   * for this UI.
   */
  public MetalTheme getTheme(ThemeManager mgr);

  /**
   * Gets the currently configured Theme.
   */
  public MetalTheme getCurrentTheme();

  /**
   * Sets the Theme that this component is currently using.
   */
  public void setCurrentTheme(MetalTheme newTheme);

}
