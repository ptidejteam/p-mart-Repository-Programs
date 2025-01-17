// $Id: MDRModelImplementation.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
// Copyright (c) 2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.model.mdr;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.MalformedXMIException;

import org.apache.log4j.Logger;
import org.argouml.model.ActivityGraphsFactory;
import org.argouml.model.ActivityGraphsHelper;
import org.argouml.model.AggregationKind;
import org.argouml.model.ChangeableKind;
import org.argouml.model.CollaborationsFactory;
import org.argouml.model.CollaborationsHelper;
import org.argouml.model.CommonBehaviorFactory;
import org.argouml.model.CommonBehaviorHelper;
import org.argouml.model.ConcurrencyKind;
import org.argouml.model.CoreFactory;
import org.argouml.model.CoreHelper;
import org.argouml.model.DataTypesFactory;
import org.argouml.model.DataTypesHelper;
import org.argouml.model.DiagramInterchangeModel;
import org.argouml.model.DirectionKind;
import org.argouml.model.EventAdapter;
import org.argouml.model.ExtensionMechanismsFactory;
import org.argouml.model.ExtensionMechanismsHelper;
import org.argouml.model.Facade;
import org.argouml.model.MementoCreationObserver;
import org.argouml.model.MetaTypes;
import org.argouml.model.ModelEventPump;
import org.argouml.model.ModelImplementation;
import org.argouml.model.ModelManagementFactory;
import org.argouml.model.ModelManagementHelper;
import org.argouml.model.OrderingKind;
import org.argouml.model.PseudostateKind;
import org.argouml.model.ScopeKind;
import org.argouml.model.StateMachinesFactory;
import org.argouml.model.StateMachinesHelper;
import org.argouml.model.UmlException;
import org.argouml.model.UmlFactory;
import org.argouml.model.UmlHelper;
import org.argouml.model.UseCasesFactory;
import org.argouml.model.UseCasesHelper;
import org.argouml.model.VisibilityKind;
import org.argouml.model.XmiReader;
import org.argouml.model.XmiWriter;
import org.netbeans.api.mdr.CreationFailedException;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReader;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.omg.uml.UmlPackage;

/**
 * The handle to find all helper and factories.
 */
public class MDRModelImplementation implements ModelImplementation {
    
    private static final Logger LOG = 
        Logger.getLogger(MDRModelImplementation.class);

    private Facade theFacade;

    private ModelEventPumpMDRImpl theModelEventPump;

    private CopyHelper theCopyHelper;

    private ActivityGraphsHelper theActivityGraphsHelper;

    private CoreHelper theCoreHelper;

    private MetaTypes theMetaTypes = new MetaTypesMDRImpl();

    private ModelManagementFactory theModelManagementFactory;

    private ModelManagementHelper theModelManagementHelper;

    private StateMachinesHelper theStateMachinesHelper;

    private UmlFactory theUmlFactory;

    private UmlHelper theUmlHelper;

    private UseCasesFactory theUseCasesFactory;

    private UseCasesHelper theUseCasesHelper;

    private ActivityGraphsFactory theActivityGraphsFactory;

    private CollaborationsFactory theCollaborationsFactory;

    private CollaborationsHelper theCollaborationsHelper;

    private CommonBehaviorFactory theCommonBehaviorFactory;

    private CommonBehaviorHelper theCommonBehaviorHelper;

    private DataTypesFactory theDataTypesFactory;

    private DataTypesHelper theDataTypesHelper;

    private ExtensionMechanismsFactory theExtensionMechanismsFactory;

    private ExtensionMechanismsHelper theExtensionMechanismsHelper;

    private StateMachinesFactory theStateMachinesFactory;

    private CoreFactory theCoreFactory;

    private KindsMDRImpl theKindsObject;
    
    private MDRepository repository;

    private UmlPackage umlPackage;

    private MofPackage mofPackage;
    
    private ModelPackage mofExtent;

    private MementoCreationObserver mementoCreationObserver;

    /**
     * @return Returns the extent.
     */
    UmlPackage getUmlPackage() {
        return umlPackage;
    }

    MofPackage getMofPackage() {
        return mofPackage;
    }
    
    ModelPackage getModelPackage() {
        return mofExtent;
    }
    
    MDRepository getRepository() {
        return repository;
    }

    static final String MOF_EXTENT_NAME = "metamodel extent";

    static final String EXTENT_NAME = "model extent";

    // UML 1.4 metamodel definition in XMI format
    static final String METAMODEL_URL = "mof/01-02-15_Diff.xml";

