// $Id: ExtensionMechanismsFactoryMDRImpl.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.argouml.model.CoreHelper;
import org.argouml.model.ExtensionMechanismsFactory;
import org.argouml.model.ExtensionMechanismsHelper;
import org.argouml.model.ModelEventPump;
import org.omg.uml.foundation.core.AStereotypeExtendedElement;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.modelmanagement.Model;

/**
 * Factory to create UML classes for the UML ExtensionMechanisms
 * package.
 * 
 * TODO: Change visibility to package after reflection problem solved.
 * <p>
 * @since ARGO0.19.5
 * @author Ludovic Maitre
 * @author Tom Morris
 * <p>
 * derived from NSUML implementation by:
 * @author Thierry Lach
 */
public class ExtensionMechanismsFactoryMDRImpl extends
        AbstractUmlModelFactoryMDR implements ExtensionMechanismsFactory {

    /**
     * The model implementation.
     */
    private MDRModelImplementation nsmodel;
    
    /**
     * The model event pump.
     */
    private ModelEventPump eventPump;

    /**
     * The logger.
     */
    private Logger LOG = Logger.
            getLogger(ExtensionMechanismsFactoryMDRImpl.class);

    /**
     * The stereotype helper.
     */
    private AStereotypeExtendedElement stereotypeHelper;

    /**
     * The extension mechanism helper.
     */
    private ExtensionMechanismsHelper extensionHelper;

    /**
     * Don't allow instantiation.
     * 
     * @param implementation
     *            To get other helpers and factories.
     */
    ExtensionMechanismsFactoryMDRImpl(MDRModelImplementation implementation) {
        nsmodel = implementation;
        stereotypeHelper = nsmodel.getUmlPackage().getCore().
                getAStereotypeExtendedElement();
        extensionHelper = implementation.getExtensionMechanismsHelper();
        eventPump = nsmodel.getModelEventPump();
    }

    /**
     * Create an empty but initialized instance of a UML TaggedValue.
     * 
     * @return an initialized UML TaggedValue instance.
     */
    public Object createTaggedValue() {
        TaggedValue tv = nsmodel.getUmlPackage().getCore().getTaggedValue().
                createTaggedValue();
        super.initialize(tv);
        eventPump.flushModelEvents();
        return tv;
    }

    /**
     * Get an instance of a UML TagDefinition.
     * 
     * @param tagName The name of the TagDefinition to create/retrieve
     * @return an initialized UML TaggedValue instance.
     */
    public Object getTagDefinition(String tagName) {
        synchronized (tagDefinitions) {
            // TODO: We should query the repository, not a private cache - tfm
            TagDefinition td = (TagDefinition) tagDefinitions.get(tagName);
            if (td == null) {
                td = nsmodel.getUmlPackage().getCore().getTagDefinition().
                        createTagDefinition();
                td.setName(tagName);
                // TODO: Add something in the GUI of TagDefinition for setting
                // this
                td.setTagType("String");

                /*
                 * TODO: When called from the tests there is no root model
                 * set up.  Should this be considered an error?  We handle
                 * it silently for now. - tfm
                 */
                Object rootModel = nsmodel.getModelManagementFactory()
                        .getRootModel();
                if (rootModel != null) {
                    nsmodel.getCoreHelper().addOwnedElement(rootModel, td);
                }
                
                /*
                 * TODO: should be owned by a Stereotype according to
                 * the UML spec (section 2.6.2.4 of UML 1.4) - tfm
                 */
                
                tagDefinitions.put(tagName, td);
                super.initialize(td);
                eventPump.flushModelEvents();
            }
            return td;
        }
    }

    private Map tagDefinitions = Collections.synchronizedMap(new HashMap());

    /**
     * Builds a stereotype for some kind of modelelement.
     * 
     * TODO: MVW: This needs rethinking/rework! I have the following questions:
     * Why does it not search for a stereotype in the namespace using properties
     * and only create a new stereotype if it will actually be used? Ie, why is
     * there not a getStereotype(String name, String baseClass)? (edited by
     * d00mst)  <these comments imported from NSUML implementation - tfm>
     * 
     * @param theModelElementObject
     *            a Model Element that the stereotype will be applied to. The
     *            stereotype will have its BaseClass set to an appropriate value
     *            for this kind of Model Elements.
     * @param theName
     *            the name for the stereotype
     * @param theNamespaceObject
     *            the namespace the stereotype will be created within.
     * @return the resulting stereotype object
     * @throws IllegalArgumentException
     *             if either argument is null.
     */
    public Object buildStereotype(
            Object theModelElementObject,
            Object theName,
            Object theNamespaceObject) {
        
        if (theModelElementObject == null || theName == null
                || theNamespaceObject == null) {
            throw new IllegalArgumentException("one of the arguments is null");
        }
        
        ModelElement me = (ModelElement) theModelElementObject;
        
        String text = (String) theName;
        Namespace ns = (Namespace) theNamespaceObject;
        Stereotype stereo = buildStereotype(text);
        stereo.getBaseClass().add(extensionHelper.getMetaModelName(me));
        // TODO: this doesn't look right - review - tfm
        Stereotype stereo2 = (Stereotype) extensionHelper.getStereotype(ns,
                stereo);
        if (stereo2 != null) {
            me.getStereotype().add(stereo2);
            nsmodel.getUmlFactory().delete(stereo);
            eventPump.flushModelEvents();
            return stereo2;
        }
        stereo.setNamespace(ns);
        me.getStereotype().add(stereo);
        eventPump.flushModelEvents();
        return stereo;
    }

    /**
     * Builds an initialized stereotype.
     * 
     * @param theModelElementObject
     *            the baseclass for the new stereotype
     * @param theName
     *            the name for the new stereotype
     * @param model
     *            the current model of interest
     * @param models
     *            all the models
     * @return the new stereotype
     */
    public Object buildStereotype(
            Object theModelElementObject,
            String theName,
            Object model,
            Collection models) {
        
        ModelElement me = (ModelElement) theModelElementObject;

        Stereotype stereo = buildStereotype(theName);
        stereo.getBaseClass().add(
                nsmodel.getExtensionMechanismsHelper().getMetaModelName(me));
        Stereotype stereo2 = (Stereotype) extensionHelper.getStereotype(models,
                stereo);
        if (stereo2 != null) {
            me.getStereotype().add(stereo2);
            nsmodel.getUmlFactory().delete(stereo);
            return stereo2;
        }
        stereo.setNamespace((Model) model);
        if (me != null) {
            me.getStereotype().add(stereo);
        }
        eventPump.flushModelEvents();
        return stereo;
    }

    /**
     * Builds an initialized stereotype with no namespace. A stereotype must
     * have a namespace so this method is unsafe. Use buildStereotype(String,
     * Object).
     * 
     * @param text
     *            is the name of the stereotype
     * @return an initialized stereotype.
     */
    private Stereotype buildStereotype(String text) {
        Stereotype stereotype = nsmodel.getUmlPackage().getCore().
                getStereotype().createStereotype();
        super.initialize(stereotype);
        stereotype.setName(text);
        LOG.info("Created a new stereotype of <<" + text + ">>");
        return stereotype;
    }

    /**
     * Builds an initialized stereotype.
     * 
     * @param text
     *            is the name of the stereotype
     * @param ns
     *            namespace where the stereotype lives (is known)
     * @return an initialized stereotype.
     */
    public Object buildStereotype(String text, Object ns) {
        
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException(
                    "Namespace is not of the good type! text:" + text + ",ns:"
                            + ns);
        }
        Stereotype stereo = buildStereotype(text);
        if (ns != null && ns instanceof Namespace) {
            stereo.setNamespace((Namespace) ns);
        }
        eventPump.flushModelEvents();
        return stereo;
    }

    /**
     * Build an initialized instance of a UML TaggedValue.
     * 
     * @param tag
     *            is the tag name (a String).
     * @param value
     *            is the value (a String).
     * @return an initialized UML TaggedValue instance.
     */
    public Object buildTaggedValue(String tag, String value) {
        return buildTaggedValue(tag, value, null);
    }

    /**
     * Build an initialized instance of a UML TaggedValue.
     * 
     * @param tag
     *            is the tag name (a String).
     * @param value
     *            is the value (a String).
     * @param tagType
     *            is the name of the TagDefinition
     * @return an initialized UML TaggedValue instance.
     */
    public Object buildTaggedValue(String tag, String value, String tagType) {
        TaggedValue tv = (TaggedValue) createTaggedValue();
        tv.setType((TagDefinition) getTagDefinition(tag));
        if (tagType != null) {
            tv.getType().setTagType(tagType);
        }
        // TODO: It seems that the other CASE tools manage only one
        // dataValue. This is an array of String according to the
        // UML 1.4 specs.
        tv.getDataValue().add(value);
        eventPump.flushModelEvents();
        return tv;
    }

    /**
     * @param elem
     *            the stereotype
     */
    void deleteStereotype(Object elem) {
        if (!(elem instanceof Stereotype)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param elem
     *            the taggedvalue
     */
    void deleteTaggedValue(Object elem) {
        if (!(elem instanceof TaggedValue)) {
            throw new IllegalArgumentException();
        }
    }

    void deleteTagDefinition(Object elem) {
        if (!(elem instanceof TagDefinition)) {
            throw new IllegalArgumentException();            
        }
        //TODO: Also delete the related TaggedValues. 
    }
    /**
     * Copies a stereotype.
     * 
     * @param source
     *            is the stereotype to copy.
     * @param ns
     *            is the namespace to put the copy in.
     * @return a newly created stereotype
     */
    public Object copyStereotype(Object source, Object ns) {
        if (!(source instanceof Stereotype)) {
            throw new IllegalArgumentException("source");
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException("namespace");
        }

        Stereotype st = buildStereotype(null);
        ((Namespace) ns).getOwnedElement().add(st);
        doCopyStereotype((Stereotype) source, st);
        eventPump.flushModelEvents();
        return st;
    }

    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            The stereotype to copy from.
     * @param target
     *            The object becoming a copy.
     */
    private void doCopyStereotype(Stereotype source, Stereotype target) {
        ((CoreFactoryMDRImpl) nsmodel.getCoreFactory())
                .doCopyGeneralizableElement(source, target);
        target.getBaseClass().clear();
        target.getBaseClass().addAll(source.getBaseClass());
        target.setIcon(source.getIcon());
        // TODO: constraints
        // TODO: required tags
    }

    /**
     * @see org.argouml.model.ExtensionMechanismsFactory#buildTagDefinition(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Object buildTagDefinition(String text, Object owner, Object ns) {
        if (!(owner instanceof Stereotype || owner == null)) {
            throw new IllegalArgumentException("owner: " + owner);
        }
        if (!(ns instanceof Namespace)) {
            throw new IllegalArgumentException("namespace: " + ns);
        }
        Object td = createTagDefinition();
        CoreHelper coreHelper = org.argouml.model.Model.getCoreHelper();
        if (owner != null)
            coreHelper.setOwner(td, owner);
        coreHelper.setNamespace(td, ns);
        coreHelper.setName(td, text);
        return td;
    }

    /**
     * @see org.argouml.model.ExtensionMechanismsFactory#createTagDefinition()
     */
    public Object createTagDefinition() {
        TagDefinition td = nsmodel.getUmlPackage().getCore().getTagDefinition()
                .createTagDefinition();
        super.initialize(td);
        eventPump.flushModelEvents();
        return td;
    }

    /**
     * @see org.argouml.model.ExtensionMechanismsFactory#copyTagDefinition(java.lang.Object, java.lang.Object)
     */
    public Object copyTagDefinition(Object anElement, Object aNs) {
        if (!(anElement instanceof TagDefinition)) {
            throw new IllegalArgumentException("source: "+anElement);
}
        if (!(aNs instanceof Namespace || aNs instanceof Stereotype)) {
            throw new IllegalArgumentException("namespace: "+aNs);
        }
        TagDefinition source = (TagDefinition) anElement;
        TagDefinition td = (TagDefinition)createTagDefinition();
        if (aNs instanceof Namespace)
            ((Namespace) aNs).getOwnedElement().add(td);
        else
            ((Stereotype) aNs).getDefinedTag().add(td);
        doCopyTagDefinition(source, td);
        eventPump.flushModelEvents();
        return td;
    }
    /**
     * Used by the copy functions. Do not call this function directly.
     * 
     * @param source
     *            The stereotype to copy from.
     * @param target
     *            The object becoming a copy.
     */
    private void doCopyTagDefinition(TagDefinition source, TagDefinition target) {
        ((CoreFactoryMDRImpl) nsmodel.getCoreFactory())
                .doCopyModelElement(source, target);
        if (source.getOwner()!=null) {
            //Stereotype stereo = nsmodel.getModelManagementHelper().getCorrespondingElement(source.getOwner().)
        }
        target.setTagType(source.getTagType());
        // TODO: constraints
        // TODO: required tags
    }    
}
