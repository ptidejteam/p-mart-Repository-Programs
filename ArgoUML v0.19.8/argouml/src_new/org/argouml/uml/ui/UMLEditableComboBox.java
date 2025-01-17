// $Id: UMLEditableComboBox.java,v 1.2 2006/03/02 05:07:09 vauchers Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// $Id: UMLEditableComboBox.java,v 1.2 2006/03/02 05:07:09 vauchers Exp $

package org.argouml.uml.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.argouml.application.helpers.ResourceLoaderWrapper;

/**
 * An editable combobox. Upon pressing enter the text entered by the user is
 * sent as an actioncommand to the actionlistener (this). The item that's being
 * edited is sent to the method doIt after that. The developer should implement
 * this method
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 4, 2003
 */
public abstract class UMLEditableComboBox extends UMLComboBox2 {

    /**
     * The comboboxeditor for editable uml comboboxes. This has to be changed
     * since it controls the rendering of the textfield where the user can edit
     * the list elements. Setitem has to give the correct value. Furthermore,
     * the standard comboboxeditor (BasicComboBoxEditor) does not support
     * showing icons.
     *
     * @author jaap.branderhorst@xs4all.nl
     * @since Jan 5, 2003
     */
    protected class UMLComboBoxEditor extends BasicComboBoxEditor {

        /**
         * A panel which helps us to show the editable textfield for this
         * combobox (including the Icon).
         *
         * @author jaap.branderhorst@xs4all.nl
         * @since Jan 5, 2003
         */
        private class UMLImagePanel extends JPanel {

            /**
             * The label that shows the icon.
             */
            private JLabel imageIconLabel = new JLabel();
            /**
             * The textfield the user can edit.
             */
            private JTextField theTextField;

            /**
             * Constructs a UMLImagePanel
             * @param textField The textfield the user can edit
             * @param showIcon boolean which must be true if an icon is to be
             * shown.
             */
            public UMLImagePanel(JTextField textField, boolean showIcon) {
                setLayout(new BorderLayout());
                theTextField = textField;
                setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                if (showIcon) {
                    // we don't want to show some nasty gray background
                    // color, now do we?
                    imageIconLabel.setOpaque(true);
                    imageIconLabel.setBackground(theTextField.getBackground());
                    add(imageIconLabel, BorderLayout.WEST);
                }
                add(theTextField, BorderLayout.CENTER);
            }

            public void setText(String text) {
                theTextField.setText(text);
            }

            public String getText() {
                return theTextField.getText();
            }

            /**
             * Sets the icon. Calls repaint to redraw the panel
             * @param i The icon to be shown.
             */
            public void setIcon(Icon i) {
                if (i != null) {
                    imageIconLabel.setIcon(i);
                    // necessary to create distance between
                    // the textfield and the icon.
                    imageIconLabel.setBorder(BorderFactory
                            .createEmptyBorder(0, 2, 0, 2));

                } else {
                    imageIconLabel.setIcon(null);
                    imageIconLabel.setBorder(null);
                }
                imageIconLabel.invalidate();
                validate();
                repaint();
            }

            public void selectAll() {
                theTextField.selectAll();
            }

            public void addActionListener(ActionListener l) {
                theTextField.addActionListener(l);
            }

            public void removeActionListener(ActionListener l) {
                theTextField.removeActionListener(l);
            }

        }

        private UMLImagePanel panel;

        /**
         * True if an icon should be shown.
         */
        private boolean theShowIcon;


        /**
         * Constructor for UMLComboBoxEditor.
         *
         * @param showIcon true if an icon is to be shown
         */
        public UMLComboBoxEditor(boolean showIcon) {
            super();
            panel = new UMLImagePanel(editor, showIcon);
            setShowIcon(showIcon);
        }

        /**
         * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
         */
        public void setItem(Object anObject) {
            if (((UMLComboBoxModel2) getModel()).contains(anObject)) {
                editor.setText(((UMLListCellRenderer2) getRenderer())
                        .makeText(anObject));
                if (theShowIcon)
                    panel.setIcon(ResourceLoaderWrapper.getInstance()
                            .lookupIcon(anObject));
            } else
                super.setItem(anObject);

        }

        /**
         * Returns the showIcon.
         * @return boolean
         */
        public boolean isShowIcon() {
            return theShowIcon;
        }

        /**
         * Sets the showIcon.
         * @param showIcon The showIcon to set
         */
        public void setShowIcon(boolean showIcon) {
            theShowIcon = showIcon;
        }

        /**
         * @see javax.swing.ComboBoxEditor#getEditorComponent()
         */
        public Component getEditorComponent() {
            return panel;
        }

        /**
         * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
         */
        public void addActionListener(ActionListener l) {
            panel.addActionListener(l);
        }



        /**
         * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
         */
        public void removeActionListener(ActionListener l) {
            panel.removeActionListener(l);
        }

        /**
         * @see javax.swing.ComboBoxEditor#selectAll()
         */
        public void selectAll() {
            super.selectAll();
        }

        /**
         * @see javax.swing.ComboBoxEditor#getItem()
         */
        public Object getItem() {
            return panel.getText();
        }

    }

    /**
     * @see org.argouml.uml.ui.UMLComboBox2#UMLComboBox2(
     * UMLComboBoxModel2, UMLAction, boolean)
     */
    public UMLEditableComboBox(UMLComboBoxModel2 model, UMLAction selectAction,
            boolean showIcon) {
        super(model, selectAction, showIcon);
        setEditable(true);
        setEditor(new UMLComboBoxEditor(showIcon));
        getEditor().addActionListener(this);
    }

    /**
     * @see org.argouml.uml.ui.UMLComboBox2#UMLComboBox2(
     * UMLComboBoxModel2, UMLAction)
     */
    public UMLEditableComboBox(UMLComboBoxModel2 arg0, UMLAction selectAction) {
        this(arg0, selectAction, true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() instanceof JTextField) {
            Object oldValue = getSelectedItem();
            ComboBoxEditor editor = getEditor();
            Object item = editor.getItem();
            doOnEdit(item);
            // next statement is necessary to update the textfield
            // if the selection is equal to what was allready
            // selected
            if (oldValue == getSelectedItem())
                getEditor().setItem(getSelectedItem());
        }
    }

    /**
     * This method is called after the user has edited the editable textfield
     * and has press enter. ActionPerformed determines that the action is about
     * editing the textfield and calls this method afterwards.
     * @param item The item in the comboboxeditor. In this case it's the text of
     * the editable textfield.
     */
    protected abstract void doOnEdit(Object item);

}
