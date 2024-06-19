package junit.samples;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A sample test case, testing <code>java.util.Vector</code>.
 *
 */
public class VectorTest extends TestCase {
	protected Vector fEmpty;
	protected Vector fFull;

	public VectorTest(String name) {
		super(name);
	}
	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	protected void setUp() {
		this.fEmpty= new Vector();
		this.fFull= new Vector();
		this.fFull.addElement(new Integer(1));
		this.fFull.addElement(new Integer(2));
		this.fFull.addElement(new Integer(3));
	}
	public static Test suite() {
		return new TestSuite(VectorTest.class);
	}
	public void testCapacity() {
		int size= this.fFull.size(); 
		for (int i= 0; i < 100; i++)
			this.fFull.addElement(new Integer(i));
		assertTrue(this.fFull.size() == 100+size);
	}
	public void testClone() {
		Vector clone= (Vector)this.fFull.clone(); 
		assertTrue(clone.size() == this.fFull.size());
		assertTrue(clone.contains(new Integer(1)));
	}
	public void testContains() {
		assertTrue(this.fFull.contains(new Integer(1)));  
		assertTrue(!this.fEmpty.contains(new Integer(1)));
	}
	public void testElementAt() {
		Integer i= (Integer)this.fFull.elementAt(0);
		assertTrue(i.intValue() == 1);

		try { 
			this.fFull.elementAt(this.fFull.size());
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		fail("Should raise an ArrayIndexOutOfBoundsException");
	}
	public void testRemoveAll() {
		this.fFull.removeAllElements();
		this.fEmpty.removeAllElements();
		assertTrue(this.fFull.isEmpty());
		assertTrue(this.fEmpty.isEmpty()); 
	}
	public void testRemoveElement() {
		this.fFull.removeElement(new Integer(3));
		assertTrue(!this.fFull.contains(new Integer(3)) ); 
	}
}