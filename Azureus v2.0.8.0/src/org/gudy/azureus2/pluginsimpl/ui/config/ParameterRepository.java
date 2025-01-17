/*
 * File    : ParameterRepository.java
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
 
package org.gudy.azureus2.pluginsimpl.ui.config;
import java.util.HashMap;
import java.util.Set;

import org.gudy.azureus2.plugins.ui.config.*;
import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;

/**
 * @author epall
 *
 */
public class ParameterRepository
{
	private static ParameterRepository instance;
	private HashMap params;
	
	private ParameterRepository()
	{
		params = new HashMap();
	}
	
	public static synchronized ParameterRepository getInstance()
	{
		if(instance == null)
			instance = new ParameterRepository();
		return instance;
	}
	
	public void addPlugin(Parameter[] parameters, String displayName)
	{
		params.put(displayName, parameters);
    
    // set the defaults
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
    if (def == null)
      return;

    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      if (!(parameter instanceof GenericParameter))
        continue;
      String sKey = ((GenericParameter)parameter).getKey();

      if(parameter instanceof StringParameter) {
        def.addParameter(sKey,
                         ((StringParameter)parameter).getDefaultValue());
      } else if(parameter instanceof IntParameter) {
        def.addParameter(sKey,
                         ((IntParameter)parameter).getDefaultValue());
      } else if(parameter instanceof BooleanParameter) {
        def.addParameter(sKey,
                         ((BooleanParameter)parameter).getDefaultValue());
      } else if(parameter instanceof FileParameter) {
        def.addParameter(sKey,
                         ((FileParameter)parameter).getDefaultValue());
      } else if(parameter instanceof DirectoryParameter) {
        def.addParameter(sKey,
                         ((DirectoryParameter)parameter).getDefaultValue());
      } else if(parameter instanceof IntsParameter) {
        def.addParameter(sKey,
                         ((IntsParameter)parameter).getDefaultValue());
      } else if(parameter instanceof StringsParameter) {
        def.addParameter(sKey,
                         ((StringsParameter)parameter).getDefaultValue());
      } else if(parameter instanceof ColorParameter) {
        def.addParameter(sKey + ".red",
                         ((ColorParameter)parameter).getDefaultRed());
        def.addParameter(sKey + ".green",
                         ((ColorParameter)parameter).getDefaultGreen());
        def.addParameter(sKey + ".blue",
                         ((ColorParameter)parameter).getDefaultBlue());
      }
    }
	}	
	
	public String[] getNames()
	{
	  Set keys = params.keySet();
	  return (String[])(keys.toArray(new String[keys.size()]));
	}
	
	public Parameter[] getParameterBlock(String key)
	{	
		return (Parameter[])params.get(key);
	}
}
