/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.pluginmanager;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.java.sip.communicator.util.swing.*;

import org.osgi.framework.*;

public class PluginManagerPanel
    extends TransparentPanel
{
    private final JTable pluginTable = new JTable();

    private final PluginTableModel tableModel = new PluginTableModel();

    private final ManageButtonsPanel buttonsPanel;

    public PluginManagerPanel()
    {
        super(new BorderLayout());
        JScrollPane pluginListScrollPane = new JScrollPane();

        pluginTable.setModel(tableModel);

        TableColumn col = pluginTable.getColumnModel().getColumn(0);
        col.setCellRenderer(new PluginListCellRenderer());

        PluginListSelectionListener selectionListener =
            new PluginListSelectionListener();

        pluginTable.getSelectionModel().addListSelectionListener(
            selectionListener);
        pluginTable.getColumnModel().getSelectionModel()
            .addListSelectionListener(selectionListener);

        pluginTable.setRowHeight(48);

        pluginTable.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        pluginTable.setTableHeader(null);

        buttonsPanel = new ManageButtonsPanel(pluginTable);

        this.add(pluginListScrollPane, BorderLayout.CENTER);

        this.add(buttonsPanel, BorderLayout.EAST);

        pluginListScrollPane.getViewport().add(pluginTable);

        pluginListScrollPane
            .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pluginListScrollPane.getVerticalScrollBar().setUnitIncrement(30);

        PluginManagerActivator.bundleContext
            .addBundleListener(new PluginListBundleListener());
    }

    /**
     * Listens for events triggered when a selection is made in the plugin list.
     */
    private class PluginListSelectionListener
        implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            int selectedRow = pluginTable.getSelectedRow();

            if (selectedRow == -1)
                return;

            Bundle selectedBundle =
                (Bundle) pluginTable.getValueAt(selectedRow, 0);


            if(PluginManagerActivator.isSystemBundle(selectedBundle))
            {
                buttonsPanel.enableUninstallButton(false);
                buttonsPanel.enableDeactivateButton(false);

                if (selectedBundle.getState() != Bundle.ACTIVE)
                {
                    buttonsPanel.enableActivateButton(true);
                }
                else
                {
                    buttonsPanel.enableActivateButton(false);
                }
            }
            else
            {
                buttonsPanel.enableUninstallButton(true);

                if (selectedBundle.getState() != Bundle.ACTIVE)
                {
                    buttonsPanel.enableActivateButton(true);
                    buttonsPanel.enableDeactivateButton(false);
                }
                else
                {
                    buttonsPanel.enableActivateButton(false);
                    buttonsPanel.enableDeactivateButton(true);
                }
            }

            // every bundle can be updated
            buttonsPanel.enableUpdateButton(true);
        }
    }

    /**
     * Listens for <tt>BundleEvents</tt> triggered by the bundle context.
     */
    private class PluginListBundleListener
        implements BundleListener
    {
        public void bundleChanged(BundleEvent event)
        {
            tableModel.update();

            if (event.getType() == BundleEvent.INSTALLED)
            {
                pluginTable.scrollRectToVisible(new Rectangle(0, pluginTable
                    .getHeight(), 1, pluginTable.getHeight()));
            }
        }
    }
}
