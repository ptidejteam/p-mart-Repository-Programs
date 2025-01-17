/*
 * InfoCompanions.java
 *
 * Copyright (C) 2001 Thomas G. W. Epperly
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 2, 2001, 1:34 PM
 */

package pcgen.gui;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import java.util.Vector;

/**
 * This is a bare bones companions text editor.
 *
 * @author  Tom Epperly <tepperly@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class InfoCompanions extends javax.swing.JPanel
{
  
  /** Creates new form InfoCompanions */
  public InfoCompanions()
  {
    initComponents();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents()//GEN-BEGIN:initComponents
  {
    d_companionLabel = new javax.swing.JLabel();
    d_companionArea = new javax.swing.JScrollPane();
    d_companionText = new javax.swing.JTextArea();

    setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gridBagConstraints1;

    addComponentListener(new java.awt.event.ComponentAdapter()
    {
      public void componentShown(java.awt.event.ComponentEvent evt)
      {
        initializeContent(evt);
      }
      public void componentHidden(java.awt.event.ComponentEvent evt)
      {
        storeContent(evt);
      }
    });

    d_companionLabel.setText("Companions");
    gridBagConstraints1 = new java.awt.GridBagConstraints();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    add(d_companionLabel, gridBagConstraints1);

    d_companionText.setLineWrap(true);
    d_companionText.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(java.awt.event.FocusEvent evt)
      {
        updatePC(evt);
      }
    });

    d_companionArea.setViewportView(d_companionText);

    gridBagConstraints1 = new java.awt.GridBagConstraints();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 1;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.weightx = 1.0;
    gridBagConstraints1.weighty = 1.0;
    add(d_companionArea, gridBagConstraints1);

  }//GEN-END:initComponents

  private void storeContent(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_storeContent
  {//GEN-HEADEREND:event_storeContent
    if (Globals.currentPC != null && d_shown)
    {
      java.util.ArrayList ml = Globals.currentPC.miscList();
      String currentCore = (String)ml.get(1);
      String currentDisplay = d_companionText.getText();
      if (!currentDisplay.equals(currentCore))
      {
        ml.set(1, currentDisplay);
        Globals.currentPC.setDirty(true);
      }
    }
  }//GEN-LAST:event_storeContent

  private void initializeContent(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_initializeContent
  {//GEN-HEADEREND:event_initializeContent
    if (Globals.currentPC != null)
    {
      String currentCore = (String)Globals.currentPC.miscList().get(1);
      String currentDisplay = d_companionText.getText();
      if (!currentDisplay.equals(currentCore))
      {
        d_companionText.setText(currentCore);
      }
    }
    else
    {
      d_companionText.setText("");
    }
    requestDefaultFocus();
    d_shown = true;
  }//GEN-LAST:event_initializeContent

  private void updatePC(java.awt.event.FocusEvent evt)//GEN-FIRST:event_updatePC
  {//GEN-HEADEREND:event_updatePC
    PlayerCharacter aPC = Globals.currentPC;
    String origText = (String)Globals.currentPC.miscList().get(1);
    String newText = d_companionText.getText();
    if ((aPC != null) &&
        (Globals.currentPC.miscList().size() > 1) &&
        !newText.equals(origText)
    )
    {
      Globals.currentPC.miscList().set(1,
        newText);
      Globals.currentPC.setDirty(true);
    }
  }//GEN-LAST:event_updatePC

  private boolean d_shown = false;
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel d_companionLabel;
  private javax.swing.JScrollPane d_companionArea;
  private javax.swing.JTextArea d_companionText;
  // End of variables declaration//GEN-END:variables
    
}
