package tudresden.ocl.test.royloy;

import java.util.HashSet;
import java.util.Vector;

public class LoyaltyProgram extends RLObject
{
	
	/**
	 * @element-type Customer
	 * @invariant customer_unique_name:
	 *    self.customer->forAll(c1, c2 | c1<>c2 implies c1.name<>c2.name)
	 * @invariant customer_unique_name2:
	 *    self.customer->isUnique(name)
	 */
	public Vector  customer=new Vector();
	
	public Vector  serviceLevel=new Vector();
	
	public Vector  membership=new Vector();
	
	public HashSet partners=new HashSet();
	
	public LoyaltyProgram(final String description)
	{
		super(description);
	}
	
	public void enroll(Customer c)
	{
	}
	
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class LoyaltyProgram */

