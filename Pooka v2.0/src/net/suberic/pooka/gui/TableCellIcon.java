package net.suberic.pooka.gui;
import java.awt.Component;

public interface TableCellIcon extends Comparable {

    /**
     * This method should return the appropriate component depending on the
     * values of the particular TableCellIcon.  
     */
    public Component getIcon();
}
