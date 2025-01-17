/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.callhistoryform;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * The <tt>CallList</tt> is the component that contains history call records.
 * 
 * @author Yana Stamcheva
 */
public class CallList
    extends JList
    implements MouseListener
{
    public CallList()
    {
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        this.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        this.setCellRenderer(new CallListCellRenderer());
        
        this.setModel(new CallListModel());

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        this.addMouseListener(this);
    }
    
    public void addItem(Object item)
    {
        ((CallListModel)this.getModel()).addElement(item);
    }
    
    public void addItem(Object item, int index)
    {
        ((CallListModel)this.getModel()).addElement(index, item);
    }
    
    public void removeAll()
    {
        ((CallListModel)this.getModel()).removeAll();
    }
    
    /**
     * Closes or opens a group of calls.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {

            CallListModel listModel = (CallListModel) this.getModel();

            Object element = listModel.getElementAt(this.getSelectedIndex());

            if (element instanceof String) {
                if (listModel.isDateClosed(element)) {
                    listModel.openDate(element);
                } else {
                    listModel.closeDate(element);
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {}
    
    public void mouseExited(MouseEvent e)
    {}

    public void mousePressed(MouseEvent e)
    {}

    public void mouseReleased(MouseEvent e)
    {}
}
