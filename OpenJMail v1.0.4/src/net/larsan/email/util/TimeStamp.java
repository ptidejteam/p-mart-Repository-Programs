/*
*  Code: TimeStamp.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU General Public License
*       as published by the Free Software Foundation; either version 2
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU General Public License for more details.
*
*       You should have received a copy of the GNU General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email.util;

import java.util.*;
import java.text.*;

/**
* This class handles date for email headers according to the syntax in RFC 822. Any 
* string passed to this class <code>parse</code> method must have correct syntax, 
* otherwise a ParseException will be thrown.<p>
*
* For detailed information of the date syntax, please have a look at RFC 822, but
* here's a simplification: A full date field might be expressed like this, where
* the figures surrounded by a "[" and a "]" may be ommitted and the "*" is indicating
* a time zone figure described below:
*
* <pre>
*    [Sun, ]12 Nov 2000 20:06[:31] *
* </pre>
*
* The "*" should be replaced by a time zone indicator, showing the offset in time
* from UTC (GMT). The zone can be expressed with military character notation 
* (single char, 'z' = UTC, 'a' = -1h and then on to 'm' = -12 ('j' is not counted), 
* 'n' = +1h and on to 'y' = +12h), offset expressed as hours and minutes in four 
* integers preceeded be a '+' or a '-' sign (+1h 30min = "+0130" and -11h = "-1100") 
* or alpha time zones (PST, GMT, UT and so on...). Thus the date above in the 
* US pacific time zone can be written as all three lines below:
*
* <pre>
*    Sun, 12 Nov 2000 20:06:31 PST
*    12 Nov 2000 20:06:31 -0800
*    Sun, 12 Nov 2000 20:06:31 H
* </pre>
*
* The year can be notated in two digits but it is recomended to use all four
* to avoid bad interpretation.
*
* @author Lars J. Nilsson
* @version 1.0 12/11/00
*/

public class TimeStamp {
    
    /** Private contructor to aviod instances. */
    
    private TimeStamp() { }
    
    
    /**
    * Parse this date string to a Date object. This method wil throw
    * a ParseException if the syntax in the string is incorrect.
    */
	
	public static Date parse(String original) throws ParseException { 
	
	   try {
	       
	        int year = 0;
	        int month = 0;
	        int date = 0;
	        int hour = 0;
	        int minute = 0;
	        int second = 0;
	        int offset = 0;
	        
	        // the string can be broken down in token separated by a 
	        // space character so...
	        
	        StringTokenizer str = new StringTokenizer(original, " ");
	        
	        // first the date, if the token ends with a ',' character
	        // it is a week day so continue to the next token
	        
	        String token = str.nextToken();
	        if(token.endsWith(",")) token = str.nextToken();
	        date = Integer.parseInt(token);
	        
	        // parse month
	        
	        token = str.nextToken();
	        month = parseMonth(token);
	        
	        // parse year, if the year is two digits and below 70 I'll
	        // believe it's in the new millenium and if it two digits between
	        // 70 and 99 I'll say it th 20th century
	        
	        token = str.nextToken();
	        year = Integer.parseInt(token);
	        
	        if(year < 70) year += 2000;
	        else if(year < 99) year += 1900;
	        
	        // parse time, the syntax is rigid so we can substring using
	        // positions, if the length is over 5 the time contains seconds
	        
	        token = str.nextToken();
	        hour = Integer.parseInt(token.substring(0, 2));
	        minute = Integer.parseInt(token.substring(3, 5));
	        
	        if(token.length() > 5) second = Integer.parseInt(token.substring(6, 8));

            // next goes the offset

            token = str.nextToken();
            
            if(token.charAt(0) == '+' || token.charAt(0) == '-') offset = getRawOffset(token);
            else if(token.length() == 1) offset = getMilitaryOffset(token);
            else offset = getAlphaOffset(token);
            
            // offset in milliseconds please
            
            offset = offset * 60 * 1000;
            
            // contruct a GMT calendar, set values and return
            
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            cal.clear();
	        
	        cal.set(Calendar.YEAR, year);
	        cal.add(Calendar.MONTH, month);
	        cal.set(Calendar.DATE, date);
	        cal.set(Calendar.HOUR_OF_DAY, hour);
	        cal.set(Calendar.MINUTE, minute);
	        cal.set(Calendar.SECOND, second);
	        cal.set(Calendar.MILLISECOND, offset); // offset in milliseconds

            return cal.getTime();   
	        
	     } catch(Exception e) {
	       
	           // catch all sorts of exceptions from invalid syntax
	       
	           if(e instanceof ParseException) throw (ParseException)e;
	           else throw new ParseException("Invalid syntax in string: " + original, 0);
	           
	     }
	}

    
    /**
    * Format this date according to the RFC 822 syntax rules. Will use
    * a time zone offset notation in digits indicating the offset from GMT.
    */

