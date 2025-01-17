/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMStandin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.icu.text.MessageFormat;

/**
 * This is a container for VM definitions such as the VM definitions that are
 * stored in the workbench preferences.  
 * <p>
 * An instance of this class may be obtained from an XML document by calling
 * <code>parseXMLIntoContainer</code>.
 * </p>
 * <p>
 * An instance of this class may be translated into an XML document by calling
 * <code>getAsXML</code>.
 * </p>
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 */
public class VMDefinitionsContainer {
		
	/**
	 * Map of VMInstallTypes to Lists of corresponding VMInstalls.
	 */
	private Map fVMTypeToVMMap;
	
	/**
	 * Cached list of VMs in this container
	 */
	private List fVMList;
	
	/**
	 * VMs managed by this container whose install locations don't actually exist.
	 */
	private List fInvalidVMList;
			
	/**
	 * The composite identifier of the default VM.  This consists of the install type ID
	 * plus an ID for the VM.
	 */
	private String fDefaultVMInstallCompositeID;
	
	/**
	 * The identifier of the connector to use for the default VM.
	 */
	private String fDefaultVMInstallConnectorTypeID;
	
	/**
	 * Contains any error/information status of parsing XML
	 */
	private MultiStatus fStatus;
	
	/**
	 * Constructs an empty VM container 
	 */
	public VMDefinitionsContainer() {
		fVMTypeToVMMap = new HashMap(10);
		fInvalidVMList = new ArrayList(10);	
		fVMList = new ArrayList(10);		
	}
		
	/**
	 * Add the specified VM to the VM definitions managed by this container.
	 * <p>
	 * If distinguishing valid from invalid VMs is important, the specified VM must
	 * have already had its install location set.  An invalid VM is one whose install
	 * location doesn't exist.
	 * </p>
	 * 
	 * @param vm the VM to be added to this container
	 */
	public void addVM(IVMInstall vm) {
		if (!fVMList.contains(vm)) {	
			IVMInstallType vmInstallType = vm.getVMInstallType();
			List vmList = (List) fVMTypeToVMMap.get(vmInstallType);
			if (vmList == null) {
				vmList = new ArrayList(3);
				fVMTypeToVMMap.put(vmInstallType, vmList);			
			}
			vmList.add(vm);
			File installLocation = vm.getInstallLocation();
			if (installLocation == null || vmInstallType.validateInstallLocation(installLocation).getSeverity() == IStatus.ERROR) {
				fInvalidVMList.add(vm);
			}
			fVMList.add(vm);
		}
	}
	
	/**
	 * Add all VM's in the specified list to the VM definitions managed by this container.
	 * <p>
	 * If distinguishing valid from invalid VMs is important, the specified VMs must
	 * have already had their install locations set.  An invalid VM is one whose install
	 * location doesn't exist.
	 * </p>
	 * 
	 * @param vmList a list of VMs to be added to this container
	 */
	public void addVMList(List vmList) {
		Iterator iterator = vmList.iterator();
		while (iterator.hasNext()) {
			IVMInstall vm = (IVMInstall) iterator.next();
			addVM(vm);
		}
	}

	/**
	 * Return a mapping of VM install types to lists of VMs.  The keys of this map are instances of
	 * <code>IVMInstallType</code>.  The values are instances of <code>java.util.List</code>
	 * which contain instances of <code>IVMInstall</code>.  
	 * 
	 * @return Map the mapping of VM install types to lists of VMs
	 */
	public Map getVMTypeToVMMap() {
		return fVMTypeToVMMap;
	}
	
	/**
	 * Return a list of all VMs in this container, including any invalid VMs.  An invalid
	 * VM is one whose install location does not exist on the file system.
	 * The order of the list is not specified.
	 * 
	 * @return List the data structure containing all VMs managed by this container
	 */
	public List getVMList() {
		return fVMList;
	}
	
	/**
	 * Return a list of all valid VMs in this container.  A valid VM is one whose install
	 * location exists on the file system.  The order of the list is not specified.
	 * 
	 * @return List 
	 */
	public List getValidVMList() {
		List vms = getVMList();
		List resultList = new ArrayList(vms.size());
		resultList.addAll(vms);
		resultList.removeAll(fInvalidVMList);
		return resultList;
	}
	
	/**
	 * Returns the composite ID for the default VM.  The composite ID consists
	 * of an ID for the VM install type together with an ID for VM.  This is
	 * necessary because VM ids by themselves are not necessarily unique across
	 * VM install types.
	 * 
	 * @return String returns the composite ID of the current default VM
	 */
	public String getDefaultVMInstallCompositeID(){
		return fDefaultVMInstallCompositeID;
	}
	
