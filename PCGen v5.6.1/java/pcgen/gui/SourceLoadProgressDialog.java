/*
 * SourceLoadProgressDialog.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 16-Mar-2004
 *
 * Current Ver: $Revision: 1.1 $
 * 
 * Last Editor: $Author: vauchers $
 * 
 * Last Edited: $Date: 2006/02/21 01:33:07 $
 *
 */
package pcgen.gui;

import java.awt.Frame;

import javax.swing.JDialog;


public class SourceLoadProgressDialog extends JDialog
{


	private pcgen.gui.SourceLoadProgressPanel sourceLoadProgressPanel = null;
	/**
	 * This method initializes 
	 * 
	 */
	public SourceLoadProgressDialog(Frame owner, boolean modal) {
		super(owner, modal);
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setContentPane(getSourceLoadProgressPanel());
        this.setSize(587, 248);
        this.setTitle("Loading data Files");
        this.setVisible(true);
			
	}
	/**
	 * This method initializes sourceLoadProgressPanel
	 * 
	 * @return pcgen.gui.SourceLoadProgressPanel
	 */
	private pcgen.gui.SourceLoadProgressPanel getSourceLoadProgressPanel() {
		if(sourceLoadProgressPanel == null) {
			sourceLoadProgressPanel = new pcgen.gui.SourceLoadProgressPanel();
		}
		return sourceLoadProgressPanel;
	}
	
	
	public void addMessage(String message)
	{
		getSourceLoadProgressPanel().addMessage(message);
	}
	
	public void setCurrentFile(String filename)
	{
		getSourceLoadProgressPanel().setCurrentFilename(filename);
	}
	
	public void setCurrentFileCount(int count)
	{
		getSourceLoadProgressPanel().setCutrrentProgress(count);
	}
	
	public void setTotalFileCount(int count)
	{
		getSourceLoadProgressPanel().setMaxProgress(count);
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