	public static String format(Date date) {
	   
	    // first: all except the offset is really simple and can be done with 
	    // a SimpleDateFormat as below, we'll use an english Locale to make sure
	    // we get the week day and month in english
	   
	    StringBuffer answer = new StringBuffer();
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
	    
	    answer.append(sdf.format(date));
	    
	    // now, get a default Calendar and set the time
	    
	    Calendar cal = new GregorianCalendar();
	    cal.clear();
	    cal.setTime(date);
	    
	    // get the default (system) time zone and calculate offset
	    
	    TimeZone zone = cal.getTimeZone();
	    
	    int offset = zone.getOffset(cal.get(Calendar.ERA), 
	                   cal.get(Calendar.YEAR), 
	                   cal.get(Calendar.MONTH), 
	                   cal.get(Calendar.DATE), 
	                   cal.get(Calendar.DAY_OF_WEEK), 
	                   cal.get(Calendar.MILLISECOND));
	                   
	     // append a plus or minus sign according to the offset
	                   
	     if(offset > 0) answer.append(" +");
	     else {
	         
	         answer.append(" -");
	         offset = Math.abs(offset); 
	     }
	     
	     // calculate the offset in hours and minutes and append and return
	     
	     int offset_h = (offset / 1000 / 60) / 60;
	     int offset_m = (offset / 1000 / 60) % 60;
	     
	     answer.append(offset_h / 10).append(offset_h % 10);
	     answer.append(offset_m / 10).append(offset_m % 10);
	     
         return answer.toString();
         
	}

    
    // parse the month syntax string

    private static int parseMonth(String month) throws ParseException {
        
        int answer = -1;
        
        String[] m = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", 
                "Aug", "Sep", "Oct", "Nov", "Dec" };
            
        for(int i = 0; i < m.length; i++) {
                
            if(m[i].equalsIgnoreCase(month)) {
                    
                answer = i;
                break;
            }
        }
        
        if(answer == -1) throw new ParseException("Invalid month format: " + month, 0);
        else return answer;
    }

    
    // parse the offset in digit notation

    private static int getRawOffset(String offset) throws ParseException {
        
        try {
            
            // take away the plus or minus sign
        
            char[] ch = (offset.substring(1)).toCharArray();
            
            int answer = Integer.parseInt("" + ch[0]) * 10 * 60; // "tens" of hours
            answer += Integer.parseInt("" + ch[1]) * 60; // hours
            answer += Integer.parseInt("" + ch[2]) * 10; // "tens" of minutes
            answer += Integer.parseInt("" + ch[3]); // minutes
            
            // to make this work we'll have to switch the plus and minus
            
            if(offset.charAt(0) == '-') return answer; // return positive
            else return -answer; // return negative

        } catch(Exception e) {
            
            throw new ParseException("Invalid time zone offset format:" + offset, 0);
            
        }
    }

    
    // parse the military notaion offset

    private static int getMilitaryOffset(String offset) throws ParseException {
        
        int answer = 666; // the number of the beast
        
        String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", 
            "w", "x", "y", "z" }; 
            
        for(int i = 0; i < chars.length; i++) {
            
            if(chars[i].equalsIgnoreCase(offset)) {
                answer = i + 1; // add one to make the character location computable
                break;
            }
        }
    
        // oops... the 'rough beast slouching towards Bethlehem ?
    
        if(answer == 666) throw new ParseException("Invalid time zone offset format: " + offset, 0);
    
        // again swith positive and negative and compute minutes
    
        if(answer < 13) return (-(answer * 60));
        else if(answer == chars.length) return 0;
        else return (answer - 12) * 60;
    }
    
    
    // get alpha notated offset

    private static int getAlphaOffset(String offset) throws ParseException {
        
        int answer = -1;
        
        String[] m = { "UT", "GMT", "EST", "EDT", "CST", "CDT", "MST", 
                "MDT", "PST", "PDT" };
            
        for(int i = 0; i < m.length; i++) {
                
            if(m[i].equalsIgnoreCase(offset)) {
                    
                answer = i;
                break;
            }
        }
        
        if(answer == -1) throw new ParseException("Invalid time zone offset format: " + offset, 0);

        switch(answer) {
            
            case 0 :
            case 1 :
            
                answer = 0; // UT or GMT
                break;
                
            case 2 : 
                        
                answer = 5 * 60; // EST
                break;
                
            case 3 :
                        
                answer = 4 * 60; // EDT
                break;
                
            case 4 :
                        
                answer = 6 * 60; // CST
                break;
                
            case 5 :
                        
                answer = 5 * 60; // CDT
                break;
                
            case 6 :
                        
                answer = 7 * 60; // MST
                break;
                
            case 7 :
                        
                answer = 6 * 60; // MDT
                break;
                
            case 8 :
                        
                answer = 8 * 60; // PST
                break;
                
            case 9 : 
                        
                answer = 7 * 60; // PDT
                break;
                
            default : answer = -1; // Error
            
        }
    
        if(answer == -1) throw new ParseException("Invalid time zone offset format:" + offset, 0);
        else return answer;
    }
}