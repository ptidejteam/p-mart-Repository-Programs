/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.login;

import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.cert.*;
import java.security.interfaces.*;
import java.text.*;

import javax.naming.*;
import javax.swing.*;
import javax.naming.ldap.*;
import javax.security.auth.x500.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.service.resources.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.util.swing.*;


/**
 * Asks the user for permission for the
 * certificates which are for some reason not valid and not globally trusted.
 *
 * @author Damian Minkov
 */
public class CertificateVerificationServiceImpl
    implements CertificateVerificationService
{
    /**
     * The logger.
     */
    private static final Logger logger =
        Logger.getLogger(CertificateVerificationServiceImpl.class);

    /**
     * Checks does the user trust the supplied chain of certificates, when
     * connecting to the server and port.
     *
     * @param   chain the chain of the certificates to check with user.
     * @param   toHost the host we are connecting.
     * @param   toPort the port used when connecting.
     * @return  the result of user interaction on of DO_NOT_TRUST, TRUST_ALWAYS,
     *          TRUST_THIS_SESSION_ONLY.
     */
    public int verificationNeeded(
        final Certificate[] chain, final String toHost, final int toPort)
    {
        final VerifyCertificateDialog dialog = new VerifyCertificateDialog(
                        chain, toHost, toPort);
        try
        {
            // show the dialog in the swing thread and wait for the user
            // choice
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run()
                {
                    dialog.setVisible(true);
                }
            });
        }
        catch (Exception e)
        {
            logger.error("Cannot show certificate verification dialog", e);
            return DO_NOT_TRUST;
        }

        if(!dialog.isTrusted)
            return DO_NOT_TRUST;
        else if(dialog.alwaysTrustCheckBox.isSelected())
            return TRUST_ALWAYS;
        else
            return TRUST_THIS_SESSION_ONLY;
    }

    /**
     * The dialog that is shown to user.
     */
    private static class VerifyCertificateDialog
        extends SIPCommDialog
    {
        /**
         * Date formatter.
         */
        private static SimpleDateFormat dateFormatter =
            new SimpleDateFormat("MM/dd/yyyy");

        /**
         * Used for converting bytes to HEX.
         */
        private static final String HEXES = "0123456789ABCDEF";

        /**
         * The maximum width that we allow message dialogs to have.
         */
        private static final int MAX_MSG_PANE_WIDTH = 600;

        /**
         * The maximum height that we allow message dialogs to have.
         */
        private static final int MAX_MSG_PANE_HEIGHT = 800;

        /**
         * The certificate to show.
         */
        Certificate cert;

        /**
         * The host we are connecting to.
         */
        String host;

        /**
         * The port we use.
         */
        int port;

        /**
         * The certificate panel.
         */
        TransparentPanel certPanel;

        /**
         * This dialog content pane.
         */
        TransparentPanel contentPane;

        /**
         * Whether certificate description is shown.
         */
        boolean certOpened = false;

        /**
         * The button to show certificate description.
         */
        JButton certButton;

        /**
         * The check box if checked permanently stored the certificate
         * which will be always trusted.
         */
        JCheckBox alwaysTrustCheckBox = new JCheckBox(
            GuiActivator.getResources().getI18NString("service.gui.ALWAYS_TRUST"),
            false);

        /**
         * Whether the user trusts this certificate.
         */
        boolean isTrusted = false;

        /**
         * Creates the dialog.
         * @param certs
         * @param host 
         * @param port
         */
        public VerifyCertificateDialog(Certificate[] certs, String host, int port)
        {
            super(GuiActivator.getUIService().getMainFrame(), false);

            setTitle(GuiActivator.getResources().getI18NString(
                "service.gui.CERT_DIALOG_TITLE"));
            setModal(true);

            // for now shows only the first certificate from the chain
            this.cert = certs[0];
            this.host = host;
            this.port = port;

            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            init();

            setLocationRelativeTo(getParent());
        }

        /**
         * Inits the dialog initial display.
         */
        private void init()
        {
            this.getContentPane().setLayout(new BorderLayout());

            contentPane =
                new TransparentPanel(new BorderLayout(5, 5));

            TransparentPanel northPanel =
                new TransparentPanel(new BorderLayout(5, 5));
            northPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

            JLabel imgLabel = new JLabel(
                GuiActivator.getResources().getImage(
                    "impl.media.security.zrtp.CONF_ICON"));
            imgLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            northPanel.add(imgLabel, BorderLayout.WEST);

            String descriptionTxt = GuiActivator.getResources()
                .getI18NString(
                    "service.gui.CERT_DIALOG_DESCRIPTION_TXT",
                    new String[]{
                        GuiActivator.getResources().getSettingsString(
                            "service.gui.APPLICATION_NAME"),
                        host,
                        String.valueOf(port)});

            JEditorPane descriptionPane = new JEditorPane();
            descriptionPane.setOpaque(false);
            descriptionPane.setEditable(false);
            descriptionPane.setContentType("text/html");
            descriptionPane.setText(descriptionTxt);
            descriptionPane.setSize(
                        new Dimension(MAX_MSG_PANE_WIDTH, MAX_MSG_PANE_HEIGHT));
            int height = descriptionPane.getPreferredSize().height;
            descriptionPane.setPreferredSize(
                        new Dimension(MAX_MSG_PANE_WIDTH, height));

            northPanel.add(descriptionPane, BorderLayout.CENTER);
            contentPane.add(northPanel, BorderLayout.NORTH);

            certPanel = new TransparentPanel();
            contentPane.add(certPanel, BorderLayout.CENTER);

            TransparentPanel southPanel =
                new TransparentPanel(new BorderLayout());
            contentPane.add(southPanel, BorderLayout.SOUTH);

            certButton = new JButton();
            certButton.setText(GuiActivator.getResources()
                .getI18NString("service.gui.SHOW_CERT"));
            certButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    actionShowCertificate();
                }
            });
            TransparentPanel firstButonPanel = 
                new TransparentPanel(new FlowLayout(FlowLayout.LEFT));
            firstButonPanel.add(certButton);
            southPanel.add(firstButonPanel, BorderLayout.WEST);

            TransparentPanel secondButonPanel =
                new TransparentPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton(
                GuiActivator.getResources().getI18NString("service.gui.CANCEL"));
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    actionCancel();
                }
            });
            JButton continueButton = new JButton(
                GuiActivator.getResources().getI18NString("service.gui.CONTINUE"));
            continueButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    actionContinue();
                }
            });
            secondButonPanel.add(continueButton);
            secondButonPanel.add(cancelButton);
            southPanel.add(secondButonPanel, BorderLayout.EAST);

            this.getContentPane().add(contentPane, BorderLayout.CENTER);

            pack();
        }

        /**
         * Action when shoe certificate button is clicked.
         */
        private void actionShowCertificate()
        {
            if(certOpened)
            {
                certPanel.removeAll();
                certButton.setText(GuiActivator.getResources()
                    .getI18NString("service.gui.SHOW_CERT"));

                certPanel.revalidate();
                certPanel.repaint();
                pack();
                certOpened = false;
                setLocationRelativeTo(getParent());
                return;
            }

            certPanel.setLayout(new BorderLayout());
            certPanel.add(alwaysTrustCheckBox, BorderLayout.NORTH);

            Component certInfoPane = null;
            if(cert instanceof X509Certificate)
            {
                certInfoPane = getX509DisplayComponent((X509Certificate)cert);
            }
            else
            {
                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setText(cert.toString());
                certInfoPane = textArea;
            }

            final JScrollPane certScroll = new JScrollPane(certInfoPane);
            certScroll.setPreferredSize(new Dimension(300, 300));
            certPanel.add(certScroll, BorderLayout.CENTER);

            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    certScroll.getVerticalScrollBar().setValue(0);
                }
            });

                certButton.setText(GuiActivator.getResources()
                .getI18NString("service.gui.HIDE_CERT"));
            
            certPanel.revalidate();
            certPanel.repaint();
            // restore default values for prefered size,
            // as we have resized its components let it calculate
            // that size
            setPreferredSize(null);
            pack();
            certOpened = true;
            setLocationRelativeTo(getParent());
        }

        /**
         * Action when cancel button is clicked.
         */
        private void actionCancel()
        {
            isTrusted = false;
            dispose();
        }

        /**
         * Action when continue is clicked.
         */
        private void actionContinue()
        {
            isTrusted = true;
            dispose();
        }

        /**
         * Called when dialog closed or escape pressed.
         * @param isEscaped is escape button pressed.
         */
        protected void close(boolean isEscaped)
        {
            actionCancel();
        }

        /**
         *
         * @param certificate
         * @return
         */
        private static Component getX509DisplayComponent(
            X509Certificate certificate)
        {
            Insets valueInsets = new Insets(2,10,0,0);
            Insets titleInsets = new Insets(10,5,0,0);

            ResourceManagementService resources = GuiActivator.getResources();

            TransparentPanel certDisplayPanel =
                new TransparentPanel(new GridBagLayout());

            int currentRow = 0;

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(2,5,0,0);
            constraints.gridx = 0;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.gridy = currentRow++;

            X500Principal issuer = certificate.getIssuerX500Principal();
            X500Principal subject = certificate.getSubjectX500Principal();

            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_ISSUED_TO")),
                constraints);

            constraints.gridy = currentRow++;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_CN")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(subject.getName(), "CN")),
                constraints);
            
            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_O")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(subject.getName(), "O")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_C")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(subject.getName(), "C")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_ST")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(subject.getName(), "ST")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_L")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(subject.getName(), "L")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            constraints.insets = titleInsets;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_ISSUED_BY")),
                constraints);
            constraints.insets = valueInsets;

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_C")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(issuer.getName(), "C")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_O")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(issuer.getName(), "O")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_OU")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(getCertificateValue(issuer.getName(), "OU")),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            constraints.insets = titleInsets;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_VALIDITY")),
                constraints);
            constraints.insets = valueInsets;

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_ISSUED_ON")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(dateFormatter.format(certificate.getNotBefore())),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_EXPIRES_ON")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(dateFormatter.format(certificate.getNotAfter())),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            constraints.insets = titleInsets;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_FINGERPRINTS")),
                constraints);
            constraints.insets = valueInsets;

            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(certificate.getEncoded());
                String sha1String = getHex(md.digest());

                md = MessageDigest.getInstance("MD5");
                md.update(certificate.getEncoded());
                String md5String = getHex(md.digest());

                JTextArea sha1Area = new JTextArea(sha1String);
                sha1Area.setLineWrap(false);
                sha1Area.setOpaque(false);
                sha1Area.setWrapStyleWord(true);
                sha1Area.setEditable(false);

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel("SHA1:"),
                    constraints);
                
                constraints.gridx = 1;
                certDisplayPanel.add(
                    sha1Area,
                    constraints);

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel("MD5:"),
                    constraints);

                JTextArea md5Area = new JTextArea(md5String);
                md5Area.setLineWrap(false);
                md5Area.setOpaque(false);
                md5Area.setWrapStyleWord(true);
                md5Area.setEditable(false);

                constraints.gridx = 1;
                certDisplayPanel.add(
                    md5Area,
                    constraints);
            }
            catch (Exception e)
            {
                // do nothing as we cannot show this value
                logger.warn("Error in certificate, cannot show fingerprints", e);
            }
            

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            constraints.insets = titleInsets;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_CERT_DETAILS")),
                constraints);
            constraints.insets = valueInsets;

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_SER_NUM")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(certificate.getSerialNumber().toString()),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_VER")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(String.valueOf(certificate.getVersion())),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_SIGN_ALG")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(String.valueOf(certificate.getSigAlgName())),
                constraints);

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            constraints.insets = titleInsets;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_PUB_KEY_INFO")),
                constraints);
            constraints.insets = valueInsets;

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_ALG")),
                constraints);
            constraints.gridx = 1;
            certDisplayPanel.add(
                new JLabel(certificate.getPublicKey().getAlgorithm()),
                constraints);

            if(certificate.getPublicKey().getAlgorithm().equals("RSA"))
            {
                RSAPublicKey key = (RSAPublicKey)certificate.getPublicKey();

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel(
                    resources.getI18NString("service.gui.CERT_INFO_PUB_KEY")),
                    constraints);

                JTextArea pubkeyArea = new JTextArea(
                    resources.getI18NString(
                        "service.gui.CERT_INFO_KEY_BYTES_PRINT",
                        new String[]{
                            String.valueOf(key.getModulus().toByteArray().length - 1),
                            key.getModulus().toString(16)
                        }));
                pubkeyArea.setLineWrap(false);
                pubkeyArea.setOpaque(false);
                pubkeyArea.setWrapStyleWord(true);
                pubkeyArea.setEditable(false);

                constraints.gridx = 1;
                certDisplayPanel.add(
                    pubkeyArea,
                    constraints);

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel(
                    resources.getI18NString("service.gui.CERT_INFO_EXP")),
                    constraints);
                constraints.gridx = 1;
                certDisplayPanel.add(
                    new JLabel(key.getPublicExponent().toString()),
                    constraints);

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel(
                    resources.getI18NString("service.gui.CERT_INFO_KEY_SIZE")),
                    constraints);
                constraints.gridx = 1;
                certDisplayPanel.add(
                    new JLabel(resources.getI18NString(
                        "service.gui.CERT_INFO_KEY_BITS_PRINT",
                        new String[]{
                            String.valueOf(key.getModulus().bitLength())})),
                    constraints);
            }
            else if(certificate.getPublicKey().getAlgorithm().equals("DSA"))
            {
                DSAPublicKey key =
                    (DSAPublicKey)certificate.getPublicKey();

                constraints.gridy = currentRow++;
                constraints.gridx = 0;
                certDisplayPanel.add(new JLabel("Y:"), constraints);

                JTextArea yArea = new JTextArea(key.getY().toString(16));
                yArea.setLineWrap(false);
                yArea.setOpaque(false);
                yArea.setWrapStyleWord(true);
                yArea.setEditable(false);

                constraints.gridx = 1;
                certDisplayPanel.add(
                    yArea,
                    constraints);
            }

            constraints.gridy = currentRow++;
            constraints.gridx = 0;
            certDisplayPanel.add(new JLabel(
                resources.getI18NString("service.gui.CERT_INFO_SIGN")),
                constraints);

            JTextArea signArea = new JTextArea(
                resources.getI18NString(
                        "service.gui.CERT_INFO_KEY_BYTES_PRINT",
                        new String[]{
                            String.valueOf(certificate.getSignature().length),
                            getHex(certificate.getSignature())
                        }));
            signArea.setLineWrap(false);
            signArea.setOpaque(false);
            signArea.setWrapStyleWord(true);
            signArea.setEditable(false);

            constraints.gridx = 1;
            certDisplayPanel.add(
                signArea,
                constraints);

            return certDisplayPanel;
        }

        /**
         * Extract values from certificate DNs(Distinguished Names).
         * @param rfc2253String the certificate string.
         * @param attributeName the DN attribute name to search for.
         * @return empty string or the found value.
         */
        private static String getCertificateValue(
            String rfc2253String, String attributeName)
        {
            try
            {
                LdapName issuerDN = new LdapName(rfc2253String);
                java.util.List<Rdn> l = issuerDN.getRdns();
                for (int i = 0; i < l.size(); i++)
                {
                    Rdn rdn = l.get(i);
                    if (rdn.getType().equals(attributeName))
                    {
                        return (String) rdn.getValue();
                    }
                }
            }
            catch (InvalidNameException ex)
            {
                // do nothing
                logger.warn("Wrong DN:" + rfc2253String, ex);
            }

            return "";
        }

        /**
         * Converts the byte array to hex string.
         * @param raw the data.
         * @return the hex string.
         */
        public static String getHex( byte [] raw )
        {
            if ( raw == null )
            {
                return null;
            }
            final StringBuilder hex = new StringBuilder( 2 * raw.length );
            for ( final byte b : raw )
            {
                hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
            }
            return hex.toString();
        }
    }
}
