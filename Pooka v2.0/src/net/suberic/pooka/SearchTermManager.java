package net.suberic.pooka;

import javax.mail.search.*;
import javax.mail.*;
import java.util.HashMap;
import java.util.Vector;
import java.text.DateFormat;

/**
 * This class generates SearchTerms from properties in the Pooka
 * VariableBundle.  It also will give out a list of property labels and
 * then will translate those labels and/or properties into SearchTerms.
 *
 * This class also handles the Filter properties and editors.  This might
 * get moved out of this class eventually.
 */
public class SearchTermManager {

  HashMap labelToPropertyMap;
  Vector termLabels;
  HashMap labelToOperationMap;
  Vector operationLabels;
  HashMap typeToLabelMap;

  DateFormat dateFormat;

  Class stringTermClass;
  Class flagTermClass;
  Class dateTermClass;

  String sourceProperty;

  public static String STRING_MATCH = "String";
  public static String BOOLEAN_MATCH = "Boolean";
  public static String DATE_MATCH = "Date";
  public static String HEADER_MATCH = "Header";

  // filter properties

  Vector displayFilterLabels;
  Vector backendFilterLabels;
  HashMap filterLabelToPropertyMap;
  HashMap filterClassToPropertyMap;

  /**
   * Default constructor.  Initializes the labelToPropertyMap and the
   * termLabels Vector from the Pooka property.
   */
  public SearchTermManager(String propertyName) {
    sourceProperty = propertyName;
    try {
      flagTermClass = Class.forName("javax.mail.search.FlagTerm");
      stringTermClass = Class.forName("javax.mail.search.StringTerm");
      dateTermClass = Class.forName("javax.mail.search.DateTerm");
    } catch (Exception e) { }
    createTermMaps(propertyName + ".searchTerms");
    createOperationMaps(propertyName + ".operations");
    createOperationTypeMaps(propertyName);

    createFilterMaps();

    dateFormat = new java.text.SimpleDateFormat(Pooka.getProperty(propertyName + ".dateFormat", "MM/dd/yyyy"));
  }

  /**
   * Creates the labelToOperationMap and operationLabels from the given
   * property, as well as the termLabels Vector.
   */
  private void createTermMaps(String propName) {
    Vector keys = Pooka.getResources().getPropertyAsVector(propName, "");
    termLabels = new Vector();
    if (keys != null) {
      labelToPropertyMap = new HashMap();
      for (int i = 0; i < keys.size(); i++) {
        String thisValue = propName + "." + (String) keys.elementAt(i);
        String thisLabel = Pooka.getProperty(thisValue + ".label", (String)keys.elementAt(i));
        labelToPropertyMap.put(thisLabel, thisValue);
        termLabels.add(thisLabel);
      }
    }
  }

  /**
   * Creates the labelToOperationMap and operationLabels from the given
   * propery.
   */
  private void createOperationMaps(String propName) {
    Vector keys = Pooka.getResources().getPropertyAsVector(propName, "");
    operationLabels = new Vector();
    if (keys != null) {
      labelToOperationMap = new HashMap();
      for (int i = 0; i < keys.size(); i++) {
        String thisValue = propName + "." + (String) keys.elementAt(i);
        String thisLabel = Pooka.getProperty(thisValue + ".label", (String)keys.elementAt(i));
        labelToOperationMap.put(thisLabel, thisValue);
        operationLabels.add(thisLabel);
      }
    }
  }

  /**
   * Creates the typeToLabelMap for the given property.
   */
  private void createOperationTypeMaps(String propName) {
    typeToLabelMap = new HashMap();
    Vector types = Pooka.getResources().getPropertyAsVector(propName + ".operationTypes", "");
    for (int i = 0; i < types.size(); i++) {
      String currentType = (String) types.elementAt(i);
      Vector currentList = Pooka.getResources().getPropertyAsVector(propName + ".operationTypes." + currentType, "");
      Vector labelList = new Vector();

      for (int j = 0; j < currentList.size(); j++) {
        labelList.add(Pooka.getProperty(propName + ".operations." + (String) currentList.elementAt(j) + ".label"));
      }

      typeToLabelMap.put(currentType, labelList);
    }
  }

