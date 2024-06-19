package com.sdi.pws.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class FilteredCellRenderer
implements TableCellRenderer
{
    private String searchString;

    private static class MyLabel
    extends JPanel
    {
        private String text = "";
        private boolean selected = false;
        private String searchString = null;

        public void setText(String aText)
        {
            text = aText;
        }

        public void setSelected(boolean aSelected)
        {
            selected = aSelected;
        }

        public void setSearchString(String aSearchString)
        {
            searchString = aSearchString;
        }

        public void paint(Graphics g)
        {

            final Graphics2D g2 = (Graphics2D) g;

            final Dimension d = getSize();
            final FontMetrics fm = g2.getFontMetrics();
            int lHeight = fm.getHeight();
            final Font lNormal = g2.getFont();
            final Font lBold = lNormal.deriveFont(Font.BOLD);

            // First we paint the background of the cell.
            if(selected) g2.setColor(Color.lightGray);
            else g2.setColor(Color.white);
            g2.fillRect(0,0, getWidth(), getHeight());

            // We only attempt to paint the text if there is
            // some text to pain.
            if(text != null && text.length() > 0)
            {
                // Create the standard text, with standard annotations.
                final AttributedString as = new AttributedString(text);
                as.addAttribute(TextAttribute.FOREGROUND, Color.black);

                // Now colorize all the pattern occurences.
                // We attribute the string.
                if(searchString != null && searchString.length() > 0)
                {
                    final String lLowerText = text.toLowerCase();
                    final String lLowerSearch = searchString.toLowerCase();
                    final int lLen = lLowerSearch.length();

                    int lPos = lLowerText.indexOf(lLowerSearch);
                    while(lPos >= 0)
                    {
                        as.addAttribute(TextAttribute.FOREGROUND, Color.orange, lPos, lPos + lLen);
                        as.addAttribute(TextAttribute.FONT, lBold, lPos, lPos + lLen);
                        lPos += lLen;
                        lPos = lLowerText.indexOf(lLowerSearch, lPos);
                    }
                }
                // Draw the string.
                g2.drawString(as.getIterator(), 2, lHeight + (int) ((d.getHeight() -lHeight)/ 2) -fm.getMaxDescent() );
            }
        }
    }

    private static MyLabel lLabel = new MyLabel();

    public Component getTableCellRendererComponent(JTable aJTable, Object o, boolean b, boolean b1, int i, int i1)
    {
        lLabel.setText(o.toString());
        lLabel.setSelected(b);
        lLabel.setSearchString(searchString);
        return lLabel;
        //return super.getTableCellRendererComponent(aJTable, toHtml(o.toString(), searchString), b, b1, i, i1);
    }


    public void setSearchString(String aSearchString)
    {
        searchString = aSearchString;
    }
}
