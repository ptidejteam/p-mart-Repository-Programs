/*
 * File : IpCheckerWizard.java Created : 10 nov. 2003 By : Olivier
 * 
 * Azureus - a Java Bittorrent client
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details ( see the LICENSE file ).
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.gudy.azureus2.ui.swt.ipchecker;

import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
import org.gudy.azureus2.ui.swt.wizard.Wizard;

/**
 * @author Olivier
 *  
 */
public class IpCheckerWizard extends Wizard {

  IpSetterCallBack callBack;
  
  ExternalIPCheckerService selectedService;
  String detectedIp;
  
  public IpCheckerWizard(Display display) {
    super(display, "ipCheckerWizard.title");
    IWizardPanel panel = new ChooseServicePanel(this,null);
    this.setFirstPanel(panel);
  }
  
  public void setIpSetterCallBack(IpSetterCallBack callBack) {
    this.callBack = callBack;
  }
}
