package net.suberic.pooka.gui.search;
import javax.swing.*;
import javax.swing.event.*;
import javax.mail.search.*;
import java.util.*;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.SearchTermManager;
import net.suberic.util.VariableBundle;

/**
 * This is a gui component which lets you configure a 
 * javax.mail.search.SearchTerm.  It can be used either to configure mail
 * filters, or to search folders.
 */
public class SearchEntryForm implements java.awt.event.ItemListener {
    // ui objects
    private Box panel;
    private JComboBox searchFieldCombo;
    private JPanel selectionPanel; 

    private Box stringMatchPanel;
    private JComboBox operationCombo;
    private JTextField textField;

    private Box booleanPanel;
    private JComboBox booleanValueCombo;

    private Box datePanel;
    private JComboBox dateComparisonCombo;
    private JTextField dateField;
    private java.awt.CardLayout layout;

    private Box headerMatchPanel;
    private JComboBox headerOperationCombo;
    private JTextField headerNameTextField;
    private JTextField headerValueTextField;

    // the source SearchTermManager.
    SearchTermManager manager;

    /**
     * Constructs a new SearchEntryForm.
     */
    public SearchEntryForm(SearchTermManager newManager) { 
	manager = newManager;
	panel = new Box(BoxLayout.X_AXIS);

	searchFieldCombo = new JComboBox(manager.getTermLabels());
	searchFieldCombo.setPreferredSize(searchFieldCombo.getMinimumSize());
	searchFieldCombo.setMaximumSize(searchFieldCombo.getMinimumSize());

	searchFieldCombo.addItemListener( this );

	createPanels();

	selectionPanel = new JPanel();
	layout = new java.awt.CardLayout();
	selectionPanel.setLayout(layout);
	selectionPanel.add(stringMatchPanel, SearchTermManager.STRING_MATCH);
	selectionPanel.add(booleanPanel, SearchTermManager.BOOLEAN_MATCH);
	selectionPanel.add(datePanel, SearchTermManager.DATE_MATCH);
	selectionPanel.add(headerMatchPanel, SearchTermManager.HEADER_MATCH);
	panel.add(searchFieldCombo);
	panel.add(selectionPanel);

    }

    /**
     * Constructs a new SearchEntryForm using the given property and
     * VariableBundle.
     */
    public SearchEntryForm(SearchTermManager newManager, String rootProperty, VariableBundle bundle) { 
	this(newManager);
	setTermValue(rootProperty, bundle);
    }
  
  /**
   * Creates all of the selection panels.
   */
  public void createPanels() {

    Vector operationFields = manager.getOperationLabels(SearchTermManager.STRING_MATCH);

    headerMatchPanel = new Box(BoxLayout.X_AXIS);
    headerOperationCombo = new JComboBox(operationFields);
    headerOperationCombo.setPreferredSize(headerOperationCombo.getMinimumSize());
    headerOperationCombo.setMaximumSize(headerOperationCombo.getMinimumSize());

    headerNameTextField = new JTextField(10);
    headerNameTextField.setMaximumSize(new java.awt.Dimension(500, headerNameTextField.getPreferredSize().height));

    headerValueTextField = new JTextField(15);
    headerValueTextField.setMaximumSize(new java.awt.Dimension(1000, headerValueTextField.getPreferredSize().height));

    headerMatchPanel.add(headerNameTextField);
    headerMatchPanel.add(headerOperationCombo);
    headerMatchPanel.add(headerValueTextField);
    headerMatchPanel.add(Box.createGlue());

    stringMatchPanel = new Box(BoxLayout.X_AXIS);
    operationCombo = new JComboBox(operationFields);
    operationCombo.setPreferredSize(operationCombo.getMinimumSize());
    operationCombo.setMaximumSize(operationCombo.getMinimumSize());
    textField = new JTextField(20);
    textField.setMaximumSize(new java.awt.Dimension(1000, textField.getPreferredSize().height));
    
    stringMatchPanel.add(operationCombo);
    stringMatchPanel.add(textField);
    stringMatchPanel.add(Box.createGlue());
    
    booleanPanel = new Box(BoxLayout.X_AXIS);
    Vector booleanFields = manager.getOperationLabels(SearchTermManager.BOOLEAN_MATCH);
    booleanValueCombo = new JComboBox(booleanFields);
    booleanValueCombo.setPreferredSize(booleanValueCombo.getMinimumSize());
    booleanValueCombo.setMaximumSize(booleanValueCombo.getMinimumSize());
    booleanPanel.add(booleanValueCombo);
    booleanPanel.add(Box.createGlue());
    
    datePanel = new Box(BoxLayout.X_AXIS);
    
    Vector dateFields = manager.getOperationLabels(SearchTermManager.DATE_MATCH);
    dateComparisonCombo = new JComboBox(dateFields);
    dateComparisonCombo.setPreferredSize(dateComparisonCombo.getMinimumSize());
    dateComparisonCombo.setMaximumSize(dateComparisonCombo.getMinimumSize());
    dateField = new JTextField(10);
    dateField.setMaximumSize(new java.awt.Dimension(1000, dateField.getPreferredSize().height));
    
    JLabel dateFormatLabel = new JLabel(Pooka.getProperty("Search.dateFormat", "mm/dd/yyyy"));
    datePanel.add(dateComparisonCombo);
    datePanel.add(dateField);
    datePanel.add(dateFormatLabel);
    datePanel.add(Box.createGlue());
  }

