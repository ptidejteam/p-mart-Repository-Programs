/*
 *  NotesTreeNode.java - 'node' for the notes tree in the Notes Plugin for GMGen
 *  Copyright (C) 2003 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */
package plugin.notes.gui;

import gmgen.GMGenSystem;
import gmgen.gui.ExtendedHTMLDocument;
import gmgen.gui.ExtendedHTMLEditorKit;
import gmgen.util.MiscUtilities;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLWriter;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import pcgen.util.Logging;

/**
 *  This defines the preferences tree
 *
 *@author     devon
 *@created    August 27, 2003
 */

public class NotesTreeNode implements MutableTreeNode, DocumentListener {
	/**  directory this NotesTreeNode represents */
	protected File dir;
	/**  Counter used to detemine if this node needs to be flushed of it's data */
	protected int cacheCounter = 0;
	/**  is this node dirty (has notesDoc been edited, but is unsaved) */
	protected boolean dirty = false;
	/**
	 *  setDocument causes an event to fire, which makes the document dirty This
	 *  semaphore prevents that This is only used if we are not cacheing the
	 *  JTextPane
	 */
	protected boolean ignoreUpdateSemaphore = false;
	/**  Cache of the pane that is displaed when this node is clicked */
	protected JTextPane pane;
	/**  Document to be displayed when this node is selected */
	protected ExtendedHTMLDocument notesDoc;
	/**
	 *  Parent tree that this node is on used for updating the tree when certain
	 *  changes happen
	 */
	protected JTree tree;
	/**  Flad to detemine if this node has had it's children populated */
	protected boolean hasBeenPopulated = false;

	/**
	 *  An enumeration that is always empty. This is used when an enumeration of a
	 *  leaf node's children is requested.
	 */
	public static final Enumeration EMPTY_ENUMERATION =
		new Enumeration() {
			public boolean hasMoreElements() {
				return false;
			}


			public Object nextElement() {
				throw new NoSuchElementException("No more elements");
			}
		};

	/**  this node's parent, or null if this node has no parent */
	protected MutableTreeNode parent;

	/**  array of children, may be null if this node has no children */
	protected Vector children;

	/**  optional user object */
	protected transient Object userObject;

	/**  true if the node is able to have children */
	protected boolean allowsChildren = true;


	/**
	 *  Constructor for the NotesTreeNode object
	 *
	 *@param  name  Name of the node
	 *@param  dir   Directory this node represents
	 *@param  tree  tree the node will live in
	 */
	public NotesTreeNode(String name, File dir, JTree tree) {
		userObject = name;
		this.tree = tree;
		this.dir = dir;
	}


