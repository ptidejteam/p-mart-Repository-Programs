package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.pooka.cache.*;
import net.suberic.pooka.gui.filter.DisplayFilter;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
import java.util.Date;
import java.util.Calendar;

/**
 * This class overrides the default TableCellRenderer in order to
 * show things like unread messages, etc.
 */

public class FilterFolderCellRenderer extends DefaultTableCellRenderer {

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    Component returnValue = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    }
    else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

    if (value instanceof TableCellIcon) {
      TableCellIcon tcIcon = (TableCellIcon)value;
      Component icon = tcIcon.getIcon();
      if (icon != null) {
        icon.setBackground(returnValue.getBackground());
      }
      return icon;
    } else if (value instanceof RowCounter) {
      if (returnValue instanceof JLabel)
        ((JLabel)returnValue).setText(Integer.toString(row + 1));
      else {
        JLabel label = new JLabel(Integer.toString(row + 1));
        label.setBackground(returnValue.getBackground());
        label.setForeground(returnValue.getForeground());
        label.setFont(returnValue.getFont());
        returnValue = label;
      }
    } else if (value instanceof Date) {
      Date displayDate = (Date)value;
      String dateText = null;
      Calendar current = Calendar.getInstance();
      current.set(Calendar.HOUR_OF_DAY, 0);
      current.set(Calendar.MINUTE, 0);
      if (current.getTime().before(displayDate)) {
        dateText = Pooka.getDateFormatter().todayFormat.format(displayDate);
      } else {
        current.add(Calendar.DAY_OF_YEAR, (current.getMaximum(Calendar.DAY_OF_WEEK) - 1) * -1);
        if (current.getTime().before(displayDate)) {
          dateText = Pooka.getDateFormatter().thisWeekFormat.format(displayDate);
        } else {
          dateText = Pooka.getDateFormatter().shortFormat.format(displayDate);
        }
      }

      if (returnValue instanceof JLabel)
        ((JLabel)returnValue).setText(dateText);
      else {
        JLabel label = new JLabel(dateText);
        label.setBackground(returnValue.getBackground());
        label.setForeground(returnValue.getForeground());
        label.setFont(returnValue.getFont());
        returnValue = label;
      }
    }


    FolderTableModel ftm = null;

    if (table.getModel() instanceof FolderTableModel) {
      ftm = (FolderTableModel) table.getModel();
      MessageProxy msg = ftm.getMessageProxy(row);

      if ( msg != null) {
        MessageFilter[] matchingFilters = msg.getMatchingFilters();
        for (int i = 0; i < matchingFilters.length; i++)
          ((DisplayFilter)matchingFilters[i].getAction()).apply(returnValue);
      }
    }
    return returnValue;
  }
} //end class DefaultFolderCellRenderer
