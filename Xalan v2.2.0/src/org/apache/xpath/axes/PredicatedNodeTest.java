package org.apache.xpath.axes;

import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.SubContextList;

import org.apache.xml.utils.PrefixResolver;

//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeFilter;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;

public abstract class PredicatedNodeTest extends NodeTest implements SubContextList
{

  /**
   * Construct an AxesWalker using a LocPathIterator.
   *
   * @param locPathIterator non-null reference to the parent iterator.
   */
  PredicatedNodeTest(LocPathIterator locPathIterator)
  {
    m_lpi = locPathIterator;
  }
  
  /**
   * Construct an AxesWalker.  The location path iterator will have to be set
   * before use.
   */
  PredicatedNodeTest()
  {
  }
  
  /**
   * Read the object from a serialization stream.
   *
   * @param stream Input stream to read from
   *
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   */
  private void readObject(java.io.ObjectInputStream stream)
          throws java.io.IOException, javax.xml.transform.TransformerException
  {
    try
    {
      stream.defaultReadObject();
      m_predicateIndex = -1;
      resetProximityPositions();
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new javax.xml.transform.TransformerException(cnfe);
    }
  }
  
  /**
   * Get a cloned PrdicatedNodeTest.
   *
   * @return A new PredicatedNodeTest that can be used without mutating this one.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {
    // Do not access the location path itterator during this operation!
    
    PredicatedNodeTest clone = (PredicatedNodeTest) super.clone();

    if ((null != this.m_proximityPositions)
            && (this.m_proximityPositions == clone.m_proximityPositions))
    {
      clone.m_proximityPositions = new int[this.m_proximityPositions.length];

      System.arraycopy(this.m_proximityPositions, 0,
                       clone.m_proximityPositions, 0,
                       this.m_proximityPositions.length);
    }
    
    if(clone.m_lpi == this)
      clone.m_lpi = (LocPathIterator)clone;

    return clone;
  }
  
  // Only for clones for findLastPos.  See bug4638.
  protected int m_predCount = -1;

  /**
   * Get the number of predicates that this walker has.
   *
   * @return the number of predicates that this walker has.
   */
  public int getPredicateCount()
  {
    if(-1 == m_predCount)
      return (null == m_predicates) ? 0 : m_predicates.length;
    else
      return m_predCount;
  }

  /**
   * Set the number of predicates that this walker has.  This does more 
   * that one would think, as it creates a new predicate array of the 
   * size of the count argument, and copies count predicates into the new 
   * one from the old, and then reassigns the predicates value.  All this 
   * to keep from having to have a predicate count value.
   *
   * @param count The number of predicates, which must be equal or less 
   *               than the existing count.
   */
  public void setPredicateCount(int count)
  {
    if(count > 0)
    {
      Expression[] newPredicates = new Expression[count];
      for (int i = 0; i < count; i++) 
      {
        newPredicates[i] = m_predicates[i];
      }
      m_predicates = newPredicates;
    }
    else
      m_predicates = null;
    
  }

  /**
   * Init predicate info.
   *
   * @param compiler The Compiler object that has information about this 
   *                 walker in the op map.
   * @param opPos The op code position of this location step.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void initPredicateInfo(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {

    int pos = compiler.getFirstPredicateOpPos(opPos);

    if(pos > 0)
      m_predicates = compiler.getCompiledPredicates(pos);
  }

  /**
   * Get a predicate expression at the given index.
   *
   *
   * @param index Index of the predicate.
   *
   * @return A predicate expression.
   */
  Expression getPredicate(int index)
  {
    return m_predicates[index];
  }
  
  /**
   * Get the current sub-context position.
   *
   * @return The node position of this walker in the sub-context node list.
   */
  public int getProximityPosition()
  {

    // System.out.println("getProximityPosition - m_predicateIndex: "+m_predicateIndex);
    return getProximityPosition(m_predicateIndex);
  }

  /**
   * Get the current sub-context position.
   *
   * @param xctxt The XPath runtime context.
   *
   * @return The node position of this walker in the sub-context node list.
   */
  public int getProximityPosition(XPathContext xctxt)
  {
    return getProximityPosition();
  }
  
  /**
   * Get the index of the last node that can be itterated to.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
   */
  public abstract int getLastPos(XPathContext xctxt);

  /**
   * Get the current sub-context position.
   *
   * @param predicateIndex The index of the predicate where the proximity 
   *                       should be taken from.
   *
   * @return The node position of this walker in the sub-context node list.
   */
  protected int getProximityPosition(int predicateIndex)
  {
    return (predicateIndex >= 0) ? m_proximityPositions[predicateIndex] : 0;
  }

  /**
   * Reset the proximity positions counts.
   */
  public void resetProximityPositions()
  {
    int nPredicates = getPredicateCount();
    if (nPredicates > 0)
    {
      if (null == m_proximityPositions)
        m_proximityPositions = new int[nPredicates];

      for (int i = 0; i < nPredicates; i++)
      {
        try
        {
          initProximityPosition(i);
        }
        catch(Exception e)
        {
          // TODO: Fix this...
          throw new org.apache.xml.utils.WrappedRuntimeException(e);
        }
      }
    }
  }

  /**
   * Init the proximity position to zero for a forward axes.
   *
   * @param i The index into the m_proximityPositions array.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initProximityPosition(int i) throws javax.xml.transform.TransformerException
  {
    m_proximityPositions[i] = 0;
  }

  /**
   * Count forward one proximity position.
   *
   * @param i The index into the m_proximityPositions array, where the increment 
   *          will occur.
   */
  protected void countProximityPosition(int i)
  {
    if (i < m_proximityPositions.length)
      m_proximityPositions[i]++;
  }

