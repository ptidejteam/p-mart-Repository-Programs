// $Id: DataTypesFactoryMDRImpl.java,v 1.2 2006/03/02 05:07:41 vauchers Exp $
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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.argouml.model.DataTypesFactory;
import org.argouml.model.ModelEventPump;
import org.omg.uml.foundation.datatypes.ActionExpression;
import org.omg.uml.foundation.datatypes.ArgListsExpression;
import org.omg.uml.foundation.datatypes.BooleanExpression;
import org.omg.uml.foundation.datatypes.Expression;
import org.omg.uml.foundation.datatypes.IterationExpression;
import org.omg.uml.foundation.datatypes.MappingExpression;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.foundation.datatypes.ObjectSetExpression;
import org.omg.uml.foundation.datatypes.ProcedureExpression;
import org.omg.uml.foundation.datatypes.TimeExpression;
import org.omg.uml.foundation.datatypes.TypeExpression;

/**
 * Factory to create UML classes for the UML DataTypes package.
 * <p>
 * 
 * TODO: Change visibility to package after reflection problem solved.
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
 * @author Tom Morris
 * derived from NSUML implementation by:
 * @author Thierry Lach
 */
public class DataTypesFactoryMDRImpl extends AbstractUmlModelFactoryMDR
        implements DataTypesFactory {

    /**
     * The model implementation.
     */
    private MDRModelImplementation modelImpl;
    
    /**
     * The model event pump.
     */
    private ModelEventPump eventPump;
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(CoreHelperMDRImpl.class);

    /**
     * Don't allow instantiation.
     * 
     * @param implementation
     *            To get other helpers and factories.
     */
    DataTypesFactoryMDRImpl(MDRModelImplementation implementation) {
        this.modelImpl = implementation;
        this.eventPump = modelImpl.getModelEventPump(); 
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createActionExpression(java.lang.String, java.lang.String)
     */
    public Object createActionExpression(String language, String body) {
        ActionExpression myActionExpression = modelImpl.getUmlPackage()
                .getDataTypes().getActionExpression()
                .createActionExpression(language, body);
        super.initialize(myActionExpression);
        eventPump.flushModelEvents();
        return myActionExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createArgListsExpression(java.lang.String, java.lang.String)
     */
    public Object createArgListsExpression(String language, String body) {
        ArgListsExpression myArgListsExpression = modelImpl.getUmlPackage()
                .getDataTypes().getArgListsExpression()
                .createArgListsExpression(language, body);
        super.initialize(myArgListsExpression);
        eventPump.flushModelEvents();
        return myArgListsExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createBooleanExpression(java.lang.String, java.lang.String)
     */
    public Object createBooleanExpression(String language, String body) {
        BooleanExpression myBooleanExpression = modelImpl.getUmlPackage()
                .getDataTypes().getBooleanExpression()
                .createBooleanExpression(language, body);
        super.initialize(myBooleanExpression);
        eventPump.flushModelEvents();
        return myBooleanExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createExpression(java.lang.String, java.lang.String)
     */
    public Object createExpression(String language, String body) {
        Expression myExpression = modelImpl.getUmlPackage().getDataTypes()
                .getExpression().createExpression(language, body);
        super.initialize(myExpression);
        eventPump.flushModelEvents();
        return myExpression;
    }

    /*
     * @see org.argouml.model.DataTypesFactory#createIterationExpression(java.lang.String,
     *      java.lang.String)
     */
    public Object createIterationExpression(String language, String body) {
        IterationExpression myIterationExpression = modelImpl.getUmlPackage()
                .getDataTypes().getIterationExpression()
                .createIterationExpression(language, body);
        super.initialize(myIterationExpression);
        eventPump.flushModelEvents();
        return myIterationExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createMappingExpression(java.lang.String, java.lang.String)
     */
    public Object createMappingExpression(String language, String body) {
        MappingExpression myMappingExpression = modelImpl.getUmlPackage()
                .getDataTypes().getMappingExpression().createMappingExpression(
                        language, body);
        super.initialize(myMappingExpression);
        eventPump.flushModelEvents();
        return myMappingExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createObjectSetExpression(java.lang.String, java.lang.String)
     */
    public Object createObjectSetExpression(String language, String body) {
        ObjectSetExpression myObjectSetExpression = modelImpl.getUmlPackage()
                .getDataTypes().getObjectSetExpression()
                .createObjectSetExpression(language, body);
        super.initialize(myObjectSetExpression);
        eventPump.flushModelEvents();
        return myObjectSetExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createProcedureExpression(java.lang.String, java.lang.String)
     */
    public Object createProcedureExpression(String language, String body) {
        ProcedureExpression myProcedureExpression = modelImpl.getUmlPackage()
                .getDataTypes().getProcedureExpression()
                .createProcedureExpression(language, body);
        super.initialize(myProcedureExpression);
        eventPump.flushModelEvents();
        return myProcedureExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createTimeExpression(java.lang.String, java.lang.String)
     */
    public Object createTimeExpression(String language, String body) {
        TimeExpression myTimeExpression = modelImpl.getUmlPackage()
                .getDataTypes().getTimeExpression()
                .createTimeExpression(language, body);
        super.initialize(myTimeExpression);
        eventPump.flushModelEvents();
        return myTimeExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createTypeExpression(java.lang.String, java.lang.String)
     */
    public Object createTypeExpression(String language, String body) {
        TypeExpression myTypeExpression = modelImpl.getUmlPackage()
                .getDataTypes().getTypeExpression()
                .createTypeExpression(language, body);
        super.initialize(myTypeExpression);
        eventPump.flushModelEvents();
        return myTypeExpression;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createMultiplicity(int, int)
     */
    public Object createMultiplicity(int lower, int upper) {
        Multiplicity multiplicity = modelImpl.getUmlPackage().getDataTypes()
                .getMultiplicity().createMultiplicity();
        multiplicity.getRange().add(createMultiplicityRange(lower, upper));
        super.initialize(multiplicity);
        eventPump.flushModelEvents();
        return multiplicity;
    }

    /*
     * TODO: As currently implemented, this expects a list of
     * MultiplicityRanges. Is this an interface change from the NSUML
     * implementation? I suspect it used to accept a list of Integers. - tfm
     */
    /**
     * @see org.argouml.model.DataTypesFactory#createMultiplicity(java.util.List)
     */
    public Object createMultiplicity(List range) {
        Multiplicity multiplicity = modelImpl.getUmlPackage().getDataTypes()
                .getMultiplicity().createMultiplicity();
        multiplicity.getRange().addAll(range);
        super.initialize(multiplicity);
        eventPump.flushModelEvents();
        return multiplicity;
    }

    /**
     * @see org.argouml.model.DataTypesFactory#createMultiplicity(java.lang.String)
     */
    public Object createMultiplicity(String str) {
        List ranges = Collections.unmodifiableList(parseRanges(str));
        return createMultiplicity(ranges);
    }

    private List parseRanges(String str) {
        StringTokenizer stk = new StringTokenizer(str, ",");
        if (!stk.hasMoreTokens()) {
            throw new IllegalArgumentException("empty multiplicity");
        }
        List rc = new ArrayList();
        while (stk.hasMoreTokens()) {
            rc.add(createMultiplicityRange(stk.nextToken()));
        }
        return rc;
    }

    /**
     * Create a MultiplicityRange from a string.
     * TODO: Needs to be added to Model interface
     * @since UML 1.4
     * @param str the string definition of the range
     * @return MultiplicityRange A multiplicity range
     */
    public MultiplicityRange createMultiplicityRange(String str) {
        StringTokenizer stk = new StringTokenizer(str, ". ");
        if (!stk.hasMoreTokens()) {
            throw new IllegalArgumentException("empty multiplicity range");
        }
        int lower = s2b(stk.nextToken());
        int upper = 0;
        if (!stk.hasMoreTokens()) {
            upper = lower;
            // Convert "*" to "0..*" instead of "*..*"
            if (lower == -1) {
                lower = 0;
            }
        } else {
            upper = s2b(stk.nextToken());
            if (stk.hasMoreTokens()) {
                throw new IllegalArgumentException(
                        "illegal range specification");
            }
        }
        return createMultiplicityRange(lower, upper);
    }
    
    /**
     * Create a MultiplicityRange from a pair of integers.
     * 
     * TODO: Needs to be added to Model interface
     * 
     * @since UML 1.4
     * @param lower
     *            the lower bound of the range
     * @param upper
     *            the upper bound of the range
     * @return MultiplictyRange A multiplicity range
     */
    public MultiplicityRange createMultiplicityRange(int lower, int upper) {
        MultiplicityRange range = 
            modelImpl.getUmlPackage().getDataTypes().getMultiplicityRange()
                .createMultiplicityRange(lower, upper);
        eventPump.flushModelEvents();
        return range;
    }

    static String b2s(int i) {
        if (i == -1)
            return "*";
        else
            return "" + i;
    }

    static int s2b(String b) {
        try {
            if (b.equals("n") || b.equals("*"))
                return -1;
            else
                return Integer.parseInt(b);
        } catch (Exception ex) {
            throw new IllegalArgumentException("illegal range bound");
        }
    }
}
