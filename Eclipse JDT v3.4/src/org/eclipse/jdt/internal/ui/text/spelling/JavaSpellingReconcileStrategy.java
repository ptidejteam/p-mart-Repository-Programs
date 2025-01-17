/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.ui.text.spelling;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import org.eclipse.ui.editors.text.EditorsUI;

import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;

/**
 * Reconcile strategy for spell checking comments.
 *
 * @since 3.1
 */
public class JavaSpellingReconcileStrategy extends SpellingReconcileStrategy {

	
	/**
	 * Spelling problem collector that forwards {@link SpellingProblem}s as
	 * {@link IProblem}s to the {@link IProblemRequestor}.
	 */
	private class SpellingProblemCollector implements ISpellingProblemCollector {

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#accept(org.eclipse.ui.texteditor.spelling.SpellingProblem)
		 */
		public void accept(SpellingProblem problem) {
			IProblemRequestor requestor= fRequestor;
			if (requestor != null) {
				try {
					int line= getDocument().getLineOfOffset(problem.getOffset()) + 1;
					String word= getDocument().get(problem.getOffset(), problem.getLength());
					boolean dictionaryMatch= false;
					boolean sentenceStart= false;
					if (problem instanceof JavaSpellingProblem) {
						dictionaryMatch= ((JavaSpellingProblem)problem).isDictionaryMatch();
						sentenceStart= ((JavaSpellingProblem) problem).isSentenceStart();
					}
					// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=81514
					IEditorInput editorInput= fEditor.getEditorInput();
					if (editorInput != null) {
						CoreSpellingProblem iProblem= new CoreSpellingProblem(problem.getOffset(), problem.getOffset() + problem.getLength() - 1, line, problem.getMessage(), word, dictionaryMatch, sentenceStart, getDocument(), editorInput.getName());
						requestor.acceptProblem(iProblem);
					}
				} catch (BadLocationException x) {
					// drop this SpellingProblem
				}
			}
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#beginCollecting()
		 */
		public void beginCollecting() {
			if (fRequestor != null)
				fRequestor.beginReporting();
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#endCollecting()
		 */
		public void endCollecting() {
			if (fRequestor != null)
				fRequestor.endReporting();
		}
	}

	
	/** The id of the problem */
	public static final int SPELLING_PROBLEM_ID= 0x80000000;

	/** Properties file content type */
	private static final IContentType JAVA_CONTENT_TYPE= Platform.getContentTypeManager().getContentType(JavaCore.JAVA_SOURCE_CONTENT_TYPE);

	/** The text editor to operate on. */
	private ITextEditor fEditor;

	/** The problem requester. */
	private IProblemRequestor fRequestor;

	
	/**
	 * Creates a new comment reconcile strategy.
	 *
	 * @param viewer the source viewer
	 * @param editor the text editor to operate on
	 */
	public JavaSpellingReconcileStrategy(ISourceViewer viewer, ITextEditor editor) {
		super(viewer, EditorsUI.getSpellingService());
		fEditor= editor;
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion region) {
		if (fRequestor != null && isSpellingEnabled())
			super.reconcile(region);
	}
	
	private boolean isSpellingEnabled() {
		return EditorsUI.getPreferenceStore().getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED);
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#createSpellingProblemCollector()
	 * @since 3.3
	 */
	protected ISpellingProblemCollector createSpellingProblemCollector() {
		return new SpellingProblemCollector();
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#getContentType()
	 * @since 3.3
	 */
	protected IContentType getContentType() {
		return JAVA_CONTENT_TYPE;
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		super.setDocument(document);
		updateProblemRequester();
	}

	/**
	 * Update the problem requester based on the current editor
	 */
	private void updateProblemRequester() {
		IAnnotationModel model= getAnnotationModel();
		fRequestor= (model instanceof IProblemRequestor) ? (IProblemRequestor) model : null;
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#getAnnotationModel()
	 * @since 3.3
	 */
	protected IAnnotationModel getAnnotationModel() {
		final IDocumentProvider documentProvider= fEditor.getDocumentProvider();
		if (documentProvider == null)
			return null;
		return documentProvider.getAnnotationModel(fEditor.getEditorInput());
	}
}