    /**
     * Returns the Box which shows the SearchEntryForm.
     */ 
    public Box getPanel() {
	return panel;
    }

    /**
     * This generates a SearchTerm from the information on the Box, and
     * returns that value.
     */
    public SearchTerm generateSearchTerm() throws java.text.ParseException {
	if (Pooka.isDebug())
	    System.out.println("SearchEntryForm:  generating SearchTerm from " + searchFieldCombo.getSelectedItem() + " and " + operationCombo.getSelectedItem());

	String searchProperty = (String)(manager.getLabelToPropertyMap().get(searchFieldCombo.getSelectedItem()));
	String selectedType = Pooka.getProperty(searchProperty + ".type", "");
	String operationProperty = null;
	String pattern = null;
	String header = null;

	if (selectedType.equalsIgnoreCase(SearchTermManager.HEADER_MATCH)) {
	  operationProperty = (String)(manager.getLabelToOperationMap().get(headerOperationCombo.getSelectedItem()));
	  header = headerNameTextField.getText();
	  pattern = headerValueTextField.getText();
	  if (Pooka.isDebug())
	    System.out.println("using " + searchProperty + ", " + operationProperty + ", " + header + ", " + pattern);

	} else if (selectedType.equalsIgnoreCase(SearchTermManager.STRING_MATCH)) {
	    operationProperty = (String)(manager.getLabelToOperationMap().get(operationCombo.getSelectedItem()));
	    pattern = textField.getText();
	    if (Pooka.isDebug())
		System.out.println("using " + searchProperty + ", " + operationProperty + ", " + pattern);

	} else 	if (selectedType.equalsIgnoreCase(SearchTermManager.BOOLEAN_MATCH)) {
	    operationProperty = (String)(manager.getLabelToOperationMap().get(booleanValueCombo.getSelectedItem()));
	    if (Pooka.isDebug())
		System.out.println("using " + searchProperty + ", " + operationProperty);
	    pattern = null;

	} else	if (selectedType.equalsIgnoreCase(SearchTermManager.DATE_MATCH)) {
	    operationProperty = (String)(manager.getLabelToOperationMap().get(dateComparisonCombo.getSelectedItem()));
	    if (Pooka.isDebug())
		System.out.println("using " + searchProperty + ", " + operationProperty);
	    pattern = dateField.getText();
	}
	
	return manager.generateSearchTerm(searchProperty, operationProperty, pattern, header);
    }

    /**
     * This generates the Properties for the given SearchTerm.
     */
    public java.util.Properties generateSearchTermProperties(String rootProperty) {
      if (Pooka.isDebug())
	System.out.println("SearchEntryForm:  generating SearchTerm property from " + searchFieldCombo.getSelectedItem() + " and " + operationCombo.getSelectedItem() + " using rootProperty " + rootProperty);
      
      String searchProperty = (String)(manager.getLabelToPropertyMap().get(searchFieldCombo.getSelectedItem()));
      String selectedType = Pooka.getProperty(searchProperty + ".type", "");
      String operationProperty = null;
      String pattern = null;
      String header = null;
      
      if (selectedType.equalsIgnoreCase(SearchTermManager.HEADER_MATCH)) {
	operationProperty = (String)(manager.getLabelToOperationMap().get(headerOperationCombo.getSelectedItem()));
	if (Pooka.isDebug())
	  System.out.println("using " + searchProperty + ", " + operationProperty);
	pattern = headerValueTextField.getText();
	header = headerNameTextField.getText();
	
      } else if (selectedType.equalsIgnoreCase(SearchTermManager.STRING_MATCH)) {
	operationProperty = (String)(manager.getLabelToOperationMap().get(operationCombo.getSelectedItem()));
	if (Pooka.isDebug())
	  System.out.println("using " + searchProperty + ", " + operationProperty);
	    pattern = textField.getText();

	} else 	if (selectedType.equalsIgnoreCase(SearchTermManager.BOOLEAN_MATCH)) {
	    operationProperty = (String)(manager.getLabelToOperationMap().get(booleanValueCombo.getSelectedItem()));
	    if (Pooka.isDebug())
		System.out.println("using " + searchProperty + ", " + operationProperty);
	    pattern = null;

	} else	if (selectedType.equalsIgnoreCase(SearchTermManager.DATE_MATCH)) {
	    operationProperty = (String)(manager.getLabelToOperationMap().get(dateComparisonCombo.getSelectedItem()));
	    if (Pooka.isDebug())
		System.out.println("using " + searchProperty + ", " + operationProperty);
	    pattern = dateField.getText();
	}
	
	Properties returnValue = new java.util.Properties();
	if (searchProperty != null)
	    returnValue.setProperty(rootProperty + ".searchTerm", searchProperty);
	if (operationProperty != null)
	    returnValue.setProperty(rootProperty + ".operation", operationProperty);
	if (pattern != null)
	    returnValue.setProperty(rootProperty + ".pattern", pattern);

	if (header != null)
	    returnValue.setProperty(rootProperty + ".header", header);
	returnValue.setProperty(rootProperty + ".type", "single");

	return returnValue;
    }

