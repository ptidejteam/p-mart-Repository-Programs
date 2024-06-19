package src.STATE;
//import java.io.*;
//import java.io.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class AccountManager extends JFrame {
  public static final String newline = "\n";
  public static final String EXECUTE = "Execute";
  public static final String EXIT = "Exit";
  public static final String DEPOSIT = "Deposit";
  public static final String WITHDRAW = "Withdraw";


  private JComboBox cmbTransactionType;
  private JTextField txtTransactionAmount;
  private BusinessAccount account;
  public JLabel lblTransactionType, lblTransactionAmount,
  lblResult, lblResultValue;

  public AccountManager() {
    super("State Pattern - Example");
    account = new BusinessAccount("100 200 300 4000");

    cmbTransactionType = new JComboBox();
    cmbTransactionType.addItem(AccountManager.DEPOSIT);
    cmbTransactionType.addItem(AccountManager.WITHDRAW);

    txtTransactionAmount = new JTextField(10);

    lblTransactionType = new JLabel("Transaction Type:");
    lblTransactionAmount = new JLabel("Transaction Amount:");

    lblResult = new JLabel("Result:");
    lblResultValue =
      new JLabel(" Please click on Execute button");

    //Create the open button
    JButton openButton = new JButton(AccountManager.EXECUTE);

    openButton.setMnemonic(KeyEvent.VK_E);
    JButton exitButton = new JButton(AccountManager.EXIT);
    exitButton.setMnemonic(KeyEvent.VK_X);
    ButtonHandler objButtonHandler = new ButtonHandler(this);


    openButton.addActionListener(objButtonHandler);
    exitButton.addActionListener(new ButtonHandler());

    JPanel buttonPanel = new JPanel();

    //****************************************************
    GridBagLayout gridbag = new GridBagLayout();
    buttonPanel.setLayout(gridbag);
    GridBagConstraints gbc = new GridBagConstraints();

    buttonPanel.add(lblTransactionType);
    buttonPanel.add(cmbTransactionType);
    buttonPanel.add(lblTransactionAmount);
    buttonPanel.add(txtTransactionAmount);
    buttonPanel.add(lblResult);
    buttonPanel.add(lblResultValue);

    buttonPanel.add(openButton);
    buttonPanel.add(exitButton);

    gbc.insets.top = 5;
    gbc.insets.bottom = 5;
    gbc.insets.left = 5;
    gbc.insets.right = 5;

    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gridbag.setConstraints(lblTransactionType, gbc);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 1;
    gbc.gridy = 0;
    gridbag.setConstraints(cmbTransactionType, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gridbag.setConstraints(lblTransactionAmount, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gridbag.setConstraints(txtTransactionAmount, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gridbag.setConstraints(lblResult, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gridbag.setConstraints(lblResultValue, gbc);

    gbc.insets.left = 2;
    gbc.insets.right = 2;
    gbc.insets.top = 40;
    gbc.anchor = GridBagConstraints.EAST;

    gbc.gridx = 0;
    gbc.gridy = 5;
    gridbag.setConstraints(openButton, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 5;
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

  public static void main(String[] args) {
    JFrame frame = new AccountManager();

    frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        }
                           );

    //frame.pack();
    frame.setSize(400, 300);
    frame.setVisible(true);
  }
  public String getTransactionType() {
    return (String) cmbTransactionType.getSelectedItem();
  }
  public String getTransactionAmount() {
    return txtTransactionAmount.getText();
  }
  public BusinessAccount getAccount() {
    return account;
  }
  public void setResult(String searchResult) {
    lblResultValue.setText(searchResult);
  }

} // End of class AccountManager


class ButtonHandler implements ActionListener {
  AccountManager objAccountManager;
  public void actionPerformed(ActionEvent e) {
    String searchResult = null;
    boolean result = false;

    if (e.getActionCommand().equals(AccountManager.EXIT)) {
      System.exit(1);
    }
    if (e.getActionCommand().equals(AccountManager.EXECUTE)) {
      //get input values
      String type = objAccountManager.getTransactionType();

      String amount =
        objAccountManager.getTransactionAmount();

      if (type.equals(AccountManager.DEPOSIT)) {
        BusinessAccount account =
          objAccountManager.getAccount();
        result = account.deposit(
                   new Double(amount).doubleValue());
      }
      if (type.equals(AccountManager.WITHDRAW)) {
        BusinessAccount account =
          objAccountManager.getAccount();
        result = account.withdraw(
                   new Double(amount).doubleValue());
      }
      if (result) {
        searchResult =
          "Transaction Successful:: " + "Balance=" +
          objAccountManager.getAccount().
          getBalance();
      } else {
        searchResult =
          "Transaction Was Not Successful:: " +
          "Overdraw Limit Exceeded::" +
          "Balance=" +
          objAccountManager.getAccount().
          getBalance();
      }
      objAccountManager.setResult(searchResult);
    }

  }
  public ButtonHandler() {
  }
  public ButtonHandler(AccountManager inObjAccountManager) {
    objAccountManager = inObjAccountManager;
  }

} // End of class ButtonHandler

