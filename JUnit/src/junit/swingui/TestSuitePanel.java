package junit.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * A Panel showing a test suite as a tree. 
 */
class TestSuitePanel extends JPanel implements TestListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree fTree;
	private JScrollPane fScrollTree;
	private TestTreeModel fModel;

	static class TestTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Icon fErrorIcon;
		private Icon fOkIcon;
		private Icon fFailureIcon;
		
		TestTreeCellRenderer() {
	    		super();
	    		loadIcons();
		}
		
		void loadIcons() {
			this.fErrorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");
	    		this.fOkIcon= TestRunner.getIconResource(getClass(), "icons/ok.gif");
	    		this.fFailureIcon= TestRunner.getIconResource(getClass(), "icons/failure.gif");
		}
		
		String stripParenthesis(Object o) {
			String text= o.toString ();
    			int pos= text.indexOf('(');
    			if (pos < 1)
    				return text;
    			return text.substring (0, pos);
  		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			
			Component c= super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	  		TreeModel model= tree.getModel();
	  		if (model instanceof TestTreeModel) {	
				TestTreeModel testModel= (TestTreeModel)model;
				Test t= (Test)value;
				String s= "";
	    		if (testModel.isFailure(t)) {
	    			if (this.fFailureIcon != null)
	    				setIcon(this.fFailureIcon);
	    			s= " - Failed";
	    		}
	    		else if (testModel.isError(t)) {
	    			if (this.fErrorIcon != null)
	    				setIcon(this.fErrorIcon);
	    			s= " - Error";
	    		}
	    		else if (testModel.wasRun(t)) {
	    			if (this.fOkIcon != null)
	    				setIcon(this.fOkIcon);
	    			s= " - Passed";
	    		}
	    		if (c instanceof JComponent)
	    			((JComponent)c).setToolTipText(getText()+s);
	  		}
	  		setText(stripParenthesis(value));
			return c;
		}
	}
	
	public TestSuitePanel() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(300, 100));
		this.fTree= new JTree();
		this.fTree.setModel(null);
		this.fTree.setRowHeight(20);
		ToolTipManager.sharedInstance().registerComponent(this.fTree);	
		this.fTree.putClientProperty("JTree.lineStyle", "Angled");
		this.fScrollTree= new JScrollPane(this.fTree);
		add(this.fScrollTree, BorderLayout.CENTER);
	}
		
	public void addError(final Test test, final Throwable t) {
 		this.fModel.addError(test);
		fireTestChanged(test, true);
	}
	
	public void addFailure(final Test test, final AssertionFailedError t) {
 		this.fModel.addFailure(test);
		fireTestChanged(test, true);
	}
	
	/**
 	 * A test ended.
 	 */
 	public void endTest(Test test) {
		this.fModel.addRunTest(test);
	 	fireTestChanged(test, false);
 	}

	/**
 	 * A test started.
 	 */
 	public void startTest(Test test) {
 	} 	  
 			
	/**
	 * Returns the selected test or null if multiple or none is selected
	 */
	public Test getSelectedTest() {
		TreePath[] paths= this.fTree.getSelectionPaths();
		if (paths != null && paths.length == 1)
			return (Test)paths[0].getLastPathComponent();
		return null;
	}

	/**
	 * Returns the Tree
	 */
	public JTree getTree() {
		return this.fTree;
	}

	/**
	 * Shows the test hierarchy starting at the given test
	 */
	public void showTestTree(Test root) {
		this.fModel= new TestTreeModel(root);
		this.fTree.setModel(this.fModel);
		this.fTree.setCellRenderer(new TestTreeCellRenderer());
	}
	
	private void fireTestChanged(final Test test, final boolean expand) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					Vector vpath= new Vector();
					int index= TestSuitePanel.this.fModel.findTest(test, (Test)TestSuitePanel.this.fModel.getRoot(), vpath);
					if (index >= 0) {
						Object[] path= new Object[vpath.size()];
						vpath.copyInto(path);
						TreePath treePath= new TreePath(path);
						TestSuitePanel.this.fModel.fireNodeChanged(treePath, index);
						if (expand) {
							Object[] fullPath= new Object[vpath.size()+1];
							vpath.copyInto(fullPath);
							fullPath[vpath.size()]= TestSuitePanel.this.fModel.getChild(treePath.getLastPathComponent(), index);;
							TreePath fullTreePath= new TreePath(fullPath);
							TestSuitePanel.this.fTree.scrollPathToVisible(fullTreePath);
						}
					}
				}
			}
		);
	}
}