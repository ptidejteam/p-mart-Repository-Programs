package net.suberic.pooka.gui.filter;

/**
 * This defines a filter which can be used on a Folder Table.  These filters
 * should do things like change colors, fonts, etc.  They probably can't
 * or shouldn't be used actually to remove messages from the display.
 */
public interface DisplayFilter extends net.suberic.pooka.filter.FilterAction {
   
    /**
     * Applies the filter to the given component.
     */
    public void apply(java.awt.Component target);

}
