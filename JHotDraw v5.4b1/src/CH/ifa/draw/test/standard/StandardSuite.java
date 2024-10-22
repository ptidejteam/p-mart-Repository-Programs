package CH.ifa.draw.test.standard;



import junit.framework.TestSuite;
// JUnitDoclet begin import
// JUnitDoclet end import

/*
* Generated by JUnitDoclet, a tool provided by
* ObjectFab GmbH under LGPL.
* Please see www.junitdoclet.org, www.gnu.org
* and www.objectfab.de for informations about
* the tool, the licence and the authors.
*/


// JUnitDoclet begin javadoc_class
/**
* TestSuite StandardSuite
*/
// JUnitDoclet end javadoc_class
public class StandardSuite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // JUnitDoclet end class
  
  public static TestSuite suite() {
    
    TestSuite suite;
    
    suite = new TestSuite("CH.ifa.draw.test.standard");
    
    suite.addTestSuite(CH.ifa.draw.test.standard.PeripheralLocatorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SingleFigureEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.NullDrawingViewTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.NullToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.HandleAndEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.InsertIntoDrawingVisitorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.DeleteFromDrawingVisitorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SelectAllCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.FigureAndEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.HandleEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.StandardFigureSelectionTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.FastBufferedUpdateStrategyTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ToolButtonTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ToggleGridCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.StandardDrawingViewTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.StandardDrawingTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SimpleUpdateStrategyTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SelectionToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SelectAreaTrackerTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ReverseFigureEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.RelativeLocatorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.PasteCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.OffsetLocatorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.NullHandleTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.LocatorConnectorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.HandleTrackerTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.GridConstrainerTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.FigureEnumeratorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.FigureChangeEventMulticasterTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.DuplicateCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.DragTrackerTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.DeleteCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.CutCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.CreationToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.CopyCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ConnectionToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ConnectionHandleTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ChopBoxConnectorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ChangeConnectionStartHandleTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ChangeConnectionEndHandleTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.ChangeAttributeCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.BufferedUpdateStrategyTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.SendToBackCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.BringToFrontCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.LocatorHandleTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.BoxHandleKitTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.AlignCommandTest.class);
    suite.addTestSuite(CH.ifa.draw.test.standard.FigureChangeAdapterTest.class);
    
    
    
    // JUnitDoclet begin method suite()
    // JUnitDoclet end method suite()
    
    return suite;
  }
  
  /**
  * Method to execute the TestSuite from command line
  * using JUnit's textui.TestRunner .
  */
  public static void main(String[] args) {
    // JUnitDoclet begin method testsuite.main
    junit.textui.TestRunner.run(suite());
    // JUnitDoclet end method testsuite.main
  }
}
