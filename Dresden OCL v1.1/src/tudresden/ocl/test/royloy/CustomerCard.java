package tudresden.ocl.test.royloy;

import java.util.Vector;

public class CustomerCard extends RLObject
{
	
	// Attributes
	
	public boolean valid;
	
	public Date validFrom;
	
	public Date validThru;
	
	public int color;
	
	public static int COLOR_GOLD;
	
	public static int COLOR_SILVER;
	
	public String printedName;
	
	// Associations
	
	public Customer owner;
	
	public Membership membership;
	
	public Vector  transactions=new Vector();
	
	public CustomerCard(final String description)
	{
		super(description);
	}
	
	// Operations
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class CustomerCard */

