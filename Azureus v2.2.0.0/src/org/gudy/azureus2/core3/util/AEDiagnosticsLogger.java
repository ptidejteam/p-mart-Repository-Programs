/*
 * Created on 22-Sep-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
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

package org.gudy.azureus2.core3.util;

/**
 * @author parg
 *
 */
public class 
AEDiagnosticsLogger 
{
	protected String 		name;
	protected boolean		first_file	= true;
	
	protected
	AEDiagnosticsLogger(
		String		_name )
	{
		name		= _name;
	}
	
	protected String
	getName()
	{
		return( name );
	}
	
	protected boolean
	isFirstFile()
	{
		return( first_file );
	}
	
	protected void
	setFirstFile(
		boolean		b )
	{
		first_file	= b;
	}
	
	public void
	log(
		String		str )
	{
		AEDiagnostics.log( this, str );
	}
	
	public void
	log(
		Throwable	e )
	{
		AEDiagnostics.log( this, e );
	}
	
	public void
	logAndOut(
		String		str )
	{
		logAndOut( str, false );
	}
	
	public void
	logAndOut(
		String		str,
		boolean		stderr )
	{
		if ( stderr ){
			System.err.println( str );
		}else{
			System.out.println( str );
		}
		
		log( str );
	}
	
	public void
	logAndOut(
		Throwable 	e )
	{
		e.printStackTrace();
		
		log( e );
	}
}
