/*
 * Created on Sep 9, 2003
 *
 */
package plugin.initiative.gui;

import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @author LodgeR
 * <p>GradesViewColumnModel</p>
 * <p>edit method description . . .</p>
 */
public class AutoSizingColumnModel extends DefaultTableColumnModel
{
   private static final int COLUMN_WIDTH_PADDING = 10;
   JTable m_table = null;

   /* (non-Javadoc)
    * @see javax.swing.table.TableColumnModel#addColumn(javax.swing.table.TableColumn)
    */
   public void addColumn(TableColumn aColumn)
   {
      setColumnPreferredWidth(aColumn);
      super.addColumn(aColumn);
   }

   public void referenceTable(JTable table)
   {
      m_table = table;
   }

   public void setColumnPreferredWidth(TableColumn aColumn)
   {
      if (m_table != null)
      {
         TableCellRenderer renderer = null;
         int rowCount = m_table.getRowCount();
         int headerWidth = 0, contentsWidth = 0;
         if (aColumn.getHeaderRenderer() != null)
         {
            renderer = aColumn.getHeaderRenderer();
         }
         else
         {
            renderer = m_table.getTableHeader().getDefaultRenderer();
         }
         if (renderer != null)
         {
            headerWidth = renderer.getTableCellRendererComponent(m_table,
                                                                 aColumn.getHeaderValue(),
                                                                 false,
                                                                 false,
                                                                 0,
                                                                 0).getPreferredSize().width;
         }
         renderer = m_table.getDefaultRenderer(m_table.getModel().getColumnClass(getColumnCount()));
         if (renderer != null)
         {
            for (int row = 0; row < rowCount; row++)
            {
               contentsWidth = Math.max(contentsWidth,
                                        renderer.getTableCellRendererComponent(m_table,
                                                                               m_table.getModel().getValueAt(row, getColumnCount()),
                                                                               false,
                                                                               false,
                                                                               row,
                                                                               0).getPreferredSize().width);
            }
         }
         aColumn.setPreferredWidth(Math.max(headerWidth, contentsWidth)
                                   + getColumnMargin()
                                   + COLUMN_WIDTH_PADDING);
      }
      else
      {
         aColumn.setPreferredWidth(150);
      }
   }

   public int getTotalPreferredWidth()
   {
      int returnValue = 0;
      Enumeration columns = getColumns();
      while (columns.hasMoreElements())
      {
         returnValue += ((TableColumn)columns.nextElement()).getPreferredWidth();
      }
      return returnValue;
   }

}
