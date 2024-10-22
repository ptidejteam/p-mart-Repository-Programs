// $Id: UmlFactoryMDRImpl.java,v 1.2 2006/03/02 05:07:42 vauchers Exp $
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

package org.argouml.model.mdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jmi.reflect.InvalidObjectException;
import javax.jmi.reflect.RefObject;

import org.apache.log4j.Logger;
import org.argouml.model.IllegalModelElementConnectionException;
import org.argouml.model.MetaTypes;
import org.argouml.model.ModelEventPump;
import org.argouml.model.UmlFactory;
import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.CallState;
import org.omg.uml.behavioralelements.activitygraphs.ClassifierInState;
import org.omg.uml.behavioralelements.activitygraphs.ObjectFlowState;
import org.omg.uml.behavioralelements.activitygraphs.Partition;
import org.omg.uml.behavioralelements.activitygraphs.SubactivityState;
import org.omg.uml.behavioralelements.collaborations.AssociationEndRole;
import org.omg.uml.behavioralelements.collaborations.AssociationRole;
import org.omg.uml.behavioralelements.collaborations.ClassifierRole;
import org.omg.uml.behavioralelements.collaborations.Collaboration;
import org.omg.uml.behavioralelements.collaborations.Interaction;
import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.ActionSequence;
import org.omg.uml.behavioralelements.commonbehavior.AttributeLink;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.CreateAction;
import org.omg.uml.behavioralelements.commonbehavior.DataValue;
import org.omg.uml.behavioralelements.commonbehavior.DestroyAction;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.LinkObject;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.behavioralelements.commonbehavior.Reception;
import org.omg.uml.behavioralelements.commonbehavior.ReturnAction;
import org.omg.uml.behavioralelements.commonbehavior.SendAction;
import org.omg.uml.behavioralelements.commonbehavior.Signal;
import org.omg.uml.behavioralelements.commonbehavior.TerminateAction;
import org.omg.uml.behavioralelements.commonbehavior.UninterpretedAction;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.StubState;
import org.omg.uml.behavioralelements.statemachines.SubmachineState;
import org.omg.uml.behavioralelements.statemachines.SynchState;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.usecases.Actor;
import org.omg.uml.behavioralelements.usecases.Extend;
import org.omg.uml.behavioralelements.usecases.ExtensionPoint;
import org.omg.uml.behavioralelements.usecases.Include;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.behavioralelements.usecases.UseCaseInstance;
import org.omg.uml.foundation.core.Abstraction;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.Binding;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Comment;
import org.omg.uml.foundation.core.Component;
import org.omg.uml.foundation.core.Constraint;
import org.omg.uml.foundation.core.DataType;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Element;
import org.omg.uml.foundation.core.Feature;
import org.omg.uml.foundation.core.Flow;
import org.omg.uml.foundation.core.GeneralizableElement;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.Interface;
import org.omg.uml.foundation.core.Method;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Node;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.core.Permission;
import org.omg.uml.foundation.core.PresentationElement;
import org.omg.uml.foundation.core.Relationship;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StructuralFeature;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TemplateParameter;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.Usage;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.modelmanagement.ElementImport;
import org.omg.uml.modelmanagement.Subsystem;
import org.omg.uml.modelmanagement.UmlPackage;

/**
 * Root factory for UML model element instance creation.
 *
 * <p>
 * Emulates synchronous event delivery as implemented in NSUML library.
 * Any methods which call org.omg.uml or NetBeans MDR methods that generate
 * events must call flushModelEvents() before returning.  'Get' methods do
 * not needed to be handled specially.
 * <p>
 * 
 * @since ARGO0.19.5
 * @author Ludovic Ma�tre
 * based on NSUML implementation by:
 * @author Thierry Lach
 */