  /**
   * Creates the filter properties.
   */
  public void createFilterMaps() {
    displayFilterLabels=new Vector();
    backendFilterLabels=new Vector();
    filterLabelToPropertyMap = new HashMap();
    filterClassToPropertyMap = new HashMap();

    Vector filterProperties = Pooka.getResources().getPropertyAsVector("FolderFilters.display", "");
    for (int i = 0; i < filterProperties.size(); i++) {
      String currentProperty = "FolderFilters.display." + (String) filterProperties.elementAt(i);
      String label = Pooka.getProperty(currentProperty + ".label", (String) filterProperties.elementAt(i));
      String className = Pooka.getProperty(currentProperty + ".class", "");
      displayFilterLabels.add(label);
      filterLabelToPropertyMap.put(label, currentProperty);
      filterClassToPropertyMap.put(className, currentProperty);
    }

    filterProperties = Pooka.getResources().getPropertyAsVector("FolderFilters.backend", "");
    for (int i = 0; i < filterProperties.size(); i++) {
      String currentProperty = "FolderFilters.backend." + (String) filterProperties.elementAt(i);
      String label = Pooka.getProperty(currentProperty + ".label", (String) filterProperties.elementAt(i));
      String className = Pooka.getProperty(currentProperty + ".class", "");
      backendFilterLabels.add(label);
      filterLabelToPropertyMap.put(label, currentProperty);
      filterClassToPropertyMap.put(className, currentProperty);
    }
  }

  /**
   * Generates a compound SearchTerm.
   */
  public SearchTerm generateCompoundSearchTerm(String[] properties, String operation) throws java.text.ParseException {
    SearchTerm[] terms = new SearchTerm[properties.length];
    for (int i = 0; i < properties.length; i++)
      terms[i] = generateSearchTermFromProperty(properties[i]);

    if (operation.equalsIgnoreCase("and"))
      return new AndTerm(terms);
    else if (operation.equalsIgnoreCase("or"))
      return new OrTerm(terms);
    else
      return null;
  }

  /**
   * Generates a SearchTerm from a single property root.  This method
   * expects the following sub-properties to be set on the given
   * property:
   *
   * property.type should be set either to 'compound' or 'single'
   *
   * for 'single' types:
   * property.searchTerm
   * property.operation (optional)
   * property.pattern (optional)
   *
   * for 'compound' types:
   * property.subTerms
   * property.operation (should be 'or' or 'and')
   */
  public SearchTerm generateSearchTermFromProperty(String property) throws java.text.ParseException {
    //System.out.println("generating search term for " + property);
    String type = Pooka.getProperty(property + ".type", "single");
    if (type.equalsIgnoreCase("single")) {
      String searchProperty = Pooka.getProperty(property + ".searchTerm", "");
      String operationProperty = Pooka.getProperty(property + ".operation", "");
      String pattern = Pooka.getProperty(property + ".pattern", "");
      String header = Pooka.getProperty(property + ".header", "");
      return generateSearchTerm(searchProperty, operationProperty, pattern, header);
    } else if (type.equalsIgnoreCase("compound")) {
      Vector subTermList = Pooka.getResources().getPropertyAsVector(property + ".subTerms", "");
      String[] subTerms = new String[subTermList.size()];
      for (int i = 0; i < subTerms.length; i++)
        subTerms[i] = (String) subTermList.elementAt(i);
      String operation = Pooka.getProperty(property + ".operation", "");

      return generateCompoundSearchTerm(subTerms, operation);
    } else
      return null;
  }

  /**
   * Generates a SearchTerm from the given property and pattern.
   *
   * This method used the .class subproperty of the given searchProperty
   * String to determine what type of SearchTerm to create.  If the
   * .class is an instance of FlagTerm, the .flag subproperty is used
   * to determine which flag to test.  If the .class is an instance
   * of StringTerm, then .ignoreCase is checked to see whether or not
   * to ignore case (default to false).
   *
   * This also uses the operationProperty to determine whether to make
   * this a positive or negative search (is or is not), or, in the case
   * of comparison searches, a greater than or less than search.
   *
   */
  public SearchTerm generateSearchTerm(String searchProperty, String operationProperty, String pattern) throws java.text.ParseException {
    return generateSearchTerm(searchProperty, operationProperty, pattern, "");
  }


