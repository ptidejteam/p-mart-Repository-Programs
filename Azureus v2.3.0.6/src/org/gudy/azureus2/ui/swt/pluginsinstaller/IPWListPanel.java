/*
 * Created on 29 nov. 2004
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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
 * 
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.ui.swt.pluginsinstaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.gudy.azureus2.core3.html.HTMLUtils;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.AEThread;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.plugins.installer.StandardPlugin;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.Wizard;


/**
 * @author Olivier Chalouhi
 *
 */
public class IPWListPanel extends AbstractWizardPanel {

  Table pluginList;
  StyledText txtDescription;
  
  public 
  IPWListPanel(
	Wizard 					wizard, 
	IWizardPanel 			previous ) 
  {
	super(wizard, previous);
  }


  public void 
  show() 
  {
    wizard.setTitle(MessageText.getString("installPluginsWizard.list.title"));
    wizard.setErrorMessage("");
    
	Composite rootPanel = wizard.getPanel();
	GridLayout layout = new GridLayout();
	layout.numColumns = 1;
	rootPanel.setLayout(layout);

	Composite panel = new Composite(rootPanel, SWT.NULL);
	GridData gridData = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
	panel.setLayoutData(gridData);
	layout = new GridLayout();
	layout.numColumns = 1;
	panel.setLayout(layout);
	
	final Label lblStatus = new Label(panel,SWT.NULL);
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	lblStatus.setLayoutData(data);
	Messages.setLanguageText(lblStatus,"installPluginsWizard.list.loading");
	
	pluginList = new Table(panel,SWT.BORDER | SWT.V_SCROLL | SWT.CHECK | SWT.FULL_SELECTION | SWT.SINGLE); 
	pluginList.setHeaderVisible(true);
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.heightHint = 120;
	pluginList.setLayoutData(data);
	
	
	TableColumn tcName = new TableColumn(pluginList,SWT.LEFT);
	Messages.setLanguageText(tcName,"installPluginsWizard.list.name");
	tcName.setWidth(200);
	
	TableColumn tcVersion = new TableColumn(pluginList,SWT.LEFT);
	Messages.setLanguageText(tcVersion,"installPluginsWizard.list.version");
	tcVersion.setWidth(150);
		
	
	Label lblDescription = new Label(panel,SWT.NULL);
	Messages.setLanguageText(lblDescription,"installPluginsWizard.list.description");
	
	txtDescription = new StyledText(panel,SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
	txtDescription.setWordWrap(true);
	txtDescription.setEditable(false);
	
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.heightHint = 100;
	txtDescription.setLayoutData(data);

	AEThread listLoader = new AEThread("Plugin List Loader") {
	  public void runSupport() {
	    final StandardPlugin plugins[];
	    try {
	      plugins = ((InstallPluginWizard)wizard).getStandardPlugins();
	      
	      Arrays.sort( 
	      	plugins,
		  	new Comparator()
			{
	      		public int 
				compare(
					Object o1, 
					Object o2)
	      		{
	      			return(((StandardPlugin)o1).getName().compareTo(((StandardPlugin)o2).getName()));
	      		}
			});
			
	    } catch(final Exception e) {
	    	
	    	Debug.printStackTrace(e);
		    wizard.getDisplay().asyncExec(new AERunnable() {
			      public void runSupport() {
			      	txtDescription.setText( Debug.getNestedExceptionMessage(e));
			      }
		    });
		    
	    	return;
	    }
	    
	    wizard.getDisplay().asyncExec(new AERunnable() {
	      public void runSupport() {
	       
	        lblStatus.setText( ((InstallPluginWizard)wizard).getListTitleText());
	        
	        List	selected_plugins = ((InstallPluginWizard)wizard).getPluginList();

	        for(int i = 0 ; i < plugins.length ; i++) {
	          StandardPlugin plugin = plugins[i];
	          if(plugin.getAlreadyInstalledPlugin() == null) {
	            if(pluginList == null || pluginList.isDisposed())
	              return;
	            TableItem item = new TableItem(pluginList,SWT.NULL);
	            item.setData(plugin);
	            item.setText(0,plugin.getName());
	            boolean	selected = false;
	            for (int j=0;j<selected_plugins.size();j++){
	            	if (((StandardPlugin)selected_plugins.get(j)).getId() == plugin.getId()){
	            		selected = true;
	            	}
	            }
	            item.setChecked( selected );
	            item.setText(1,plugin.getVersion());
	          }
	        }
	        
	        	// if there's only one entry then we might as well pull it in (this is really to
	        	// support explicit install directions in the wizard as opposed to selection from
	        	// the SF list )
	        
	        if ( plugins.length == 1 && pluginList.getItemCount() > 0 ){
	        	
	        	pluginList.select(0);
	        	
	        	loadPluginDetails( pluginList.getItem(0));
	        }
	      }
	    });
	  }
	};
	
	listLoader.setDaemon(true);
	
	listLoader.start();
	
	
	pluginList.addListener(SWT.Selection,new Listener() {
	  public void handleEvent(Event e) {
	    if(pluginList.getSelectionCount() > 0) {
	    	loadPluginDetails( pluginList.getSelection()[0]);

	    }
	    updateList();
	  }
	});
  }
  
  	protected void
  	loadPluginDetails(
  		final TableItem	selected_item )
  	{
	      txtDescription.setText( MessageText.getString( "installPluginsWizard.details.loading"));
	   
	      final StandardPlugin plugin = (StandardPlugin) selected_item.getData();
	      
	      
	      AEThread detailsLoader = new AEThread("Detail Loader") {
	        public void runSupport() {	         
	         final String description = HTMLUtils.convertListToString(HTMLUtils.convertHTMLToText(plugin.getDescription(),""));
	         wizard.getDisplay().asyncExec(new AERunnable() {
			      public void runSupport() {
			        if(pluginList == null || pluginList.isDisposed() || pluginList.getSelectionCount() ==0)
			          return;
			        if(pluginList.getSelection()[0] != selected_item)
			          return;
			      	if(txtDescription == null || txtDescription.isDisposed())
			      	  return;			      	
			        txtDescription.setText(description);
			      }
		      	});
	        }
	      };
	      
	      detailsLoader.setDaemon(true);
	      detailsLoader.start();
  	}
  
	public boolean 
	isNextEnabled() 
	{
		return(((InstallPluginWizard)wizard).getPluginList().size() > 0 );
	}
	
	public boolean 
	isFinishEnabled() 
	{
	   return false ;
	}
	
  public IWizardPanel getNextPanel() {
    return new IPWInstallModePanel(wizard,this);
  }
	
  public void updateList() {
    ArrayList list = new ArrayList();
    TableItem[] items = pluginList.getItems();
    for(int i = 0 ; i < items.length ; i++) {
      if(items[i].getChecked()){
        list.add(items[i].getData());
      }
    }
    ((InstallPluginWizard)wizard).setPluginList(list);
    ((InstallPluginWizard)wizard).setNextEnabled( isNextEnabled() );
    
  }
}
