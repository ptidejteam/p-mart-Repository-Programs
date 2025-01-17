/*
Copyright (C) 2000  Ralf Wiebicke

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package tudresden.ocl.injection.ocl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import tudresden.ocl.check.OclTypeException;
import tudresden.ocl.check.types.DefaultReflectionAdapter;
import tudresden.ocl.check.types.ReflectionFacade;
import tudresden.ocl.injection.IllegalParameterException;
import tudresden.ocl.lib.ArgoNameAdapter;
import tudresden.ocl.lib.NameAdapter;
import tudresden.ocl.lib.SimpleNameAdapter;
import tudresden.ocl.parser.OclParserException;

public class Main extends tudresden.ocl.injection.Main
{
	
	private static void makeCode(File constraintfile, OclConfig conf)
	throws OclParserException, OclTypeException, IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(constraintfile));
		
		String nextLine;
		String nextConstraint="";
		do
		{
			nextLine=br.readLine();
			if(nextLine==null || nextLine.trim().equals(""))
			{
				if(!nextConstraint.equals(""))
				{
					conf.makeConstraint(nextConstraint);
				}
				nextConstraint="";
			}
			else
				nextConstraint=nextConstraint+"\n"+nextLine;
		}
		while (nextLine!=null);

		/*
		for(Iterator iter=constrainedTypes.keySet().iterator(); iter.hasNext(); )
		{
			String nexttype=(String)iter.next();
			System.out.println("generated code for "+nexttype+":");
			((SortedFragments)(constrainedTypes.get(nexttype))).print(System.out);
		}
		 */
	}

	public static void main(final String[] args)
	{
		(new Main()).run(args);
	}

	private Main() {}

	protected void printUsage(PrintStream o)
	{
		super.printUsage(o);
		o.println("  -f  --constraint-file constraints.txt");
		o.println("  -r  --reflection-model modelpackage");
		o.println("      the model given by reflection");
		o.println("  -n  --name-adapter [none|argo]");
		o.println("      the nameadapter");
		o.println("  -is --invariant-scope [all|private|protected|package|public|explicit]");
		o.println("      the scope of invariants");
	}
	
	private String constraintfile=null;
	private ArrayList reflectionmodel=new ArrayList();
	private NameAdapter nameadapter=null;
	private OclConfig oclconf = new OclConfig();
	
	protected void processParameter() throws IOException, IllegalParameterException
	{
		if("--constraint-file".equals(args[i])||"-f".equals(args[i]))
		{
			if(constraintfile!=null)
				throw new IllegalParameterException("can use only one constraint file.");
			i++;
			if(i>=args.length)
				throw new IllegalParameterException("constraint file not given.");
			constraintfile=args[i];
		}
		else if("--reflection-model".equals(args[i])||"-r".equals(args[i]))
		{
			i++;
			if(i>=args.length)
				throw new IllegalParameterException("reflection package not given.");
			reflectionmodel.add(args[i]);
		}
		else if("--name-adapter".equals(args[i])||"-n".equals(args[i]))
		{
			if(nameadapter!=null)
				throw new IllegalParameterException("can use only one name adapter.");
			i++;
			if(i>=args.length)
				throw new IllegalParameterException("name adapter not given.");
			if("none".equals(args[i]))
				nameadapter=new SimpleNameAdapter();
			else if("argo".equals(args[i]))
				nameadapter=new ArgoNameAdapter();
			else
				throw new IllegalParameterException("name adapter must be 'none' or 'argo'.");
		}
		else if("--invariant-scope".equals(args[i])||"-is".equals(args[i]))
		{
			i++;
			if(i>=args.length)
				throw new IllegalParameterException("invariant scope not given.");
			if("private".equals(args[i])||"all".equals(args[i]))
				oclconf.invariantScope=oclconf.INVARIANT_SCOPE_PRIVATE;
			else if("protected".equals(args[i]))
				oclconf.invariantScope=oclconf.INVARIANT_SCOPE_PROTECTED;
			else if("package".equals(args[i]))
				oclconf.invariantScope=oclconf.INVARIANT_SCOPE_PACKAGE;
			else if("public".equals(args[i]))
				oclconf.invariantScope=oclconf.INVARIANT_SCOPE_PUBLIC;
			else if("explicit".equals(args[i]))
				oclconf.invariantScope=oclconf.INVARIANT_SCOPE_EXPLICIT;
			else
				throw new IllegalParameterException("invariant scope must be 'all', 'private', 'protected', 'package', 'public' or 'explicit'.");
		}
		else if("--trace-checking".equals(args[i]))
			oclconf.tracechecking=true;
		else if("--log-class".equals(args[i]))
			oclconf.logclass=true;
		else
			super.processParameter();
	}
	
	protected void postProcessParameters() throws IOException, IllegalParameterException
	{
		super.postProcessParameters();
		
		if(nameadapter==null)
			nameadapter=new SimpleNameAdapter();
		
		oclconf.modelfacade=new ReflectionFacade
		(
		(String[])(reflectionmodel.toArray(new String[0])),
		new DefaultReflectionAdapter(),
		nameadapter,
		new SourceReflectionExtender()
		);
		
		if(constraintfile!=null)
		{
			makeCode(new File(constraintfile), oclconf);
		}
		oclconf.violationmacro = getViolationMacro();
		taskConfigs.add(oclconf);
		
	}
	
	
}



