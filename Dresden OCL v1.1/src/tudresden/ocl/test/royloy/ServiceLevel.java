package tudresden.ocl.test.royloy;

import java.util.Vector;

public class ServiceLevel extends RLObject
{
	
	// Attributes
	public String name;
	
	// Associations
	
	/**
	 * @invariant :
	 *    loyaltyProgram.partners->includes(service.programPartner)
	 */
	public LoyaltyProgram loyaltyProgram;
	
	/**
	 * @element-type Membership
	 */
	public Vector  membership=new Vector();
	
	public Service service;
	
	public ServiceLevel(final String description)
	{
		super(description);
	}
	
	// Operations
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class ServiceLevel */

