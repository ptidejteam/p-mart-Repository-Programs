/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genady Beryozkin <eclipse@genady.org> - [misc] Display values for constant fields in the Javadoc view - https://bugs.eclipse.org/bugs/show_bug.cgi?id=204914
 *     Brock Janiczak <brockj@tpg.com.au> - [implementation] Streams not being closed in Javadoc views - https://bugs.eclipse.org/bugs/show_bug.cgi?id=214854
 *     Benjamin Muskalla <bmuskalla@innoopract.com> - [javadoc view] NPE on enumerations - https://bugs.eclipse.org/bugs/show_bug.cgi?id=223586
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.javadoc.JavaDocLocations;
import org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil;
import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.IWorkbenchCommandIds;
import org.eclipse.jdt.internal.ui.actions.OpenExternalBrowserAction;
import org.eclipse.jdt.internal.ui.actions.SimpleSelectionProvider;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks;

import org.osgi.framework.Bundle;


/**
 * View which shows Javadoc for a given Java element.
 *
 * FIXME: As of 3.0 selectAll() and getSelection() is not working
 *			see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
 *
 * @since 3.0
 */
public class JavadocView extends AbstractInfoView {

	/**
	 * Implementation of a {@link BrowserInput} using
	 * a {@link IJavaElement} as input.
	 * 
	 * @since 3.4
	 */
	private static final class JavaElementBrowserInput extends BrowserInput {

		private final IJavaElement fInput;

		public JavaElementBrowserInput(BrowserInput previous, IJavaElement inputElement) {
			super(previous);
			Assert.isNotNull(inputElement);
			fInput= inputElement;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.infoviews.JavadocView.IBrowserInput#getInputElement()
		 */
		public Object getInputElement() {
			return fInput;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.infoviews.JavadocView.IBrowserInput#getInputName()
		 */
		public String getInputName() {
			return fInput.getElementName();
		}
	}
	
	/**
	 * Implementation of a {@link BrowserInput} using an
	 * {@link URL} as input.
	 * 
	 * @since 3.4
	 */
	private static class URLBrowserInput extends BrowserInput {

		private final URL fURL;

		public URLBrowserInput(BrowserInput previous, URL url) {
			super(previous);
			Assert.isNotNull(url);
			fURL= url;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.infoviews.JavadocView.IBrowserInput#getInputElement()
		 */
		public Object getInputElement() {
			return fURL;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.infoviews.JavadocView.IBrowserInput#getInputName()
		 */
		public String getInputName() {
			return fURL.toExternalForm();
		}
	}
	
	/**
	 * Action to go forward in the history.
	 * 
	 * @since 3.4
	 */
	private final class ForthAction extends Action {
		
		public ForthAction() {
			setText(InfoViewMessages.JavadocView_action_forward_name);
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
			
			update();
		}
		
		public void update() {
			if (fCurrent != null && fCurrent.getNext() != null) {
				BrowserInput element= fCurrent.getNext();
				setToolTipText(Messages.format(InfoViewMessages.JavadocView_action_forward_enabledTooltip, BasicElementLabels.getJavaElementName(element.getInputName())));
				setEnabled(true);
			} else {
				setToolTipText(InfoViewMessages.JavadocView_action_forward_disabledTooltip);
				setEnabled(false);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			setInput(fCurrent.getNext());
		}
		
	}

	/**
	 * Action to go backwards in the history.
	 * 
	 * @since 3.4
	 */
	private final class BackAction extends Action {
		
		public BackAction() {
			setText(InfoViewMessages.JavadocView_action_back_name);
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));

			update();
		}

		private void update() {
			if (fCurrent != null && fCurrent.getPrevious() != null) {
				BrowserInput element= fCurrent.getPrevious();
				setToolTipText(Messages.format(InfoViewMessages.JavadocView_action_back_enabledTooltip, BasicElementLabels.getJavaElementName(element.getInputName())));
				setEnabled(true);
			} else {
				setToolTipText(InfoViewMessages.JavadocView_action_back_disabledTooltip);
				setEnabled(false);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			setInput(fCurrent.getPrevious());
		}
	}
	
	/**
	 * Action to toggle linking with selection.
	 * 
	 * @since 3.4
	 */
	private class LinkAction extends Action {

		public LinkAction() {
			super(InfoViewMessages.JavadocView_action_toogleLinking_text, SWT.TOGGLE);

			setTitleToolTip(InfoViewMessages.JavadocView_action_toggleLinking_toolTipText);

			JavaPluginImages.setLocalImageDescriptors(this, "synced.gif"); //$NON-NLS-1$
			setChecked(isLinkingEnabled());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			setLinkingEnabled(!isLinkingEnabled());
		}
	}

	/**
	 * Preference key for the preference whether to show a dialog
	 * when the SWT Browser widget is not available.
	 * @since 3.0
	 */
	private static final String DO_NOT_WARN_PREFERENCE_KEY= "JavadocView.error.doNotWarn"; //$NON-NLS-1$
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=73558
	private static final boolean WARNING_DIALOG_ENABLED= false;

	/** Flags used to render a label in the text widget. */
	private static final long LABEL_FLAGS=  JavaElementLabels.ALL_FULLY_QUALIFIED
		| JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS
		| JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.T_TYPE_PARAMETERS;


	/** The HTML widget. */
	private Browser fBrowser;
	/** The text widget. */
	private StyledText fText;
	/** The information presenter. */
	private DefaultInformationControl.IInformationPresenter fPresenter;
	/** The text presentation. */
	private final TextPresentation fPresentation= new TextPresentation();
	/** The select all action */
	private SelectAllAction fSelectAllAction;
	/** The style sheet (css) */
	private static String fgStyleSheet;
	/**
	 * <code>true</code> once the style sheet has been loaded.
	 * @since 3.3
	 */
	private static boolean fgStyleSheetLoaded= false;

	/** The Browser widget */
	private boolean fIsUsingBrowserWidget;

	private RGB fBackgroundColorRGB;
	/**
	 * The font listener.
	 * @since 3.3
	 */
	private IPropertyChangeListener fFontListener;

	/**
	 * Holds original Javadoc input string.
	 * @since 3.4
	 */
	private String fOriginalInput;
	
	/**
	 * The current input element if any
	 * @since 3.4
	 */
	private BrowserInput fCurrent;

	/**
	 * Action to go back in the link history.
	 * @since 3.4
	 */
	private BackAction fBackAction;

	/**
	 * Action to go forth in the link history.
	 * @since 3.4
	 */
	private ForthAction fForthAction;

	/**
	 * Action to enable and disable link with selection.
	 * @since 3.4
	 */
	private LinkAction fToggleLinkAction;
	
	/**
	 * Action to show content in external browser
	 * @since 3.4
	 */
	private OpenExternalBrowserAction fOpenExternalBrowserAction;

	/**
	 * A selection provider providing the current
	 * Java element input of this view as selection.
	 * @since 3.4
	 */
	private ISelectionProvider fInputSelectionProvider;

	/**
	 * The Javadoc view's select all action.
	 */
	private class SelectAllAction extends Action {

		/** The control. */
		private final Control fControl;
		/** The selection provider. */
		private final SelectionProvider fSelectionProvider;

		/**
		 * Creates the action.
		 *
		 * @param control the widget
		 * @param selectionProvider the selection provider
		 */
		public SelectAllAction(Control control, SelectionProvider selectionProvider) {
			super("selectAll"); //$NON-NLS-1$

			Assert.isNotNull(control);
			Assert.isNotNull(selectionProvider);
			fControl= control;
			fSelectionProvider= selectionProvider;

			// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
			setEnabled(!fIsUsingBrowserWidget);

			setText(InfoViewMessages.SelectAllAction_label);
			setToolTipText(InfoViewMessages.SelectAllAction_tooltip);
			setDescription(InfoViewMessages.SelectAllAction_description);

			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IAbstractTextEditorHelpContextIds.SELECT_ALL_ACTION);
		}

		/**
		 * Selects all in the view.
		 */
		public void run() {
			if (fControl instanceof StyledText)
		        ((StyledText)fControl).selectAll();
			else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).selectAll();
				if (fSelectionProvider != null)
					fSelectionProvider.fireSelectionChanged();
			}
		}
	}

