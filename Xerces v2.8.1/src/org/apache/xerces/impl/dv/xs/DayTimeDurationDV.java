/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

/**
 * Used to validate the <dayTimeDuration> type
 *
 * @xerces.internal 
 * 
 * @author Ankit Pasricha, IBM
 * 
 * @version $Id: DayTimeDurationDV.java 320310 2005-05-06 15:31:15Z ankitp $
 */
class DayTimeDurationDV extends DurationDV {
    
    public Object getActualValue(String content, ValidationContext context)
        throws InvalidDatatypeValueException {
        try {
            return parse(content, DurationDV.DAYTIMEDURATION_TYPE);
        } 
        catch (Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "dayTimeDuration"});
        }
    }
    
    protected Duration getDuration(DateTimeData date) {
        int sign = 1;
        if (date.day<0 || date.hour<0 || date.minute<0 || date.second<0) {
            sign = -1;
        }
        return factory.newDuration(sign == 1, null, null, 
                date.day != DatatypeConstants.FIELD_UNDEFINED?BigInteger.valueOf(sign*date.day):null, 
                date.hour != DatatypeConstants.FIELD_UNDEFINED?BigInteger.valueOf(sign*date.hour):null, 
                date.minute != DatatypeConstants.FIELD_UNDEFINED?BigInteger.valueOf(sign*date.minute):null, 
                date.second != DatatypeConstants.FIELD_UNDEFINED?new BigDecimal(String.valueOf(sign*date.second)):null);
    }
}
