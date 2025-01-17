/*
 * File    : ConfigSection*.java
 * Created : 11 mar. 2004
 * By      : TuxPaper
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

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.ConfigSectionSWT;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.Utils;

public class ConfigSectionFilePerformance implements ConfigSectionSWT {
  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_FILES;
  }

  /* Name of section will be pulled from 
   * ConfigView.section.<i>configSectionGetName()</i>
   */
	public String configSectionGetName() {
		return "file.perf";
	}

  public void configSectionSave() {
  }

  public void configSectionDelete() {
  }
  

  public Composite configSectionCreate(final Composite parent) {
    GridData gridData;
    GridLayout layout;
    Label label;

    Composite cSection = new Composite(parent, SWT.NULL);
    cSection.addControlListener(new Utils.LabelWrapControlListener());  
    layout = new GridLayout();
    layout.numColumns = 3;
    cSection.setLayout(layout);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 2;
    cSection.setLayoutData(gridData);

    label = new Label(cSection, SWT.WRAP);
    Messages.setLanguageText(label, "ConfigView.section.file.perf.explain");
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    label.setLayoutData( gridData );
     
    // diskmanager.friendly.hashchecking
    final BooleanParameter friendly_hashchecking = new BooleanParameter(cSection, "diskmanager.friendly.hashchecking", "ConfigView.section.file.friendly.hashchecking");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    friendly_hashchecking.setLayoutData(gridData);
    
    
    
    // Max Open Files
    
    label = new Label(cSection, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ConfigView.section.file.max_open_files");
    IntParameter file_max_open = new IntParameter(cSection, "File Max Open");
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.widthHint = 30;
    file_max_open.setLayoutData( gridData );
    label = new Label(cSection, SWT.WRAP);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ConfigView.section.file.max_open_files.explain");
    
    	// write block limit
    
    label = new Label(cSection, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    label.setLayoutData(gridData);
    String label_text = 
    	MessageText.getString( 
    		"ConfigView.section.file.write_block_limit", 
    		new String[]{ DisplayFormatters.formatByteCountToKiBEtc( DiskManager.BLOCK_SIZE )});
    label.setText(label_text);
    IntParameter write_block_limit = new IntParameter(cSection, "DiskManager Write Queue Block Limit", 0);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.widthHint = 30;
    write_block_limit.setLayoutData( gridData );
    label = new Label(cSection, SWT.WRAP);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ConfigView.section.file.write_block_limit.explain");
         
    	// check piece limit
    
    label = new Label(cSection, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ConfigView.section.file.check_piece_limit");
    IntParameter check_piece_limit = new IntParameter(cSection, "DiskManager Check Queue Piece Limit", 0);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.widthHint = 30;
    check_piece_limit.setLayoutData( gridData );
    label = new Label(cSection, SWT.WRAP);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ConfigView.section.file.check_piece_limit.explain");

    
    // diskmanager.perf.cache.enable
    
    final BooleanParameter disk_cache = new BooleanParameter(cSection, "diskmanager.perf.cache.enable", "ConfigView.section.file.perf.cache.enable");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    disk_cache.setLayoutData(gridData);
    
   	// diskmanager.perf.cache.size
    
    long max_mem_bytes 	= Runtime.getRuntime().maxMemory();
    long mb_1			= 1*1024*1024;
    long mb_32			= 32*mb_1;
    
    Label cache_size_label = new Label(cSection, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    cache_size_label.setLayoutData(gridData);
    Messages.setLanguageText(cache_size_label, "ConfigView.section.file.perf.cache.size", new String[]{ DisplayFormatters.getUnit(DisplayFormatters.UNIT_MB)});
    IntParameter cache_size = new IntParameter(cSection, "diskmanager.perf.cache.size" );
    cache_size.setAllowZero(false);
    cache_size.setMinimumValue(1);
    cache_size.setMaximumValue(COConfigurationManager.CONFIG_CACHE_SIZE_MAX_MB );
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.widthHint = 30;
    cache_size.setLayoutData( gridData );
    
     
    Label cache_explain_label = new Label(cSection, SWT.WRAP);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
    cache_explain_label.setLayoutData(gridData);
    Messages.setLanguageText(
    		cache_explain_label, 
			"ConfigView.section.file.perf.cache.size.explain",
			new String[]{ 
    			DisplayFormatters.formatByteCountToKiBEtc(mb_32),
    			DisplayFormatters.formatByteCountToKiBEtc(max_mem_bytes),
				Constants.AELITIS_WEB_SITE + "wiki/"
			});
    
    // don't cache smaller than
    
    Label cnst_label = new Label(cSection, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    cnst_label.setLayoutData(gridData);
    Messages.setLanguageText(cnst_label, "ConfigView.section.file.perf.cache.notsmallerthan", new String[]{ DisplayFormatters.getUnit(DisplayFormatters.UNIT_KB)});
    IntParameter cache_not_smaller_than= new IntParameter(cSection, "diskmanager.perf.cache.notsmallerthan" );
    cache_not_smaller_than.setAllowZero(false);
    cache_not_smaller_than.setMinimumValue(0);
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.widthHint = 30;
    cache_not_smaller_than.setLayoutData( gridData );
    
 
    // diskmanager.perf.cache.enable.read
    
    final BooleanParameter disk_cache_read = new BooleanParameter(cSection, "diskmanager.perf.cache.enable.read", "ConfigView.section.file.perf.cache.enable.read");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    disk_cache_read.setLayoutData(gridData);
 
    // diskmanager.perf.cache.enable.write
    
    final BooleanParameter disk_cache_write = new BooleanParameter(cSection, "diskmanager.perf.cache.enable.write", "ConfigView.section.file.perf.cache.enable.write");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    disk_cache_write.setLayoutData(gridData);
    
     // diskmanager.perf.cache.trace
    
    final BooleanParameter disk_cache_trace = new BooleanParameter(cSection, "diskmanager.perf.cache.trace", "ConfigView.section.file.perf.cache.trace");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    disk_cache_trace.setLayoutData(gridData);
     
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( cache_size.getControls() ));
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( disk_cache_trace.getControls() ));
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( disk_cache_read.getControls() ));
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( disk_cache_write.getControls() ));
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( new Control[]{ cache_size_label, cache_explain_label, cnst_label }));
    disk_cache.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( cache_not_smaller_than.getControls() ));
    
    
    return cSection;
  }
}
