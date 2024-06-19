package gmgen.gui;

import java.util.Enumeration;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.swing.undo.UndoableEdit;

public class ExtendedHTMLDocument extends HTMLDocument {

	public ExtendedHTMLDocument() {

	}

	public ExtendedHTMLDocument(AbstractDocument.Content c, StyleSheet styles) {
		super(c, styles);
	}

	public ExtendedHTMLDocument(StyleSheet styles) {
		super(styles);
	}

	public void replaceAttributes(Element e, AttributeSet a, HTML.Tag tag) {
		if( (e != null) && (a != null)) {
			try {
				writeLock();
				int start = e.getStartOffset();
				DefaultDocumentEvent changes = new DefaultDocumentEvent(start, e.getEndOffset() - start, DocumentEvent.EventType.CHANGE);
				AttributeSet sCopy = a.copyAttributes();
				changes.addEdit(new AttributeUndoableEdit(e, sCopy, false));
				MutableAttributeSet attr = (MutableAttributeSet) e.getAttributes();
				Enumeration aNames = attr.getAttributeNames();
				Object value;
				Object aName;
				while (aNames.hasMoreElements()) {
					aName = aNames.nextElement();
					value = attr.getAttribute(aName);
					if(value != null && !value.toString().equalsIgnoreCase(tag.toString())) {
						attr.removeAttribute(aName);
					}
				}
				attr.addAttributes(a);
				changes.end();
				fireChangedUpdate(changes);
				fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
			}
			finally {
				writeUnlock();
			}
		}
	}


	public void removeElements(Element e, int index, int count) throws BadLocationException {
		writeLock();
		int start = e.getElement(index).getStartOffset();
		int end = e.getElement(index + count - 1).getEndOffset();
		try {
			Element[] removed = new Element[count];
			Element[] added = new Element[0];
			for (int counter = 0; counter < count; counter++) {
				removed[counter] = e.getElement(counter + index);
			}
			DefaultDocumentEvent dde = new DefaultDocumentEvent(
				start, end - start, DocumentEvent.EventType.REMOVE);
				( (AbstractDocument.BranchElement) e).replace(index, removed.length,
				added);
			dde.addEdit(new ElementEdit(e, index, removed, added));
			UndoableEdit u = getContent().remove(start, end - start);
			if(u != null) {
				dde.addEdit(u);
			}
			postRemoveUpdate(dde);
			dde.end();
			fireRemoveUpdate(dde);
			if(u != null) {
				fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
			}
		}
		finally {
			writeUnlock();
		}
	}
}
