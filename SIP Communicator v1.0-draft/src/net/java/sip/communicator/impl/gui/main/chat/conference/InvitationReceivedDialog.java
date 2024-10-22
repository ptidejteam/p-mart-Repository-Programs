/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chat.conference;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.swing.*;

/**
 * The dialog that pops up when a chat room invitation is received.
 *  
 * @author Yana Stamcheva
 * @author Valentin Martinet
 */
public class InvitationReceivedDialog
    extends SIPCommDialog
    implements ActionListener
{
    private final JTextArea infoTextArea = new JTextArea();

    private final JTextArea invitationReasonTextArea = new JTextArea();

    private final JPanel reasonPanel = new JPanel(new BorderLayout());

    private final JLabel reasonLabel = new JLabel(
        GuiActivator.getResources().getI18NString("service.gui.REASON") + ": ");

    private final JTextField reasonField = new JTextField();

    private final JPanel dataPanel = new JPanel(new BorderLayout(10, 10));

    private final JPanel buttonsPanel
        = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    private final JButton acceptButton = new JButton(
        GuiActivator.getResources().getI18NString("service.gui.ACCEPT"));

    private final JButton rejectButton = new JButton(
        GuiActivator.getResources().getI18NString("service.gui.REJECT"));

    private final JButton ignoreButton = new JButton(
        GuiActivator.getResources().getI18NString("service.gui.IGNORE"));

    private final JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

    private final JPanel northPanel = new JPanel(new BorderLayout(10, 10));

    private final JLabel iconLabel = new JLabel(new ImageIcon(
            ImageLoader.getImage(ImageLoader.INVITE_DIALOG_ICON)));

    private final String title = GuiActivator.getResources()
        .getI18NString("service.gui.INVITATION_RECEIVED");

    /**
     * The <tt>ChatRoomInvitation</tt> for which this dialog is.
     */
    private ChatRoomInvitation invitation = null;;

    /**
     * The <tt>AdHocChatRoomInvitation</tt> for which this dialog is, in case of
     * an <tt>AdHocChatRoom</tt>.
     */
    private AdHocChatRoomInvitation invitationAdHoc = null;

    /**
     * The <tt>MultiUserChatManager</tt> is the one that deals with invitation
     * events.
     */
    private final ConferenceChatManager multiUserChatManager;

    /**
     * The operation set that would handle the rejection if the user choose to
     * reject the invitation.
     */
    private OperationSetMultiUserChat multiUserChatOpSet = null;;

    /**
     * The operation set that would handle the rejection if the user choose to
     * reject the invitation, in case of an <tt>AdHocChatRoom</tt>.
     */
    private OperationSetAdHocMultiUserChat multiUserChatAdHocOpSet = null;;

    /**
     * Constructs the <tt>ChatInviteDialog</tt>.
     * 
     * @param multiUserChatManager the <tt>MultiUserChatManager</tt> is the one
     * that deals with invitation events
     * @param multiUserChatOpSet the operation set that would handle the
     * rejection if the user choose to reject the invitation
     * @param invitation the invitation that this dialog represents
     */
    public InvitationReceivedDialog (ConferenceChatManager multiUserChatManager,
            OperationSetMultiUserChat multiUserChatOpSet,
            ChatRoomInvitation invitation)
    {
        super(GuiActivator.getUIService().getMainFrame());

        this.multiUserChatManager = multiUserChatManager;

        this.multiUserChatOpSet = multiUserChatOpSet;

        this.invitation = invitation;

        this.setModal(false);

        this.setTitle(title);

        this.mainPanel.setPreferredSize(new Dimension(400, 230));

//        this.mainPanel.setOpaque(false);

        infoTextArea.setText(
            GuiActivator.getResources().getI18NString(
                "service.gui.INVITATION_RECEIVED_MSG",
                new String[] {  invitation.getInviter(),
                                invitation.getTargetChatRoom().getName()}));

        if(invitation.getReason() != null && !invitation.getReason().equals(""))
        {
            invitationReasonTextArea.setText(invitation.getReason());
            invitationReasonTextArea.setBorder(
                BorderFactory.createTitledBorder(
                    GuiActivator.getResources()
                        .getI18NString("service.gui.INVITATION")));

            this.dataPanel.add(invitationReasonTextArea, BorderLayout.CENTER);
        }

        this.initGUI();
    }

    /**
     * Constructs the <tt>ChatInviteDialog</tt>, in case of an
     * <tt>AdHocChatRoom</tt>.
     * 
     * @param multiUserChatManager the <tt>MultiUserChatManager</tt> is the one
     * that deals with invitation events
     * @param multiUserChatAdHocOpSet the operation set that would handle the
     * rejection if the user choose to reject the invitation
     * @param invitationAdHoc the invitation that this dialog represents
     */
    public InvitationReceivedDialog (ConferenceChatManager multiUserChatManager,
            OperationSetAdHocMultiUserChat multiUserChatAdHocOpSet,
            AdHocChatRoomInvitation invitationAdHoc)
    {
        super(GuiActivator.getUIService().getMainFrame());

        this.multiUserChatManager = multiUserChatManager;

        this.multiUserChatAdHocOpSet = multiUserChatAdHocOpSet;

        this.invitationAdHoc = invitationAdHoc;

        this.setModal(false);

        this.setTitle(title);

        this.mainPanel.setPreferredSize(new Dimension(400, 230));

//        this.mainPanel.setOpaque(false);

        infoTextArea.setText(
            GuiActivator.getResources().getI18NString(
                "service.gui.INVITATION_RECEIVED_MSG",
                new String[] {
                    invitationAdHoc.getInviter(),
                    invitationAdHoc.getTargetAdHocChatRoom().getName()}));

        if(invitationAdHoc.getReason() != null
                && !invitationAdHoc.getReason().equals(""))
        {
            invitationReasonTextArea.setText(invitationAdHoc.getReason());
            invitationReasonTextArea.setBorder(
                BorderFactory.createTitledBorder(
                    GuiActivator.getResources()
                        .getI18NString("service.gui.INVITATION")));

            this.dataPanel.add(invitationReasonTextArea, BorderLayout.CENTER);
        }
        
        this.initGUI();
    }

    /**
     * Initializes and builds the GUI.
     */
    public void initGUI()
    {
        this.infoTextArea.setFont(infoTextArea.getFont().deriveFont(Font.BOLD));
        this.infoTextArea.setLineWrap(true);
        this.infoTextArea.setOpaque(false);
        this.infoTextArea.setWrapStyleWord(true);
        this.infoTextArea.setEditable(false);

        this.northPanel.add(iconLabel, BorderLayout.WEST);
        this.northPanel.add(infoTextArea, BorderLayout.CENTER);
        this.northPanel.setOpaque(false);

        this.reasonPanel.add(reasonLabel, BorderLayout.WEST);
        this.reasonPanel.add(reasonField, BorderLayout.CENTER);
        this.reasonPanel.setOpaque(false);

        this.dataPanel.add(reasonPanel, BorderLayout.SOUTH);
        this.dataPanel.setOpaque(false);

        this.acceptButton.addActionListener(this);
        this.rejectButton.addActionListener(this);
        this.ignoreButton.addActionListener(this);

        this.buttonsPanel.add(acceptButton);
        this.buttonsPanel.add(rejectButton);
        this.buttonsPanel.add(ignoreButton);
        this.buttonsPanel.setOpaque(false);

        this.getRootPane().setDefaultButton(acceptButton);
        this.acceptButton.setMnemonic(
            GuiActivator.getResources().getI18nMnemonic("service.gui.ACCEPT"));
        this.rejectButton.setMnemonic(
            GuiActivator.getResources().getI18nMnemonic("service.gui.REJECT"));
        this.ignoreButton.setMnemonic(
            GuiActivator.getResources().getI18nMnemonic("service.gui.IGNORE"));

        this.mainPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        this.mainPanel.add(northPanel, BorderLayout.NORTH);
        this.mainPanel.add(dataPanel, BorderLayout.CENTER);
        this.mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        this.mainPanel.setOpaque(false);

        this.getContentPane().add(mainPanel);
    }

    /**
     * Handles the <tt>ActionEvent</tt> triggered when one user clicks
     * on one of the buttons.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton button = (JButton)e.getSource();

        if (button.equals(acceptButton))
        {
            if(invitationAdHoc == null)
                multiUserChatManager.acceptInvitation(invitation);
            else
                try 
                {
                    multiUserChatManager.acceptInvitation(
                            invitationAdHoc, multiUserChatAdHocOpSet);
                } catch (OperationFailedException e1) 
                {
                    e1.printStackTrace();
                }
        }
        else if (button.equals(rejectButton))
        {
            if(multiUserChatAdHocOpSet == null && invitationAdHoc == null)
                multiUserChatManager.rejectInvitation(multiUserChatOpSet,
                    invitation, reasonField.getText());
            else
                multiUserChatManager.rejectInvitation(multiUserChatAdHocOpSet,
                        invitationAdHoc, reasonField.getText());
        }

        this.dispose();
    }

    protected void close(boolean isEscaped)
    {}
}
