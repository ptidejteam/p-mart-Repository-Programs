package pcgen.gui;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import pcgen.core.Globals;
import pcgen.util.SkinLFResourceChecker;

public class UIFactory 
{
        private static String lafData[][];
        
        public final static int NAME = 0;
        public final static int CLASSNAME = 1;
        public final static int TOOLTIP = 2;

        private static int systemIndex = 0;
        private static int crossPlatformIndex = 1;

        static
        {
                UIManager.LookAndFeelInfo lafInfo[] = UIManager.getInstalledLookAndFeels();
                lafData = new String[lafInfo.length][3];

                lafData[ 0 ][ NAME ] = "System";
                lafData[ 0 ][ CLASSNAME ] = UIManager.getSystemLookAndFeelClassName();
                lafData[ 0 ][ TOOLTIP ] = "Sets the look to that of the System you are using";
                int j = 1;
                if ( !lafData[ 0 ][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()) ) {
                        lafData[ 1 ][ NAME ] = "Cross Platform";
                        lafData[ 1 ][ CLASSNAME ] = UIManager.getCrossPlatformLookAndFeelClassName();
                        lafData[ 1 ][ TOOLTIP ] = "Sets the look to that of Java's cross platform look";
                        j++;
                }
                
                for ( int  i = 0; i < lafInfo.length && j < lafData.length; i++ ) {
                        lafData[ j ][ CLASSNAME ] = lafInfo[ i ].getClassName();
                        if ( !lafData[ j ][ CLASSNAME ].equals( UIManager.getSystemLookAndFeelClassName() ) &&
                             !lafData[ j ][ CLASSNAME ].equals( UIManager.getCrossPlatformLookAndFeelClassName() ) ) {
                                lafData[ j ][ NAME ] = lafInfo[ i ].getName();
                                lafData[ j ][ TOOLTIP ] = "Sets the look to " + lafData[ j ][ NAME ] + " look";
                                j++;
                        }                        
                }
        }
        
        public static void initLookAndFeel() 
        {
                if ( Globals.getLooknFeel() < lafData.length ) {
                        setLookAndFeel( new Integer( Globals.getLooknFeel() ) );
                } else if ( Globals.getLooknFeel() == lafData.length ) {
				try {
                                       //to get this case you should have already had skinlf.jar installed...
                                       if (SkinLFResourceChecker.getMissingResourceCount() == 0)
                                       {
                                               SkinManager.applySkin();
                                       //but just to be safe...
                                       } else {
                                               System.err.println(SkinLFResourceChecker.getMissingResourceMessage());
                                               setLookAndFeel( lafData[crossPlatformIndex][CLASSNAME] );
                                       }
				} catch (Exception e) {
                                        Globals.setLooknFeel( 0 );
                                        setLookAndFeel( lafData[crossPlatformIndex][CLASSNAME] );
				}
                } else {
                        Globals.setLooknFeel( 0 );
                        setLookAndFeel( lafData[crossPlatformIndex][CLASSNAME] );
                }
        }
        
        public static int indexOfSystemLnF() 
        {
                return systemIndex;
        }

        public static int indexOfCrossPlatformLnF() 
        {
                return crossPlatformIndex;
        }
        
	public static int getLnFCount()
	{
                return lafData.length;
        }
        
	public static String getLnFName( int index )
	{
                return lafData[ index ][ NAME ];
        }

	public static String getLnFTooltip( int index )
	{
                return lafData[ index ][ TOOLTIP ];
        }

	public static void setLookAndFeel(Object looknfeel )
	{
		try
		{
                        if (looknfeel instanceof String)
                                UIManager.setLookAndFeel((String)looknfeel);                        
                        else if (looknfeel instanceof javax.swing.LookAndFeel)
                                UIManager.setLookAndFeel((javax.swing.LookAndFeel)looknfeel);                        
                        else if (looknfeel instanceof Integer)
                                UIManager.setLookAndFeel(lafData[((Integer)looknfeel).intValue()][ CLASSNAME ]);
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			e.printStackTrace();
		}
                SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
	}

	public static void setLookAndFeel(int looknfeel) 
        {
                setLookAndFeel( new Integer( looknfeel ) );
        }        

}
