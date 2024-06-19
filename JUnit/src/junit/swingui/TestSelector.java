package junit.swingui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import junit.runner.Sorter;
import junit.runner.TestCollector;

/**
 * A test class selector. A simple dialog to pick the name of a test suite.
 */
class TestSelector extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton fCancel;
	private JButton fOk;
	private JList fList;
	private JScrollPane fScrolledList;
	private JLabel fDescription;
	private String fSelectedItem;
	
	/**
	 * Renders TestFailures in a JList
	 */
	static class TestCellRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Icon fLeafIcon;
		Icon fSuiteIcon;
		
		public TestCellRenderer() {
			this.fLeafIcon= UIManager.getIcon("Tree.leafIcon");
			this.fSuiteIcon= UIManager.getIcon("Tree.closedIcon");
		}
		
		public Component getListCellRendererComponent(
				JList list, Object value, int modelIndex, 
				boolean isSelected, boolean cellHasFocus) {
			Component c= super.getListCellRendererComponent(list, value, modelIndex, isSelected, cellHasFocus);
			String displayString= displayString((String)value);
			
			if (displayString.startsWith("AllTests"))
				setIcon(this.fSuiteIcon);
			else
				setIcon(this.fLeafIcon);
				
			setText(displayString);
		    	return c;
		}
		
		public static String displayString(String className) {
			int typeIndex= className.lastIndexOf('.');
    			if (typeIndex < 0) 
    				return className;
    			return className.substring(typeIndex+1) + " - " + className.substring(0, typeIndex);
		}
		
		public static boolean matchesKey(String s, char ch) {
    			return ch == Character.toUpperCase(s.charAt(typeIndex(s)));
		}
		
		private static int typeIndex(String s) {
			int typeIndex= s.lastIndexOf('.');
			int i= 0;
    			if (typeIndex > 0) 
    				i= typeIndex+1;
    			return i;
		}
	}
	
	protected class DoubleClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
	    		if (e.getClickCount() == 2) {
	    			okSelected();
	    		}
	      }
	}
	
	protected class KeySelectListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			keySelectTestClass(e.getKeyChar());
		}
	}

	public TestSelector(Frame parent, TestCollector testCollector) {
		super(parent, true);
		setSize(350, 300);
		setResizable(false);
		setLocationRelativeTo(parent);
		setTitle("Test Selector");
		
		Vector list= null;
		try {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			list= createTestList(testCollector);
		} finally {
			parent.setCursor(Cursor.getDefaultCursor());
		}
		this.fList= new JList(list);
		this.fList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fList.setCellRenderer(new TestCellRenderer());
		this.fScrolledList= new JScrollPane(this.fList);

		this.fCancel= new JButton("Cancel");
		this.fDescription= new JLabel("Select the Test class:");
		this.fOk= new JButton("OK");
		this.fOk.setEnabled(false);
		getRootPane().setDefaultButton(this.fOk);
		
		defineLayout();
		addListeners();
	}
	
	private void addListeners() {
		this.fCancel.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
		
		this.fOk.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okSelected();
				}
			}
		);

		this.fList.addMouseListener(new DoubleClickListener());
		this.fList.addKeyListener(new KeySelectListener());
		this.fList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					checkEnableOK(e);
				}
			}
		);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	}
	
	private void defineLayout() {
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx= 0; labelConstraints.gridy= 0;
		labelConstraints.gridwidth= 1; labelConstraints.gridheight= 1;
		labelConstraints.fill= GridBagConstraints.BOTH;
		labelConstraints.anchor= GridBagConstraints.WEST;
		labelConstraints.weightx= 1.0;
		labelConstraints.weighty= 0.0;
		labelConstraints.insets= new Insets(8, 8, 0, 8);
		getContentPane().add(this.fDescription, labelConstraints);

		GridBagConstraints listConstraints = new GridBagConstraints();
		listConstraints.gridx= 0; listConstraints.gridy= 1;
		listConstraints.gridwidth= 4; listConstraints.gridheight= 1;
		listConstraints.fill= GridBagConstraints.BOTH;
		listConstraints.anchor= GridBagConstraints.CENTER;
		listConstraints.weightx= 1.0;
		listConstraints.weighty= 1.0;
		listConstraints.insets= new Insets(8, 8, 8, 8);
		getContentPane().add(this.fScrolledList, listConstraints);
		
		GridBagConstraints okConstraints= new GridBagConstraints();
		okConstraints.gridx= 2; okConstraints.gridy= 2;
		okConstraints.gridwidth= 1; okConstraints.gridheight= 1;
		okConstraints.anchor= java.awt.GridBagConstraints.EAST;
		okConstraints.insets= new Insets(0, 8, 8, 8);
		getContentPane().add(this.fOk, okConstraints);


		GridBagConstraints cancelConstraints = new GridBagConstraints();
		cancelConstraints.gridx= 3; cancelConstraints.gridy= 2;
		cancelConstraints.gridwidth= 1; cancelConstraints.gridheight= 1;
		cancelConstraints.anchor= java.awt.GridBagConstraints.EAST;
		cancelConstraints.insets= new Insets(0, 8, 8, 8);
		getContentPane().add(this.fCancel, cancelConstraints);
	}
	
	public void checkEnableOK(ListSelectionEvent e) {
		this.fOk.setEnabled(this.fList.getSelectedIndex() != -1);
	}
	
	public void okSelected() {
		this.fSelectedItem= (String)this.fList.getSelectedValue();
		dispose();
	}
	
	public boolean isEmpty() {
		return this.fList.getModel().getSize() == 0;
	}
	
	public void keySelectTestClass(char ch) {
		ListModel model= this.fList.getModel();
		if (!Character.isJavaIdentifierStart(ch))
			return;
		for (int i= 0; i < model.getSize(); i++) {
			String s= (String)model.getElementAt(i);
			if (TestCellRenderer.matchesKey(s, Character.toUpperCase(ch))) {
				this.fList.setSelectedIndex(i);
				this.fList.ensureIndexIsVisible(i);
				return;
			}
		}
		Toolkit.getDefaultToolkit().beep();
	}
	
	public String getSelectedItem() {
		return this.fSelectedItem;
	}

	private Vector createTestList(TestCollector collector) {
    		Enumeration each= collector.collectTests();
    		Vector v= new Vector(200);
    		Vector displayVector= new Vector(v.size());
    		while(each.hasMoreElements()) {
    			String s= (String)each.nextElement();
    			v.addElement(s);
    			displayVector.addElement(TestCellRenderer.displayString(s));
    		}
    		if (v.size() > 0)
    			Sorter.sortStrings(displayVector, 0, displayVector.size()-1, new ParallelSwapper(v));
    		return v;
	}
	
	private class ParallelSwapper implements Sorter.Swapper {
		Vector fOther;
		
		ParallelSwapper(Vector other) {
			this.fOther= other;
		}
		public void swap(Vector values, int left, int right) {
			Object tmp= values.elementAt(left); 
			values.setElementAt(values.elementAt(right), left); 
			values.setElementAt(tmp, right);
			Object tmp2= this.fOther.elementAt(left);
			this.fOther.setElementAt(this.fOther.elementAt(right), left);
			this.fOther.setElementAt(tmp2, right);
		}			
	}
}