class UmlFactoryMDRImpl extends AbstractUmlModelFactoryMDR implements
        UmlFactory {

    /**
     * The logger.
     */
    private Logger LOG = Logger.getLogger(UmlFactoryMDRImpl.class);
        
    /**
     * The model implementation.
     */
    private MDRModelImplementation nsmodel;
    
    /**
     * The model event pump
     */
    private ModelEventPump eventPump;
    
    /**
     * The meta types factory
     */
    private MetaTypes metaTypes;

    /**
     * A map of valid connections keyed by the connection type. The constructor
     * builds this from the data in the VALID_CONNECTIONS array
     */
    private Map validConnectionMap = new HashMap();

    /**
     * The instances that have been already deleted.
     */
    private Set instancesDeleted = Collections.synchronizedSet(new HashSet());
    
    /**
     * The instance that we are deleting.
     */
    private Map instancesToBeDeleted = Collections.
            synchronizedMap(new HashMap());

    /**
     * The top object is the first object given to the UmlFactory when calling
     * the delete method.
     */
    private Object top;
    
    /**
     * The mutex for this class
     */
    private Object lock = new Byte[0];
    
    /**
     * An array of valid connections, the combination of connecting class and
     * node classes must exist as a row in this list to be considered valid.
     * <ul>
     * <li>The 1st column is the connecting element.
     * <li>The 2nd column is the "from" element type.
     * <li>The 3rd column is the "to" element type.
     * <li>The 3rd column is optional, if not given then it is assumed to be
     * the same as the "to" element.
     * <li>The existence of a 4th column indicates that the connection is valid
     * in one direction only.
     * </ul>
     */
    private static final Object[][] VALID_CONNECTIONS = {
        {Generalization.class,   ClassifierRole.class, },
        {Generalization.class,   UmlClass.class, },
        {Generalization.class,   Interface.class, },
        {Generalization.class,   UmlPackage.class, },
        {Generalization.class,   UseCase.class, },
        {Generalization.class,   Actor.class, },
        {Generalization.class,   Node.class, },
        {Generalization.class,   Component.class, },
        {Dependency.class,       Package.class, },
        {Dependency.class,       UmlClass.class, },
        {Dependency.class,       Interface.class, },
        {Dependency.class, Interface.class, UmlClass.class, },
        {Dependency.class, Interface.class, Package.class, },
        {Dependency.class, UmlClass.class, Package.class, },
        {Dependency.class,       UseCase.class, },
        {Dependency.class,       Actor.class, },
        {Dependency.class, Actor.class, UseCase.class, },
        {Dependency.class,       Component.class, },
        {Dependency.class,       ComponentInstance.class, },
        {Dependency.class,       Object.class, },
        {Dependency.class, Component.class, Node.class, null, },
        {Dependency.class, Object.class, Component.class, null, },
        {Dependency.class, ComponentInstance.class, NodeInstance.class,
            null, },
        {Dependency.class,
            org.omg.uml.behavioralelements.commonbehavior.Object.class,
            ComponentInstance.class, null, },
        {Dependency.class, Component.class, Interface.class, null, },
        {Dependency.class,       ClassifierRole.class, },
        {Usage.class,            UmlClass.class, },
        {Usage.class,            Interface.class, },
        {Usage.class,            UmlPackage.class, },
        {Usage.class, Class.class, Package.class, },
        {Usage.class, Class.class, Interface.class, },
        {Usage.class, Interface.class, Package.class, },
        {Permission.class,       UmlClass.class, },
        {Permission.class,       Interface.class, },
        {Permission.class,       UmlPackage.class, },
        {Permission.class, UmlClass.class, Package.class, },
        {Permission.class, UmlClass.class, Interface.class, },
        {Permission.class, Interface.class, Package.class, },
        {Abstraction.class, UmlClass.class, Interface.class, null, },
        {Abstraction.class, UmlClass.class, UmlClass.class, null, },
        {Abstraction.class, UmlPackage.class, UmlPackage.class, null, },
        {Abstraction.class, Component.class, Interface.class, null, },
        {UmlAssociation.class,      UmlClass.class, },
        {UmlAssociation.class, UmlClass.class, Interface.class, },
        {UmlAssociation.class,      Actor.class, },
        {UmlAssociation.class,      UseCase.class, },
        {UmlAssociation.class, Actor.class, UseCase.class, },
        {UmlAssociation.class,      Node.class, },
        {AssociationRole.class,  ClassifierRole.class, },
        {Extend.class,           UseCase.class, },
        {Include.class,          UseCase.class, },
        {Link.class, NodeInstance.class, }, {Link.class, Object.class, },
        {Transition.class,       StateVertex.class, },
        {AssociationClass.class, UmlClass.class, },
        {AssociationEnd.class, UmlClass.class, UmlAssociation.class, },
        {Message.class, ClassifierRole.class }, };

    /**
     * Don't allow external instantiation.
     *
     * @param implementation
     *            To get other helpers and factories.
     */
    UmlFactoryMDRImpl(MDRModelImplementation implementation) {
        nsmodel = implementation;
        eventPump = nsmodel.getModelEventPump();
        metaTypes = nsmodel.getMetaTypes();

        buildValidConnectionMap();
    }

    private void buildValidConnectionMap() {
        // A list of valid connections between elements, the
        // connection type first and then the elements to be connected

        Object connection = null;
        for (int i = 0; i < VALID_CONNECTIONS.length; ++i) {
            connection = VALID_CONNECTIONS[i][0];
            List validItems = (ArrayList) validConnectionMap.get(connection);
            if (validItems == null) {
                validItems = new ArrayList();
                validConnectionMap.put(connection, validItems);
            }
            if (VALID_CONNECTIONS[i].length < 3) {
                // If there isn't a 3rd column then this represents a connection
                // of elements of the same type.
                Object[] modeElementPair = new Class[2];
                modeElementPair[0] = VALID_CONNECTIONS[i][1];
                modeElementPair[1] = VALID_CONNECTIONS[i][1];
                validItems.add(modeElementPair);
            } else {
                // If there is a 3rd column then this represents a connection
                // of between 2 different types of element.
                Object[] modeElementPair = new Class[2];
                modeElementPair[0] = VALID_CONNECTIONS[i][1];
                modeElementPair[1] = VALID_CONNECTIONS[i][2];
                validItems.add(modeElementPair);
                // If the array hasn't been flagged to indicate otherwise
                // swap elements the elemnts and add again.
                if (VALID_CONNECTIONS[i].length < 4) {
                    Object[] reversedModeElementPair = new Class[2];
                    reversedModeElementPair[0] = VALID_CONNECTIONS[i][2];
                    reversedModeElementPair[1] = VALID_CONNECTIONS[i][1];
                    validItems.add(reversedModeElementPair);
                }
            }
        }
    }


    /**
     * Creates a UML model element of the given type and uses this to connect
     * two other existing UML model elements. This only works for UML elements.
     * If a diagram contains elements of another type then it is the
     * responsibility of the diagram to manage those items and not call this
     * method. It also only works for UML model elements that are represented in
     * diagrams by an edge, hence the requirement to state the connecting ends.
     *
     * @param elementType
     *            the UML object type of the connection
     * @param fromElement
     *            the UML object for the "from" element
     * @param fromStyle
     *            the aggregationkind for the connection in case of an
     *            association
     * @param toElement
     *            the UML object for the "to" element
     * @param toStyle
     *            the aggregationkind for the connection in case of an
     *            association
     * @param unidirectional
     *            for association and associationrole
     * @param namespace
     *            the namespace to use if it can't be determined
     * @return               the newly build connection (UML object)
     * @throws IllegalModelElementConnectionException
     *             if the connection is not a valid thing to do
     */
    public Object buildConnection(Object elementType, Object fromElement,
            Object fromStyle, Object toElement, Object toStyle,
            Object unidirectional, Object namespace)
        throws IllegalModelElementConnectionException {

        if (!isConnectionValid(elementType, fromElement, toElement)) {
            throw new IllegalModelElementConnectionException("Cannot make a "
                    + elementType.getClass().getName() + " between a "
                    + fromElement.getClass().getName() + " and a "
                    + toElement.getClass().getName());
        }

        Object connection = null;

        if (elementType == metaTypes.getAssociation()) {
            connection = getCore().buildAssociation((Classifier) fromElement,
                    (AggregationKind) fromStyle, (Classifier) toElement,
                    (AggregationKind) toStyle, (Boolean) unidirectional);
        } else if (elementType == metaTypes.getAssociationEnd()) {
            if (fromElement instanceof UmlAssociation) {
                connection = getCore().buildAssociationEnd(toElement,
                        fromElement);
            } else if (fromElement instanceof Classifier) {
                connection = getCore().buildAssociationEnd(fromElement,
                        toElement);
            }
        } else if (elementType
                == metaTypes.getAssociationClass()) {
            connection = getCore().
                    buildAssociationClass(fromElement, toElement);
        } else if (elementType == metaTypes.getAssociationRole()) {
            connection = getCollaborations().buildAssociationRole(fromElement,
                    fromStyle, toElement, toStyle, (Boolean) unidirectional);
        } else if (elementType == metaTypes.getGeneralization()) {
            connection = getCore().buildGeneralization(fromElement, toElement);
        } else if (elementType == metaTypes.getPermission()) {
            connection = getCore().buildPermission(fromElement, toElement);
        } else if (elementType == metaTypes.getUsage()) {
            connection = getCore().buildUsage(fromElement, toElement);
        } else if (elementType == metaTypes.getGeneralization()) {
            connection = getCore().buildGeneralization(fromElement, toElement);
        } else if (elementType == metaTypes.getDependency()) {
            connection = getCore().buildDependency(fromElement, toElement);
        } else if (elementType == metaTypes.getAbstraction()) {
            connection = getCore().buildRealization(fromElement, toElement,
                        namespace);
        } else if (elementType == metaTypes.getLink()) {
            connection = getCommonBehavior().buildLink(fromElement, toElement);
        } else if (elementType == metaTypes.getExtend()) {
            // Extend, but only between two use cases. Remember we draw from the
            // extension port to the base port.
            connection = getUseCases().buildExtend(toElement, fromElement);
        } else if (elementType == metaTypes.getInclude()) {
            connection = getUseCases().buildInclude(fromElement, toElement);
        } else if (elementType == metaTypes.getTransition()) {
            connection = getStateMachines().buildTransition(fromElement,
                    toElement);
        }

        if (connection == null) {
            throw new IllegalModelElementConnectionException("Cannot make a "
                    + elementType.getClass().getName() + " between a "
                    + fromElement.getClass().getName() + " and a "
                    + toElement.getClass().getName());
        }

        return connection;
    }

    /**
     * Creates a UML model element of the given type. This only works for UML
     * elements. If a diagram contains elements of another type then it is the
     * responsibility of the diagram manage those items and not call this
     * method. It also only works for UML model elements that are represented in
     * diagrams by a node.
     * <p>
     *
     * The parameter "elementType" stands for the type of model element to
     * build.
     *
     * @see org.argouml.model.UmlFactory#buildNode(java.lang.Object)
     */
    public Object buildNode(Object elementType) {

        Object modelElement = null;
        if (elementType == metaTypes.getActor()) {
            return getUseCases().createActor();
        } else if (elementType == metaTypes.getUseCase()) {
            return getUseCases().createUseCase();
        } else if (elementType == metaTypes.getUMLClass()) {
            return getCore().buildClass();
        } else if (elementType == metaTypes.getInterface()) {
            return getCore().buildInterface();
        } else if (elementType == metaTypes.getDataType()) {
            return getCore().createDataType();
        } else if (elementType == metaTypes.getPackage()) {
            return getModelManagement().createPackage();
        } else if (elementType == metaTypes.getModel()) {
            return getModelManagement().createModel();
        } else if (elementType == metaTypes.getInstance()) {
            throw new RuntimeException("Attempt to instantiate abstract type");
        } else if (elementType == metaTypes.getSubsystem()) {
            return getModelManagement().createSubsystem();
        } else if (elementType == metaTypes.getCallState()) {
            return getActivityGraphs().createCallState();
        } else if (elementType == metaTypes.getSimpleState()) {
            return getStateMachines().createSimpleState();
        } else if (elementType == metaTypes.getFinalState()) {
            return getStateMachines().createFinalState();
        } else if (elementType == metaTypes.getPseudostate()) {
            return getStateMachines().createPseudostate();
        } else if (elementType == metaTypes.getObjectFlowState()) {
            return getActivityGraphs().createObjectFlowState();
        } else if (elementType == metaTypes.getActionState()) {
            return getActivityGraphs().createActionState();
        } else if (elementType == metaTypes.getSubactivityState()) {
            return getActivityGraphs().createSubactivityState();
        } else if (elementType == metaTypes.getStubState()) {
            return getStateMachines().createStubState();
        } else if (elementType == metaTypes.getSubmachineState()) {
            return getStateMachines().createSubmachineState();
        } else if (elementType == metaTypes.getCompositeState()) {
            return getStateMachines().createCompositeState();
        } else if (elementType == metaTypes.getSynchState()) {
            return getStateMachines().createSynchState();
        } else if (elementType == metaTypes.getState()) {
            throw new RuntimeException("Attempt to instantiate abstract type");
        } else if (elementType == nsmodel.getMetaTypes().getSimpleState()) {
            return getStateMachines().createSimpleState();
        } else if (elementType == metaTypes.getClassifierRole()) {
            return getCollaborations().createClassifierRole();
        } else if (elementType == metaTypes.getComponent()) {
            return getCore().createComponent();
        } else if (elementType == metaTypes.getComponentInstance()) {
            return getCommonBehavior().createComponentInstance();
        } else if (elementType == metaTypes.getNode()) {
            return getCore().createNode();
        } else if (elementType == metaTypes.getNodeInstance()) {
            return getCommonBehavior().createNodeInstance();
        } else if (elementType == metaTypes.getObject()) {
            return getCommonBehavior().createObject();
        } else if (elementType == metaTypes.getComment()) {
            return getCore().createComment();
        } else if (elementType == metaTypes.getNamespace()) {
            throw new RuntimeException("Attempt to instantiate abstract type");
        } else if (elementType == metaTypes.getOperation()) {
            return getCore().createOperation();
        }
        return modelElement;
    }

    /**
     * Checks if some type of UML model element is valid to connect two other
     * existing UML model elements. This only works for UML elements. If a
     * diagram contains elements of another type then it is the responsibility
     * of the diagram to filter those out before calling this method.
     *
     * @param connectionType
     *            the UML object type of the connection
     * @param fromElement
     *            the UML object type of the "from"
     * @param toElement
     *            the UML object type of the "to"
     * @return true if valid
     */
    public boolean isConnectionValid(Object connectionType, Object fromElement,
            Object toElement) {
        // Get the list of valid model item pairs for the given connection type
        List validItems = (ArrayList) validConnectionMap.get(connectionType);
        if (validItems == null) {
            return false;
        }
        // See if there's a pair in this list that match the given
        // model elements
        Iterator it = validItems.iterator();
        while (it.hasNext()) {
            Class[] modeElementPair = (Class[]) it.next();
            if (modeElementPair[0].isInstance(fromElement)
                && modeElementPair[1].isInstance(toElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the package factory for the UML package
     * Foundation::ExtensionMechanisms.
     *
     * @return the ExtensionMechanisms factory instance.
     */
    private ExtensionMechanismsFactoryMDRImpl getExtensionMechanisms() {
        return (ExtensionMechanismsFactoryMDRImpl) nsmodel.
                getExtensionMechanismsFactory();
    }

    /**
     * Returns the package factory for the UML package Foundation::Core.
     *
     * @return the Core factory instance.
     */
    public CoreFactoryMDRImpl getCore() {
        return (CoreFactoryMDRImpl) nsmodel.getCoreFactory();
    }

    /**
     * Returns the package factory for the UML package
     * BehavioralElements::CommonBehavior.
     *
     * @return the CommonBehavior factory instance.
     */
    public CommonBehaviorFactoryMDRImpl getCommonBehavior() {
        return (CommonBehaviorFactoryMDRImpl) nsmodel.
                getCommonBehaviorFactory();
    }

    /**
     * Returns the package factory for the UML package
     * BehavioralElements::UseCases.
     *
     * @return the UseCases factory instance.
     */
    public UseCasesFactoryMDRImpl getUseCases() {
        return (UseCasesFactoryMDRImpl) nsmodel.getUseCasesFactory();
    }

    /**
     * Returns the package factory for the UML package
     * BehavioralElements::StateMachines.
     *
     * @return the StateMachines factory instance.
     */
    public StateMachinesFactoryMDRImpl getStateMachines() {
        return (StateMachinesFactoryMDRImpl) nsmodel.getStateMachinesFactory();
    }

    /**
     * Returns the package factory for the UML package
     * BehavioralElements::Collaborations.
     *
     * @return the Collaborations factory instance.
     */
    public CollaborationsFactoryMDRImpl getCollaborations() {
        return (CollaborationsFactoryMDRImpl) nsmodel.
                getCollaborationsFactory();
    }

    /**
     * Returns the package factory for the UML package
     * BehavioralElements::ActivityGraphs.
     *
     * @return the ActivityGraphs factory instance.
     */
    private ActivityGraphsFactoryMDRImpl getActivityGraphs() {
        return (ActivityGraphsFactoryMDRImpl) nsmodel.
                getActivityGraphsFactory();
    }

    /**
     * Returns the package factory for the UML package ModelManagement.
     *
     * @return the ModelManagement factory instance.
     */
    public ModelManagementFactoryMDRImpl getModelManagement() {
        return (ModelManagementFactoryMDRImpl) nsmodel.
                getModelManagementFactory();
    }

    /**
     * Deletes a modelelement. It calls the remove method of the modelelement
     * but also does 'cascading deletes' that are not provided for in the remove
     * method of the modelelement itself. For example: this delete method also
     * removes the binary associations that a class has if the class is deleted.
     * In this way, illegal states are prevented from existing in the
     * nsmodel.
     * <p>
     *
     * The actual deletion is delegated to delete methods in the rest of the
     * factories. For example: a method deleteClass exists on CoreHelper. Delete
     * methods as deleteClass should only do those extra actions that are
     * necessary for the deletion of the modelelement itself. I.e. deleteClass
     * should only take care of things specific to UmlClass.
     * <p>
     *
     * The delete methods in the UML Factories should not be called directly
     * throughout the code! Calls should always refer to this method and never
     * call the deleteXXX method on XXXFactory directly. The reason that it is
     * possible to call the deleteXXX methods directly is a pure implementation
     * detail.
     * <p>
     *
     * The implementation of this method uses a quite complicated if/then/else
     * tree. This is done to provide optimal performance and full compliance to
     * the UML 1.3 nsmodel. The last remark refers to the fact that the UML 1.3
     * model uses multiple inheritance in several places. This has to be taken
     * into account.
     * <p>
     *
     * Extensions and its children are not taken into account here. They do not
     * require extra cleanup actions. Not in the form of a call to the remove
     * method as is normal for all children of MBase and not in the form of
     * other behaviour we want to implement via this operation.
     *
     * @param elem
     *            The element to be deleted
     */
    public void delete(Object elem) {
        if (elem == null) {
            throw new IllegalArgumentException("Element may not be null "
                    + "in delete");
        }

        Object key = elem.getClass() + ":" + elem.hashCode();
        synchronized (lock) {
            if (instancesDeleted.contains(key)
                    || instancesToBeDeleted.containsKey(key)) {
                return;
            }
            if (top == null) {
                top = elem;
            }
            instancesToBeDeleted.put(key, elem);
        }

        if (LOG.isInfoEnabled()) {
            if (top == elem)
                LOG.info("Set top to " + elem);
            LOG.info("Deleting " + elem);
        }

        // TODO: Update this decision tree for the UML 1.4 structure
        try {
            if (elem instanceof Element) {
                getCore().deleteElement(elem);
                if (elem instanceof ModelElement) {
                    getCore().deleteModelElement(elem);
                    // no else here to make sure Classifier with
                    // its double inheritance goes ok

                    if (elem instanceof GeneralizableElement) {
                        GeneralizableElement ge = (GeneralizableElement) elem;
                        getCore().deleteGeneralizableElement(ge);
                        if (elem instanceof Stereotype) {
                            Stereotype s = (Stereotype) elem;
                            getExtensionMechanisms().deleteStereotype(s);
                        }
                    } // no else here to make sure AssociationClass goes ok

                    if (elem instanceof Parameter) {
                        getCore().deleteParameter(elem);
                    } else if (elem instanceof Constraint) {
                        getCore().deleteConstraint(elem);
                    } else if (elem instanceof Relationship) {
                        deleteRelationship((Relationship) elem);
                    } else if (elem instanceof AssociationEnd) {
                        getCore().deleteAssociationEnd(elem);
                        if (elem instanceof AssociationEndRole) {
                            getCollaborations().deleteAssociationEndRole(elem);
                        }
                    } else if (elem instanceof Comment) {
                        getCore().deleteComment(elem);
                    } else if (elem instanceof Action) {
                        deleteAction(elem);
                    } else if (elem instanceof AttributeLink) {
                        getCommonBehavior().deleteAttributeLink(elem);
                    } else if (elem instanceof Instance) {
                        deleteInstance((Instance) elem);
                    } // no else to handle multiple inheritance of linkobject

                    if (elem instanceof Link) {
                        getCommonBehavior().deleteLink(elem);
                    } else if (elem instanceof LinkEnd) {
                        getCommonBehavior().deleteLinkEnd(elem);
                    } else if (elem instanceof Interaction) {
                        getCollaborations().deleteInteraction(elem);
                    } else if (elem instanceof Message) {
                        getCollaborations().deleteMessage(elem);
                    } else if (elem instanceof ExtensionPoint) {
                        getUseCases().deleteExtensionPoint(elem);
                    } else if (elem instanceof StateVertex) {
                        deleteStateVertex((StateVertex) elem);
                    }

                    if (elem instanceof StateMachine) {
                        getStateMachines().deleteStateMachine(elem);
                        if (elem instanceof ActivityGraph) {
                            getActivityGraphs().deleteActivityGraph(elem);
                        }
                    } else if (elem instanceof Transition) {
                        getStateMachines().deleteTransition(elem);
                    } else if (elem instanceof Guard) {
                        getStateMachines().deleteGuard(elem);
                    }
                    // else if (elem instanceof MEvent) {
                    //
                    // }
                } else if (elem instanceof PresentationElement) {
                    getCore().deletePresentationElement(elem);
                }
            } else if (elem instanceof TemplateParameter) {
                getCore().deleteTemplateParameter(elem);
            } else if (elem instanceof TaggedValue) {
                getExtensionMechanisms().deleteTaggedValue(elem);
            }

            if (elem instanceof Partition) {
                getActivityGraphs().deletePartition(elem);
            } else if (elem instanceof ElementImport) {
                getModelManagement().deleteElementImport(elem);
            }

            if (elem instanceof Feature) {
                deleteFeature((Feature) elem);
            } else if (elem instanceof Namespace) {
                deleteNamespace((Namespace) elem);
            }
        } catch (InvalidObjectException e) {
            // we probably tried to delete something twice, just log for now
            // TODO: When we get here it indicates a problem in the delete code
            // it's probably calling MDR delete methods directly which it
            // shouldn't do - tfm
            LOG.error("Attempted deletion of deleted object " + elem);
        }

        if (elem instanceof RefObject && elem == top) {
            synchronized (lock) {
                Collection toDelete = new Vector();
                toDelete.addAll(instancesToBeDeleted.keySet());
                Iterator itDelete = toDelete.iterator();
                while (itDelete.hasNext()) {
                    String theKey = (String) itDelete.next();
                    RefObject deleted = (RefObject) instancesToBeDeleted
                            .remove(theKey);
                    try {
                        deleted.refDelete();
                    } catch (InvalidObjectException e) {
                        LOG.warn("Object already deleted " + deleted);
                    }
                    // LOG.info("Deleted the object "+deleted);
                    instancesDeleted.add(theKey);
                }
                top = null;
            }
        }
        eventPump.flushModelEvents();
    }

    /**
     * The Project may check if a certain Base has been removed.
     * 
     * @param o
     *            the object to be checked
     * @return true if removed
     */
    public boolean isRemoved(Object o) {
        Object key = o.getClass() + ":" + o.hashCode();
        synchronized (lock) {
            // TODO: We should clear the set when we change of project.
            // (or at another times).
            return instancesDeleted.contains(key)
                    || instancesToBeDeleted.containsKey(key);
        }
    }

    /**
     * Deprecated in model interface and unimplemented in our event
     * listener, so do nothing.
     */
    public void addListenersToModelElement(Object handle) {
        LOG.info("Ignoring deprecated call to addListenersToModelElement");
    }
    
    /**
     * Delete a Feature.
     *
     * @param elem feature to be deleted
     */
    private void deleteFeature(Feature elem) {
        getCore().deleteFeature(elem);
        if (elem instanceof BehavioralFeature) {
            getCore().deleteBehavioralFeature(elem);
            if (elem instanceof Operation) {
                getCore().deleteOperation(elem);
            } else if (elem instanceof Method) {
                getCore().deleteMethod(elem);
            } else if (elem instanceof Reception) {
                getCommonBehavior().deleteReception(elem);
            }
        } else if (elem instanceof StructuralFeature) {
            getCore().deleteStructuralFeature(elem);
            if (elem instanceof Attribute) {
                getCore().deleteAttribute(elem);
            }
        }
    }

    /**
     * Delete a Namespace.
     *
     * @param elem namespace to be deleted
     */
    private void deleteNamespace(Namespace elem) {
        getCore().deleteNamespace(elem);
        if (elem instanceof Classifier) {
            getCore().deleteClassifier(elem);
            if (elem instanceof UmlClass) {
                getCore().deleteClass(elem);
                if (elem instanceof AssociationClass) {
                    getCore().deleteAssociationClass(elem);
                }
            } else if (elem instanceof Interface) {
                getCore().deleteInterface(elem);
            } else if (elem instanceof DataType) {
                getCore().deleteDataType(elem);
            } else if (elem instanceof Node) {
                getCore().deleteNode(elem);
            } else if (elem instanceof Component) {
                getCore().deleteComponent(elem);
            } else if (elem instanceof Signal) {
                getCommonBehavior().deleteSignal(elem);
                if (elem instanceof Exception) {
                    getCommonBehavior().deleteException(elem);
                }
            } else if (elem instanceof ClassifierRole) {
                getCollaborations().deleteClassifierRole(elem);
            } else if (elem instanceof UseCase) {
                getUseCases().deleteUseCase(elem);
            } else if (elem instanceof Actor) {
                getUseCases().deleteActor(elem);
            } else if (elem instanceof ClassifierInState) {
                getActivityGraphs().deleteClassifierInState(elem);
            }
        } else if (elem instanceof Collaboration) {
            getCollaborations().deleteCollaboration(elem);
        } else if (elem instanceof UmlPackage) {
            getModelManagement().deletePackage(elem);
            if (elem instanceof org.omg.uml.modelmanagement.Model) {
                getModelManagement().deleteModel(elem);
            } else if (elem instanceof Subsystem) {
                getModelManagement().deleteSubsystem(elem);
            }
        }
    }

    /**
     * Delete a Relationship.
     *
     * @param elem Relationship to be deleted
     */
    private void deleteRelationship(Relationship elem) {
        getCore().deleteRelationship(elem);
        if (elem instanceof Flow) {
            getCore().deleteFlow(elem);
        } else if (elem instanceof Generalization) {
            getCore().deleteGeneralization(elem);
        } else if (elem instanceof UmlAssociation) {
            getCore().deleteAssociation(elem);
            if (elem instanceof AssociationRole) {
                getCollaborations().deleteAssociationRole(elem);
            }
        } else if (elem instanceof Dependency) {
            getCore().deleteDependency(elem);
            if (elem instanceof Abstraction) {
                getCore().deleteAbstraction(elem);
            } else if (elem instanceof Binding) {
                getCore().deleteBinding(elem);
            } else if (elem instanceof Usage) {
                getCore().deleteUsage(elem);
            } else if (elem instanceof Permission) {
                getCore().deletePermission(elem);
            }
        } else if (elem instanceof Include) {
            getUseCases().deleteInclude(elem);
        } else if (elem instanceof Extend) {
            getUseCases().deleteExtend(elem);
        }
    }

    /**
     * Delete an Action.
     *
     * @param elem the Action to be deleted
     */
    private void deleteAction(Object elem) {
        getCommonBehavior().deleteAction(elem);
        if (elem instanceof ActionSequence) {
            getCommonBehavior().deleteActionSequence(elem);
        } else if (elem instanceof CreateAction) {
            getCommonBehavior().deleteCreateAction(elem);
        } else if (elem instanceof CallAction) {
            getCommonBehavior().deleteCallAction(elem);
        } else if (elem instanceof ReturnAction) {
            getCommonBehavior().deleteReturnAction(elem);
        } else if (elem instanceof SendAction) {
            getCommonBehavior().deleteSendAction(elem);
        } else if (elem instanceof TerminateAction) {
            getCommonBehavior().deleteTerminateAction(elem);
        } else if (elem instanceof UninterpretedAction) {
            getCommonBehavior().deleteUninterpretedAction(elem);
        } else if (elem instanceof DestroyAction) {
            getCommonBehavior().deleteDestroyAction(elem);
        }
    }

    /**
     * Delete an Instance.
     *
     * @param elem the Instance to be deleted.
     */
    private void deleteInstance(Instance elem) {
        getCommonBehavior().deleteInstance(elem);
        if (elem instanceof DataValue) {
            getCommonBehavior().deleteDataValue(elem);
        } else if (elem instanceof ComponentInstance) {
            getCommonBehavior().deleteComponentInstance(elem);
        } else if (elem instanceof NodeInstance) {
            getCommonBehavior().deleteNodeInstance(elem);
        } else if (elem instanceof 
                org.omg.uml.behavioralelements.commonbehavior.Object) {
            getCommonBehavior().deleteObject(elem);
            if (elem instanceof LinkObject) {
                getCommonBehavior().deleteLinkObject(elem);
            }
        }
        if (elem instanceof UseCaseInstance) {
            getUseCases().deleteUseCaseInstance(elem);
        }
    }

    /**
     * Delete a StateVertex
     *
     * @param elem the StateVertex to be deleted
     */
    private void deleteStateVertex(StateVertex elem) {
        getStateMachines().deleteStateVertex(elem);
        if (elem instanceof Pseudostate) {
            getStateMachines().deletePseudostate(elem);
        } else if (elem instanceof SynchState) {
            getStateMachines().deleteSynchState(elem);
        } else if (elem instanceof StubState) {
            getStateMachines().deleteStubState(elem);
        } else if (elem instanceof State) {
            if (elem instanceof CompositeState) {
                getStateMachines().deleteCompositeState(elem);
                if (elem instanceof SubmachineState) {
                    getStateMachines().deleteSubmachineState(elem);
                    if (elem instanceof SubactivityState) {
                        getActivityGraphs().deleteSubactivityState(elem);
                    }
                }
            } else if (elem instanceof SimpleState) {
                getStateMachines().deleteSimpleState(elem);
                if (elem instanceof ActionState) {
                    getActivityGraphs().deleteActionState(elem);
                    if (elem instanceof CallState) {
                        getActivityGraphs().deleteCallState(elem);
                    }
                } else if (elem instanceof ObjectFlowState) {
                    getActivityGraphs().deleteObjectFlowState(elem);
                }
            } else if (elem instanceof FinalState) {
                getStateMachines().deleteFinalState(elem);
            }
        }
    }

}
