/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.preferences.formatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager.CustomProfile;
import org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager.Profile;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;

public abstract class ModifyDialog extends StatusDialog implements IModifyDialogTabPage.IModificationListener {
    
    /**
     * The keys to retrieve the preferred area from the dialog settings.
     */
    private static final String DS_KEY_PREFERRED_WIDTH= "modify_dialog.preferred_width"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_HEIGHT= "modify_dialog.preferred_height"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_X= "modify_dialog.preferred_x"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_Y= "modify_dialog.preferred_y"; //$NON-NLS-1$
    
    
    /**
     * The key to store the number (beginning at 0) of the tab page which had the 
     * focus last time.
     */
    private static final String DS_KEY_LAST_FOCUS= "modify_dialog.last_focus"; //$NON-NLS-1$
    
    private static final int APPLAY_BUTTON_ID= IDialogConstants.CLIENT_ID;
    private static final int SAVE_BUTTON_ID= IDialogConstants.CLIENT_ID + 1;

	private final String fKeyPreferredWidth;
	private final String fKeyPreferredHight;
	private final String fKeyPreferredX;
	private final String fKeyPreferredY;
	private final String fKeyLastFocus;
	private final String fLastSaveLoadPathKey;
	private final ProfileStore fProfileStore;
	private final boolean fNewProfile;
	private Profile fProfile;
	private final Map fWorkingValues;
	private final List fTabPages;
	private final IDialogSettings fDialogSettings;
	private TabFolder fTabFolder;
	private final ProfileManager fProfileManager;
	private Button fApplyButton;
	private Button fSaveButton;
	private StringDialogField fProfileNameField;

	public ModifyDialog(Shell parentShell, Profile profile, ProfileManager profileManager, ProfileStore profileStore, boolean newProfile, String dialogPreferencesKey, String lastSavePathKey) {
		super(parentShell);
		
		fProfileStore= profileStore;
		fLastSaveLoadPathKey= lastSavePathKey;

		fKeyPreferredWidth= JavaUI.ID_PLUGIN + dialogPreferencesKey + DS_KEY_PREFERRED_WIDTH;
		fKeyPreferredHight= JavaUI.ID_PLUGIN + dialogPreferencesKey + DS_KEY_PREFERRED_HEIGHT;
		fKeyPreferredX= JavaUI.ID_PLUGIN + dialogPreferencesKey + DS_KEY_PREFERRED_X;
		fKeyPreferredY= JavaUI.ID_PLUGIN + dialogPreferencesKey + DS_KEY_PREFERRED_Y;
		fKeyLastFocus= JavaUI.ID_PLUGIN + dialogPreferencesKey + DS_KEY_LAST_FOCUS;

		fProfileManager= profileManager;
		fNewProfile= newProfile;
				
		fProfile= profile;
		setTitle(Messages.format(FormatterMessages.ModifyDialog_dialog_title, profile.getName()));
		fWorkingValues= new HashMap(fProfile.getSettings());
		setStatusLineAboveButtons(false);
		fTabPages= new ArrayList();
		fDialogSettings= JavaPlugin.getDefault().getDialogSettings();	
	}
	
	/*
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 * @since 3.4
	 */
	protected boolean isResizable() {
		return true;
	}

	protected abstract void addPages(Map values);

	public void create() {
		super.create();
		int lastFocusNr= 0;
		try {
			lastFocusNr= fDialogSettings.getInt(fKeyLastFocus);
			if (lastFocusNr < 0) lastFocusNr= 0;
			if (lastFocusNr > fTabPages.size() - 1) lastFocusNr= fTabPages.size() - 1;
		} catch (NumberFormatException x) {
			lastFocusNr= 0;
		}
		
		if (!fNewProfile) {
			fTabFolder.setSelection(lastFocusNr);
			((IModifyDialogTabPage)fTabFolder.getSelection()[0].getData()).setInitialFocus();
		}
	}
	
	

