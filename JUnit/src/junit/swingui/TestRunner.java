package junit.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;
import junit.runner.FailureDetailView;
import junit.runner.SimpleTestCollector;
import junit.runner.TestCollector;
import junit.runner.Version;

/**
 * A Swing based user interface to run tests.
 * Enter the name of a class which either provides a static
 * suite method or is a subclass of TestCase.
 * <pre>
 * Synopsis: java junit.swingui.TestRunner [-noloading] [TestCase]
 * </pre>
 * TestRunner takes as an optional argument the name of the testcase class to be run.
 */
public class TestRunner extends BaseTestRunner implements TestRunContext {
	protected JFrame fFrame;
	private Thread fRunner;
	private TestResult fTestResult;
	
	private JComboBox fSuiteCombo;
	private ProgressBar fProgressIndicator;
	private DefaultListModel fFailures;
	private JLabel fLogo;
	private CounterPanel fCounterPanel;
	private JButton fRun;
	private JButton fQuitButton;
	private JButton fRerunButton;
	private StatusLine fStatusLine;
	private FailureDetailView fFailureView;
	private JTabbedPane fTestViewTab;
	private JCheckBox fUseLoadingRunner;
	private Vector fTestRunViews= new Vector(); // view associated with tab in tabbed pane
	// private static Font PLAIN_FONT= StatusLine.PLAIN_FONT;
	// private static Font BOLD_FONT= StatusLine.BOLD_FONT;
	private static final int GAP= 4;
	private static final int HISTORY_LENGTH= 5;

	private static final String TESTCOLLECTOR_KEY= "TestCollectorClass";
	private static final String FAILUREDETAILVIEW_KEY= "FailureViewClass";
		
	public TestRunner() {
	} 
	
	public static void main(String[] args) {
		new TestRunner().start(args);
	}
	 
	public static void run(Class test) {
		String args[]= { test.getName() };
		main(args);
	}
	