  /**
   * Generates a SearchTerm from the given property and pattern.
   *
   * This method used the .class subproperty of the given searchProperty
   * String to determine what type of SearchTerm to create.  If the
   * .class is an instance of FlagTerm, the .flag subproperty is used
   * to determine which flag to test.  If the .class is an instance
   * of StringTerm, then .ignoreCase is checked to see whether or not
   * to ignore case (default to false).
   *
   * This also uses the operationProperty to determine whether to make
   * this a positive or negative search (is or is not), or, in the case
   * of comparison searches, a greater than or less than search.
   *
   */
  public SearchTerm generateSearchTerm(String searchProperty, String operationProperty, String pattern, String header) throws java.text.ParseException {
    SearchTerm term = null;
    try {
      String className = Pooka.getProperty(searchProperty + ".class", "");
      Class stClass = Class.forName(className);

      // ****** Create a StringTerm.
      if (stringTermClass.isAssignableFrom(stClass)) {
        boolean ignoreCase = Pooka.getProperty(searchProperty + ".ignoreCase", "false").equals("true");

        // check for the special cases.
        if (className.equals("javax.mail.search.RecipientStringTerm")) {
          String recipientType = Pooka.getProperty(searchProperty + ".recipientType", "to");
          if (recipientType.equalsIgnoreCase("to"))
            term = new RecipientStringTerm(javax.mail.Message.RecipientType.TO, pattern);
          else if (recipientType.equalsIgnoreCase("cc"))
            term = new RecipientStringTerm(javax.mail.Message.RecipientType.CC, pattern);
          else if (recipientType.equalsIgnoreCase("toorcc"))
            term = new OrTerm(new RecipientStringTerm(javax.mail.Message.RecipientType.CC, pattern), new RecipientStringTerm(javax.mail.Message.RecipientType.TO, pattern));

        } else if (className.equals("javax.mail.search.HeaderTerm")) {
          term = new HeaderTerm(header, pattern);
        } else {
          // default case for StringTerms

          java.lang.reflect.Constructor termConst = stClass.getConstructor(new Class[] {Class.forName("java.lang.String")});
          term = (SearchTerm) termConst.newInstance(new Object[] { pattern});

        }
      }

      // ********** Create a FlagTerm

      else if (flagTermClass.isAssignableFrom(stClass)) {
        term = new FlagTerm(getFlags(Pooka.getProperty(searchProperty + ".flag", "")), Pooka.getProperty(searchProperty + ".value", "true").equalsIgnoreCase("true"));
      }

      // ********** Create a DateTerm

      else if (dateTermClass.isAssignableFrom(stClass)) {

        java.util.Date compareDate = dateFormat.parse(pattern);

        int comparison = 0;

        String operationPropertyType = Pooka.getProperty(operationProperty, "");
        if (operationPropertyType.equalsIgnoreCase("equals") || operationPropertyType.equalsIgnoreCase("notEquals"))
          comparison = DateTerm.EQ;
        else if (operationPropertyType.equalsIgnoreCase("before"))
          comparison = DateTerm.LT;
        else if (operationPropertyType.equalsIgnoreCase("after"))
          comparison = DateTerm.GT;

        java.lang.reflect.Constructor termConst = stClass.getConstructor(new Class[] {Integer.TYPE , Class.forName("java.util.Date")});
        term = (SearchTerm) termConst.newInstance(new Object[] { new Integer(comparison), compareDate });
      }

      // ********** Default Case, no term known.

      else {
        // default case for any term.
        term = (SearchTerm) stClass.newInstance();
      }

      // *********** Handles not cases.

      String operationPropertyValue = Pooka.getProperty(operationProperty, "");
      if (operationPropertyValue.equalsIgnoreCase("not") || operationPropertyValue.equalsIgnoreCase("notEquals"))
        term = new NotTerm(term);
    } catch (ClassNotFoundException cnfe) {
      showError(Pooka.getProperty("error.search.generatingSearchTerm", "Error generating SearchTerm:  "), cnfe);
    } catch (NoSuchMethodException nsme) {
      showError(Pooka.getProperty("error.search.generatingSearchTerm", "Error generating SearchTerm:  "), nsme);
    } catch (InstantiationException ie) {
      showError(Pooka.getProperty("error.search.generatingSearchTerm", "Error generating SearchTerm:  "), ie);
    } catch (IllegalAccessException iae) {
      showError(Pooka.getProperty("error.search.generatingSearchTerm", "Error generating SearchTerm:  "), iae);
    } catch (java.lang.reflect.InvocationTargetException ite) {
      showError(Pooka.getProperty("error.search.generatingSearchTerm", "Error generating SearchTerm:  "), ite);
    }

    return term;
  }