	/**
	 * Sets the composite ID for the default VM.  The composite ID consists
	 * of an ID for the VM install type together with an ID for VM.  This is
	 * necessary because VM ids by themselves are not necessarily unique across
	 * VM install types.
	 * 
	 * @param id identifies the new default VM using a composite ID
	 */
	public void setDefaultVMInstallCompositeID(String id){
		fDefaultVMInstallCompositeID = id;
	}
	
	/**
	 * Return the default VM's connector type ID.
	 * 
	 * @return String the current value of the default VM's connector type ID
	 */
	public String getDefaultVMInstallConnectorTypeID() {
		return fDefaultVMInstallConnectorTypeID;
	}
	
	/**
	 * Set the default VM's connector type ID.
	 * 
	 * @param id the new value of the default VM's connector type ID
	 */
	public void  setDefaultVMInstallConnectorTypeID(String id){
		fDefaultVMInstallConnectorTypeID = id;
	}
	
	/**
	 * Return the VM definitions contained in this object as a String of XML.  The String
	 * is suitable for storing in the workbench preferences.
	 * <p>
	 * The resulting XML is compatible with the static method <code>parseXMLIntoContainer</code>.
	 * </p>
	 * @return String the results of flattening this object into XML
	 * @throws IOException if this method fails. Reasons include:<ul>
	 * <li>serialization of the XML document failed</li>
	 * </ul>
	 * @throws ParserConfigurationException if creation of the XML document failed
	 * @throws TransformerException if serialization of the XML document failed
	 */
	public String getAsXML() throws CoreException {
		
		// Create the Document and the top-level node
		Document doc = DebugPlugin.newDocument();
		Element config = doc.createElement("vmSettings");    //$NON-NLS-1$
		doc.appendChild(config);
		
		// Set the defaultVM attribute on the top-level node
		if (getDefaultVMInstallCompositeID() != null) {
			config.setAttribute("defaultVM", getDefaultVMInstallCompositeID()); //$NON-NLS-1$
		}
		
		// Set the defaultVMConnector attribute on the top-level node
		if (getDefaultVMInstallConnectorTypeID() != null) {
			config.setAttribute("defaultVMConnector", getDefaultVMInstallConnectorTypeID()); //$NON-NLS-1$
		}
				
		// Create a node for each install type represented in this container
		Set vmInstallTypeSet = getVMTypeToVMMap().keySet();
		Iterator keyIterator = vmInstallTypeSet.iterator();
		while (keyIterator.hasNext()) {
			IVMInstallType vmInstallType = (IVMInstallType) keyIterator.next();
			Element vmTypeElement = vmTypeAsElement(doc, vmInstallType);
			config.appendChild(vmTypeElement);
		}
		
		// Serialize the Document and return the resulting String
		return DebugPlugin.serializeDocument(doc);
	}
	
	/**
	 * Create and return a node for the specified VM install type in the specified Document.
	 */
	private Element vmTypeAsElement(Document doc, IVMInstallType vmType) {
		
		// Create a node for the vm type and set its 'id' attribute
		Element element= doc.createElement("vmType");   //$NON-NLS-1$
		element.setAttribute("id", vmType.getId());     //$NON-NLS-1$
		
		// For each vm of the specified type, create a subordinate node for it
		List vmList = (List) getVMTypeToVMMap().get(vmType);
		Iterator vmIterator = vmList.iterator();
		while (vmIterator.hasNext()) {
			IVMInstall vm = (IVMInstall) vmIterator.next();
			Element vmElement = vmAsElement(doc, vm);
			element.appendChild(vmElement);
		}
		
		return element;
	}
	
