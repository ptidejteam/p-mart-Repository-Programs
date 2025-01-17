// $Id: CoreFactoryMDRImpl.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.argouml.model.CoreFactory;
import org.argouml.model.ModelEventPump;
import org.omg.uml.behavioralelements.statemachines.Event;
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
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.DataType;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Element;
import org.omg.uml.foundation.core.ElementResidence;
import org.omg.uml.foundation.core.Enumeration;
import org.omg.uml.foundation.core.EnumerationLiteral;
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
import org.omg.uml.foundation.core.TemplateParameter;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.Usage;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.BooleanExpression;
import org.omg.uml.foundation.datatypes.CallConcurrencyKindEnum;
import org.omg.uml.foundation.datatypes.ChangeableKind;
import org.omg.uml.foundation.datatypes.ChangeableKindEnum;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.foundation.datatypes.OrderingKind;
import org.omg.uml.foundation.datatypes.OrderingKindEnum;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;
import org.omg.uml.foundation.datatypes.ScopeKind;
import org.omg.uml.foundation.datatypes.ScopeKindEnum;
import org.omg.uml.foundation.datatypes.VisibilityKind;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;
import org.omg.uml.modelmanagement.Model;

/**
 * Factory to create UML classes for the UML Foundation::Core package.
 * <p>
 * 
 * Feature, StructuralFeature, and PresentationElement do not have a create
 * method since they are called an "abstract metaclass" in the UML 
 * specifications.
 * <p>
 * Emulates synchronous event delivery as implemented in NSUML library.
 * Any methods which call org.omg.uml or NetBeans MDR methods that generate
 * events must call flushModelEvents() before returning.  'Get' methods do
 * not needed to be handled specially.
 * <p>
 * 
 * @since ARGO0.19.5
 * @author Ludovic Ma�tre
 * @author Tom Morris
 * <p>
 * Derived from NSUML implementation by: 
 * @author Thierry Lach
 * @author Jaap Branderhorst
 */
