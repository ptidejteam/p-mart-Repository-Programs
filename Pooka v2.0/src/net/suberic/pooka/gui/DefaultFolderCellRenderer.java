package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.pooka.cache.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
import java.util.Date;
import java.util.Calendar;

/**
 * This class overrides the default TableCellRenderer in order to
 * show things like unread messages, etc.
 */

public class DefaultFolderCellRenderer extends DefaultTableCellRenderer {
  static Font unreadFont = null;
  static Color defaultColor = null;
  static Color uncachedColor = null;

  public static Font getUnreadFont() {
    return unreadFont;
  }

  public static void setUnreadFont(Font newValue) {
    unreadFont = newValue;
  }

  public static Color getDefaultColor() {
    if (defaultColor != null)
      return defaultColor;
    else {
      defaultColor = Color.getColor(Pooka.getProperty("CachedMessages.notCached.foregroundColor", "black"));
      return defaultColor;

    }
  }

  public static Color getUncachedColor() {
    if (uncachedColor != null)
      return uncachedColor;
    else {
      String colorString = Pooka.getProperty("CachedMessages.notCached.foregroundColor", "red");
      try {
        uncachedColor = new Color(Integer.parseInt(colorString));
      } catch (Exception e) {
        uncachedColor = Color.red;
      }
      return uncachedColor;
    }
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    Component returnValue = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

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
        if (! (msg.isSeen())) {
          if (getUnreadFont() != null) {
            returnValue.setFont(getUnreadFont());
          } else {
            // create the new font.
            String fontStyle = Pooka.getProperty("FolderTable.UnreadStyle", "");
            Font f = null;

            if (fontStyle.equalsIgnoreCase("BOLD"))
              f = returnValue.getFont().deriveFont(Font.BOLD);
            else if (fontStyle.equalsIgnoreCase("ITALIC"))
              f = returnValue.getFont().deriveFont(Font.ITALIC);

            if (f == null)
              f = returnValue.getFont();

            setUnreadFont(f);
            returnValue.setFont(f);
          }
        }

        if (msg.getMessageInfo().getFolderInfo() instanceof CachingFolderInfo) {
          CachingFolderInfo cfi = (CachingFolderInfo) msg.getMessageInfo().getFolderInfo();
          if (cfi.showCacheInfo()) {
            CachingMimeMessage cmm = (CachingMimeMessage) msg.getMessageInfo().getMessage();
            long uid = cmm.getUID();
            if (cfi.isCached(uid)) {
              returnValue.setForeground(getDefaultColor());
            } else {
              returnValue.setForeground(getUncachedColor());
            }
          } else {
            returnValue.setForeground(getDefaultColor());
          }
        }
      }

    }

    return returnValue;
  }
} //end class DefaultFolderCellRenderer
