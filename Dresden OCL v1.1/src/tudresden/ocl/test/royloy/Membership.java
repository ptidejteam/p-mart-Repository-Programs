package tudresden.ocl.test.royloy;


public class Membership extends RLObject
{
	
	// Attributes
	
	// Associations
	/**
	 * @invariant actualLevel:
	 *    program.serviceLevel->includes(actualLevel)
	 */
	public ServiceLevel actualLevel;
	
	public CustomerCard card;
	
	/**
	 * @invariant :
	 *    loyaltyAccount.points>=0 or loyaltyAccount->isEmpty
	 */
	public LoyaltyAccount loyaltyAccount;
	
	public LoyaltyProgram program;
	
	/**
	 * @invariant membership_back:
	 *    customer.cards.membership->includes(self)
	 */
	public Customer customer;
	
	public Membership(final String description)
	{
		super(description);
	}
	
	// Operations
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class Membership */

