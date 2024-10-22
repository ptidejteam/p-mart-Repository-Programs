/*
Copyright (C) 2001  Sten Loecher

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tudresden.ocl.sql;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import tudresden.ocl.NameCreator;
import tudresden.ocl.OclTree;
import tudresden.ocl.check.types.ModelFacade;
import tudresden.ocl.check.types.xmifacade.Model;
import tudresden.ocl.check.types.xmifacade.XmiParser;
import tudresden.ocl.codegen.CodeFragment;
import tudresden.ocl.codegen.decl.DeclarativeCodeFragment;
import tudresden.ocl.codegen.decl.ILSQLCodeGenerator;
import tudresden.ocl.gui.OCLEditor;

/**
 *  OCL2SQL is the main class of the OCL2SQL tool. It manages all the different
 *  components and the flow of information between them which generate some
 *  kind of SQL code. These components are:
 *  <ul>
 *      <li>ORMappingImp - for the object relational mapping</li>
 *      <li>SchemaGenerator - the generator of table schemas</li>
 *      <li>ObjectViewSchema - the object view generator and object relational interface for the integrity view generator</li>
 *      <li>ILSQLCodeGenerator - the integrity view generator</li>
 *      <li>TriggerGenerator - the generator that creates SQL92-Assertionreplacement triggers and ECA-trigger-templates</li>
 *  </ul>
 *  The purpose of the tool is to demonstrate the usability of UML/OCL in database design.
 *  It therefore takes an arbitrary UML model from an XMI source file and a number of OCL invariants
 *  and generates DDL (Data Definition Language) scripts that are executable on database systems.
 *  The following scripts are going to be generated during a project run:
 *  <ul>
 *      <li>a table schema to store persistent objects from a class model to a relational database</li>
 *      <li>object view definitions that serve as a middle tier between the table schema and the integrity evaluation mechanism</li>
 *      <li>integrity views that select all tupels from the specified objects resp. tables that do not belong to a certain OCL constraint</li>
 *      <li>trigger defintions for the automatic evaluation of the integrity views during the runtime</li>
 *  </ul>
 *  @author Sten Loecher
 */
public class OCL2SQL extends JPanel implements ActionListener {

    // Step 1
    private String xmiFileLocation;
    private String rulesFileLocation;
    private String theProjectDirectory;

    // Step 2
    private ModelFacade theModelFacade;
    private Model theRoughModel;
    private SimpleOCLEditorModel theOCLEditorModel;
    private OCLEditor theOCLEditor;

    // Step 3
    private ORMapping theORMapping;
    private ObjectViewSchema theObjectViewSchema;
    private SQLBuilder theSQLBuilder = new OracleSQLBuilder();
    private ILSQLCodeGenerator theSQLCodeGenerator;

    // default object relational mapping parameters
    private int ormClassToTableMode = 2;
    private int ormNumOfPKColumns = 1;
    private String ormPKColType = "int";
    private boolean ormOneTablePerAss = false;

    // result
    private java.util.List lIntegrityViews;
    private String resultTableSchema, resultObjectViews, resultIntegrityViews, resultTrigger;
    private String fileNameTableSchema = "tables.sql";
    private String fileNameObjectViews = "object_views.sql";
    private String fileNameIntegrityViews = "integrity_views.sql";
    private String fileNameTrigger = "trigger.sql";

    // some other parameters
    private String sqlCodeGenPatternCatalogue = (OclTree.class.getResource("codegen/decl/OCL2SQL4Oracle.xml")).toString();

    // gui
    protected JTabbedPane tabs;
    protected JButton bLoadConstraints, bSaveConstraints, bXmiSource, bLoadXmi, bProjectDirectory, bExecute;
    protected JTextField tfXmiSource, tfProjectDirectory, tfPKNoCol;
    protected JRadioButton rbInheritance0, rbInheritance1, rbInheritance2, rbAssociations0, rbAssociations1, rbTriggerAssertion, rbTriggerECA, rbTriggerNone;
    protected JComboBox cbPKType;
    protected JTextArea taResultTables, taResultObjectViews, taResultIntegrityViews, taResultTrigger, taProgress;
    protected JDialog dlgProgress;
    protected JFrame theMainFrame;
    protected JLabel progressLabel;

