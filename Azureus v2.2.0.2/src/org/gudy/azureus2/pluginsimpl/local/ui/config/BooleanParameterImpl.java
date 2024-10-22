/*
 * File    : GenericParameter.java
 * Created : Nov 21, 2003
 * By      : epall
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package org.gudy.azureus2.pluginsimpl.local.ui.config;



import org.gudy.azureus2.plugins.PluginConfig;
import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
import org.gudy.azureus2.core3.config.COConfigurationManager;

/**
 * @author Olivier
 *
 */
public class 
BooleanParameterImpl 
	extends 	ParameterImpl 
	implements 	BooleanParameter
{
	private boolean defaultValue;
	
	public 
	BooleanParameterImpl(
		PluginConfig	config,
		String 			key, 
		String 			label, 
		boolean 		defaultValue)
	{ 
		super( config, key, label);
 
		COConfigurationManager.setBooleanDefault( getKey(), defaultValue );
		
		this.defaultValue = defaultValue;
 
	}
	
	public boolean getDefaultValue()
	{
		return defaultValue;
	}

	public boolean
	getValue()
	{
		return( config.getBooleanParameter( getKey(), getDefaultValue()));
	}
}
