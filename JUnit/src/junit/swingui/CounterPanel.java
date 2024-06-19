package junit.swingui;

import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A panel with test run counters
 */
public class CounterPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField fNumberOfErrors;
	private JTextField fNumberOfFailures;
	private JTextField fNumberOfRuns;
	private int fTotal;
	
	public CounterPanel() {
		super(new GridLayout(2, 3));	
		add(new JLabel("Runs:"));		
		add(new JLabel("Errors:"));	
		add(new JLabel("Failures: "));	
		this.fNumberOfErrors= createOutputField();
		this.fNumberOfFailures= createOutputField();
		this.fNumberOfRuns= createOutputField();
		add(this.fNumberOfRuns);
		add(this.fNumberOfErrors);
		add(this.fNumberOfFailures);
	} 
	
	private JTextField createOutputField() {
		JTextField field= new JTextField("0", 4);
		field.setHorizontalAlignment(SwingConstants.LEFT);
		field.setFont(StatusLine.BOLD_FONT);
		field.setEditable(false);
		field.setBorder(BorderFactory.createEmptyBorder());
		return field;
	}
	
	public void reset() {
		setLabelValue(this.fNumberOfErrors, 0);
		setLabelValue(this.fNumberOfFailures, 0);
		setLabelValue(this.fNumberOfRuns, 0);
		this.fTotal= 0;
	}
	
	public void setTotal(int value) {
		this.fTotal= value;
	}
	
	public void setRunValue(int value) {
		this.fNumberOfRuns.setText(Integer.toString(value) + "/" + this.fTotal);
	}
	
	public void setErrorValue(int value) {
		setLabelValue(this.fNumberOfErrors, value);
	}
	
	public void setFailureValue(int value) {
		setLabelValue(this.fNumberOfFailures, value);
	}
	
	//	private String asString(int value) {
	//		return Integer.toString(value);
	//	}
	
	private void setLabelValue(JTextField label, int value) {
		label.setText(Integer.toString(value));
	}
}