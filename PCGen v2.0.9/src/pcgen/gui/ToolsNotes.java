package pcgen.gui;

import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import pcgen.core.Globals;

/**
 * Create a simple panel to store application-level notes
 *
 * @author  Jason Buchanan <lonejedi70@yahoo.com>
 * @version $Revision: 1.1 $
 */
public class ToolsNotes extends javax.swing.JPanel
{

  JScrollPane notesCenter = new JScrollPane();
  static JTextArea d_Notes = new JTextArea();
  private NotesListener d_NotesHandler = new NotesListener();

  public ToolsNotes()
  {
    initComponents();
  }

  private void initComponents()
  {
    this.setLayout(new BorderLayout());
    d_Notes.setLineWrap(true);
    d_Notes.setWrapStyleWord(true);
    d_Notes.setDoubleBuffered(true);
    d_Notes.setMinimumSize(new Dimension(426, 180));

    d_Notes.setText(Globals.dmNotes);
    d_Notes.getDocument().addDocumentListener(d_NotesHandler);

    notesCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    notesCenter.setDoubleBuffered(true);
    notesCenter.setPreferredSize(new Dimension(446, 200));

    this.add(notesCenter, BorderLayout.CENTER);
    notesCenter.getViewport().add(d_Notes, null);
  }

  private final class NotesListener implements DocumentListener
  {
    public void insertUpdate(DocumentEvent docEvent)
    {
      Globals.dmNotes = d_Notes.getText();
    }
    public void removeUpdate(DocumentEvent docEvent)
    {
      Globals.dmNotes = d_Notes.getText();
    }
    public void changedUpdate(DocumentEvent docEvent)
    {
    }
  }
}