    /**
     * Constructor.
     * 
     * @throws CreationFailedException If the creation of the Extend fail
     * @throws MalformedXMIException If the XMI is bad formed
     * @throws IOException If there is a problem opening a file
     */
    public MDRModelImplementation() throws CreationFailedException,
            IOException, MalformedXMIException {

        String storageImplementation = System.getProperty(
                "org.netbeans.mdr.storagemodel.StorageFactoryClassName",
                "org.netbeans.mdr.persistence.memoryimpl.StorageFactoryImpl");
        System.setProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName",
                storageImplementation);

        // Connect to the repository
        repository = MDRManager.getDefault().getDefaultRepository();

        mofExtent = (ModelPackage) repository.
                getExtent(MOF_EXTENT_NAME);

        // Create an extent and read in our metamodel (M2 model) 
        if (mofExtent == null) {
            mofExtent = (ModelPackage) repository.createExtent(MOF_EXTENT_NAME);
            XMIReader reader = XMIReaderFactory.getDefault().createXMIReader();
            String metafacade = System.getProperty("argouml.model.mdr.facade",
                    METAMODEL_URL);
            URL resource = getClass().getResource(metafacade);
            reader.read(resource.toString(), mofExtent);
        }

        mofPackage = null;
        for (Iterator iter = mofExtent.getMofPackage().refAllOfClass().
                iterator(); iter.hasNext();) {
            MofPackage pkg = (MofPackage) iter.next();
            if ("UML".equals(pkg.getName())) {
                mofPackage = pkg;
                break;
            }
        }

        // Create an extent for the uml data
        umlPackage = (UmlPackage) repository.getExtent(EXTENT_NAME);
        if (umlPackage == null) {
            umlPackage = (UmlPackage) repository.createExtent(EXTENT_NAME,
                    mofPackage);
        }

        if (umlPackage == null) {
            LOG.fatal("Could not find MofPackage UML");
            System.exit(1);
            return;
        }

        // Create and start event pump first so it's available for all others
        theModelEventPump = new ModelEventPumpMDRImpl(this, repository);
        theModelEventPump.startPumpingEvents();

        // DataTypes is next so it's available for Kinds, ModelManagement,
        // & Extensions
        theDataTypesFactory = new DataTypesFactoryMDRImpl(this);
        theDataTypesHelper = new DataTypesHelperMDRImpl(this);

        theKindsObject = new KindsMDRImpl(this);
        theModelManagementFactory = new ModelManagementFactoryMDRImpl(this);
        theExtensionMechanismsHelper = new ExtensionMechanismsHelperMDRImpl(
                this);
        theExtensionMechanismsFactory = new ExtensionMechanismsFactoryMDRImpl(
                this);
        
