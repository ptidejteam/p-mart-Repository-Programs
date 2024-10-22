/*
 * Created on 29-Apr-2005
 * Created by Paul Gardner
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.util.bloom.impl;

import java.math.BigInteger;
import java.util.Random;

import com.aelitis.azureus.core.util.bloom.BloomFilter;

public class 
BloomFilterImpl
	implements BloomFilter
{
	private static final char[]	HEX_CHARS = "0123456789ABCDEF".toCharArray();
	
		// change the hash num and you gotta change the hash function below!!!!
	
	private static final int	HASH_NUM	= 5;
	
	private static final BigInteger	bi_zero		= new BigInteger("0");
	private static final BigInteger	bi_a2		= new BigInteger("2");
	private static final BigInteger	bi_a3		= new BigInteger("3");
	private static final BigInteger	bi_a4		= new BigInteger("5");
	
	private static final BigInteger	bi_b2		= new BigInteger("51");
	private static final BigInteger	bi_b3		= new BigInteger("145");
	private static final BigInteger	bi_b4		= new BigInteger("216");
	

	private int			max_entries;
	private BigInteger	bi_max_entries;
	private byte[]		map;
	
	public 
	BloomFilterImpl(
		int		_max_entries )
	{
		//bi_max_entries	= new BigInteger( ""+_max_entries ).nextProbablePrime();
				
		max_entries	= bi_max_entries.intValue();
		
			// 4 bits per entry
		
		map	= new byte[(max_entries+1)/2];
	}
	
	public void
	add(
		String		value )
	{
		add( value.getBytes());
	}
	
	public void
	remove(
		String		value )
	{
		remove( value.getBytes());
	}
	
	public boolean
	contains(
		String		value )
	{
		return( contains( value.getBytes()));
	}
	
	public void
	add(
		byte[]		value )
	{
		add( bytesToBigInteger( value ));
	}
	
	public void
	remove(
		byte[]		value )
	{
		remove( bytesToBigInteger( value ));
	}
	
	public boolean
	contains(
		byte[]		value )
	{
		return( contains( bytesToBigInteger( value )));
	}
	
	public void
	add(
		BigInteger		value )
	{
		for (int i=0;i<HASH_NUM;i++){
			
			int	index = getHash( i, value );
			
			byte	v = getValue( index );
			
			if ( v < 15 ){
				
				setValue( index, (byte)(v+1));
			}
		}
	}
	
	public void
	remove(
		BigInteger		value )
	{
		for (int i=0;i<HASH_NUM;i++){
			
			int	index = getHash( i, value );
			
			byte	v = getValue( index );
			
			if ( v > 0 ){
				
				setValue( index, (byte)(v-1));
			}
		}		
	}
	
	public boolean
	contains(
		BigInteger		value )
	{
		for (int i=0;i<HASH_NUM;i++){
			
			int	index = getHash( i, value );
			
			int	v = getValue( index );
				
			if ( v == 0 ){
				
				return( false );
			}
		}
		
		return( true );		
	}
	
	protected byte
	getValue(
		int		index )
	{
		byte	b = map[index/2];
				
		if ( index % 2 == 0 ){
			
			return((byte)( b&0x0f ));
		}else{
			
			return((byte)((b>>4)&0x0f));
		}
	}
	
	protected void
	setValue(
		int		index,
		byte	value )
	{
		byte	b = map[index/2];
				
		if ( index % 2 == 0 ){
			
			b = (byte)((b&0xf0) | value );
			
		}else{
			
			b = (byte)((b&0x0f) | (value<<4)&0xf0 );
		}
		
		// System.out.println( "setValue[" + index + "]:" + Integer.toHexString( map[index/2]&0xff) + "->" + Integer.toHexString( b&0xff ));
		
		map[index/2] = b;
	}
	
	protected int
	getHash(
		int			function,
		BigInteger	value )
	{
		BigInteger	res;
		
		switch( function ){
			case 0:
			{
					// x mod p
				
				res = value;
				
				break;
			}
			case 1:
			{
				 	// x^2 mod p
				
				res	= value.pow(2);
				
				break;
			}
			case 2:
			{
					// bx + a mod p
				
				res = value.multiply( bi_a2 ).add( bi_b2 );
				
				break;
			}
			case 3:
			{
					// cx + d mod p
				
				res = value.multiply( bi_a3 ).add( bi_b3 );
				
				break;

			}
			case 4:
			{
					// ex + f mod p
				
				res = value.multiply( bi_a4 ).add( bi_b4 );
				
				break;
			}
			default:
			{
				System.out.println( "**** BloomFilter hash function doesn't exist ****" );
				
				res = new BigInteger("0");
			}
		}
		
		int	r = 0;
		
		while(true){
			
			BigInteger	x = res.divide( bi_max_entries );
			
			if ( x.compareTo( bi_zero) == 0 ){
				
				r += res.mod( bi_max_entries ).intValue();
				
				break;
			}
			
			r += x.mod( bi_max_entries ).intValue();

			res = x;
		}
		
		
		// System.out.println( "hash[" + function + "] " + value + "->" + r );
		
		return( r % max_entries );
	}
	
	protected BigInteger
	bytesToBigInteger(
		byte[]		data )
	{
		StringBuffer	buffer = new StringBuffer(data.length*2);
		
		for (int i=0;i<data.length;i++){
			
			buffer.append( HEX_CHARS[(data[i]>>4)&0x0f] );
			
			buffer.append( HEX_CHARS[data[i]&0x0f] );
		}
				
		BigInteger	res		= new BigInteger( new String(buffer), 16 );	
		
		return( res );
	}
	
	public static void
	main(
		String[]	args )
	{
		Random	rand = new Random();
		
		for (int j=0;j<100;j++){
			
			long	start = System.currentTimeMillis();
			
			BloomFilter b = new BloomFilterImpl(10000);
			
			int	fp = 0;
			
			for (int i=0;i<1000;i++){
				
				String	key = "" + rand.nextInt();
				
				//byte[]	key = new byte[4];
				
				//rand.nextBytes( key );
				
				if ( i%2 == 0 ){
					
					b.add( key  );
				
					if ( !b.contains( key )){
						
						System.out.println( "false negative on add!!!!" );
					}
				}else{
					
					if ( b.contains( key )){
						
						fp++;
					}
				}
				
				/*
				if ( i%2 == 0 ){
					
					b.remove( key  );
				
					if ( b.contains( key )){
					
						System.out.println( "false positive" );
					}
				}
				*/
			}
			
			System.out.println( "" + (System.currentTimeMillis() - start + ", fp = " + fp ));
		}
	}
}