	/**
	 * The Javadoc view's selection provider.
	 */
	private static class SelectionProvider implements ISelectionProvider {

		/** The selection changed listeners. */
		private final ListenerList fListeners= new ListenerList(ListenerList.IDENTITY);
		/** The widget. */
		private final Control fControl;

		/**
		 * Creates a new selection provider.
		 *
		 * @param control	the widget
		 */
		public SelectionProvider(Control control) {
		    Assert.isNotNull(control);
			fControl= control;
			if (fControl instanceof StyledText) {
			    ((StyledText)fControl).addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
					    fireSelectionChanged();
					}
			    });
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						fireSelectionChanged();
//					}
//				});
			}
		}

		/**
		 * Sends a selection changed event to all listeners.
		 */
		public void fireSelectionChanged() {
			ISelection selection= getSelection();
			SelectionChangedEvent event= new SelectionChangedEvent(this, selection);
			Object[] selectionChangedListeners= fListeners.getListeners();
			for (int i= 0; i < selectionChangedListeners.length; i++)
				((ISelectionChangedListener)selectionChangedListeners[i]).selectionChanged(event);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.add(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
		 */
		public ISelection getSelection() {
			if (fControl instanceof StyledText) {
				IDocument document= new Document(((StyledText)fControl).getSelectionText());
				return new TextSelection(document, 0, document.getLength());
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
				return StructuredSelection.EMPTY;
			}
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.remove(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			// not supported
		}
	}

	/*
	 * @see AbstractInfoView#internalCreatePartControl(Composite)
	 */
	protected void internalCreatePartControl(Composite parent) {
		try {
			fBrowser= new Browser(parent, SWT.NONE);
			fIsUsingBrowserWidget= true;
			addLinkListener(fBrowser);
			
		} catch (SWTError er) {

			/* The Browser widget throws an SWTError if it fails to
			 * instantiate properly. Application code should catch
			 * this SWTError and disable any feature requiring the
			 * Browser widget.
			 * Platform requirements for the SWT Browser widget are available
			 * from the SWT FAQ web site.
			 */

			IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
			boolean doNotWarn= store.getBoolean(DO_NOT_WARN_PREFERENCE_KEY);
			if (WARNING_DIALOG_ENABLED && !doNotWarn) {
				String title= InfoViewMessages.JavadocView_error_noBrowser_title;
				String message= InfoViewMessages.JavadocView_error_noBrowser_message;
				String toggleMessage= InfoViewMessages.JavadocView_error_noBrowser_doNotWarn;
				MessageDialogWithToggle dialog= MessageDialogWithToggle.openError(parent.getShell(), title, message, toggleMessage, false, null, null);
				if (dialog.getReturnCode() == Window.OK)
					store.setValue(DO_NOT_WARN_PREFERENCE_KEY, dialog.getToggleState());
			}

			fIsUsingBrowserWidget= false;
		}

		if (!fIsUsingBrowserWidget) {
			fText= new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			fText.setEditable(false);
			fPresenter= new HTMLTextPresenter(false);

			fText.addControlListener(new ControlAdapter() {
				/*
				 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
				 */
				public void controlResized(ControlEvent e) {
					doSetInput(fOriginalInput);
				}
			});
		}

		initStyleSheet();
		listenForFontChanges();
		getViewSite().setSelectionProvider(new SelectionProvider(getControl()));
	}

	/**
	 * Registers a listener for the Java editor font.
	 * 
	 * @since 3.3
	 */
	private void listenForFontChanges() {
		fFontListener= new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (PreferenceConstants.APPEARANCE_JAVADOC_FONT.equals(event.getProperty())) {
					fgStyleSheetLoaded= false;
					// trigger reloading, but make sure other listeners have already run, so that
					// the style sheet gets reloaded only once.
					final Display display= getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
					if (!display.isDisposed()) {
						display.asyncExec(new Runnable() {
							public void run() {
								if (!display.isDisposed()) {
									initStyleSheet();
									refresh();
								}
							}
						});
					}
				}
			}
		};
		JFaceResources.getFontRegistry().addListener(fFontListener);
	}
	
	private static void initStyleSheet() {
		if (fgStyleSheetLoaded)
			return;
		fgStyleSheetLoaded= true;
		fgStyleSheet= loadStyleSheet();
	}
	
	private static String loadStyleSheet() {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL styleSheetURL= bundle.getEntry("/JavadocViewStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL == null)
			return null;

		BufferedReader reader= null;
		try {
			reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
			StringBuffer buffer= new StringBuffer(1500);
			String line= reader.readLine();
			while (line != null) {
				buffer.append(line);
				buffer.append('\n');
				line= reader.readLine();
			}

			FontData fontData= JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			return HTMLPrinter.convertTopLevelFont(buffer.toString(), fontData);
		} catch (IOException ex) {
			JavaPlugin.log(ex);
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
	}

	/*
	 * @see AbstractInfoView#createActions()
	 */
	protected void createActions() {
		super.createActions();
		fSelectAllAction= new SelectAllAction(getControl(), (SelectionProvider) getSelectionProvider());
		
		fBackAction= new BackAction();
		fBackAction.setActionDefinitionId("org.eclipse.ui.navigate.back"); //$NON-NLS-1$
		fForthAction= new ForthAction();
		fForthAction.setActionDefinitionId("org.eclipse.ui.navigate.forward"); //$NON-NLS-1$
		
		fToggleLinkAction= new LinkAction();
		fToggleLinkAction.setActionDefinitionId(IWorkbenchCommandIds.LINK_WITH_EDITOR);
		
		fInputSelectionProvider= new SimpleSelectionProvider();
		fOpenExternalBrowserAction= new OpenExternalBrowserAction(getSite().getShell().getDisplay(), fInputSelectionProvider);
		fOpenExternalBrowserAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EXTERNAL_JAVADOC);
		fInputSelectionProvider.addSelectionChangedListener(fOpenExternalBrowserAction);
		
		IJavaElement input= getInput();
		StructuredSelection selection;
		if (input != null) {
			selection= new StructuredSelection(input);
		} else {
			selection= new StructuredSelection();
		}
		fInputSelectionProvider.setSelection(selection);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#fillActionBars(org.eclipse.ui.IActionBars)
	 * @since 3.4
	 */
	protected void fillActionBars(final IActionBars actionBars) {
		super.fillActionBars(actionBars);

		actionBars.setGlobalActionHandler(ActionFactory.BACK.getId(), fBackAction);
		actionBars.setGlobalActionHandler(ActionFactory.FORWARD.getId(), fForthAction);
	
		fInputSelectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				actionBars.setGlobalActionHandler(JdtActionConstants.OPEN_EXTERNAL_JAVA_DOC, fOpenExternalBrowserAction);
			}
		});

		IHandlerService handlerService= (IHandlerService) getSite().getService(IHandlerService.class);
		handlerService.activateHandler(IWorkbenchCommandIds.LINK_WITH_EDITOR, new ActionHandler(fToggleLinkAction));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#fillToolBar(org.eclipse.jface.action.IToolBarManager)
	 * @since 3.4
	 */
	protected void fillToolBar(IToolBarManager tbm) {
		tbm.add(fBackAction);
		tbm.add(fForthAction);
		tbm.add(new Separator());
		
		tbm.add(fToggleLinkAction);
		super.fillToolBar(tbm);
		tbm.add(fOpenExternalBrowserAction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 * @since 3.4
	 */
	public void menuAboutToShow(IMenuManager menu) {
		super.menuAboutToShow(menu);

		menu.appendToGroup(IContextMenuConstants.GROUP_GOTO, fBackAction);
		menu.appendToGroup(IContextMenuConstants.GROUP_GOTO, fForthAction);
		
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, fOpenExternalBrowserAction);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getSelectAllAction()
	 * @since 3.0
	 */
	protected IAction getSelectAllAction() {
		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
		if (fIsUsingBrowserWidget)
			return null;

		return fSelectAllAction;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getCopyToClipboardAction()
	 * @since 3.0
	 */
	protected IAction getCopyToClipboardAction() {
		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
		if (fIsUsingBrowserWidget)
			return null;

		return super.getCopyToClipboardAction();
	}

	/*
 	 * @see AbstractInfoView#setForeground(Color)
 	 */
	protected void setForeground(Color color) {
		getControl().setForeground(color);
	}

	/*
	 * @see AbstractInfoView#setBackground(Color)
	 */
	protected void setBackground(Color color) {
		getControl().setBackground(color);
		fBackgroundColorRGB= color.getRGB();
		refresh();
	}

	/**
	 * Refreshes the view.
	 *
	 * @since 3.3
	 */
	private void refresh() {
		IJavaElement input= getInput();
		if (input == null) {
			StringBuffer buffer= new StringBuffer(""); //$NON-NLS-1$
			HTMLPrinter.insertPageProlog(buffer, 0, null, fBackgroundColorRGB, fgStyleSheet);
			doSetInput(buffer.toString());
		} else {
			doSetInput(computeInput(input));
		}
	}
	
	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getBackgroundColorKey()
	 * @since 3.2
	 */
	protected String getBackgroundColorKey() {
		return "org.eclipse.jdt.ui.JavadocView.backgroundColor";		 //$NON-NLS-1$
	}

	/*
	 * @see AbstractInfoView#internalDispose()
	 */
	protected void internalDispose() {
		fText= null;
		fBrowser= null;
		if (fFontListener != null) {
			JFaceResources.getFontRegistry().removeListener(fFontListener);
			fFontListener= null;
		}
		
		if (fOpenExternalBrowserAction != null) {
			fInputSelectionProvider.removeSelectionChangedListener(fOpenExternalBrowserAction);
			fOpenExternalBrowserAction= null;
		}
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		getControl().setFocus();
	}

	/*
	 * @see AbstractInfoView#computeInput(Object)
	 */
	protected Object computeInput(Object input) {
		if (getControl() == null || ! (input instanceof IJavaElement))
			return null;
		
		IWorkbenchPart part= null;
		IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				part= page.getActivePart();
			}
		}
		
		ISelection selection= null;
		if (part != null) {
			IWorkbenchPartSite site= part.getSite();
			if (site != null) {
				ISelectionProvider provider= site.getSelectionProvider();
				if (provider != null) {
					selection= provider.getSelection();
				}
			}
		}

		return computeInput(part, selection, (IJavaElement) input, new NullProgressMonitor());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#computeInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection, org.eclipse.jdt.core.IJavaElement, org.eclipse.core.runtime.IProgressMonitor)
	 * @since 3.4
	 */
	protected Object computeInput(IWorkbenchPart part, ISelection selection, IJavaElement input, IProgressMonitor monitor) {
		if (getControl() == null || input == null)
			return null;

		String javadocHtml;

		switch (input.getElementType()) {
			case IJavaElement.COMPILATION_UNIT:
				try {
					javadocHtml= getJavadocHtml(((ICompilationUnit) input).getTypes(), part, selection, monitor);
				} catch (JavaModelException ex) {
					javadocHtml= null;
				}
				break;
			case IJavaElement.CLASS_FILE:
				javadocHtml= getJavadocHtml(new IJavaElement[] { ((IClassFile) input).getType() }, part, selection, monitor);
				break;
			default:
				javadocHtml= getJavadocHtml(new IJavaElement[] { input }, part, selection, monitor);
		}
		
		if (javadocHtml == null)
			return ""; //$NON-NLS-1$
		
		return javadocHtml;
	}

	/*
	 * @see AbstractInfoView#computeDescription(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection, org.eclipse.jdt.core.IJavaElement, org.eclipse.core.runtime.IProgressMonitor)
	 * @since 3.4
	 */
	protected String computeDescription(IWorkbenchPart part, ISelection selection, IJavaElement inputElement, IProgressMonitor monitor) {
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Set input to the given input.
	 * 
	 * @param input the input for the view
	 * @since 3.4
	 */
	public void setInput(BrowserInput input) {
		fCurrent= input;
		
		Object inputElement= input.getInputElement();
		if (inputElement instanceof IJavaElement) {
			setInput((IJavaElement) inputElement);
		} else if (inputElement instanceof URL) {
			fBrowser.setUrl(((URL) inputElement).toExternalForm());
			
			if (fInputSelectionProvider != null)
				fInputSelectionProvider.setSelection(new StructuredSelection(inputElement));
		}
		
		fForthAction.update();
		fBackAction.update();
	}
	
	/*
	 * @see AbstractInfoView#setInput(Object)
	 */
	protected void doSetInput(Object input) {
		String javadocHtml= (String)input; // FIXME: undocumented hard cast
		fOriginalInput= javadocHtml;
		
		if (fInputSelectionProvider != null) {
			IJavaElement inputElement= getInput();
			StructuredSelection selection= inputElement == null ? StructuredSelection.EMPTY : new StructuredSelection(inputElement);
			fInputSelectionProvider.setSelection(selection);
		}

		if (fIsUsingBrowserWidget) {
			if (javadocHtml != null && javadocHtml.length() > 0) {
				boolean RTL= (getSite().getShell().getStyle() & SWT.RIGHT_TO_LEFT) != 0;
				if (RTL) {
					StringBuffer buffer= new StringBuffer(javadocHtml);
					HTMLPrinter.insertStyles(buffer, new String[] { "direction:rtl" } ); //$NON-NLS-1$
					javadocHtml= buffer.toString();
				}
			}
			fBrowser.setText(javadocHtml);
		} else {
			fPresentation.clear();
			Rectangle size=  fText.getClientArea();

			try {
				javadocHtml= fPresenter.updatePresentation(getSite().getShell().getDisplay(), javadocHtml, fPresentation, size.width, size.height);
			} catch (IllegalArgumentException ex) {
				// the javadoc might no longer be valid
				return;
			}
			fText.setText(javadocHtml);
			TextPresentation.applyTextPresentation(fPresentation, fText);
		}
	}
	
	/**
	 * Returns the Javadoc in HTML format.
	 *
	 * @param result the Java elements for which to get the Javadoc
	 * @param activePart the active part if any
	 * @param selection the selection of the active site if any
	 * @param monitor a monitor to report progress to
	 * @return a string with the Javadoc in HTML format.
	 */
	private String getJavadocHtml(IJavaElement[] result, IWorkbenchPart activePart, ISelection selection, IProgressMonitor monitor) {
		StringBuffer buffer= new StringBuffer();
		int nResults= result.length;

		if (nResults == 0)
			return null;

		if (nResults > 1) {

			for (int i= 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IJavaElement curr= result[i];
				if (curr instanceof IMember)
					HTMLPrinter.addBullet(buffer, getInfoText(curr, null, false));
				HTMLPrinter.endBulletList(buffer);
			}

		} else {

			IJavaElement curr= result[0];
			if (curr instanceof IMember) {
				final IMember member= (IMember) curr;
				
				String constantValue= null;
				if (member instanceof IField)
					constantValue= computeFieldConstant(activePart, selection, (IField) member, monitor);
				
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member, constantValue, true));
				Reader reader;
				try {
					String content= JavadocContentAccess2.getHTMLContent(member, true, true);
					reader= content == null ? null : new StringReader(content);
					
					// Provide hint why there's no Javadoc
					if (reader == null && member.isBinary()) {
						boolean hasAttachedJavadoc= JavaDocLocations.getJavadocBaseLocation(member) != null;
						IPackageFragmentRoot root= (IPackageFragmentRoot)member.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
						boolean hasAttachedSource= root != null && root.getSourceAttachmentPath() != null;
						IOpenable openable= member.getOpenable();
						boolean hasSource= openable.getBuffer() != null;
						
						if (!hasAttachedSource && !hasAttachedJavadoc)
							reader= new StringReader(InfoViewMessages.JavadocView_noAttachments);
						else if (!hasAttachedJavadoc && !hasSource)
							reader= new StringReader(InfoViewMessages.JavadocView_noAttachedJavadoc);
						else if (!hasAttachedSource)
							reader= new StringReader(InfoViewMessages.JavadocView_noAttachedSource);
						else if (!hasSource)
							reader= new StringReader(InfoViewMessages.JavadocView_noInformation);
					}
					
				} catch (JavaModelException ex) {
					reader= new StringReader(InfoViewMessages.JavadocView_error_gettingJavadoc);
					JavaPlugin.log(ex.getStatus());
				}
				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
			}
		}

		boolean flushContent= true;
		if (buffer.length() > 0 || flushContent) {
			HTMLPrinter.insertPageProlog(buffer, 0, null, fBackgroundColorRGB, fgStyleSheet);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	/**
	 * Gets the label for the given member.
	 *
	 * @param member the Java member
	 * @param constantValue the constant value if any
	 * @param allowImage true if the java element image should be shown
	 * @return a string containing the member's label
	 */
	private String getInfoText(IJavaElement member, String constantValue, boolean allowImage) {
		StringBuffer label= new StringBuffer(JavaElementLabels.getElementLabel(member, LABEL_FLAGS));
		if (member.getElementType() == IJavaElement.FIELD && constantValue != null) {
			label.append(constantValue);
		}
		
		String imageName= null;
		if (allowImage) {
			URL imageUrl= JavaPlugin.getDefault().getImagesOnFSRegistry().getImageURL(member);
			if (imageUrl != null) {
				imageName= imageUrl.toExternalForm();
			}
		}
		
		StringBuffer buf= new StringBuffer();
		JavadocHover.addImageAndLabel(buf, imageName, 16, 16, 8, 5, label.toString(), 22, 0);
		return buf.toString();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#isIgnoringNewInput(org.eclipse.jdt.core.IJavaElement, org.eclipse.jface.viewers.ISelection)
	 * @since 3.2
	 */
	protected boolean isIgnoringNewInput(IJavaElement je, IWorkbenchPart part, ISelection selection) {
		if (fCurrent != null && fCurrent.getInputElement() instanceof URL)
			return false;
		
		if (super.isIgnoringNewInput(je, part, selection)
				&& part instanceof ITextEditor
				&& selection instanceof ITextSelection) {
			
			ITextEditor editor= (ITextEditor)part;
			IDocumentProvider docProvider= editor.getDocumentProvider();
			if (docProvider == null)
				return false;
			
			IDocument document= docProvider.getDocument(editor.getEditorInput());
			if (!(document instanceof IDocumentExtension3))
				return false;
			
			try {
				int offset= ((ITextSelection)selection).getOffset();
				String partition= ((IDocumentExtension3)document).getContentType(IJavaPartitions.JAVA_PARTITIONING, offset, false);
				return  partition != IJavaPartitions.JAVA_DOC;
			} catch (BadPartitioningException ex) {
				return false;
			} catch (BadLocationException ex) {
				return false;
			}

		}
		return false;
	}

	/*
	 * @see AbstractInfoView#findSelectedJavaElement(IWorkbenchPart)
	 */
	protected IJavaElement findSelectedJavaElement(IWorkbenchPart part, ISelection selection) {
		IJavaElement element;
		try {
			element= super.findSelectedJavaElement(part, selection);

			if (element == null && part instanceof JavaEditor && selection instanceof ITextSelection) {

				JavaEditor editor= (JavaEditor)part;
				ITextSelection textSelection= (ITextSelection)selection;

				IDocumentProvider documentProvider= editor.getDocumentProvider();
				if (documentProvider == null)
					return null;

				IDocument document= documentProvider.getDocument(editor.getEditorInput());
				if (document == null)
					return null;

				ITypedRegion typedRegion= TextUtilities.getPartition(document, IJavaPartitions.JAVA_PARTITIONING, textSelection.getOffset(), false);
				if (IJavaPartitions.JAVA_DOC.equals(typedRegion.getType()))
					return TextSelectionConverter.getElementAtOffset((JavaEditor)part, textSelection);
				else
					return null;
			} else
				return element;
		} catch (JavaModelException e) {
			return null;
		} catch (BadLocationException e) {
			return null;
		}
	}

	/*
	 * @see AbstractInfoView#getControl()
	 */
	protected Control getControl() {
		if (fIsUsingBrowserWidget)
			return fBrowser;
		else
			return fText;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getHelpContextId()
	 * @since 3.1
	 */
	protected String getHelpContextId() {
		return IJavaHelpContextIds.JAVADOC_VIEW;
	}
	
	/**
	 * Compute the textual representation of a 'static' 'final' field's constant initializer value.
	 * 
	 * @param activePart the part that triggered the computation, or <code>null</code>
	 * @param selection the selection that references the field, or <code>null</code>
	 * @param resolvedField the filed whose constant value will be computed
	 * @param monitor the progress monitor
	 * 
	 * @return the textual representation of the constant, or <code>null</code> if the
	 *   field is not a constant field, the initializer value could not be computed, or
	 *   the progress monitor was cancelled
	 * @since 3.4
	 */
	private String computeFieldConstant(IWorkbenchPart activePart, ISelection selection, IField resolvedField, IProgressMonitor monitor) {

		if (!isStaticFinal(resolvedField))
			return null;

		Object constantValue;
		IJavaProject preferenceProject;

		if (selection instanceof ITextSelection && activePart instanceof JavaEditor) {
			IEditorPart editor= (IEditorPart) activePart;
			ITypeRoot activeType= JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
			preferenceProject= activeType.getJavaProject();
			constantValue= getConstantValueFromActiveEditor(activeType, resolvedField, (ITextSelection) selection, monitor);
			if (constantValue == null) // fall back - e.g. when selection is inside Javadoc of the element
				constantValue= computeFieldConstantFromTypeAST(resolvedField, monitor);
		} else {
			constantValue= computeFieldConstantFromTypeAST(resolvedField, monitor);
			preferenceProject= resolvedField.getJavaProject();
		}

		if (constantValue != null)
			return getFormattedAssignmentOperator(preferenceProject) + formatCompilerConstantValue(constantValue);

		return null;
	}

	/**
	 * Retrieve a constant initializer value of a field by (AST) parsing field's type.
	 * 
	 * @param constantField the constant field
	 * @param monitor the progress monitor
	 * @return the constant value of the field, or <code>null</code> if it could not be computed
	 *   (or if the progress was cancelled).
	 * @since 3.4
	 */
	private Object computeFieldConstantFromTypeAST(IField constantField, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return null;
		
		CompilationUnit ast= SharedASTProvider.getAST(constantField.getTypeRoot(), SharedASTProvider.WAIT_NO, monitor);
		if (ast != null) {
			try {
				if (constantField.isEnumConstant())
					return null;

				VariableDeclarationFragment fieldDecl= ASTNodeSearchUtil.getFieldDeclarationFragmentNode(constantField, ast);
				Expression initializer= fieldDecl.getInitializer();
				if (initializer == null)
					return null;
				return initializer.resolveConstantExpressionValue();
			} catch (JavaModelException e) {
				// ignore the exception and try the next method
			}
		}

		if (monitor.isCanceled())
			return null;

		ASTParser p= ASTParser.newParser(AST.JLS3);
		p.setProject(constantField.getJavaProject());
		IBinding[] createBindings;
		try {
			createBindings= p.createBindings(new IJavaElement[] { constantField }, monitor);
		} catch (OperationCanceledException e) {
			return null;
		}

		IVariableBinding variableBinding= (IVariableBinding) createBindings[0];
		if (variableBinding != null)
			return variableBinding.getConstantValue();

		return null;
	}

	/**
	 * Tells whether the given member is static final.
	 * <p>
	 * XXX: Copied from {@link JavadocHover}.
	 * </p>
	 * @param member the member to test
	 * @return <code>true</code> if static final
	 * @since 3.4
	 */
	private static boolean isStaticFinal(IJavaElement member) {
		if (member.getElementType() != IJavaElement.FIELD)
			return false;
		
		IField field= (IField)member;
		try {
			return JdtFlags.isFinal(field) && JdtFlags.isStatic(field);
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
			return false;
		}
	}

	/**
	 * Returns the constant value for a field that is referenced by the currently active type.
	 * This method does may not run in the main UI thread.
	 * <p>
	 * XXX: This method was part of the JavadocHover#getConstantValue(IField field, IRegion hoverRegion)
	 * 		method (lines 299-314).
	 * </p>
	 * @param activeType the type that is currently active
	 * @param field the field that is being referenced (usually not declared in <code>activeType</code>)
	 * @param selection the region in <code>activeType</code> that contains the field reference
	 * @param monitor a progress monitor
	 * 
	 * @return the constant value for the given field or <code>null</code> if none
	 * @since 3.4
	 */
	private static Object getConstantValueFromActiveEditor(ITypeRoot activeType, IField field, ITextSelection selection, IProgressMonitor monitor) {
		Object constantValue= null;
		
		CompilationUnit unit= SharedASTProvider.getAST(activeType, SharedASTProvider.WAIT_ACTIVE_ONLY, monitor);
		if (unit == null)
			return null;
		
		ASTNode node= NodeFinder.perform(unit, selection.getOffset(), selection.getLength());
		if (node != null && node.getNodeType() == ASTNode.SIMPLE_NAME) {
			IBinding binding= ((SimpleName)node).resolveBinding();
			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding variableBinding= (IVariableBinding)binding;
				if (field.equals(variableBinding.getJavaElement())) {
					constantValue= variableBinding.getConstantValue();
				}
			}
		}
		return constantValue;
	}

	/**
	 * Returns the string representation of the given constant value.
	 * <p>
	 * XXX: In {@link JavadocHover} this method was part of JavadocHover#getConstantValue lines 318-361.
	 * </p>
	 * @param constantValue the constant value
	 * @return the string representation of the given constant value.
	 * @since 3.4
	 */
	private static String formatCompilerConstantValue(Object constantValue) {
		if (constantValue instanceof String) {
			StringBuffer result= new StringBuffer();
			result.append('"');
			String stringConstant= (String)constantValue;
			if (stringConstant.length() > 80) {
				result.append(stringConstant.substring(0, 80));
				result.append(JavaElementLabels.ELLIPSIS_STRING);
			} else {
				result.append(stringConstant);
			}
			result.append('"');
			return result.toString();
			
		} else if (constantValue instanceof Character) {
			String constantResult= '\'' + constantValue.toString() + '\'';
			
			char charValue= ((Character) constantValue).charValue();
			String hexString= Integer.toHexString(charValue);
			StringBuffer hexResult= new StringBuffer("\\u"); //$NON-NLS-1$
			for (int i= hexString.length(); i < 4; i++) {
				hexResult.append('0');
			}
			hexResult.append(hexString);
			return formatWithHexValue(constantResult, hexResult.toString());
			
		} else if (constantValue instanceof Byte) {
			int byteValue= ((Byte) constantValue).intValue() & 0xFF;
			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(byteValue)); //$NON-NLS-1$
			
		} else if (constantValue instanceof Short) {
			int shortValue= ((Short) constantValue).shortValue() & 0xFFFF;
			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(shortValue)); //$NON-NLS-1$
			
		} else if (constantValue instanceof Integer) {
			int intValue= ((Integer) constantValue).intValue();
			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(intValue)); //$NON-NLS-1$
			
		} else if (constantValue instanceof Long) {
			long longValue= ((Long) constantValue).longValue();
			return formatWithHexValue(constantValue, "0x" + Long.toHexString(longValue)); //$NON-NLS-1$
			
		} else {
			return constantValue.toString();
		}
	}

	/**
	 * Creates and returns the a formatted message for the given
	 * constant with its hex value.
	 * <p>
	 * XXX: Copied from {@link JavadocHover}.
	 * </p>
	 * @param constantValue
	 * @param hexValue
	 * @return a formatted string with constant and hex values
	 * @since 3.4
	 */
	private static String formatWithHexValue(Object constantValue, String hexValue) {
		return Messages.format(InfoViewMessages.JavadocView_constantValue_hexValue, new String[] { constantValue.toString(), hexValue });
	}

	/**
	 * Returns the assignment operator string with the project's formatting applied to it.
	 * <p>
	 * XXX: This method was extracted from JavadocHover#getInfoText method.
	 * </p>
	 * @param javaProject the Java project whose formatting options will be used.
	 * @return the formatted assignment operator string.
	 * @since 3.4
	 */
	private static String getFormattedAssignmentOperator(IJavaProject javaProject) {
		StringBuffer buffer= new StringBuffer();
		if (JavaCore.INSERT.equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, true)))
			buffer.append(' ');
		buffer.append('=');
		if (JavaCore.INSERT.equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, true)))
			buffer.append(' ');
		return buffer.toString();
	}
	
	/**
	 * see also org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover.addLinkListener(BrowserInformationControl)
	 * 
	 * Add link listener to the given browser
	 * @param browser the browser to add a listener to
	 * @since 3.4
	 */
	private void addLinkListener(Browser browser) {
		browser.addLocationListener(JavaElementLinks.createLocationListener(new JavaElementLinks.ILinkHandler() {

			/* (non-Javadoc)
			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleDeclarationLink(org.eclipse.jdt.core.IJavaElement)
			 */
			public void handleDeclarationLink(IJavaElement target) {
				try {
					JavaUI.openInEditor(target);
				} catch (PartInitException e) {
					JavaPlugin.log(e);
				} catch (JavaModelException e) {
					JavaPlugin.log(e);
				}
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleExternalLink(java.net.URL, org.eclipse.swt.widgets.Display)
			 */
			public boolean handleExternalLink(final URL url, Display display) {
				if (fCurrent == null || !url.equals(fCurrent.getInputElement())) {
					fCurrent= new URLBrowserInput(fCurrent, url);
					
					if (fBackAction != null) {
						fBackAction.update();
						fForthAction.update();
					}
					
					if (fInputSelectionProvider != null)
						fInputSelectionProvider.setSelection(new StructuredSelection(url));
				}
				
				return false;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleInlineJavadocLink(org.eclipse.jdt.core.IJavaElement)
			 */
			public void handleInlineJavadocLink(IJavaElement target) {
				JavaElementBrowserInput newInput= new JavaElementBrowserInput(fCurrent, target);
				JavadocView.this.setInput(newInput);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleJavadocViewLink(org.eclipse.jdt.core.IJavaElement)
			 */
			public void handleJavadocViewLink(IJavaElement target) {
				handleInlineJavadocLink(target);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleTextSet()
			 */
			public void handleTextSet() {
				IJavaElement input= getInput();
				if (input == null)
					return;
				
				if (fCurrent == null || !fCurrent.getInputElement().equals(input)) {
					fCurrent= new JavaElementBrowserInput(null, input);
					
					if (fBackAction != null) {
						fBackAction.update();
						fForthAction.update();
					}
				}
			}
		}));
	}

}