  /**
   * Tells if this is a reverse axes.
   *
   * @return false, unless a derived class overrides.
   */
  public boolean isReverseAxes()
  {
    return false;
  }

  /**
   * Get which predicate is executing.
   *
   * @return The current predicate index, or -1 if no predicate is executing.
   */
  public int getPredicateIndex()
  {
    return m_predicateIndex;
  }

  /**
   * Process the predicates.
   *
   * @param context The current context node.
   * @param xctxt The XPath runtime context.
   *
   * @return the result of executing the predicate expressions.
   *
   * @throws javax.xml.transform.TransformerException
   */
  boolean executePredicates(int context, XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    
    int nPredicates = getPredicateCount();
    // System.out.println("nPredicates: "+nPredicates);
    if (nPredicates == 0)
      return true;

    PrefixResolver savedResolver = xctxt.getNamespaceContext();

    try
    {
      m_predicateIndex = 0;
      xctxt.pushSubContextList(this);
      xctxt.pushNamespaceContext(m_lpi.getPrefixResolver());
      xctxt.pushCurrentNode(context);

      for (int i = 0; i < nPredicates; i++)
      {
        // System.out.println("Executing predicate expression - waiting count: "+m_lpi.getWaitingCount());
        XObject pred = m_predicates[i].execute(xctxt);
        // System.out.println("\nBack from executing predicate expression - waiting count: "+m_lpi.getWaitingCount());
        // System.out.println("pred.getType(): "+pred.getType());
        if (XObject.CLASS_NUMBER == pred.getType())
        {
          if (DEBUG_PREDICATECOUNTING)
          {
            System.out.flush();
            System.out.println("\n===== start predicate count ========");
            System.out.println("m_predicateIndex: " + m_predicateIndex);
            // System.out.println("getProximityPosition(m_predicateIndex): "
            //                   + getProximityPosition(m_predicateIndex));
            System.out.println("pred.num(): " + pred.num());
          }

          int proxPos = this.getProximityPosition(m_predicateIndex);
          if (proxPos != (int) pred.num())
          {
            if (DEBUG_PREDICATECOUNTING)
            {
              System.out.println("\nnode context: "+nodeToString(context));
              System.out.println("index predicate is false: "+proxPos);
              System.out.println("\n===== end predicate count ========");
            }
            return false;
          }
          else if (DEBUG_PREDICATECOUNTING)
          {
            System.out.println("\nnode context: "+nodeToString(context));
            System.out.println("index predicate is true: "+proxPos);
            System.out.println("\n===== end predicate count ========");
          }
          
          // If there is a proximity index that will not change during the 
          // course of itteration, then we know there can be no more true 
          // occurances of this predicate, so flag that we're done after 
          // this.
          if(m_predicates[i].isStableNumber())
          {
            m_foundLast = true;
          }
        }
        else if (!pred.bool())
          return false;

        countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popNamespaceContext();
      xctxt.popSubContextList();
      m_predicateIndex = -1;
    }

    return true;
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);

    int nPredicates = getPredicateCount();

    for (int i = 0; i < nPredicates; i++)
    {
      m_predicates[i].fixupVariables(vars, globalsSize);
    }
  }

  
  /**
   * Diagnostics.
   *
   * @param n Node to give diagnostic information about, or null.
   *
   * @return Informative string about the argument.
   */
  protected String nodeToString(int n)
  {
    if(DTM.NULL != n)
    {
      DTM dtm = m_lpi.getXPathContext().getDTM(n);
      return dtm.getNodeName(n) + "{" + (n+1) + "}";
    }
    else
    {
      return "null";
    }
  }
  
  //=============== NodeFilter Implementation ===============

  /**
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int n)
  {

    XPathContext xctxt = m_lpi.getXPathContext();

    try
    {
      xctxt.pushCurrentNode(n);

      XObject score = execute(xctxt, n);

      // System.out.println("\n::acceptNode - score: "+score.num()+"::");
      if (score != NodeTest.SCORE_NONE)
      {
        if (getPredicateCount() > 0)
        {
          countProximityPosition(0);

          if (!executePredicates(n, xctxt))
            return DTMIterator.FILTER_SKIP;
        }

        return DTMIterator.FILTER_ACCEPT;
      }
    }
    catch (javax.xml.transform.TransformerException se)
    {

      // TODO: Fix this.
      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
    }

    return DTMIterator.FILTER_SKIP;
  }

  
  /**
   * Get the owning location path iterator.
   *
   * @return the owning location path iterator, which should not be null.
   */
  public LocPathIterator getLocPathIterator()
  {
    return m_lpi;
  }

  /**
   * Set the location path iterator owner for this walker.  Besides 
   * initialization, this function is called during cloning operations.
   *
   * @param li non-null reference to the owning location path iterator.
   */
  public void setLocPathIterator(LocPathIterator li)
  {
    m_lpi = li;
  }
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    int n = getPredicateCount();
    for (int i = 0; i < n; i++) 
    {
      if(getPredicate(i).canTraverseOutsideSubtree())
        return true;
    }
    return false;
   }
   
  /** This is true if nextNode returns null. */
  transient protected boolean m_foundLast = false;
    
  /** The owning location path iterator.
   *  @serial */
  protected LocPathIterator m_lpi;
  
  /**
   * Which predicate we are executing.
   */
  transient int m_predicateIndex = -1;
  
  /** The list of predicate expressions. Is static and does not need 
   *  to be deep cloned.
   *  @serial 
   */
  private Expression[] m_predicates;

  /**
   * An array of counts that correspond to the number
   * of predicates the step contains.
   */
  transient protected int[] m_proximityPositions;

  /** If true, diagnostic messages about predicate execution will be posted.  */
  static final boolean DEBUG_PREDICATECOUNTING = false;

}