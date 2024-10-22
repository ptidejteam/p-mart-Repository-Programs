/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.preferences.cleanup;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.jdt.internal.ui.fix.ICleanUp;
import org.eclipse.jdt.internal.ui.preferences.formatter.JavaPreview;
import org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage;

public abstract class CleanUpTabPage extends ModifyDialogTabPage implements ICleanUpTabPage {

	private Map fValues;
	private JavaPreview fCleanUpPreview;
	private boolean fIsSaveAction;
	int fCount;
	int fSelectedCount;
	
	public CleanUpTabPage() {
		super();
		fCount= 0;
		fSelectedCount= 0;
		fIsSaveAction= false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setOptionsKind(int kind) {
		fIsSaveAction= kind == ICleanUp.DEFAULT_SAVE_ACTION_OPTIONS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setWorkingValues(Map workingValues) {
		super.setWorkingValues(workingValues);
		fValues= workingValues;
	}
	
	/**
	 * @return is this tab page shown in the save action dialog
	 */
	public boolean isSaveAction() {
		return fIsSaveAction;
	}
	
	public int getCleanUpCount() {
		return fCount;
	}

	public int getSelectedCleanUpCount() {
		return fSelectedCount;
	}
	
	protected abstract ICleanUp[] createPreviewCleanUps(Map values);
	
	protected JavaPreview doCreateJavaPreview(Composite parent) {
        fCleanUpPreview= new CleanUpPreview(parent, createPreviewCleanUps(fValues));
    	return fCleanUpPreview;
    }

	protected void doUpdatePreview() {
		fCleanUpPreview.setWorkingValues(fValues);
		fCleanUpPreview.update();
	}
	
	protected void initializePage() {
		fCleanUpPreview.update();
	}
	
	protected void registerPreference(final CheckboxPreference preference) {
		fCount++;
		preference.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				if (preference.getChecked()) {
					fSelectedCount++;
				} else {
					fSelectedCount--;
				}
			}
		});
		if (preference.getChecked()) {
			fSelectedCount++;
		}
	}
	
	protected void registerSlavePreference(final CheckboxPreference master, final RadioPreference[] slaves) {
		internalRegisterSlavePreference(master, slaves);
		registerPreference(master);
	}
	
	protected void registerSlavePreference(final CheckboxPreference master, final CheckboxPreference[] slaves) {
		internalRegisterSlavePreference(master, slaves);
		fCount+= slaves.length;
		
		master.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				if (master.getChecked()) {
					for (int i= 0; i < slaves.length; i++) {
						if (slaves[i].getChecked()) {
							fSelectedCount++;
						}
					}	
				} else {
					for (int i= 0; i < slaves.length; i++) {
						if (slaves[i].getChecked()) {
							fSelectedCount--;
						}
					}
				}
			}
		});
		
		for (int i= 0; i < slaves.length; i++) {
			final CheckboxPreference slave= slaves[i];
			slave.addObserver(new Observer() {
				public void update(Observable o, Object arg) {
					if (slave.getChecked()) {
						fSelectedCount++;
					} else {
						fSelectedCount--;
					}
				}
			});
		}
		
		if (master.getChecked()) {
			for (int i= 0; i < slaves.length; i++) {
				if (slaves[i].getChecked()) {
					fSelectedCount++;
				}
			}
		}
	}
	
	private void internalRegisterSlavePreference(final CheckboxPreference master, final ButtonPreference[] slaves) {
    	master.addObserver( new Observer() {
    		public void update(Observable o, Object arg) {
    			for (int i= 0; i < slaves.length; i++) {
					slaves[i].setEnabled(master.getChecked());
				}
    		}
    	});
    	
    	for (int i= 0; i < slaves.length; i++) {
			slaves[i].setEnabled(master.getChecked());
		}
	}

	protected void intent(Composite group) {
        Label l= new Label(group, SWT.NONE);
    	GridData gd= new GridData();
    	gd.widthHint= fPixelConverter.convertWidthInCharsToPixels(4);
    	l.setLayoutData(gd);
    }

}