	/**
	 *  Check to seeif this node or any of it's children are dirty. If they are,
	 *  ask if the user wants to save them, and then do so.
	 */
	public void checkSave() {
		if(((String)userObject).equals("Logs")) {
			if(isTreeDirty()) {
				int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "You have unsaved Logs.  Save?", "Save", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					save();
					saveChildren();
				}
			}
			trimEmpty();
		}
		else {
			if (dirty) {
				int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "Note '" + getUserObject() +
						"' changed.  Save?", "Save", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					save();
				}
			}
			if (hasBeenPopulated) {
				Enumeration newNodes = children();
				for (; newNodes.hasMoreElements(); ) {
					NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
					node.checkSave();
				}
			}
		}
	}

	private void trimEmpty() {
		Enumeration newNodes = children();
		for (; newNodes.hasMoreElements(); ) {
			NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
			node.trimEmpty();
			if(node.isEmpty()) {
				node.delete();
			}
		}
	}

	/**
	 *  Check to see if this object's cache should be cleared. if it should,
	 *  revert. Then check all children
	 */
	public void checkCache() {
		if (!dirty) {
			if (pane != null) {
				if (cacheCounter > 0) {
					cacheCounter--;
				} else {
					revert();
				}
			}
		}

		if (hasBeenPopulated) {
			Enumeration newNodes = children();
			for (; newNodes.hasMoreElements(); ) {
				NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
				node.checkCache();
			}
		}
	}


	/**
	 *  Create a new child named newName (n), and create it's directory.
	 *
	 *@param  newName  name to attempt to create - if it exists, (n) will be
	 *      appended where n = the number of existing directories with the same
	 *      name
	 */
	public NotesTreeNode createChild(String newName) {
		boolean notDone = true;

		newName = checkName(newName);

		int num = 1;
		if (!hasBeenPopulated) {
			populate();
		}
		while (notDone) {
			if (num > 1) {
				newName = newName += " (" + num + ")";
			}
			File newDir = new File(dir.getAbsolutePath() + File.separator + newName);
			if (!newDir.exists()) {
				try {
					newDir.mkdir();
					notDone = false;
					NotesTreeNode newNode = new NotesTreeNode(newName, newDir, tree);
					add(newNode);
					return newNode;
				} catch (Exception e) {
					Logging.errorPrint(e.getMessage(), e);
					return null;
				}
			}
			num++;
		}
		return null;
	}

	public static String checkName(String name) {
		name = name.replaceAll("\\:", "-");
		name = name.replaceAll("\\;", "-");
		name = name.replaceAll("\\+", "-");
		name = name.replaceAll("\\=", "-");
		name = name.replaceAll("\\|", "-");
		name = name.replaceAll("\\?", "-");
		name = name.replaceAll("\\*", "-");
		return name;
	}


	/**  Create a new child named "New Node (n)", and create it's directory. */
	public NotesTreeNode createChild() {
		return createChild("New Note");
	}


	/**
	 *  Try to delete this node. If it has children, or content, throw a dialog to
	 *  let the user block this deletion. If any files or directories cannot be
	 *  deleted, let the use know.
	 */
	public void delete() {
		File[] children = dir.listFiles();
		if(!isEmpty()) {
			int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "Node " + dir.getName() +
					" Contains Content.  Delete?", "Node Populated", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) {
				return;
			}
		}

		try {
			for (int i = 0; i < children.length; i++) {
				boolean test = children[i].delete();
				if (!test) {
					JOptionPane.showMessageDialog(null, "Cannot delete file " + children[i].getName());
					break;
				}
			}
			boolean test = dir.delete();
			if (test) {
				removeFromParent();
			} else {
				JOptionPane.showMessageDialog(null, "Cannot delete directory " + dir.getName());
			}
		} catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
		}
	}


	/**
	 *  Gets the directory that this object represents
	 *
	 *@return    The dir
	 */
	public File getDir() {
		return dir;
	}


	/**
	 *  Gets the files that are in the directory this object represents (but not
	 *  directories
	 *
	 *@return    A List of File objects
	 */
	public List getFiles() {
		ArrayList list = new ArrayList();
		File[] children = dir.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (!children[i].isDirectory()) {
				if (!children[i].getName().equals("data.html")) {
					list.add(children[i]);
				}
			}
		}
		return list;
	}


	/**
	 *  Gets a JTextPane that contains the content of the "data.html" in this
	 *  directory (or the nodified document if it has been modified), or is empty
	 *  if that file does not exist. This function takes in an external JTextPan
	 *  that it populates in excrutiatingly slow speed.
	 *
	 *@param  editor  Editor pane you want to populate
	 *@return         The populated Pane
	 */
	public JTextPane getTextPane(JTextPane editor) {
		boolean repopulate = false;
		cacheCounter = 10;
		if (notesDoc != null) {
			//setDocument causes an event to fire, which makes the document dirty - these semaphores prevent that
			ignoreUpdateSemaphore = true;
		}
		if (pane == null) {
			pane = editor;
			repopulate = true;
			ExtendedHTMLEditorKit htmlKit = new ExtendedHTMLEditorKit();
			pane.setEditorKit(htmlKit);
			notesDoc = (ExtendedHTMLDocument) (htmlKit.createDefaultDocument());
			notesDoc.putProperty("docroot", dir.getAbsolutePath() + File.separator + "data.html");
		}

		pane.setDocument(notesDoc);

		if (repopulate) {
			File notes = new File(dir.getAbsolutePath() + File.separator + "data.html");
			if (notes.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(notes));
					StringBuffer sb = new StringBuffer();
					String newLine = "";
					while (newLine != null) {
						newLine = br.readLine();
						if (newLine != null) {
							sb.append(newLine + "\n");
						}
					}
					br.close();
					pane.setText(sb.toString());
				} catch (Exception e) {
					Logging.errorPrint(e.getMessage(), e);
				}
			}
			notesDoc.addDocumentListener(this);
		}
		return pane;
	}


	/**
	 *  Gets a JTextPane that contains the content of the "data.html" in this
	 *  directory (or the nodified document if it has been modified), or is empty
	 *  if that file does not exist. This funciton caches the JTextPan so that the
	 *  speed doesn't suck.
	 *
	 *@return    The populated JTextPane
	 */
	public JTextPane getTextPane() {
		boolean repopulate = false;
		cacheCounter = 10;
		if (pane == null) {
			pane = new JTextPane();
			repopulate = true;
			ExtendedHTMLEditorKit htmlKit = new ExtendedHTMLEditorKit();
			pane.setEditorKit(htmlKit);
			notesDoc = (ExtendedHTMLDocument) (htmlKit.createDefaultDocument());
			notesDoc.putProperty("docroot", dir.getAbsolutePath() + File.separator + "data.html");
		}

		pane.setDocument(notesDoc);

		if (repopulate) {
			File notes = new File(dir.getAbsolutePath() + File.separator + "data.html");
			if (notes.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(notes));
					StringBuffer sb = new StringBuffer();
					String newLine = "";
					while (newLine != null) {
						newLine = br.readLine();
						if (newLine != null) {
							sb.append(newLine + "\n");
						}
					}
					br.close();
					pane.setText(sb.toString());
				} catch (Exception e) {
					Logging.errorPrint(e.getMessage(), e);
				}
			}
			pane.setCaretPosition(0);
			notesDoc.addDocumentListener(this);
		}
		return pane;
	}


	/**
	 *  Determines if the 'data.html' for this dir has been modified, but is not
	 *  saved.
	 *
	 *@return    boolean of the dirty state
	 */
	public boolean isDirty() {
		return dirty;
	}


	/**
	 *  Determines if any of this node's children are dirty
	 *
	 *@return    true if even a single node is dirty, false otherwise.
	 */
	public boolean isTreeDirty() {
		if (dirty) {
			return true;
		} else {
			if (hasBeenPopulated) {
				Enumeration newNodes = children();
				for (; newNodes.hasMoreElements(); ) {
					NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
					if (node.isTreeDirty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isEmpty() {
		File[] children = dir.listFiles();
		if (children.length > 0 || dirty) {
			return false;
		}
		return true;
	}


	public void appendText(String text) {
		try {
			if(notesDoc == null) {
				getTextPane();
			}
			notesDoc.insertAfterEnd(notesDoc.getCharacterElement(notesDoc.getLength()), text);
		}
		catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
		}
	}


	/**  Refereshs the tree to take into account any added/removed directories */
	public void refresh() {
		// TODO: This function seems to not always generate the proper results. Sometimes duplicating nodes.
		if (hasBeenPopulated) {
			Enumeration childNodes = children();
			List childDirs = Arrays.asList(dir.listFiles());
			List removeDirs = new ArrayList();
			for (; childNodes.hasMoreElements(); ) {
				NotesTreeNode node = (NotesTreeNode) childNodes.nextElement();
				File nodeDir = node.getDir();
				if (nodeDir.exists()) {
					for (int i = 0; i < childDirs.size(); i++) {
						File childDir = (File) childDirs.get(i);
						if (nodeDir.getName().equals(childDir.getName())) {
							removeDirs.add(childDir);
							continue;
						}
					}
				} else {
					remove(node);
				}
			}

			for (int i = 0; i < childDirs.size(); i++) {
				File childDir = (File) childDirs.get(i);
				if (!removeDirs.contains(childDir)) {
					if (childDir.isDirectory()) {
						add(new NotesTreeNode(childDir.getName(), childDir, tree));
					}
				}
			}
			Enumeration newNodes = children();
			for (; newNodes.hasMoreElements(); ) {
				NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
				node.refresh();
			}
		}
	}


	/**
	 *  As we rename a directory, we need to re-home all of it's children to the
	 *  new directory. This function replaces the Directory of this object, then
	 *  re-homs all of it's children.
	 *
	 *@param  path  New path to move the children to.
	 */
	public void rehome(String path) {
		// TODO: Children cease being editable after a rehome, fix this.
		dir = new File(path + File.separator + dir.getName());
		rehomeChildren(dir.getAbsolutePath());
		notesDoc.putProperty("docroot", dir.getAbsolutePath() + File.separator + "data.html");
	}


	/**
	 *  Rehomes the children
	 *
	 *@param  path  New path for the child
	 */
	public void rehomeChildren(String path) {
		if (hasBeenPopulated) {
			Enumeration childNodes = children();
			for (; childNodes.hasMoreElements(); ) {
				NotesTreeNode node = (NotesTreeNode) childNodes.nextElement();
				node.rehome(path);
			}
		}
	}


	/**
	 *  This Renames the object, and it's ritectory, and then re-homes all of the
	 *  children.
	 *
	 *@param  newName                  New name for the node
	 *@exception  java.io.IOException  thrown if the renaming can't take place
	 *      because of an IO error
	 */
	public void rename(String newName) throws java.io.IOException {
		String path = dir.getParent();
		if (dir.exists()) {
			String oldPath = dir.getAbsolutePath();
			String oldName = dir.getName();
			boolean tryrename = dir.renameTo(new File(path + File.separator + newName));
			if (!tryrename) {
				dir = new File(oldPath);
				setUserObject(oldName);
			} else {
				dir = new File(path + File.separator + newName);
				rehomeChildren(dir.getAbsolutePath());
			}
		} else {
			dir = new File(path + File.separator + newName);
			dir.mkdirs();
		}
	}


	/**  reverts back to the saved file (and as a consequence, clears the cache) */
	public void revert() {
		//If it is modified, confirm
		if (dirty) {
			int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "Note '" + getUserObject() + "' has been altered, are you sure you wish to revert to the saved copy?", "Revert?", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.NO_OPTION) {
				return;
			}
		}
		dirty = false;
		pane = null;
		notesDoc.removeDocumentListener(this);
		notesDoc = null;
	}


	/**  Saves this node's data. */
	public void save() {
		if (dirty) {
			try {
				File notes = new File(dir.getAbsolutePath() + File.separator + "data.html");
				if (!notes.exists()) {
					notes.createNewFile();
				}
				if (pane != null) {
					FileWriter fw = new FileWriter(notes);
					HTMLWriter hw = new HTMLWriter(fw, notesDoc);
					hw.write();
					fw.flush();
					fw.close();
					dirty = false;
				}
			} catch (Exception e) {
				Logging.errorPrint(e.getMessage(), e);
			}
		}
	}


	public void saveChildren() {
		Enumeration newNodes = children();
		for (; newNodes.hasMoreElements(); ) {
			NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();
			node.save();
			node.saveChildren();
		}
	}

	//Listener functions
	/**
	 *  handles a drop of a java file list
	 *
	 *@param  dtde  drop target drop even - a java dile list has been dropped on
	 *      something that represents this node.
	 *@return       returns true if the drop takes place, false if not
	 */
	public boolean handleDropJavaFileList(DropTargetDropEvent dtde) {
		dtde.acceptDrop(dtde.getDropAction());
		Transferable t = dtde.getTransferable();
		try {
			List fileList = ((List) t.getTransferData(DataFlavor.javaFileListFlavor));
			for (int i = 0; i < fileList.size(); i++) {
				File newFile = (File) fileList.get(i);
				if (newFile.exists()) {
					MiscUtilities.copy(newFile, new File(dir.getAbsolutePath() + File.separator + newFile.getName()));
				}
			}
		} catch (Exception e) {
			Logging.errorPrint(e.getMessage(), e);
			return false;
		}
		return true;
	}


	/**
	 *  This listener method is intended to listen to the notesDoc. A change event
	 *  has occured, so mark the document as dirty.
	 *
	 *@param  e  a Document Event
	 */
	public void changedUpdate(DocumentEvent e) {
		if (ignoreUpdateSemaphore) {
			ignoreUpdateSemaphore = false;
		} else {
			dirty = true;
			tree.updateUI();
		}
	}


	/**
	 *  This listener method is intended to listen to the notesDoc. An incert event
	 *  has occured, so mark the document as dirty.
	 *
	 *@param  e  a Document Event
	 */
	public void insertUpdate(DocumentEvent e) {
		dirty = true;
		tree.updateUI();
	}


	/**
	 *  This listener method is intended to listen to the notesDoc. A remove event
	 *  has occured, so mark the document as dirty.
	 *
	 *@param  e  a Document Event
	 */
	public void removeUpdate(DocumentEvent e) {
		dirty = true;
		tree.updateUI();
	}


	//Other functions

	/**
	 *  this method is called to print the name of the node in the tree
	 *
	 *@return    the name of the node (with a * if it is dirty)
	 */
	public String toString() {
		if (dirty) {
			return "* " + getUserObject().toString();
		} else {
			return getUserObject().toString();
		}
	}


	private void populate() {
		hasBeenPopulated = true;
		File[] children = dir.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isDirectory() && !children[i].getName().equals("CVS")) {
				add(new NotesTreeNode(children[i].getName(), children[i], tree));
			}
		}
	}


	//MutableTreeNode Methods
	/**
	 *  Gets the childCount attribute of the NotesTreeNode object
	 *
	 *@return    The childCount value
	 */
	public int getChildCount() {
		int counter = 0;
		if (children != null) {
			counter = children.size();
		}
		if (!hasBeenPopulated) {
			File[] kids = dir.listFiles();
			for (int i = 0; i < kids.length; i++) {
				if (kids[i].isDirectory() && !kids[i].getName().equals("CVS")) {
					counter++;
				}
			}
		}
		return counter;
	}


	/**
	 *  Returns an enumeration of this node's children
	 *
	 *@return    Enumeration containing MutableTreeNodes
	 */
	public Enumeration children() {
		if (!hasBeenPopulated) {
			populate();
		}
		if (children == null) {
			return EMPTY_ENUMERATION;
		} else {
			return children.elements();
		}
	}


	/**  Removes all child nodes */
	public void removeAllChildren() {
		for (int i = children.size() - 1; i >= 0; i--) {
			remove(i);
		}
	}


	/**
	 *  Gets the allowsChildren attribute of the NotesTreeNode object
	 *
	 *@return    The allowsChildren value
	 */
	public boolean getAllowsChildren() {
		return allowsChildren;
	}


	/**
	 *  Gets the child At a certain index of the NotesTreeNode object
	 *
	 *@param  index  index to get the child from
	 *@return        The child At value
	 */
	public TreeNode getChildAt(int index) {
		if (!hasBeenPopulated) {
			populate();
		}
		if (children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		return (TreeNode) children.elementAt(index);
	}


	/**
	 *  Gets the index of a particular TreeNode
	 *
	 *@param  node  Node to get the index of
	 *@return       The index value
	 */
	public int getIndex(TreeNode node) {
		if (node == null) {
			throw new IllegalArgumentException("argument is null");
		}
		if (!hasBeenPopulated) {
			populate();
		}

		if (!isNodeChild(node)) {
			return -1;
		}
		return children.indexOf(node);
		// linear search
	}


	/**
	 *  Gets the parent TreeNode of the NotesTreeNode object
	 *
	 *@return    The parent value
	 */
	public TreeNode getParent() {
		return parent;
	}


	/**
	 *  determins if this node is aleaf or a branch
	 *
	 *@return    The leaf value
	 */
	public boolean isLeaf() {
		return (getChildCount() == 0);
	}


	/**
	 *  Inserts a new MutableTreeNode into this node as a child.
	 *
	 *@param  child  Child to insert
	 *@param  index  Location to insert it.
	 */
	public void insert(MutableTreeNode child, int index) {
		if (!allowsChildren) {
			throw new IllegalStateException("node does not allow children");
		} else if (child == null) {
			throw new IllegalArgumentException("new child is null");
		} else if (isNodeAncestor(child)) {
			throw new IllegalArgumentException("new child is an ancestor");
		}
		if (!hasBeenPopulated) {
			populate();
		}

		MutableTreeNode oldParent = (MutableTreeNode) child.getParent();

		if (oldParent != null) {
			oldParent.remove(child);
		}
		child.setParent(this);
		if (children == null) {
			children = new Vector();
		}
		children.insertElementAt(child, index);
	}


	/**
	 *  removes the child node at index
	 *
	 *@param  index  index of child to remove
	 */
	public void remove(int index) {
		if (!hasBeenPopulated) {
			populate();
		}
		MutableTreeNode child = (MutableTreeNode) getChildAt(index);
		children.removeElementAt(index);
		child.setParent(null);
	}


	/**
	 *  removes the passed in MutableTreeNode
	 *
	 *@param  node  node to remove
	 */
	public void remove(MutableTreeNode node) {
		if (node == null) {
			throw new IllegalArgumentException("argument is null");
		}

		if (!isNodeChild(node)) {
			throw new IllegalArgumentException("argument is not a child");
		}
		if (!hasBeenPopulated) {
			populate();
		}
		remove(getIndex(node));
		// linear search
	}


	/**  Removes this node from it's parent */
	public void removeFromParent() {
		MutableTreeNode parent = (MutableTreeNode) getParent();
		if (parent != null) {
			parent.remove(this);
		}
	}


	/**
	 *  Sets the node of this object.
	 *
	 *@param  newParent  The new parent value
	 */
	public void setParent(MutableTreeNode newParent) {
		parent = newParent;
	}


	/**
	 *  Sets the userObject attribute of the NotesTreeNode object
	 *
	 *@param  object  The new userObject value
	 */
	public void setUserObject(Object object) {
		this.userObject = object;
	}


	//DefaultMutableTreeNode Methods we like ;)
	/**
	 *  Gets the userObject attribute of the NotesTreeNode object
	 *
	 *@return    The userObject value
	 */
	public Object getUserObject() {
		return userObject;
	}


	/**
	 *  Gets the nodeChild attribute of the NotesTreeNode object
	 *
	 *@param  node  Description of the Parameter
	 *@return       The nodeChild value
	 */
	public boolean isNodeChild(TreeNode node) {
		boolean retval;

		if (node == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (node.getParent() == this);
			}
		}
		return retval;
	}


	/**
	 *  Gets the nodeAncestor attribute of the NotesTreeNode object
	 *
	 *@param  node  Description of the Parameter
	 *@return       The nodeAncestor value
	 */
	public boolean isNodeAncestor(TreeNode node) {
		if (node == null) {
			return false;
		}

		TreeNode ancestor = this;

		do {
			if (ancestor == node) {
				return true;
			}
		} while ((ancestor = ancestor.getParent()) != null);
		return false;
	}


	/**
	 *  adds a MutableTreeNode
	 *
	 *@param  node  Node to add
	 */
	public void add(MutableTreeNode node) {
		if (node != null && node.getParent() == this) {
			insert(node, getChildCount() - 1);
		} else {
			insert(node, getChildCount());
		}
	}

}

