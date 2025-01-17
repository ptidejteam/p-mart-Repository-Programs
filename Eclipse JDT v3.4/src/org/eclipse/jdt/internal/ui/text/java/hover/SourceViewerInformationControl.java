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
package org.eclipse.jdt.internal.ui.text.java.hover;

import org.eclipse.core.runtime.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jdt.internal.ui.text.SimpleJavaSourceViewerConfiguration;

/**
 * Source viewer based implementation of <code>IInformationControl</code>.
 * Displays information in a source viewer.
 *
 * @since 3.0
 */
public class SourceViewerInformationControl implements IInformationControl, IInformationControlExtension, IInformationControlExtension3, IInformationControlExtension5, DisposeListener {

	/** The control's shell */
	private Shell fShell;
	/** The control's text widget */
	private StyledText fText;
	/** The text font (do not dispose!) */
	private Font fTextFont;
	/** The control's source viewer */
	private SourceViewer fViewer;
	/**
	 * The optional status field.
	 *
	 * @since 3.0
	 */
	private Label fStatusField;
	/**
	 * The separator for the optional status field.
	 *
	 * @since 3.0
	 */
	private Label fSeparator;
	/**
	 * The font of the optional status text label.
	 *
	 * @since 3.0
	 */
	private Font fStatusTextFont;
	/**
	 * The width size constraint.
	 * @since 3.2
	 */
	private int fMaxWidth= SWT.DEFAULT;
	/**
	 * The height size constraint.
	 * @since 3.2
	 */
	private int fMaxHeight= SWT.DEFAULT;
	/**
	 * The orientation of the shell
	 * @since 3.4
	 */
	private final int fOrientation;
	
	private Color fBackgroundColor;
	private boolean fIsSystemBackgroundColor= true;


	/**
	 * Creates a source viewer information control with the given shell as parent. The given
	 * styles are applied to the created styled text widget. The status field will
	 * contain the given text or be hidden.
	 *
	 * @param parent the parent shell
	 * @param isResizable <code>true</code> if resizable
	 * @param orientation the orientation
	 * @param statusFieldText the text to be used in the optional status field
	 *            or <code>null</code> if the status field should be hidden
	 */
	public SourceViewerInformationControl(Shell parent, boolean isResizable, int orientation, String statusFieldText) {
		Assert.isLegal(orientation == SWT.RIGHT_TO_LEFT || orientation == SWT.LEFT_TO_RIGHT || orientation == SWT.NONE);
		fOrientation= orientation;
		
		GridLayout layout;
		GridData gd;

		int shellStyle= SWT.TOOL | SWT.ON_TOP | orientation | (isResizable ? SWT.RESIZE : 0);
		int textStyle= isResizable ? SWT.V_SCROLL | SWT.H_SCROLL : SWT.NONE;

		fShell= new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | shellStyle);
		Display display= fShell.getDisplay();
		
		initializeColors();
		
		Composite composite= fShell;
		layout= new GridLayout(1, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		gd= new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gd);

		if (statusFieldText != null) {
			composite= new Composite(composite, SWT.NONE);
			layout= new GridLayout(1, false);
			layout.marginHeight= 0;
			layout.marginWidth= 0;
			layout.verticalSpacing= 1;
			composite.setLayout(layout);
			gd= new GridData(GridData.FILL_BOTH);
			composite.setLayoutData(gd);
			composite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			composite.setBackground(fBackgroundColor);
		}

		// Source viewer
		IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();
		fViewer= new JavaSourceViewer(composite, null, null, false, textStyle, store);
		fViewer.configure(new SimpleJavaSourceViewerConfiguration(JavaPlugin.getDefault().getJavaTextTools().getColorManager(), store, null, IJavaPartitions.JAVA_PARTITIONING, false));
		fViewer.setEditable(false);

		fText= fViewer.getTextWidget();
		gd= new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
		fText.setLayoutData(gd);
		fText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		fText.setBackground(fBackgroundColor);
		
		initializeFont();