public class CoreFactoryMDRImpl extends AbstractUmlModelFactoryMDR implements
        CoreFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(CoreFactoryMDRImpl.class);

    /**
     * The model implementation.
     */
    private MDRModelImplementation nsmodel;

    /**
     * The MDR core package for model element construction
     */
    private CorePackage corePackage;

    /**
     * The model event pump
     */
    private ModelEventPump eventPump;

    /**
     * Don't allow instantiation.
     * 
     * @param implementation
     *            To get other helpers and factories.
     */
    CoreFactoryMDRImpl(MDRModelImplementation implementation) {
        nsmodel = implementation;
        eventPump = nsmodel.getModelEventPump();
    }

    /**
     * Create an empty but initialized instance of a UML Abstraction.
     * TODO: This method is not part of the interface, but it is
     * tested directly.
     * 
     * @return an initialized UML Abstraction instance.
     */
    public Object createAbstraction() {
        org.omg.uml.foundation.core.Abstraction myAbstraction = nsmodel
                .getUmlPackage().getCore().getAbstraction().createAbstraction();
        super.initialize(myAbstraction);
        eventPump.flushModelEvents();
        return myAbstraction;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAbstraction(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Object buildAbstraction(String name, Object supplier, 
            Object client) {
        if (!(client instanceof Classifier)
                || !(supplier instanceof Classifier)) {
            throw new IllegalArgumentException(
                    "The supplier and client of an abstraction"
                            + "should be classifiers");
        }
        Abstraction abstraction = (Abstraction) createAbstraction();
        abstraction.setName(name);
        abstraction.getClient().add(client);
        abstraction.getSupplier().add(supplier);
        eventPump.flushModelEvents();
        return abstraction;
    }

    /**
     * Create an empty but initialized instance of a UML Association.
     * 
     * TODO: This method is not part of the interface, but it is
     * invoked directly by tests using reflection.  
     * 
     * @return an initialized UML Association instance.
     */
    public Object createAssociation() {
        UmlAssociation assoc = nsmodel.getUmlPackage().getCore()
                .getUmlAssociation().createUmlAssociation();
        super.initialize(assoc);
        eventPump.flushModelEvents();
        return assoc;
    }

    /**
     * Create an empty but initialized instance of a UML AssociationClass.
     * 
     * TODO: This method is not part of the interface, but it is
     * invoked directly by tests using reflection.  
     * 
     * @return an initialized UML AssociationClass instance.
     */
    public Object createAssociationClass() {
        AssociationClass assoc = nsmodel.getUmlPackage().getCore()
                .getAssociationClass().createAssociationClass();
        super.initialize(assoc);
        eventPump.flushModelEvents();
        return assoc;
    }

    /**
     * @see org.argouml.model.CoreFactory#createAssociationEnd()
     */
    public Object createAssociationEnd() {
        AssociationEnd assocEnd = nsmodel.getUmlPackage().getCore().
        		getAssociationEnd().createAssociationEnd();
        super.initialize(assocEnd);
        eventPump.flushModelEvents();
        return assocEnd;
    }

    /**
     * @see org.argouml.model.CoreFactory#createAttribute()
     */
    public Object createAttribute() {
        Attribute myAttribute = nsmodel.getUmlPackage().getCore()
                .getAttribute().createAttribute();
        super.initialize(myAttribute);
        eventPump.flushModelEvents();
        return myAttribute;
    }

    /**
     * @see org.argouml.model.CoreFactory#createBinding()
     */
    public Object createBinding() {
        Binding myBinding = nsmodel.getUmlPackage().getCore().getBinding()
                .createBinding();
        super.initialize(myBinding);
        eventPump.flushModelEvents();
        return myBinding;
    }

    /**
     * @see org.argouml.model.CoreFactory#createClass()
     */
    public Object createClass() {
        UmlClass myClass = getCorePackage().getUmlClass().createUmlClass();
        super.initialize(myClass);
        eventPump.flushModelEvents();
        return myClass;
    }

    /**
     * @see org.argouml.model.CoreFactory#createComment()
     */
    public Object createComment() {
        Comment myComment = nsmodel.getUmlPackage().getCore().getComment()
                .createComment();
        super.initialize(myComment);
        eventPump.flushModelEvents();
        return myComment;
    }

    /**
     * @see org.argouml.model.CoreFactory#createComponent()
     */
    public Object createComponent() {
        Component myComponent = nsmodel.getUmlPackage().getCore()
                .getComponent().createComponent();
        super.initialize(myComponent);
        eventPump.flushModelEvents();
        return myComponent;
    }

    /**
     * @see org.argouml.model.CoreFactory#createConstraint()
     */
    public Object createConstraint() {
        Constraint myConstraint = nsmodel.getUmlPackage().getCore()
                .getConstraint().createConstraint();
        super.initialize(myConstraint);
        eventPump.flushModelEvents();
        return myConstraint;
    }

    /**
     * @see org.argouml.model.CoreFactory#createDataType()
     */
    public Object createDataType() {
        DataType dataType = nsmodel.getUmlPackage().
                getCore().getDataType().createDataType();
        super.initialize(dataType);
        eventPump.flushModelEvents();
        return dataType;
    }

    /**
     * Create an empty but initialized instance of a UML Dependency.
     *  
     * TODO: This method is not part of the interface, but it is
     * invoked directly by tests using reflection.  
     * 
     * @return an initialized UML Dependency instance.
     */
    public Object createDependency() {
        Dependency myDependency = getCorePackage().
                getDependency().createDependency();
        super.initialize(myDependency);
        eventPump.flushModelEvents();
        return myDependency;
    }

    /**
     * @see org.argouml.model.CoreFactory#createElementResidence()
     */
    public Object createElementResidence() {
        ElementResidence myElementResidence = getCorePackage().
                getElementResidence().createElementResidence();
        super.initialize(myElementResidence);
        eventPump.flushModelEvents();
        return myElementResidence;
    }

    /**
     * @see org.argouml.model.CoreFactory#createFlow()
     */
    public Object createFlow() {
        Flow myFlow = nsmodel.getUmlPackage().getCore().getFlow().createFlow();
        super.initialize(myFlow);
        eventPump.flushModelEvents();
        return myFlow;
    }

    /**
     * @see org.argouml.model.CoreFactory#createGeneralization()
     */
    public Object createGeneralization() {
        Generalization myGeneralization = getCorePackage().getGeneralization()
                .createGeneralization();
        super.initialize(myGeneralization);
        eventPump.flushModelEvents();
        return myGeneralization;
    }

    /**
     * @see org.argouml.model.CoreFactory#createInterface()
     */
    public Object createInterface() {
        Interface myInterface = nsmodel.getUmlPackage().getCore()
                .getInterface().createInterface();
        super.initialize(myInterface);
        eventPump.flushModelEvents();
        return myInterface;
    }

    /**
     * @see org.argouml.model.CoreFactory#createMethod()
     */
    public Object createMethod() {
        Method myMethod = nsmodel.getUmlPackage().getCore().getMethod()
                .createMethod();
        super.initialize(myMethod);
        eventPump.flushModelEvents();
        return myMethod;
    }

    /**
     * @see org.argouml.model.CoreFactory#createNode()
     */
    public Object createNode() {
        Node myNode = nsmodel.getUmlPackage().getCore().getNode().createNode();
        super.initialize(myNode);
        eventPump.flushModelEvents();
        return myNode;
    }

    /**
     * @see org.argouml.model.CoreFactory#createOperation()
     */
    public Object createOperation() {
        Operation myOperation = nsmodel.getUmlPackage().getCore()
                .getOperation().createOperation();
        super.initialize(myOperation);
        eventPump.flushModelEvents();
        return myOperation;
    }

    /**
     * @see org.argouml.model.CoreFactory#createParameter()
     */
    public Object createParameter() {
        Parameter myParameter = nsmodel.getUmlPackage().getCore()
                .getParameter().createParameter();
        super.initialize(myParameter);
        eventPump.flushModelEvents();
        return myParameter;
    }

    /**
     * @see org.argouml.model.CoreFactory#createPermission()
     */
    public Object createPermission() {
        Permission myPermission = nsmodel.getUmlPackage().getCore()
                .getPermission().createPermission();
        super.initialize(myPermission);
        eventPump.flushModelEvents();
        return myPermission;
    }

    /**
     * @see org.argouml.model.CoreFactory#createTemplateParameter()
     */
    public Object createTemplateParameter() {
        TemplateParameter myTemplateParameter = nsmodel.getUmlPackage()
                .getCore().getTemplateParameter().createTemplateParameter();
        super.initialize(myTemplateParameter);
        eventPump.flushModelEvents();
        return myTemplateParameter;
    }

    /**
     * @see org.argouml.model.CoreFactory#createUsage()
     */
    public Object createUsage() {
        Usage myUsage = nsmodel.getUmlPackage().getCore().getUsage()
                .createUsage();
        super.initialize(myUsage);
        eventPump.flushModelEvents();
        return myUsage;
    }

    /**
     * Builds a default binary association with two default association ends.
     * 
     * @param c1
     *            The first classifier to connect to
     * @param nav1
     *            The navigability of the Associaton end
     * @param agg1
     *            The aggregation type of the second Associaton end
     * @param c2
     *            The second classifier to connect to
     * @param nav2
     *            The navigability of the second Associaton end
     * @param agg2
     *            The aggregation type of the second Associaton end
     * @return MAssociation
     * @throws IllegalArgumentException
     *             if either Classifier is null
     */
    private UmlAssociation buildAssociation(Classifier c1, boolean nav1,
            AggregationKind agg1, Classifier c2, boolean nav2,
            AggregationKind agg2) {
        if (c1 == null || c2 == null) {
            throw new IllegalArgumentException("one of "
                    + "the classifiers to be " + "connected is null");
        }
        Namespace ns1 = c1.getNamespace();
        Namespace ns2 = c2.getNamespace();
        if (ns1 == null || ns2 == null) {
            throw new IllegalArgumentException("one of "
                    + "the classifiers does not " + "belong to a namespace");
        }
        UmlAssociation assoc = (UmlAssociation) createAssociation();
        assoc.setName("");
        assoc.setNamespace((Namespace) nsmodel.getCoreHelper().
                getFirstSharedNamespace(ns1, ns2));
        buildAssociationEnd(assoc, null, c1, null, null,
                nav1, null, agg1, null, null, null);
        buildAssociationEnd(assoc, null, c2, null, null, 
                nav2, null, agg2, null, null, null);
        eventPump.flushModelEvents();
        return assoc;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAssociation(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Object,
     *      java.lang.Boolean)
     */
    public Object buildAssociation(Object fromClassifier,
            Object aggregationKind1, Object toClassifier,
            Object aggregationKind2, Boolean unidirectional) {
        if (fromClassifier == null || toClassifier == null) {
            throw new IllegalArgumentException("one of "
                    + "the classifiers to be " + "connected is null");
        }
        Classifier from = (Classifier) fromClassifier;
        Classifier to = (Classifier) toClassifier;
        AggregationKind agg1 = (AggregationKind) aggregationKind1;
        AggregationKind agg2 = (AggregationKind) aggregationKind2;

        Namespace ns1 = from.getNamespace();
        Namespace ns2 = to.getNamespace();
        if (ns1 == null || ns2 == null) {
            throw new IllegalArgumentException("one of "
                    + "the classifiers does not " + "belong to a namespace");
        }
        UmlAssociation assoc = (UmlAssociation) createAssociation();
        assoc.setName("");
        assoc.setNamespace((Namespace) nsmodel.getCoreHelper().
                getFirstSharedNamespace(ns1, ns2));

        boolean nav1 = true;
        boolean nav2 = true;

        if (from instanceof Interface) {
            nav2 = false;
            agg2 = agg1;
            agg1 = null;
        } else if (to instanceof Interface) {
            nav1 = false;
        } else {
            nav1 = !Boolean.TRUE.equals(unidirectional);
            nav2 = true;
        }

        buildAssociationEnd(assoc, null, from, null, null, nav1, null, agg1,
                null, null, null);
        buildAssociationEnd(assoc, null, to, null, null, nav2, null, agg2,
                null, null, null);

        eventPump.flushModelEvents();
        return assoc;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAssociation(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildAssociation(Object classifier1, Object classifier2) {
        Classifier c1 = (Classifier) classifier1;
        Classifier c2 = (Classifier) classifier2;
        return buildAssociation(c1, true, AggregationKindEnum.AK_NONE, c2,
                true, AggregationKindEnum.AK_NONE);
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAssociation(java.lang.Object,
     *      boolean, java.lang.Object, boolean, java.lang.String)
     */
    public Object buildAssociation(Object c1, boolean nav1, Object c2,
            boolean nav2, String name) {
        UmlAssociation assoc = buildAssociation((Classifier) c1, nav1,
                AggregationKindEnum.AK_NONE, (Classifier) c2, nav2,
                AggregationKindEnum.AK_NONE);
        if (assoc != null) {
            assoc.setName(name);
        }
        return assoc;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAssociationClass(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildAssociationClass(Object end1, Object end2) {
        if (end1 == null || end2 == null || !(end1 instanceof Classifier)
                || !(end2 instanceof Classifier)) {
            throw new IllegalArgumentException(
                    "either one of the arguments was null");
        }
        return buildAssociationClass(
                (UmlClass) buildClass(),
                (Classifier) end1,
                (Classifier) end2);
    }


    /**
     * @see org.argouml.model.CoreFactory#buildAssociationEnd(java.lang.Object,
     *      java.lang.String, java.lang.Object, java.lang.Object,
     *      java.lang.Object, boolean, java.lang.Object, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object buildAssociationEnd(Object assoc, String name, Object type,
            Object multi, Object stereo, boolean navigable, Object order,
            Object aggregation, Object scope, Object changeable,
            Object visibility) {
        // wellformednessrules and preconditions
        if (assoc == null || !(assoc instanceof UmlAssociation) || type == null
                || !(type instanceof Classifier)) {
            throw new IllegalArgumentException("either type or association "
                    + "are null");
        }
        if (multi != null && !(multi instanceof Multiplicity)) {
            throw new IllegalArgumentException("Multiplicity");
        }
        if (stereo != null && !(stereo instanceof Stereotype)) {
            throw new IllegalArgumentException("Stereotype");
        }
        if (order != null && !(order instanceof OrderingKind)) {
            throw new IllegalArgumentException("OrderingKind");
        }
        if (aggregation != null && !(aggregation instanceof AggregationKind)) {
            throw new IllegalArgumentException("AggregationKind");
        }
        if (scope != null && !(scope instanceof ScopeKind)) {
            throw new IllegalArgumentException("ScopeKind");
        }
        if (changeable != null && !(changeable instanceof ChangeableKind)) {
            throw new IllegalArgumentException("ChangeableKind");
        }
        if (visibility != null && !(visibility instanceof VisibilityKind)) {
            throw new IllegalArgumentException("VisibilityKind");
        }

        if (type instanceof DataType || type instanceof Interface) {
            if (!navigable) {
                throw new IllegalArgumentException(
                        "Wellformedness rule 2.5.3.3 [1] is broken. "
                                + "The Classifier of an AssociationEnd cannot"
                                + "be an Interface or a DataType if the "
                                + "association is navigable away from "
                                + "that end.");
            }
            List ends = new ArrayList();
            ends.addAll(((UmlAssociation) assoc).getConnection());
            Iterator it = ends.iterator();
            while (it.hasNext()) {
                AssociationEnd end = (AssociationEnd) it.next();
                if (end.isNavigable()) {
                    throw new IllegalArgumentException("type is either "
                            + "datatype or " + "interface and is "
                            + "navigable to");
                }
            }
        }
        if (aggregation != null
                && aggregation.equals(AggregationKindEnum.AK_COMPOSITE)
                && multi != null && getMaxUpper((Multiplicity) multi) > 1) {
            throw new IllegalArgumentException("aggregation is composite "
                    + "and multiplicity > 1");
        }

        AssociationEnd end = (AssociationEnd) createAssociationEnd();
        end.setAssociation((UmlAssociation) assoc);
        end.setParticipant((Classifier) type);
        end.setName(name);
        if (multi != null) {
            end.setMultiplicity((Multiplicity) multi);
        } else {
            end.setMultiplicity(getMultiplicity11());
        }
        if (stereo != null) {
            end.getStereotype().clear();
            end.getStereotype().add(stereo);
        }
        end.setNavigable(navigable);
        if (order != null) {
            end.setOrdering((OrderingKind) order);
        } else {
            end.setOrdering(OrderingKindEnum.OK_UNORDERED);
        }
        if (aggregation != null) {
            end.setAggregation((AggregationKind) aggregation);
        } else {
            end.setAggregation(AggregationKindEnum.AK_NONE);
        }
        if (scope != null) {
            end.setTargetScope((ScopeKind) scope);
        } else {
            end.setTargetScope(ScopeKindEnum.SK_INSTANCE);
        }
        if (changeable != null) {
            end.setChangeability((ChangeableKind) changeable);
        } else {
            end.setChangeability(ChangeableKindEnum.CK_CHANGEABLE);
        }
        if (visibility != null) {
            end.setVisibility((VisibilityKind) visibility);
        } else {
            end.setVisibility(VisibilityKindEnum.VK_PUBLIC);
        }
        eventPump.flushModelEvents();
        return end;
    }

    /**
     * Get the maximum value of a multiplicity
     * 
     * @param m
     *            the Multiplicity
     * @return upper range
     */
    private int getMaxUpper(Multiplicity m) {
        int max = 0;
        for (Iterator i = m.getRange().iterator(); i.hasNext();) {
            int value = ((MultiplicityRange) i.next()).getUpper();
            if (value > max) {
                max = value;
            }
        }
        return 0;
    }

    /**
     * Get a 1..1 multiplicity
     */
    private Multiplicity getMultiplicity11() {
        return (Multiplicity) nsmodel.getDataTypesFactory().createMultiplicity(
                1, 1);
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAssociationEnd(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildAssociationEnd(Object type, Object assoc) {
        if (type == null || !(type instanceof Classifier) || assoc == null
                || !(assoc instanceof UmlAssociation)) {
            throw new IllegalArgumentException("one of the arguments is null");
        }
        return buildAssociationEnd(assoc, "", type, null, null, true, null,
                null, null, null, VisibilityKindEnum.VK_PUBLIC);
    }

    /**
     * Builds an association class from a class and two classifiers that should
     * be associated. Both ends of the associationclass are navigable.
     * <p>
     * 
     * @param cl
     *            the class
     * @param end1
     *            the first classifier
     * @param end2
     *            the second classifier
     * @return MAssociationClass
     */
    private AssociationClass buildAssociationClass(UmlClass cl,
            Classifier end1, Classifier end2) {
        if (end1 == null || end2 == null || cl == null) {
            throw new IllegalArgumentException(
                    "one of the arguments was null");
        }
        AssociationClass assoc = (AssociationClass) createAssociationClass();
        
        // Copy attributes from our template class
        assoc.setNamespace(cl.getNamespace());
        assoc.setName(cl.getName());
        assoc.setAbstract(cl.isAbstract());
        assoc.setActive(cl.isActive());
        assoc.setLeaf(cl.isLeaf());
        assoc.setRoot(cl.isRoot());
        assoc.setSpecification(cl.isSpecification());
        assoc.getStereotype().addAll(cl.getStereotype());
        assoc.setVisibility(cl.getVisibility());
        
        /*
         * Normally we will be called with a newly created default class as our
         * template so only the above attribute copying is needed. The rest of
         * this is just in case someone wants it to be more general in the
         * future.
         * TODO: This is untested and just copies what was done in the NSUML
         * implementation (which is also untested for the same reason as above).
         */
        assoc.getClientDependency().addAll(cl.getClientDependency());
        assoc.getComment().addAll(cl.getComment());
        assoc.getConstraint().addAll(cl.getConstraint());        
        assoc.getFeature().addAll(cl.getFeature());
        assoc.getGeneralization().addAll(cl.getGeneralization());
        assoc.getPowertypeRange().addAll(cl.getPowertypeRange());
        assoc.getSourceFlow().addAll(cl.getSourceFlow());
        assoc.getTaggedValue().addAll(cl.getTaggedValue());
        assoc.getTargetFlow().addAll(cl.getTargetFlow());
        assoc.getTemplateParameter().addAll(cl.getTemplateParameter());

        // Other things copied in the NSUML implementation 
        // which have no direct analog here
        
        //assoc.setAssociationEnds(facade.getAssociationEnds(cl));
        //assoc.setClassifierRoles(cl.getClassifierRole());
        //assoc.setClassifierRoles1(cl.getClassifierRoles1());
        //assoc.setClassifiersInState(cl.getClassifiersInState());
        //assoc.setCollaborations(cl.getCollaborations());
        //assoc.setCollaborations1(cl.getCollaborations1());
        //assoc.setCreateActions(cl.getCreateActions());
        //assoc.setExtensions(cl.getExtensions());
        //assoc.setInstances(cl.getInstances());
        //assoc.setObjectFlowStates(cl.getObjectFlowStates());
        //assoc.setParticipants(cl.getParticipants());
        //assoc.setPartitions1(cl.getPartitions1());
        //assoc.setPresentations(cl.getPresentations());
        //assoc.setStructuralFeatures(cl.getStructuralFeatures());

        buildAssociationEnd(assoc, null, end1, null, null, true, null, null,
                null, null, null);
        buildAssociationEnd(assoc, null, end2, null, null, true, null, null,
                null, null, null);
        eventPump.flushModelEvents();

        return assoc;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAttribute(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildAttribute(Object model, Object theType) {
        Classifier clsType = (Classifier) theType;
        // Force type element into given namespace if not already there
        // side effect!
        if (model != clsType.getNamespace()
                && !(nsmodel.getModelManagementHelper().getAllNamespaces(model).
                        contains(clsType.getNamespace()))) {
            clsType.setNamespace((Model) model);
        }
        Attribute attr = (Attribute) createAttribute();
        attr.setName("newAttr");
        attr.setMultiplicity(getMultiplicity11());
        attr.getStereotype().clear();
        attr.setOwner(null);
        attr.setType(clsType);
        attr.setInitialValue(null);
        attr.setVisibility(VisibilityKindEnum.VK_PUBLIC);
        attr.setOwnerScope(ScopeKindEnum.SK_INSTANCE);
        attr.setChangeability(ChangeableKindEnum.CK_CHANGEABLE);
        attr.setTargetScope(ScopeKindEnum.SK_INSTANCE);
        eventPump.flushModelEvents();
        return attr;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildAttribute(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.util.Collection)
     */
    public Object buildAttribute(Object handle, Object model, Object intType,
            Collection propertyChangeListeners) {
        if (!(handle instanceof Classifier)
                && !(handle instanceof AssociationEnd)) {
            return null;
        }
        Attribute attr = (Attribute) buildAttribute(model, intType);
        if (handle instanceof Classifier) {
            Classifier cls = (Classifier) handle;
            cls.getFeature().add(attr);
            attr.setOwner(cls);
        }
        if (handle instanceof AssociationEnd) {
            AssociationEnd assend = (AssociationEnd) handle;
            assend.getQualifier().add(attr);
            attr.setOwner(assend.getParticipant());
        }
        // we set the listeners to the figs here too
        // it would be better to do that in the figs themselves
        Iterator it = propertyChangeListeners.iterator();
        while (it.hasNext()) {
            PropertyChangeListener listener = 
                (PropertyChangeListener) it.next();
            eventPump.addModelEventListener(listener, attr);
        }
        LOG.warn("Attr owner is: " + attr.getOwner());
        eventPump.flushModelEvents();
        return attr;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildClass()
     */
    public Object buildClass() {
        UmlClass cl = (UmlClass) createClass();
        cl.setName("");
        cl.getStereotype().clear();
        cl.setAbstract(false);
        cl.setActive(false);
        cl.setRoot(false);
        cl.setLeaf(false);
        cl.setSpecification(false);
        cl.setVisibility(VisibilityKindEnum.VK_PUBLIC);
        eventPump.flushModelEvents();
        return cl;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildClass(java.lang.Object)
     */
    public Object buildClass(Object owner) {
        Object clazz = buildClass();
        if (owner instanceof Namespace) {
            nsmodel.getCoreHelper().setNamespace(clazz, owner);
        }
        return clazz;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildClass(java.lang.String)
     */
    public Object buildClass(String name) {
        Object clazz = buildClass();
        nsmodel.getCoreHelper().setName(clazz, name);
        return clazz;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildClass(java.lang.String,
     *      java.lang.Object)
     */
    public Object buildClass(String name, Object owner) {
        Object clazz = buildClass();
        nsmodel.getCoreHelper().setName(clazz, name);
        if (owner instanceof Namespace) {
            nsmodel.getCoreHelper().setNamespace(clazz, /* MNamespace */owner);
        }
        eventPump.flushModelEvents();
        return clazz;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildInterface()
     */
    public Object buildInterface() {
        Interface cl = (Interface) createInterface();
        // cl.setNamespace(ProjectBrowser.getInstance().getProject()
        // .getModel());
        cl.setName("");
        cl.getStereotype().clear();
        cl.setAbstract(false);
        cl.setRoot(false);
        cl.setLeaf(false);
        cl.setSpecification(false);
        cl.setVisibility(VisibilityKindEnum.VK_PUBLIC);
        eventPump.flushModelEvents();
        return cl;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildInterface(java.lang.Object)
     */
    public Object buildInterface(Object owner) {
        Interface cl = (Interface) buildInterface();
        if (owner instanceof Namespace) {
            cl.setNamespace((Namespace) owner);
        }
        eventPump.flushModelEvents();
        return cl;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildInterface(java.lang.String)
     */
    public Object buildInterface(String name) {
        Interface cl = (Interface) buildInterface();
        cl.setName(name);
        eventPump.flushModelEvents();
        return cl;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildInterface(java.lang.String,
     *      java.lang.Object)
     */
    public Object buildInterface(String name, Object owner) {
        Interface cl = (Interface) buildInterface();
        cl.setName(name);
        if (owner instanceof Namespace) {
            cl.setNamespace((Namespace) owner);
        }
        eventPump.flushModelEvents();
        return cl;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildDataType(java.lang.String,
     *      java.lang.Object)
     */
    public Object buildDataType(String name, Object owner) {
        DataType dt = (DataType) createDataType();
        dt.setName(name);
        if (owner instanceof Namespace) {
            dt.setNamespace((Namespace) owner);
        }
        eventPump.flushModelEvents();
        return dt;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildDependency(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildDependency(Object clientObj, Object supplierObj) {

        ModelElement client = (ModelElement) clientObj;
        ModelElement supplier = (ModelElement) supplierObj;
        if (client == null || supplier == null || client.getNamespace() == null
                || supplier.getNamespace() == null) {
            throw new IllegalArgumentException("client or supplier is null "
                    + "or their namespaces.");
        }
        Dependency dep = (Dependency) createDependency();
        dep.getSupplier().add(supplier);
        dep.getClient().add(client);
        if (supplier.getNamespace() != null) {
            dep.setNamespace(supplier.getNamespace());
        } else if (client.getNamespace() != null) {
            dep.setNamespace(client.getNamespace());
        }
        eventPump.flushModelEvents();
        return dep;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildPermission(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildPermission(Object clientObj, Object supplierObj) {

        ModelElement client = (ModelElement) clientObj;
        ModelElement supplier = (ModelElement) supplierObj;
        if (client == null || supplier == null || client.getNamespace() == null
                || supplier.getNamespace() == null) {
            throw new IllegalArgumentException("client or supplier is null "
                    + "or their namespaces.");
        }
        Permission per = (Permission) createPermission();
        per.getSupplier().add(supplier);
        per.getClient().add(client);
        if (supplier.getNamespace() != null) {
            per.setNamespace(supplier.getNamespace());
        } else if (client.getNamespace() != null) {
            per.setNamespace(client.getNamespace());
        }
        nsmodel.getExtensionMechanismsFactory().buildStereotype(per, "import",
                per.getNamespace());
        eventPump.flushModelEvents();
        return per;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildGeneralization(java.lang.Object,
     *      java.lang.Object, java.lang.String)
     */
    public Object buildGeneralization(Object child, Object parent, 
            String name) {
        if (child == null || parent == null
                || !(child instanceof GeneralizableElement)
                || !(parent instanceof GeneralizableElement)) {
            return null;
        }
        Object gen = buildGeneralization(child, parent);
        if (gen != null) {
            ((Generalization) gen).setName(name);
        }
        return gen;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildGeneralization(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildGeneralization(Object child1, Object parent1) {
        if ((!(child1 instanceof GeneralizableElement) 
                || !(parent1 instanceof GeneralizableElement))
                && child1 != parent1) {
            throw new IllegalArgumentException(
                    "Both items must be different generalizable elements");
        }

        GeneralizableElement child = (GeneralizableElement) child1;
        GeneralizableElement parent = (GeneralizableElement) parent1;

        // Check that the two elements aren't already linked the opposite way
        Iterator it = parent.getGeneralization().iterator();
        while (it.hasNext()) {
            Generalization gen = (Generalization) it.next();
            if (gen.getParent().equals(child)) {
                return null;
            }
        }

        if (parent.getNamespace() == null) {
            throw new IllegalArgumentException("parent has no namespace");
        }
        if (parent.isLeaf()) {
            throw new IllegalArgumentException("parent is leaf");
        }
        if (child.isRoot()) {
            throw new IllegalArgumentException("child is root");
        }

        Generalization gen = (Generalization) createGeneralization();
        gen.setParent(parent);
        gen.setChild(child);
        gen.setNamespace(parent.getNamespace());
        eventPump.flushModelEvents();
        return gen;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildMethod(java.lang.String)
     */
    public Object buildMethod(String name) {
        Method method = (Method) createMethod();
        if (method != null) {
            method.setName(name);
        }
        eventPump.flushModelEvents();
        return method;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildOperation(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.util.Collection)
     */
    public Object buildOperation(Object classifier, Object model,
            Object voidType, Collection propertyChangeListeners) {
        if (!(classifier instanceof Classifier)) {
            throw new IllegalArgumentException("Handle is not a classifier");
        }
        Classifier cls = (Classifier) classifier;
        Operation oper = (Operation) createOperation();
        oper.setName("newOperation");
        oper.getStereotype().clear();
        oper.setOwner(cls);
        oper.setVisibility(VisibilityKindEnum.VK_PUBLIC);
        oper.setAbstract(false);
        oper.setLeaf(false);
        oper.setRoot(false);
        oper.setQuery(false);
        oper.setOwnerScope(ScopeKindEnum.SK_INSTANCE);
        // Jaap Branderhorst 6-4-2003 commented out next line since an
        // operation cannot have two owners. the owner must be the
        // owning classifier which must be set via the setOwner
        // method, not via the namespace.
        //
        // oper.setNamespace(cls);
        oper.setConcurrency(CallConcurrencyKindEnum.CCK_SEQUENTIAL);

        Parameter returnParameter = (Parameter) buildParameter(oper, model,
                voidType, propertyChangeListeners);
        returnParameter.setKind(ParameterDirectionKindEnum.PDK_RETURN);
        returnParameter.setName("return");
        // we set the listeners to the figs here too it would be
        // better to do that in the figs themselves the
        // elementlistener for the parameter is allready set in
        // buildparameter(oper)
        Iterator it = propertyChangeListeners.iterator();
        while (it.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) it.
                    next();
            // UmlModelEventPump.getPump().removeModelEventListener(listener,
            // oper);
            eventPump.addModelEventListener(listener, oper);
        }
        eventPump.flushModelEvents();
        return oper;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildOperation(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.String,
     *      java.util.Collection)
     */
    public Object buildOperation(Object cls, Object model, Object voidType,
            String name, Collection propertyChangeListeners) {
        Object oper = buildOperation(cls, model, voidType,
                propertyChangeListeners);
        if (oper != null) {
            ((Operation) oper).setName(name);
        }
        eventPump.flushModelEvents();
        return oper;
    }

    /**
     * Constructs a default parameter.
     * 
     * @return The newly created parameter.
     */
    private Object buildParameter(Model model, Classifier voidType) {
        // this should not be here via the ProjectBrowser but the CoreHelper
        // should provide this functionality
        Parameter param = nsmodel.getUmlPackage().getCore().getParameter().
                createParameter();
        param.setType(voidType);
        param.setNamespace(model.getNamespace());
        eventPump.flushModelEvents();
        return param;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildParameter(java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.util.Collection)
     */
    public Object buildParameter(Object o, Object model, Object voidType,
            Collection propertyChangeListeners) {
        if (o instanceof Event) {
            Event event = (Event) o;
            Parameter res = (Parameter) buildParameter((Model) model,
                    (Classifier) voidType);
            res.setKind(ParameterDirectionKindEnum.PDK_IN);
            // removing this next line solves issue 2209
            // res.setNamespace(event.getNamespace());
            event.getParameter().add(res);
            eventPump.flushModelEvents();
            return res;
        } else if (o instanceof BehavioralFeature) {
            BehavioralFeature oper = (BehavioralFeature) o;
            if (oper == null || oper.getOwner() == null) {
                throw new IllegalArgumentException(
                        "operation is null or does not have an owner");
            }
            Parameter res = (Parameter) buildParameter((Model) model,
                    (Classifier) voidType);
            String name = "arg";
            int counter = 1;

            oper.getParameter().add(res);
            Iterator it = oper.getParameter().iterator();
            while (it.hasNext()) {
                Parameter para = (Parameter) it.next();
                if ((name + counter).equals(para.getName())) {
                    counter++;
                }
            }

            res.setName(name + counter);

            // we set the listeners to the figs here too
            // it would be better to do that in the figs themselves
            it = propertyChangeListeners.iterator();
            while (it.hasNext()) {
                PropertyChangeListener listener = (PropertyChangeListener) it.
                        next();
                eventPump.removeModelEventListener(listener, res);
                eventPump.addModelEventListener(listener, res);
            }
            eventPump.flushModelEvents();
            return res;
        } else {
            return null;
        }
    }

    /**
     * @see org.argouml.model.CoreFactory#buildRealization(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public Object buildRealization(Object clnt, Object spplr, Object model) {
        ModelElement client = (ModelElement) clnt;
        ModelElement supplier = (ModelElement) spplr;
        if (client == null || supplier == null || client.getNamespace() == null
                || supplier.getNamespace() == null) {
            throw new IllegalArgumentException("faulty arguments.");
        }
        Object realization = createAbstraction();
        Namespace nsc = client.getNamespace();
        Namespace nss = supplier.getNamespace();
        Namespace ns = null;
        if (nsc.equals(nss)) {
            ns = nsc;
        } else {
            ns = (Namespace) model;
        }
        nsmodel.getExtensionMechanismsFactory().buildStereotype(realization,
                "realize", ns);
        nsmodel.getCoreHelper().addClientDependency(client, realization);
        nsmodel.getCoreHelper().addSupplierDependency(supplier, realization);
        eventPump.flushModelEvents();
        return realization;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildUsage(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildUsage(Object client, Object supplier) {
        if (client == null || supplier == null) {
            throw new IllegalArgumentException("In buildUsage null arguments.");
        }
        if (!(client instanceof ModelElement)) {
            throw new IllegalArgumentException("client ModelElement");
        }
        if (!(supplier instanceof ModelElement)) {
            throw new IllegalArgumentException("supplier ModelElement");
        }
        Usage usage = (Usage) nsmodel.getCoreFactory().createUsage();
        usage.getSupplier().add(supplier);
        usage.getClient().add(client);
        if (((ModelElement) supplier).getNamespace() != null) {
            usage.setNamespace(((ModelElement) supplier).getNamespace());
        } else if (((ModelElement) client).getNamespace() != null) {
            usage.setNamespace(((ModelElement) client).getNamespace());
        }
        eventPump.flushModelEvents();
        return usage;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildComment(java.lang.Object,
     *      java.lang.Object)
     */
    public Object buildComment(Object element, Object model) {
        if (model == null) {
            throw new IllegalArgumentException("A namespace must be supplied.");
        }
        ModelElement elementToAnnotate = (ModelElement) element;
        Comment comment = (Comment) createComment();

        Namespace commentsModel = null;
        if (elementToAnnotate != null) {
            comment.getAnnotatedElement().add(elementToAnnotate);
            commentsModel = elementToAnnotate.getNamespace();
        } else {
            commentsModel = (Namespace) model;
        }

        comment.setNamespace(commentsModel);

        eventPump.flushModelEvents();
        return comment;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildConstraint(java.lang.Object)
     */
    public Object buildConstraint(Object constrElement) {
        ModelElement constrainedElement = (ModelElement) constrElement;
        if (constrainedElement == null) {
            throw new IllegalArgumentException("the constrained element is "
                    + "mandatory and may not be " + "null.");
        }
        Constraint con = (Constraint) createConstraint();
        con.getConstrainedElement().add(constrainedElement);
        con.setNamespace(constrainedElement.getNamespace());
        eventPump.flushModelEvents();
        return con;
    }

    /**
     * @see org.argouml.model.CoreFactory#buildConstraint(java.lang.String,
     *      java.lang.Object)
     */
    public Object buildConstraint(String name, Object bexpr) {
        if (bexpr == null || !(bexpr instanceof BooleanExpression)) {
            throw new IllegalArgumentException("invalid boolean expression.");
        }
        Constraint con = (Constraint) createConstraint();
        if (name != null) {
            con.setName(name);
        }
        con.setBody((BooleanExpression) bexpr);
        eventPump.flushModelEvents();
        return con;
    }

    /**
     * @param elem
     *            the abstraction to be deleted
     */
    void deleteAbstraction(Object elem) {
        if (!(elem instanceof Abstraction)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the association to be deleted
     */
    void deleteAssociation(Object elem) {
        if (!(elem instanceof UmlAssociation)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the a. to be deleted
     */
    void deleteAssociationClass(Object elem) {
        if (!(elem instanceof AssociationClass)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * Does a 'cascading delete' to all modelelements that are associated with
     * this element that would be in an illegal state after deletion of the
     * element. This method should not be called directly.
     * <p>
     * 
     * In the case of an associationend these are the following elements:
     * <ul>
     * <li>Binary Associations that 'loose' one of the associationends by this
     * deletion.
     * </ul>
     * 
     * @param elem
     * @see UmlFactoryMDRImpl#delete(Object)
     */
    void deleteAssociationEnd(Object elem) {
        if (!(elem instanceof AssociationEnd)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        UmlAssociation assoc = ((AssociationEnd) elem).getAssociation();
        if (assoc != null && assoc.getConnection() != null
                && assoc.getConnection().size() == 2) { // binary association
            nsmodel.getUmlFactory().delete(assoc);
        }
    }

    /**
     * @param elem
     *            the attribute to be deleted
     */
    void deleteAttribute(Object elem) {
        if (!(elem instanceof Attribute)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteBehavioralFeature(Object elem) {
        if (!(elem instanceof BehavioralFeature)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteBinding(Object elem) {
        if (!(elem instanceof Binding)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteClass(Object elem) {
        if (!(elem instanceof UmlClass)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * Does a 'cascading delete' to all modelelements that are associated with
     * this element that would be in an illegal state after deletion of the
     * element. Does not do an cascading delete for elements that are deleted by
     * the MDR method remove. This method should not be called directly.
     * <p>
     * 
     * In the case of a classifier these are the following elements:
     * <ul>
     * <li>AssociationEnds that have this classifier as type
     * </ul>
     * 
     * @param elem
     * @see UmlFactoryMDRImpl#delete(Object)
     */
    void deleteClassifier(Object elem) {
        if (!(elem instanceof Classifier)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
        Collection col = nsmodel.getFacade().getAssociationEnds(elem);
        Iterator it = col.iterator();
        while (it.hasNext()) {
            nsmodel.getUmlFactory().delete(it.next());
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteComment(Object elem) {
        if (!(elem instanceof Comment)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteComponent(Object elem) {
        if (!(elem instanceof Component)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteConstraint(Object elem) {
        if (!(elem instanceof Constraint)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteDataType(Object elem) {
        if (!(elem instanceof DataType)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteDependency(Object elem) {
        if (!(elem instanceof Dependency)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteElement(Object elem) {
        if (!(elem instanceof Element)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteElementResidence(Object elem) {
        if (!(elem instanceof ElementResidence)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteFeature(Object elem) {
        if (!(elem instanceof Feature)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteFlow(Object elem) {
        if (!(elem instanceof Flow)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteGeneralizableElement(Object elem) {
        if (!(elem instanceof GeneralizableElement)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

        GeneralizableElement generalizableElement = (GeneralizableElement) elem;
        // This iterator should be OK, because "delete" really just checks
        // things for later deletion if needed.
        Iterator it = generalizableElement.getGeneralization().iterator();
        while (it.hasNext()) {
            nsmodel.getUmlFactory().delete(it.next());
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteGeneralization(Object elem) {
        if (!(elem instanceof Generalization)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteInterface(Object elem) {
        if (!(elem instanceof Interface)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteMethod(Object elem) {
        if (!(elem instanceof Method)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * Does a 'cascading delete' to all modelelements that are associated with
     * this element that would be in an illegal state after deletion of the
     * element. Does not do an cascading delete for elements that are deleted by
     * the NSUML method remove. This method should not be called directly.
     * <p>
     * 
     * In the case of a modelelement these are the following elements:
     * <ul>
     * <li>Dependencies that have the modelelement as supplier or as a client
     * and are binary. (that is, they only have one supplier and one client)
     * </ul>
     * 
     * @param elem
     * @see UmlFactoryMDRImpl#delete(Object)
     */
    void deleteModelElement(Object elem) {
        if (!(elem instanceof ModelElement)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

        // Delete dependencies where this is the only client
        Collection deps = org.argouml.model.Model.getFacade()
                .getClientDependencies(elem);
        Iterator it = deps.iterator();
        while (it.hasNext()) {
            Dependency dep = (Dependency) it.next();
            if (dep.getClient().size() < 2
                    && dep.getClient().contains(elem)) {
                nsmodel.getUmlFactory().delete(dep);
            }
        }

        // Delete dependencies where this is the only supplier
        deps = org.argouml.model.Model.getFacade()
                .getSupplierDependencies(elem);
        it = deps.iterator();
        while (it.hasNext()) {
            Dependency dep = (Dependency) it.next();
            if (dep.getSupplier().size() < 2
                    && dep.getSupplier().contains(elem)) {
                nsmodel.getUmlFactory().delete(dep);
            }
        }

        Collection ownedBehaviors = nsmodel.getFacade().getBehaviors(elem);
        if (!ownedBehaviors.isEmpty()) {
            it = ownedBehaviors.iterator();
            while (it.hasNext()) {
                nsmodel.getUmlFactory().delete(it.next());
            }
        }

        eventPump.flushModelEvents();
    }

    /**
     * A namespace deletes its owned elements.
     * 
     * @param elem
     *            is the namespace.
     */
    void deleteNamespace(Object elem) {
        if (!(elem instanceof Namespace)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

        List ownedElements = new ArrayList();
        ownedElements.addAll(((Namespace) elem).getOwnedElement());
        Iterator it = ownedElements.iterator();
        while (it.hasNext()) {
            nsmodel.getUmlFactory().delete(it.next());
        }
        eventPump.flushModelEvents();
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteNode(Object elem) {
        if (!(elem instanceof Node)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteOperation(Object elem) {
        if (!(elem instanceof Operation)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteParameter(Object elem) {
        if (!(elem instanceof Parameter)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deletePermission(Object elem) {
        if (!(elem instanceof Permission)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deletePresentationElement(Object elem) {
        if (!(elem instanceof PresentationElement)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteRelationship(Object elem) {
        if (!(elem instanceof Relationship)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteStructuralFeature(Object elem) {
        if (!(elem instanceof StructuralFeature)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteTemplateParameter(Object elem) {
        if (!(elem instanceof TemplateParameter)) {
            throw new IllegalArgumentException("elem: " + elem);
        }
    }

    /**
     * @param elem
     *            the element to be deleted
     */
    void deleteUsage(Object elem) {
        if (!(elem instanceof Usage)) {
            throw new IllegalArgumentException("elem: " + elem);
        }

    }

    /**
     * Copies a class, and it's features. This may also require other
     * classifiers to be copied.
     * 
     * @param source
     *            is the class to copy.
     * @param ns
     *            is the namespace to put the copy in.
     * @return a newly created class.
     */
    public Object copyClass(Object source, Object ns) {
        if (!(source instanceof UmlClass && ns instanceof Namespace)) {
            throw new IllegalArgumentException("source: " + source + ",ns: "
                    + ns);
        }

        UmlClass c = (UmlClass) createClass();
        ((Namespace) ns).getOwnedElement().add(c);
        doCopyClass(source, c);
        return c;
    }

    /**
     * Copies a datatype, and it's features. This may also require other
     * classifiers to be copied.
     * 
     * @param source
     *            is the datatype to copy.
     * @param ns
     *            is the namespace to put the copy in.
     * @return a newly created data type.
     */
    public Object copyDataType(Object source, Object ns) {
        if (!(source instanceof DataType)) {
            throw new IllegalArgumentException();
        }

        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        DataType i = (DataType) createDataType();
        ((Namespace) ns).getOwnedElement().add(i);
        doCopyDataType(source, i);
        return i;
    }

    /**
     * Copies an interface, and it's features. This may also require other
     * classifiers to be copied.
     * 
     * @param source
     *            is the interface to copy.
     * @param ns
     *            is the namespace to put the copy in.
     * @return a newly created interface.
     */
    public Object copyInterface(Object source, Object ns) {
        if (!(source instanceof Interface)) {
            throw new IllegalArgumentException();
        }

        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        Interface i = (Interface) createInterface();
        ((Namespace) ns).getOwnedElement().add(i);
        doCopyInterface(source, i);
        return i;
    }

    /**
     * 
     * @param from
     *            The object which own the enumeration to copy
     * @param to
     *            The object to which copy the enumeration
     */
    public void copyEnumeration(Object from, Object to) {
        doCopyModelElement(from, to);
        List listFrom = ((Enumeration) from).getLiteral();
        List listTo = ((Enumeration) to).getLiteral();
        Object literalFrom;
        Object literalTo;
        for (int i = 0; i < listFrom.size(); i++) {
            literalFrom = listFrom.get(i);
            if (listTo.size() > i) {
                literalTo = listTo.get(i);
            } else {
                literalTo = createEnumerationLiteral();
                listTo.add(literalTo);
            }
            doCopyModelElement(literalFrom, literalTo);
            ((EnumerationLiteral) literalTo).setEnumeration((Enumeration) to);
        }
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     */
    private void doCopyElement(Object source, Object target) {
        // Nothing more to do.
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            the source class
     * @param target
     *            the target class
     */
    public void doCopyClass(Object source, Object target) {
        if (!(source instanceof UmlClass)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof UmlClass)) {
            throw new IllegalArgumentException();
        }

        doCopyClassifier(source, target);

        ((UmlClass) target).setActive(((UmlClass) source).isActive());
    }

    /*
     * TODO: All the ToDos in the doCopyFoo methods below are inherited from the
     * NSUML implementation and do not reflect new issues. One additional thing
     * which does need to be dealt with is the copying of any attributes which
     * have been added since this code was implemented for UML 1.3.
     */
    /**
     * Used by the copy functions. Do not call this function directly. 
     * TODO: actions? instances? collaborations etc?
     * 
     * @param source
     *            the source classifier
     * @param target
     *            the target classifier
     */
    public void doCopyClassifier(Object source, Object target) {
        if (!(source instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof Classifier)) {
            throw new IllegalArgumentException();
        }

        // TODO: how to merge multiple inheritance? Necessary?
        doCopyNamespace(source, target);
        doCopyGeneralizableElement(source, target);

        // TODO: Features
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            the source datatype
     * @param target
     *            the target datatype
     */
    public void doCopyDataType(Object source, Object target) {
        if (!(source instanceof DataType)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof DataType)) {
            throw new IllegalArgumentException();
        }

        doCopyClassifier(source, target);
    }

    /**
     * Used by the copy functions. Do not call this function directly. 
     * TODO: generalizations, specializations?
     * 
     * @param source
     *            the source generalizable element
     * @param target
     *            the target generalizable element
     */
    public void doCopyGeneralizableElement(Object source, Object target) {
        if (!(source instanceof GeneralizableElement
                && target instanceof GeneralizableElement)) {
            throw new IllegalArgumentException("source: " + source
                    + ",target: " + target);
        }

        doCopyModelElement(source, target);

        GeneralizableElement targetGE = ((GeneralizableElement) target);
        GeneralizableElement sourceGE = ((GeneralizableElement) source);
        targetGE.setAbstract(sourceGE.isAbstract());
        targetGE.setLeaf(sourceGE.isLeaf());
        targetGE.setRoot(sourceGE.isRoot());
        eventPump.flushModelEvents();
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            the source interface
     * @param target
     *            the target interface
     */
    public void doCopyInterface(Object source, Object target) {
        if (!(source instanceof Interface)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof Interface)) {
            throw new IllegalArgumentException();
        }

        doCopyClassifier(source, target);
    }

    /**
     * Used by the copy functions. Do not call this function directly. 
     * TODO: template parameters, default type 
     * TODO: constraining elements 
     * TODO: flows, dependencies, comments, bindings, contexts ??? 
     * TODO: contents, residences ???
     * 
     * @param source
     *            the source me
     * @param target
     *            the target me
     */
    public void doCopyModelElement(Object source, Object target) {
        if (!(source instanceof ModelElement)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof ModelElement)) {
            throw new IllegalArgumentException();
        }

        // Set the name so that superclasses can find the newly
        // created element in the model, if necessary.
        ModelElement targetME = ((ModelElement) target);
        ModelElement sourceME = ((ModelElement) source);
        targetME.setName(sourceME.getName());
        doCopyElement(source, target);

        targetME.setSpecification(sourceME.isSpecification());
        targetME.setVisibility(sourceME.getVisibility());
        nsmodel.getDataTypesHelper().copyTaggedValues(source, target);

        if (!sourceME.getStereotype().isEmpty()) {
            // Note that if we're copying this element then we
            // must also be allowed to copy other necessary
            // objects.
            Model targetModel = (Model) org.argouml.model.Model.getFacade()
                    .getModel(targetME);
            Iterator it = sourceME.getStereotype().iterator();
            while (it.hasNext()) {
                Stereotype st = (Stereotype) nsmodel.getModelManagementHelper()
                        .getCorrespondingElement(it.next(), targetModel, true);
                targetME.getStereotype().add(st);
            }
        }
        eventPump.flushModelEvents();
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            the source namespace
     * @param target
     *            the target namespace
     */
    public void doCopyNamespace(Object source, Object target) {
        if (!(source instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        if (!(target instanceof Namespace)) {
            throw new IllegalArgumentException();
        }

        doCopyModelElement(source, target);
        // Nothing more to do, don't copy owned elements.
    }

    /**
     * Helper method to return the MDR corePackage for model element creation.
     * 
     * @return the core package
     */
    private CorePackage getCorePackage() {
        if (corePackage == null) {
            corePackage = nsmodel.getUmlPackage().getCore();
        }
        return corePackage;
    }

    // / UML 1.4+

    /**
     * @return Object
     */
    public Object createEnumeration() {
        Enumeration myEnumeration = getCorePackage().getEnumeration()
                .createEnumeration();
        super.initialize(myEnumeration);
        return myEnumeration;
    }

    /**
     * @return Object
     */
    public Object createEnumerationLiteral() {
        EnumerationLiteral myEnumerationLiteral = getCorePackage()
                .getEnumerationLiteral().createEnumerationLiteral();
        super.initialize(myEnumerationLiteral);
        eventPump.flushModelEvents();
        return myEnumerationLiteral;
    }

    /**
     * @param name
     *            The name of the EnumerationLiteral
     * @return Object
     */
    public Object buildEnumerationLiteral(String name) {
        EnumerationLiteral myEnumerationLiteral = 
            (EnumerationLiteral) createEnumerationLiteral();
        myEnumerationLiteral.setName(name);
        eventPump.flushModelEvents();
        return myEnumerationLiteral;
    }

}
