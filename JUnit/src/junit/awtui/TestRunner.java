package junit.awtui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.lang.reflect.Constructor;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;
 
/**
 * An AWT based user interface to run tests.
 * Enter the name of a class which either provides a static
 * suite method or is a subclass of TestCase.
 * <pre>
 * Synopsis: java junit.awtui.TestRunner [-noloading] [TestCase]
 * </pre>
 * TestRunner takes as an optional argument the name of the testcase class to be run.
 */
 public class TestRunner extends BaseTestRunner {
	protected Frame fFrame;
	protected Vector fExceptions;
	protected Vector fFailedTests;
	protected Thread fRunner;
	protected TestResult fTestResult;
	
	protected TextArea fTraceArea;
	protected TextField fSuiteField;
	protected Button fRun;
	protected ProgressBar fProgressIndicator;
	protected List fFailureList;
	protected Logo fLogo;
	protected Label fNumberOfErrors;
	protected Label fNumberOfFailures;
	protected Label fNumberOfRuns;
	protected Button fQuitButton;
	protected Button fRerunButton;
	protected TextField fStatusLine;
	protected Checkbox fUseLoadingRunner;
	
	protected static Font PLAIN_FONT= new Font("dialog", Font.PLAIN, 12);
	private static final int GAP= 4;
	// private static final String SUITE_METHODNAME= "suite";
	
	public TestRunner() {
	}
	 
	private void about() {
		AboutDialog about= new AboutDialog(this.fFrame);
		about.setModal(true);
		about.setLocation(300, 300);
		about.setVisible(true);
	}
	
	public void addError(Test test, Throwable t) {
		this.fNumberOfErrors.setText(Integer.toString(this.fTestResult.errorCount()));
		appendFailure("Error", test, t);
	}

	public void addFailure(Test test, AssertionFailedError t) {
		this.fNumberOfFailures.setText(Integer.toString(this.fTestResult.failureCount()));
		appendFailure("Failure", test, t);
	}
	
	protected void addGrid(Panel p, Component co, int x, int y, int w, int fill, double wx, int anchor) {
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
	
	private void appendFailure(String kind, Test test, Throwable t) {
		kind+= ": " + test;
		String msg= t.getMessage();
		if (msg != null) {
			kind+= ":" + truncate(msg); 
		}
		this.fFailureList.add(kind);
		this.fExceptions.addElement(t);
		this.fFailedTests.addElement(test);
		if (this.fFailureList.getItemCount() == 1) {
			this.fFailureList.select(0);
			failureSelected();	
		}
	}
	/**
	 * Creates the JUnit menu. Clients override this
	 * method to add additional menu items.
	 */
	protected Menu createJUnitMenu() {
		Menu menu= new Menu("JUnit");
		MenuItem mi= new MenuItem("About...");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            about();
		        }
		    }
		);
		menu.add(mi);
		
		menu.addSeparator();
		mi= new MenuItem("Exit");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            System.exit(0);
		        }
		    }
		);
		menu.add(mi);
		return menu;
	}
	
	protected void createMenus(MenuBar mb) {
		mb.add(createJUnitMenu());
	}
	protected TestResult createTestResult() {
		return new TestResult();
	}
	
	protected Frame createUI(String suiteName) {	
		Frame frame= new Frame("JUnit");
		Image icon= loadFrameIcon();	
		if (icon != null)
			frame.setIconImage(icon);

		frame.setLayout(new BorderLayout(0, 0));
		frame.setBackground(SystemColor.control);
		final Frame finalFrame= frame;
		
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					finalFrame.dispose();
					System.exit(0);
				}
			}
		); 

		MenuBar mb = new MenuBar();
		createMenus(mb);
		frame.setMenuBar(mb);
		
		//---- first section
		Label suiteLabel= new Label("Test class name:");

		this.fSuiteField= new TextField(suiteName != null ? suiteName : "");
		this.fSuiteField.selectAll();
		this.fSuiteField.requestFocus();
		this.fSuiteField.setFont(PLAIN_FONT);
		this.fSuiteField.setColumns(40);
		this.fSuiteField.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSuite();
				}
			}
		);
		this.fSuiteField.addTextListener(
			new TextListener() {
				public void textValueChanged(TextEvent e) {
					TestRunner.this.fRun.setEnabled(TestRunner.this.fSuiteField.getText().length() > 0);
					TestRunner.this.fStatusLine.setText("");
				}
			}
		);
		this.fRun= new Button("Run");
		this.fRun.setEnabled(false);
		this.fRun.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSuite();
				}
			}
		);
		boolean useLoader= useReloadingTestSuiteLoader();
		this.fUseLoadingRunner= new Checkbox("Reload classes every run", useLoader);
		if (inVAJava())
			this.fUseLoadingRunner.setVisible(false);
			
		//---- second section
		this.fProgressIndicator= new ProgressBar();	

		//---- third section 
		this.fNumberOfErrors= new Label("0000", Label.RIGHT);
		this.fNumberOfErrors.setText("0");
		this.fNumberOfErrors.setFont(PLAIN_FONT);
	
		this.fNumberOfFailures= new Label("0000", Label.RIGHT);
		this.fNumberOfFailures.setText("0");
		this.fNumberOfFailures.setFont(PLAIN_FONT);
	
		this.fNumberOfRuns= new Label("0000", Label.RIGHT);
		this.fNumberOfRuns.setText("0");
		this.fNumberOfRuns.setFont(PLAIN_FONT);
	
		Panel numbersPanel= new Panel(new FlowLayout());
		numbersPanel.add(new Label("Runs:"));			numbersPanel.add(this.fNumberOfRuns);
		numbersPanel.add(new Label("   Errors:"));		numbersPanel.add(this.fNumberOfErrors);
		numbersPanel.add(new Label("   Failures:"));	numbersPanel.add(this.fNumberOfFailures);

	
		//---- fourth section
		Label failureLabel= new Label("Errors and Failures:");
		
		this.fFailureList= new List(5);
		this.fFailureList.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					failureSelected();
				}
			}
		);
		this.fRerunButton= new Button("Run");
		this.fRerunButton.setEnabled(false);
		this.fRerunButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rerun();
				}
			}
		);

		Panel failedPanel= new Panel(new GridLayout(0, 1, 0, 2));
		failedPanel.add(this.fRerunButton);
		
		this.fTraceArea= new TextArea();
		this.fTraceArea.setRows(5);
		this.fTraceArea.setColumns(60);

		//---- fifth section
		this.fStatusLine= new TextField();
		this.fStatusLine.setFont(PLAIN_FONT);
		this.fStatusLine.setEditable(false);
		this.fStatusLine.setForeground(Color.red);

		this.fQuitButton= new Button("Exit");
		this.fQuitButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);
	
		// ---------
		this.fLogo= new Logo();
	
		//---- overall layout
		Panel panel= new Panel(new GridBagLayout());
	
		addGrid(panel, suiteLabel,		 0, 0, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		
		addGrid(panel, this.fSuiteField, 	 0, 1, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fRun, 			 2, 1, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);
		addGrid(panel, this.fUseLoadingRunner, 0, 2, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fProgressIndicator, 0, 3, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fLogo, 			 2, 3, 1, GridBagConstraints.NONE, 			0.0, GridBagConstraints.NORTH);

		addGrid(panel, numbersPanel,	 0, 4, 2, GridBagConstraints.NONE, 			0.0, GridBagConstraints.CENTER);

		addGrid(panel, failureLabel, 	 0, 5, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, this.fFailureList, 	 0, 6, 2, GridBagConstraints.BOTH, 			1.0, GridBagConstraints.WEST);
		addGrid(panel, failedPanel, 	 2, 6, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);
		addGrid(panel, this.fTraceArea, 	     0, 7, 2, GridBagConstraints.BOTH, 			1.0, GridBagConstraints.WEST);
		
		addGrid(panel, this.fStatusLine, 	 0, 8, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.CENTER);
		addGrid(panel, this.fQuitButton, 	 2, 8, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		return frame;
	}
	
	public void failureSelected() {
		this.fRerunButton.setEnabled(isErrorSelected());
		showErrorTrace();
	}

	public void endTest(Test test) {
		setLabelValue(this.fNumberOfRuns, this.fTestResult.runCount());
		synchronized(this) {
			this.fProgressIndicator.step(this.fTestResult.wasSuccessful());
		}
	}
		
	private boolean isErrorSelected() {
		return this.fFailureList.getSelectedIndex() != -1;
	}
	
	private Image loadFrameIcon() {
		Toolkit toolkit= Toolkit.getDefaultToolkit();
		try {
			java.net.URL url= BaseTestRunner.class.getResource("smalllogo.gif");
			return toolkit.createImage((ImageProducer) url.getContent());
		} catch (Exception ex) {
		}
		return null;
	}
	
	public Thread getRunner() {
		return this.fRunner;
	}
	
	public static void main(String[] args) {
		new TestRunner().start(args);
	}
	 
	public static void run(Class test) {
		String args[]= { test.getName() };	
		main(args);
	}
	
	public void rerun() {
		int index= this.fFailureList.getSelectedIndex();
		if (index == -1)
			return;
	
		Test test= (Test)this.fFailedTests.elementAt(index);
		if (!(test instanceof TestCase)) {
			showInfo("Could not reload "+ test.toString());
			return;
		}
		Test reloadedTest= null;
		try {
			Class reloadedTestClass= getLoader().reload(test.getClass());
			Class[] classArgs= { String.class };
			Constructor constructor= reloadedTestClass.getConstructor(classArgs);
			Object[] args= new Object[]{((TestCase)test).getName()};
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
		setLabelValue(this.fNumberOfErrors, 0);
		setLabelValue(this.fNumberOfFailures, 0);
		setLabelValue(this.fNumberOfRuns, 0);
		this.fProgressIndicator.reset();
		this.fRerunButton.setEnabled(false);
		this.fFailureList.removeAll();
		this.fExceptions= new Vector(10);
		this.fFailedTests= new Vector(10);
		this.fTraceArea.setText("");

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
		this.fRun.setLabel("Run");
		this.fRunner= null;
	}
	
	synchronized public void runSuite() {
		if (this.fRunner != null) {
			this.fTestResult.stop();
		} else {
			setLoading(shouldReload());
			this.fRun.setLabel("Stop");
			showInfo("Initializing...");
			reset();
			
			showInfo("Load Test Case...");

			final Test testSuite= getTest(this.fSuiteField.getText());
			if (testSuite != null) {
				this.fRunner= new Thread() {
					public void run() {
						TestRunner.this.fTestResult= createTestResult();
						TestRunner.this.fTestResult.addListener(TestRunner.this);
						TestRunner.this.fProgressIndicator.start(testSuite.countTestCases());
						showInfo("Running...");
					
						long startTime= System.currentTimeMillis();
						testSuite.run(TestRunner.this.fTestResult);
						
						if (TestRunner.this.fTestResult.shouldStop()) {
							showStatus("Stopped");
						} else {
							long endTime= System.currentTimeMillis();
							long runTime= endTime-startTime;
							showInfo("Finished: " + elapsedTimeAsString(runTime) + " seconds");
						}
						TestRunner.this.fTestResult= null;
						TestRunner.this.fRun.setLabel("Run");
						TestRunner.this.fRunner= null;
						System.gc();
					}
				};
				this.fRunner.start();
			}
		}
	}
	
	private boolean shouldReload() {
		return !inVAJava() && this.fUseLoadingRunner.getState();
	}
	
	private void setLabelValue(Label label, int value) {
		label.setText(Integer.toString(value));
		label.invalidate();
		label.getParent().validate();

	}
	
	public void setSuiteName(String suite) {
		this.fSuiteField.setText(suite);
	}
	
	private void showErrorTrace() {
		int index= this.fFailureList.getSelectedIndex();
		if (index == -1)
			return;
	
		Throwable t= (Throwable) this.fExceptions.elementAt(index);
		this.fTraceArea.setText(getFilteredTrace(t));
	}
	

	private void showInfo(String message) {
		this.fStatusLine.setFont(PLAIN_FONT);
		this.fStatusLine.setForeground(Color.black);
		this.fStatusLine.setText(message);
	}
	
	protected void clearStatus() {
		showStatus("");
	}

	private void showStatus(String status) {
		this.fStatusLine.setFont(PLAIN_FONT);
		this.fStatusLine.setForeground(Color.red);
		this.fStatusLine.setText(status);
	}
	/**
	 * Starts the TestRunner
	 */
	public void start(String[] args) {
		String suiteName= processArguments(args);			
		this.fFrame= createUI(suiteName);
		this.fFrame.setLocation(200, 200);
		this.fFrame.setVisible(true);
	
		if (suiteName != null) {
			setSuiteName(suiteName);
			runSuite();
		}
	}
	
	public void startTest(Test test) {
		showInfo("Running: "+test);
	}
}