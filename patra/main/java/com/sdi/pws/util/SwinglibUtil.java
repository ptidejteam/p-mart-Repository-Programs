package com.sdi.pws.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class SwinglibUtil
{
    private SwinglibUtil()
    {
    }

    public static void centerOnScreen(Component aCompo)
    {
        final Dimension lComponentSize = aCompo.getSize();
        centerOnScreen(aCompo, lComponentSize);
    }

    public static void centerOnScreen(Component aCompo, Dimension aCompoSize)
    {
        final Dimension lScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        aCompo.setLocation(lScreenSize.width / 2 - (aCompoSize.width / 2), lScreenSize.height / 2 - (aCompoSize.height / 2));
    }
    public static JFrame findTopFrame(Component aCompo)
    {
        if(aCompo == null) return null;
        else if(aCompo instanceof JFrame) return (JFrame) aCompo;
        else return findTopFrame(aCompo.getParent());
    }

    public static interface ComponentProcessor
    {
        public void process(Component aComponent);
    }

    public static class BackgroundColorChanger
    implements ComponentProcessor
    {
        private Color color;

        public BackgroundColorChanger(Color aColor)
        {
            color = aColor;
        }

        public void process(Component aComponent)
        {
            if(aComponent instanceof JPanel  )
            {
                JPanel lPanel = (JPanel) aComponent;
                lPanel.setBackground(color);
            }
            else if (aComponent instanceof JOptionPane)
            {
                JOptionPane lPane = (JOptionPane) aComponent;
                lPane.setOpaque(false);
            }
        }
    }

    public static void processNestedComponents(Component aComponent, ComponentProcessor aProcessor)
    {
        aProcessor.process(aComponent);
        if(aComponent instanceof Container)
        {
            Container lCont = (Container) aComponent;
            for(int i = 0; i < lCont.getComponentCount(); i++)
            {
                processNestedComponents(lCont.getComponent(i), aProcessor);
            }
        }
        
    }

    /**
     * Make a list of all URL's that can be found in a string.
     * @param aSource  A string, possibly containing URL's.
     * @return A List of URL's that could be scraped from the string.
     */
    public static java.util.List<String> extractUrl(String aSource)
    {
        final String lPrefix = "http://";
        final java.util.List<String> lResult = new ArrayList<String>();
        String lTodo = aSource;
        int lUrlPos = lTodo.indexOf(lPrefix);
        while (lUrlPos >= 0)
        {
            lUrlPos += lPrefix.length();
            StringBuilder lUrlBuf = new  StringBuilder();
            while((lUrlPos < lTodo.length()) && lTodo.charAt(lUrlPos) != '\n' && !Character.isSpaceChar(lTodo.charAt(lUrlPos))) lUrlBuf.append(lTodo.charAt(lUrlPos++));
            lResult.add(lUrlBuf.toString());

            if(lUrlPos < lTodo.length()) lTodo = lTodo.substring(lUrlPos);
            else lTodo = "";
            lUrlPos = lTodo.indexOf(lPrefix);
        }
        return lResult;
    }

    /**
     * Make a popu menu using lists of actions. Each action list will be separated by
     * a menu separator. Empty action lists, or null-valued action lists will be ignored as if
     * the were not there.
     * @param aActionLists
     * @return A popup menu.
     */
    public static JPopupMenu popupBuilder(Action[] ... aActionLists)
    {
        // Prepare the popup.
        final JPopupMenu lMenu = new JPopupMenu();

        // First we are going to filter out the empty lists.
        final java.util.List<Action[]> lActionList = new ArrayList<Action[]>();
        for(Action[] lList : aActionLists) if(lList != null && lList.length > 0) lActionList.add(lList);

        // Build the popup menu.
        for(int i = 0; i < lActionList.size(); i++)
        {
            for(Action lAction: lActionList.get(i)) lMenu.add(lAction);
            if( i < lActionList.size() - 1) lMenu.addSeparator();
        }
        return lMenu;
    }
}
