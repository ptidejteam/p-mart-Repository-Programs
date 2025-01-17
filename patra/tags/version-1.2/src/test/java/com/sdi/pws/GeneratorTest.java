/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws;

import com.sdi.pws.generator.GeneratorImpl;
import com.sdi.pws.generator.Generator;
import com.sdi.pws.gui.compo.generator.change.ChangeViewGenerator;
import com.sdi.pws.gui.compo.generator.view.JGeneratorProps;

import javax.swing.*;

public class GeneratorTest
{
    public static final void main(String[] aArgs)
    {
        ChangeViewGenerator lGen = new ChangeViewGenerator(new GeneratorImpl());
        JDialog lDialog = new JDialog();
        lDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        lDialog.setModal(true);
        JGeneratorProps lProps = new JGeneratorProps(lGen);
        lDialog.setContentPane(lProps);
        lDialog.pack();
        lDialog.show();

        for(int i = 0; i < 5; i++)
            System.out.println(lGen.generate());
    }
}