    // tokens
    public static String TRIGGER = "tr_";
    public static String INTERR = "integrity violation at ";

    /**
     *  The constructor.
     *  @param theMainFrame a main frame for the gui
     */
    public OCL2SQL(JFrame theMainFrame) {
        if (theMainFrame != null) buildGUI();
        this.theMainFrame = theMainFrame;
    }

    /**
     *  Main methode to start the gui of the OCL2SQL tool.
     */
    public static void main(String[] args) {
        JFrame theMainFrame = new JFrame("OCL2SQL");
        OCL2SQL ce = new OCL2SQL(theMainFrame);

        theMainFrame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        theMainFrame.getContentPane().add(ce);
        theMainFrame.setSize(600, 400);
        theMainFrame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    //                            methodes for gui
    // -------------------------------------------------------------------------

    /**
     *  Builds the gui.
     */
    protected void buildGUI() {
        tabs = new JTabbedPane();
        tabs.addTab("Input", getInputPane());
        tabs.addTab("Project", getProjectPane());
        tabs.addTab("Output", getOutputPane());
        tabs.addTab("About", getAboutPane());
        setLayout(new BorderLayout());
        add(tabs);
    }

    /**
     *  @return the input panel
     */
    protected JPanel getInputPane() {
        JPanel result = new JPanel(new BorderLayout());

        // constraints
        JPanel c = new JPanel(new BorderLayout());
        c.setBorder(BorderFactory.createTitledBorder(" Constraints "));

        theOCLEditor = new OCLEditor();
        theOCLEditorModel = new SimpleOCLEditorModel();
        theOCLEditor.setModel(theOCLEditorModel);
        c.add(theOCLEditor);

        JPanel buttons = new JPanel(new GridLayout(0,4));
        bLoadConstraints = new JButton("Load");
        bLoadConstraints.addActionListener(this);
        bSaveConstraints = new JButton("Save");
        bSaveConstraints.addActionListener(this);
        buttons.add(new JPanel());
        buttons.add(new JPanel());
        buttons.add(bLoadConstraints);
        buttons.add(bSaveConstraints);
        c.add(BorderLayout.SOUTH, buttons);

        result.add(c);

        // object oriented Model from XMI file
        JPanel oom = new JPanel(new BorderLayout());
        oom.setBorder(BorderFactory.createTitledBorder(" URL of XMI file "));
        tfXmiSource = new JTextField("");
        oom.add(BorderLayout.CENTER, tfXmiSource);

        JPanel oomb = new JPanel(new GridLayout(1,2));
        bXmiSource = new JButton(". . .");
        bXmiSource.addActionListener(this);
        oomb.add(bXmiSource);
        bLoadXmi = new JButton("Parse");
        bLoadXmi.addActionListener(this);
        oomb.add(bLoadXmi);

        oom.add(BorderLayout.EAST, oomb);

        result.add(BorderLayout.SOUTH, oom);

        return result;
    }

    /**
     *  @return the project panel
     */
    protected JPanel getProjectPane() {
        JPanel result = new JPanel(new BorderLayout());

        // project directory
        JPanel prdir = new JPanel(new BorderLayout());
        prdir.setBorder(BorderFactory.createTitledBorder(" project directory "));

        tfProjectDirectory = new JTextField("");
        prdir.add(BorderLayout.CENTER, tfProjectDirectory);

        bProjectDirectory = new JButton(". . .");
        bProjectDirectory.addActionListener(this);
        prdir.add(BorderLayout.EAST, bProjectDirectory);

        // object relational mapping
        JPanel orm = new JPanel(new BorderLayout());
        orm.setBorder(BorderFactory.createTitledBorder(" object relational mapping "));

        JPanel pInheritance = new JPanel();
        pInheritance.setLayout(new BoxLayout(pInheritance, BoxLayout.Y_AXIS));
        pInheritance.setBorder(BorderFactory.createTitledBorder(" classes to tables "));
        rbInheritance0 = new JRadioButton("one table per class");
        rbInheritance1 = new JRadioButton("one table per hierarchy");
        rbInheritance2 = new JRadioButton("one table per leaf class");
        ButtonGroup bgInheritance = new ButtonGroup();
        bgInheritance.add(rbInheritance0);
        bgInheritance.add(rbInheritance1);
        bgInheritance.add(rbInheritance2);
        rbInheritance0.setSelected(true);
        pInheritance.add(rbInheritance0);
        pInheritance.add(rbInheritance1);
        pInheritance.add(rbInheritance2);
        orm.add(BorderLayout.WEST, pInheritance);

        JPanel pAssociations = new JPanel();
        pAssociations.setLayout(new BoxLayout(pAssociations, BoxLayout.Y_AXIS));
        pAssociations.setBorder(BorderFactory.createTitledBorder(" associations "));
        rbAssociations0 = new JRadioButton("prefer foreign keys");
        rbAssociations1 = new JRadioButton("one table per association");
        ButtonGroup bgAssociations = new ButtonGroup();
        bgAssociations.add(rbAssociations0);
        bgAssociations.add(rbAssociations1);
        rbAssociations0.setSelected(true);
        pAssociations.add(rbAssociations0);
        pAssociations.add(rbAssociations1);
        orm.add(BorderLayout.CENTER, pAssociations);

        JPanel pPK = new JPanel(new BorderLayout());
        pPK.setBorder(BorderFactory.createTitledBorder(" primary keys "));

        JPanel pType = new JPanel(new BorderLayout());
        JLabel lType = new JLabel(" type:  ");
        lType.setOpaque(false);
        cbPKType = new JComboBox();
        cbPKType.addItem("NUMBER");
        cbPKType.addItem("VARCHAR");
        pType.add(BorderLayout.WEST, lType);
        pType.add(BorderLayout.CENTER, cbPKType);
        pPK.add(BorderLayout.WEST, pType);

        JPanel pNoCol = new JPanel(new BorderLayout());
        JLabel lNoCol = new JLabel("   number of columns:  ");
        lNoCol.setOpaque(false);
        tfPKNoCol = new JTextField("1", 5);
        tfPKNoCol.setEditable(true);
        pNoCol.add(BorderLayout.WEST, lNoCol);
        pNoCol.add(BorderLayout.CENTER, tfPKNoCol);
        pPK.add(BorderLayout.CENTER, pNoCol);

        orm.add(BorderLayout.SOUTH, pPK);

        // trigger
        JPanel tp = new JPanel();
        tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS));
        tp.setBorder(BorderFactory.createTitledBorder(" trigger "));
        rbTriggerAssertion = new JRadioButton("assertion replacement");
        rbTriggerECA = new JRadioButton("ECA template");
        rbTriggerNone = new JRadioButton("none");
        ButtonGroup bgTrigger = new ButtonGroup();
        bgTrigger.add(rbTriggerAssertion);
        bgTrigger.add(rbTriggerECA);
        bgTrigger.add(rbTriggerNone);
        rbTriggerAssertion.setSelected(true);
        tp.add(rbTriggerAssertion);
        tp.add(rbTriggerECA);
        tp.add(rbTriggerNone);