	/**
	 * Create and return a node for the specified VM in the specified Document.
	 */
	private Element vmAsElement(Document doc, IVMInstall vm) {
		
		// Create the node for the VM and set its 'id' & 'name' attributes
		Element element= doc.createElement("vm");        //$NON-NLS-1$
		element.setAttribute("id", vm.getId());	         //$NON-NLS-1$
		element.setAttribute("name", vm.getName());      //$NON-NLS-1$
		
		// Determine and set the 'path' attribute for the VM
		String installPath= "";                          //$NON-NLS-1$
		File installLocation= vm.getInstallLocation();
		if (installLocation != null) {
			installPath= installLocation.getAbsolutePath();
		}
		element.setAttribute("path", installPath);       //$NON-NLS-1$
		
		// If the 'libraryLocations' attribute is specified, create a node for it 
		LibraryLocation[] libraryLocations= vm.getLibraryLocations();
		if (libraryLocations != null) {
			Element libLocationElement = libraryLocationsAsElement(doc, libraryLocations);
			element.appendChild(libLocationElement);
		}
		
		// Java doc location
		URL url = vm.getJavadocLocation();
		if (url != null) {
			element.setAttribute("javadocURL", url.toExternalForm()); //$NON-NLS-1$
		}
		
		if (vm instanceof IVMInstall2) {
			String vmArgs = ((IVMInstall2)vm).getVMArgs();
			if (vmArgs != null && vmArgs.length() > 0) {
				element.setAttribute("vmargs", vmArgs); //$NON-NLS-1$
			}
		} else {
			String[] vmArgs = vm.getVMArguments();
			if (vmArgs != null && vmArgs.length > 0) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < vmArgs.length; i++) {
					buffer.append(vmArgs[i] + " "); //$NON-NLS-1$
				}
				element.setAttribute("vmargs", buffer.toString()); //$NON-NLS-1$
			}
		}
		
		// vm attributes
		if (vm instanceof AbstractVMInstall) {
			Map attributes = ((AbstractVMInstall)vm).getAttributes();
			if (!attributes.isEmpty()) {
				Element attrElement = doc.createElement("attributeMap"); //$NON-NLS-1$
				Iterator iterator = attributes.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					Element entryElement = doc.createElement("entry"); //$NON-NLS-1$
					entryElement.setAttribute("key", (String)entry.getKey()); //$NON-NLS-1$
					entryElement.setAttribute("value", (String)entry.getValue()); //$NON-NLS-1$
					attrElement.appendChild(entryElement);
				}
				element.appendChild(attrElement);
			}
		}
		
		return element;
	}
	
	/**
	 * Create and return a 'libraryLocations' node.  This node owns subordinate nodes that
	 * list individual library locations.
	 */
	private static Element libraryLocationsAsElement(Document doc, LibraryLocation[] locations) {
		Element root = doc.createElement("libraryLocations");       //$NON-NLS-1$
		for (int i = 0; i < locations.length; i++) {
			Element element = doc.createElement("libraryLocation");  //$NON-NLS-1$
			element.setAttribute("jreJar", locations[i].getSystemLibraryPath().toString()); //$NON-NLS-1$
			element.setAttribute("jreSrc", locations[i].getSystemLibrarySourcePath().toString()); //$NON-NLS-1$

			IPath packageRootPath = locations[i].getPackageRootPath();
            if (packageRootPath != null) {
                element.setAttribute("pkgRoot", packageRootPath.toString()); //$NON-NLS-1$
            }
            
			URL url= locations[i].getJavadocLocation();
			if (url != null) {
				element.setAttribute("jreJavadoc", url.toExternalForm()); //$NON-NLS-1$
			}
			root.appendChild(element);
		}
		return root;
	}
	
	public static VMDefinitionsContainer parseXMLIntoContainer(InputStream inputStream) throws IOException {
		VMDefinitionsContainer container = new VMDefinitionsContainer();
		parseXMLIntoContainer(inputStream, container);
		return container;
	}
			
	/**
	 * Parse the VM definitions contained in the specified InputStream into the
	 * specified container.
	 * <p>
	 * The VMs in the returned container are instances of <code>VMStandin</code>.
	 * </p>
	 * <p>
	 * This method has no side-effects.  That is, no notifications are sent for VM adds,
	 * changes, deletes, and the workbench preferences are not affected.
	 * </p>
	 * <p>
	 * If the <code>getAsXML</code> method is called on the returned container object,
	 * the resulting XML will be semantically equivalent (though not necessarily syntactically equivalent) as
	 * the XML contained in <code>inputStream</code>.
	 * </p>
	 * @param inputStream the <code>InputStream</code> containing XML that declares a set of VMs and a default VM
	 * @param container the container to add the VM definitions to
	 * @throws IOException if this method fails. Reasons include:<ul>
	 * <li>the XML in <code>inputStream</code> was badly formatted</li>
	 * <li>the top-level node was not 'vmSettings'</li>
	 * </ul>
	 * @since 3.2
	 */
	public static void parseXMLIntoContainer(InputStream inputStream, VMDefinitionsContainer container) throws IOException {

		// Wrapper the stream for efficient parsing
		InputStream stream= new BufferedInputStream(inputStream);

		// Do the parsing and obtain the top-level node
		Element config= null;		
		try {
			DocumentBuilder parser= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			config = parser.parse(new InputSource(stream)).getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(LaunchingMessages.JavaRuntime_badFormat); 
		} catch (ParserConfigurationException e) {
			stream.close();
			throw new IOException(LaunchingMessages.JavaRuntime_badFormat); 
		} finally {
			stream.close();
		}
		
		// If the top-level node wasn't what we expected, bail out
		if (!config.getNodeName().equalsIgnoreCase("vmSettings")) { //$NON-NLS-1$
			throw new IOException(LaunchingMessages.JavaRuntime_badFormat); 
		}
		
		// Populate the default VM-related fields
		container.setDefaultVMInstallCompositeID(config.getAttribute("defaultVM")); //$NON-NLS-1$
		container.setDefaultVMInstallConnectorTypeID(config.getAttribute("defaultVMConnector")); //$NON-NLS-1$
		
		// Traverse the parsed structure and populate the VMType to VM Map
		NodeList list = config.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element vmTypeElement = (Element) node;
				if (vmTypeElement.getNodeName().equalsIgnoreCase("vmType")) { //$NON-NLS-1$
					populateVMTypes(vmTypeElement, container);
				}
			}
		}
	}
	
	/**
	 * For the specified vm type node, parse all subordinate VM definitions and add them
	 * to the specified container.
	 */
	private static void populateVMTypes(Element vmTypeElement, VMDefinitionsContainer container) {
		// Retrieve the 'id' attribute and the corresponding VM type object
		String id = vmTypeElement.getAttribute("id");         //$NON-NLS-1$
		IVMInstallType vmType= JavaRuntime.getVMInstallType(id);
		if (vmType != null) {
			// For each VM child node, populate the container with a subordinate node
			NodeList vmNodeList = vmTypeElement.getElementsByTagName("vm"); //$NON-NLS-1$
			for (int i = 0; i < vmNodeList.getLength(); ++i) {
				populateVMForType(vmType, (Element) vmNodeList.item(i), container);
			}
		} else {
			// status information for removed VMs (missing VM type)
			NodeList vmNodeList = vmTypeElement.getElementsByTagName("vm"); //$NON-NLS-1$
			for (int i = 0; i < vmNodeList.getLength(); ++i) {
				Element vmElement = (Element) vmNodeList.item(i);
				String installPath= vmElement.getAttribute("path"); //$NON-NLS-1$
				String name = vmElement.getAttribute("name"); //$NON-NLS-1$
				IStatus status = null;
				if (name != null) {
					status = new Status(IStatus.INFO, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_0, new String[]{name}));
				} else if (installPath != null) {
					status = new Status(IStatus.INFO, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_0, new String[]{installPath}));
				} else {
					status = new Status(IStatus.INFO, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_2, new String[]{id}));
				}
				container.addStatus(status);
			}
		}
	}

	/**
	 * Parse the specified VM node, create a VMStandin for it, and add this to the 
	 * specified container.
	 * 
	 * @param vmType vm type
	 * @param vmElement XML element
	 * @param container container to add VM to
	 */
	private static void populateVMForType(IVMInstallType vmType, Element vmElement, VMDefinitionsContainer container) {
		String id= vmElement.getAttribute("id"); //$NON-NLS-1$
		if (id != null) {
			
			// Retrieve the 'path' attribute.  If none, skip this node.
			String installPath= vmElement.getAttribute("path"); //$NON-NLS-1$
			String name = vmElement.getAttribute("name"); //$NON-NLS-1$
			if (name == null) {
				if (installPath == null) {
					container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_3, new String[]{vmType.getName()})));
					return;
				} else {
					container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_4, new String[]{installPath})));
					return;
				}
			}
			if (installPath == null) {
				container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
						MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_5, new String[]{name})));
				return;
			}
						
			// Create a VMStandin for the node and set its 'name' & 'installLocation' attributes
			VMStandin vmStandin = new VMStandin(vmType, id);
			vmStandin.setName(name);
			File installLocation= new File(installPath);
			vmStandin.setInstallLocation(installLocation);
			container.addVM(vmStandin);
			
			// Look for subordinate nodes.  These may be 'libraryLocation',
			// 'libraryLocations' or 'versionInfo'.
			NodeList list = vmElement.getChildNodes();
			int length = list.getLength();
			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				short type = node.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element subElement = (Element)node;
					String subElementName = subElement.getNodeName();
					if (subElementName.equals("libraryLocation")) { //$NON-NLS-1$
						LibraryLocation loc = getLibraryLocation(subElement);
						vmStandin.setLibraryLocations(new LibraryLocation[]{loc});
					} else if (subElementName.equals("libraryLocations")) { //$NON-NLS-1$
						setLibraryLocations(vmStandin, subElement);
					} else if (subElementName.equals("attributeMap")) { //$NON-NLS-1$
						NodeList entries = subElement.getElementsByTagName("entry"); //$NON-NLS-1$
						for (int j = 0; j < entries.getLength(); j++) {
							Node entryNode = entries.item(j);
							if (entryNode instanceof Element) {
								Element entryElement = (Element) entryNode;
								String key = entryElement.getAttribute("key"); //$NON-NLS-1$
								String value = entryElement.getAttribute("value"); //$NON-NLS-1$
								if (key != null && value != null) {
									vmStandin.setAttribute(key, value);
								}
							}
						}
					}
				}
			}
			
			// javadoc URL
			String externalForm = vmElement.getAttribute("javadocURL"); //$NON-NLS-1$
			if (externalForm != null && externalForm.length() > 0) {
				try {
					vmStandin.setJavadocLocation(new URL(externalForm));
				} catch (MalformedURLException e) {
					container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
							MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_6, new String[]{name}), e));
				}
			}
			
			// vm Arguments
			String vmArgs = vmElement.getAttribute("vmargs"); //$NON-NLS-1$
			if (vmArgs != null && vmArgs.length() >0) {
				vmStandin.setVMArgs(vmArgs);
			}

		} else {
			String installPath= vmElement.getAttribute("path"); //$NON-NLS-1$
			String name = vmElement.getAttribute("name"); //$NON-NLS-1$
			if (name != null) {
				container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
						MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_7, new String[]{name})));
			} else if (installPath != null) {
				container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
						MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_7, new String[]{installPath})));
			} else {
				container.addStatus(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN,
					MessageFormat.format(LaunchingMessages.VMDefinitionsContainer_9, new String[]{vmType.getName()})));
			}
		}
	}	
	
	/**
	 * Create & return a LibraryLocation object populated from the attribute values
	 * in the specified node.
	 */
	private static LibraryLocation getLibraryLocation(Element libLocationElement) {
		String jreJar= libLocationElement.getAttribute("jreJar"); //$NON-NLS-1$
		String jreSrc= libLocationElement.getAttribute("jreSrc"); //$NON-NLS-1$
		String pkgRoot= libLocationElement.getAttribute("pkgRoot"); //$NON-NLS-1$
		String jreJavadoc= libLocationElement.getAttribute("jreJavadoc"); //$NON-NLS-1$
		URL javadocURL= null;
		if (jreJavadoc.length() == 0) {
			jreJavadoc= null;
		} else {
			try {
				javadocURL= new URL(jreJavadoc);
			} catch (MalformedURLException e) {
				LaunchingPlugin.log("Library location element is specified incorrectly.");  //$NON-NLS-1$
			}
		}
		if (jreJar != null && jreSrc != null && pkgRoot != null) {
			return new LibraryLocation(new Path(jreJar), new Path(jreSrc), new Path(pkgRoot), javadocURL);
		}
		LaunchingPlugin.log("Library location element is specified incorrectly.");  //$NON-NLS-1$
		return null;
	}
	
	/**
	 * Set the LibraryLocations on the specified VM, by extracting the subordinate
	 * nodes from the specified 'lirbaryLocations' node.
	 */
	private static void setLibraryLocations(IVMInstall vm, Element libLocationsElement) {
		NodeList list = libLocationsElement.getChildNodes();
		int length = list.getLength();
		List locations = new ArrayList(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element libraryLocationElement= (Element)node;
				if (libraryLocationElement.getNodeName().equals("libraryLocation")) { //$NON-NLS-1$
					locations.add(getLibraryLocation(libraryLocationElement));
				}
			}
		}	
		vm.setLibraryLocations((LibraryLocation[])locations.toArray(new LibraryLocation[locations.size()]));
	}
	
	/**
	 * Removes the VM from this container.
	 * 
	 * @param vm vm install
	 */
	public void removeVM(IVMInstall vm) {
		fVMList.remove(vm);
		fInvalidVMList.remove(vm);
		List list = (List) fVMTypeToVMMap.get(vm.getVMInstallType());
		if (list != null) {
			list.remove(vm);
		}
	}
		
	private void addStatus(IStatus status) {
		if (fStatus == null) {
			fStatus = new MultiStatus(LaunchingPlugin.ID_PLUGIN, 0, LaunchingMessages.VMDefinitionsContainer_10, null);
		}
		fStatus.add(status);
	}

	/**
	 * Returns status from parsing VM installs or <code>null</code> if none.
	 * 
	 * @return status or <code>null</code>
	 */
	public IStatus getStatus() {
		return fStatus;
	}
}
