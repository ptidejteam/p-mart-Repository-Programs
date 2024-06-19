package junit.swingui;

import java.awt.Component;

import javax.swing.JTextArea;

import junit.framework.TestFailure;
import junit.runner.BaseTestRunner;
import junit.runner.FailureDetailView;

/**
 * A view that shows a stack trace of a failure
 */
class DefaultFailureDetailView implements FailureDetailView {
	JTextArea fTextArea;
	
	/**
	 * Returns the component used to present the trace
	 */
	public Component getComponent() {
		if (this.fTextArea == null) {
			this.fTextArea= new JTextArea();
			this.fTextArea.setRows(5);
			this.fTextArea.setTabSize(0);
			this.fTextArea.setEditable(false);
		}
		return this.fTextArea;
	}
	
	/**
	 * Shows a TestFailure
	 */
	public void showFailure(TestFailure failure) {
		this.fTextArea.setText(BaseTestRunner.getFilteredTrace(failure.thrownException()));
		this.fTextArea.select(0, 0);	
	}
	
	public void clear() {
		this.fTextArea.setText("");
	}
}