  /**
   * This creates a javax.mail.Flags object containing the flag indicated
   * by flagName.
   */
  public Flags getFlags(String flagName) {
    if (flagName.equalsIgnoreCase("answered"))
      return new Flags(Flags.Flag.ANSWERED);
    else if (flagName.equalsIgnoreCase("deleted"))
      return new Flags(Flags.Flag.DELETED);
    else if (flagName.equalsIgnoreCase("draft"))
      return new Flags(Flags.Flag.DRAFT);
    else if (flagName.equalsIgnoreCase("flagged"))
      return new Flags(Flags.Flag.FLAGGED);
    else if (flagName.equalsIgnoreCase("recent"))
      return new Flags(Flags.Flag.RECENT);
    else if (flagName.equalsIgnoreCase("seen"))
      return new Flags(Flags.Flag.SEEN);

    return new Flags(flagName);
  }

  /**
   * Returns the available flag labels.
   */
  public Vector getFlagLabels() {
    // FIXME this isn't customizable or internationalized at all.
    Vector v = new Vector();
    v.add("flagged");
    v.add("seen");
    v.add("answered");
    v.add("deleted");
    v.add("draft");
    v.add("recent");
    return v;
  }

  /**
   * Returns the display filter labels.
   */
  public Vector getDisplayFilterLabels() {
    return displayFilterLabels;
  }

  /**
   * Returns the backend filter labels.
   */
  public Vector getBackendFilterLabels() {
    return backendFilterLabels;
  }

  /**
   * creates an editor for a given filter label.
   */
  public net.suberic.pooka.gui.filter.FilterEditor getEditorForFilterLabel(String label) {

    String property = (String) filterLabelToPropertyMap.get(label);
    String className = Pooka.getProperty(property + ".editorClass", "");
    if (className.equals(""))
      return null;
    else {
      try {
        Class editorClass = Class.forName(className);
        net.suberic.pooka.gui.filter.FilterEditor editor = (net.suberic.pooka.gui.filter.FilterEditor) editorClass.newInstance();
        return editor;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  /**
   * Returns the appropriate label for the given filter class.
   */
  public String getLabelForFilterClass(String className) {
    String property = (String) filterClassToPropertyMap.get(className);
    String label = Pooka.getProperty(property + ".label", "");
    return label;
  }

  /**
   * Shows an error message.
   */
  public void showError ( String message, Exception e ) {
    if (Pooka.getUIFactory() != null)
      Pooka.getUIFactory().showError(message, e);
    else {
      System.err.println(message + e.getMessage());
      e.printStackTrace();
    }
  }
  // accessor methods

  public HashMap getLabelToPropertyMap() {
    return labelToPropertyMap;
  }

  public Vector getTermLabels() {
    return termLabels;
  }

  public HashMap getLabelToOperationMap() {
    return labelToOperationMap;
  }

  public Vector getOperationLabels() {
    return operationLabels;
  }

  public Vector getOperationLabels(String operationType) {
    return (Vector) typeToLabelMap.get(operationType);
  }
}

