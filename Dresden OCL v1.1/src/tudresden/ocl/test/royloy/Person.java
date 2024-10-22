package tudresden.ocl.test.royloy;

import java.util.HashSet;


/**
 * The Person of the Person-Company model.
 */
public class Person extends RLObject implements Comparable
{
	
	// Attributes
	/**
	 * @invariant : self.oclIsKindOf(Person)
	 * @invariant : age.oclIsTypeOf(Integer)
	 * @invariant : 1 . oclIsKindOf(Real)
	 * @invariant happy_computing:
	 *    let a=0 in
	 *    let b=a+1 in
	 *    let c=a + self.employers->size in
	 *    let d=b*2 in
	 *    a<b and self.employers->size<=c and d>0
	 *   @invariant nameUpperCase:
	 *      let firstLetter:String=name.substring(1,1) in
	 *      firstLetter = firstLetter.toUpper
	 */
	public String name;
	
	/**
	 * @invariant ageGreaterEqual0: age>=0
	 * @invariant age_greater_zero: employers.employees->forAll(age>0)
	 * @invariant age0to199: Set{ 0 .. 199 }->includes(age)
	 */
	public long age=18;
	
	/**
	 * @invariant husband:
	 *    if isMarried then
	 *      wife->isEmpty xor husband->isEmpty
	 *    else
	 *      wife->isEmpty and husband->isEmpty
	 *    endif
	 * @invariant manager_is_employee3:
	 *    managedCompanies->forAll(employees->includes(self))
	 */
	public boolean isMarried=false;
	
	public boolean isUnemployed=false;
	
	// Associations
	/**
	 * @element-type Company
	 */
	// tests comma separated attributes
	public HashSet managedCompanies=new HashSet(), employers=new HashSet();
	
	protected Person wife;
	
	protected Person husband;
	
	public Person(String name)
	{
		super(name);
		this.name=name;
	}
	
	// Operations
	public float incomeaftertax=1.0f;
	
	/**
	 * @invariant getIncomeAfterTax1: getIncomeAfterTax(0.3)>0
	 * @invariant getIncomeAfterTax2: getIncomeAfterTax(1)=1.0
	 */
	public float getIncomeAfterTax(float tax)
	{
		return incomeaftertax;
	}
	
	public void marry(Person wife)
	{
		this.wife=wife;
		isMarried=true;
		wife.husband=this;
		wife.isMarried=true;
	}
	
	public int compareTo(Object o)
	{
		int a=hashCode();
		int b=o.hashCode();
		if(a<b)
			return -1;
		else if(a>b)
			return 1;
		else
			return 0;
	}
	
	public boolean assertTrue()
	{
		return true;
	}
	
} /* end class Person */

