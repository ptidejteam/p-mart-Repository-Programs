/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.dictaccregwizz;

import java.awt.Component;
import java.util.*;

import javax.swing.*;

import net.java.dict4j.*;

/**
 * Class managing the list of strategies
 * 
 * @author ROTH Damien
 */
public class StrategiesList
    extends JList
{
    private ListModel model;
    private CellRenderer renderer;
    
    /**
     * Create an instance of the <tt>StrategiesList</tt>
     */
    public StrategiesList()
    {
        super();
        
        this.model = new ListModel();
        this.renderer = new CellRenderer();
        
        this.setCellRenderer(this.renderer);
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setVisibleRowCount(6);
    }
    
    /**
     * Stores a new set of strategies
     * @param strategies List of strategies
     */
    public void setStrategies(List<Strategy> strategies)
    {
        this.model.setStrategies(strategies);
    }
    
    /**
     * Remove all the strategies of the list
     */
    public void clear()
    {
        this.model.clear();
    }
    
    /**
     * Automatic selection of strategies
     * @param initStrategy
     */
    public void autoSelectStrategy(String initStrategy)
    {
        int index = -1;
        
        if (initStrategy.length() > 0)
        {   // saved strategy
            index = this.model.indexOf(initStrategy);
        }
        if (index < 0)
        {
            // First case : levenstein distance
            index = this.model.indexOf("lev");
        }
        if (index < 0)
        {
            // Second case : soundex
            index = this.model.indexOf("soundex");
        }
        if (index < 0)
        {
            // Last case : prefix
            index = this.model.indexOf("prefix");
        }
        
        // If the index is still < 0, we select the first index
        if (index < 0)
        {
            index = 0;
        }
        if (index < this.getVisibleRowCount())
        {
            // If the index is visible row, we don't need to scroll
            this.setSelectedIndex(index);
        }
        else
        {
            // Otherwise, we scroll to the selected value
            this.setSelectedValue(this.model.getElementAt(index), true);
        }
    }
    
    /**
     * Class managing the list datas
     * 
     * @author ROTH Damien
     */
    static class ListModel
        extends AbstractListModel
    {
        List<Strategy> data;
        
        /**
         * Create an instance of <tt>ListModel</tt>
         */
        public ListModel()
        {
            this.data = new ArrayList<Strategy>();
        }
        
        /**
         * Stores the strategies into this model
         * @param strategies the strategies list
         */
        public void setStrategies(List<Strategy> strategies)
        {
            this.data = strategies;
            fireContentsChanged(this, 0, this.data.size());
        }
        
        /**
         * Remove all the strategies of the list
         */
        public void clear()
        {
            this.data.clear();
        }
        
        /**
         * Implements <tt>ListModel.getElementAt</tt>
         */
        public Object getElementAt(int row)
        {
            return this.data.get(row);
        }

        /**
         * Implements <tt>ListModel.getSize</tt>
         */
        public int getSize()
        {
            return this.data.size();
        }
        
        /**
         * Find the index of a strategie
         * @param strategyCode the code of the strategy
         * @return the index of the strategy
         */
        public int indexOf(String strategyCode)
        {
            for (int i=0; i<this.data.size(); i++)
            {
                if (this.data.get(i).getCode().equals(strategyCode))
                {
                    return i;
                }
            }
            return -1;
        }
    }
    
    /**
     * Class managing the cell rendering
     * 
     * @author ROTH Damien
     */
    static class CellRenderer
        extends JLabel
        implements ListCellRenderer
    {
        /**
         * implements <tt>ListCellRenderer.getListCellRendererComponent</tt>
         */
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus)
        {
            Strategy strategy = (Strategy) value;
            this.setText(strategy.getName());
            
            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            
            return this;
        }
    }
}

