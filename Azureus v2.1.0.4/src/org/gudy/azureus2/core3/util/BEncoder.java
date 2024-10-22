/*
 * BEncoder.java
 *
 * Created on June 4, 2003, 10:17 PM
 */

package org.gudy.azureus2.core3.util;

import java.io.*;
import java.util.*;

/**
 * A set of utility methods to encode a Map into a bencoded array of byte.
 * integer are represented as Long, String as byte[], dictionnaries as Map, and list as List.
 *
 * @author  TdC_VgA
 */
public class BEncoder {          
    /** Creates a new instance of BEncoder */
    public BEncoder() {
    }
    
    public static byte[] encode(Map object) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BEncoder.encode(baos, object);
        return baos.toByteArray();
    }    
    
    private static void 
	encode(
		ByteArrayOutputStream baos, 
		Object object) 
    
    	throws IOException
	{
    	
        if ( object instanceof String || object instanceof Float){
        	
            String tempString = (object instanceof String) ? (String)object : String.valueOf((Float)object);

            
            baos.write((String.valueOf(tempString.getBytes(Constants.DEFAULT_ENCODING).length)).getBytes());
            
            baos.write(':');
            
            baos.write(tempString.getBytes(Constants.DEFAULT_ENCODING));
            
        }else if(object instanceof Map){
        	
            Map tempMap = (Map)object;
            
            SortedMap tempTree = null;
            
            	// unfortunately there are some occasions where we want to ensure that
            	// the 'key' of the map is not mangled by assuming its UTF-8 encodable.
            	// In particular the response from a tracker scrape request uses the
            	// torrent hash as the KEY. Hence the introduction of the type below
            	// to allow the constructor of the Map to indicate that the keys should
            	// be extracted using a BYTE_ENCODING 
            	
            boolean	byte_keys = object instanceof ByteEncodedKeyHashMap;
            
            //write the d            
            baos.write('d');
            
            //are we sorted?
            if ( tempMap instanceof TreeMap ){
            	
                tempTree = (TreeMap)tempMap;
                
            }else{
            	
                	//do map sorting here
            	
                tempTree = new TreeMap(tempMap);                
            }            
                   
            Iterator	it = tempTree.entrySet().iterator();
            
            while( it.hasNext()){
            	
            	Map.Entry	entry = (Map.Entry)it.next();
			
            	String key = (String)entry.getKey();
   			   		           	
   			   	Object value = entry.getValue();

   			   	if ( value != null ){
   			   		
	                if ( byte_keys ){
	                		   		
	   					try{
	  					
	   				 		BEncoder.encode(baos,key.getBytes( Constants.BYTE_ENCODING ));
	      				
	      					BEncoder.encode(baos, tempMap.get(key));
	      		
	    				}catch( UnsupportedEncodingException e ){
	                		
	    					throw( new IOException( "BEncoder: unsupport encoding: " + e.getMessage()));
	    				}
	
	                }else{                 

	                	BEncoder.encode(baos, key );	// Key goes in as UTF-8
	      				
	      				BEncoder.encode(baos, value);
    				}      
                }     
            }
            
            /*
            //create a list to hold the alpha ordered keys
            ArrayList keyList = new ArrayList();            
            
            //BUILD THE KEY LIST
            //I KNOW THIS IS NASTY, BUT SUN DIDN'T SEE FIT TO RETURN A NULL
            do{
                try{
                    //get the key
                    String tempKey = (String)tempTree.firstKey();          
                    //stuff it into the list
                    keyList.add(tempKey);                    
                    //get the rest of the tree
                    tempTree = tempTree.tailMap(tempKey+"\0");                    
                }catch(NoSuchElementException e){
                    break;
                }
            }while(true);
            
            	//encode all of the keys
            
            if ( byte_keys ){
            	
            	try{
            	
					for(int i = 0; i<keyList.size(); i++){
						
				   		String key = (String)keyList.get(i);
				   		Object value = tempMap.get(key);
				   		if (value != null) {
  				   			//encode the key
  				   		BEncoder.encode(baos,key.getBytes( Constants.BYTE_ENCODING ));
  				   			//encode the value
  				   		BEncoder.encode(baos, tempMap.get(key));
  				   	}
			   		}
            	}catch( UnsupportedEncodingException e ){
            		
            		throw( new IOException( "BEncoder: unsupport encoding: " + e.getMessage()));
            	}
            }else{                 
           
				for(int i = 0; i<keyList.size(); i++){
					Object key = keyList.get(i);
					Object value = tempMap.get(key);
					if (value != null) {
  					//encode the key
  					BEncoder.encode(baos, key );	// Key goes in as UTF-8
  					//encode the value
  					BEncoder.encode(baos, value);
  				}
				}      
            }          
            */
            
            
            baos.write('e');
            
            
        }else if(object instanceof List){
        	
            List tempList = (List)object;
            
            	//write out the l
            
            baos.write('l');                                   
            
            for(int i = 0; i<tempList.size(); i++){
                
                BEncoder.encode(baos, tempList.get(i));                            
            }   
            
            baos.write('e');                          
            
        }else if(object instanceof Long){
        	
            Long tempLong = (Long)object;         
            //write out the l       
               baos.write('i');
               baos.write(tempLong.toString().getBytes());
               baos.write('e');
         }else if(object instanceof Integer){
         	
			Integer tempInteger = (Integer)object;         
			//write out the l       
			baos.write('i');
			baos.write(tempInteger.toString().getBytes());
			baos.write('e');
			
       }else if(object instanceof byte[]){
       	
            byte[] tempByteArray = (byte[])object;
            baos.write((String.valueOf(tempByteArray.length)).getBytes());
            baos.write(':');
            baos.write(tempByteArray);
        }      
    }
    
    public static boolean
	mapsAreIdentical(
		Map	map1,
		Map	map2 )
	{
    	if ( map1 == null && map2 == null ){
    		
    		return( true );
    		
    	}else if ( map1 == null || map2 == null ){
    		
    		return( false );
    	}
    	
    	if ( map1.size() != map2.size()){
    		
    		return( false );
    	}
    	
    	try{
    		return(  Arrays.equals( encode(map1), encode(map2)));
    		
    	}catch( IOException e ){
    		
    		e.printStackTrace();
    		
    		return( false );
    	}
    }		
}
