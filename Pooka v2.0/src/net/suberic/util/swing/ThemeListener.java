package net.suberic.util.swing;

/**
 * An interface which allows classes to listen to changes to Themes.
 */
public interface ThemeListener {

  /**
   * Called when the specifics of a Theme change.
   */
  public void themeChanged(ConfigurableMetalTheme theme);

}