		fText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e)  {
				if (e.character == 0x1B) // ESC
					fShell.dispose();
			}

			public void keyReleased(KeyEvent e) {}
		});

		// Status field
		if (statusFieldText != null) {

			// Horizontal separator line
			fSeparator= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
			fSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// Status field label
			fStatusField= new Label(composite, SWT.RIGHT);
			fStatusField.setText(statusFieldText);
			Font font= fStatusField.getFont();
			FontData[] fontDatas= font.getFontData();
			for (int i= 0; i < fontDatas.length; i++)
				fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
			fStatusTextFont= new Font(fStatusField.getDisplay(), fontDatas);
			fStatusField.setFont(fStatusTextFont);
			GridData gd2= new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
			fStatusField.setLayoutData(gd2);

			// Regarding the color see bug 41128
			fStatusField.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
			fStatusField.setBackground(fBackgroundColor);
		}

		addDisposeListener(this);
	}

	private void initializeColors() {
		RGB bgRGB= getHoverBackgroundColorRGB();
		if (bgRGB != null) {
			fBackgroundColor= new Color(fShell.getDisplay(), bgRGB);
			fIsSystemBackgroundColor= false;
		} else {
			fBackgroundColor= fShell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
			fIsSystemBackgroundColor= true;
		}
	}
	
	private RGB getHoverBackgroundColorRGB() {
		IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR_SYSTEM_DEFAULT)
			? null
			: PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR);
	}

	/**
	 * Initialize the font to the Java editor font.
	 * 
	 * @since 3.2
	 */
	private void initializeFont() {
		fTextFont= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		StyledText styledText= getViewer().getTextWidget();
		styledText.setFont(fTextFont);
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		if (input instanceof String)
			setInformation((String)input);
		else
			setInformation(null);
	}

	/*
	 * @see IInformationControl#setInformation(String)
	 */
	public void setInformation(String content) {
		if (content == null) {
			fViewer.setInput(null);
			return;
		}

		IDocument doc= new Document(content);
		JavaPlugin.getDefault().getJavaTextTools().setupJavaDocumentPartitioner(doc, IJavaPartitions.JAVA_PARTITIONING);
		fViewer.setInput(doc);
	}

	/*
	 * @see IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
			fShell.setVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 * @since 3.0
	 */
	public void widgetDisposed(DisposeEvent event) {
		if (fStatusTextFont != null && !fStatusTextFont.isDisposed())
			fStatusTextFont.dispose();

		fStatusTextFont= null;
		fTextFont= null;
		fShell= null;
		fText= null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void dispose() {
		if (!fIsSystemBackgroundColor)
			fBackgroundColor.dispose();
		if (fShell != null && !fShell.isDisposed())
			fShell.dispose();
		else
			widgetDisposed(null);
	}

	/*
	 * @see IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		fShell.setSize(width, height);
	}

	/*
	 * @see IInformationControl#setLocation(Point)
	 */
	public void setLocation(Point location) {
		fShell.setLocation(location);
	}

	/*
	 * @see IInformationControl#setSizeConstraints(int, int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		fMaxWidth= maxWidth;
		fMaxHeight= maxHeight;
	}

	/*
	 * @see IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint() {
		// compute the preferred size
		int x= SWT.DEFAULT;
		int y= SWT.DEFAULT;
		Point size= fShell.computeSize(x, y);
		if (size.x > fMaxWidth)
			x= fMaxWidth;
		if (size.y > fMaxHeight)
			y= fMaxHeight;

		// recompute using the constraints if the preferred size is larger than the constraints
		if (x != SWT.DEFAULT || y != SWT.DEFAULT)
			size= fShell.computeSize(x, y, false);

		return size;
	}

	/*
	 * @see IInformationControl#addDisposeListener(DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		fShell.addDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#removeDisposeListener(DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener) {
		fShell.removeDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#setForegroundColor(Color)
	 */
	public void setForegroundColor(Color foreground) {
		fText.setForeground(foreground);
	}

	/*
	 * @see IInformationControl#setBackgroundColor(Color)
	 */
	public void setBackgroundColor(Color background) {
		fText.setBackground(background);
	}

	/*
	 * @see IInformationControl#isFocusControl()
	 */
	public boolean isFocusControl() {
		return fShell.getDisplay().getActiveShell() == fShell;
	}

	/*
	 * @see IInformationControl#setFocus()
	 */
	public void setFocus() {
		fShell.forceFocus();
		fText.setFocus();
	}

	/*
	 * @see IInformationControl#addFocusListener(FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		fText.addFocusListener(listener);
	}

	/*
	 * @see IInformationControl#removeFocusListener(FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		fText.removeFocusListener(listener);
	}

	/*
	 * @see IInformationControlExtension#hasContents()
	 */
	public boolean hasContents() {
		return fText.getCharCount() > 0;
	}

	protected ISourceViewer getViewer()  {
		return fViewer;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension3#computeTrim()
	 * @since 3.4
	 */
	public Rectangle computeTrim() {
		Rectangle trim= fShell.computeTrim(0, 0, 0, 0);
		addInternalTrim(trim);
		return trim;
	}

	/**
	 * Adds the internal trimmings to the given trim of the shell.
	 * 
	 * @param trim the shell's trim, will be updated
	 * @since 3.4
	 */
	private void addInternalTrim(Rectangle trim) {
		Rectangle textTrim= fText.computeTrim(0, 0, 0, 0);
		trim.x+= textTrim.x;
		trim.y+= textTrim.y;
		trim.width+= textTrim.width;
		trim.height+= textTrim.height;
		
		if (fStatusField != null) {
			trim.height+= fSeparator.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			trim.height+= fStatusField.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			trim.height+= 1; // verticalSpacing
		}
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension3#getBounds()
	 * @since 3.4
	 */
	public Rectangle getBounds() {
		return fShell.getBounds();
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresLocation()
	 * @since 3.4
	 */
	public boolean restoresLocation() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresSize()
	 * @since 3.4
	 */
	public boolean restoresSize() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#getInformationPresenterControlCreator()
	 * @since 3.4
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new SourceViewerInformationControl(parent, true, fOrientation, null);
			}
		};
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#containsControl(org.eclipse.swt.widgets.Control)
	 * @since 3.4
	 */
	public boolean containsControl(Control control) {
		do {
			if (control == fShell)
				return true;
			if (control instanceof Shell)
				return false;
			control= control.getParent();
		} while (control != null);
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#isVisible()
	 * @since 3.4
	 */
	public boolean isVisible() {
		return fShell != null && !fShell.isDisposed() && fShell.isVisible();
	}
	
	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#computeSizeConstraints(int, int)
	 */
	public Point computeSizeConstraints(int widthInChars, int heightInChars) {
		GC gc= new GC(fText);
		gc.setFont(fTextFont);
		int width= gc.getFontMetrics().getAverageCharWidth();
		int height= gc.getFontMetrics().getHeight();
		gc.dispose();

		return new Point(widthInChars * width, heightInChars * height);
	}
}
