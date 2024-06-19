package junit.swingui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;

/**
 * A view presenting the test failures as a list.
 */
class FailureRunView implements TestRunView {
	JList fFailureList;
	TestRunContext fRunContext;
	
	/**
	 * Renders TestFailures in a JList
	 */
	static class FailureListCellRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Icon fFailureIcon;
		private Icon fErrorIcon;
		
		FailureListCellRenderer() {
	    		super();
	    		loadIcons();
		}
	
		void loadIcons() {
			this.fFailureIcon= TestRunner.getIconResource(getClass(), "icons/failure.gif");
			this.fErrorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");		
		}
						
		public Component getListCellRendererComponent(
			JList list, Object value, int modelIndex, 
			boolean isSelected, boolean cellHasFocus) {
	
			TestFailure failure= (TestFailure)value;
			String text= failure.failedTest().toString();
			String msg= failure.thrownException().getMessage();
			if (msg != null) 
				text+= ":" + BaseTestRunner.truncate(msg); 
	 
			if (failure.thrownException() instanceof AssertionFailedError) { 
				if (this.fFailureIcon != null)
		    			setIcon(this.fFailureIcon);
			} else {
		    		if (this.fErrorIcon != null)
		    			setIcon(this.fErrorIcon);
		    	}
		    	Component c= super.getListCellRendererComponent(list, text, modelIndex, isSelected, cellHasFocus);
			setText(text);
			setToolTipText(text);
			return c;
		}
	}
	
	public FailureRunView(TestRunContext context) {
		this.fRunContext= context;
		this.fFailureList= new JList(this.fRunContext.getFailures());
		this.fFailureList.setPrototypeCellValue(
			new TestFailure(new TestCase("dummy") {
				protected void runTest() {}
			}, 
			new AssertionFailedError("message"))
		);	
		this.fFailureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fFailureList.setCellRenderer(new FailureListCellRenderer());
		this.fFailureList.setToolTipText("Failure - grey X; Error - red X");
		this.fFailureList.setVisibleRowCount(5);

		this.fFailureList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					testSelected();
				}
			}
		);
	}
	
	public Test getSelectedTest() {
		int index= this.fFailureList.getSelectedIndex();
		if (index == -1)
			return null;
			
		ListModel model= this.fFailureList.getModel();
		TestFailure failure= (TestFailure)model.getElementAt(index);
		return failure.failedTest();
	}
	
	public void activate() {
		testSelected();
	}
	
	public void addTab(JTabbedPane pane) {
		JScrollPane sl= new JScrollPane(this.fFailureList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		Icon errorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");
		pane.addTab("Failures", errorIcon, sl, "The list of failed tests");
	}
		
	public void revealFailure(Test failure) {
		this.fFailureList.setSelectedIndex(0);
	}
	
	public void aboutToStart(Test suite, TestResult result) {
	}

	public void runFinished(Test suite, TestResult result) {
	}

	protected void testSelected() {
		this.fRunContext.handleTestSelected(getSelectedTest());
	}
}


