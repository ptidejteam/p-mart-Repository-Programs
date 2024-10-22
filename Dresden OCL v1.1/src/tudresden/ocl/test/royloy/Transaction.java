package tudresden.ocl.test.royloy;

import tudresden.ocl.lib.Ocl;
import tudresden.ocl.lib.OclAnyImpl;
import tudresden.ocl.lib.OclBoolean;
import tudresden.ocl.lib.OclRoot;


public class Transaction extends RLObject
{
	
	// Attributes
	public int points;
	
	public Date date;
	
	// Associations
	
	public CustomerCard card;
	
	public Service service;
	
	public LoyaltyAccount loyaltyAccount;
	
	public Transaction(final String description)
	{
		super(description);
	}
	
	// Operations
	
	/**
	 * @invariant : self.program()=card.membership.program
	 */
	public LoyaltyProgram program()
	{
		LoyaltyProgram ret=loyaltyAccount.membership.program;
		
		// post: result=self.card.membership.program
		OclRoot result=Ocl.getFor(ret);
		OclAnyImpl self=(OclAnyImpl)Ocl.getFor(this);
		OclBoolean p1=result.isEqualTo(
		self.getFeature("card").getFeature("membership").getFeature("program")
		);
		
		if (p1.isUndefined() || !p1.isTrue())
		{
			System.out.println("LoyaltyProgram.program() postcondition violated: "+p1);
		}
		
		return ret;
	}
	
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class Transaction */