        // Finally all the remaining factories & helpers
        theFacade = new FacadeMDRImpl(this);
        theCopyHelper = new CopyHelper(this);
        theActivityGraphsHelper = new ActivityGraphsHelperMDRImpl();
        theCoreHelper = new CoreHelperMDRImpl(this);
        theModelManagementHelper = new ModelManagementHelperMDRImpl(this);
        theStateMachinesHelper = new StateMachinesHelperMDRImpl(this);
        theUmlFactory = new UmlFactoryMDRImpl(this);
        theUmlHelper = new UmlHelperMDRImpl(this);
        theUseCasesFactory = new UseCasesFactoryMDRImpl(this);
        theUseCasesHelper = new UseCasesHelperMDRImpl(this);
        theActivityGraphsFactory = new ActivityGraphsFactoryMDRImpl(this);
        theCollaborationsFactory = new CollaborationsFactoryMDRImpl(this);
        theCollaborationsHelper = new CollaborationsHelperMDRImpl(this);
        theCommonBehaviorFactory = new CommonBehaviorFactoryMDRImpl(this);
        theCommonBehaviorHelper = new CommonBehaviorHelperMDRImpl(this);
        theStateMachinesFactory = new StateMachinesFactoryMDRImpl(this);
        theCoreFactory = new CoreFactoryMDRImpl(this);

    }
    
    /**
     * Shutdown repository in a graceful fashion
     * (currently unused)
     */
    public void shutdown() {
        theModelEventPump.flushModelEvents();
        theModelEventPump.stopPumpingEvents();
        MDRManager.getDefault().shutdownAll();
    }

    /**
     * @see org.argouml.model.ModelImplementation#getDiagramInterchangeModel()
     */
    public DiagramInterchangeModel getDiagramInterchangeModel() {
        return null;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getFacade()
     */
    public Facade getFacade() {
        return theFacade;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getModelEventPump()
     */
    public ModelEventPump getModelEventPump() {
        return theModelEventPump;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getActivityGraphsFactory()
     */
    public ActivityGraphsFactory getActivityGraphsFactory() {
        return theActivityGraphsFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getActivityGraphsHelper()
     */
    public ActivityGraphsHelper getActivityGraphsHelper() {
        return theActivityGraphsHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCollaborationsFactory()
     */
    public CollaborationsFactory getCollaborationsFactory() {
        return theCollaborationsFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCollaborationsHelper()
     */
    public CollaborationsHelper getCollaborationsHelper() {
        return theCollaborationsHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCommonBehaviorFactory()
     */
    public CommonBehaviorFactory getCommonBehaviorFactory() {
        return theCommonBehaviorFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCommonBehaviorHelper()
     */
    public CommonBehaviorHelper getCommonBehaviorHelper() {
        return theCommonBehaviorHelper;
    }

    /**
     * Get the CopyHelper
     * 
     * @return The copy helper.
     */
    public org.argouml.model.CopyHelper getCopyHelper() {
        return theCopyHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCoreFactory()
     */
    public CoreFactory getCoreFactory() {
        return theCoreFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getCoreHelper()
     */
    public CoreHelper getCoreHelper() {
        return theCoreHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getDataTypesFactory()
     */
    public DataTypesFactory getDataTypesFactory() {
        return theDataTypesFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getDataTypesHelper()
     */
    public DataTypesHelper getDataTypesHelper() {
        return theDataTypesHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getExtensionMechanismsFactory()
     */
    public ExtensionMechanismsFactory getExtensionMechanismsFactory() {
        return theExtensionMechanismsFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getExtensionMechanismsHelper()
     */
    public ExtensionMechanismsHelper getExtensionMechanismsHelper() {
        return theExtensionMechanismsHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getModelManagementFactory()
     */
    public ModelManagementFactory getModelManagementFactory() {
        return theModelManagementFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getModelManagementHelper()
     */
    public ModelManagementHelper getModelManagementHelper() {
        return theModelManagementHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getStateMachinesFactory()
     */
    public StateMachinesFactory getStateMachinesFactory() {
        return theStateMachinesFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getStateMachinesHelper()
     */
    public StateMachinesHelper getStateMachinesHelper() {
        return theStateMachinesHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getUmlFactory()
     */
    public UmlFactory getUmlFactory() {
        return theUmlFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getUmlHelper()
     */
    public UmlHelper getUmlHelper() {
        return theUmlHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getUseCasesFactory()
     */
    public UseCasesFactory getUseCasesFactory() {
        return theUseCasesFactory;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getUseCasesHelper()
     */
    public UseCasesHelper getUseCasesHelper() {
        return theUseCasesHelper;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getMetaTypes()
     */
    public MetaTypes getMetaTypes() {
        return theMetaTypes;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getChangeableKind()
     */
    public ChangeableKind getChangeableKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getAggregationKind()
     */
    public AggregationKind getAggregationKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getPseudostateKind()
     */
    public PseudostateKind getPseudostateKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getScopeKind()
     */
    public ScopeKind getScopeKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getConcurrencyKind()
     */
    public ConcurrencyKind getConcurrencyKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getDirectionKind()
     */
    public DirectionKind getDirectionKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getOrderingKind()
     */
    public OrderingKind getOrderingKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getVisibilityKind()
     */
    public VisibilityKind getVisibilityKind() {
        return theKindsObject;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getXmiReader()
     */
    public XmiReader getXmiReader() throws UmlException {
        XmiReader reader = new XmiReaderImpl(this, mofPackage);
        return reader;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getXmiWriter(Object, Writer)
     */
    public XmiWriter getXmiWriter(Object model, Writer writer) 
        throws UmlException {
        return new XmiWriterMDRImpl(this, model, writer);
    }

    /**
     * @see org.argouml.model.ModelImplementation#getEventAdapter()
     */
    public EventAdapter getEventAdapter() {
        return theModelEventPump;
    }

    /**
     * @see org.argouml.model.ModelImplementation#setMementoCreationObserver(
     *      org.argouml.model.MementoCreationObserver)
     */
    public void setMementoCreationObserver(MementoCreationObserver observer) {
        mementoCreationObserver = observer;
    }

    /**
     * @see org.argouml.model.ModelImplementation#getMementoCreationObserver()
     */
    public MementoCreationObserver getMementoCreationObserver() {
        return mementoCreationObserver;
    }

}
