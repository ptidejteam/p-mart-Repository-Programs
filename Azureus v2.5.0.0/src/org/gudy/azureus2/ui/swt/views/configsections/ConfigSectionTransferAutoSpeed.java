/*
 * Copyright (C) 2004, 2005, 2006 Aelitis SAS, All rights Reserved
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
 * AELITIS, SAS au capital de 46,603.30 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.components.LinkLabel;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;



public class ConfigSectionTransferAutoSpeed implements UISWTConfigSection {

	private final String CFG_PREFIX = "ConfigView.section.transfer.autospeed.";
	
	public String configSectionGetParentSection() {
		return ConfigSection.SECTION_TRANSFER;
	}

	public String configSectionGetName() {
		return "transfer.autospeed";
	}

	public void configSectionSave() {
	}

	public void configSectionDelete() {
	}

	public Composite configSectionCreate(final Composite parent) {
		GridData gridData;

		Composite cSection = new Composite(parent, SWT.NULL);

		gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		cSection.setLayoutData(gridData);
		GridLayout advanced_layout = new GridLayout();
		advanced_layout.numColumns = 2;
		cSection.setLayout(advanced_layout);

		int userMode = COConfigurationManager.getIntParameter("User Mode");

		
		Label linfo = new Label(cSection, SWT.NULL);
		Messages.setLanguageText( linfo, CFG_PREFIX + "info" );
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		linfo.setLayoutData(gridData);
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		LinkLabel linkLabel = new LinkLabel(cSection, gridData, "ConfigView.label.please.visit.here",
				"http://azureus.aelitis.com/wiki/index.php/Auto_Speed");

		
		String[]	units = { DisplayFormatters.getRateUnit( DisplayFormatters.UNIT_KB )};

			// min up
		
		Label llmux = new Label(cSection, SWT.NULL);
		Messages.setLanguageText( llmux, CFG_PREFIX + "minupload", units );
		IntParameter min_upload = new IntParameter( cSection, "AutoSpeed Min Upload KBs", false );
		gridData = new GridData();
		gridData.widthHint = 40;
		min_upload.setLayoutData(gridData);
		
			// max up
		
		Label llmdx = new Label(cSection, SWT.NULL);
		Messages.setLanguageText( llmdx, CFG_PREFIX + "maxupload", units );
		IntParameter max_upload = new IntParameter( cSection, "AutoSpeed Max Upload KBs", false );
		gridData = new GridData();
		gridData.widthHint = 40;
		max_upload.setLayoutData(gridData);
		
		BooleanParameter enable_au = new BooleanParameter(
				cSection, "Auto Upload Speed Enabled", false,
				CFG_PREFIX + "enableauto" );
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		enable_au.setLayoutData(gridData);

		
		BooleanParameter enable_au_seeding = new BooleanParameter(
				cSection, "Auto Upload Speed Seeding Enabled", false,
				CFG_PREFIX + "enableautoseeding" );
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		enable_au_seeding.setLayoutData(gridData);

		enable_au.setAdditionalActionPerformer(
	    		new ChangeSelectionActionPerformer( enable_au_seeding.getControls(), true ));
		
		if ( userMode > 0 ){
			
			BooleanParameter enable_down_adj = new BooleanParameter(
					cSection, "AutoSpeed Download Adj Enable", false,
					CFG_PREFIX + "enabledownadj" );
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			enable_down_adj.setLayoutData(gridData);

			
			Label label = new Label(cSection, SWT.NULL);
			Messages.setLanguageText( label, CFG_PREFIX + "downadjratio" );
			
			FloatParameter down_adj = new FloatParameter( cSection, "AutoSpeed Download Adj Ratio", 0, Float.MAX_VALUE, false, 2  );
			gridData = new GridData();
			gridData.widthHint = 40;
			down_adj.setLayoutData(gridData);
			

			enable_down_adj.setAdditionalActionPerformer(
		    		new ChangeSelectionActionPerformer( new Control[]{ down_adj.getControl()}));
		}
		
		if ( userMode > 1 ){
			
				// max inc
			
			Label label = new Label(cSection, SWT.NULL);
			Messages.setLanguageText( label, CFG_PREFIX + "maxinc", units );
			
			final IntParameter max_increase = new IntParameter( cSection, "AutoSpeed Max Increment KBs", false );
			gridData = new GridData();
			gridData.widthHint = 40;
			max_increase.setLayoutData(gridData);
			
				// max dec
			
			label = new Label(cSection, SWT.NULL);
			Messages.setLanguageText( label, CFG_PREFIX + "maxdec", units );
			
			final IntParameter max_decrease = new IntParameter( cSection, "AutoSpeed Max Decrement KBs", false );
			gridData = new GridData();
			gridData.widthHint = 40;
			max_decrease.setLayoutData(gridData);
			

				// choking ping
			
			label = new Label(cSection, SWT.NULL);
			Messages.setLanguageText( label, CFG_PREFIX + "chokeping" );

			final IntParameter choke_ping = new IntParameter( cSection, "AutoSpeed Choking Ping Millis", false );
			gridData = new GridData();
			gridData.widthHint = 40;
			choke_ping.setLayoutData(gridData);
			
				// latency
			
			label = new Label(cSection, SWT.NULL);
			Messages.setLanguageText( label, CFG_PREFIX + "latencyfactor" );

			final IntParameter latency_factor = new IntParameter( cSection, "AutoSpeed Latency Factor", 1, Integer.MAX_VALUE, false, false );
			gridData = new GridData();
			gridData.widthHint = 40;
			latency_factor.setLayoutData(gridData);

		    Label reset_label = new Label(cSection, SWT.NULL );
		    Messages.setLanguageText(reset_label, CFG_PREFIX + "reset");

		    Button reset_button = new Button(cSection, SWT.PUSH);

		    Messages.setLanguageText(reset_button, CFG_PREFIX + "reset.button" );

		    reset_button.addListener(SWT.Selection, 
		    		new Listener() 
					{
				        public void 
						handleEvent(Event event) 
				        {
				        	max_increase.resetToDefault();
				        	max_decrease.resetToDefault();
				        	choke_ping.resetToDefault();
				        	latency_factor.resetToDefault();
				        }
				    });
		    
			BooleanParameter debug_au = new BooleanParameter(
					cSection, "Auto Upload Speed Debug Enabled", false,
					CFG_PREFIX + "enabledebug" );
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			debug_au.setLayoutData(gridData);			
		}

		return cSection;

	}

}
