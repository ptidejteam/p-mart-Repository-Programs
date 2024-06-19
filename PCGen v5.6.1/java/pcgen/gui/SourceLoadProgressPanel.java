/*
 * SourceLoadProgressPanel.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.1 $
 *
 * Last Editor: $Author: vauchers $
 *
 * Last Edited: $Date: 2006/02/21 01:33:07 $
 *
 */
package pcgen.gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class SourceLoadProgressPanel extends JPanel {

	private javax.swing.JProgressBar jProgressBar = null;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JTextArea jTextArea = null;
	private JScrollPane scrollPane = null;
	/**
	 * This method initializes 
	 * 
	 */
	public SourceLoadProgressPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
        consGridBagConstraints3.gridx = 0;
        consGridBagConstraints3.gridy = 1;
        consGridBagConstraints3.ipadx = 0;
        consGridBagConstraints3.ipady = 0;
        consGridBagConstraints3.weightx = 1.0;
        consGridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        consGridBagConstraints3.insets = new java.awt.Insets(5,10,5,10);
        consGridBagConstraints2.gridx = 0;
        consGridBagConstraints2.gridy = 0;
        consGridBagConstraints2.ipadx = 0;
        consGridBagConstraints2.ipady = 0;
        consGridBagConstraints2.weightx = 1.0;
        consGridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        consGridBagConstraints2.insets = new java.awt.Insets(0,10,5,10);
        consGridBagConstraints4.gridx = 0;
        consGridBagConstraints4.gridy = 2;
        consGridBagConstraints4.weightx = 1.0;
        consGridBagConstraints4.weighty = 2.0;
        consGridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        consGridBagConstraints4.ipadx = 340;
        consGridBagConstraints4.ipady = 40;
        consGridBagConstraints4.insets = new java.awt.Insets(5,0,0,0);
        this.setLayout(new java.awt.GridBagLayout());
        this.add(getJProgressBar(), consGridBagConstraints2);
        this.add(getJLabel(), consGridBagConstraints3);
        this.add(getScrollPane(), consGridBagConstraints4);
        this.setSize(594, 193);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,10,10));
			
	}
	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar() {
		if(jProgressBar == null) {
			jProgressBar = new javax.swing.JProgressBar();
			jProgressBar.setValue(17);
      jProgressBar.setStringPainted(true);
		}
		return jProgressBar;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("Current File Name");
			jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jLabel.setName("labelCurrentFileName");
		}
		return jLabel;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setName("errorMessageBox");
			jTextArea.setEditable(false);
			jTextArea.setTabSize(8);
		}
		return jTextArea;
	}
	
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JScrollPane getScrollPane() {
		if(scrollPane == null) {
			scrollPane = new JScrollPane(getJTextArea());
		}
		return scrollPane;
	}
	
	
	public void setMaxProgress(int max) {
		getJProgressBar().setMaximum(max);
	}
	
	public void setCutrrentProgress(int curr) {
		getJProgressBar().setValue(curr);
	}
	
	public void setCurrentFilename(String filename) {
		Graphics g = getJLabel().getGraphics();
		FontMetrics fm = g.getFontMetrics();
		int width = fm.stringWidth(filename);
		//System.out.println("labelwidth="+getJLabel().getWidth() + ", textWidth="+width);
		
		if (width < getJLabel().getWidth())
			getJLabel().setText(filename);
		else
			getJLabel().setText( shortenString(fm, filename, getJLabel().getWidth() ) );
	}
	
	/**
	 * @param fm
	 * @param string
	 * @param width
	 * @param stringLength
	 * @return
	 */
	private String shortenString(FontMetrics fm, String string, int maxWidth) {
		for (int i=string.length() ; i>0 ; i-=5) {
			String foo = "..." + string.substring( string.length()-i);

			int width = fm.stringWidth(foo);
			//System.out.println("testing '"+foo+"' = "+width);
			if (width < maxWidth)
				return foo;
		}
		return "";
	}

	
	public void addMessage(String message)
	{
		getJTextArea().append(message+"\n");
	}
	
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
