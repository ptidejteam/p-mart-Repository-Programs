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
package org.eclipse.jdt.debug.ui.launchConfigurations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.NameValuePairDialog;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
 
/**
 * This tab appears for java applet launch configurations and allows the user to edit
 * applet-specific attributes such as width, height, name & applet parameters.
 * <p>
 * This class may be instantiated.
 * </p>
 * @since 2.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class AppletParametersTab extends JavaLaunchTab {
	
	private Label fWidthLabel;
	private Text fWidthText;
	private Label fHeightLabel;
	private Text fHeightText;
	private Label fNameLabel;
	private Text fNameText;
	private Button fParametersAddButton;
	private Button fParametersRemoveButton;
	private Button fParametersEditButton;
	
	private class AppletTabListener extends SelectionAdapter implements ModifyListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			Object source= e.getSource();
			if (source == fViewer.getTable() || source == fViewer) {
				setParametersButtonsEnableState();
			} else if (source == fParametersAddButton) {
				handleParametersAddButtonSelected();
			} else if (source == fParametersEditButton) {
				handleParametersEditButtonSelected();
			} else if (source == fParametersRemoveButton) {
				handleParametersRemoveButtonSelected();
			}
		}

	}
	
	private AppletTabListener fListener= new AppletTabListener();

	private static final String EMPTY_STRING = "";	 //$NON-NLS-1$
	
	/**
	 * The default value for the 'width' attribute.
	 */
	public static final int DEFAULT_APPLET_WIDTH = 200;
	
	/**
	 * The default value for the 'height' attribute.
	 */
	public static final int DEFAULT_APPLET_HEIGHT = 200;
	
	/**
	 * The parameters table viewer
	 */
	private TableViewer fViewer;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parent) {	
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),	IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_APPLET_PARAMETERS_TAB);
		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);		
		GridData gd;
		
		Composite widthHeightNameComp = new Composite(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		widthHeightNameComp.setLayoutData(gd);
		GridLayout widthHeightNameLayout = new GridLayout();
		widthHeightNameLayout.marginHeight = 0;
		widthHeightNameLayout.marginWidth = 0;
		widthHeightNameLayout.numColumns = 4;
		widthHeightNameComp.setLayout(widthHeightNameLayout);
		
		fWidthLabel= new Label(widthHeightNameComp, SWT.NONE);
		fWidthLabel.setText(LauncherMessages.appletlauncher_argumenttab_widthlabel_text); 
		
		fWidthText = new Text(widthHeightNameComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fWidthText.setLayoutData(gd);
		fWidthText.addModifyListener(fListener);

		fNameLabel = new Label(widthHeightNameComp, SWT.NONE);
		fNameLabel.setText(LauncherMessages.appletlauncher_argumenttab_namelabel_text); 
		
		fNameText = new Text(widthHeightNameComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fNameText.setLayoutData(gd);
		fNameText.addModifyListener(fListener);	

		fHeightLabel= new Label(widthHeightNameComp, SWT.NONE);
		fHeightLabel.setText(LauncherMessages.appletlauncher_argumenttab_heightlabel_text); 
		
		fHeightText = new Text(widthHeightNameComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fHeightText.setLayoutData(gd);
		fHeightText.addModifyListener(fListener);
		
		Label blank = new Label(widthHeightNameComp, SWT.NONE);
		blank.setText(EMPTY_STRING);
		Label hint = new Label(widthHeightNameComp, SWT.NONE);
		hint.setText(LauncherMessages.AppletParametersTab__optional_applet_instance_name__1); 
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		hint.setLayoutData(gd);
				
		createVerticalSpacer(comp);
		
		Composite parametersComp = new Composite(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		parametersComp.setLayoutData(gd);
		GridLayout parametersLayout = new GridLayout();
		parametersLayout.numColumns = 2;
		parametersLayout.marginHeight = 0;
		parametersLayout.marginWidth = 0;
		parametersComp.setLayout(parametersLayout);
		
		Label parameterLabel = new Label(parametersComp, SWT.NONE);
		parameterLabel.setText(LauncherMessages.appletlauncher_argumenttab_parameterslabel_text); 
		gd = new GridData();
		gd.horizontalSpan = 2;
		parameterLabel.setLayoutData(gd);
		
		
		fViewer = new TableViewer(parametersComp);
		Table parametersTable = fViewer.getTable();
		gd = new GridData(GridData.FILL_BOTH);
		parametersTable.setLayoutData(gd);		
		TableColumn column1 = new TableColumn(parametersTable, SWT.NONE);
		column1.setText(LauncherMessages.appletlauncher_argumenttab_parameterscolumn_name_text);
		TableColumn column2 = new TableColumn(parametersTable, SWT.NONE);
		column2.setText(LauncherMessages.appletlauncher_argumenttab_parameterscolumn_value_text);
		TableLayout tableLayout = new TableLayout();
		parametersTable.setLayout(tableLayout);
		tableLayout.addColumnData(new ColumnWeightData(100));
		tableLayout.addColumnData(new ColumnWeightData(100));
		parametersTable.setHeaderVisible(true);
		parametersTable.setLinesVisible(true);
		parametersTable.addSelectionListener(fListener);
		parametersTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				setParametersButtonsEnableState();
				if (fParametersEditButton.isEnabled()) {
					handleParametersEditButtonSelected();
				}
			}
		});
		
		fViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				Map params = (Map) inputElement;
				return params.keySet().toArray();
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
	
		fViewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0) {
					return element.toString();
				}

				String key = (String) element;
				Map params = (Map) fViewer.getInput();
				Object object = params.get(key);
				if (object != null)
					return object.toString();
				return null;
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
		
		fViewer.setComparator(new ViewerComparator());
		
		Composite envButtonComp = new Composite(parametersComp, SWT.NONE);
		GridLayout envButtonLayout = new GridLayout();
		envButtonLayout.marginHeight = 0;
		envButtonLayout.marginWidth = 0;
		envButtonComp.setLayout(envButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		envButtonComp.setLayoutData(gd);
		
		fParametersAddButton = createPushButton(envButtonComp ,LauncherMessages.appletlauncher_argumenttab_parameters_button_add_text, null); 
		fParametersAddButton.addSelectionListener(fListener);
		
		fParametersEditButton = createPushButton(envButtonComp, LauncherMessages.appletlauncher_argumenttab_parameters_button_edit_text, null); 
		fParametersEditButton.addSelectionListener(fListener);
		
		fParametersRemoveButton = createPushButton(envButtonComp, LauncherMessages.appletlauncher_argumenttab_parameters_button_remove_text, null); 
		fParametersRemoveButton.addSelectionListener(fListener);
		
		Dialog.applyDialogFont(parent);
	}

		
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		try {
			Integer.parseInt(getWidthText());
		} catch(NumberFormatException nfe) {
			setErrorMessage(LauncherMessages.appletlauncher_argumenttab_width_error_notaninteger); 
			return false;
		}
		try {
			Integer.parseInt(getHeightText());
		} catch(NumberFormatException nfe) {
			setErrorMessage(LauncherMessages.appletlauncher_argumenttab_height_error_notaninteger); 
			return false;
		}
		return true;
	}

	private void handleParametersAddButtonSelected() {
		NameValuePairDialog dialog = 
			new NameValuePairDialog(getShell(), 
				LauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_title,  
				new String[] {LauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_name_text, LauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_value_text},  // 
				new String[] {EMPTY_STRING, EMPTY_STRING}); 
		openNewParameterDialog(dialog, null);
		setParametersButtonsEnableState();
	}

	private void handleParametersEditButtonSelected() {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		String key = (String) selection.getFirstElement();
		Map params = (Map) fViewer.getInput();
		String value = (String) params.get(key);
		
		NameValuePairDialog dialog =
			new NameValuePairDialog(getShell(), 
				LauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_title,  
				new String[] {LauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_name_text, LauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_value_text},  // 
				new String[] {key, value});
		
		openNewParameterDialog(dialog, key);		
	}

	private void handleParametersRemoveButtonSelected() {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		Object[] keys = selection.toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			Map params = (Map) fViewer.getInput();
			params.remove(key);			
		}
		fViewer.refresh();
		setParametersButtonsEnableState();
		updateLaunchConfigurationDialog();
	}

	/**
	 * Set the enabled state of the three environment variable-related buttons based on the
	 * selection in the Table widget.
	 */
	private void setParametersButtonsEnableState() {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		int selectCount = selection.size();
		if (selectCount < 1) {
			fParametersEditButton.setEnabled(false);
			fParametersRemoveButton.setEnabled(false);
		} else {
			fParametersRemoveButton.setEnabled(true);
			if (selectCount == 1) {
				fParametersEditButton.setEnabled(true);
			} else {
				fParametersEditButton.setEnabled(false);
			}
		}		
		fParametersAddButton.setEnabled(true);
	}

	/**
	 * Show the specified dialog and update the parameter table based on its results.
	 * 
	 * @param updateItem the item to update, or <code>null</code> if
	 *  adding a new item
	 */
	private void openNewParameterDialog(NameValuePairDialog dialog, String key) {
		if (dialog.open() != Window.OK) {
			return;
		}
		String[] nameValuePair = dialog.getNameValuePair();
		Map params = (Map) fViewer.getInput();
		params.remove(key);
		params.put(nameValuePair[0], nameValuePair[1]);
		fViewer.refresh();
		updateLaunchConfigurationDialog();	
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		try {
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_WIDTH, Integer.parseInt(getWidthText()));
		} catch (NumberFormatException e) {
		}
		try {
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_HEIGHT, Integer.parseInt(getHeightText()));
		} catch (NumberFormatException e) {
		}
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_NAME, fNameText.getText());
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_PARAMETERS, (Map) fViewer.getInput());
	}

	/**
	 * Returns the current width specified by the user
	 * @return the width specified by the user
	 */
	private String getWidthText() {
		return fWidthText.getText().trim();
	}
	
	/**
	 * Returns the current height specified by the user
	 * @return the height specified by the user
	 */
	private String getHeightText() {
		return fHeightText.getText().trim();
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration config) {
		try {
			fWidthText.setText(Integer.toString(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_WIDTH, DEFAULT_APPLET_WIDTH))); 
		} catch(CoreException ce) {
			fWidthText.setText(Integer.toString(DEFAULT_APPLET_WIDTH)); 
		}
		try {
			fHeightText.setText(Integer.toString(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_HEIGHT, DEFAULT_APPLET_HEIGHT))); 
		} catch(CoreException ce) {
			fHeightText.setText(Integer.toString(DEFAULT_APPLET_HEIGHT)); 
		}
		try {
			fNameText.setText(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_NAME, LauncherMessages.appletlauncher_argumenttab_name_defaultvalue)); 
		} catch(CoreException ce) {
			fNameText.setText(LauncherMessages.appletlauncher_argumenttab_name_defaultvalue); 
		}
		
		Map input = new HashMap();
		try {
			 Map params = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_PARAMETERS, (Map) null);
             if (params != null)
                 input.putAll(params);
		} catch (CoreException e) {
		}
		
		fViewer.setInput(input);
	}
	
	/**
	 * Create some empty space 
	 */
	private void createVerticalSpacer(Composite comp) {
		new Label(comp, SWT.NONE);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return LauncherMessages.appletlauncher_argumenttab_name; 
	}	
	
	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
	 * 
	 * @since 3.3
	 */
	public String getId() {
		return "org.eclipse.jdt.debug.ui.appletParametersTab"; //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#activated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// do nothing when activated
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#deactivated(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		// do nothing when deactivated
	}	
}

