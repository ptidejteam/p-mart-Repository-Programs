/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

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

package com.sdi.pws.gui;

import java.awt.*;

public class CyclicFocusPolicy
extends FocusTraversalPolicy
{
    private Component[] compos;

    public CyclicFocusPolicy(Component[] aComponents)
    {
        compos = aComponents;
    }

    public Component getDefaultComponent(Container focusCycleRoot)
    {
        return getFirstComponent(focusCycleRoot);
    }

    public Component getFirstComponent(Container focusCycleRoot)
    {
        if(compos.length > 0) return compos[0];
        else return null;
    }

    public Component getLastComponent(Container focusCycleRoot)
    {
        if(compos.length > 0) return compos[compos.length - 1];
        else return null;
    }

    public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
    {
        for(int i = 0; i < compos.length; i++)
        {
            if(compos[i] == aComponent)
            {
                if(i == (compos.length - 1)) return getFirstComponent(focusCycleRoot);
                else return compos[i+1];
            }
        }
        return null;
    }

    public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
    {
        for(int i = compos.length; i >= 0; i--)
        {
            if(compos[i] == aComponent)
            {
                if(i == 0) return getLastComponent(focusCycleRoot);
                else return compos[i-1];
            }
        }
        return null;
    }
}