    /**
     * Sets the value of this SearchTerm to the one defined by the
     * given property.
     */
    public void setTermValue(String rootProperty, VariableBundle bundle) {
	if (Pooka.isDebug())
	    System.out.println("SearchEntryForm:  setting SearchTerm value to that defined by " + rootProperty);
	
	String searchProperty = bundle.getProperty(rootProperty + ".searchTerm", "Search.searchTerms.Subject");
	if (Pooka.isDebug())
	    System.out.println("getting searchProperty from " + rootProperty + ".searchTerm; value is " + searchProperty);
	
	String selectedType = Pooka.getProperty(searchProperty + ".type", "");
	if (Pooka.isDebug())
	    System.out.println("getting selectedType from " + searchProperty + ".type; value is " + selectedType);
	String typeLabel = Pooka.getProperty(searchProperty + ".label", "Subject");
	if (Pooka.isDebug())
	    System.out.println("getting typeLabel from " + searchProperty + ".label; value is " + typeLabel);
	String operationProperty = bundle.getProperty(rootProperty + ".operation", "Search.operations.Contains");
	if (Pooka.isDebug())
	    System.out.println("getting operationProperty from " + rootProperty + ".operation; value is " + operationProperty);
	String operationLabel = Pooka.getProperty(operationProperty + ".label");
	if (Pooka.isDebug())
	    System.out.println("got operationLabel:  " + operationLabel);
	
	String pattern = bundle.getProperty(rootProperty + ".pattern", "");
	if (Pooka.isDebug())
	    System.out.println("got pattern from " + rootProperty + ".pattern; value is " + pattern);

	String header = bundle.getProperty(rootProperty + ".header", "");
	if (Pooka.isDebug())
	    System.out.println("got header from " + rootProperty + ".header; value is " + header);
	
	if (selectedType.equalsIgnoreCase(SearchTermManager.HEADER_MATCH)) {
	    if (Pooka.isDebug())
		System.out.println("selectedType is header_match.");
	    headerOperationCombo.setSelectedItem(operationLabel);
	    headerValueTextField.setText(pattern);
	    headerNameTextField.setText(header);

	} else if (selectedType.equalsIgnoreCase(SearchTermManager.STRING_MATCH)) {
	    if (Pooka.isDebug())
		System.out.println("selectedType is string_match.");
	    operationCombo.setSelectedItem(operationLabel);
	    textField.setText(pattern);
	} else if (selectedType.equalsIgnoreCase(SearchTermManager.BOOLEAN_MATCH)) {
	    if (Pooka.isDebug())
		System.out.println("selectedType is boolean_match.");
	    booleanValueCombo.setSelectedItem(operationLabel);
	} else	if (selectedType.equalsIgnoreCase(SearchTermManager.DATE_MATCH)) {
	    if (Pooka.isDebug())
		System.out.println("selectedType is date_match.");
	    dateComparisonCombo.setSelectedItem(operationLabel);
	    dateField.setText(pattern);
	}
	
	searchFieldCombo.setSelectedItem(typeLabel);
	
    }

    /**
     * This enables or disables the SearchEntryForm.
     */
    public void setEnabled(boolean newValue) {
	searchFieldCombo.setEnabled(newValue);

	stringMatchPanel.setEnabled(newValue);
	operationCombo.setEnabled(newValue);
	textField.setEnabled(newValue);

	booleanValueCombo.setEnabled(newValue);

	dateComparisonCombo.setEnabled(newValue);
	dateField.setEnabled(newValue);

	headerMatchPanel.setEnabled(true);
	headerOperationCombo.setEnabled(newValue);
	headerNameTextField.setEnabled(newValue);
	headerValueTextField.setEnabled(newValue);
    }

    /**
     * This handles the switch of the selectionPanel when the searchFieldCombo
     * value changes.
     */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
	String selectedString = (String)(manager.getLabelToPropertyMap().get(searchFieldCombo.getSelectedItem()));
	
	String selectedType = Pooka.getProperty(selectedString + ".type", "");
	layout.show(selectionPanel, selectedType);
    }
}




