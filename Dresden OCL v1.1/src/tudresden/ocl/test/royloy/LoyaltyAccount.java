package tudresden.ocl.test.royloy;

import java.util.Vector;

public class LoyaltyAccount extends RLObject
{
	
	public int points;
	
	// Associations
	public Membership membership;
	
	public Vector  transactions=new Vector();
	
	public LoyaltyAccount(final String description)
	{
		super(description);
	}
	
	// Operations
	public void earn(int i)
	{
	}
	
	public void burn(int i)
	{
	}
	
	public boolean isEmpty()
	{
		return false;
	}
	
	public boolean assertTrue()
	{
		return true;
	}
} /* end class LoyaltyAccount */

