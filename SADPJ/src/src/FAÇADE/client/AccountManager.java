package src.FAÇADE.client;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import src.FAÇADE.server.CustomerIntr;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class AccountManager extends JFrame {
  public static final String newline = "\n";
  public static final String VALIDATE_SAVE = "Validate & Save";

  public static final String EXIT = "Exit";
  public static final String VISA = "Visa";
  public static final String DISCOVER = "Discover";
  public static final String MASTER = "Master";
  public static final String VALIDCARD = "Valid Card";

  private String RMIHost, RMIPort;
  private JComboBox cmbCardType;

  private JTextField txtFirstName, txtLastName;
  private JTextField txtAddress, txtCity, txtState;
  private JTextField txtCardNumber, txtExpDate;

  private JLabel lblCardType;
  private JLabel lblFirstName, lblLastName;
  private JLabel lblAddress, lblCity, lblState;
  private JLabel lblCardNumber, lblExpDate;
  private JLabel lblResult, lblResultValue;

  public AccountManager(String host, String port) {
    super(" Proxy Pattern - Example ");

    RMIHost = host;
    RMIPort = port;

    cmbCardType = new JComboBox();
    cmbCardType.addItem(AccountManager.VISA);
    cmbCardType.addItem(AccountManager.MASTER);
    cmbCardType.addItem(AccountManager.DISCOVER);

    txtFirstName = new JTextField(20);
    txtLastName = new JTextField(20);
    txtAddress = new JTextField(20);
    txtCity = new JTextField(20);
    txtState = new JTextField(20);
    txtCardNumber = new JTextField(20);
    txtExpDate = new JTextField(20);

    lblCardType = new JLabel("Card Type:");
    lblFirstName = new JLabel("First Name:");
    lblLastName = new JLabel("Last Name:");
    lblAddress = new JLabel("Address:");
    lblCity = new JLabel("City:");
    lblState = new JLabel("State:");
    lblCardNumber = new JLabel("Card Number:");
    lblExpDate = new JLabel("Exp Date:");

    lblResult = new JLabel("Result:");
    lblResultValue =
      new JLabel(" Please click on Validate & Save Button");


    //Create the open button
    JButton validateSaveButton =
      new JButton(AccountManager.VALIDATE_SAVE);
    validateSaveButton.setMnemonic(KeyEvent.VK_V);
    JButton exitButton = new JButton(AccountManager.EXIT);
    exitButton.setMnemonic(KeyEvent.VK_X);
    ButtonHandler objButtonHandler = new ButtonHandler(this);


    validateSaveButton.addActionListener(objButtonHandler);
    exitButton.addActionListener(new ButtonHandler());

    //For layout purposes, put the buttons in a separate panel
    JPanel buttonPanel = new JPanel();

    //****************************************************
    GridBagLayout gridbag = new GridBagLayout();
    buttonPanel.setLayout(gridbag);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;

    buttonPanel.add(lblCardType);
    buttonPanel.add(cmbCardType);
    buttonPanel.add(lblFirstName);
    buttonPanel.add(txtFirstName);
    buttonPanel.add(lblLastName);
    buttonPanel.add(txtLastName);
    buttonPanel.add(lblAddress);
    buttonPanel.add(txtAddress);
    buttonPanel.add(lblCity);
    buttonPanel.add(txtCity);
    buttonPanel.add(lblState);
    buttonPanel.add(txtState);
    buttonPanel.add(lblCardNumber);
    buttonPanel.add(txtCardNumber);
    buttonPanel.add(lblExpDate);
    buttonPanel.add(txtExpDate);
    buttonPanel.add(lblResult);
    buttonPanel.add(lblResultValue);

    buttonPanel.add(validateSaveButton);
    buttonPanel.add(exitButton);

    gbc.insets.top = 5;
    gbc.insets.bottom = 5;
    gbc.insets.left = 5;
    gbc.insets.right = 5;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gridbag.setConstraints(lblFirstName, gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gridbag.setConstraints(txtFirstName, gbc);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gridbag.setConstraints(lblLastName, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gridbag.setConstraints(txtLastName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gridbag.setConstraints(lblAddress, gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gridbag.setConstraints(txtAddress, gbc);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gridbag.setConstraints(lblCity, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    gridbag.setConstraints(txtCity, gbc);
    gbc.gridx = 0;
    gbc.gridy = 4;
    gridbag.setConstraints(lblState, gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    gridbag.setConstraints(txtState, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gridbag.setConstraints(lblCardType, gbc);
    gbc.gridx = 1;
    gbc.gridy = 5;
    gridbag.setConstraints(cmbCardType, gbc);
    gbc.gridx = 0;
    gbc.gridy = 6;
    gridbag.setConstraints(lblCardNumber, gbc);
    gbc.gridx = 1;
    gbc.gridy = 6;
    gridbag.setConstraints(txtCardNumber, gbc);
    gbc.gridx = 0;
    gbc.gridy = 7;
    gridbag.setConstraints(lblExpDate, gbc);
    gbc.gridx = 1;
    gbc.gridy = 7;
    gridbag.setConstraints(txtExpDate, gbc);

    gbc.gridx = 0;
    gbc.gridy = 8;
    gridbag.setConstraints(lblResult, gbc);
    gbc.gridx = 1;
    gbc.gridy = 8;
    gridbag.setConstraints(lblResultValue, gbc);

    gbc.insets.left = 2;
    gbc.insets.right = 2;
    gbc.insets.top = 40;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 10;
    gridbag.setConstraints(validateSaveButton, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 10;
    gridbag.setConstraints(exitButton, gbc);

    //****************************************************

    //Add the buttons and the log to the frame
    Container contentPane = getContentPane();

    contentPane.add(buttonPanel, BorderLayout.CENTER);
    try {
      UIManager.setLookAndFeel(new WindowsLookAndFeel());
      SwingUtilities.updateComponentTreeUI(
        AccountManager.this);
    } catch (Exception ex) {
      System.out.println(ex);
    }


  }
  public String getFirstName() {
    return txtFirstName.getText();
  }
  public String getLastName() {
    return txtLastName.getText();
  }
  public String getAddress() {
    return txtAddress.getText();
  }
  public String getCity() {
    return txtCity.getText();
  }
  public String getAddrState() {
    return txtState.getText();
  }
  public String getCardNumber() {
    return txtCardNumber.getText();
  }
  public String getExpDate() {
    return txtExpDate.getText();
  }
  public String getCardType() {
    return (String) cmbCardType.getSelectedItem();
  }
  public void setResultDisplay(String msg) {
    lblResultValue.setText(msg);
  }
  public String getRMIHost() {
    return RMIHost;
  }
  public String getRMIPort() {
    return RMIPort;
  }
  public static void main(String[] args) {
    String port = "1099";
    String host = "localhost";

    // Check for hostname argument
    if (args.length == 1) {
      host = args[0];
    }

    if (args.length == 2) {
      port = args[1];
    }

    // Assign security manager
    if (System.getSecurityManager() == null) {
      System.setSecurityManager (new RMISecurityManager());

    }

    JFrame frame = new AccountManager(host, port);

    frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        }
                           );

    //frame.pack();
    frame.setSize(500, 450);
    frame.setVisible(true);
  }

} // End of class AccountManager

class ButtonHandler implements ActionListener {
  AccountManager objAccountManager;
  public void actionPerformed(ActionEvent e) {
    String validateCheckResult = null;
    CustomerIntr facade = null;

    if (e.getActionCommand().equals(AccountManager.EXIT)) {
      System.exit(1);
    }
    if (e.getActionCommand().equals(
          AccountManager.VALIDATE_SAVE)) {
      //get input values
      String firstName = objAccountManager.getFirstName();
      String lastName = objAccountManager.getLastName();
      String address = objAccountManager.getAddress();
      String city = objAccountManager.getCity();
      String state = objAccountManager.getAddrState();
      String cardType = objAccountManager.getCardType();
      String cardNumber =
        objAccountManager.getCardNumber();
      String cardExpDate = objAccountManager.getExpDate();

      // Create a facade instance
      //CustomerIntr facade=new  CustomerFacade();

      try {
        // Call registry for AddOperation
        facade = (CustomerIntr) Naming.lookup ("rmi://" +
                 objAccountManager.getRMIHost() + ":" +
                 objAccountManager.getRMIPort() +
                 "/CustomerFacade");

        facade.setFName(firstName);
        facade.setLName(lastName);
        facade.setAddress(address);
        facade.setCity(city);
        facade.setState(state);
        facade.setCardType(cardType);
        facade.setCardNumber(cardNumber);
        facade.setCardExpDate(cardExpDate);

        //Client is not required to access subsystem components.
        boolean result = facade.saveCustomerData();

        if (result) {
          validateCheckResult = 
            " Valid Customer Data: Data Saved Successfully ";

        } else {
          validateCheckResult = 
            " Invalid Customer Data: Data Could Not Be Saved ";

        }
      } catch (Exception ex) {
        System.out.println(
          "Error: Please check to ensure the " +
          "remote server is running" +
          ex.getMessage());
      }

      objAccountManager.setResultDisplay(
        validateCheckResult);
    }
  }

  public ButtonHandler() {
  }
  public ButtonHandler(AccountManager inobjAccountManager) {
    objAccountManager = inobjAccountManager;
  }

} // End of class ButtonHandler