	protected Control createDialogArea(Composite parent) {
		
		final Composite composite= (Composite)super.createDialogArea(parent);
		
		Composite nameComposite= new Composite(composite, SWT.NONE);
		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameComposite.setLayout(new GridLayout(3, false));
		
		fProfileNameField= new StringDialogField();
		fProfileNameField.setLabelText(FormatterMessages.ModifyDialog_ProfileName_Label);
		fProfileNameField.setText(fProfile.getName());
		fProfileNameField.getLabelControl(nameComposite).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		fProfileNameField.getTextControl(nameComposite).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fProfileNameField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				doValidate();
            }
		});
		
		fSaveButton= createButton(nameComposite, SAVE_BUTTON_ID, FormatterMessages.ModifyDialog_Export_Button, false);
		
		fTabFolder = new TabFolder(composite, SWT.NONE);
		fTabFolder.setFont(composite.getFont());
		fTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addPages(fWorkingValues); 

		applyDialogFont(composite);
		
		fTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				final TabItem tabItem= (TabItem)e.item;
				final IModifyDialogTabPage page= (IModifyDialogTabPage)tabItem.getData();
				//				page.fSashForm.setWeights();
				fDialogSettings.put(fKeyLastFocus, fTabPages.indexOf(page));
				page.makeVisible();
			}
		});
		
		doValidate();
		
		return composite;
	}
	
	public void updateStatus(IStatus status) {
		if (status == null) {
			doValidate();
		} else {
	    	super.updateStatus(status);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	protected Point getInitialSize() {
		Point initialSize= super.getInitialSize();
		try {
			int lastWidth= fDialogSettings.getInt(fKeyPreferredWidth);
			if (initialSize.x > lastWidth)
				lastWidth= initialSize.x;
			int lastHeight= fDialogSettings.getInt(fKeyPreferredHight);
			if (initialSize.y > lastHeight)
				lastHeight= initialSize.y;
			return new Point(lastWidth, lastHeight);
		} catch (NumberFormatException ex) {
		}
		return initialSize;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	protected Point getInitialLocation(Point initialSize) {
		try {
			return new Point(fDialogSettings.getInt(fKeyPreferredX), fDialogSettings.getInt(fKeyPreferredY));
		} catch (NumberFormatException ex) {
			return super.getInitialLocation(initialSize);
		}
	}
	    
	public boolean close() {
		final Rectangle shell= getShell().getBounds();

		fDialogSettings.put(fKeyPreferredWidth, shell.width);
		fDialogSettings.put(fKeyPreferredHight, shell.height);
		fDialogSettings.put(fKeyPreferredX, shell.x);
		fDialogSettings.put(fKeyPreferredY, shell.y);

		return super.close();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		applyPressed();
		super.okPressed();
	}
	
    protected void buttonPressed(int buttonId) {
		if (buttonId == APPLAY_BUTTON_ID) {
			applyPressed();
			setTitle(Messages.format(FormatterMessages.ModifyDialog_dialog_title, fProfile.getName()));
		} else if (buttonId == SAVE_BUTTON_ID) {
			saveButtonPressed();
		} else {
			super.buttonPressed(buttonId);
		}
    }
	
	private void applyPressed() {
		if (!fProfile.getName().equals(fProfileNameField.getText())) {
			fProfile= fProfile.rename(fProfileNameField.getText(), fProfileManager);
		}
		fProfile.setSettings(new HashMap(fWorkingValues));
		fProfileManager.setSelected(fProfile);
		doValidate();
	}
	
	private void saveButtonPressed() {
		Profile selected= new CustomProfile(fProfileNameField.getText(), new HashMap(fWorkingValues), fProfile.getVersion(), fProfileManager.getProfileVersioner().getProfileKind());
		
		final FileDialog dialog= new FileDialog(getShell(), SWT.SAVE);
		dialog.setText(FormatterMessages.CodingStyleConfigurationBlock_save_profile_dialog_title); 
		dialog.setFilterExtensions(new String [] {"*.xml"}); //$NON-NLS-1$

		final String lastPath= JavaPlugin.getDefault().getDialogSettings().get(fLastSaveLoadPathKey + ".savepath"); //$NON-NLS-1$
		if (lastPath != null) {
			dialog.setFilterPath(lastPath);
		}
		final String path= dialog.open();
		if (path == null) 
			return;

		JavaPlugin.getDefault().getDialogSettings().put(fLastSaveLoadPathKey + ".savepath", dialog.getFilterPath()); //$NON-NLS-1$

		final File file= new File(path);
		if (file.exists() && !MessageDialog.openQuestion(getShell(), FormatterMessages.CodingStyleConfigurationBlock_save_profile_overwrite_title, Messages.format(FormatterMessages.CodingStyleConfigurationBlock_save_profile_overwrite_message, BasicElementLabels.getPathLabel(file)))) { 
			return;
		}
		String encoding= ProfileStore.ENCODING;
		final IContentType type= Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.xml"); //$NON-NLS-1$
		if (type != null)
			encoding= type.getDefaultCharset();
		final Collection profiles= new ArrayList();
		profiles.add(selected);
		try {
			fProfileStore.writeProfilesToFile(profiles, file, encoding);
		} catch (CoreException e) {
			final String title= FormatterMessages.CodingStyleConfigurationBlock_save_profile_error_title;
			final String message= FormatterMessages.CodingStyleConfigurationBlock_save_profile_error_message;
			ExceptionHandler.handle(e, getShell(), title, message);
		}
	}
    
    protected void createButtonsForButtonBar(Composite parent) {
	    fApplyButton= createButton(parent, APPLAY_BUTTON_ID, FormatterMessages.ModifyDialog_apply_button, false); 
		fApplyButton.setEnabled(false);
		
		GridLayout layout= (GridLayout) parent.getLayout();
		layout.numColumns++;
		layout.makeColumnsEqualWidth= false;
		Label label= new Label(parent, SWT.NONE);
		GridData data= new GridData();
		data.widthHint= layout.horizontalSpacing;
		label.setLayoutData(data);
		super.createButtonsForButtonBar(parent);
	}

	protected final void addTabPage(String title, IModifyDialogTabPage tabPage) {
		final TabItem tabItem= new TabItem(fTabFolder, SWT.NONE);
		applyDialogFont(tabItem.getControl());
		tabItem.setText(title);
		tabItem.setData(tabPage);
		tabItem.setControl(tabPage.createContents(fTabFolder));
		fTabPages.add(tabPage);
	}

	public void valuesModified() {
		doValidate();
	}

	protected void updateButtonsEnableState(IStatus status) {
	    super.updateButtonsEnableState(status);
	    if (fApplyButton != null && !fApplyButton.isDisposed()) {
	    	fApplyButton.setEnabled(hasChanges() && !status.matches(IStatus.ERROR));
		}
	    if (fSaveButton != null && !fSaveButton.isDisposed()) {
	    	fSaveButton.setEnabled(!validateProfileName().matches(IStatus.ERROR));
	    }
	}
	
    private void doValidate() {
    	String name= fProfileNameField.getText().trim();
		if (name.equals(fProfile.getName()) && fProfile.hasEqualSettings(fWorkingValues, fWorkingValues.keySet())) {
			updateStatus(StatusInfo.OK_STATUS);
			return;
		}
    	
    	IStatus status= validateProfileName();
    	if (status.matches(IStatus.ERROR)) {
    		updateStatus(status);
    		return;
    	}
    	
		if (!name.equals(fProfile.getName()) && fProfileManager.containsName(name)) {
			updateStatus(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, FormatterMessages.ModifyDialog_Duplicate_Status));
			return;
		}
		
		if (fProfile.isBuiltInProfile() || fProfile.isSharedProfile()) {
			updateStatus(new Status(IStatus.INFO, JavaUI.ID_PLUGIN, FormatterMessages.ModifyDialog_NewCreated_Status));
			return;
		}

	    updateStatus(StatusInfo.OK_STATUS);
    }

    private IStatus validateProfileName() {
    	final String name= fProfileNameField.getText().trim();
    	
	    if (fProfile.isBuiltInProfile()) {
			if (fProfile.getName().equals(name)) { 
				return new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, FormatterMessages.ModifyDialog_BuiltIn_Status);
			}	
    	}
    	
    	if (fProfile.isSharedProfile()) {
			if (fProfile.getName().equals(name)) { 
				return new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, FormatterMessages.ModifyDialog_Shared_Status);
			}
    	}
		
		if (name.length() == 0) {
			return new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, FormatterMessages.ModifyDialog_EmptyName_Status);
		}
		
		return StatusInfo.OK_STATUS;
    }

	private boolean hasChanges() {
		if (!fProfileNameField.getText().trim().equals(fProfile.getName()))
			return true;
		
		Iterator iter= fProfile.getSettings().entrySet().iterator();
		for (;iter.hasNext();) {
			Map.Entry curr= (Map.Entry) iter.next();
			if (!fWorkingValues.get(curr.getKey()).equals(curr.getValue())) {
				return true;
			}
		}
		return false;
	}
	
}
