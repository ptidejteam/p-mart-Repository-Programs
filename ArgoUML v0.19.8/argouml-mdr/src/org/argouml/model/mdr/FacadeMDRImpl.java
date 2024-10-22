// $Id: FacadeMDRImpl.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jmi.reflect.InvalidObjectException;
import javax.jmi.reflect.RefBaseObject;

import org.apache.log4j.Logger;
import org.argouml.model.Facade;
import org.argouml.model.UmlException;
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
import org.omg.uml.behavioralelements.commonbehavior.Argument;
import org.omg.uml.behavioralelements.commonbehavior.AttributeLink;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.CreateAction;
import org.omg.uml.behavioralelements.commonbehavior.DataValue;
import org.omg.uml.behavioralelements.commonbehavior.DestroyAction;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.behavioralelements.commonbehavior.Reception;
import org.omg.uml.behavioralelements.commonbehavior.ReturnAction;
import org.omg.uml.behavioralelements.commonbehavior.SendAction;
import org.omg.uml.behavioralelements.commonbehavior.Signal;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.TerminateAction;
import org.omg.uml.behavioralelements.commonbehavior.UninterpretedAction;
import org.omg.uml.behavioralelements.statemachines.CallEvent;
import org.omg.uml.behavioralelements.statemachines.ChangeEvent;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.Event;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SignalEvent;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.StubState;
import org.omg.uml.behavioralelements.statemachines.SubmachineState;
import org.omg.uml.behavioralelements.statemachines.SynchState;
import org.omg.uml.behavioralelements.statemachines.TimeEvent;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.usecases.Actor;
import org.omg.uml.behavioralelements.usecases.Extend;
import org.omg.uml.behavioralelements.usecases.ExtensionPoint;
import org.omg.uml.behavioralelements.usecases.Include;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.foundation.core.Abstraction;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Comment;
import org.omg.uml.foundation.core.Component;
import org.omg.uml.foundation.core.Constraint;
import org.omg.uml.foundation.core.DataType;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Element;
import org.omg.uml.foundation.core.ElementResidence;
import org.omg.uml.foundation.core.Enumeration;
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
import org.omg.uml.foundation.core.Relationship;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StructuralFeature;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TemplateParameter;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.Usage;
import org.omg.uml.foundation.datatypes.ActionExpression;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.CallConcurrencyKind;
import org.omg.uml.foundation.datatypes.CallConcurrencyKindEnum;
import org.omg.uml.foundation.datatypes.ChangeableKind;
import org.omg.uml.foundation.datatypes.ChangeableKindEnum;
import org.omg.uml.foundation.datatypes.Expression;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.foundation.datatypes.OrderingKind;
import org.omg.uml.foundation.datatypes.OrderingKindEnum;
import org.omg.uml.foundation.datatypes.ParameterDirectionKind;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;
import org.omg.uml.foundation.datatypes.PseudostateKind;
import org.omg.uml.foundation.datatypes.ScopeKind;
import org.omg.uml.foundation.datatypes.ScopeKindEnum;
import org.omg.uml.foundation.datatypes.VisibilityKind;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;
import org.omg.uml.modelmanagement.ElementImport;
import org.omg.uml.modelmanagement.Model;
import org.omg.uml.modelmanagement.Subsystem;
import org.omg.uml.modelmanagement.UmlPackage;

/**
 * Facade implementation.
 */
class FacadeMDRImpl implements Facade {

    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(FacadeMDRImpl.class);

    private MDRModelImplementation implementation;
    
    /**
     * Constructor.
     * 
     * @param impl The model implementation
     */
    public FacadeMDRImpl(MDRModelImplementation impl) {
        this.implementation = impl;
    }

    /**
     * @see org.argouml.model.Facade#isAAbstraction(java.lang.Object)
     */
    public boolean isAAbstraction(Object handle) {
        return handle instanceof Abstraction;
    }

    /**
     * @see org.argouml.model.Facade#isAAction(java.lang.Object)
     */
    public boolean isAAction(Object handle) {
        return handle instanceof Action;
    }

    /**
     * @see org.argouml.model.Facade#isAActionExpression(java.lang.Object)
     */
    public boolean isAActionExpression(Object handle) {
        return handle instanceof ActionExpression;
    }

    /**
     * @see org.argouml.model.Facade#isAActionSequence(java.lang.Object)
     */
    public boolean isAActionSequence(Object handle) {
        return handle instanceof ActionSequence;
    }

    /**
     * @see org.argouml.model.Facade#isAActionState(java.lang.Object)
     */
    public boolean isAActionState(Object handle) {
        return handle instanceof ActionState;
    }

    /**
     * @see org.argouml.model.Facade#isACallState(java.lang.Object)
     */
    public boolean isACallState(Object handle) {
        return handle instanceof CallState;
    }

    /**
     * @see org.argouml.model.Facade#isAObjectFlowState(java.lang.Object)
     */
    public boolean isAObjectFlowState(Object handle) {
        return handle instanceof ObjectFlowState;
    }

    /**
     * @see org.argouml.model.Facade#isASubactivityState(java.lang.Object)
     */
    public boolean isASubactivityState(Object handle) {
        return handle instanceof SubactivityState;
    }

    /**
     * @see org.argouml.model.Facade#isAActor(java.lang.Object)
     */
    public boolean isAActor(Object handle) {
        return handle instanceof Actor;
    }

    /**
     * @see org.argouml.model.Facade#isAAggregationKind(java.lang.Object)
     */
    public boolean isAAggregationKind(Object handle) {
        return handle instanceof AggregationKind;
    }
    
    /**
     * @see org.argouml.model.Facade#isAArgument(java.lang.Object)
     */
    public boolean isAArgument(Object modelElement) {
        return modelElement instanceof Argument;
    }

    /**
     * @see org.argouml.model.Facade#isAAssociation(java.lang.Object)
     */
    public boolean isAAssociation(Object handle) {
        return handle instanceof UmlAssociation;
    }

    /**
     * @see org.argouml.model.Facade#isAAssociationEnd(java.lang.Object)
     */
    public boolean isAAssociationEnd(Object handle) {
        return handle instanceof AssociationEnd;
    }

    /**
     * @see org.argouml.model.Facade#isAAssociationRole(java.lang.Object)
     */
    public boolean isAAssociationRole(Object handle) {
        return handle instanceof AssociationRole;
    }

    /**
     * @see org.argouml.model.Facade#isAAssociationEndRole(java.lang.Object)
     */
    public boolean isAAssociationEndRole(Object handle) {
        return handle instanceof AssociationEndRole;
    }

    /**
     * @see org.argouml.model.Facade#isAAttribute(java.lang.Object)
     */
    public boolean isAAttribute(Object handle) {
        return handle instanceof Attribute;
    }

