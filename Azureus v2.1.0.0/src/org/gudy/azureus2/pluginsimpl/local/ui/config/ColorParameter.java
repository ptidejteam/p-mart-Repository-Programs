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


/**
 * @author Olivier
 *
 */
public class ColorParameter extends GenericParameter
{
	private int defaultRed;
	private int defaultGreen;
	private int defaultBlue;
	
	public ColorParameter(String key, String label, int red,int green,int blue)
	{ 
		super(key, label);
    this.defaultRed = red;
    this.defaultGreen = green;
    this.defaultBlue = blue;
	}
	
	public int getDefaultRed()
	{
		return defaultRed;
	}
	
	public int getDefaultGreen()
	{
	  return defaultGreen;
	}
	
	public int getDefaultBlue()
	{
	  return defaultBlue;
	}

}
