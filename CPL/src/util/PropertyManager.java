/*
 * (c) Copyright 2001, 2002 Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * Yann-Gaël Guéhéneuc, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package util;

public class PropertyManager {
	public static String getAbstractLevelsExtension() {
		return ".class";
	}
	public static String getAbstractLevelsPackage() {
		return "padl.abstractlevel";
	}
	public static String getExamplesDirectory() {
		return "../Ptidej Examples and Configurations/";
	}
	public static String getGraphLayoutExtension() {
		return ".class";
	}
	public static String getGraphLayoutPackage() {
		return "ptidej.ui.layout.repository";
	}
	public static String getGraphLayoutType() {
		return "ptidej.ui.layout.IGraphLayout";
	}
	public static String getMicroPatternDetectionExtension() {
		return ".class";
	}
	public static String getMicroPatternDetectionPackage() {
		return "padl.analysis.micropattern.detection";
	}
	public static String getMicroPatternDetectionType() {
		return "padl.analysis.micropattern.IMicroPatternDetection";
	}
	public static String getMiscDestinationDirectory() {
		return "../Ptidej Solver Data/";
	}
	public static String getMiscOriginalDirectory() {
		return "../Ptidej Solver Data/";
	}
	public static String getMotifExtension() {
		return ".class";
	}
	public static String getMotifPackage() {
		return "padl.motif.repository";
	}
	public static String getPADLAnalysisExtension() {
		return ".class";
	}
	public static String getPADLAnalysisPackage() {
		return "padl.analysis.repository";
	}
	public static String getPADLAnalysisType() {
		return "padl.analysis.IAnalysis";
	}
	public static String getPADLVisitorExtension() {
		return ".class";
	}
	public static String getPADLVisitorPackage() {
		return "padl.visitor";
	}
	public static String getPtidejUIAnalysisExtension() {
		return ".class";
	}
	public static String getPtidejUIAnalysisPackage() {
		return "ptidej.ui.analysis.repository";
	}
	public static String getPtidejUIAnalysisType() {
		return "ptidej.ui.analysis.IAnalysis";
	}
	public static String getPtidejUIViewerExtensionExtension() {
		return ".class";
	}
	public static String getPtidejUIViewerExtensionPackage() {
		return "ptidej.viewer.extension.repository";
	}
	public static String getPtidejUIViewerExtensionType() {
		return "ptidej.viewer.extension.IExtension";
	}
	public static String getRulecardDirectory() {
		return "../SAD Rules Creator/rsc/";
	}
	public static String getRulecardTemplatesDirectory() {
		return "../SAD Rules Creator/rsc/Templates";
	}
	public static String getSADBinaryDirectory() {
		return "../SAD/bin/sad/";
	}

	public static String getSADResultDirectory() {
		return "../SAD/";
	}
	public static String getSADSourceDirectory() {
		return "../SAD/src/sad/";
	}
	public static String getSolverArguments() {
		return "-s 4 4";
	}
	public static String getSolverCommand() {
		return "PtidejSolver.exe";
	}
	public static String getSolverDirectory() {
		// Yann 2003/10/24: Relativity!
		// The PtidejSolver executable file must be
		// in the Ptidej project folder where it
		// will be found automatically, no need for
		// hard-coded path anymore!
		// return "C:/Documents and Settings/Yann/Work/Ptidej Solver 3/Ptidej/";
		return "../";
	}
	public static String getSolverDomainDirectory() {
		// HACK FOR PLUG-IN!!!
		//		return "../Ptidej Solver Data/";
		return "";
	}
	public static String getSolverDomainFile() {
		return "Domain.cl";
	}
	public static String getSolverInstructionFile() {
		return "Instructions.cl";
	}
	public static String getSolverResultDirectory() {
		return "../Ptidej Solver Data/";
	}
	public static String getSolverResultFile() {
		return "ConstraintResults.ini";
	}
	public static String getTestDirectory() {
		return "../Ptidej Examples and Configurations/bin/";
	}
	public static String getTestExtension() {
		return ".class";
	}
	public static String getTestPackage() {
		return "ptidej.tests.composite2";
	}

	private PropertyManager() {
	}
}