        // execute
        JPanel ep = new JPanel(new GridLayout(0,4));
        bExecute = new JButton("Execute Project");
        bExecute.addActionListener(this);
        ep.add(new JPanel());
        ep.add(new JPanel());
        ep.add(new JPanel());
        ep.add(bExecute);

        result.add(BorderLayout.NORTH, prdir);
        result.add(BorderLayout.CENTER, orm);
        result.add(BorderLayout.SOUTH, ep);
        result.add(BorderLayout.EAST, tp);

        return result;
    }

    /**
     *  @return the output panel
     */
    protected JPanel getOutputPane() {
        JPanel result = new JPanel(new BorderLayout());
        result.setBorder(BorderFactory.createEtchedBorder());

        JTabbedPane tpResult = new JTabbedPane();
        tpResult.setTabPlacement(SwingConstants.TOP);

        taResultTables = new JTextArea("");
        taResultTables.setEditable(false);
        taResultTables.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp1 = new JScrollPane(taResultTables);
        tpResult.addTab("Table Schema", sp1);

        taResultObjectViews = new JTextArea("");
        taResultObjectViews.setEditable(false);
        taResultObjectViews.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp2 = new JScrollPane(taResultObjectViews);
        tpResult.addTab("Object Views", sp2);

        taResultIntegrityViews = new JTextArea("");
        taResultIntegrityViews.setEditable(false);
        taResultIntegrityViews.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp3 = new JScrollPane(taResultIntegrityViews);
        tpResult.addTab("Integrity Views", sp3);

        taResultTrigger = new JTextArea("");
        taResultTrigger.setEditable(false);
        taResultTrigger.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp4 = new JScrollPane(taResultTrigger);
        tpResult.addTab("Trigger", sp4);

        result.add(tpResult);

        return result;
    }

    /**
     *  @return the about panel
     */
    protected JPanel getAboutPane() {
        JPanel result = new JPanel(new BorderLayout());

        JPanel allLogos=new JPanel(new BorderLayout());
        JPanel smallLogos=new JPanel();
        JPanel center=new JPanel(new GridLayout(0, 1));

        smallLogos.add(new JLabel( getImage("images/tulogo.gif") ));
        smallLogos.add(new JLabel( getImage("images/st.gif") ));
        allLogos.add(smallLogos);

        center.add(new JLabel("OCL2SQL", SwingConstants.CENTER));
        center.add(new JLabel("OCL Compiler written 1999/2000 by Frank Finger (frank@finger.org)", SwingConstants.CENTER));
        center.add(new JLabel("XMI support 2000 by Ralf Wiebicke (ralf@rw7.de)", SwingConstants.CENTER));
        center.add(new JLabel("OCL Editor 2001 by Steffen Zschaler (sz9@inf.tu-dresden.de)", SwingConstants.CENTER));
        center.add(new JLabel("SQL related parts 2001 by Sten Loecher (sten-loecher@gmx.de)", SwingConstants.CENTER));
        center.add(new JLabel("visit http://dresden-ocl.sourceforge.net/", SwingConstants.CENTER));
        center.add(new JLabel("Chair for Software Technology, Dresden University of Technology", SwingConstants.CENTER));

        result.add(allLogos, BorderLayout.NORTH);
        result.add(center);
        result.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        return result;
    }

    /**
     *  Reads the input data from the gui and updates the class parameters accordingly.
     */
    private boolean synchronizeObjectState() {
        // xmi source file
        xmiFileLocation = tfXmiSource.getText();
        if (xmiFileLocation.trim().length() == 0) {
            showMessage("Error", "You must specify a XMI source file !", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // object relational mapping
        if (rbInheritance0.isSelected()) {
            ormClassToTableMode = 2;
        } else if (rbInheritance1.isSelected()) {
            ormClassToTableMode = 0;
        } else if (rbInheritance2.isSelected()) {
            ormClassToTableMode = 1;
        }

        if (rbAssociations0.isSelected()) {
            ormOneTablePerAss = false;
        } else if (rbAssociations1.isSelected()) {
            ormOneTablePerAss = true;
        }

        try {
            ormNumOfPKColumns = (Integer.valueOf(tfPKNoCol.getText())).intValue();
        } catch(Exception e) {
            showMessage("Error", "Number of primary key columns is no valid number: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cbPKType.getSelectedItem().equals("VARCHAR")) {
            ormPKColType = "String";
        } else if (cbPKType.getSelectedItem().equals("NUMBER")) {
            ormPKColType = "int";
        }

        // project path
        theProjectDirectory = tfProjectDirectory.getText();
        if (theProjectDirectory.trim().length() == 0) {
            showMessage("Error", "You must specifiy a project directory !", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            File temp = new File(theProjectDirectory);
            if (theProjectDirectory.charAt(theProjectDirectory.length()-1) != temp.separatorChar) {
                theProjectDirectory += temp.separatorChar;
            }
        }

        return true;
    }

    /**
     *  Updates the output pane after project execution.
     */
    private void updateOutputPane() {
        taResultTables.setText(resultTableSchema);
        taResultObjectViews.setText(resultObjectViews);
        taResultIntegrityViews.setText(resultIntegrityViews);
        taResultTrigger.setText(resultTrigger);
    }

    /**
     *  Manages the user input from the gui.
     */
    public void actionPerformed(java.awt.event.ActionEvent event) {
        Object source=event.getSource();

        if (source == bLoadConstraints) {
            loadConstraints();
        } else if (source == bSaveConstraints) {
            saveConstraints();
        } else if (source == bXmiSource) {
            loadXmiSourceURL();
        } else if (source == bLoadXmi) {
            loadXmiSource();
        } else if (source == bProjectDirectory) {
            loadProjectPath();
        } else if (source == bExecute) {
            executeProject();
        }
    }

    /**
     *  Loads some OCL constraints from a source file.
     *  Offers a dialog box to specify the location of the file.
     */
    private void loadConstraints() {
        JFileChooser fileChooser = new JFileChooser();
        File theFile = null;
        FileInputStream fis;
        ObjectInputStream ois;
        String ext[] = {"ocl"};
        int dlgRetVal;

        // show open dialog
        fileChooser.setFileFilter(new SimpleFileFilter("OCL constraint files (*.ocl)", ext));
        fileChooser.setDialogTitle("Open Constraint List");
        dlgRetVal = fileChooser.showOpenDialog(this);

        // check for selected file
        theFile = fileChooser.getSelectedFile();
        if ((theFile == null) || (dlgRetVal == JFileChooser.CANCEL_OPTION)) return;
        System.err.println(theFile.getAbsolutePath());

        // load the constraints from the specified file
        try {
            fis = new FileInputStream(theFile.getAbsolutePath());
            ois = new ObjectInputStream(fis);

            theOCLEditorModel = (SimpleOCLEditorModel)ois.readObject();
            theOCLEditorModel.setModelFacade(theModelFacade);

            ois.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load constraint list.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        theOCLEditor.setModel(theOCLEditorModel);
    }

    /**
     *  Saves the specified OCL constraints to a file.
     *  Offers a dialog box to specify the location of the file.
     */
    private void saveConstraints() {
        JFileChooser fileChooser = new JFileChooser();
        File theFile = null;
        FileOutputStream fos;
        ObjectOutputStream oos;
        String ext[] = {"ocl"};
        int dlgRetVal;

        // show save dialog
        fileChooser.setFileFilter(new SimpleFileFilter("OCL constraint files (*.ocl)", ext));
        fileChooser.setDialogTitle("Save Constraint List");
        dlgRetVal = fileChooser.showSaveDialog(this);

        // check for selected file
        theFile = fileChooser.getSelectedFile();
        if ((theFile == null) || (dlgRetVal == JFileChooser.CANCEL_OPTION)) return;

        // save constraints to the specified file
        try {
            fos = new FileOutputStream(theFile.getAbsolutePath());
            oos = new ObjectOutputStream(fos);

            oos.writeObject(theOCLEditorModel);

            oos.flush();
            oos.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Could not save constraint list.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
        }

        // show success Message
        JOptionPane.showMessageDialog(this, "Constraint list successfully stored.", "", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *  Offers a dialog box to specify the location of a XMI file.
     */
    private void loadXmiSourceURL() {
        JFileChooser fileChooser = new JFileChooser();
        File theFile = null;
        FileInputStream fis;
        ObjectInputStream ois;
        String ext[] = {"xmi"};
        int dlgRetVal;

        // show open dialog
        fileChooser.setFileFilter(new SimpleFileFilter("XMI files (*.xmi)", ext));
        fileChooser.setDialogTitle("Choose XMI Source");
        dlgRetVal = fileChooser.showOpenDialog(this);

        // check for selected file
        theFile = fileChooser.getSelectedFile();
        if ((theFile == null) || (dlgRetVal == JFileChooser.CANCEL_OPTION)) return;
        System.err.println(theFile.getAbsolutePath());

        // update class parameter
        try {
            tfXmiSource.setText(theFile.toURL().toString());
        } catch(Exception e) {
        }
    }

    /**
     * Loads the class model from the specified XMI file.
     */
    private void loadXmiSource() {
        if (JOptionPane.showConfirmDialog(this, "This will delete all OCL constraints ?\nContinue anyway ?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        try {
            theModelFacade = XmiParser.createModel(new URL(tfXmiSource.getText()), "model in classic mode");
            theOCLEditorModel.setModelFacade(theModelFacade);
        } catch(Exception e) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, "Could not parse XMI source.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
            return;
        }

        theOCLEditorModel = new SimpleOCLEditorModel();
        theOCLEditorModel.setModelFacade(theModelFacade);
        theOCLEditor.setModel(theOCLEditorModel);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        JOptionPane.showMessageDialog(this, "XMI source has been parsed successfully.", "", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *  Offers a dialog box to specify the project path.
     *  The resulting scripts are going to be stored to this location.
     */
    private void loadProjectPath() {
        JFileChooser fileChooser = new JFileChooser();
        File theFile = null;
        FileInputStream fis;
        ObjectInputStream ois;

        // show open dialog
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose Project Directory");
        fileChooser.showOpenDialog(this);

        // check for selected file
        theFile = fileChooser.getSelectedFile();
        if (theFile == null) return;

        // load the constraints from the specified file
        tfProjectDirectory.setText(theFile.getAbsolutePath());
    }

    /**
     *  Code originally from ConstraintEvaluation.
     */
    protected Icon getImage(String name) {
        java.net.URL url= OclTree.class.getResource(name);
        ImageIcon ii=null;

        try {
            ii=new ImageIcon(url);
        }
        catch (RuntimeException e) {
            // image not found, go on without it...
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return ii;
    }

    /**
     *  Helper methode. Shows a message.
     */
    private void showMessage(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    /**
     *  Helper methode. Prepares a progress info dialog.
     */
    private void prepareProgressInfo() {
    }

    /**
     *  Helper methode. Shows a message in the progress info dialog.
     */
    private void increaseProgressInfo(String msg) {
        System.err.println(msg);
    }

    /**
     *  Helper methode. Closes the progress info dialog.
     */
    private void closeProgressWindows() {
    }

    // -------------------------------------------------------------------------
    //                     methodes for project execution
    // -------------------------------------------------------------------------

    /**
     *  Prepares the project execution. The following steps will be done:
     *  <ul>
     *      <li>creates the rough and classic model from the XMI Source</li>
     *      <li>mapps the classes to tables</li>
     *      <li>prepares the object relational interface for the integrity view generator</li>
     *      <li>initializes the integrity view generator</li>
     *  </ul>
     */
    public boolean prepareProjectExecution() {
        if (!synchronizeObjectState()) return false;

        // check XMI source
        try {
            theRoughModel = XmiParser.createRoughModel(new URL(xmiFileLocation), "model in rough mode");
            theModelFacade = XmiParser.createModel(new URL(xmiFileLocation), "model in classic mode");
        } catch(Exception e) {
            showMessage("Error", "Could not process XMI source file !\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // do object relational mapping
        theORMapping = new ORMappingImp(theRoughModel,
                                        ormClassToTableMode,
                                        ormNumOfPKColumns,
                                        ormPKColType,
                                        ormOneTablePerAss);

        // initialize object view shema
        theObjectViewSchema = new ObjectViewSchema(theORMapping, theSQLBuilder);

        // initialize SQL code generator
        theSQLCodeGenerator = new ILSQLCodeGenerator(sqlCodeGenPatternCatalogue);
        theSQLCodeGenerator.setORMappingScheme(theObjectViewSchema);
        lIntegrityViews = new ArrayList();

        return true;
    }

    /**
     *  Executes the project with the specified parameters.
     */
    public void executeProject() {
        StringBuffer tmp;
        String constraint, triggers[], involvedViews[], baseTables[];
        OclTree theTree;
        CodeFragment cf[];
        DeclarativeCodeFragment dcf;
        TriggerGenerator tg;
        NameCreator nameCreator = new NameCreator();
        Set involvedTables;

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        prepareProgressInfo();
        increaseProgressInfo("preparing project execution ...");
        if (!prepareProjectExecution()) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        // create table schema
        increaseProgressInfo("create table schema ...");
        SchemaGenerator ddlg = new SchemaGenerator(theORMapping, theSQLBuilder);
        ddlg.construct();
        resultTableSchema = ddlg.getCode();

        // create object views
        increaseProgressInfo("create object views ...");
        resultObjectViews = theObjectViewSchema.getViewDefinitions();

        // create integrity views
        increaseProgressInfo("generate integrity views ...");
        tmp = new StringBuffer();
        try {
            for (int i=0; i<theOCLEditorModel.getConstraintCount(); i++) {
                constraint = (theOCLEditorModel.getConstraintAt(i)).getData();
                theTree = OclTree.createTree(constraint, theModelFacade);
                theTree.setNameCreator(nameCreator);
                theTree.applyDefaultNormalizations();
                cf = theSQLCodeGenerator.getCode(theTree);
                tmp.append(cf[0].getCode() + ";\n");
                lIntegrityViews.add(cf[0]);
            }
        } catch(Exception e) {
            showMessage("Error", "Error during Integrity View generation.\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        resultIntegrityViews = tmp.toString();

        // create trigger definitions
        increaseProgressInfo("create triggers ...");
        tg = new TriggerGenerator(theSQLBuilder);
        tmp = new StringBuffer();

        for (int i=0; i<lIntegrityViews.size(); i++) {
            // get involved views and determine all base tables
            dcf = (DeclarativeCodeFragment)lIntegrityViews.get(i);
            involvedViews = dcf.getAdditionalInfo();
            involvedTables = new HashSet();
            triggers = new String[0];

            for (int k=0; k<involvedViews.length; k++) {
                baseTables = theObjectViewSchema.getQueriedTables(involvedViews[k]);
                for (int l=0; l<baseTables.length; l++) {
                    involvedTables.add(baseTables[l]);
                }
            }

            if (rbTriggerAssertion.isSelected()) {
                triggers = tg.getAssertionReplacement(TRIGGER,
                                                      INTERR + dcf.getName(),
                                                      dcf.getName(),
                                                      (String[])involvedTables.toArray(new String[involvedTables.size()]));
            } else if (rbTriggerECA.isSelected()) {
                triggers = tg.getECATriggerTemplate(TRIGGER,
                                                    dcf.getName(),
                                                    (String[])involvedTables.toArray(new String[involvedTables.size()]));
            }
            for (int k=0; k<triggers.length; k++) {
                tmp.append(triggers[k]);
                tmp.append("/\n");
            }
        }
        resultTrigger = tmp.toString();

        // update gui
        increaseProgressInfo("update output pane...");
        updateOutputPane();

        // save the results to the according files
        increaseProgressInfo("save results ...");
        saveResults();
        closeProgressWindows();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     *  Stores the resulting DDL scripts from project execution to according files.
     */
    private void saveResults() {
        FileWriter fw;

        try {
            fw = new FileWriter(theProjectDirectory + fileNameTableSchema);
            fw.write(resultTableSchema);
            fw.close();

            fw = new FileWriter(theProjectDirectory + fileNameObjectViews);
            fw.write(resultObjectViews);
            fw.close();

            fw = new FileWriter(theProjectDirectory + fileNameIntegrityViews);
            fw.write(resultIntegrityViews);
            fw.close();

            fw = new FileWriter(theProjectDirectory + fileNameTrigger);
            fw.write(resultTrigger);
            fw.close();
        } catch(Exception e) {
            showMessage("Error", "Could not write results !\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
