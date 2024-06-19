package PROXY.GUI.after;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class FTPGUI extends JFrame {
  public static final String newline = "\n";
  public static final String UPLOAD = "Upload";
  public static final String DOWNLOAD = "Download";
  public static final String DELETE = "Delete";
  public static final String EXIT = "Exit";

  private JPanel pnlFTPUI;
  private JList localList;
  private JList remoteList;
  private DefaultListModel defLocalList, defRemoteList;
  private UploadButton btnUpload;
  private DownloadButton btnDownload;
  private DeleteButton btnDelete;

  public FTPGUI() throws Exception {
    super("Command Pattern - Example");

    // Create controls
    defLocalList = new DefaultListModel();
    defRemoteList = new DefaultListModel();
    localList = new JList(defLocalList);
    remoteList = new JList(defRemoteList);
    pnlFTPUI = new JPanel();

    localList.setSelectionMode(
      ListSelectionModel.SINGLE_SELECTION);
    localList.setSelectedIndex(-1);
    JScrollPane spLocalList = new JScrollPane(localList);

    remoteList.setSelectionMode(
      ListSelectionModel.SINGLE_SELECTION);
    remoteList.setSelectedIndex(-1);
    JScrollPane spRemoteList = new JScrollPane(remoteList);

    //Create Labels
    JLabel lblLocalList = new JLabel("Local List:");
    JLabel lblRemoteList = new JLabel("Remote List:");
    JLabel lblSpacer = new JLabel("         ");

    //Create buttons
    btnUpload = new UploadButton(FTPGUI.UPLOAD);
    btnUpload.setMnemonic(KeyEvent.VK_U);
    btnDownload = new DownloadButton(FTPGUI.DOWNLOAD);
    btnDownload.setMnemonic(KeyEvent.VK_N);
    btnDelete = new DeleteButton(FTPGUI.DELETE);
    btnDelete.setMnemonic(KeyEvent.VK_D);
    ExitButton btnExit = new ExitButton(FTPGUI.EXIT);
    btnExit.setMnemonic(KeyEvent.VK_X);

    buttonHandler vf = new buttonHandler();

    btnUpload.addActionListener(vf);
    btnDownload.addActionListener(vf);
    btnDelete.addActionListener(vf);
    btnExit.addActionListener(vf);

    JPanel lstPanel = new JPanel();

    GridBagLayout gridbag2 = new GridBagLayout();
    lstPanel.setLayout(gridbag2);
    GridBagConstraints gbc2 = new GridBagConstraints();
    lstPanel.add(lblLocalList);
    lstPanel.add(lblRemoteList);
    lstPanel.add(spLocalList);
    lstPanel.add(spRemoteList);
    lstPanel.add(lblSpacer);

    gbc2.gridx = 0;
    gbc2.gridy = 0;
    gridbag2.setConstraints(lblLocalList, gbc2);
    gbc2.gridx = 1;
    gbc2.gridy = 0;
    gridbag2.setConstraints(lblSpacer, gbc2);

    gbc2.gridx = 5;
    gbc2.gridy = 0;
    gridbag2.setConstraints(lblRemoteList, gbc2);
    gbc2.gridx = 0;
    gbc2.gridy = 1;
    gridbag2.setConstraints(spLocalList, gbc2);
    gbc2.gridx = 5;
    gbc2.gridy = 1;
    gridbag2.setConstraints(spRemoteList, gbc2);

    //-----------------------------------
    //For layout purposes, put the buttons in a separate panel
    JPanel buttonPanel = new JPanel();

    //----------------------------------------------
    GridBagLayout gridbag = new GridBagLayout();
    buttonPanel.setLayout(gridbag);
    GridBagConstraints gbc = new GridBagConstraints();
    buttonPanel.add(lstPanel);
    buttonPanel.add(btnUpload);
    buttonPanel.add(btnDownload);
    buttonPanel.add(btnDelete);
    buttonPanel.add(btnExit);

    gbc.insets.top = 5;
    gbc.insets.bottom = 5;
    gbc.insets.left = 5;
    gbc.insets.right = 5;

    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gridbag.setConstraints(btnUpload, gbc);
    gbc.gridx = 2;
    gbc.gridy = 0;
    gridbag.setConstraints(btnDownload, gbc);
    gbc.gridx = 3;
    gbc.gridy = 0;
    gridbag.setConstraints(btnDelete, gbc);
    gbc.gridx = 4;
    gbc.gridy = 0;
    gridbag.setConstraints(btnExit, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gridbag.setConstraints(lstPanel, gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets.left = 2;
    gbc.insets.right = 2;
    gbc.insets.top = 40;

    //****************************************************
    //Add the buttons and the log to the frame
    Container contentPane = getContentPane();
    contentPane.add(lstPanel, BorderLayout.CENTER);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    initialize();
    try {
      UIManager.setLookAndFeel(new WindowsLookAndFeel());
      SwingUtilities.updateComponentTreeUI(FTPGUI.this);
    } catch (Exception ex) {
      System.out.println(ex);
    }

  }
  private void initialize() {
    // fill some test data here into the listbox.
    defLocalList.addElement("first.html");
    defLocalList.addElement("second.html");
    defLocalList.addElement("third.html");
    defLocalList.addElement("fourth.html");
    defLocalList.addElement("fifth.html");
    defLocalList.addElement("Design Patterns 1.html");

    defRemoteList.addElement("sixth.html");
    defRemoteList.addElement("seventh.html");
    defRemoteList.addElement("eighth.html");
    defRemoteList.addElement("ninth.html");
    defRemoteList.addElement("Design Patterns 2.html");

  }

  public static void main(String[] args) throws Exception {

    JFrame frame = new FTPGUI();
    frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        }
                           );

    //frame.pack();
    frame.setSize(450, 300);
    frame.setVisible(true);
  }

  class buttonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      CommandInterface CommandObj =
        (CommandInterface) e.getSource();
      CommandObj.processEvent();
    }
  }

  interface CommandInterface {
    public void processEvent();
  }
  class UploadButton extends JButton 
	implements CommandInterface {

    public void processEvent() {
      int index = localList.getSelectedIndex();
      String selectedItem =
        localList.getSelectedValue().toString();
      ((DefaultListModel) localList.getModel()).remove(
        index);

      ((DefaultListModel) remoteList.getModel()).addElement(
        selectedItem);
    }
    public UploadButton(String name) {
      super(name);
    }
  }
  class DownloadButton extends JButton 
	implements CommandInterface {
    public void processEvent() {
      int index = remoteList.getSelectedIndex();
      String selectedItem =
        remoteList.getSelectedValue().toString();
      ((DefaultListModel) remoteList.getModel()).remove(
        index);

      ((DefaultListModel) localList.getModel()).addElement(
        selectedItem);
    }
    public DownloadButton(String name) {
      super(name);
    }
  }

  class DeleteButton extends JButton 
	implements CommandInterface {

    public void processEvent() {
      int index = localList.getSelectedIndex();
      if (index >= 0) {
        ((DefaultListModel) localList.getModel()).remove(
          index);
      }

      index = remoteList.getSelectedIndex();
      if (index >= 0) {
        ((DefaultListModel) remoteList.getModel()).remove(
          index);
      }
    }
    public DeleteButton(String name) {
      super(name);
    }
  }
  class ExitButton extends JButton 
	implements CommandInterface {

    public void processEvent() {
      System.exit(1);
    }
    public ExitButton(String name) {
      super(name);
    }
  }

}// end of class

