/**
 *
 * WHAT IS THE LICENSE OF THIS FILE???
 *
 * Bryan, you included this file, could you please update this section?
 *
 *
 *
 */

package pcgen.gui;

import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;

/**
 * <code>AbstractCellEditor</code>.
 *
 * @author ???
 * @version $Revision: 1.1 $
 */

class AbstractCellEditor implements CellEditor
{

	private EventListenerList listenerList = new EventListenerList();

	public final Object getCellEditorValue()
	{
		return null;
	}

	public boolean isCellEditable(EventObject e)
	{
		return true;
	}

	public final boolean shouldSelectCell(EventObject anEvent)
	{
		return false;
	}

	public final boolean stopCellEditing()
	{
		return true;
	}

	public final void cancelCellEditing()
	{
	}

	public final void addCellEditorListener(CellEditorListener l)
	{
		listenerList.add(CellEditorListener.class, l);
	}

	public final void removeCellEditorListener(CellEditorListener l)
	{
		listenerList.remove(CellEditorListener.class, l);
	}

}
