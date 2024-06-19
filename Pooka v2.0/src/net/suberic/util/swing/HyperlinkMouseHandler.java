package net.suberic.util.swing;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.text.Document;

/**
 * This is a class which tracks hyperlink action for a JEditorPane.
 */
public class HyperlinkMouseHandler extends MouseInputAdapter {

    public class URLSelection {
	public URL url;
	public int start;
	public int end;
	public JEditorPane editor;
	
	public URLSelection(JEditorPane newEditor, URL newUrl, int newStart, int newEnd) {
	    url = newUrl;
	    start = newStart;
	    end = newEnd;
	    editor = newEditor;
	}
    }

    private int lineLength;
    private URLSelection currentSelection = null;

    public HyperlinkMouseHandler(int newLineLength) {
	lineLength = newLineLength;
    }

    // track the moving of the mouse.
    public void mouseMoved(MouseEvent e) {
	JEditorPane editor = (JEditorPane) e.getSource();
	if (!editor.isEditable()) {
	    Point pt = new Point(e.getX(), e.getY());
	    URLSelection newSelection = getIndicatedURL(pt, editor);
	    if (newSelection != currentSelection) {
		if (currentSelection != null) {
		    editor.fireHyperlinkUpdate(new HyperlinkEvent(currentSelection, HyperlinkEvent.EventType.EXITED, currentSelection.url));
		}
		
		if (newSelection != null) {
		    editor.fireHyperlinkUpdate(new HyperlinkEvent(newSelection, HyperlinkEvent.EventType.ENTERED, newSelection.url));
		}
		currentSelection = newSelection;
	    }
	}
    }
    
    URLSelection getIndicatedURL(Point pt, JEditorPane editor) {
	int pos = editor.viewToModel(pt);
	if (pos >= 0) {
	    try {
		Document doc = editor.getDocument();
		int docLength = doc.getLength();
		
		int wordStart = 0;
		
		int startOffset = pos;
		int relativePosition;
		boolean startFound = false;
		
		while ( ! startFound ) {
		    startOffset = startOffset - lineLength;
		    relativePosition = lineLength;
		    
		    if (startOffset < 0) {
			relativePosition = relativePosition + startOffset;
			startOffset = 0;
		    }
		    
		    String possibleText = doc.getText(startOffset, relativePosition);
		    char[] charArray = possibleText.toCharArray();
		    for (int i = relativePosition - 1; (! startFound && i >= 0 ) ; i--) {  
			if (Character.isWhitespace(charArray[i]) || charArray[i] == '(' || charArray[i] == ')' || charArray[i] == '<' || charArray[i] == '>') {
			    startFound = true;
			    wordStart = startOffset + i + 1;
			}
		    }
		    
		    if (startOffset == 0)
			startFound = true;
		}
		
		int wordEnd = docLength - 1;
		startOffset = pos - 1 - lineLength;
		int length = lineLength;
		boolean endFound = false;
		
		while ( ! endFound ) {
		    startOffset = startOffset + lineLength;
		    
		    if (startOffset + lineLength > docLength) {
			length = docLength - startOffset;
		    } 
		    
		    String possibleText = doc.getText(startOffset, length);
		    char[] charArray = possibleText.toCharArray();
		    for (int i = 0; (! endFound && i < length ) ; i++) {  
			if (Character.isWhitespace(charArray[i]) || charArray[i] == '(' || charArray[i] == ')' || charArray[i] == '<' || charArray[i] == '>') {
			    endFound = true;
			    wordEnd = startOffset + i - 1;
			}
		    }
		    
		    if (startOffset + length >= docLength)
			endFound = true;
		}
		
		int wordLength = wordEnd - wordStart + 1;
		if (wordLength > 3) {
		    String word = doc.getText(wordStart, wordLength);
		    if (word.indexOf("://") != -1) {
			try {
			    URL urlSelected = new URL(word);
			    
			    return new URLSelection(editor, urlSelected, wordStart, wordEnd);
			} catch (MalformedURLException mue) {
			}
		    }
		}
	    } catch (javax.swing.text.BadLocationException ble) {

	    }
	}
	
	return null;
    }

    public void mouseClicked(MouseEvent e) {
	JEditorPane editor = (JEditorPane) e.getSource();
	if (!editor.isEditable()) {
	    Point pt = new Point(e.getX(), e.getY());
	    URLSelection selection = getIndicatedURL(pt, editor);
	    if (selection != null) {
		editor.fireHyperlinkUpdate(new HyperlinkEvent(selection, HyperlinkEvent.EventType.ACTIVATED, selection.url));
	    }
	}
    }
    
}
