package src.AbstractFactory;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class AutoSearchUI extends JFrame {
  public static final String newline = "\n";
  public static final String SEARCH = "Search";
  public static final String EXIT = "Exit";
  public static final String CAR = "Car";
  public static final String SUV = "SUV";


  private JComboBox cmbVehicleCategory, cmbVehicleType;

  private JLabel lblVehicleCategory, lblVehicleType,
  lblCarName, lblCarNameValue;

  public AutoSearchUI() {
    super("Abstract Factory - Example");

    cmbVehicleCategory = new JComboBox();
    cmbVehicleCategory.addItem(VehicleFactory.LUXURY_VEHICLE);
    cmbVehicleCategory.addItem(
      VehicleFactory.NON_LUXURY_VEHICLE);

    cmbVehicleType = new JComboBox();
    cmbVehicleType.addItem(AutoSearchUI.CAR);
    cmbVehicleType.addItem(AutoSearchUI.SUV);

    lblVehicleCategory = new JLabel("Vehicle Category:");
    lblVehicleType = new JLabel("VehicleType:");
    lblCarName = new JLabel("Search Result:");
    lblCarNameValue =
      new JLabel(" Please click on Search button");

    //Create the open button
    JButton openButton = new JButton(AutoSearchUI.SEARCH);
    openButton.setMnemonic(KeyEvent.VK_S);
    JButton exitButton = new JButton(AutoSearchUI.EXIT);
    exitButton.setMnemonic(KeyEvent.VK_X);
    ButtonHandler objButtonHandler = new ButtonHandler(this);


    openButton.addActionListener(objButtonHandler);
    exitButton.addActionListener(new ButtonHandler());

    JPanel buttonPanel = new JPanel();

    //****************************************************
    GridBagLayout gridbag = new GridBagLayout();
    buttonPanel.setLayout(gridbag);
    GridBagConstraints gbc = new GridBagConstraints();

    buttonPanel.add(lblVehicleCategory);
    buttonPanel.add(cmbVehicleCategory);
    buttonPanel.add(lblVehicleType);
    buttonPanel.add(cmbVehicleType);
    buttonPanel.add(lblCarName);
    buttonPanel.add(lblCarNameValue);

    buttonPanel.add(openButton);
    buttonPanel.add(exitButton);

    gbc.insets.top = 5;
    gbc.insets.bottom = 5;
    gbc.insets.left = 5;
    gbc.insets.right = 5;

    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gridbag.setConstraints(lblVehicleCategory, gbc);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 1;
    gbc.gridy = 0;
    gridbag.setConstraints(cmbVehicleCategory, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gridbag.setConstraints(lblVehicleType, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gridbag.setConstraints(cmbVehicleType, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gridbag.setConstraints(lblCarName, gbc);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gridbag.setConstraints(lblCarNameValue, gbc);

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
        AutoSearchUI.this);
    } catch (Exception ex) {
      System.out.println(ex);
    }

  }

  public static void main(String[] args) {
    JFrame frame = new AutoSearchUI();

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
  public String getSelectedCategory() {
    return (String) cmbVehicleCategory.getSelectedItem();
  }
  public String getSelectedType() {
    return (String) cmbVehicleType.getSelectedItem();
  }
  public void setResult(String searchResult) {
    lblCarNameValue.setText(searchResult);
  }
} // End of class AutoSearchUI



class ButtonHandler implements ActionListener {
  AutoSearchUI objAutoSearchUI;
  public void actionPerformed(ActionEvent e) {
    String searchResult = null;

    if (e.getActionCommand().equals(AutoSearchUI.EXIT)) {
      System.exit(1);
    }
    if (e.getActionCommand().equals(AutoSearchUI.SEARCH)) {
      //get input values
      String vhCategory =
        objAutoSearchUI.getSelectedCategory();
      String vhType = objAutoSearchUI.getSelectedType();

      //get one of Luxury or NonLuxury vehicle factories
      VehicleFactory vf =
        (VehicleFactory) VehicleFactory.getVehicleFactory(vhCategory);

      if (vhType.equals(AutoSearchUI.CAR)) {
        Car c = vf.getCar();
        searchResult =
          "Name: " + c.getCarName() + "  Features: " +
          c.getCarFeatures();
      }
      if (vhType.equals(AutoSearchUI.SUV)) {
        SUV s = vf.getSUV();
        searchResult =
          "Name: " + s.getSUVName() + "  Features: " +
          s.getSUVFeatures();
      }
      objAutoSearchUI.setResult(searchResult);
    }

  }
  public ButtonHandler() {
  }
  public ButtonHandler(AutoSearchUI inObjAutoSearchUI) {
    objAutoSearchUI = inObjAutoSearchUI;
  }

} // End of class ButtonHandler