	public void addError(final Test test, final Throwable t) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					TestRunner.this.fCounterPanel.setErrorValue(TestRunner.this.fTestResult.errorCount());
					appendFailure("Error", test, t);
				}
			}
		);
	}
	
	public void addFailure(final Test test, final AssertionFailedError t) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					TestRunner.this.fCounterPanel.setFailureValue(TestRunner.this.fTestResult.failureCount());
					appendFailure("Failure", test, t);
				}
			}		
		);
	}
	
	public void startTest(Test test) {
		postInfo("Running: "+test);
	}
	
	public void endTest(Test test) {
		postEndTest(test);
	}

	private void postEndTest(final Test test) {
		synchUI();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					if (TestRunner.this.fTestResult != null) {
						TestRunner.this.fCounterPanel.setRunValue(TestRunner.this.fTestResult.runCount());
						TestRunner.this.fProgressIndicator.step(TestRunner.this.fTestResult.wasSuccessful());
					}
				}
			}
		);
	}

	public void setSuite(String suiteName) {
		this.fSuiteCombo.getEditor().setItem(suiteName);
	}

	private void addToHistory(final String suite) {
		for (int i= 0; i < this.fSuiteCombo.getItemCount(); i++) {
			if (suite.equals(this.fSuiteCombo.getItemAt(i))) {
				this.fSuiteCombo.removeItemAt(i);
				this.fSuiteCombo.insertItemAt(suite, 0);
				this.fSuiteCombo.setSelectedIndex(0);
				return;
			}
		}
		this.fSuiteCombo.insertItemAt(suite, 0);
		this.fSuiteCombo.setSelectedIndex(0);
		pruneHistory();
	}
	
	private void pruneHistory() {
		int historyLength= getPreference("maxhistory", HISTORY_LENGTH);
		if (historyLength < 1)
			historyLength= 1;
		for (int i= this.fSuiteCombo.getItemCount()-1; i > historyLength-1; i--) 
			this.fSuiteCombo.removeItemAt(i);
	}
	
	private void appendFailure(String kind, Test test, Throwable t) {
		this.fFailures.addElement(new TestFailure(test, t));
		if (this.fFailures.size() == 1) 
			revealFailure(test);
	}
	
	private void revealFailure(Test test) {
		for (Enumeration e= this.fTestRunViews.elements(); e.hasMoreElements(); ) {
			TestRunView v= (TestRunView) e.nextElement();
			v.revealFailure(test);
		}
	}
		
	protected void aboutToStart(final Test testSuite) {
		for (Enumeration e= this.fTestRunViews.elements(); e.hasMoreElements(); ) {
			TestRunView v= (TestRunView) e.nextElement();
			v.aboutToStart(testSuite, this.fTestResult);
		}
	}
	
	protected void runFinished(final Test testSuite) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					for (Enumeration e= TestRunner.this.fTestRunViews.elements(); e.hasMoreElements(); ) {
						TestRunView v= (TestRunView) e.nextElement();
						v.runFinished(testSuite, TestRunner.this.fTestResult);
					}
				}
			}
		);
	}

	protected CounterPanel createCounterPanel() {
		return new CounterPanel();
	}
	
	protected JPanel createFailedPanel() {
		JPanel failedPanel= new JPanel(new GridLayout(0, 1, 0, 2));
		this.fRerunButton= new JButton("Run");
		this.fRerunButton.setEnabled(false);
		this.fRerunButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rerun();
				}
			}
		);
		failedPanel.add(this.fRerunButton);
		return failedPanel;
	}
			
	protected FailureDetailView createFailureDetailView() {
		String className= BaseTestRunner.getPreference(FAILUREDETAILVIEW_KEY);
		if (className != null) {			
			Class viewClass= null;
			try {
				viewClass= Class.forName(className);
				return (FailureDetailView)viewClass.newInstance();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(this.fFrame, "Could not create Failure DetailView - using default view");
			}
		}
		return new DefaultFailureDetailView();
	}

	/**
	 * Creates the JUnit menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createJUnitMenu() {
		JMenu menu= new JMenu("JUnit");
		menu.setMnemonic('J');
		JMenuItem mi1= new JMenuItem("About...");
		mi1.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            about();
		        }
		    }
		);
		mi1.setMnemonic('A');
		menu.add(mi1);
		
		menu.addSeparator();
		JMenuItem mi2= new JMenuItem(" Exit ");
		mi2.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            terminate();
		        }
		    }
		);
		mi2.setMnemonic('x');
		menu.add(mi2);

		return menu;
	}
	
	protected JFrame createFrame(String title) {
		JFrame frame= new JFrame("JUnit");
		Image icon= loadFrameIcon();	
		if (icon != null)
			frame.setIconImage(icon);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					terminate();
				}
			}
		);
		return frame;
	}
	
	protected JLabel createLogo() {
		JLabel label;
		Icon icon= getIconResource(BaseTestRunner.class, "logo.gif");
		if (icon != null) 
			label= new JLabel(icon);
		else
			label= new JLabel("JV");
		label.setToolTipText("JUnit Version "+Version.id());
		return label;
	}
	
	protected void createMenus(JMenuBar mb) {
		mb.add(createJUnitMenu());
	}
		
	protected JCheckBox createUseLoaderCheckBox() {
		boolean useLoader= useReloadingTestSuiteLoader();
		JCheckBox box= new JCheckBox("Reload classes every run", useLoader);
		box.setToolTipText("Use a custom class loader to reload the classes for every run");
		if (inVAJava())
			box.setVisible(false);
		return box;
	}
	
	protected JButton createQuitButton() {
		 // spaces required to avoid layout flicker
		 // Exit is shorter than Stop that shows in the same column
		JButton quit= new JButton(" Exit "); 
		quit.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					terminate();
				}
			}
		);
		return quit;
	}
	
	protected JButton createRunButton() {
		JButton run= new JButton("Run");
		run.setEnabled(true);
		run.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSuite();
				}
			}
		);
		return run;
	}
	
	protected Component createBrowseButton() {
		JButton browse= new JButton("...");
		browse.setToolTipText("Select a Test class");
		browse.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					browseTestClasses();
				}
			}
		);
		return browse;		
	}
	
	protected StatusLine createStatusLine() {
		return new StatusLine(420);
	}
	
	protected JComboBox createSuiteCombo() {
		JComboBox combo= new JComboBox();
		combo.setEditable(true);
		combo.setLightWeightPopupEnabled(false);
		
		combo.getEditor().getEditorComponent().addKeyListener(
			new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					textChanged();
					if (e.getKeyChar() == KeyEvent.VK_ENTER)
						runSuite();
				}
			}
		);
		try {
			loadHistory(combo);
		} catch (IOException e) {
			// fails the first time
		}
		combo.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						textChanged();
					}
				}
			}
		);
		return combo;
	}
	
	protected JTabbedPane createTestRunViews() {
		JTabbedPane pane= new JTabbedPane(SwingConstants.BOTTOM);

		FailureRunView lv= new FailureRunView(this);
		this.fTestRunViews.addElement(lv);
		lv.addTab(pane);
		
		TestHierarchyRunView tv= new TestHierarchyRunView(this);
		this.fTestRunViews.addElement(tv);
		tv.addTab(pane);
		
		pane.addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					testViewChanged();
				}
			}
		);
		return pane;
	}
	
	public void testViewChanged() {
		TestRunView view= (TestRunView)this.fTestRunViews.elementAt(this.fTestViewTab.getSelectedIndex());
		view.activate();
	}
	
	protected TestResult createTestResult() {
		return new TestResult();
	}
	
	protected JFrame createUI(String suiteName) {	
		JFrame frame= createFrame("JUnit");	
		JMenuBar mb= new JMenuBar();
		createMenus(mb);
		frame.setJMenuBar(mb);
	
		JLabel suiteLabel= new JLabel("Test class name:");
		this.fSuiteCombo= createSuiteCombo();
		this.fRun= createRunButton();
		frame.getRootPane().setDefaultButton(this.fRun);
		Component browseButton= createBrowseButton();
		
		this.fUseLoadingRunner= createUseLoaderCheckBox();
		this.fProgressIndicator= new ProgressBar();
		this.fCounterPanel= createCounterPanel();
		
		// JLabel failureLabel= new JLabel("Errors and Failures:");
		this.fFailures= new DefaultListModel();
		
		this.fTestViewTab= createTestRunViews();	
		JPanel failedPanel= createFailedPanel();
		
		this.fFailureView= createFailureDetailView();
		JScrollPane tracePane= new JScrollPane(this.fFailureView.getComponent(), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.fStatusLine= createStatusLine();
		this.fQuitButton= createQuitButton();
		this.fLogo= createLogo();
					
		JPanel panel= new JPanel(new GridBagLayout());
	
		addGrid(panel, suiteLabel,	0, 0, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fSuiteCombo, 	0, 1, 1, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, browseButton, 	1, 1, 1, GridBagConstraints.NONE, 			0.0, GridBagConstraints.WEST);
		addGrid(panel, this.fRun, 		2, 1, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);

		addGrid(panel, this.fUseLoadingRunner,  	0, 2, 3, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);
		addGrid(panel, new JSeparator(), 	0, 3, 3, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);

		
		addGrid(panel, this.fProgressIndicator, 	0, 4, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fLogo, 			2, 4, 1, GridBagConstraints.NONE, 			0.0, GridBagConstraints.NORTH);

		addGrid(panel, this.fCounterPanel,	 0, 5, 2, GridBagConstraints.NONE, 			0.0, GridBagConstraints.CENTER);

		JSplitPane splitter= new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.fTestViewTab, tracePane);
		addGrid(panel, splitter, 	 0, 6, 2, GridBagConstraints.BOTH, 			1.0, GridBagConstraints.WEST);				

		addGrid(panel, failedPanel, 	 2, 6, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.NORTH/*CENTER*/);
		
		addGrid(panel, this.fStatusLine, 	 0, 8, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.CENTER);
		addGrid(panel, this.fQuitButton, 	 2, 8, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocation(200, 200);
		return frame;
	}

	private void addGrid(JPanel p, Component co, int x, int y, int w, int fill, double wx, int anchor) {
		GridBagConstraints c= new GridBagConstraints();
		c.gridx= x; c.gridy= y;
		c.gridwidth= w;
		c.anchor= anchor;
		c.weightx= wx;
		c.fill= fill;
		if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL)
			c.weighty= 1.0;
		c.insets= new Insets(y == 0 ? GAP : 0, x == 0 ? GAP : 0, GAP, GAP);
		p.add(co, c);
	}

	protected String getSuiteText() {
		if (this.fSuiteCombo == null)
			return "";
		return (String)this.fSuiteCombo.getEditor().getItem();
	}
	
	public ListModel getFailures() {
		return this.fFailures;
	}
	
	public void insertUpdate(DocumentEvent event) {
		textChanged();
	}
		
	public void browseTestClasses() {
		TestCollector collector= createTestCollector();		
		TestSelector selector= new TestSelector(this.fFrame, collector);
		if (selector.isEmpty()) {
			JOptionPane.showMessageDialog(this.fFrame, "No Test Cases found.\nCheck that the configured \'TestCollector\' is supported on this platform.");
			return;
		}
		selector.show();
		String className= selector.getSelectedItem();
		if (className != null)
			setSuite(className);
	}

	TestCollector createTestCollector() {
		String className= BaseTestRunner.getPreference(TESTCOLLECTOR_KEY);
		if (className != null) {			
			Class collectorClass= null;
			try {
				collectorClass= Class.forName(className);
				return (TestCollector)collectorClass.newInstance();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(this.fFrame, "Could not create TestCollector - using default collector");
			}
		}
		return new SimpleTestCollector();
	}
	
	private Image loadFrameIcon() {
		ImageIcon icon= (ImageIcon)getIconResource(BaseTestRunner.class, "smalllogo.gif");
		if (icon != null)
			return icon.getImage();
		return null;
	}
	
	private void loadHistory(JComboBox combo) throws IOException {
		BufferedReader br= new BufferedReader(new FileReader(getSettingsFile()));
		int itemCount= 0;
		try {
			String line;
			while ((line= br.readLine()) != null) {
				combo.addItem(line);
				itemCount++;
			}
			if (itemCount > 0)
				combo.setSelectedIndex(0);

		} finally {
			br.close();
		}
	}
	
	private File getSettingsFile() {
	 	String home= System.getProperty("user.home");
 		return new File(home,".junitsession");
 	}
	
	private void postInfo(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					showInfo(message);
				}
			}
		);
	}
	
	private void postStatus(final String status) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					showStatus(status);
				}
			}
		);
	}
	
	public void removeUpdate(DocumentEvent event) {
		textChanged();
	}
	
	private void rerun() {
		TestRunView view= (TestRunView)this.fTestRunViews.elementAt(this.fTestViewTab.getSelectedIndex());
		Test rerunTest= view.getSelectedTest();
		if (rerunTest != null)
			rerunTest(rerunTest);
	}
	
	private void rerunTest(Test test) {
		if (!(test instanceof TestCase)) {
			showInfo("Could not reload "+ test.toString());
			return;
		}
		Test reloadedTest= null;
		try {
			Class reloadedTestClass= getLoader().reload(test.getClass());
			Class[] classArgs= { String.class };
			Object[] args= new Object[]{((TestCase)test).getName()};
			Constructor constructor= reloadedTestClass.getConstructor(classArgs);
			reloadedTest=(Test)constructor.newInstance(args);
		} catch(Exception e) {
			showInfo("Could not reload "+ test.toString());
			return;
		}
		TestResult result= new TestResult();
		reloadedTest.run(result);
		
		String message= reloadedTest.toString();
		if(result.wasSuccessful())
			showInfo(message+" was successful");
		else if (result.errorCount() == 1) 
			showStatus(message+" had an error"); 
		else 
			showStatus(message+" had a failure");
	}

	protected void reset() {
		this.fCounterPanel.reset();
		this.fProgressIndicator.reset();
		this.fRerunButton.setEnabled(false);
		this.fFailureView.clear();
		this.fFailures.clear();
	}
	
	/**
	 * runs a suite.
	 * @deprecated use runSuite() instead
	 */
	public void run() {
		runSuite();
	}
	
	protected void runFailed(String message) {
		showStatus(message);
		this.fRun.setText("Run");
		this.fRunner= null;
	}
	
	synchronized public void runSuite() {
		if (this.fRunner != null) {
			this.fTestResult.stop();
		} else {
			setLoading(shouldReload());
			reset();
			showInfo("Load Test Case...");
			final String suiteName= getSuiteText();
			final Test testSuite= getTest(suiteName);		
			if (testSuite != null) {
				addToHistory(suiteName);
				doRunTest(testSuite);
			}
		}
	}
	
	private boolean shouldReload() {
		return !inVAJava() && this.fUseLoadingRunner.isSelected();
	}
	

	synchronized protected void runTest(final Test testSuite) {
		if (this.fRunner != null) {
			this.fTestResult.stop();
		} else {
			reset();	
			if (testSuite != null) {
				doRunTest(testSuite);
			}
		}
	}
	
	private void doRunTest(final Test testSuite) {
		setButtonLabel(this.fRun, "Stop");
		this.fRunner= new Thread("TestRunner-Thread") {
			public void run() {
				TestRunner.this.start(testSuite); 
				postInfo("Running...");
				
				long startTime= System.currentTimeMillis();
				testSuite.run(TestRunner.this.fTestResult);
					
				if (TestRunner.this.fTestResult.shouldStop()) {
					postStatus("Stopped");
				} else {
					long endTime= System.currentTimeMillis();
					long runTime= endTime-startTime;
					postInfo("Finished: " + elapsedTimeAsString(runTime) + " seconds");
				}
				runFinished(testSuite);
				setButtonLabel(TestRunner.this.fRun, "Run");
				TestRunner.this.fRunner= null;
				System.gc();
			}
		};
		// make sure that the test result is created before we start the
		// test runner thread so that listeners can register for it.
		this.fTestResult= createTestResult();
		this.fTestResult.addListener(TestRunner.this);
		aboutToStart(testSuite);

		this.fRunner.start();
	}

	private void saveHistory() throws IOException {
		BufferedWriter bw= new BufferedWriter(new FileWriter(getSettingsFile()));
		try {
			for (int i= 0; i < this.fSuiteCombo.getItemCount(); i++) {
				String testsuite= this.fSuiteCombo.getItemAt(i).toString();
				bw.write(testsuite, 0, testsuite.length());
				bw.newLine();
			}
		} finally {
			bw.close();
		}
	}
	
	private void setButtonLabel(final JButton button, final String label) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					button.setText(label);
				}
			}
		);
	}
	
	//	private void setLabelValue(final JTextField label, final int value) {
	//		SwingUtilities.invokeLater(
	//			new Runnable() {
	//				public void run() {
	//					label.setText(Integer.toString(value));
	//				}
	//			}
	//		);
	//	}
		
	public void handleTestSelected(Test test) {
		this.fRerunButton.setEnabled(test != null && (test instanceof TestCase));
		showFailureDetail(test);
	}

	private void showFailureDetail(Test test) {
		if (test != null) {
			ListModel failures= getFailures();
			for (int i= 0; i < failures.getSize(); i++) {
				TestFailure failure= (TestFailure)failures.getElementAt(i);
				if (failure.failedTest() == test) {
					this.fFailureView.showFailure(failure);
					return;
				}
			}
		}
		this.fFailureView.clear();
	}
		
	private void showInfo(String message) {
		this.fStatusLine.showInfo(message);
	}
	
	private void showStatus(String status) {
		this.fStatusLine.showError(status);
	}
	
	/**
	 * Starts the TestRunner
	 */
	public void start(String[] args) {		
		String suiteName= processArguments(args);
		this.fFrame= createUI(suiteName);
		this.fFrame.pack(); 
		this.fFrame.setVisible(true);

		if (suiteName != null) {
			setSuite(suiteName);
			runSuite();
		}
	}
		
	private void start(final Test test) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					int total= test.countTestCases();
					TestRunner.this.fProgressIndicator.start(total);
					TestRunner.this.fCounterPanel.setTotal(total);
				}
			}
		);
	}
	
	/**
	 * Wait until all the events are processed in the event thread
	 */
	private void synchUI() {
		try {
			SwingUtilities.invokeAndWait(
				new Runnable() {
					public void run() {}
				}		
			);
		}
		catch (Exception e) {
		}
	}
	
	/**
	 * Terminates the TestRunner
	 */
	public void terminate() {
		this.fFrame.dispose();
		try {
			saveHistory();
		} catch (IOException e) {
			System.out.println("Couldn't save test run history");
		}
		System.exit(0);
	}
	
	public void textChanged() {
		this.fRun.setEnabled(getSuiteText().length() > 0);
		clearStatus();
	}
	
	protected void clearStatus() {
		this.fStatusLine.clear();
	}
	
	public static Icon getIconResource(Class clazz, String name) {
		URL url= clazz.getResource(name);
		if (url == null) {
			System.err.println("Warning: could not load \""+name+"\" icon");
			return null;
		} 
		return new ImageIcon(url);
	}
	
	private void about() {
		AboutDialog about= new AboutDialog(this.fFrame); 
		about.show();
	}
	

}