    /**
     * @see org.argouml.model.Facade#isAsynchronous(java.lang.Object)
     */
    public boolean isAsynchronous(Object handle) {
        if (handle instanceof Action) {
            return ((Action) handle).isAsynchronous();
        }
        return illegalArgumentBoolean(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Cannot get the abstract property of a null object");
        }
        if (handle instanceof Operation) {
            return ((Operation) handle).isAbstract();
        }
        if (handle instanceof GeneralizableElement) {
            return ((GeneralizableElement) handle).isAbstract();
        }
        if (handle instanceof UmlAssociation) {
            return ((UmlAssociation) handle).isAbstract();
        }
        if (handle instanceof Reception) {
            return ((Reception) handle).isAbstract();
        }
        throw new IllegalArgumentException(
                "Can only get the abstract property of an Operation, "
                        + "GeneralizableElement, Association or Reception."
                        + "Got a " + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isAActivityGraph(java.lang.Object)
     */
    public boolean isAActivityGraph(Object handle) {
        return handle instanceof ActivityGraph;
    }

    /**
     * @see org.argouml.model.Facade#isABase(java.lang.Object)
     */
    public boolean isABase(Object handle) {
        return (handle instanceof ModelElement);
    }

    /**
     * @see org.argouml.model.Facade#isABehavioralFeature(java.lang.Object)
     */
    public boolean isABehavioralFeature(Object handle) {
        return handle instanceof BehavioralFeature;
    }

    /**
     * @see org.argouml.model.Facade#isACallAction(java.lang.Object)
     */
    public boolean isACallAction(Object handle) {
        return handle instanceof CallAction;
    }

    /**
     * @see org.argouml.model.Facade#isACallEvent(java.lang.Object)
     */
    public boolean isACallEvent(Object handle) {
        return handle instanceof CallEvent;
    }

    /**
     * @see org.argouml.model.Facade#isAChangeEvent(java.lang.Object)
     */
    public boolean isAChangeEvent(Object handle) {
        return handle instanceof ChangeEvent;
    }

    /**
     * @see org.argouml.model.Facade#isAClass(java.lang.Object)
     */
    public boolean isAClass(Object handle) {
        return handle instanceof UmlClass;
    }

    /**
     * @see org.argouml.model.Facade#isAAssociationClass(java.lang.Object)
     */
    public boolean isAAssociationClass(Object handle) {
        return handle instanceof AssociationClass;
    }

    /**
     * @see org.argouml.model.Facade#isAClassifierAndARelationship(java.lang.Object)
     */
    public boolean isAClassifierAndARelationship(Object handle) {
        return isAClassifier(handle) && isARelationship(handle);
    }

    /**
     * @see org.argouml.model.Facade#isAClassifier(java.lang.Object)
     */
    public boolean isAClassifier(Object handle) {
        return handle instanceof Classifier;
    }

    /**
     * @see org.argouml.model.Facade#isAClassifierInState(java.lang.Object)
     */
    public boolean isAClassifierInState(Object handle) {
        return handle instanceof ClassifierInState;
    }

    /**
     * @see org.argouml.model.Facade#isAClassifierRole(java.lang.Object)
     */
    public boolean isAClassifierRole(Object handle) {
        return handle instanceof ClassifierRole;
    }

    /**
     * @see org.argouml.model.Facade#isAComment(java.lang.Object)
     */
    public boolean isAComment(Object handle) {
        return handle instanceof Comment;
    }

    /**
     * @see org.argouml.model.Facade#isACollaboration(java.lang.Object)
     */
    public boolean isACollaboration(Object handle) {
        return handle instanceof Collaboration;
    }

    /**
     * @see org.argouml.model.Facade#isAComponent(java.lang.Object)
     */
    public boolean isAComponent(Object handle) {
        return handle instanceof Component;
    }

    /**
     * @see org.argouml.model.Facade#isAComponentInstance(java.lang.Object)
     */
    public boolean isAComponentInstance(Object handle) {
        return handle instanceof ComponentInstance;
    }

    /**
     * @see org.argouml.model.Facade#isAConstraint(java.lang.Object)
     */
    public boolean isAConstraint(Object handle) {
        return handle instanceof Constraint;
    }

    /**
     * @see org.argouml.model.Facade#isACreateAction(java.lang.Object)
     */
    public boolean isACreateAction(Object handle) {
        return handle instanceof CreateAction;
    }

    /**
     * @see org.argouml.model.Facade#isADataType(java.lang.Object)
     */
    public boolean isADataType(Object handle) {
        return handle instanceof DataType;
    }

    /**
     * @see org.argouml.model.Facade#isADataValue(java.lang.Object)
     */
    public boolean isADataValue(Object handle) {
        return handle instanceof DataValue;
    }

    /**
     * @see org.argouml.model.Facade#isADependency(java.lang.Object)
     */
    public boolean isADependency(Object handle) {
        return handle instanceof Dependency;
    }

    /**
     * @see org.argouml.model.Facade#isADestroyAction(java.lang.Object)
     */
    public boolean isADestroyAction(Object handle) {
        return handle instanceof DestroyAction;
    }

    /**
     * @see org.argouml.model.Facade#isACompositeState(java.lang.Object)
     */
    public boolean isACompositeState(Object handle) {
        return handle instanceof CompositeState;
    }

    /**
     * @see org.argouml.model.Facade#isAElement(java.lang.Object)
     */
    public boolean isAElement(Object handle) {
        return handle instanceof Element;
    }

    /**
     * @see org.argouml.model.Facade#isAElementImport(java.lang.Object)
     */
    public boolean isAElementImport(Object handle) {
        return handle instanceof ElementImport;
    }

    /**
     * @see org.argouml.model.Facade#isAElementResidence(java.lang.Object)
     */
    public boolean isAElementResidence(Object handle) {
        return handle instanceof ElementResidence;
    }

    /**
     * @see org.argouml.model.Facade#isAEvent(java.lang.Object)
     */
    public boolean isAEvent(Object handle) {
        return handle instanceof Event;
    }

    /**
     * @see org.argouml.model.Facade#isAException(java.lang.Object)
     */
    public boolean isAException(Object handle) {
        return handle instanceof Exception;
    }

    /**
     * @see org.argouml.model.Facade#isAExpression(java.lang.Object)
     */
    public boolean isAExpression(Object handle) {
        return handle instanceof Expression;
    }

    /**
     * @see org.argouml.model.Facade#isAExtend(java.lang.Object)
     */
    public boolean isAExtend(Object handle) {
        return handle instanceof Extend;
    }

    /**
     * @see org.argouml.model.Facade#isAExtensionPoint(java.lang.Object)
     */
    public boolean isAExtensionPoint(Object handle) {
        return handle instanceof ExtensionPoint;
    }

    /**
     * @see org.argouml.model.Facade#isAFeature(java.lang.Object)
     */
    public boolean isAFeature(Object handle) {
        return handle instanceof Feature;
    }

    /**
     * @see org.argouml.model.Facade#isAFinalState(java.lang.Object)
     */
    public boolean isAFinalState(Object handle) {
        return handle instanceof FinalState;
    }

    /**
     * @see org.argouml.model.Facade#isAFlow(java.lang.Object)
     */
    public boolean isAFlow(Object handle) {
        return handle instanceof Flow;
    }

    /**
     * @see org.argouml.model.Facade#isAGuard(java.lang.Object)
     */
    public boolean isAGuard(Object handle) {
        return handle instanceof Guard;
    }

    /**
     * @see org.argouml.model.Facade#isAGeneralizableElement(java.lang.Object)
     */
    public boolean isAGeneralizableElement(Object handle) {
        return handle instanceof GeneralizableElement;
    }

    /**
     * @see org.argouml.model.Facade#isAGeneralization(java.lang.Object)
     */
    public boolean isAGeneralization(Object handle) {
        return handle instanceof Generalization;
    }

    /**
     * @see org.argouml.model.Facade#isAInclude(java.lang.Object)
     */
    public boolean isAInclude(Object handle) {
        return handle instanceof Include;
    }

    /**
     * @see org.argouml.model.Facade#isAInstance(java.lang.Object)
     */
    public boolean isAInstance(Object handle) {
        return handle instanceof Instance;
    }

    /**
     * @see org.argouml.model.Facade#isAInteraction(java.lang.Object)
     */
    public boolean isAInteraction(Object handle) {
        return handle instanceof Interaction;
    }

    /**
     * @see org.argouml.model.Facade#isAInterface(java.lang.Object)
     */
    public boolean isAInterface(Object handle) {
        return handle instanceof Interface;
    }

    /**
     * @see org.argouml.model.Facade#isALink(java.lang.Object)
     */
    public boolean isALink(Object handle) {
        return handle instanceof Link;
    }

    /**
     * @see org.argouml.model.Facade#isALinkEnd(java.lang.Object)
     */
    public boolean isALinkEnd(Object handle) {
        return handle instanceof LinkEnd;
    }

    /**
     * @see org.argouml.model.Facade#isAMessage(java.lang.Object)
     */
    public boolean isAMessage(Object handle) {
        return handle instanceof Message;
    }

    /**
     * @see org.argouml.model.Facade#isAMethod(java.lang.Object)
     */
    public boolean isAMethod(Object handle) {
        return handle instanceof Method;
    }

    /**
     * @see org.argouml.model.Facade#isAModel(java.lang.Object)
     */
    public boolean isAModel(Object handle) {
        return handle instanceof Model;
    }

    /**
     * @see org.argouml.model.Facade#isAModelElement(java.lang.Object)
     */
    public boolean isAModelElement(Object handle) {
        return handle instanceof ModelElement;
    }

    /**
     * @see org.argouml.model.Facade#isAMultiplicity(java.lang.Object)
     */
    public boolean isAMultiplicity(Object handle) {
        return handle instanceof Multiplicity;
    }

    /**
     * @see org.argouml.model.Facade#isAMultiplicityRange(java.lang.Object)
     */
    public boolean isAMultiplicityRange(Object handle) {
        return handle instanceof MultiplicityRange;
    }

    /**
     * @see org.argouml.model.Facade#isANamespace(java.lang.Object)
     */
    public boolean isANamespace(Object handle) {
        return handle instanceof Namespace;
    }

    /**
     * @see org.argouml.model.Facade#isANaryAssociation(java.lang.Object)
     */
    public boolean isANaryAssociation(Object handle) {
        if (handle instanceof UmlAssociation) {
            return (getConnections(handle).size() > 2);
        }
        return false;    		
    }

    /**
     * @see org.argouml.model.Facade#isANode(java.lang.Object)
     */
    public boolean isANode(Object handle) {
        return handle instanceof Node;
    }

    /**
     * @see org.argouml.model.Facade#isANodeInstance(java.lang.Object)
     */
    public boolean isANodeInstance(Object handle) {
        return handle instanceof NodeInstance;
    }

    /**
     * @see org.argouml.model.Facade#isAOperation(java.lang.Object)
     */
    public boolean isAOperation(Object handle) {
        return handle instanceof Operation;
    }

    /**
     * @see org.argouml.model.Facade#isAObject(java.lang.Object)
     */
    public boolean isAObject(Object handle) {
        return handle
        	instanceof org.omg.uml.behavioralelements.commonbehavior.Object;
    }

    /**
     * @see org.argouml.model.Facade#isAParameter(java.lang.Object)
     */
    public boolean isAParameter(Object handle) {
        return handle instanceof Parameter;
    }

    /**
     * @see org.argouml.model.Facade#isAPartition(java.lang.Object)
     */
    public boolean isAPartition(Object handle) {
        return handle instanceof Partition;
    }

    /**
     * @see org.argouml.model.Facade#isAPermission(java.lang.Object)
     */
    public boolean isAPermission(Object handle) {
        return handle instanceof Permission;
    }

    /**
     * @see org.argouml.model.Facade#isAPackage(java.lang.Object)
     */
    public boolean isAPackage(Object handle) {
        return handle instanceof UmlPackage;
    }

    /**
     * @see org.argouml.model.Facade#isAPseudostate(java.lang.Object)
     */
    public boolean isAPseudostate(Object handle) {
        return handle instanceof Pseudostate;
    }

    /**
     * @see org.argouml.model.Facade#isAPseudostateKind(java.lang.Object)
     */
    public boolean isAPseudostateKind(Object handle) {
        return handle instanceof PseudostateKind;
    }

    /**
     * @see org.argouml.model.Facade#getPseudostateKind(java.lang.Object)
     */
    public Object getPseudostateKind(Object handle) {
        if (handle instanceof Pseudostate) {
            return ((Pseudostate) handle).getKind();
        }
        return illegalArgumentObject(handle);    		
    }

    /**
     * @see org.argouml.model.Facade#getKind(java.lang.Object)
     */
    public Object getKind(Object handle) {
        if (handle instanceof Pseudostate) {
            return ((Pseudostate) handle).getKind();
        }
        if (handle instanceof Parameter) {
            return ((Parameter) handle).getKind();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getReceiver(java.lang.Object)
     */
    public Object getReceiver(Object handle) {
        if (handle instanceof Stimulus) {
            return ((Stimulus) handle).getReceiver();
        }
        if (handle instanceof Message) {
            return ((Message) handle).getReceiver();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getLink(java.lang.Object)
     */
    public Object getLink(Object handle) {
        if (handle instanceof LinkEnd) {
            return ((LinkEnd) handle).getLink();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#equalsPseudostateKind( java.lang.Object,
     *      java.lang.Object)
     */
    public boolean equalsPseudostateKind(Object ps1, Object ps2) {
        if (isAPseudostateKind(ps1)) {
            return ((PseudostateKind) ps1).equals(ps2);
        }
        return illegalArgumentBoolean(ps1);    	
    }

    /**
     * @see org.argouml.model.Facade#isAReception(java.lang.Object)
     */
    public boolean isAReception(Object handle) {
        return handle instanceof Reception;
    }

    /**
     * @see org.argouml.model.Facade#isAReturnAction(java.lang.Object)
     */
    public boolean isAReturnAction(Object handle) {
        return handle instanceof ReturnAction;
    }

    /**
     * @see org.argouml.model.Facade#isARelationship(java.lang.Object)
     */
    public boolean isARelationship(Object handle) {
        return handle instanceof Relationship;
    }

    /**
     * @see org.argouml.model.Facade#isASendAction(java.lang.Object)
     */
    public boolean isASendAction(Object handle) {
        return handle instanceof SendAction;
    }

    /**
     * @see org.argouml.model.Facade#isASignal(java.lang.Object)
     */
    public boolean isASignal(Object handle) {
        return handle instanceof Signal;
    }

    /**
     * @see org.argouml.model.Facade#isASignalEvent(java.lang.Object)
     */
    public boolean isASignalEvent(Object handle) {
        return handle instanceof SignalEvent;
    }
    
    /**
     * @see org.argouml.model.Facade#isASimpleState(java.lang.Object)
     */
    public boolean isASimpleState(Object handle) {
        return handle instanceof SimpleState;
    }

    /**
     * @see org.argouml.model.Facade#isAStateMachine(java.lang.Object)
     */
    public boolean isAStateMachine(Object handle) {
        return handle instanceof StateMachine;
    }

    /**
     * @see org.argouml.model.Facade#isAStimulus(java.lang.Object)
     */
    public boolean isAStimulus(Object handle) {
        return handle instanceof Stimulus;
    }

    /**
     * @see org.argouml.model.Facade#isAStateVertex(java.lang.Object)
     */
    public boolean isAStateVertex(Object handle) {
        return handle instanceof StateVertex;
    }

    /**
     * @see org.argouml.model.Facade#isAStereotype(java.lang.Object)
     */
    public boolean isAStereotype(Object handle) {
        return handle instanceof Stereotype;
    }

    /**
     * @see org.argouml.model.Facade#isAStructuralFeature(java.lang.Object)
     */
    public boolean isAStructuralFeature(Object handle) {
        return handle instanceof StructuralFeature;
    }

    /**
     * @see org.argouml.model.Facade#isAState(java.lang.Object)
     */
    public boolean isAState(Object handle) {
        return handle instanceof State;
    }

    /**
     * @see org.argouml.model.Facade#isAStubState(java.lang.Object)
     */
    public boolean isAStubState(Object handle) {
        return handle instanceof StubState;
    }

    /**
     * @see org.argouml.model.Facade#isASubmachineState(java.lang.Object)
     */
    public boolean isASubmachineState(Object handle) {
        return handle instanceof SubmachineState;
    }

    /**
     * @see org.argouml.model.Facade#isASubsystem(java.lang.Object)
     */
    public boolean isASubsystem(Object handle) {
        return handle instanceof Subsystem;
    }

    /**
     * @see org.argouml.model.Facade#isASynchState(java.lang.Object)
     */
    public boolean isASynchState(Object handle) {
        return handle instanceof SynchState;
    }

    /**
     * @see org.argouml.model.Facade#isATaggedValue(java.lang.Object)
     */
    public boolean isATaggedValue(Object handle) {
        return handle instanceof TaggedValue;
    }

    /**
     * @see org.argouml.model.Facade#isATransition(java.lang.Object)
     */
    public boolean isATransition(Object handle) {
        return handle instanceof Transition;
    }

    /**
     * @see org.argouml.model.Facade#isATimeEvent(java.lang.Object)
     */
    public boolean isATimeEvent(Object handle) {
        return handle instanceof TimeEvent;
    }

    /**
     * @see org.argouml.model.Facade#isAUninterpretedAction(java.lang.Object)
     */
    public boolean isAUninterpretedAction(Object handle) {
        return handle instanceof UninterpretedAction;
    }
    
    /**
     * @see org.argouml.model.Facade#isAUsage(java.lang.Object)
     */
    public boolean isAUsage(Object handle) {
        return handle instanceof Usage;
    }

    /**
     * @see org.argouml.model.Facade#isAUseCase(java.lang.Object)
     */
    public boolean isAUseCase(Object handle) {
        return handle instanceof UseCase;
    }

    /**
     * @see org.argouml.model.Facade#isAVisibilityKind(java.lang.Object)
     */
    public boolean isAVisibilityKind(Object handle) {
        return handle instanceof VisibilityKind;
    }

    /**
     * @see org.argouml.model.Facade#isActive(java.lang.Object)
     */
    public boolean isActive(Object handle) {
        if (handle instanceof UmlClass) {
            return ((UmlClass) handle).isActive();
        }
        throw new IllegalArgumentException(
                "Can't determine the active state of a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isChangeable(java.lang.Object)
     */
    public boolean isChangeable(Object handle) {
        if (handle != null && handle instanceof StructuralFeature) {
            ChangeableKind changeability = ((StructuralFeature) handle).
                    getChangeability();
            return ChangeableKindEnum.CK_CHANGEABLE.equals(changeability);

        } else if (handle != null && handle instanceof AssociationEnd) {
            ChangeableKind changeability = ((AssociationEnd) handle).
                    getChangeability();
            return ChangeableKindEnum.CK_CHANGEABLE.equals(changeability);
        }
        throw new IllegalArgumentException(
                "Can't determine the changeability of a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isClassifierScope(java.lang.Object)
     */
    public boolean isClassifierScope(Object handle) {
        if (handle instanceof Attribute) {
            Attribute a = (Attribute) handle;
            return ScopeKindEnum.SK_CLASSIFIER.equals(a.getOwnerScope());
        }
        if (handle instanceof Feature) {
            Feature f = (Feature) handle;
            return ScopeKindEnum.SK_CLASSIFIER.equals(f.getOwnerScope());
        }
        // ...
        return illegalArgumentBoolean(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#isConcurrent(java.lang.Object)
     */
    public boolean isConcurrent(Object handle) {
        if (handle instanceof CompositeState) {
            return ((CompositeState) handle).isConcurrent();
        }
        return illegalArgumentBoolean(handle);        
    }

    /**
     * @see org.argouml.model.Facade#isAConcurrentRegion(java.lang.Object)
     */
    public boolean isAConcurrentRegion(Object handle) {
        if ((handle instanceof CompositeState)
                && (getContainer(handle) != null)) {
            return (isConcurrent(getContainer(handle)));
        }
        return false;    	
    }

    /**
     * @see org.argouml.model.Facade#isConstructor(java.lang.Object)
     */
    public boolean isConstructor(Object handle) {
        Operation operation = null;
        if (handle instanceof Method) {
            operation = ((Method) handle).getSpecification();
            if (operation == null) {
                // This is not a well formed model in a strict sense. See the
                // multiplicity in UML 1.3 Figure 2-5.
                return false;
            }
        } else if (handle instanceof Operation) {
            operation = (Operation) handle;
        } else {
            return illegalArgumentBoolean(handle);
        }

        Object stereo = null;
        Iterator iter = getStereotypes(operation).iterator();
        while (iter.hasNext()) {
            stereo = iter.next();
            if (implementation.getExtensionMechanismsHelper().isStereotypeInh(
                    stereo, "create", "BehavioralFeature")) {
                return true;
            }
        }
        return false;        
    }

    /**
     * @see org.argouml.model.Facade#isFrozen(java.lang.Object)
     */
    public boolean isFrozen(Object handle) {
        if (handle instanceof ChangeableKind) {
            ChangeableKind ck = (ChangeableKind) handle;
            return ChangeableKindEnum.CK_FROZEN.equals(ck);
        }
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isComposite(java.lang.Object)
     */
    public boolean isComposite(Object handle) {
        if (handle instanceof AssociationEnd) {
            return AggregationKindEnum.AK_COMPOSITE.
                    equals(((AssociationEnd) handle).getAggregation());
        }
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isAggregate(java.lang.Object)
     */
    public boolean isAggregate(Object handle) {
        if (handle instanceof AssociationEnd) {
            return AggregationKindEnum.AK_AGGREGATE.
                    equals(((AssociationEnd) handle).getAggregation());
        }
        throw new RuntimeException("Don't know how to return aggregation for "
                + handle);
    }

    /**
     * @see org.argouml.model.Facade#isInitialized(java.lang.Object)
     */
    public boolean isInitialized(Object handle) {
        if (handle instanceof Attribute) {
            Expression init = ((Attribute) handle).getInitialValue();

            if (init != null && init.getBody() != null
                && init.getBody().trim().length() > 0) {
                return true;
            }
            return false;
        }
        // ...
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isInstanceScope(java.lang.Object)
     */
    public boolean isInstanceScope(Object handle) {
        if (handle instanceof Feature) {
            Feature a = (Feature) handle;
            return ScopeKindEnum.SK_INSTANCE.equals(a.getOwnerScope());
        }
        // ...
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isInternal(java.lang.Object)
     */
    public boolean isInternal(Object handle) {
        if (handle instanceof Transition) {
            Object state = getState(handle);
            Object end0 = getSource(handle);
            Object end1 = getTarget(handle);
            if (end0 != null) {
                return ((state == end0) && (state == end1));
            }
        }
        return illegalArgumentBoolean(handle);        
    }

    /**
     * @see org.argouml.model.Facade#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't determine if a null object is a leaf");
        }

        if (handle instanceof GeneralizableElement) {
            return ((GeneralizableElement) handle).isLeaf();
        }
        if (handle instanceof Operation) {
            return ((Operation) handle).isLeaf();
        }
        if (handle instanceof Reception) {
            return ((Reception) handle).isLeaf();
        }
        
        throw new IllegalArgumentException(
                "Expected a GeneralizableElement, Operation or Reception. "
                + "Got a " + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isRoot(java.lang.Object)
     */
    public boolean isRoot(Object handle) {
        if (handle instanceof GeneralizableElement) {
            return ((GeneralizableElement) handle).isRoot();
        }
        if (handle instanceof Operation) {
            return ((Operation) handle).isRoot();
        }
        if (handle instanceof Reception) {
            return ((Reception) handle).isRoot();
        }
        throw new IllegalArgumentException(
                "Expected a GeneralizableElement, Operation or Reception. "
                + "Got a " + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isSpecification(java.lang.Object)
     */
    public boolean isSpecification(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).isSpecification();
        }
        return illegalArgumentBoolean(handle);        
    }

    /**
     * @see org.argouml.model.Facade#isNavigable(java.lang.Object)
     */
    public boolean isNavigable(Object handle) {
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).isNavigable();
        }

        // ...
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isPrimaryObject(java.lang.Object)
     */
    public boolean isPrimaryObject(Object handle) {
        if (handle instanceof ModelElement) {
            Collection c = implementation.getUmlPackage().getCore().
                    getAReferenceValueReferenceTag().getReferenceTag(
                            (ModelElement) handle);
            for (Iterator i = c.iterator(); i.hasNext();) {
                TaggedValue tv = (TaggedValue) i.next();
                if (GENERATED_TAG.equals(tv.getType().getTagType())) {
                    return false;
                }
            }
            return true;
        }
        // ...
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isPrivate(java.lang.Object)
     */
    public boolean isPrivate(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't check if a null object is private");
        }
        if (!(handle instanceof ModelElement)) {
            throw new IllegalArgumentException(
                    "Expecting a ModelElement. Got a "
                    + handle.getClass().getName());
        }
        ModelElement element = (ModelElement) handle;
        return VisibilityKindEnum.VK_PRIVATE.equals(element.getVisibility());
    }

    /**
     * @see org.argouml.model.Facade#isPublic(java.lang.Object)
     */
    public boolean isPublic(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't check if a null object is public");
        }
        if (!(handle instanceof ModelElement)) {
            throw new IllegalArgumentException(
                    "Expecting a ModelElement. Got a "
                    + handle.getClass().getName());
        }
        ModelElement element = (ModelElement) handle;
        return VisibilityKindEnum.VK_PUBLIC.equals(element.getVisibility());
    }

    /**
     * @see org.argouml.model.Facade#isQuery(java.lang.Object)
     */
    public boolean isQuery(Object handle) {
        if (handle instanceof BehavioralFeature) {
            return ((BehavioralFeature) handle).isQuery();
        }
        throw new IllegalArgumentException(
                "Expected a BehavioralFeature. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isProtected(java.lang.Object)
     */
    public boolean isProtected(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't check if a null object is protected");
        }
        if (!(handle instanceof ModelElement)) {
            throw new IllegalArgumentException(
                    "Expecting a ModelElement. Got a "
                    + handle.getClass().getName());
        }
        ModelElement element = (ModelElement) handle;
        return VisibilityKindEnum.VK_PROTECTED.equals(element.getVisibility());
    }

    /**
     * @see org.argouml.model.Facade#isRealize(java.lang.Object)
     */
    public boolean isRealize(Object handle) {
    		return isStereotype(handle, "realize");
    }

    /**
     * @see org.argouml.model.Facade#isReturn(java.lang.Object)
     */
    public boolean isReturn(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't determine if a null object is a return parameter");
        }

        if (handle instanceof Parameter) {
            return ParameterDirectionKindEnum.PDK_RETURN.
                    equals(((Parameter) handle).getKind());
        }
        throw new IllegalArgumentException(
                "Expected a GeneralizableElement, Operation or Reception. "
                + "Got a " + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#isSingleton(java.lang.Object)
     */
    public boolean isSingleton(Object handle) {
    		return isStereotype(handle, "singleton");
    }

    /**
     * @see org.argouml.model.Facade#isStereotype( java.lang.Object,
     *      java.lang.String)
     */
    public boolean isStereotype(Object handle, String stereotypeName) {
        if (handle instanceof ModelElement) {
            Collection stereotypes = ((ModelElement) handle).getStereotype();
            Iterator it = stereotypes.iterator();
            Stereotype stereotype;
            while (it.hasNext()) {
                stereotype = (Stereotype) it.next();
                if (stereotypeName.equals(stereotype.getName()))
                    return true;
            }
            return false;
        }
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#isATerminateAction(java.lang.Object)
     */
    public boolean isATerminateAction(Object handle) {
        return handle instanceof TerminateAction;
    }

    /**
     * @see org.argouml.model.Facade#isTop(java.lang.Object)
     */
    public boolean isTop(Object handle) {
        if (isACompositeState(handle)) {
            return ((CompositeState) handle).getStateMachine() != null;
        }
        return illegalArgumentBoolean(handle);        
    }

    /**
     * @see org.argouml.model.Facade#isType(java.lang.Object)
     */
    public boolean isType(Object handle) {
        return isStereotype(handle, "type");
    }

    /**
     * @see org.argouml.model.Facade#isUtility(java.lang.Object)
     */
    public boolean isUtility(Object handle) {
        return isStereotype(handle, "utility");
    }

    /**
     * @see org.argouml.model.Facade#getAssociation(java.lang.Object)
     */
    public Object getAssociation(Object handle) {
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getAssociation();
        }
        if (handle instanceof Link) {
        		return ((Link) handle).getAssociation();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAssociationEnd( java.lang.Object,
     *      java.lang.Object)
     */
    public Object getAssociationEnd(Object handle, Object assoc) {
        if (handle instanceof Classifier && assoc instanceof UmlAssociation) {
            Classifier classifier = (Classifier) handle;
            Iterator it = getAssociationEnds(classifier).iterator();
            while (it.hasNext()) {
                AssociationEnd end = (AssociationEnd) it.next();
                if (((UmlAssociation) assoc).getConnection().contains(end)) {
                    return end;
                }
            }
            return null;    	
        }
        throw new IllegalArgumentException("handle: " + handle + ",assoc: "
                + assoc);
    }

    /**
     * @see org.argouml.model.Facade#getAssociationEnds(java.lang.Object)
     */
    public Collection getAssociationEnds(Object handle) {
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getCore()
                    .getAParticipantAssociation().getAssociation(
                            (Classifier) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAssociationRoles(java.lang.Object)
     */
    public Collection getAssociationRoles(Object handle) {
        if (handle instanceof UmlAssociation) {
            return implementation.getUmlPackage().getCollaborations()
                    .getABaseAssociationRole().getAssociationRole(
                            (UmlAssociation) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAttributes(java.lang.Object)
     */
    /**
     * The list of Attributes.
     *
     * @param handle
     *            classifier to examine.
     * @return iterator with attributes.
     */
    public Collection getAttributes(Object handle) {     
        if (handle instanceof Classifier) {
            return getStructuralFeatures(handle);
        }
        throw new IllegalArgumentException(
                "Can only get the Attributes of a Classifier. Got a "
                + handle.getClass().getName());
    }


    /**
     * @see org.argouml.model.Facade#getBaseClass(java.lang.Object)
     */
    public Object getBaseClass(Object handle) {
        if (isAStereotype(handle)) {
            Collection stereos = ((Stereotype) handle).getBaseClass();
            if (!stereos.isEmpty()) {
                if (stereos.size() > 1)
                    LOG.warn("There is more than one stereotype defined,"
                            + " returning the first");
                return stereos.iterator().next();
            }
            return null;
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getBase(java.lang.Object)
     */
    public Object getBase(Object handle) {
        if (handle instanceof AssociationEndRole) {
            return ((AssociationEndRole) handle).getBase();
        } else if (handle instanceof AssociationRole) {
            return ((AssociationRole) handle).getBase();
        } else if (handle instanceof Extend) {
            return ((Extend) handle).getBase();
        } else if (handle instanceof Include) {
            return ((Include) handle).getBase();
        } else if (handle instanceof ClassifierRole) {
            return ((ClassifierRole) handle).getBase();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getBases(java.lang.Object)
     */
    public Collection getBases(Object handle) {
        if (handle instanceof ClassifierRole) {
            return ((ClassifierRole) handle).getBase();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getBehaviors(java.lang.Object)
     */
    public Collection getBehaviors(Object handle) {
        if (handle instanceof ModelElement) {
            // TODO: Check if this is the correct method call
            return implementation.getUmlPackage().getStateMachines()
                    .getABehaviorContext().getBehavior((ModelElement) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getBehavioralFeature(java.lang.Object)
     */
    public Object getBehavioralFeature(Object handle) {
        if (handle instanceof Parameter) {
            return ((Parameter) handle).getBehavioralFeature();
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getBody(java.lang.Object)
     */
    public Object getBody(Object handle) {
        if (handle instanceof Method) {
            return ((Method) handle).getBody();
        }
        if (handle instanceof Constraint) {
            return ((Constraint) handle).getBody();
        }
        if (handle instanceof Expression) {
            return ((Expression) handle).getBody();
        }
        if (handle instanceof Comment) {
            // Text was stored in name in UML 1.3
            return ((Comment) handle).getBody();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getBound(java.lang.Object)
     */
    public int getBound(Object handle) {
        if (handle instanceof SynchState) {
            return ((SynchState) handle).getBound();
        }
        return illegalArgumentInt(handle);
    }

    /**
     * @see org.argouml.model.Facade#getChangeability(java.lang.Object)
     */
    public Object getChangeability(Object handle) {
        if (handle instanceof StructuralFeature) {
            return ((StructuralFeature) handle).getChangeability();
        }
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getChangeability();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getChild(java.lang.Object)
     */
    public Object getChild(Object handle) {
        if (handle instanceof Generalization) {
            return ((Generalization) handle).getChild();
        }
        throw new IllegalArgumentException("Expected a Generalization. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object handle) {
        return implementation.getCoreHelper().getChildren(handle);
    }

    /**
     * @see org.argouml.model.Facade#getClassifierRoles(java.lang.Object)
     */
    public Collection getClassifierRoles(Object handle) {
        if (handle instanceof Feature) {
            return implementation.getUmlPackage().getCollaborations()
                    .getAClassifierRoleAvailableFeature().getClassifierRole(
                            (Feature) handle);
        }
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getCollaborations()
                    .getAClassifierRoleBase().getClassifierRole(
                            (Classifier) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getClassifier(java.lang.Object)
     */
    public Object getClassifier(Object handle) {
        if (isAAssociationEnd(handle)) {
            return ((AssociationEnd) handle).getParticipant();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getClassifiers(java.lang.Object)
     */
    public Collection getClassifiers(Object handle) {
        if (handle instanceof Instance) {
            return ((Instance) handle).getClassifier();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getClassifiersInState(java.lang.Object)
     */
    public Collection getClassifiersInState(Object handle) {
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getActivityGraphs()
                    .getATypeClassifierInState().getClassifierInState(
                            (Classifier) handle);
        }
        if (handle instanceof State) {
            return implementation.getUmlPackage().getActivityGraphs()
                    .getAClassifierInStateInState().getClassifierInState(
                            (State) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getClients(java.lang.Object)
     */
    public Collection getClients(Object handle) {
        if (isADependency(handle)) {
            return ((Dependency) handle).getClient();
        }
        throw new IllegalArgumentException("Expected a Dependency. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getClientDependencies(java.lang.Object)
     */
    public Collection getClientDependencies(Object handle) {
        if (handle instanceof ModelElement) {
            return implementation.getUmlPackage().getCore().
                    getAClientClientDependency().getClientDependency(
                            (ModelElement) handle);
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getCondition(java.lang.Object)
     */
    public Object getCondition(Object handle) {
        if (handle instanceof Extend) {
            return ((Extend) handle).getCondition();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getConcurrency(java.lang.Object)
     */
    public Object getConcurrency(Object handle) {
        if (handle instanceof Operation) {
            return ((Operation) handle).getConcurrency();
        }
        return illegalArgumentObject(handle);       
    }

    /**
     * @see org.argouml.model.Facade#getConnections(java.lang.Object)
     */
    public Collection getConnections(Object handle) {
        if (handle instanceof UmlAssociation) {
            return ((UmlAssociation) handle).getConnection();
        }
        if (handle instanceof Link) {
            return ((Link) handle).getConnection();
        }
	return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getEffect(java.lang.Object)
     */
    public Object getEffect(Object handle) {
        if (handle instanceof Transition) {
            return ((Transition) handle).getEffect();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getElementResidences(java.lang.Object)
     */
    public Collection getElementResidences(Object handle) {
        if (handle instanceof ModelElement) {
            return implementation.getUmlPackage().getCore().
                    getAResidentElementResidence().getElementResidence(
                        (ModelElement) handle);
        }
        // ...
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getElementImports2(java.lang.Object)
     */
    public Collection getElementImports2(Object handle) {
        if (handle instanceof ModelElement) {
            return implementation.getUmlPackage().getModelManagement()
                    .getAImportedElementElementImport().getElementImport(
                            (ModelElement) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getEntry(java.lang.Object)
     */
    public Object getEntry(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getEntry();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getExit(java.lang.Object)
     */
    public Object getExit(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getExit();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getExpression(java.lang.Object)
     */
    public Object getExpression(Object handle) {
        if (handle instanceof Guard) {
            return ((Guard) handle).getExpression();
        }
        if (handle instanceof ChangeEvent) {
            return ((ChangeEvent) handle).getChangeExpression();
        }
        if (handle instanceof TimeEvent) {
            return ((TimeEvent) handle).getWhen();
        }
        if (handle instanceof Argument) {
            return ((Argument) handle).getValue();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getExtends(java.lang.Object)
     */
    public Collection getExtends(Object handle) {
        if (handle instanceof UseCase) {
            return ((UseCase) handle).getExtend();
        }
        if (handle instanceof ExtensionPoint) {
            return implementation.getUmlPackage().getUseCases().
                    getAExtensionPointExtend().getExtend(
                            (ExtensionPoint) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * Return extenders
     * @see org.argouml.model.Facade#getExtends2(java.lang.Object)
     */
    public Collection getExtends2(Object handle) {
        if (handle instanceof UseCase) {
            return implementation.getUmlPackage().getUseCases()
                    .getABaseExtender().getExtender((UseCase) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getExtension(java.lang.Object)
     */
    public Object getExtension(Object handle) {
        if (handle instanceof Extend) {
            return ((Extend) handle).getExtension();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getExtensionPoint(java.lang.Object, int)
     */
    public Object getExtensionPoint(Object handle, int index) {
        if (handle instanceof Extend) {
            return ((Extend) handle).getExtensionPoint().get(index);
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getExtensionPoints(java.lang.Object)
     */
    public Collection getExtensionPoints(Object handle) {
        if (handle instanceof UseCase) {
            return ((UseCase) handle).getExtensionPoint();
        }
        if (handle instanceof Extend) {
            return ((Extend) handle).getExtensionPoint();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getFeatures(java.lang.Object)
     */
    public Collection getFeatures(Object handle) {
        if (handle instanceof Classifier) {
                return ((Classifier) handle).getFeature();                
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getGeneralization( java.lang.Object,
     *      java.lang.Object)
     */
    public Object getGeneralization(Object handle, Object parent) {
        if (handle instanceof GeneralizableElement
                && parent instanceof GeneralizableElement) {
            Iterator it = getGeneralizations(handle).iterator();
            while (it.hasNext()) {
                Generalization gen = (Generalization) it.next();
                if (gen.getParent() == parent) {
                    return gen;
                }
            }
            return null;
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getGeneralizations(java.lang.Object)
     */
    public Collection getGeneralizations(Object handle) {
        if (handle instanceof GeneralizableElement) {
            return implementation.getUmlPackage().getCore().
                    getAChildGeneralization().getGeneralization(
                            (GeneralizableElement) handle);
        }
        throw new IllegalArgumentException(
                "Can only get Generalizations of a GenerizableElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getGuard(java.lang.Object)
     */
    public Object getGuard(Object handle) {
        if (isATransition(handle)) {
            return ((Transition) handle).getGuard();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getIcon(java.lang.Object)
     */
    public Object getIcon(Object handle) {
        if (handle instanceof Stereotype) {
            return ((Stereotype) handle).getIcon();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getImplementationLocation(java.lang.Object)
     */
    public Object getImplementationLocation(Object handle) {
        if (handle instanceof ElementResidence) {
            return implementation.getUmlPackage().getCore()
                    .getAContainerResidentElement().getContainer(
                            (ElementResidence) handle);
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getIncludes(java.lang.Object)
     */
    public Collection getIncludes(Object handle) {
        if (handle instanceof UseCase) {
            return ((UseCase) handle).getInclude();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * Return includers
     * @see org.argouml.model.Facade#getIncludes2(java.lang.Object)
     */
    public Collection getIncludes2(Object handle) {
        if (handle instanceof UseCase) {
            return implementation.getUmlPackage().getUseCases()
                    .getAIncluderAddition().getIncluder((UseCase) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getIncomings(java.lang.Object)
     */
    public Collection getIncomings(Object handle) {
        if (isAGuard(handle) || isAAction(handle)) {
            return getIncomings(getTransition(handle));
        } 
        if (isAEvent(handle)) {
            Iterator trans = getTransitions(handle).iterator();
            Collection incomings = new ArrayList();
            while (trans.hasNext()) {
                incomings.addAll(getIncomings(trans.next()));
            }
            return incomings;
        }
        if (isAStateVertex(handle)) {
            return ((StateVertex) handle).getIncoming();
        }
        //TODO: Check if this is the right interpretation of 
        //getIncoming for a Transition        
        if (isATransition(handle)) {
            return ((Transition) handle).getSource().getIncoming();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getInitialValue(java.lang.Object)
     */
    public Object getInitialValue(Object handle) {
        if (handle instanceof Attribute) {
            return ((Attribute) handle).getInitialValue();
        }
	return illegalArgumentCollection(handle);
    }
    
    /**
     * @see org.argouml.model.Facade#getInstance(java.lang.Object)
     */
    public Object getInstance(Object handle) {
        if (handle instanceof AttributeLink) {
            return ((AttributeLink) handle).getInstance();
        }
        if (handle instanceof LinkEnd) {
            return ((LinkEnd) handle).getInstance();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getInstances(java.lang.Object)
     */
    public Collection getInstances(Object handle) {
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getCommonBehavior()
                    .getAInstanceClassifier().getInstance((Classifier) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getInStates(java.lang.Object)
     */
    public Collection getInStates(Object handle) {
        if (handle instanceof ClassifierInState) {
            return ((ClassifierInState) handle).getInState();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getInteraction(java.lang.Object)
     */
    public Object getInteraction(Object handle) {
        if (handle instanceof Message) {
            return ((Message) handle).getInteraction();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getInteractions(java.lang.Object)
     */
    public Collection getInteractions(Object handle) {
        if (handle instanceof Collaboration) {
            return ((Collaboration) handle).getInteraction();
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getInternalTransitions(java.lang.Object)
     */
    public Collection getInternalTransitions(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getInternalTransition();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getMessages(java.lang.Object)
     */
    public Collection getMessages(Object handle) {
        if (isAInteraction(handle)) {
            return ((Interaction) handle).getMessage();
        }
        if (handle instanceof AssociationRole) {
            return ((AssociationRole) handle).getMessage();
        }
        if (handle instanceof Action) {        	
            return implementation.getUmlPackage().getCollaborations().
                    getAActionMessage().getMessage(((Action) handle));
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * Get Successors to the given message
     * 
     * @see org.argouml.model.Facade#getMessages3(java.lang.Object)
     */
    public Collection getMessages3(Object handle) {
        if (handle instanceof Message) {
            // TODO: remove this copy when Argo handles live collections
            // ParserDisplay fixed for 0.19.7 - remove this copy after release
            return Collections.unmodifiableCollection(new ArrayList(
                    implementation.getUmlPackage().getCollaborations()
                            .getAPredecessorSuccessor().getSuccessor(
                                    (Message) handle)));
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * Get the messages that are activated by the given message.
     * 
     * @see org.argouml.model.Facade#getMessages4(java.lang.Object)
     */
    public Collection getMessages4(Object handle) {
        if (handle instanceof Message) {
            return implementation.getUmlPackage().getCollaborations().
                    getAMessageActivator().getMessage((Message) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * Returns the messages received by the given classifier role.
     * 
     * @see org.argouml.model.Facade#getMessages1(java.lang.Object)
     */
    public Collection getMessages1(Object handle) {
        if (handle instanceof ClassifierRole) {
            return implementation.getUmlPackage().getCollaborations().
                    getAReceiverMessage().getMessage((ClassifierRole) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * Returns the messages send by the given classifier role.
     * 
     * @see org.argouml.model.Facade#getMessages2(java.lang.Object)
     */
    public Collection getMessages2(Object handle) {
        if (handle instanceof ClassifierRole) {
            return implementation.getUmlPackage().getCollaborations().
                    getAMessageSender().getMessage((ClassifierRole) handle);
        }
        return illegalArgumentCollection(handle);    	    	
    }

    /**
     * Return the model for this object.
     * 
     * @see org.argouml.model.Facade#getModel(java.lang.Object)
     */
    public Object getModel(Object handle) {
        if (isAModel(handle)) {
            return handle;
        }
        if (isAFeature(handle)) {
            Classifier owner = ((Feature) handle).getOwner();
            if ( owner == null) {
                return getModel(getNamespace(handle));
            } else {
                return getModel(owner);                
            }
        }
        if (isAParameter(handle)) {
            if (getBehavioralFeature(handle) != null)
                return getModel(getBehavioralFeature(handle));
            Object event = implementation.getUmlPackage().getStateMachines().
                getAEventParameter().getEvent((Parameter) handle);
            if (event != null)
                return getModel(event);
        }
        if (isAState(handle)) {
            Object stateMachine = getStateMachine(handle);
            if (stateMachine != null)
                return getModel(getContext(stateMachine));
            else {
                Object container = getContainer(handle);
                if (container != null)
                    return getModel(container);
            }
        }
        if (isATransition(handle)) {
            return getModel(getContext(getStateMachine(handle)));
        }
        if (isATaggedValue(handle)) {
            return getModel(((TaggedValue) handle).getModelElement());
        }
        if (isAModelElement(handle)) {
            Namespace ns = (Namespace) getNamespace(handle);
            if (ns instanceof Model) {
                return ns;
            }
            if (ns != null) {
                return getModel(ns);
            } else {
                // We probably won't be able to get the name of the
                // element either, so don't try to log it
                LOG.error("Cannot find model for "
//                        + ((ModelElement) handle).getName() 
                        + ": " + handle
                        + "/ " + ns);
                return null;
            }
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getModelElement(java.lang.Object)
     */
    public Object getModelElement(Object handle) {
        if (handle instanceof ElementImport) {
    	    		return ((ElementImport) handle).getImportedElement();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getMultiplicity(java.lang.Object)
     */
    public Object getMultiplicity(Object handle) {
        if (handle instanceof StructuralFeature) {
            StructuralFeature sf = (StructuralFeature) handle;
            return sf.getMultiplicity();
        }
        if (handle instanceof TagDefinition) {
            TagDefinition td = (TagDefinition) handle;
            return td.getMultiplicity();
        } 
        if (handle instanceof ClassifierRole) {
            ClassifierRole cr = (ClassifierRole) handle;
            return cr.getMultiplicity();
        }
        if (handle instanceof AssociationEnd) {
            AssociationEnd ae = (AssociationEnd) handle;
            return ae.getMultiplicity();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getRanges(java.lang.Object)
     */
    public Iterator getRanges(Object handle) {
        if (handle instanceof Multiplicity) {
            Collection c = ((Multiplicity) handle).getRange();
            return c.iterator();
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    /**
     * @see org.argouml.model.Facade#getComments(java.lang.Object)
     */
    public Collection getComments(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getComment();
        }
        // ...
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getAnnotatedElements(java.lang.Object)
     */
    public Collection getAnnotatedElements(Object handle) {
        if (handle instanceof Comment) {
            return ((Comment) handle).getAnnotatedElement();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getCommunicationConnection(java.lang.Object)
     */
    public Object getCommunicationConnection(Object handle) {
        if (handle instanceof Message) {
            return ((Message) handle).getCommunicationConnection();
        }
        // ...
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getCommunicationLink(java.lang.Object)
     */
    public Object getCommunicationLink(Object handle) {
        if (handle instanceof Stimulus) {
            return ((Stimulus) handle).getCommunicationLink();
        }
        // ...
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getCollaborations(java.lang.Object)
     */
    public Collection getCollaborations(Object handle) {
        if (handle instanceof Operation) {
            return implementation.getUmlPackage().getCollaborations()
                    .getARepresentedOperationCollaboration().getCollaboration(
                            (Operation) handle);
        }
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getCollaborations()
                    .getARepresentedClassifierCollaboration().getCollaboration(
                            (Classifier) handle);
        }
        // ...
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getComponentInstance(java.lang.Object)
     */
    public Object getComponentInstance(Object handle) {
        if (handle instanceof Instance) {
            return ((Instance) handle).getComponentInstance();
        }
        // ...
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getConstrainingElements(java.lang.Object)
     */
    public Collection getConstrainingElements(Object handle) {
        if (handle instanceof Collaboration) {
            return ((Collaboration) handle).getConstrainingElement();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getConstrainedElements(java.lang.Object)
     */
    public Collection getConstrainedElements(Object handle) {
        if (handle instanceof Constraint) {
            return ((Constraint) handle).getConstrainedElement();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getConstraints(java.lang.Object)
     */
    public Collection getConstraints(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getConstraint();
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getModelElementContainer(java.lang.Object)
     */
    public Object getModelElementContainer(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getNamespace();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getContainer(java.lang.Object)
     */
    public Object getContainer(Object handle) {
        if (isAGuard(handle) || isAAction(handle)) {
            return getContainer(getTransition(handle));
        }
        if (isAEvent(handle)) {
            Object container;
            Iterator it = getTransitions(handle).iterator();
            while (it.hasNext()) {
                container = getContainer(it.next());
                if (container != null)
                    return container;
            }
        }        
        if (handle instanceof StateVertex) {
            return ((StateVertex) handle).getContainer();
        }
        //TODO: Check if this is the right interpretation of 
        //getContainer for a Transition.
        if (isATransition(handle)) {        	
            return ((Transition) handle).getStateMachine().getTop().
                    getContainer();
        }           
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getContents(java.lang.Object)
     */
    public Collection getContents(Object handle) {
        if (handle instanceof Partition) {
            return ((Partition) handle).getContents();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getContext(java.lang.Object)
     */
    public Object getContext(Object handle) {
        if (isAStateMachine(handle)) {
            return ((StateMachine) handle).getContext();
        }
        if (isAInteraction(handle)) {
            return ((Interaction) handle).getContext();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getContexts(java.lang.Object)
     */
    public Collection getContexts(Object handle) {
        if (handle instanceof Signal) {
            return implementation.getUmlPackage().getCommonBehavior().
            	getAContextRaisedSignal().getContext((Signal) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getCreateActions(java.lang.Object)
     */
    public Collection getCreateActions(Object handle) {
        if (handle instanceof Classifier) {
        	return implementation.getUmlPackage().getCommonBehavior().
                    getACreateActionInstantiation().getCreateAction(
                            (Classifier) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getDefaultValue(java.lang.Object)
     */
    public Object getDefaultValue(Object handle) {
        if (handle instanceof Parameter) {
            return ((Parameter) handle).getDefaultValue();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getDeferrableEvents(java.lang.Object)
     */
    public Collection getDeferrableEvents(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getDeferrableEvent();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getDeploymentLocations(java.lang.Object)
     */
    public Collection getDeploymentLocations(Object handle) {
        if (handle instanceof Component) {
            return ((Component) handle).getDeploymentLocation();
        }
        throw new IllegalArgumentException("handle: " + handle);
    }

    /**
     * @see org.argouml.model.Facade#getDiscriminator(java.lang.Object)
     */
    public Object getDiscriminator(Object handle) {
        if (handle instanceof Generalization) {
            return ((Generalization) handle).getDiscriminator();
        }
        throw new IllegalArgumentException("Expected a Generalization. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getDispatchAction(java.lang.Object)
     */
    public Object getDispatchAction(Object handle) {
        if (handle instanceof Stimulus) {
            return ((Stimulus) handle).getDispatchAction();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getDoActivity(java.lang.Object)
     */
    public Object getDoActivity(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getDoActivity();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getLinks(java.lang.Object)
     */
    public Collection getLinks(Object handle) {
        if (handle instanceof UmlAssociation) {
            return implementation.getUmlPackage().getCommonBehavior().
                    getAAssociationLink().getLink((UmlAssociation) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getLinkEnds(java.lang.Object)
     */
    public Collection getLinkEnds(Object handle) {
        if (handle instanceof AssociationEnd) {
            return implementation.getUmlPackage().getCommonBehavior().
                    getAAssociationEndLinkEnd().getLinkEnd(
                            (AssociationEnd) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getLocation(java.lang.Object)
     */
    public String getLocation(Object handle) {
        if (handle instanceof ExtensionPoint) {
            return ((ExtensionPoint) handle).getLocation();
        }
        return illegalArgumentString(handle);
    }

    /**
     * @see org.argouml.model.Facade#getMethods(java.lang.Object)
     */
    public Collection getMethods(Object handle) {
        if (handle instanceof Operation) {
            return implementation.getUmlPackage().getCore()
                    .getASpecificationMethod().getMethod((Operation) handle);
        }
        // ...
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getNamespace(java.lang.Object)
     */
    public Object getNamespace(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't get the namespace of a null object");
        }
        
        if (!(handle instanceof ModelElement)) {
            throw new IllegalArgumentException(
                    "Expected a ModelElement. Got a "
                    + handle.getClass().getName());
        }
        try {
            return ((ModelElement) handle).getNamespace();
        } catch (InvalidObjectException e) {
            // TODO: find out why we get deleted elements here - tfm
            return null;
        }
    }

    /**
     * @see org.argouml.model.Facade#getNodeInstance(java.lang.Object)
     */
    public Object getNodeInstance(Object handle) {
        if (handle instanceof ComponentInstance) {
            return ((ComponentInstance) handle).getNodeInstance();
        }
        // ...
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getObjectFlowStates(java.lang.Object)
     */
    public Collection getObjectFlowStates(Object handle) {
        if (handle instanceof Classifier) {
            return implementation.getUmlPackage().getActivityGraphs()
                    .getATypeObjectFlowState().getObjectFlowState(
                            (Classifier) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getOperation(java.lang.Object)
     */
    public Object getOperation(Object handle) {
        if (handle instanceof CallAction) {
            return ((CallAction) handle).getOperation();
        }
        if (handle instanceof CallEvent) {
            return ((CallEvent) handle).getOperation();
        }
        // ...
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getOccurrences(java.lang.Object)
     */
    public Collection getOccurrences(Object handle) {
        if (handle instanceof Operation) {
            return implementation.getUmlPackage().getStateMachines()
                    .getAOccurrenceOperation()
                    .getOccurrence((Operation) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getOperations(java.lang.Object)
     */
    public Collection getOperations(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException("A null Operation is invalid");
        }
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException("Expected a Classifier. Got a "
                    + handle.getClass().getName());
        }
        Classifier mclassifier = (Classifier) handle;
        Collection result = new ArrayList();
        Iterator features = mclassifier.getFeature().iterator();
        while (features.hasNext()) {
            Feature feature = (Feature) features.next();
            if (feature instanceof Operation) {
                result.add(feature);
            }
        }
        // TODO: remove this copy when Argo handles live collections
        // ParserDisplay fixed for 0.19.7 - remove this copy after release
        return Collections.unmodifiableCollection(result);
    }

    /**
     * @see org.argouml.model.Facade#getOppositeEnd(java.lang.Object)
     */
    public Object getOppositeEnd(Object handle) {
        if (handle instanceof AssociationEnd) {
            List assocEnds = (((AssociationEnd) handle).getAssociation()).
                    getConnection();
            if (assocEnds.size() > 2)
                LOG.warn("There is more than 2 associations ends, " 
                        + "returning the first opposite");
            Object opposite = null;
            Iterator it = assocEnds.iterator();
            while (it.hasNext()) {
                opposite = it.next();
                if (opposite != handle)
                    break;
            }
            return opposite;
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getOrdering(java.lang.Object)
     */
    public Object getOrdering(Object handle) {
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getOrdering();
        }

        // ...
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getOutgoings(java.lang.Object)
     */
    public Collection getOutgoings(Object handle) {
        if (isAGuard(handle) || isAAction(handle)) {
            return getOutgoings(getTransition(handle));
        }
        if (isAEvent(handle)) {
            Iterator trans = getTransitions(handle).iterator();
            Collection outgoings = new ArrayList();
            while (trans.hasNext()) {
                outgoings.addAll(getOutgoings(trans.next()));
            }
            return outgoings;
        }        
        if (isAStateVertex(handle)) {
            return ((StateVertex) handle).getOutgoing();
        }
        //TODO: Check if this is the right interpretation of 
        //getOutgoing for a Transition        
        if (isATransition(handle)) {
            return ((Transition) handle).getTarget().getOutgoing();
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getOtherAssociationEnds(java.lang.Object)
     */
    public Collection getOtherAssociationEnds(Object handle) {
        if (handle instanceof AssociationEnd) {
            UmlAssociation a = ((AssociationEnd) handle).getAssociation();

            if (a == null) {
                return Collections.EMPTY_LIST;
            }

            Collection allEnds = a.getConnection();
            if (allEnds == null) {
                return Collections.EMPTY_LIST;
            }

            // TODO: An Iterator filter would be nice here instead of the
            // mucking around with the Collection.
            allEnds = new ArrayList(allEnds);
            allEnds.remove(handle);
            return allEnds;
        }

        // ...
        return illegalArgumentCollection(handle);        
    }

    /**
     * Get the list of Link Ends connected to this link end.
     *
     * @param handle link end to start from
     * @return Iterator with all connected link ends.
     */
    public Collection getOtherLinkEnds(Object handle) {
        if (handle instanceof LinkEnd) {
            Link link = ((LinkEnd) handle).getLink();

            if (link == null) {
                return Collections.EMPTY_LIST;
            }

            Collection allEnds = link.getConnection();
            if (allEnds == null) {
                return Collections.EMPTY_LIST;
            }

            // TODO: An Iterator filter would be nice here instead of the
            // mucking around with the Collection.
            allEnds = new ArrayList(allEnds);
            allEnds.remove(handle);
            return allEnds;
        }

        // ...
    return illegalArgumentCollection(handle);
    }

    
    /**
     * @see org.argouml.model.Facade#getOwnedElements(java.lang.Object)
     */
    public Collection getOwnedElements(Object handle) {
        
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't get the owned elements of a null object");
        }
        
        if (!(handle instanceof Namespace)) {
            throw new IllegalArgumentException(
                    "Can't get the owned elements of a "
                            + handle.getClass().getName());
        }
        return ((Namespace) handle).getOwnedElement();
    }

    /**
     * @see org.argouml.model.Facade#getOwnerScope(java.lang.Object)
     */
    public Object getOwnerScope(Object handle) {
        if (handle instanceof Feature) {
            return ((Feature) handle).getOwnerScope();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getPowertype(java.lang.Object)
     */
    public Object getPowertype(Object handle) {
        if (handle instanceof Generalization) {
            return ((Generalization) handle).getPowertype();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getPowertypeRanges(java.lang.Object)
     */
    public Collection getPowertypeRanges(Object handle) {
        if (handle instanceof Classifier) {
            return ((Classifier) handle).getPowertypeRange();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getPredecessors(java.lang.Object)
     */
    public Collection getPredecessors(Object handle) {
        if (handle instanceof Message) {
            // TODO: remove this copy when Argo handles live collections
            // ParserDisplay fixed for 0.19.7 - remove this copy after release
            return Collections.unmodifiableCollection(
                    new ArrayList(((Message) handle).getPredecessor()));
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getQualifiers(java.lang.Object)
     */
    public Collection getQualifiers(Object handle) {
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getQualifier();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#hasReturnParameterDirectionKind(java.lang.Object)
     */
    public boolean hasReturnParameterDirectionKind(Object handle) {
        if (handle instanceof Parameter) {
            Parameter parameter = (Parameter) handle;
            return (ParameterDirectionKindEnum.PDK_RETURN.equals(parameter.
                    getKind()));
        }
        return illegalArgumentBoolean(handle);
    }

    /**
     * @see org.argouml.model.Facade#getPackage(java.lang.Object)
     */
    public Object getPackage(Object handle) {
        if (handle instanceof ElementImport) {
            return ((ElementImport) handle).getUmlPackage();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getParameter(java.lang.Object, int)
     */
    public Object getParameter(Object handle, int n) {
        if (handle instanceof BehavioralFeature) {
            return ((BehavioralFeature) handle).getParameter().get(n);
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getParameters(java.lang.Object)
     */
    public Collection getParameters(Object handle) {
        Collection results = null;
        if (handle instanceof ObjectFlowState) {
            results = ((ObjectFlowState) handle).getParameter();
        } else if (handle instanceof BehavioralFeature) {
            results = ((BehavioralFeature) handle).getParameter();
        } else if (handle instanceof Event) {
            results = ((Event) handle).getParameter();
        } else if (handle instanceof Classifier) {
            results = implementation.getUmlPackage().getCore().
            		getATypedParameterType().getTypedParameter(
            				(Classifier) handle);
        }
        if (results != null) {
            // TODO: remove this copy when Argo handles live collections
            // ParserDisplay fixed for 0.19.7 - remove this copy after release
            return Collections.unmodifiableCollection(new ArrayList(results));
        } else {
            return illegalArgumentCollection(handle);
        }
    }

    /**
     * @see org.argouml.model.Facade#getParent(java.lang.Object)
     */
    public Object getParent(Object handle) {
        if (handle instanceof Generalization) {
            return ((Generalization) handle).getParent();
        }
        throw new IllegalArgumentException("Expected a Generalization. Got a "
            + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getRaisedSignals(java.lang.Object)
     */
    public Collection getRaisedSignals(Object handle) {
        if (handle instanceof BehavioralFeature) {
            return implementation.getUmlPackage().getCommonBehavior()
                    .getAContextRaisedSignal().getRaisedSignal(
                            (BehavioralFeature) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getReceptions(java.lang.Object)
     */
    public Collection getReceptions(Object handle) {
        if (handle instanceof Signal) {
            return implementation.getUmlPackage().getCommonBehavior()
                    .getASignalReception().getReception((Signal) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getRecurrence(java.lang.Object)
     */
    public Object getRecurrence(Object handle) {
        if (handle instanceof Action) {
            return ((Action) handle).getRecurrence();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getRepresentedClassifier(java.lang.Object)
     */
    public Object getRepresentedClassifier(Object handle) {
        if (handle instanceof Collaboration) {
            return ((Collaboration) handle).getRepresentedClassifier();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getRepresentedOperation(java.lang.Object)
     */
    public Object getRepresentedOperation(Object handle) {
        if (handle instanceof Collaboration) {
            return ((Collaboration) handle).getRepresentedOperation();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getScript(java.lang.Object)
     */
    public Object getScript(Object handle) {
        if (handle instanceof Action) {
            return ((Action) handle).getScript();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getSender(java.lang.Object)
     */
    public Object getSender(Object handle) {
        if (handle instanceof Stimulus) {
            return ((Stimulus) handle).getSender();
        }
        if (handle instanceof Message) {
            return ((Message) handle).getSender();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getSignal(java.lang.Object)
     */
    public Object getSignal(Object handle) {
        if (handle instanceof SendAction) {
            return ((SendAction) handle).getSignal();
        }
        if (handle instanceof SignalEvent) {
            return ((SignalEvent) handle).getSignal();
        }
        if (handle instanceof Reception) {
            return ((Reception) handle).getSignal();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getResident(java.lang.Object)
     */
    public Object getResident(Object handle) {
        if (handle instanceof ElementResidence) {
            return ((ElementResidence) handle).getResident();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getResidentElements(java.lang.Object)
     */
    public Collection getResidentElements(Object handle) {
        if (handle instanceof Component) {
            return ((Component) handle).getResidentElement();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getResidents(java.lang.Object)
     */
    public Collection getResidents(Object handle) {
        if (isANodeInstance(handle)) {
            return ((NodeInstance) handle).getResident();
        }
        if (isAComponentInstance(handle)) {
            return ((ComponentInstance) handle).getResident();
        }
        if (handle instanceof ModelElement) {
            return implementation.getUmlPackage().getCore().
                    getAResidentElementResidence().getElementResidence(
                            (ModelElement) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getSource(java.lang.Object)
     */
    public Object getSource(Object handle) {
        if (isATransition(handle)) {
            return ((Transition) handle).getSource();
        }
        return illegalArgumentObject(handle);    		
    }

    /**
     * @see org.argouml.model.Facade#getSources(java.lang.Object)
     */
    public Collection getSources(Object handle) {
        if (handle instanceof Flow) {
            return ((Flow) handle).getSource();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getSourceFlows(java.lang.Object)
     */
    public Collection getSourceFlows(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getSourceFlow();
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getSpecializations(java.lang.Object)
     */
    public Collection getSpecializations(Object handle) {
        if (handle instanceof GeneralizableElement) {
            return implementation.getUmlPackage().getCore().
            		getAParentSpecialization().getSpecialization(
            				(GeneralizableElement) handle);
        }
        throw new IllegalArgumentException(
                "Can only get Specializations of a GenerizableElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getStateMachine(java.lang.Object)
     */
    public Object getStateMachine(Object handle) {
        if (handle instanceof State) {
            return ((State) handle).getStateMachine();
        }
        if (handle instanceof Transition) {
            return ((Transition) handle).getStateMachine();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getState(java.lang.Object)
     */
    public Object getState(Object handle) {
        if (handle instanceof Transition) {
            return implementation.getUmlPackage().getStateMachines().
                    getAStateInternalTransition().
                    getState((Transition) handle);
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getStates(java.lang.Object)
     */
    public Collection getStates(Object handle) {
        if (handle instanceof Event) {
            return implementation.getUmlPackage().getStateMachines().
        		getAStateDeferrableEvent().getState((Event) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getStereotypes(java.lang.Object)
     */
    public Collection getStereotypes(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException(
                    "Can't get the stereotypes of a null object");
        }
        
        if (!(handle instanceof ModelElement)) {
            throw new IllegalArgumentException(
                    "Expected a ModelElement. Got a "
                    + handle.getClass().getName());
        }
        return ((ModelElement) handle).getStereotype();
    }

    /**
     * Returns the stimuli belonging to some given link.
     * 
     * @see org.argouml.model.Facade#getStimuli(java.lang.Object)
     */
    public Collection getStimuli(Object handle) {
        if (isALink(handle)) {
            return implementation.getUmlPackage().getCommonBehavior().
                    getAStimulusCommunicationLink().getStimulus((Link) handle);
        }
        if (isAAction(handle)) {
            return implementation.getUmlPackage().getCommonBehavior().
                    getADispatchActionStimulus().getStimulus((Action) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * Returns the Stimuli that are received by the given Instance.
     * 
     * @param handle
     *            the Instance
     * @return the collection of stimuli
     */
    public Collection getStimuli2(Object handle) {
        if (handle instanceof Instance) {
            return implementation.getUmlPackage().getCommonBehavior()
                    .getAReceiverStimulus().getStimulus((Instance) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * Returns the Stimuli that are sent by the given Instance.
     * 
     * @see org.argouml.model.Facade#getStimuli3(java.lang.Object)
     */
    public Collection getStimuli3(Object handle) {
        if (handle instanceof Instance) {
            return implementation.getUmlPackage().getCommonBehavior()
                    .getAStimulusSender().getStimulus((Instance) handle);
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getSubvertices(java.lang.Object)
     */
    public Collection getSubvertices(Object handle) {
        if (isACompositeState(handle)) {
            return ((CompositeState) handle).getSubvertex();
        }
        return illegalArgumentCollection(handle);    
    }

    /**
     * @see org.argouml.model.Facade#getSubmachine(java.lang.Object)
     */
    public Object getSubmachine(Object handle) {
        if (handle instanceof SubmachineState) {
            return ((SubmachineState) handle).getSubmachine();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getSubmachineStates(java.lang.Object)
     */
    public Collection getSubmachineStates(Object handle) {
        if (handle instanceof StateMachine) {
            return ((StateMachine) handle).getSubmachineState();
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getSupplierDependencies(java.lang.Object)
     */
    public Collection getSupplierDependencies(Object handle) {
        if (handle instanceof ModelElement) {
            return implementation.getUmlPackage().getCore()
                    .getASupplierSupplierDependency().getSupplierDependency(
                            (ModelElement) handle);
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getTop(java.lang.Object)
     */
    public Object getTop(Object handle) {
        if (handle instanceof StateMachine) {
            return ((StateMachine) handle).getTop();
        }
        return illegalArgumentObject(handle);    		
    }

    /**
     * @see org.argouml.model.Facade#getTransition(java.lang.Object)
     */
    public Object getTransition(Object handle) {
        if (handle instanceof Guard) {
            return ((Guard) handle).getTransition();
        }
        if (handle instanceof Action) {
            return implementation.getUmlPackage().getStateMachines().
            		getATransitionEffect().getTransition((Action) handle);
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getTrigger(java.lang.Object)
     */
    public Object getTrigger(Object handle) {
        if (handle instanceof Transition) {
            return ((Transition) handle).getTrigger();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getType(java.lang.Object)
     */
    public Object getType(Object handle) {
        if (handle instanceof StructuralFeature) {
            return ((Attribute) handle).getType();
        }
        if (handle instanceof AssociationEnd) {
        		return ((AssociationEnd) handle).getParticipant();
        }
        if (handle instanceof Parameter) {
            return ((Parameter) handle).getType();
        }
        if (handle instanceof ObjectFlowState) {
            return ((ObjectFlowState) handle).getType();
        }

        // ...
        return illegalArgumentObject(handle);
    	
    }

    /**
     * @see org.argouml.model.Facade#getTarget(java.lang.Object)
     */
    public Object getTarget(Object handle) {
        if (isATransition(handle)) {
            return ((Transition) handle).getTarget();
        }
        return illegalArgumentObject(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getTargetScope(java.lang.Object)
     */
    public Object getTargetScope(Object handle) {
        if (handle instanceof StructuralFeature) {
            return ((StructuralFeature) handle).getTargetScope();
        }
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getTargetScope();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getTargetFlows(java.lang.Object)
     */
    public Collection getTargetFlows(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getTargetFlow();
        }
        return illegalArgumentCollection(handle); 
    }

    /**
     * @see org.argouml.model.Facade#getUpper(java.lang.Object)
     */
    public int getUpper(Object handle) {
        if (isAAssociationEnd(handle)) {
            int upper = 0;
            AssociationEnd end = (AssociationEnd) handle;
            if (end.getMultiplicity() != null) {
                upper = getUpper(end.getMultiplicity());
            }
            return upper;
        }
        if (isAMultiplicity(handle)) {
            Multiplicity up = (Multiplicity) handle;
            ArrayList ranges = new ArrayList(up.getRange());
            // TODO: this assumes ranges are sorted.  Is this true? - tfm
            return getUpper(ranges.get(ranges.size() - 1));
        }
        if (isAMultiplicityRange(handle)) {
            MultiplicityRange up = (MultiplicityRange) handle;
            return up.getUpper();
        }
        illegalArgument(handle);
        return 0;
    }

    /**
     * @see org.argouml.model.Facade#getUseCase(java.lang.Object)
     */
    public Object getUseCase(Object handle) {
        if (handle instanceof ExtensionPoint) {
            return ((ExtensionPoint) handle).getUseCase();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getLower(java.lang.Object)
     */
    public int getLower(Object handle) {
        if (isAAssociationEnd(handle)) {
            int lower = 0;
            AssociationEnd end = (AssociationEnd) handle;
            if (end.getMultiplicity() != null) {
                lower = getLower(end.getMultiplicity());
            }
            return lower;
        }
        if (isAMultiplicity(handle)) {
            Multiplicity low = (Multiplicity) handle;
            ArrayList ranges = new ArrayList(low.getRange());
            // TODO: this assumes ranges are sorted.  Is this true? - tfm
            return getLower(ranges.get(0));
        }
        if (isAMultiplicityRange(handle)) {
            MultiplicityRange low = (MultiplicityRange) handle;
            return low.getLower();
        }
        illegalArgument(handle);
        return 0;
    }

    /**
     * @see org.argouml.model.Facade#getTransitions(java.lang.Object)
     */
    public Collection getTransitions(Object handle) {
        if (isAStateMachine(handle)) {
            return ((StateMachine) handle).getTransitions();
        } else if (isACompositeState(handle)) {
            return ((CompositeState) handle).getInternalTransition();
        } else if (isAEvent(handle)) {
            return implementation.getUmlPackage().getStateMachines().
                    getATransitionTrigger().getTransition((Event) handle);
        }
        return illegalArgumentCollection(handle);    	
    }

    /**
     * @see org.argouml.model.Facade#getStructuralFeatures(java.lang.Object)
     */
    public Collection getStructuralFeatures(Object handle) {
        if (handle == null) {
            throw new IllegalArgumentException("A null classifier is invalid");
        }
        if (!(handle instanceof Classifier)) {
            throw new IllegalArgumentException(
                    "A Classifier must be supplied. We got "
                    + handle.getClass().getName());
        }
        Collection result = new ArrayList();
        Classifier mclassifier = (Classifier) handle;

        Iterator features = mclassifier.getFeature().iterator();
        while (features.hasNext()) {
            Feature feature = (Feature) features.next();
            if (isAStructuralFeature(feature)) {
                result.add(feature);
            }
        }
        return result;
    }

    /**
     * @see org.argouml.model.Facade#getSpecification(java.lang.Object)
     */
    public String getSpecification(Object handle) {
        if (handle instanceof Reception) {
            return ((Reception) handle).getSpecification();
        }
        return illegalArgumentString(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getSpecifications(java.lang.Object)
     */
    public Collection getSpecifications(Object handle) {
        Collection result = new ArrayList();
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getSpecification();
        }
        if (handle instanceof Classifier) {
            Collection deps = ((Classifier) handle).getClientDependency();
            Iterator depIterator = deps.iterator();
            //TODO: Check if this is the correct adaptation
            while (depIterator.hasNext()) {
                Dependency dep = (Dependency) depIterator.next();
                if (dep instanceof Abstraction
                        && !dep.getStereotype().isEmpty()) {
                    Iterator stereoIt = dep.getStereotype().iterator();
                    Stereotype current;
                    while (stereoIt.hasNext()) {
                        current = (Stereotype) stereoIt.next();
                        if (current.getName().equals("realize")) {
                            Interface i = (Interface) dep.getSupplier()
                                    .toArray()[0];
                            result.add(i);
                        }                	
                    }
                }
            }
            return result;
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getSuppliers(java.lang.Object)
     */
    public Collection getSuppliers(Object handle) {
        if (handle instanceof Dependency) {
            return ((Dependency) handle).getSupplier();
        }
        throw new IllegalArgumentException("Expected a Dependency. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getAction(java.lang.Object)
     */
    public Object getAction(Object handle) {
        if (handle instanceof Message) {
            return implementation.getUmlPackage().getCollaborations().
                    getAActionMessage().getAction((Message) handle);
        }
        if (handle instanceof Argument) {
            return ((Argument) handle).getAction();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getActivator(java.lang.Object)
     */
    public Object getActivator(Object handle) {
        if (handle instanceof Message) {
            return ((Message) handle).getActivator();
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getActualArguments(java.lang.Object)
     */
    public Collection getActualArguments(Object handle) {
        if (handle instanceof Action) {
            return ((Action) handle).getActualArgument();
        }
        return illegalArgumentCollection(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAddition(java.lang.Object)
     */
    public Object getAddition(Object handle) {
        if (handle instanceof Include) {
            return ((Include) handle).getAddition();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAggregation(java.lang.Object)
     */
    public Object getAggregation(Object handle) {
        if (handle instanceof AssociationEnd) {
            return ((AssociationEnd) handle).getAggregation();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getAssociatedClasses(java.lang.Object)
     */
    public Collection getAssociatedClasses(Object handle) {
        Collection col = new ArrayList();
        if (handle instanceof Classifier) {
            Classifier classifier = (Classifier) handle;
            Collection ends = getAssociationEnds(classifier);
            Iterator it = ends.iterator();
            Set associations = new HashSet();
            while (it.hasNext()) {
                AssociationEnd ae = (AssociationEnd) it.next();
                associations.add(ae.getAssociation());
            }
            Collection otherEnds = new ArrayList();
            it = associations.iterator();
            while (it.hasNext()) {
                otherEnds.addAll(((UmlAssociation) it.next()).getConnection());
            }
            otherEnds.removeAll(ends);
            it = otherEnds.iterator();
            while (it.hasNext()) {
                col.add(((AssociationEnd) it.next()).getParticipant());
            }
            return col;
        }
        return illegalArgumentCollection(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getName(java.lang.Object)
     */
    public String getName(Object handle) {
        if (handle instanceof OrderingKindEnum) {
            return handle.toString().replaceAll("ok_", "");
        }
        if (handle instanceof CallConcurrencyKindEnum) {
            return handle.toString().replaceAll("ck_", "");
        }
        if (handle instanceof VisibilityKindEnum) {
            return handle.toString().replaceAll("vk_", "");
        }
        if (handle instanceof AggregationKindEnum) {
            return handle.toString().replaceAll("ak_", "");
        }
        if (handle instanceof ParameterDirectionKindEnum) {
            return handle.toString().replaceAll("pdk_", "");
        }
        if (handle instanceof ModelElement) {
            ModelElement me = (ModelElement) handle;
            try {
                String name = me.getName();

                if (name != null && name.indexOf(0xffff) > -1) {
                    // The following code is a workaround for issue
                    // http://argouml.tigris.org/issues/show_bug.cgi?id=2847.
                    // The cause is
                    // not known and the best fix available for the moment is to
                    // remove
                    // the corruptions as they are found.
                    int pos = 0;
                    boolean fixed = false;
                    while ((pos = name.indexOf(0xffff)) >= 0) {
                        name = name.substring(0, pos)
                                + name.substring(pos + 1, name.length());
                        fixed = true;
                    }
                    if (fixed) {
                        try {
                            throw new UmlException(
                                    "Illegal character stripped out"
                                            + " of element name");
                        } catch (UmlException e) {
                            LOG.warn("0xFFFF detected in element name", e);
                        }
                        implementation.getCoreHelper().setName(handle, name);
                    }
                }
                return name;
            } catch (InvalidObjectException e) {
                LOG.error("Attempting to get name of deleted object" + handle);
                return null;
            }
        }
        if (handle instanceof Multiplicity) {
            return implementation.getDataTypesHelper().multiplicityToString(
                    handle);
        }
        throw new IllegalArgumentException(
                "Must have an MDR element supplied. Received a "
                        + handle.getClass().getName());
    }

	/**
     * @see org.argouml.model.Facade#getOwner(java.lang.Object)
     */
    public Object getOwner(Object handle) {
        if ((handle instanceof Attribute)
                && ((Attribute) handle).getAssociationEnd() != null) {
            return ((Attribute) handle).getAssociationEnd();
        }
        if (handle instanceof Feature) {
            return ((Feature) handle).getOwner();
        }
        if (handle instanceof TagDefinition) {
            return ((TagDefinition) handle).getOwner();
        }
        return illegalArgumentObject(handle);
    }

    /**
     * @see org.argouml.model.Facade#getTag(java.lang.Object)
     */
    public String getTag(Object handle) {
        if (handle instanceof TaggedValue) {
            TagDefinition td = ((TaggedValue) handle).getType();
            if (td != null)
                return td.getName();
            return null;
        }
        return illegalArgumentString(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getTaggedValues(java.lang.Object)
     */
    public Iterator getTaggedValues(Object handle) {
        if (handle instanceof ModelElement) {
            return getTaggedValuesCollection(handle).iterator();
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getTaggedValuesCollection(java.lang.Object)
     */
    public Collection getTaggedValuesCollection(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getTaggedValue();
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getTaggedValue( java.lang.Object,
     *      java.lang.String)
     */
    public Object getTaggedValue(Object handle, String name) {
        if (handle instanceof ModelElement) {
            ModelElement me = ((ModelElement) handle);
            Iterator i = me.getTaggedValue().iterator();
            while (i.hasNext()) {
                TaggedValue tv = (TaggedValue) i.next();
                if (tv.getType() != null 
                        && name.equals(tv.getType().getName())) {
                    return tv;
                }
            }
            return null;
        }
        throw new IllegalArgumentException("Expected a ModelElement. Got a "
                + handle.getClass().getName());
    }

    /**
     * @see org.argouml.model.Facade#getTaggedValueValue( java.lang.Object,
     *      java.lang.String)
     */
    public String getTaggedValueValue(Object handle, String name) {
        Object taggedValue = getTaggedValue(handle, name);
        if (taggedValue == null) {
            return "";
        }
        return getValueOfTag(taggedValue);
    }

    /**
     * @see org.argouml.model.Facade#getTagOfTag(java.lang.Object)
     */
    public String getTagOfTag(Object handle) {
        return getTag(handle);
    }

    /**
     * @see org.argouml.model.Facade#getTagDefinition(java.lang.Object)
     */
    public Object getTagDefinition(Object handle) {
        if (handle instanceof TaggedValue) {
            return ((TaggedValue) handle).getType();
        }
        throw new IllegalArgumentException("Not a tagged value: " + handle);
    }

    /**
     * @see org.argouml.model.Facade#getTagDefinitions(java.lang.Object)
     */
    public Collection getTagDefinitions(Object handle) {
        if (handle instanceof Stereotype) {
            return ((Stereotype) handle).getDefinedTag();
        }
        throw new IllegalArgumentException("Not a stereotype: " + handle);
    }
    
    /**
     * @see org.argouml.model.Facade#getValue(java.lang.Object)
     */
    public Object getValue(Object handle) {
        if (handle instanceof TaggedValue) {
            return ((TaggedValue) handle).getDataValue();
        }
        if (handle instanceof Argument) {
            return ((Argument) handle).getValue();
        }
        if (handle instanceof AttributeLink) {
            return ((AttributeLink) handle).getValue();
        }
        if (handle instanceof AggregationKind) {
            return new Integer(((AggregationKind) handle).toString());
        }
        if (handle instanceof OrderingKind) {
            return new Integer(((OrderingKind) handle).toString());
        }
        if (handle instanceof ParameterDirectionKind) {
            return new Integer(((ParameterDirectionKind) handle).toString());
        }
        if (handle instanceof VisibilityKind) {
            return new Integer(((VisibilityKind) handle).toString());
        }
        if (handle instanceof ScopeKind) {
            return new Integer(((ScopeKind) handle).toString());
        }
        /*
         * if (handle instanceof MessageDirectionKind) { return new
         * Integer(((MessageDirectionKind) handle).getValue()); }
         */
        if (handle instanceof ChangeableKind) {
            return new Integer(((ChangeableKind) handle).toString());
        }
        if (handle instanceof PseudostateKind) {
            return new Integer(((PseudostateKind) handle).toString());
        }
        if (handle instanceof CallConcurrencyKind) {
            return new Integer(((CallConcurrencyKind) handle).toString());
        }
        return illegalArgumentObject(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getValueOfTag(java.lang.Object)
     */
    public String getValueOfTag(Object handle) {
        if (handle instanceof TaggedValue) {
            Collection value = (Collection) getValue(handle);
            if (!value.isEmpty()) {
                if (value.size() > 1)
                    LOG.warn("Don't know how to manage multiple values " 
                            + "for a TaggedValue, returning first value");
                return value.iterator().next().toString();
            }
            return "";
        }
        return illegalArgumentString(handle);        
    }

    /**
     * @see org.argouml.model.Facade#getUUID(java.lang.Object)
     */
    public String getUUID(Object base) {
        if (base instanceof RefBaseObject) {
            return ((RefBaseObject) base).refMofId();
        }
        return illegalArgumentString(base); 
    }

    /**
     * @see org.argouml.model.Facade#getVisibility(java.lang.Object)
     */
    public Object getVisibility(Object handle) {
        if (handle instanceof ModelElement) {
            return ((ModelElement) handle).getVisibility();
        }
        throw new IllegalArgumentException("Can't get visibility of a "
                + handle.getClass().getName());
    }
    
    /**
     * @see org.argouml.model.Facade#getWhen(java.lang.Object)
     */
    public Object getWhen(Object target) {
        if (isATimeEvent(target)) {
            return ((TimeEvent) target).getWhen();
        }
        return illegalArgumentObject(target);
    }

    /**
     * @see org.argouml.model.Facade#getPartitions(java.lang.Object)
     */
    public Collection getPartitions(Object container) {
        if (container instanceof ActivityGraph) {
            return ((ActivityGraph) container).getPartition();
        }
        return illegalArgumentCollection(container);
    }

    /**
     * @see org.argouml.model.Facade#getReferenceState(java.lang.Object)
     */
    public String getReferenceState(Object o) {
        if (o instanceof StubState) {
            return ((StubState) o).getReferenceState();
        }
        return illegalArgumentString(o);
    }

    /**
     * @see org.argouml.model.Facade#lookupIn( java.lang.Object,
     *      java.lang.String)
     */
    public Object lookupIn(Object handle, String name) {
        if (handle instanceof Model) {
            return lookup((Model) handle, name);
        }
        if (handle instanceof Namespace) {
            return lookup((Namespace) handle, name);
        }
        if (handle instanceof Classifier) {
            return lookup((Classifier) handle, name);
        }
        return illegalArgumentObject(handle);
    }

    private ModelElement lookup(Namespace ns, String name) {
        int idx = name.indexOf("::");
        
        if (idx != -1) {
            String nm;
            nm = name.substring(0, idx);
            ModelElement e = lookup(ns, nm);
            if (e == null || !(e instanceof Namespace)) {
                return null;
            } 
            Namespace n = (Namespace) e;
            nm = name.substring(idx + 2);
            return lookup(n, nm); 
        }
        Iterator i = ns.getOwnedElement().iterator();
        while (i.hasNext()) {
            ModelElement e = (ModelElement) i.next();
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }
    
    /**
     * @see org.argouml.model.Facade#toString(java.lang.Object)
     */
    public String toString(Object modelElement) {
        if (modelElement instanceof Multiplicity) {
            return org.argouml.model.Model.getDataTypesHelper().
                multiplicityToString(modelElement);
        } else if (modelElement instanceof ModelElement) {
            return getUMLClassName(modelElement) + ": " + getName(modelElement);
        }
        return modelElement.toString();
    }
    
    /**
     * @see org.argouml.model.Facade#getUMLClassName(java.lang.Object)
     */
    public String getUMLClassName(Object handle) {
        if (!(handle instanceof ModelElement) 
                && !(handle instanceof Expression)
                && !(handle instanceof Multiplicity)
                && !(handle instanceof ElementImport)
                && !(handle instanceof ElementResidence)
                && !(handle instanceof TemplateParameter) ) {
            throw new IllegalArgumentException(
                    "Expected a ModelElement, got a " + handle);
        }
        return implementation.getMetaTypes().getName(handle);
    }
    
    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     * <p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return Object for use in the return statement.
     */
    private Object illegalArgumentObject(Object arg) {
	illegalArgument(arg);
	return null;
    }    

    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     * <p>
     *
     * @param arg
     *            is the incorrect argument.
     * @return String for use in the return statement.
     */
    private String illegalArgumentString(Object arg) {
        illegalArgument(arg);
        return null;
    }
    
    /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     *
     * @param arg
     *            is the incorrect argument.
     */
    private void illegalArgument(Object arg) {
	throw new IllegalArgumentException("Unrecognized object "
					   + getClassNull(arg));
    }
    
    /**
     * @param handle
     *            the Class or null
     * @return String
     */
    protected String getClassNull(Object handle) {
        if (handle == null) {
            return "[null]";
        }
        return "[" + handle + "/" + handle.getClass() + "]";
    }    

   /**
    * @see org.argouml.model.Facade#getTipString(java.lang.Object)
    */
    public String getTipString(Object modelElement) {
        return getUMLClassName(modelElement) + ": " + getName(modelElement);
    }

   /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     * <p>
     * 
     * @param arg
     *            is the incorrect argument.
     * @return Collection for use in the return statement.
     */
    private Collection illegalArgumentCollection(Object arg) {
        illegalArgument(arg);
        return null;
    }  
   
   /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     * <p>
     * 
     * @param arg
     *            is the incorrect argument.
     * @return a boolean for use in the return statement.
     */
    private boolean illegalArgumentBoolean(Object arg) {
        illegalArgument(arg);
        return false;
    }  
   
   /**
     * Method that throws an error when a method is called with an incorrect
     * argument.
     * <p>
     * 
     * @param arg
     *            is the incorrect argument.
     * @return Int for use in the return statement.
     */
    private int illegalArgumentInt(Object arg) {
        illegalArgument(arg);
        return 0;
    }

   /**
     * @see org.argouml.model.Facade#isATagDefinition(java.lang.Object)
     */
    public boolean isATagDefinition(Object handle) {
        return handle instanceof TagDefinition;
    }

    /**
     * Notice that the Enumeration are ordered.
     * @see org.argouml.model.Facade#getEnumerationLiterals(java.lang.Object)
     */
    public List getEnumerationLiterals(Object handle) {
        if (!isAEnumeration(handle))
            throw new IllegalArgumentException("handle: " + handle);
        return ((Enumeration) handle).getLiteral();
    }

    /**
     * @see org.argouml.model.Facade#isAEnumeration(java.lang.Object)
     */
    public boolean isAEnumeration(Object handle) {
        return handle instanceof Enumeration;
    }

}

