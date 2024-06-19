/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.gui.compo.db.tree;

import com.sdi.pws.codec.Codec;
import com.sdi.pws.gui.compo.db.change.*;
import com.sdi.pws.db.ModelException;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.db.PwsField;
import com.sdi.pws.db.PwsRecord;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.*;

public class TreeViewDatabase
extends DefaultTreeModel
implements PwsDatabase, ChangeViewDatabaseListener
{
    private ChangeViewDatabase db;
    private List leafs = new LinkedList();

    public TreeViewDatabase(ChangeViewDatabase aDb)
    {
        super(new DefaultMutableTreeNode());
        if(aDb == null) throw new IllegalArgumentException("Internal db should not be null.");
        db = aDb;
        buildTree();
        db.addChangeViewDatabaseListener(this);
    }

    public void setDatabase(ChangeViewDatabase aDb)
    {
        if(aDb == null) throw new IllegalArgumentException("Internal db should not be null.");
        db.removeChangeViewDatabaseListener(this);
        db = aDb;
        buildTree();
        db.addChangeViewDatabaseListener(this);
        this.nodeStructureChanged((TreeNode) this.getRoot());
    }

    // Mapping logic.

    /** Map an *internal* field to a node.
     *
     * @param aField
     */
    DefaultMutableTreeNode fieldToTree(PwsField aField)
    {
        final Iterator lIter = leafs.iterator();
        while(lIter.hasNext())
        {
            final DefaultMutableTreeNode lLeaf = (DefaultMutableTreeNode) lIter.next();
            final LeafNode lLeafContents = (LeafNode) lLeaf.getUserObject();
            final PwsRecord lInternalRecord = lLeafContents.getRecord().getInternal();
            final Iterator lLeafIterator = lInternalRecord.typeIterator();
            while(lLeafIterator.hasNext())
            {
                try
                {
                    final Byte lType = (Byte) lLeafIterator.next();
                    final PwsField lInternalField = lInternalRecord.get(lType);
                    if(lInternalField == aField) return lLeaf;
                }
                catch(ModelException eIgnore){;}
            }
        }
        return null;
    }

    /** Map an *internal* record to a node.
     *
     * @param aRecord
     */
    DefaultMutableTreeNode recordToTree(PwsRecord aRecord)
    {
        final Iterator lIter = leafs.iterator();
        while(lIter.hasNext())
        {
            final DefaultMutableTreeNode lLeaf = (DefaultMutableTreeNode) lIter.next();
            final LeafNode lLeafContents = (LeafNode) lLeaf.getUserObject();
            if(lLeafContents.getRecord().getInternal() == aRecord) return lLeaf;
        }
        return null;
    }

    PwsRecord nodeToRecord(DefaultMutableTreeNode aNode)
    {
        return ((LeafNode) aNode.getUserObject()).getRecord();
    }

    // Tree rendering.

    class LeafNode
    {
        private ChangeViewRecord record;

        public LeafNode(ChangeViewRecord aRec)
        {
            record = aRec;
        }

        public ChangeViewRecord getRecord()
        {
            return record;
        }

        public String toString()
        {
            return record.toString();
        }
    }

    // Rebuild the visual representation from scratch.
    // You have to do this when the database changes or when a group changes.
    private void buildTree()
    {
        // First we sort all the records in the database.
        final Set lRecSet = new TreeSet();
        final Iterator lIter = db.iterator();
        while(lIter.hasNext())
        {
            final ChangeViewRecord lRec = (ChangeViewRecord) lIter.next();
            lRecSet.add(lRec);
        }

        // Now we are ready for building the tree.
        leafs = new LinkedList();

        // Note that the root node has to have a name, otherwise the tree will generate
        // exception on traversal using keystokes. This is a known problem, see bug id 4546692.
        // Otherwise you get a NullpointerException on javax.swing.JTree.getNextMatch(...)
        final DefaultMutableTreeNode lRoot = new DefaultMutableTreeNode("Password Groups");
        final Iterator lSetIter = lRecSet.iterator();
        while(lSetIter.hasNext())
        {
            final ChangeViewRecord lRec = (ChangeViewRecord) lSetIter.next();
            insertRecord(lRoot, lRec);
        }
        this.setRoot(lRoot);
    }

    /**
     *
     * @param aRoot
     * @param aRecord Must be an *external*  record !
     */
    private void insertRecord(DefaultMutableTreeNode aRoot, ChangeViewRecord aRecord)
    {
        String lGroup = "default";
        if(aRecord.hasType(PwsField.FIELD_GROUP)) try {lGroup = aRecord.get(PwsField.FIELD_GROUP).getAsString();}catch(ModelException eIgnore){;}

        final List lPath = this.splitGroupString(lGroup);
        addLeaf(aRoot, lPath, aRecord);
    }

    private void addLeaf(DefaultMutableTreeNode aBase, List aPath, ChangeViewRecord aRec)
    {
        if(aPath.size() == 0)
        {
            // Add leaf in the base.
            final DefaultMutableTreeNode lLeaf = new DefaultMutableTreeNode(new LeafNode(aRec));
            aBase.add(lLeaf);
            leafs.add(lLeaf);
            final int lChildIndex = aBase.getIndex(lLeaf);
            this.nodesWereInserted(aBase, new int[]{lChildIndex});
        }
        else
        {
            // Search for subnode.
            final String lSubPath = (String) aPath.get(0);
            aPath.remove(0);

            DefaultMutableTreeNode lSubNode = findNode(aBase, lSubPath);
            if(lSubNode == null)
            {
                lSubNode = new DefaultMutableTreeNode(lSubPath);
                aBase.add(lSubNode);
                final int lSubnodeIndex = aBase.getIndex(lSubNode);
                this.nodesWereInserted(aBase, new int[]{lSubnodeIndex});
            }

            // Recursion.
            addLeaf(lSubNode, aPath, aRec);
        }
    }

    private void deleteLeaf(DefaultMutableTreeNode aNode)
    {
        leafs.remove(aNode);
        deleteNode(aNode);
    }

    private void deleteNode(DefaultMutableTreeNode aNode)
    {
        if(aNode != this.getRoot())
        {
            DefaultMutableTreeNode lParent =  (DefaultMutableTreeNode) aNode.getParent();
            if(aNode.getChildCount() == 0)
            {
                int lChildIndex = lParent.getIndex(aNode);
                lParent.remove(aNode);

                this.nodesWereRemoved(lParent, new int[]{lChildIndex}, new Object[]{aNode});
                deleteNode(lParent);
            }
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode aBase, String aName)
    {
        final int lNrChildren = aBase.getChildCount();
        for(int i = 0; i < lNrChildren; i++)
        {
            final DefaultMutableTreeNode lChild = (DefaultMutableTreeNode) aBase.getChildAt(i);
            final Object lChildContents = lChild.getUserObject();

            if(lChildContents instanceof String)
            {
                final String lChildName = (String) lChildContents;
                if(lChildName.equals(aName)) return lChild;
            }
        }
        return null;
    }

    private List splitGroupString(String aGroup)
    {
        if(aGroup == null) return new ArrayList();
        else
        {
            // Split on dot.
            String[] lBanana = aGroup.split("\\.");
            // Now we have to many pieces. Join the pieces that ended with backslash.
            final List lParts = new LinkedList();
            StringBuilder lCurrentPart = null;
            for(int i = 0; i < lBanana.length; i++)
            {
                final String lPart = lBanana[i];
                if(lPart.endsWith("\\") && !lPart.endsWith("\\\\"))
                {
                    // The part contains a continuation character.
                    // So we have to put it in the buffer.
                    final String lCleanPart = removeDoubleBackslash(lPart.substring(0, lPart.length()-1));
                    if(lCurrentPart == null) lCurrentPart = new StringBuilder(lCleanPart).append(".");
                    else lCurrentPart.append(lCleanPart);
                }
                else
                {
                    // The part does not contain  a continuation.
                    // We add it to the currentPart.
                    final String lCleanPart = removeDoubleBackslash(lPart);
                    if(lCurrentPart == null) lParts.add(lCleanPart);
                    else
                    {
                        lCurrentPart.append(lCleanPart);
                        lParts.add(lCurrentPart.toString());
                        lCurrentPart = null;
                    }
                }
            }
            // Dont forget the last part.
            if(lCurrentPart != null) lParts.add(lCurrentPart.toString());

            return lParts;
        }
    }

    private String removeDoubleBackslash(String aSource)
    {
        final StringBuilder lResult = new StringBuilder();

        int lIndex = aSource.indexOf("\\\\");
        while(lIndex >= 0)
        {
            lResult.append(aSource.substring(0, lIndex));
            aSource = aSource.substring(lIndex+1);
            lIndex = aSource.indexOf("\\\\");
        }
        if(aSource.length() > 0) lResult.append(aSource);
        return lResult.toString();
    }

    // Listen to changes.

    public void fieldChange(ChangeViewFieldEvent aEvt)
    {
        final PwsField lInternalField = (PwsField) aEvt.getSource();
        if(lInternalField.getType().equals(PwsField.FIELD_GROUP))
        {
            // Group name changed. This can lead to a reorganization of the tree.
            // We rebuild it completely.
            this.buildTree();
            this.nodeStructureChanged((TreeNode) this.getRoot());
        }
        else
        {
            // An attribute changed that does not affect the tree structure.
            // We can re-render the leaf.
            final TreeNode lNode = fieldToTree(lInternalField);
            this.nodeChanged(lNode);
        }
    }

    public void recordChange(ChangeViewRecordEvent aEvt)
    {
        final PwsRecord lInternalRecord = (PwsRecord) aEvt.getSource();
        final TreeNode lNode = recordToTree(lInternalRecord);
        this.nodeStructureChanged(lNode);
    }

    public void dbChange(ChangeViewDatabaseEvent aEvt)
    {
        final PwsRecord lInternalRec = aEvt.getRecord();
        final ChangeViewRecord lExternalRec = new ChangeViewRecord(db, lInternalRec);
        final int lOp = aEvt.getOp();

        switch(lOp)
        {
            case ChangeViewDatabaseEvent.INSERT:
            {
                this.insertRecord((DefaultMutableTreeNode) this.getRoot(), lExternalRec);
                break;
            }
            case ChangeViewDatabaseEvent.MODIFY:
            {
                final DefaultMutableTreeNode lNode = recordToTree(lInternalRec);
                this.nodeChanged(lNode);
                break;
            }
            case ChangeViewDatabaseEvent.DELETE:
            {
                final DefaultMutableTreeNode lNode = recordToTree(lInternalRec);
                this.deleteLeaf(lNode);
                break;
            }
        }
    }

    // Database methods.

    public int getNrRecords()
    {
        return db.getNrRecords();
    }

    public PwsRecord getRecord(int aIndex)
    {
        return db.getRecord(aIndex);
    }

    public void add(PwsRecord aRecord)
    {
        db.add(aRecord);
    }

    public void remove(int aIndex)
    {
        db.remove(aIndex);
    }

    public Iterator iterator()
    {
        return db.iterator();
    }

    public String getVersion()
    {
        return db.getVersion();
    }

    public String getPassphrase()
    {
        return db.getPassphrase();
    }

    public void setPassphrase(String passphrase)
    {
        db.setPassphrase(passphrase);
    }

    public Codec getCodec()
    {
        return db.getCodec();
    }

    public void setCodec(Codec codec)
    {
        db.setCodec(codec);
    }

    public File getFile()
    {
        return db.getFile();
    }

    public void setFile(File file)
    {
        db.setFile(file);
    }

    public String getParameters()
    {
        return db.getParameters();
    }

    public void setParameters(String parameters)
    {
        db.setParameters(parameters);
    }

    public boolean isChanged()
    {
        return db.isChanged();
    }

    public void setChanged(boolean aChanged)
    {
        db.setChanged(aChanged);
    }
}
