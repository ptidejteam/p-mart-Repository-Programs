/**
 * <p>Title: PtidejResourceBundle</p>
 * <p>Description: Resource Bundle for Ptidej Projects</p>
 * <p>Copyright: (c) Copyright 2005, Mehdi Lahlou,<br>
 * Université de Montréal</p>
 * 
 * <p>Use and copying of this software and preparation of derivative works<br>
 * based upon this software are permitted. Any copy of this software or<br>
 * of any derivative work must include the above copyright notice of<br>
 * Yann-Gaël Guéhéneuc, this paragraph and the one after it.</p>
 * 
 * <p>This software is made available AS IS, and THE AUTHOR DISCLAIMS<br>
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE<br>
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR<br>
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY<br>
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS<br>
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING<br>
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED<br>
 * OF THE POSSIBILITY OF SUCH DAMAGES.</p>
 * 
 * <p>All Rights Reserved.</p>
 * @author Mehdi Lahlou
 * @author Yann-Gaël Guéhéneuc
 * @since  18/05/2005
 */

package util.multilingual;

import java.util.ListResourceBundle;

public class PtidejResourceBundle extends ListResourceBundle {
	private static final Object[][] CONTENTS =
		{
				// *********** CPL ***********
				{ "util.awt.NameDialog::BUTTON_LABEL", new String("Okay") },
				{ "util.awt.NameDialog::TITLE", new String("Notice") },
				{ "util.help.Browser::BROWSER_URL",
						new String("http://www.javaworld.com") },
				{
						"util.help.Browser::Err_BRINGING_BROWSER",
						new String(
							"Error bringing up browser with command : {0}") },
				{
						"util.help.Browser::Err_INVOKE_BROWSER",
						new String(
							"Could not invoke browser with command : {0}") },
				{ "util.io.LogoBytesArrayBuilder::Err_BUILDING_BYTES",
						new String("Error while building bytes array.") },
				{ "util.io.SubtypeLoader::Err_FILES_NOT_FOUND",
						new String("Loader: No files found into {0}") },
				{ "util.io.SubtypeLoader::Err_READING_FILE",
						new String("Loader: Error while reading file {0}") },
				{ "util.io.SubtypeLoader::LOADING_FROM",
						new String("Loading from: {0}\n") },
				{ "util.lang.ClassLoader::Err_FILE",
						new String("The file {0} produces an error.") },

				// *********** Metrical Ptidej Solver ***********
				{
						"ptidej.solver.fingerprint.Fingerprint::IAException_UNKNOWN_OP",
						new String("Unknown operator") },
				{ "ptidej.solver.fingerprint.MetricUnknownException::STR",
						new String("Unknown metric") },
				{
						"ptidej.solver.fingerprint.Problem::USING_METRICS",
						new String(
							"++ Using metrics to fingerprint roles and to reduce search space efficiently") },
				{
						"ptidej.solver.fingerprint.problem.AbstractFactoryMotif::Problem_FMDM",
						new String("Factory method Design Motif") },
				{
						"ptidej.solver.fingerprint.problem.AdapterMotif::Problem_ADM",
						new String("Adapter Design Motif") },
				{
						"ptidej.solver.fingerprint.problem.CommandMotif::Problem_CDM",
						new String("Command Design Motif") },
				{
						"ptidej.solver.fingerprint.problem.CompositeMotif::Problem_CDM",
						new String("Composite Design Motif") },
				{
						"ptidej.solver.fingerprint.problem.FactoryMethodMotif::Problem_FMDM",
						new String("Factory method Design Motif") },

				// *********** PADL Analyses ***********
				{ "padl.analysis.Repository::LOAD_ANALYSIS",
						new String("Cannot load analysis {0} :\n{1}") },
				{
						"padl.analysis.micropattern.MicroPatternDetectionRepository::LOAD_ANALYSIS",
						new String("Cannot load analysis {0} :\n{1}") },
				{
						"padl.analysis.repository.SystematicUMLAnalysis::ANALYSIS_TIME",
						new String("Analysis time: {0} ms.") },
				{ "padl.analysis.repository.SystematicUMLAnalysis::NAME",
						new String("Systematic UML") },

				// *********** PADL Creators ***********
				{ "padl.creator.AOLCreator::CLASSES", new String(" classes...") },
				{ "padl.creator.AOLCreator::FILTERING_DONE",
						new String("Filtering done.") },
				{ "padl.creator.AOLCreator::PARSING", new String("Parsing") },
				{ "padl.creator.AOLCreator::PARSING_DONE",
						new String("Parsing done.") },
				{ "padl.creator.parser.AOLLexer::Err_BAD_IN_STREAM_INIT",
						new String("Error: Bad input stream initializer.") },
				{
						"padl.creator.parser.AOLLexer::Err_REPORT",
						new String(
							"Error: {0}\nInfo: {1}\nLine: {2} Char: {3} Text: \"{4}\"") },
				{ "padl.creator.parser.AOLLexer::FATAL_ERR",
						new String("Fatal Error.\n") },
				{ "padl.creator.parser.AOLLexer::ILLEGAL_CHAR",
						new String("Illegal character: {0}") },
				{ "padl.creator.parser.AOLLexer::LEXICAL_ERR",
						new String("Lexical Error: Unmatched Input.") },
				{
						"padl.creator.parser.AOLCodeParser::INVALID_ACTION_NUMBER",
						new String(
							"Invalid action number found in internal parse table") },
				{ "padl.creator.parser.AOLCodeParser::SYN_ERR",
						new String("Syntax error ({0})") },
				{ "padl.creator.AOLIdiomParser::SYN_ERR",
						new String("Syntax error ({0})") },
				{
						"padl.creator.AOLDesignParser::INVALID_ACTION_NUMBER",
						new String(
							"Invalid action number found in internal parse table") },
				{ "padl.creator.AOLDesignParser::SYN_ERR",
						new String("Syntax error ({0})") },
				{ "padl.creator.AspectCreator::CALL_AJC",
						new String("> Calling ajc...") },
				{ "padl.creator.AspectCreator::COMPILER_FAIL",
						new String("Compiler - Fail") },
				{ "padl.creator.AspectCreator::TERM_AJC",
						new String("> ajc terminate...") },
				{ "padl.creator.parserClassScope::TYPE_NAME",
						new String("ClassScope.IsTypeName: {0}") },
				{ "padl.creator.MSECreator::PARSING", new String("Parsing") },
				{ "padl.creator.MSECreator::PARSING_DONE",
						new String("Parsing done.") },
				{ "padl.creator.MSELexer::Err_BAD_IN_STREAM_INIT",
						new String("Error: Bad input stream initializer.") },
				{
						"padl.creator.MSELexer::Err_REPORT",
						new String(
							"Error: {0}\nInfo: {1}\nLine: {2} Char: {3} Text: \"{4}\"") },
				{ "padl.creator.MSEParser::SYN_ERR",
						new String("Syntax error ({0})") },
				// {"PADL AOL Creator::PADL ClassFile Creator::TYPE_VAL", new String("An instance of MethodInvocation must have a recognized type value (was {0} )")},

				// *********** PADL ***********
				{
						"padl.kernel.impl.CodeLevelModel::ENT_ADD_ORG_LEVEL",
						new String(
							"Cannot add entity directly in a model, must add a package first") },
				{ "padl.kernel.impl.Class::ALREADY_IMPL",
						new String("{0} is already implemented by {1}") },
				{ "padl.kernel.impl.Class::NOT_IMPL",
						new String("{0} is not implemented by {1}") },
				{ "padl.kernel.impl.Constituent::ACTOR_ID_NULL",
						new String("An actorID cannot be null") },
				{ "padl.kernel.impl.Constituent::ELEM_ABSTRACT",
						new String("This element can't be abstract") },
				{
						"padl.kernel.impl.Constituent::ELEM_CODE_DEF",
						new String("This Element can't contain code definition") },
				{ "padl.kernel.impl.Constituent::Exception_ACCEPT_METHOD",
						new String("Accept method: {0} ({1})") },
				{
						"padl.kernel.impl.Constructor+Destcuctor::PARAM_OR_METHOD_ADD",
						new String(
							"Only a parameter or a method invocation can be added to a method.") },
				{
						"padl.kernel.impl.ContainerAggregation::ELEMS_ATTACH",
						new String(
							"Elements only can be attached to a container aggregation at instantiation.") },
				{ "padl.kernel.impl.DelegatingMethod::ABSTRACT",
						new String("{0} can't be abstract.") },
				{
						"padl.kernel.impl.DesignLevelModel::ENT_OR_PATTERN_ADD",
						new String(
							"Only an entity or a pattern can be added to a design-level model.") },
				{ "padl.kernel.impl.Element::ATTACH",
						new String("{0} cannot be attached to {1}") },
				{ "padl.kernel.impl.Element::ELEM_ATTACH",
						new String("A Element cannot be attached to itself.") },
				{ "padl.kernel.impl.FirstClassEntity::ALREADY_INHERITED",
						new String("{0} is already inherited from {1}") },
				{
						"padl.kernel.impl.FirstClassEntity::ELEM_ADD_ENT",
						new String("Only an element can be added to an entity.") },
				{ "padl.kernel.impl.FirstClassEntity::ENT_INHERIT_ITSELF",
						new String("An entity cannot inherit from itself") },
				{ "padl.kernel.impl.Factory::ADD",
						new String("Cannot add array or primitive types.") },
				{
						"padl.kernel.impl.Field::CARDINALITY",
						new String(
							"The cardinality of a field must be one or more (was {0})") },
				{
						"padl.kernel.impl.GlobalField::CARDINALITY",
						new String(
							"The cardinality of a globalField must be one or more (was {0})") },
				{
						"padl.kernel.impl.IdiomLevelModel::ENT_ADD_ORG_LEVEL",
						new String(
							"Only an entity can be added to an organisation-level model.") },
				{ "padl.kernel.impl.PatternModel::ENT_ADD_MODEL",
						new String("Only an entity can be added to a model.") },
				{
						"padl.kernel.impl.PatternModel::Err_INIT_ALMD",
						new String(
							"Error: unable to initialize AbstractLevelModel Detector.") },
				{
						"padl.kernel.impl.Relationship::CARDINALITY",
						new String(
							"The cardinality of a relationship must be one or more (was {0})") },
				{
						"padl.kernel.impl.Package::ENTITY_ADD_PACKAGE",
						new String(
							"Cannot add constituents that are NOT entities or packages to a package.") },

				{
						"padl.motif.repository.ChainOfResponsibility::INTENT",
						new String(
							"Avoid coupling the sender of a request\nto its receiver by giving more\nthan one object a chance to handle\nthe request. Chain the receiving objects\nand pass the request along\nthe chain until an object handles it.") },
				{
						"padl.motif.repository.Composite::Err_INIT_ALMD",
						new String(
							"Error: unable to initialize the AbstractLevelModel detector.") },
				{
						"padl.motif.repository.Composite::INTENT",
						new String(
							"Compose objects into tree structures\nto represent part-whole hierarchies.\nComposite lets clients treat individual\nobjects and compositions of objects\nuniformly.") },
				{
						"padl.motif.repository.Facade::INTENT",
						new String(
							"Provide a unified interface to a set\nof interfaces in a subsystem.\nFacade defines a higher-level interface\nthat makes the subsystem easier to use.") },
				{
						"padl.motif.repository.FactoryMethod::INTENT",
						new String(
							"Define an interface for creating\na familly of objects, but let subclasses decide\nwhich class to instantiate.\nFactory Method lets a class defer\ninstantiation to subclasses.") },
				{
						"padl.motif.repository.GoodInheritance::INTENT",
						new String(
							"Define that a superclass must ignore\ncompletely its subclass(es).") },
				{
						"padl.motif.repository.Mediator::INTENT",
						new String(
							"Define an object that encapsulates\nhow a set of objects interact.\nMediator promotes loose coupling by keeping objects\nfrom referring to each other explicitly,\nand it lets you vary their interaction independently.") },
				{
						"padl.motif.repository.Memento::INTENT",
						new String(
							"Without violating encapsulation, capture\nand externalize an object's internal state\nso that the object can be restored to this state later.") },
				{
						"padl.motif.repository.Observer::ConcreteObserver_CLASS_PURPOSE",
						new String(
							"1. Maintains a reference to a ConcreteSubject object\n2. Stores state that should stay consistent with the subject's\n3. Implements the Observer updating interface to keep its state consistent with the subject's") },
				{
						"padl.motif.repository.Observer::ConcreteSubject_CLASS_PURPOSE",
						new String(
							"1. Stores state of interest to ConcreteObserver objects\n2. Sends a notification to its observers when its state changes") },
				{ "padl.motif.repository.Observer::DELEG_METHOD_COMMENT",
						new String("Add specific operations in this method") },
				{
						"padl.motif.repository.Observer::IDIOM",
						new String(
							"Exist in package java.util as class Observer.") },
				{
						"padl.motif.repository.Observer::INTENT",
						new String(
							"Define a one-to-many dependancy\nbetween objects.\nWhen one object changes state,\nall its dependants are notified\nand updated automatically.") },
				{
						"padl.motif.repository.Observer::Observer_PURPOSE",
						new String(
							"Defines a common interface for objects that should be notified when the subject changes.") },
				{
						"padl.motif.repository.Observer::Subject_PURPOSE",
						new String(
							"1. Knows  its observers. Any number of Observer objects may observe a subject\n2. Provides an interface for attaching and detaching Observer objects") },
				{
						"padl.motif.repository.Proxy::INTENT",
						new String(
							"Provide a surrogate or placeholder\nfor another object to control access to it.") },
				{ "padl.motif.repository.Visitor::INTENT",
						new String("(See the GoF's book.)") },

				// *********** Ptidej Solver 4 ***********
				{ "ptidej.solver.combination.BitString::NB_OF_BITS",
						new String("The number of bits must be {0}") },
				{ "ptidej.solver.branching.InteractiveBranching::ANOTHER_SOL",
						new String("Do you want another solution ? (y/n)") },
				{ "ptidej.solver.branching.InteractiveBranching::SOL",
						new String("> Solution {0} :") },
				{
						"ptidej.solver.branching.InteractiveRepair::CONST_CONTRADICTION",
						new String(
							"The following constraint(s) led to a contradiction:") },
				{
						"ptidej.solver.branching.InteractiveRepair::NO_MORE_SOL",
						new String(
							"There is no more solution because of the constraint") },
				{ "ptidej.solver.branching.InteractiveRepair::TO_BE_REPLACED",
						new String("To be replaced with: {0}") },
				{
						"ptidej.solver.branching.InteractiveRepair::WHICH_TO_PUT_BACK",
						new String(
							"Which one do you want to put back? (-1 -> none)") },
				{
						"ptidej.solver.branching.InteractiveRepair::WHICH_TO_RELAX",
						new String(
							"Which one do you want to relax? (-1 -> none)") },
				{
						"ptidej.solver.Problem::PTIDEJ",
						new String(
							"++ JPtidejSolver v1.5 (February, 2007), Copyright (c) 2001-2007 Y.-G. Guï¿½hï¿½neuc\n++ Constraint programming for design patterns and design defects identification") },
				{
						"ptidej.solver.solver.SimpleInteractiveRepair::NO_MORE_SOL",
						new String(
							"There is no more solution because of the constraints: {0}") },
				{
						"ptidej.solver.solver.SimpleInteractiveRepair::WHICH_TO_RELAX",
						new String(
							"Which one do you want to relax? (-1 -> none)") },

				// *********** Ptidej UI Analyses ***********
				{
						"ptidej.ui.analysis.repository.DifferenceHighlighterFromClasses::DIALOG_TITLE",
						new String("Choose a file with model differences") },
				{
						"ptidej.ui.analysis.repository.DifferenceHighlighterFromClasses::NAME",
						new String(
							"Model difference highlighter (from classes)") },
				{
						"ptidej.ui.analysis.repository.DifferenceHighlighterFromMethods::DIALOG_TITLE",
						new String("Choose a file with model differences") },
				{
						"ptidej.ui.analysis.repository.DifferenceHighlighterFromMethods::NAME",
						new String(
							"Model difference highlighter (from methods)") },
				{
						"ptidej.ui.analysis.repository.comparator.HighlighterFromMethods::Err_UNKNOWN_KEY",
						new String("Unknown key in file : {0}") },
				{
						"ptidej.ui.analysis.repository.comparator.Manager::Err_METHODE_NOT_FOUND_IN_ENTITY",
						new String("Cannot find method \"{0}\" in entity {1}") },
				{
						"ptidej.ui.analysis.repository.ModelComparator::DIALOG_TITLE",
						new String("Choose an AOL file") },
				{ "ptidej.ui.analysis.repository.ModelComparator::NAME",
						new String("AOL models comparison") },
				{ "ptidej.ui.analysis.Repository::Err_LOAD_ANALYSIS",
						new String("Cannot load analysis {0} :{1}") },

				// *********** Ptidej UI Viewer Extensions ***********
				{
						"ptidej.viewer.extension.repository.CSVGraphReader::Err_SYNTAX_ERR",
						new String("Syntax error on line {0}") },
				{
						"ptidej.viewer.extension.repository.CSVGraphReader::Err_UNEXPECTED_TOKEN",
						new String(
							"Unexpected token {0} read from stream, a word was expected") },
				{
						"ptidej.viewer.extension.repository.POMCalculator::COMPUTE_TIME",
						new String("Metrics computed in {0} ms.\n{1}") },
				{ "ptidej.viewer.extension.Repository::LOAD_EXTENSION",
						new String("Cannot load extension {0} :\n{1}") },

				// *********** Ptidej UI Viewer Plugin ***********
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::Err_Exception_OPEN_DIAGRAM_EDITOR",
						new String(
							"throwable exception while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionClass) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::MessageDialog_CONTENT",
						new String(
							"Ptidej Viewer: No class files found in :\n{0}\nAction: {1}") },
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::AddFolderAction::NO_CLASSFILE_FOUND",
						new String("No class files found in: {0}") },
				{
						"Ptidej UI Viewer Plugin::AddJavaAction::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::AddJavaAction::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::AddJavaAction::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{ "Ptidej UI Viewer Plugin::AddJavaAction::Err_TException",
						new String("Thorowable Exception") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackage::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackage::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackage::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{ "Ptidej UI Viewer Plugin::AddJDTPackage::Err_TException",
						new String("Thorowable Exception") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::Err_TException",
						new String("Thorowable Exception") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::MessageDialog_CONTENT",
						new String(
							"Ptidej Viewer: No class files found in :\n{0}\nAction: {1}") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::AddJDTPackageAction::NO_CLASSFILE_FOUND",
						new String("No class files found in: {0}") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::Err_Exception_OPEN_DIAGRAM_EDITOR",
						new String(
							"throwable exception while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionClass) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::MessageDialog_CONTENT",
						new String(
							"Ptidej Viewer: No class files found in :\n{0}\nAction: {1}") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::AddNewFolderAction::NO_CLASSFILE_FOUND",
						new String("No class files found in: {0}") },
				{
						"Ptidej UI Viewer Plugin::AddNewJavaAction::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::AddNewJavaAction::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::AddNewJavaAction::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{ "Ptidej UI Viewer Plugin::AddNewJavaAction::Err_TException",
						new String("Thorowable Exception") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackage::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackage::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackage::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{ "Ptidej UI Viewer Plugin::AddNewJDTPackage::Err_TException",
						new String("Thorowable Exception") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::Err_Exception_OPEN_DIAGRAM_EDITOR",
						new String(
							"throwable exception while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionClass) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::MessageDialog_CONTENT",
						new String(
							"Ptidej Viewer: No class files found in :\n{0}\nAction: {1}") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::AddNewJDTPackageAction::NO_CLASSFILE_FOUND",
						new String("No class files found in: {0}") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::Err_Exception_OPEN_DIAGRAM_EDITOR",
						new String(
							"throwable exception while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionClass) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::MessageDialog_CONTENT",
						new String(
							"Ptidej Viewer: No class files found in :\n{0}\nAction: {1}") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::AddSelectedPackageAction::NO_CLASSFILE_FOUND",
						new String("No class files found in: {0}") },
				{ "Ptidej UI Viewer Plugin::DiagramEditor::CANVAS_IS_NULL",
						new String("Canvas == NULL") },
				{ "Ptidej UI Viewer Plugin::DiagramEditor::CANVAS_NOT_NULL",
						new String("Canvas NOT NULL") },
				{ "Ptidej UI Viewer Plugin::DiagramEditor::Err_FILE_NOT_FOUND",
						new String("Error: Couldn't find selected file!") },
				{ "Ptidej UI Viewer Plugin::DiagramEditor::Err_INVALID_INPUT",
						new String("Invalid Input: Must be IFileEditorInput") },
				{ "Ptidej UI Viewer Plugin::DiagramEditor::PROJECT_NAME",
						new String("File 1") },
				{ "Ptidej UI Viewer Plugin::FileList::DEFAULT_SELECT",
						new String("defaut selection") },
				{
						"Ptidej UI Viewer Plugin::FindConcretePatterns::MessageDialog_CONTENT",
						new String("Action: {0}") },
				{
						"Ptidej UI Viewer Plugin::FindConcretePatterns::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::GUIOptions::Button_FIND_CONCRETE_PATTERNS",
						new String("Find concrete patterns") },
				{ "Ptidej UI Viewer Plugin::GUIOptions::Button_GEN_EXEC_INFO",
						new String("Generate execution information") },
				{ "Ptidej UI Viewer Plugin::GUIOptions::Button_GEN_MODEL",
						new String("Generate model") },
				{
						"Ptidej UI Viewer Plugin::GUIOptions::Button_LOAD_CONCRETE_PATTERNS",
						new String("Load concrete patterns") },
				{
						"Ptidej UI Viewer Plugin::GUIOptions::Button_MODIFY_STRUCTURE",
						new String("Modify structure") },
				{
						"Ptidej UI Viewer Plugin::GUIOptions::Button_REMOVE_ALL_CONCRETE_PATTERNS",
						new String("Remove all concrete patterns") },
				{ "Ptidej UI Viewer Plugin::GUIOptions::Button_SHOW_SOLUTIONS",
						new String("Show solutions") },
				{ "Ptidej UI Viewer Plugin::GUIOptions::FILE_DIALOG_TITLE",
						new String("Choose constraints result file") },
				{ "Ptidej UI Viewer Plugin::GUIOptions::Group_OPTIONS",
						new String("Options") },
				{ "Ptidej UI Viewer Plugin::NewProject::MessageDialog_CONTENT",
						new String("Action: {0}") },
				{ "Ptidej UI Viewer Plugin::NewProject::MessageDialog_TITLE",
						new String("JTU Viewer") },
				{
						"Ptidej UI Viewer Plugin::PopActionClass::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionClass) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::PopActionClass::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::PopActionJar::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionJar) view could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::PopActionJava::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionJava::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::PopActionJava::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionJavaPackage::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionJavaPackage::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::PopActionJavaPackage::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewClass::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionNewClass) diagram could not be initialized") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJar::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionJar) view could not be initialized") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJava::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJava::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJavaPackage::Err_Exception_IN_RUN",
						new String("Exception in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJavaPackage::Err_INIT_DIAGRAM_EDITOR",
						new String("Couldn't initialize DiagramEditor") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewJavaPackage::Err_NPException_IN_RUN",
						new String("NullPointerException in run()") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewPackage::Err_Exception_INIT_DIAGRAM_EDITOR",
						new String(
							"An error occured during DiagramEditor initializaton") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewPackage::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionNewPackage) diagram could not be initialized") },
				{
						"Ptidej UI Viewer Plugin::PopActionNewPtidej::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionNewPtidej) diagram could not be initialized") },
				{
						"Ptidej UI Viewer Plugin::PopActionPackage::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionPackage) could not initialize Diagram Editor!") },
				{
						"Ptidej UI Viewer Plugin::PopActionPackage::Err_OPEN_DIAGRAM_EDITOR",
						new String("Error while opening DiagramEditor!") },
				{
						"Ptidej UI Viewer Plugin::PopActionPtidej::Err_INIT_DIAGRAM_EDITOR",
						new String(
							"(class: PopActionPtidej) couldn't initialize Diagram Editor!") },
				{ "Ptidej UI Viewer Plugin::Preferences::AGGREGATION_DISPLAY",
						new String("Aggregation display") },
				{ "Ptidej UI Viewer Plugin::Preferences::AGGREGATION_NAMES",
						new String("Aggregation names") },
				{ "Ptidej UI Viewer Plugin::Preferences::ASSOCIATION_DISPLAY",
						new String("Association display") },
				{ "Ptidej UI Viewer Plugin::Preferences::ASSOCIATION_NAMES",
						new String("Association names") },
				{ "Ptidej UI Viewer Plugin::Preferences::COMPOSITION_DISPLAY",
						new String("Composition display") },
				{ "Ptidej UI Viewer Plugin::Preferences::COMPOSITION_NAMES",
						new String("Composition names") },
				{
						"Ptidej UI Viewer Plugin::Preferences::CONTAINER_AGGREGATION_DISPLAY",
						new String("Container aggregation display") },
				{
						"Ptidej UI Viewer Plugin::Preferences::CONTAINER_AGGREGATION_NAMES",
						new String("Container aggregation names") },
				{
						"Ptidej UI Viewer Plugin::Preferences::CONTAINER_COMPOSITION_DISPLAY",
						new String("Container composition display") },
				{
						"Ptidej UI Viewer Plugin::Preferences::CONTAINER_COMPOSITION_NAMES",
						new String("Container composition names") },
				{ "Ptidej UI Viewer Plugin::Preferences::CREATION_DISPLAY",
						new String("Creation display") },
				{ "Ptidej UI Viewer Plugin::Preferences::CREATION_NAMES",
						new String("Creation names") },
				{ "Ptidej UI Viewer Plugin::Preferences::FIELD_NAMES",
						new String("Field names") },
				{
						"Ptidej UI Viewer Plugin::Preferences::FULLY_QUALIFIED_NAMES",
						new String("Fully qualified names") },
				{
						"Ptidej UI Viewer Plugin::Preferences::GHOST_ENTITIES_DISPLAY",
						new String("Ghost entities display") },
				{ "Ptidej UI Viewer Plugin::Preferences::HIERARCHY_DISPLAY",
						new String("Hierarchy display") },
				{ "Ptidej UI Viewer Plugin::Preferences::HIERARCHY_NAMES",
						new String("Hierarchy names") },
				{ "Ptidej UI Viewer Plugin::Preferences::USE_DISPLAY",
						new String("Use relationship display") },
				{ "Ptidej UI Viewer Plugin::Preferences::USE_NAMES",
						new String("Use relationship names") },
				{ "Ptidej UI Viewer Plugin::Preferences::METHOD_NAMES",
						new String("Method names") },
				{
						"Ptidej UI Viewer Plugin::PtidejDiagramEditor::Err_INVALID_INPUT",
						new String("Invalid Input: Must be IFileEditorInput") },
				{ "Ptidej UI Viewer Plugin::PtidejDiagramEditor::LOADING_TIME",
						new String("Loading time: {0} ms.\n\n") },
				{
						"Ptidej UI Viewer Plugin::PtidejDiagramEditor::STATISTICS",
						new String(
							"\nStatistics:\n{0}\n              Total memory\tFree memory\nBefore build  {1}\t\t{2}\t\t{3}\nAfter build   {4}\t\t{5}\t\t{6}\nAfter display {7}\t\t{8}\t\t{9}\n") },
				{
						"Ptidej UI Viewer Plugin::RemoveConcretePatterns::MessageDialog_CONTENT",
						new String("Action: {0}") },
				{
						"Ptidej UI Viewer Plugin::RemoveConcretePatterns::MessageDialog_TITLE",
						new String("Ptidej Viewer") },
				{
						"Ptidej UI Viewer Plugin::SaveProject::MessageDialog_CONTENT",
						new String("Action: {0}") },
				{ "Ptidej UI Viewer Plugin::SaveProject::MessageDialog_TITLE",
						new String("Ptidej Viewer") },

				// *********** Ptidej UI Viewer Standalone AWT ***********
				{
						"ptidej.viewer.core.ConstraintViewerPanel::CHECKBOXES_TEXTS_R01",
						new String("Custom problem") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::CHECKBOXES_TEXTS_R02",
						new String("AC-4 problem") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::CHECKBOXES_TEXTS_R03",
						new String("Simple automatic solver") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::CHECKBOXES_TEXTS_R04",
						new String("Combinatorial automatic solver") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::CHECKBOXES_TEXTS_R05",
						new String("Automatic solver") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::FILE_DIALOG_CHOOSE_CRF_TITLE",
						new String("Choose constraints result file") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::FILE_DIALOG_SAVE_MODEL_TITLE",
						new String("Save Claire domain to...") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::FIND_SIMILAR_MA_TEXT",
						new String("Find similar micro-architectures") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::GENERATE_PROGRAM_MODEL",
						new String("Generate program model") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::GENERATE_SOLVER_DATA_TEXT",
						new String("Generate solver execution data") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::HELP_PTIDEJSOLVER3",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/IJCAI01MSPC.doc.pdf") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::HELP_PTIDEJSOLVER4",
						new String(
							"http://www.yann-gael.gueheneuc.net/Work/Tutoring/Documents/040924+JPtidejSolver.doc.pdf") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::HELP_PTIDEJSOLVER4_METRICAL",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/WCRE04.doc.pdf") },
				{ "ptidej.viewer.core.ConstraintViewerPanel::LIST_ALL_MA_TEXT",
						new String("List similar micro-architectures") },
				{ "ptidej.viewer.core.ConstraintViewerPanel::LOAD_MA_TEXT",
						new String("Load similar micro-architectures") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::MODIFY_PROGRAM_MODEL_TEXT",
						new String("Modify program model") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::PTIDEJSOLVER_ALL_TEXT",
						new String("Ptidej Solvers") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::PTIDEJSOLVER3_TEXT",
						new String("Ptidej Solver 3") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::PTIDEJSOLVER4_METRICAL_TEXT",
						new String("Metrical Ptidej Solver 4") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::PTIDEJSOLVER4_TEXT",
						new String("Ptidej Solver 4") },
				{
						"ptidej.viewer.core.ConstraintViewerPanel::REMOVE_ALL_MA_TEXT",
						new String("Remove all similar micro-architectures") },
				{
						"ptidej.viewer.core.MetricResultsActionListener::FOLD_UNFOLD",
						new String("Fold/Unfold") },
				{
						"ptidej.viewer.core.MetricResultsActionListener::FOLD_UNFOLD_ALL",
						new String("Fold/Unfold All") },
				{ "ptidej.viewer.core.MetricResultsActionListener::GO_TO",
						new String("Go to") },
				{ "ptidej.viewer.core.MetricResultsActionListener::SHOW_HIDE",
						new String("Show/Hide") },
				{ "ptidej.viewer.core.MetricResultsFrame::Lbl_COMPONENT_LABEL",
						new String("Metric {0} = {1}") },
				{
						"ptidej.viewer.core.MetricResultsFrame::Lbl_NO_SOLUTION_LABEL",
						new String("No metric computed") },
				{ "ptidej.viewer.core.MetricResultsFrame::TITLE",
						new String("Metrics results") },
				{
						"ptidej.viewer.core.MetricViewerPanel::Button_COMPUTE_METRICS",
						new String("Compute metrics") },
				{ "ptidej.viewer.core.MetricViewerPanel::Button_SHOW_RESULTS",
						new String("Show results") },
				{
						"ptidej.viewer.layout.PercentLayout::IAException_INVALID_CONSTRAINT",
						new String("Invalid constraint") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::CREATE_NEW_PROJECT_TEXT",
						new String("Create New Project") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_AOL_NAME",
						new String("AOL File") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_AOL_FILE",
						new String("an AOL file") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_AOL_FILE_TEXT",
						new String("Add AOL file (.aol)") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_ASPECTJ_FILE",
						new String("an AspectJ list") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::ADD_ASPECTJ_FILE_TEXT",
						new String("Add AspectJ list (.lst)") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_MSE_FILE",
						new String("an MSE exchange format file") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_MSE_FILE_TEXT",
						new String("Add MSE file (.mse)") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::ADD_ECLIPSE_JDT_PROJECT",
						new String("an Eclipse JDT project") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::ADD_ECLIPSE_JDT_PROJECT_TEXT",
						new String("Add Eclipse JDT project") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_CPP_FILE",
						new String("a C++ file") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_CPP_FILE_TEXT",
						new String("Add C++ file (.cpp)") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_JAR_FILE",
						new String("a jar file") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_JAR_FILE_TEXT",
						new String("Add JAR file (.jar)") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_JAVA_FILE",
						new String("a class file") },
				{ "ptidej.viewer.core.ProjectViewerPanel::ADD_JAVA_FILE_TEXT",
						new String("Add Java file (.class)") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::ADD_JAVA_PACKAGE_FILE",
						new String("classes from a package") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::ADD_JAVA_PACKAGE_TEXT",
						new String("Add Java package (.class)") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::CREATE_NEW_GRAPHICAL_PROJECT_TEXT",
						new String("Create Graphical Ptidej project") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::CREATE_NEW_HIERARCHICAL_PROJECT_TEXT",
						new String("Create Hierachical Ptidej project") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::Err_LOAD_ONLY_JAVA_PTIDEJ_PROJECT",
						new String("Loading Java-only Ptidej project.") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::Err_UNKNOWN_FILE_TYPE",
						new String("Unknown file type!") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::FILE_DIALOG_ADD_TITLE",
						new String("Choose {0} to add") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::FILE_DIALOG_CHOOSE_PROJECT_FILE",
						new String("Choose a project file") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::FILE_DIALOG_CHOOSE_PROJECT_TITLE",
						new String("Choose a project to load") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::FILE_DIALOG_EXTRA_DATA_TITLE",
						new String("Choose extra data to load") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::FILE_DIALOG_PROJECT_NAME_TITLE",
						new String("Please, enter a project name:") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::HELP_PADL",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/LMO02.doc.pdf") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::LOAD_EXTRINSIC_DATA_TEXT",
						new String("Load extrinsic data") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::LOAD_PTIDEJ_PROJECT_TEXT",
						new String("Load Ptidej project") },
				{ "ptidej.viewer.core.ProjectViewerPanel::LOADING_TIME",
						new String("Loading time: {0} ms.\n\n") },
				{ "ptidej.viewer.core.ProjectViewerPanel::PROJECT_NAME",
						new String("Name") },
				{ "ptidej.viewer.core.ProjectViewerPanel::SAVE_PROJECT_TEXT",
						new String("Save Ptidej project") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::STATISTICS",
						new String(
							"\nStatistics:\n{0}\n              Total memory\tFree memory\nBefore build  {1}\t\t{2}\t\t{3}\nAfter build   {4}\t\t{5}\t\t{6}\nAfter display {7}\t\t{8}\t\t{9}\n") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_AOL_CODE_NAME",
						new String("AOL Code") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_AOL_IDIOM_NAME",
						new String("AOL Idiom") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_ASPECTJ_NAME",
						new String("AspectJ") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_MSE_NAME",
						new String("MSE") },
				{
						"ptidej.viewer.core.ProjectViewerPanel::TYPE_ECLIPSE_JDT_PROJECT_NAME",
						new String("Eclipse JDT Project") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_CPP_NAME",
						new String("C++") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_JAVA_NAME",
						new String("JavaCode") },
				{ "ptidej.viewer.core.ProjectViewerPanel::TYPE_PTIDEJ_NAME",
						new String("Ptidej") },
				{ "ptidej.viewer.awt.occurrences.Constants::FOLD_UNFOLD",
						new String("Fold/Unfold") },
				{ "ptidej.viewer.awt.occurrences.Constants::FOLD_UNFOLD_ALL",
						new String("Fold/Unfold All") },
				{
						"ptidej.viewer.awt.occurrences.Constants::FOLD_UNFOLD_GHOSTS",
						new String("Fold/Unfold containing Ghost") },
				{
						"ptidej.viewer.awt.occurrences.Constants::FOLD_UNFOLD_OBJECT",
						new String("Fold/Unfold containing Object") },
				{ "ptidej.viewer.awt.occurrences.Constants::GO_TO",
						new String("Go to") },
				{ "ptidej.viewer.awt.occurrences.Constants::NO_SOLUTION_LABEL",
						new String("No occurrences") },
				{ "ptidej.viewer.awt.occurrences.Constants::SHOW_HIDE",
						new String("Show/Hide") },
				{
						"ptidej.viewer.awt.occurrences.ListPanel::Lbl_SOLUTION_LABEL",
						new String(
							"Micro-architecture {0} similar at {1}% with {2}") },
				{ "ptidej.viewer.awt.occurrences.ListPanel::TITLE",
						new String("Occurrences") },
				{ "ptidej.viewer.awt.entities.Constants::GO_TO",
						new String("Go to") },
				{ "ptidej.viewer.awt.entities.Constants::NO_SOLUTION_LABEL",
						new String("No entities") },
				{
						"ptidej.viewer.splash.SplashProgram::Err_SPLASH_IMG_NOT_FOUND",
						new String("Splash image not found") },
				{ "ptidej.viewer.splash.SplashProgram::IMG_SPLASH_FILE_NAME",
						new String("Setup.gif") },
				{ "ptidej.viewer.core.ViewerPanel::ABOUT_TEXT",
						new String("About") },
				{ "ptidej.viewer.core.ViewerPanel::AGGREGATION_DISPLAY",
						new String("Aggregation display") },
				{ "ptidej.viewer.core.ViewerPanel::AGGREGATION_NAMES",
						new String("Aggregation names") },
				{ "ptidej.viewer.core.ViewerPanel::ASSOCIATION_DISPLAY",
						new String("Association display") },
				{ "ptidej.viewer.core.ViewerPanel::ASSOCIATION_NAMES",
						new String("Association names") },
				{ "ptidej.viewer.core.ViewerPanel::Button_IMAGE_BUTTON",
						new String("About") },
				{ "ptidej.viewer.core.ViewerPanel::COMPOSITION_DISPLAY",
						new String("Composition display") },
				{ "ptidej.viewer.core.ViewerPanel::COMPOSITION_NAMES",
						new String("Composition names") },
				{
						"ptidej.viewer.core.ViewerPanel::CONTAINER_AGGREGATION_DISPLAY",
						new String("Container Aggregation display") },
				{
						"ptidej.viewer.core.ViewerPanel::CONTAINER_AGGREGATION_NAMES",
						new String("Container Aggregation names") },
				{
						"ptidej.viewer.core.ViewerPanel::CONTAINER_COMPOSITION_DISPLAY",
						new String("Container Composition display") },
				{
						"ptidej.viewer.core.ViewerPanel::CONTAINER_COMPOSITION_NAMES",
						new String("Container Composition names") },
				{ "ptidej.viewer.core.ViewerPanel::CREATION_DISPLAY",
						new String("Creation display") },
				{ "ptidej.viewer.core.ViewerPanel::CREATION_NAMES",
						new String("Creation names") },
				{ "ptidej.viewer.core.ViewerPanel::EXIT_TEXT",
						new String("Exit") },
				{ "ptidej.viewer.core.ViewerPanel::FIELD_NAMES",
						new String("Field names") },
				{ "ptidej.viewer.core.ViewerPanel::FULLY_QUALIFIED_NAMES",
						new String("Fully qualified names") },
				{ "ptidej.viewer.core.ViewerPanel::GHOST_ENTITIES_DISPLAY",
						new String("Ghost entities display") },
				{ "ptidej.viewer.core.ViewerPanel::HELP_TEXT",
						new String("Help") },
				{ "ptidej.viewer.core.ViewerPanel::HIERARCHY_DISPLAY",
						new String("Hierarchy display") },
				{ "ptidej.viewer.core.ViewerPanel::HIERARCHY_NAMES",
						new String("Hierarchy names") },
				{ "ptidej.viewer.core.ViewerPanel::METHOD_NAMES",
						new String("Method names") },
				{ "ptidej.viewer.core.ViewerPanel::NO_PROJECT_TEXT",
						new String("No Ptidej project") },
				{ "ptidej.viewer.core.ViewerPanel::PRINT_TEXT",
						new String("Print") },
				{ "ptidej.viewer.core.ViewerPanel::USE_DISPLAY",
						new String("Use display") },
				{ "ptidej.viewer.core.ViewerPanel::USE_NAMES",
						new String("Use names") },
				{
						"ptidej.viewer.core.VisitorViewerPanel::FILE_DIALOG_SAVE_MODEL_TITLE",
						new String("Save model to...") },

				// *********** Ptidej UI Viewer ***********
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C01",
						new String(
							"The Ptidej tool suite is a set of tools to evaluate and to enhance the quality of object-") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C02",
						new String(
							"oriented programs, promoting the use of patterns, either at the language-, design-, or") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C03",
						new String("architectural-levels.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C04",
						new String(
							"The Ptidej tool suite (Pattern Trace Identification, Detection, and Enhancement in Java)") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C05",
						new String(
							"by Yann-Gaï¿½l Guï¿½hï¿½neuc uses the PADL meta-model (Pattern and Abstract-level") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C06",
						new String(
							"Description Language), extension of the PDL meta-model (Pattern Description Language)") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C07",
						new String("by Hervï¿½ Albin-Amiot.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C08",
						new String("Get more information at www.ptidej.net.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R01_C09",
						new String(
							"Send comments and questions to yann-gael@gueheneuc.net.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C01",
						new String("Main developpers:") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C02",
						new String("        - Yann-Gaï¿½l Guï¿½hï¿½neuc") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C03",
						new String("                Universitï¿½ de Montrï¿½al") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C04",
						new String(
							"                ï¿½cole des Mines de Nantes") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C05",
						new String(
							"                Object Technology International, Inc.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C06",
						new String("        - Hervï¿½ Albin-Amiot.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C07",
						new String(
							"                ï¿½cole des Mines de Nantes") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C08",
						new String("                Softmaint S.A.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C10",
						new String("With contributions by:") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C11",
						new String("        - Jean-Yves Guyomarc'h") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C12",
						new String(
							"                On the Ptidej Solver in Java and metrics") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C13",
						new String("                Graduate, fall 2004") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C14",
						new String(
							"                Universitï¿½ de Montrï¿½al.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C15",
						new String("        - Lulzim Laloshi and Driton Salihu") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C16",
						new String("                On the Eclipse plug-in") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C17",
						new String(
							"                Undergraduates, Summer 2004") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C18",
						new String(
							"                Universitï¿½ de Montrï¿½al.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C19",
						new String(
							"        - Ward Flores and Sï¿½bastien Robidoux") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C20",
						new String(
							"                On the C++ creator and PADL") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C21",
						new String(
							"                Undergraduates, Summer 2004") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C22",
						new String(
							"                Universitï¿½ de Montrï¿½al.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C23",
						new String(
							"        - Salime Bensemmane, Iyadh Sidhom, and Fayï¿½al Skhiri") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C24",
						new String(
							"                On the Ptidej Solver in Java") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C25",
						new String(
							"                Undergraduates, Summer 2004") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C26",
						new String(
							"                Universitï¿½ de Montrï¿½al.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C27",
						new String("        - Farouk Zaidi") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C28",
						new String(
							"                On the Java classfile creator and metrics") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C29",
						new String("                Trainee, Winter 2004") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C30",
						new String(
							"                Universitï¿½ de technologie Belfort-Montbï¿½liard") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C31",
						new String(
							"                Centre de Recherche en Informatique de Montrï¿½al") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R02_C32",
						new String(
							"                Universitï¿½ de Montrï¿½al.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R03_C01",
						new String(
							"The Ptidej tool suite, copyright (c) 2000-2004,") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R03_C02",
						new String("Yann-Gaï¿½l Guï¿½hï¿½neuc.") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R03_C03",
						new String("All right reserved.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R04_C01",
						new String(
							"Use and copying of this software and preparation of derivative works based upon this") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R04_C02",
						new String(
							"software are permitted. Any copy of this software or of any derivative work must include") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R04_C03",
						new String(
							"the above copyright notice of the authors, this paragraph and the one after it.") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C01",
						new String(
							"This software is made available AS IS, and THE AUTHORS DISCLAIM ALL WARRANTIES,") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C02",
						new String(
							"EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C03",
						new String(
							"OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND NOT") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C04",
						new String(
							"WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN ANY LIABILITY FOR") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C05",
						new String(
							"DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS EXPRESSLY") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C06",
						new String(
							"DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING NEGLIGENCE)") },
				{
						"ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C07",
						new String(
							"OR STRICT, LIABILITY EVEN IF THE AUTHORS ARE ADVISED OF THE POSSIBILITY") },
				{ "ptidej.viewer.ProjectData::COPYRIGHT_MESSAGE_R05_C08",
						new String("OF SUCH DAMAGES.") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R01",
						new String("CPL v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R02",
						new String("PADL v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R03",
						new String("PADL AOL Creator v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R04",
						new String("PADL C++ Creator v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R05",
						new String("PADL ClassFile Creator v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R06",
						new String("POM v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R07",
						new String("Ptidej v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R08",
						new String("Ptidej Solver 3 v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R09",
						new String("Ptidej Solver 4 v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R10",
						new String("Ptidej UI v1.0.0") },
				{ "ptidej.viewer.ProjectData::PROJECTS_R11",
						new String("Ptidej UI Viewer v1.0.0") },

				// *********** Ptidej UI ***********
				{ "ptidej.ui.kernel.Constituent::NAME",
						new String("An instance of {0}") },
				{
						"ptidej.ui.layout.InheritanceClusterLayout::Err_CALL_getDepth",
						new String(
							"The depth of the DepthAwareEntity must be computed (computeDepth(Map)) prior to a call to getDepth().") },
				{
						"ptidej.ui.layout.InheritanceDepthLayout::Err_CALL_getDepth",
						new String(
							"The depth of the DepthAwareDEntity must be computed (computeDepth(Hashtable)) prior to a call to getDepth().") },

				// *********** Ptidej ***********
				{ "ptidej.solver.SolutionGenerator::UNKNOWN_PROBLEM_TYPE",
						new String("SolutionGenerator: Unknown problem type.") },
				{
						"ptidej.solver.SolutionGenerator::UNKNOWN_SOLVER_ALGO",
						new String(
							"SolutionGenerator: Unknown solver algorithm.") },
				{
						"ptidej.solver.SolutionGenerator::UNKNOWN_SOLVER_VER",
						new String("SolutionGenerator: Unknown solver version.") },

				{
						"ptidej.viewer.ui.window.ModelStatisticsWindow::NO_MODEL_LABEL",
						new String("No Selected Model.") },

				// *********** Ptidej UI Viewer Standalone Swing ***********
				{
						"EPI_SIMILAR_MICRO_ARCHITECTURE_HELP_LINK",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/CSMR06a.doc.pdf") },
				{
						"PTIDEJ_SOLVER_3_SIMILAR_MICRO_ARCHITECTURE_HELP_LINK",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/IJCAI01MSPC.doc.pdf") },
				{
						"PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP_LINK",
						new String(
							"http://www.yann-gael.gueheneuc.net/Work/Tutoring/Documents/040924+JPtidejSolver.doc.pdf") },
				{
						"METRICAL_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP_LINK",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/WCRE04.doc.pdf") },
				{
						"CREATE_PROJECT_HELP_LINK",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/LMO02.doc.pdf") },
				{
						"SYSTEMATIC_UML_HELP_LINK",
						new String(
							"http://www.iro.umontreal.ca/~ptidej/Publications/Documents/APSEC04.doc.pdf") },
				{ "DOTTY_HELP_LINK", new String("http://www.graphviz.org/") },
				{ "ADJACENCY_MATRIX_INFOVIS_HELP_LINK",
						new String("http://ivtk.sourceforge.net/") },
				{ "ADJACENCY_MATRIX_OADYMPPAC_HELP_LINK",
						new String("http://contraintes.inria.fr/OADymPPaC/") },
				{
						"SUGIBIB_HELP_LINK",
						new String(
							"http://www2.informatik.uni-wuerzburg.de/SugiBib/english.html") },
				{ "ptidej.viewer.ui.MenuBar::MENU_FILE", new String("File") },
				{ "ptidej.viewer.ui.MenuBar::MENU_FILE_MNEMONIC",
						new String("F") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_NEW_GRAPHICAL_PROJECT",
						new String("New Graphical Project") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_NEW_GRAPHICAL_PROJECT_SMALL_ICON",
						new String("New16.gif") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_NEW_GRAPHICAL_PROJECT_MNEMONIC",
						new String("N") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_NEW_HIERARCHICAL_PROJECT",
						new String("New Hierarchical Project") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_NEW_HIERARCHICAL_PROJECT_SMALL_ICON",
						new String("NewHierarchical16.gif") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_NEW_HIERARCHICAL_PROJECT_MNEMONIC",
						new String("N") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_LOAD_GRAPHICAL_PROJECT",
						new String("Load Graphical Project") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_GRAPHICAL_PROJECT_SMALL_ICON",
						new String("Open16.gif") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_GRAPHICAL_PROJECT_MNEMONIC",
						new String("L") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_LOAD_HIERARCHICAL_PROJECT",
						new String("Load Hierarchical Project") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_HIERARCHICAL_PROJECT_SMALL_ICON",
						new String("OpenHierachical16.gif") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_HIERARCHICAL_PROJECT_MNEMONIC",
						new String("L") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_LOAD_EXTRINSIC",
						new String("Load Extrinsic data") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_LOAD_EXTRINSIC_MNEMONIC",
						new String("E") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_USAGE", new String("Usage") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ADD", new String("Add ...") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ADD_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ADD_SMALL_ICON",
						new String("Add16.gif") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_CLASS_FILE",
						new String("Java Class File (.class)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_CLASS_FILE_MNEMONIC",
						new String("J") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_ARCHIVE_FILE",
						new String("Java Archive File (.jar)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_ARCHIVE_FILE_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_PACKAGE",
						new String("Java Package (.class)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_JAVA_PACKAGE_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CPP_FILE",
						new String("CPP File (.cpp)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CPP_FILE_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_AOL_CODE_FILE",
						new String("AOL Code File (.aol)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_AOL_CODE_FILE_MNEMONIC",
						new String("O") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_AOL_IDIOM_FILE",
						new String("AOL Idiom File (.aol)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_AOL_IDIOM_FILE_MNEMONIC",
						new String("O") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ASPECTJ_FILE",
						new String("AspectJ File (.lst)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ASPECTJ_FILE_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_MSE_FILE",
						new String("MSE File (.mse)") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_MSE_FILE_MNEMONIC",
						new String("M") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ECLIPSE_JDT_PROJECT",
						new String("Eclipse JDT Project (.project)") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ECLIPSE_JDT_PROJECT_MNEMONIC",
						new String("E") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_EXPORT_SVG",
						new String("Export SVG") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CLOSE_ALL",
						new String("Close All") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CLOSE_ACTIVE",
						new String("Close") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CLOSE_ACTIVE_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CLOSE_ALL_EXPECT_ACTIVE",
						new String("Close All But Current") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ALL",
						new String("Save All") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ALL_SMALL_ICON",
						new String("SaveAll16.gif") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ALL_MNEMONIC",
						new String("v") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ACTIVE",
						new String("Save") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ACTIVE_SMALL_ICON",
						new String("Save16.gif") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SAVE_ACTIVE_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_PRINT", new String("Print") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_PRINT_SMALL_ICON",
						new String("Print16.gif") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_PRINT_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_EXIT", new String("Exit") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_EXIT_SMALL_ICON",
						new String("Stop16.gif") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_EXIT_MNEMONIC",
						new String("x") },
				{ "ptidej.viewer.ui.MenuBar::MENU_VIEW", new String("View") },
				{ "ptidej.viewer.ui.MenuBar::MENU_VIEW_MNEMONIC",
						new String("V") },
				{ "ptidej.viewer.ui.MenuBar::MENU_LAYOUT_TOPICS",
						new String("Graph Layout") },
				{ "ptidej.viewer.ui.MenuBar::MENU_LAYOUT_TOPICS_MNEMONIC",
						new String("L") },
				{ "ptidej.viewer.ui.MenuBar::MENU_TOOLBARS",
						new String("Toolbars") },
				{ "ptidej.viewer.ui.MenuBar::MENU_TOOLBARS_MNEMONIC",
						new String("T") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SHOW_ALL",
						new String("Show All") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SHOW_ALL_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_HIDE_ALL",
						new String("Hide All") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_HIDE_ALL_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SETTINGS",
						new String("Settings") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SETTINGS_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::MENU_LANGUAGE",
						new String("Language") },
				{ "ptidej.viewer.ui.MenuBar::MENU_LANGUAGE_MNEMONIC",
						new String("L") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_ITEM_ENGLISH",
						new String("English") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_ITEM_FRENCH",
						new String("Franï¿½ais") },
				{ "ptidej.viewer.ui.MenuBar::MENU_WINDOW", new String("Window") },
				{ "ptidej.viewer.ui.MenuBar::MENU_WINDOW_MNEMONIC",
						new String("W") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_MINIMIZE_ALL",
						new String("Minimize All") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_MINIMIZE_ALL_MNEMONIC",
						new String("M") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CASCADE",
						new String("Cascade") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_CASCADE_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_TILE_VERTICALLY",
						new String("Tile Vertically") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_TILE_VERTICALLY_MNEMONIC",
						new String("V") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_TILE_HORIZONTALLY",
						new String("Tile Horizontally") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_TILE_HORIZONTALLY_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::EMPTY_ICON",
						new String("Empty16.gif") },
				{ "ptidej.viewer.ui.MenuBar::MENU_HELP", new String("Help") },
				{ "ptidej.viewer.ui.MenuBar::MENU_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_HELP_TOPICS",
						new String("Help Topics") },
				{ "ptidej.viewer.ui.MenuBar::MENU_HELP_TOPICS_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ABOUT", new String("About") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ABOUT_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::MENU_TOOLS", new String("Tools") },
				{ "ptidej.viewer.ui.MenuBar::MENU_TOOLS_MNEMONIC",
						new String("T") },
				{ "ptidej.viewer.ui.MenuBar::MENU_UML", new String("UML") },
				{ "ptidej.viewer.ui.MenuBar::MENU_UML_MNEMONIC",
						new String("U") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SYSTEMATIC_UML",
						new String("Systematic UML") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SYSTEMATIC_UML_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SYSTEMATIC_UML_HELP",
						new String("Systematic UML") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_SYSTEMATIC_UML_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_AOL_MODELS_COMPARISION",
						new String("AOL models comparision") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_AOL_MODELS_COMPARISION_MNEMONIC",
						new String("A") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_MODEL_DIFFERENCE_HIGHLIGHTER",
						new String("Model difference highlighter") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_MODEL_DIFFERENCE_HIGHLIGHTER_MNEMONIC",
						new String("h") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_METHODS",
						new String("From Methods") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_METHODS_MNEMONIC",
						new String("M") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_CLASSES",
						new String("From Classes") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_CLASSES_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SUGIBIB",
						new String("SugiBib") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SUGIBIB_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SUGIBIB",
						new String("SugiBib") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SUGIBIB_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SUGIBIB_HELP",
						new String("SugiBib") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_SUGIBIB_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_POM_BASED_METRICS",
						new String("POM-Based metrics") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_POM_BASED_METRICS_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ADJACENCY_MATRIX",
						new String("Adjacency matrix") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ADJACENCY_MATRIX_MNEMONIC",
						new String("m") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_OADYMPPAC",
						new String("OADymPPaC") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_OADYMPPAC_MNEMONIC",
						new String("O") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_OADYMPPAC_HELP",
						new String("OADymPPaC") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_OADYMPPAC_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_INFOVIS",
						new String("InfoVis") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_INFOVIS_MNEMONIC",
						new String("I") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_INFOVIS_HELP",
						new String("InfoVis") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_ADJACENCY_MATRIX_INFOVIS_HELP_MNEMONIC",
						new String("e") },
				{ "ptidej.viewer.ui.MenuBar::MENU_DOTTY", new String("Dotty") },
				{ "ptidej.viewer.ui.MenuBar::MENU_DOTTY_MNEMONIC",
						new String("D") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_DOTTY", new String("Dotty") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_DOTTY_MNEMONIC",
						new String("D") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_DOTTY_HELP",
						new String("Dotty") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_DOTTY_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_DESIGN_PATTERNS",
						new String("Design Patterns") },
				{ "ptidej.viewer.ui.MenuBar::MENU_DESIGN_PATTERNS_MNEMONIC",
						new String("p") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ANTI_PATTERNS",
						new String("Anti Patterns") },
				{ "ptidej.viewer.ui.MenuBar::MENU_ANTI_PATTERNS_MNEMONIC",
						new String("a") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PATTERN",
						new String("Design Motifs") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PATTERN_MNEMONIC",
						new String("P") },

				{ "ptidej.viewer.ui.MenuBar::RADIO_Creator",
						new String("Creators") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Creator_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_InheritanceDepthClustering",
						new String("Inheritance-depth Clustering") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_InheritanceDepthClustering_MNEMONIC",
						new String("D") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_InheritanceClustering",
						new String("Inheritance-depth Clustering") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_InheritanceClustering_MNEMONIC",
						new String("I") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Simple",
						new String("Simple") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Simple_MNEMONIC",
						new String("B") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_SugiyamaAlgorithm",
						new String("SugiyamaAlgorithm") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_SugiyamaAlgorithm_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AbstractFactory",
						new String("Abstract Factory") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AbstractFactory_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Adapter",
						new String("Adapter") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Adapter_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Builder",
						new String("Builder") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Builder_MNEMONIC",
						new String("B") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_ChainOfResponsibility",
						new String("Chain Of Responsibility") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_ChainOfResponsibility_MNEMONIC",
						new String("R") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Command",
						new String("Command") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Command_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Composite",
						new String("Composite") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Composite_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Decorator",
						new String("Decorator") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Decorator_MNEMONIC",
						new String("D") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Facade",
						new String("Facade") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Facade_MNEMONIC",
						new String("a") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_FactoryMethod",
						new String("Factory Method") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_FactoryMethod_MNEMONIC",
						new String("F") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Flyweight",
						new String("Fly Weight") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Flyweight_MNEMONIC",
						new String("F") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_GoodInheritance",
						new String("Good Inheritance") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_GoodInheritance_MNEMONIC",
						new String("G") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Mediator",
						new String("Mediator") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Mediator_MNEMONIC",
						new String("M") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Memento",
						new String("Memento") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Memento_MNEMONIC",
						new String("e") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Observer",
						new String("Observer") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Observer_MNEMONIC",
						new String("O") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Prototype",
						new String("Proxy") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Prototype_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Proxy", new String("Proxy") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Proxy_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Singleton",
						new String("Singleton") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Singleton_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_State", new String("State") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_State_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_TemplateMethod",
						new String("Template Method") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_TemplateMethod_MNEMONIC",
						new String("T") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Visitor",
						new String("Visitor") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_Visitor_MNEMONIC",
						new String("V") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SOLVER", new String("Solver") },
				{ "ptidej.viewer.ui.MenuBar::MENU_SOLVER_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AUTOMATIC_SOLVER",
						new String("Automatic Solver") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AUTOMATIC_SOLVER_MNEMONIC",
						new String("A") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_COMBINATORIAL_AUTOMATIC_SOLVER",
						new String("Combinatorial Automatic Solver") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_COMBINATORIAL_AUTOMATIC_SOLVER_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_SIMPLE_AUTOMATIC_SOLVER",
						new String("Simple Automatic Solver") },
				{
						"ptidej.viewer.ui.MenuBar::RADIO_SIMPLE_AUTOMATIC_SOLVER_MNEMONIC",
						new String("S") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_3",
						new String("Ptidej Solver 3") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_3_MNEMONIC",
						new String("3") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PROBLEM",
						new String("Problem") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PROBLEM_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AC4_PROBLEM",
						new String("AC-4 Problem") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_AC4_PROBLEM_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_CUSTOM_PROBLEM",
						new String("Custom Problem") },
				{ "ptidej.viewer.ui.MenuBar::RADIO_CUSTOM_PROBLEM_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_GENERATE_PROGRAM_MODEL",
						new String("Generate program model") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_GENERATE_PROGRAM_MODEL_MNEMONIC",
						new String("m") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_GENERATE_SOLVER_EXECUTION_DATA",
						new String("Generate solver execution data") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_GENERATE_SOLVER_EXECUTION_DATA_MNEMONIC",
						new String("d") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_3_SIMILAR_MICRO_ARCHITECTURE",
						new String("Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_3_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("S") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_3_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_3_FIND_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("F") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_3_SIMILAR_MICRO_ARCHITECTURE_HELP",
						new String(
							"Ptidej Solver 3 Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_3_SIMILAR_MICRO_ARCHITECTURE_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_4",
						new String("Ptidej Solver 4") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_4_MNEMONIC",
						new String("4") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE",
						new String("Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("S") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("F") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP",
						new String(
							"Ptidej Solver 4 Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_EPI", new String("EPI") },
				{ "ptidej.viewer.ui.MenuBar::MENU_EPI_MNEMONIC",
						new String("E") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_EPI_SIMILAR_MICRO_ARCHITECTURE",
						new String("Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_EPI_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("S") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_EPI_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_EPI_FIND_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("F") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_EPI_SIMILAR_MICRO_ARCHITECTURE_HELP",
						new String("EPI Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_EPI_SIMILAR_MICRO_ARCHITECTURE_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_METRICAL_PTIDEJ_SOLVER_4",
						new String("Metrical Ptidej Solver 4") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_METRICAL_PTIDEJ_SOLVER_4_MNEMONIC",
						new String("M") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_METRICAL_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE",
						new String("Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::MENU_METRICAL_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("S") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_METRICAL_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_METRICAL_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE_MNEMONIC",
						new String("F") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_METRICAL_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP",
						new String(
							"Metrical Ptidej Solver 4 Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_METRICAL_PTIDEJ_SOLVER_4_SIMILAR_MICRO_ARCHITECTURE_HELP_MNEMONIC",
						new String("H") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVERS",
						new String("Ptidej Solvers") },
				{ "ptidej.viewer.ui.MenuBar::MENU_PTIDEJ_SOLVERS_MNEMONIC",
						new String("t") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_SIMILAR_MICRO_ARCHITECTURES",
						new String("Load Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LOAD_SIMILAR_MICRO_ARCHITECTURES_MNEMONIC",
						new String("L") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LIST_SIMILAR_MICRO_ARCHITECTURES",
						new String("List Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_LIST_SIMILAR_MICRO_ARCHITECTURES_MNEMONIC",
						new String("i") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_REMOVE_ALL_SIMILAR_MICRO_ARCHITECTURES",
						new String("Remove All Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_REMOVE_ALL_SIMILAR_MICRO_ARCHITECTURES_MNEMONIC",
						new String("R") },
				{ "ptidej.viewer.ui.MenuBar::ITEM_MODIFY_PROGRAM_MODEL",
						new String("Modify program model") },
				{
						"ptidej.viewer.ui.MenuBar::ITEM_MODIFY_PROGRAM_MODEL_MNEMONIC",
						new String("M") },
				{ "ptidej.viewer.ui.ToolBar::TOOLBAR_FILE_TITLE",
						new String("Main toolbar") },
				{ "ptidej.viewer.ui.ToolBar::CMD_NEW_GRAPHICAL_PROJECT",
						new String("New Graphical Project") },
				{ "ptidej.viewer.ui.ToolBar::CMD_NEW_GRAPHICAL_PROJECT_ICON",
						new String("New24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_NEW_HIERARCHICAL_PROJECT",
						new String("New Hierarchical Project") },
				{
						"ptidej.viewer.ui.ToolBar::CMD_NEW_HIERARCHICAL_PROJECT_ICON",
						new String("NewHierarchical24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_LOAD_GRAPHICAL_PROJECT",
						new String("Load Graphical Project") },
				{ "ptidej.viewer.ui.ToolBar::CMD_LOAD_GRAPHICAL_PROJECT_ICON",
						new String("Open24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_LOAD_HIERARCHICAL_PROJECT",
						new String("Load Hierarchical Project") },
				{
						"ptidej.viewer.ui.ToolBar::CMD_LOAD_HIERARCHICAL_PROJECT_ICON",
						new String("OpenHierarchical24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_ADD",
						new String("Add File ...") },
				{ "ptidej.viewer.ui.ToolBar::CMD_SAVE_ALL",
						new String("Save All") },
				{ "ptidej.viewer.ui.ToolBar::CMD_SAVE_ACTIVE",
						new String("Save") },
				{ "ptidej.viewer.ui.ToolBar::CMD_PRINT", new String("Print") },
				{ "ptidej.viewer.ui.ToolBar::CMD_SAVE_ALL_ICON",
						new String("SaveAll24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_SAVE_ACTIVE_ICON",
						new String("Save24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_PRINT_ICON",
						new String("Print24.gif") },
				{ "ptidej.viewer.ui.ToolBar::CMD_JAVA_CLASS_FILE_MNEMONIC",
						new String("J") },
				{ "ptidej.viewer.ui.ToolBar::CMD_JAVA_ARCHIVE_FILE_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.ToolBar::CMD_JAVA_PACKAGE_MNEMONIC",
						new String("P") },
				{ "ptidej.viewer.ui.ToolBar::CMD_CPP_FILE_MNEMONIC",
						new String("C") },
				{ "ptidej.viewer.ui.ToolBar::CMD_AOL_FILE_MNEMONIC",
						new String("O") },
				{ "ptidej.viewer.ui.ToolBar::CMD_ASPECTJ_FILE_MNEMONIC",
						new String("A") },
				{ "ptidej.viewer.ui.ToolBar::CMD_MSE_FILE_MNEMONIC",
						new String("M") },
				{ "ptidej.viewer.ui.ToolBar::CMD_ECLIPSE_JDT_PROJECT_MNEMONIC",
						new String("E") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_ADD_ICON",
						new String("Add24.gif") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_JAVA_CLASS_FILE",
						new String("Java Class File (.class)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_JAVA_ARCHIVE_FILE",
						new String("Java Archive File (.jar)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_JAVA_PACKAGE",
						new String("Java Package (directory)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_CPP_FILE",
						new String("CPP File (.cpp)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_AOL_CODE_FILE",
						new String("AOL Code File (.aol)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_AOL_IDIOM_FILE",
						new String("AOL Idiom File (.aol)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_ASPECTJ_FILE",
						new String("AspectJ File (.lst)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_MSE_FILE",
						new String("MSE File (.mse)") },
				{
						"ptidej.viewer.widget.DropDownButton::CMD_ECLIPSE_JDT_PROJECT",
						new String("Eclipse JDT Projecy (.project)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_PTIDEJ_FILE",
						new String("Ptidej File (.ptidej)") },
				{ "ptidej.viewer.widget.DropDownButton::CMD_DROP_DOWN_ICON",
						new String("Dropdown16.gif") },
				{ "ptidej.viewer.action.FileAction::TYPE_AOL_CODE_NAME",
						new String("AOL Code") },
				{ "ptidej.viewer.action.FileAction::TYPE_AOL_IDIOM_NAME",
						new String("AOL Idiom") },
				{ "ptidej.viewer.action.FileAction::TYPE_ASPECTJ_NAME",
						new String("AspectJ") },
				{ "ptidej.viewer.action.FileAction::TYPE_MSE_NAME",
						new String("MSE") },
				{
						"ptidej.viewer.action.FileAction::TYPE_ECLIPSE_JDT_PROJECT_NAME",
						new String("Eclipse JDT Project") },
				{ "ptidej.viewer.action.FileAction::TYPE_CPP_NAME",
						new String("C++") },
				{ "ptidej.viewer.action.FileAction::TYPE_JAVA_NAME",
						new String("Java") },
				{ "ptidej.viewer.action.FileAction::TYPE_PTIDEJ_NAME",
						new String("Ptidej") },
				{ "ptidej.viewer.action.FileAction::ADD_ASPECTJ_FILE",
						new String("Add AspectJ File") },
				{ "ptidej.viewer.action.FileAction::ADD_MSE_FILE",
						new String("Add MSE File") },
				{ "ptidej.viewer.action.FileAction::ADD_ECLIPSE_JDT_PROJECT",
						new String("Add Eclipse JDT Project") },
				{ "ptidej.viewer.action.FileAction::ADD_JAVA_FILE",
						new String("Add Java File") },
				{ "ptidej.viewer.action.FileAction::ADD_JAR_FILE",
						new String("Add Jar File") },
				{ "ptidej.viewer.action.FileAction::ADD_JAVA_PACKAGE_FILE",
						new String("Add Java Package") },
				{ "ptidej.viewer.action.FileAction::ADD_PTIDEJ_FILE",
						new String("Add from Ptidej File") },
				{ "ptidej.viewer.action.FileAction::ADD_CPP_FILE",
						new String("Add C++ File") },
				{ "ptidej.viewer.action.FileAction::ADD_AOL_CODE_FILE",
						new String("Add AOL Code File") },
				{ "ptidej.viewer.action.FileAction::ADD_AOL_IDIOM_FILE",
						new String("Add AOL Idiom File") },
				{ "ptidej.viewer.ui.panel.OptionPanel::VIEWER_OPT_FRAME_TITLE",
						new String("Viewer Options") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_CREATION_NAMES",
						new String("Creation Names") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_CREATION_DISPLAY",
						new String("Creation Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_USE_NAMES",
						new String("Use Names") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_USE_DISPLAY",
						new String("Use Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_ASSOCIATION_NAMES",
						new String("Association Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_ASSOCIATION_DISPLAY",
						new String("Association Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_AGGREGATION_NAMES",
						new String("Aggregation Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_AGGREGATION_DISPLAY",
						new String("Aggregation Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_COMPOSITION_NAMES",
						new String("Composition Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_COMPOSITION_DISPLAY",
						new String("Composition Display") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_CONT_AGGREGATION_NAMES",
						new String("Container Aggregation Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_CONT_AGGREGATION_DISPLAY",
						new String("Container Aggregation Display") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_CONT_COMPOSITION_NAMES",
						new String("Container Composition Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_CONT_COMPOSITION_DISPLAY",
						new String("Container Composition Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_HIERARCHY_NAMES",
						new String("Hierarchy Names") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_HIERARCHY_DISPLAY",
						new String("Hierarchy Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_FIELD_NAMES",
						new String("Field Names") },
				{ "ptidej.viewer.ui.panel.OptionPanel::CHK_METHOD_NAMES",
						new String("Method Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_FULLY_QUALIFIED_NAMES",
						new String("Fully Qualified Names") },
				{
						"ptidej.viewer.ui.panel.OptionPanel::CHK_GHOST_ENTITIES_DISPLAY",
						new String("Ghost Entities Display") },
				{ "ptidej.viewer.ui.panel.OptionPanel::RADIO_SELECT_ALL",
						new String("Select All") },
				{ "ptidej.viewer.ui.panel.OptionPanel::RADIO_UNSELECT_ALL",
						new String("Unselect All") },
				{ "ptidej.viewer.widget.CollapsablePanel::TOGGLE_EXPAND_ICON",
						new String("expandPanel.png") },
				{
						"ptidej.viewer.widget.CollapsablePanel::TOGGLE_EXPAND_ROLLOVER_ICON",
						new String("expandPanelHover.png") },
				{
						"ptidej.viewer.widget.CollapsablePanel::TOGGLE_EXPAND_SELECTED_ICON",
						new String("collapsePanel.png") },
				{
						"ptidej.viewer.widget.CollapsablePanel::TOGGLE_EXPAND_SELECTED_ROLLOVER_ICON",
						new String("collapsePanelHover.png") },
				{ "ptidej.viewer.widget.CollapsablePanel::CMD_CLOSE_ICON",
						new String("closePanel.png") },
				{
						"ptidej.viewer.widget.CollapsablePanel::CMD_CLOSE_ROLLOVER_ICON",
						new String("closePanelHover.png") },
				{ "ptidej.viewer.widget.UndockablePanel::CMD_UNDOCK_ICON",
						new String("Undock16.png") },
				{
						"ptidej.viewer.widget.UndockablePanel::CMD_UNDOCK_ROLLOVER_ICON",
						new String("UndockHover16.png") },
				{ "ptidej.viewer.widget.UndockablePanel::CMD_DOCK_ICON",
						new String("Dock16.png") },
				{
						"ptidej.viewer.widget.UndockablePanel::CMD_DOCK_ROLLOVER_ICON",
						new String("DockHover16.png") },
				{ "ptidej.viewer.widget.UndockablePanel::CMD_DOCK",
						new String("Dock") },
				{ "ptidej.viewer.widget.UndockablePanel::CMD_UNDOCK",
						new String("Undock") },
				{
						"ptidej.viewer.ui.panel.EPIPanel::CMD_EPI_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_AbstractFactory",
						new String("Abstract Factory") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Adapter",
						new String("Adapter") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Builder",
						new String("Builder") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_ChainOfResponsibility",
						new String("Chain Of Responsibility") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Command",
						new String("Command") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Composite",
						new String("Composite") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Creator",
						new String("Creator") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Decorator",
						new String("Decorator") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Facade",
						new String("Facade") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_FactoryMethod",
						new String("Factory Method") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Flyweight",
						new String("Flyweight") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_GoodInheritance",
						new String("Good Inheritance") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Mediator",
						new String("Mediator") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Memento",
						new String("Memento") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Observer",
						new String("Observer") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Prototype",
						new String("Prototype") },
				{ "ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Proxy",
						new String("Proxy") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Singleton",
						new String("Singleton") },
				{ "ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_State",
						new String("State/Strategy") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_TemplateMethod",
						new String("Template Method") },
				{
						"ptidej.viewer.ui.panel.DesignMotifChoicePanel::RADIO_Visitor",
						new String("Visitor") },
				{
						"ptidej.viewer.ui.panel.SolverGeneralPanel::RADIO_AUTOMATIC_SOLVER",
						new String("Automatic Solver") },
				{
						"ptidej.viewer.ui.panel.SolverGeneralPanel::RADIO_COMBINATORIAL_AUTOMATIC_SOLVER",
						new String("Combinatorial Automatic Solver") },
				{
						"ptidej.viewer.ui.panel.SolverGeneralPanel::RADIO_SIMPLE_AUTOMATIC_SOLVER",
						new String("Simple Automatic Solver") },
				{ "ptidej.viewer.ui.panel.Solver3Panel::RADIO_AC4_PROBLEM",
						new String("AC-4 Problem") },
				{ "ptidej.viewer.ui.panel.Solver3Panel::RADIO_CUSTOM_PROBLEM",
						new String("Custom Problem") },
				{
						"ptidej.viewer.ui.panel.Solver3Panel::CMD_GENERATE_PROGRAM_MODEL",
						new String("Generate program model") },
				{
						"ptidej.viewer.ui.panel.Solver3Panel::CMD_GENERATE_SOLVER_EXECUTION_DATA",
						new String("Generate solver execution data") },
				{
						"ptidej.viewer.ui.panel.Solver3Panel::CMD_PTIDEJ_SOLVER_3_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.panel.Solver4Panel::CMD_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.panel.MetricalSolver4Panel::CMD_METRICAL_PTIDEJ_SOLVER_4_FIND_SIMILAR_MICRO_ARCHITECTURE",
						new String("Find Similar Micro-Architectures") },
				{ "ptidej.viewer.ui.panel.ToolPanel::CMD_SHOW_STATISTICS",
						new String("Show Model Statistics") },
				{ "ptidej.viewer.ui.panel.ToolPanel::CMD_LIST_ENTITIES",
						new String("List Model Entities") },
				{
						"ptidej.viewer.ui.panel.ToolPanel::CMD_LOAD_SIMILAR_MICRO_ARCHITECTURES",
						new String("Load Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.panel.ToolPanel::CMD_LIST_SIMILAR_MICRO_ARCHITECTURES",
						new String("List Similar Micro-Architectures") },
				{
						"ptidej.viewer.ui.panel.ToolPanel::CMD_REMOVE_ALL_SIMILAR_MICRO_ARCHITECTURES",
						new String("Remove All Similar Micro-Architectures") },
				{ "ptidej.viewer.ui.panel.ToolPanel::CMD_MODIFY_PROGRAM_MODEL",
						new String("Modify program model") },
				{ "ptidej.viewer.ui.DesktopPane::FRM_MAIN_TITLE",
						new String("Ptidej v4.0") },
				{ "ptidej.viewer.ui.Desktop::TAB_DESIGN_PATTERNS_TITLE",
						new String("Design Patterns") },
				{ "ptidej.viewer.ui.Desktop::TAB_ANTI_PATTERNS_TITLE",
						new String("Design Smells") },
				{ "ptidej.viewer.ui.Desktop::TAB_CODE_SMELLS_TITLE",
						new String("Code Smells") },
				{ "ptidej.viewer.ui.Desktop::TAB_MICRO_PATTERNS_TITLE",
						new String("Micro Patterns") },
				{ "ptidej.viewer.ui.Desktop::TAB_TOOLS_TITLE",
						new String("Tools") },
				{ "ptidej.viewer.ui.Desktop::TAB_VISITORS_TITLE",
						new String("Visitors") },
				{ "ptidej.viewer.ui.Desktop::TAB_VIEWER_OPTIONS_TITLE",
						new String("Viewer Options") },
				{ "ptidej.viewer.ui.panel.DesignMotifPanel::PNL_EPI_TITLE",
						new String("EPI") },
				{
						"ptidej.viewer.ui.panel.AntiPatternPanel::PNL_ANTI_PATTERNS_TITLE",
						new String("Anti Patterns") },
				{
						"ptidej.viewer.ui.panel.AntiPatternPanel::PNL_ANTI_PATTERN_CHOICE_TITLE",
						new String("Choice") },
				{
						"ptidej.viewer.ui.panel.AntiPatternPanel::PNL_ANTI_PATTERN_ACTION_TITLE",
						new String("Action") },
				{
						"ptidej.viewer.ui.panel.CodeSmellPanel::PNL_CODE_SMELLS_TITLE",
						new String("Code Smells") },
				{ "ptidej.viewer.ui.panel.DesignMotifPanel::PNL_PATTERN_TITLE",
						new String("Design Motifs") },
				{ "ptidej.viewer.ui.panel.DesignMotifPanel::PNL_SOLVER_TITLE",
						new String("Solver") },
				{
						"ptidej.viewer.ui.panel.DesignMotifPanel::PNL_PTIDEJ_SOLVER_3_TITLE",
						new String("Ptidej Solver 3") },
				{
						"ptidej.viewer.ui.panel.DesignMotifPanel::PNL_PTIDEJ_SOLVER_4_TITLE",
						new String("Ptidej Solver 4") },
				{
						"ptidej.viewer.ui.panel.DesignMotifPanel::PNL_METRICAL_PTIDEJ_SOLVER_4_TITLE",
						new String("Metrical Ptidej Solver 4") },
				{
						"ptidej.viewer.ui.panel.DesignMotifPanel::PNL_PTIDEJ_SOLVERS_TITLE",
						new String("Ptidej Solvers") },
				{ "ptidej.viewer.ui.panel.VisitorPanel::PNL_VISITORS_TITLE",
						new String("Visitors") },
				{
						"ptidej.viewer.ui.panel.MicroPatternPanel::PNL_MICRO_PATTERNS_TITLE",
						new String("Micro-patterns") },
				{ "ptidej.viewer.ui.panel.ToolPanel::CMD_SYSTEMATIC_UML",
						new String("Systematic UML") },
				{
						"ptidej.viewer.ui.panel.ModelPanel::CMD_AOL_MODELS_COMPARISION",
						new String("AOL models comparision") },
				{
						"ptidej.viewer.ui.panel.ModelPanel::CMD_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_METHODS",
						new String(
							"Model difference highlighter (From Methods)") },
				{
						"ptidej.viewer.ui.panel.ModelPanel::CMD_MODEL_DIFFERENCE_HIGHLIGHTER_FROM_CLASSES",
						new String(
							"Model difference highlighter (From Classes)") },
				{ "ptidej.viewer.ui.panel.OtherToolPanel::CMD_SUGIBIB",
						new String("SugiBib") },
				{
						"ptidej.viewer.ui.panel.OtherToolPanel::CMD_POM_BASED_METRICS",
						new String("POM-Based metrics") },
				{
						"ptidej.viewer.ui.panel.OtherToolPanel::CMD_ADJACENCY_MATRIX_OADYMPPAC",
						new String("Adjacency matrix (OADymPPaC)") },
				{
						"ptidej.viewer.ui.panel.OtherToolPanel::CMD_ADJACENCY_MATRIX_INFOVIS",
						new String("Adjacency matrix (InfoVis)") },
				{ "ptidej.viewer.ui.panel.OtherToolPanel::CMD_DOTTY",
						new String("Dotty") },

				{
						"ptidej.viewer.ui.panel.AntiPatternActionPanel::CMD_NEW_RULECARD",
						new String("New RuleCard") },
				{
						"ptidej.viewer.ui.panel.AntiPatternActionPanel::CMD_DETECT_ANTI_PATTERNS",
						new String("Detect Design Smell(s)") },
				{
						"ptidej.viewer.ui.panel.AntiPatternActionPanel::CMD_DELETE_RULECARD",
						new String("Delete RuleCard") },

				{ "sad.util.DesignSmellRepository::Err_LOAD_ANTI_PATTERN",
						new String("Cannot load anti-pattern list") },
				{ "sad.util.CodeSmellRepository::Err_LOAD_CODE_SMELL",
						new String("Cannot load code-smell list") },

				{ "ptidej.viewer.ui.panel.ToolPanel::PNL_UML_TITLE",
						new String("UML") },
				{ "ptidej.viewer.ui.panel.ToolPanel::PNL_MODELS_TITLE",
						new String("Models") },
				{ "ptidej.viewer.ui.panel.ToolPanel::PNL_OTHER_TOOLS_TITLE",
						new String("Other Tools") },
				{ "ptidej.viewer.widget.LookAndFeelMenu::MENU_LOOKANDFEELS",
						new String("Look and feels") },
				{
						"ptidej.viewer.widget.LookAndFeelMenu::MENU_LOOKANDFEELS_MNEMONIC",
						new String("o") },
				{ "ptidej.viewer.ui.about.AboutDialog::FRM_ABOUT_TITLE",
						new String("About the Ptidej Suite") },
				{ "ptidej.viewer.ui.about.AboutDialog::PTIDEJ_LOGO_ICON",
						new String("PtidejSplash.png") },
				{
						"ptidej.viewer.ui.about.AboutDialog::PTIDEJ_LOGO_FOOTER_ICON",
						new String("PtidejFooter.png") },
				{ "ptidej.viewer.ui.about.AboutDialog::TAB_PTIDEJ_TITLE",
						new String("Ptidej") },
				{ "ptidej.viewer.ui.about.AboutDialog::TAB_DEVELOPPERS_TITLE",
						new String("Developpers") },
				{ "ptidej.viewer.ui.about.AboutDialog::TAB_COPYRIGHT_TITLE",
						new String("Copyright") },
				{ "ptidej.viewer.ui.about.AboutDialog::TAB_TOOLS_SUITE_TITLE",
						new String("Tools Suite") },
				{ "ptidej.viewer.ui.splash.SplashScreen::FRM_ABOUT_TITLE",
						new String("About the Ptidej Suite") },
				{ "ptidej.viewer.ui.splash.SplashScreen::PTIDEJ_LOGO_ICON",
						new String("PtidejSplash.png") },
				{
						"ptidej.viewer.ui.splash.SplashScreen::PTIDEJ_LOGO_FOOTER_ICON",
						new String("PtidejFooter.png") },
				{ "ptidej.viewer.ui.splash.SplashScreen::TAB_PTIDEJ_TITLE",
						new String("Ptidej") },
				{
						"ptidej.viewer.ui.splash.SplashScreen::TAB_DEVELOPPERS_TITLE",
						new String("Developpers") },
				{ "ptidej.viewer.ui.splash.SplashScreen::TAB_COPYRIGHT_TITLE",
						new String("Copyright") },
				{
						"ptidej.viewer.ui.splash.SplashScreen::TAB_TOOLS_SUITE_TITLE",
						new String("Tools Suite") },
				{
						"ptidej.viewer.ui.splash.SplashScreen::RICH_TXT_GEODES",
						new String(
							"<center><b>GEODES</b> - Group Of Open and Distributed<Br>Systems, Experimental Software Engineering<Br>Department of Informatics ans Operations Research<Br>University of Montreal</center>") },
				{
						"ptidej.viewer.ui.about.AboutDialog::RICH_TXT_PTIDEJ",
						new String(
							"<html><body>The <b>Ptidej tool suite</b> is a set of tools to evaluate and to enhance the quality of object-"
									+ "oriented programs,<Br>promoting the use of patterns, either at the language-, design-, or "
									+ "architectural-level."
									+ "<p>The Ptidej tool suite (Pattern Trace Identification, Detection, and Enhancement in Java)<Br>"
									+ "by Yann-Gaël Guéhéneuc uses the PADL meta-model (Pattern and Abstract-level "
									+ "Description Language),<Br>extension of the PDL meta-model (Pattern Description Language)<Br>"
									+ "by Hervé Albin-Amiot.</p>"
									+ "<p>Get more information at <a href=\"http://www.ptidej.net\">www.ptidej.net</a>.<Br>"
									+ "Send comments and questions to <a href=\"mailto:guehene@iro.umontreal.ca\">guehene@iro.umontreal.ca</a>.</p></body></html>") },
				{
						"ptidej.viewer.ui.about.AboutDialog::RICH_TXT_DEVELOPPERS",
						new String(
							"<html><body><b>Main developpers:</b>"
									+ "<blockquote>- <i>Yann-Gaël Guéhéneuc</i>"
									+ "<blockquote>Université de Montréal<br>"
									+ "Ecole des Mines de Nantes<br>"
									+ "Object Technology International, Inc.</blockquote>"
									+ "- <i>Hervé Albin-Amiot.</i>"
									+ "<blockquote>Ecole des Mines de Nantes<br>"
									+ "Softmaint S.A.</blockquote></blockquote>"
									+ "<hr><b>With contributions by:</b><blockquote>"
									+

									"- <i>Naouel Moha</i>"
									+ "<blockquote>Classification, Identification, and Correction of Design Defects<br>"
									+ "Ph.D. student, in progress<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Foutse Khomh</i>"
									+ "<blockquote>Patterns and Quality of Object-oriented Programs<br>"
									+ "Ph.D. student, in progress<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Saliha Bouden</i>"
									+ "<blockquote>Refactorings and Traceability of Models Transformation<br>"
									+ "Master student, December 2006<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Jean-Yves Guyomarc'h</i>"
									+ "<blockquote>Metrics for Aspect-Oriented Programs<br>"
									+ "Master student, April 2006<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Olivier Kaczor</i>"
									+ "<blockquote>Efficient Pattern Identification<br>"
									+ "Master student, August 2005<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Khashayar Khosravi</i>"
									+ "<blockquote>Design Pattern-Enabled Object-Oriented Metrics<br>"
									+ "Master student, August 2005<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Samah Rached</i>"
									+ "<blockquote>Program Behaviour Analysis with Adjacency Matrices and Formal Concept Analysis<br>"
									+ "Master student, August 2005<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Mohamed Kahla</i>"
									+ "<blockquote>Algorithms to lay-out Class Diagrams<br>"
									+ "Undergraduate, Summer 2006<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Pierre Leduc</i>"
									+ "<blockquote>A Language to Define Design Defects and"
									+ "to Generate Detection Algorithms Automatically<br>"
									+ "Undergraduate, Summer 2006<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Pierre Leduc and Julien Tanteri</i>"
									+ "<blockquote>Implementing Micro-patterns in Ptidej"
									+ "Undergraduates, Winter 2006<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Mehdi Lahlou</i>"
									+ "<blockquote>On the Ptidej UI Standalone Swing Interface and<br>"
									+ "Multilanguage Managing<br>"
									+ "Undergraduate, Summer 2005<br>"
									+ "Université de Montréal.</blockquote>"
									+

									"- <i>Jean-Yves Guyomarc'h</i>"
									+ "<blockquote>On the Ptidej Solver in Java and metrics<br>"
									+ "Graduate, fall 2004<br>"
									+ "Université de Montréal.</blockquote>"
									+ "- <i>Lulzim Laloshi and Driton Salihu</i>"
									+ "<blockquote>On the Eclipse plug-in<br>"
									+ "Undergraduates, Summer 2004<br>"
									+ "Université de Montréal.</blockquote>"
									+ "- <i>Ward Flores and Sébastien Robidoux</i>"
									+ "<blockquote>On the C++ creator and PADL<br>"
									+ "Undergraduates, Summer 2004<br>"
									+ "Université de Montréal.</blockquote>"
									+ "- <i>Salime Bensemmane, Iyadh Sidhom, and Fayéal Skhiri</i>"
									+ "<blockquote>On the Ptidej Solver in Java<br>"
									+ "Undergraduates, Summer 2004<br>"
									+ "Université de Montréal.</blockquote>"
									+ "- <i>Farouk Zaidi</i>"
									+ "<blockquote>On the Java classfile creator and metrics<br>"
									+ "Trainee, Winter 2004<br>"
									+ "Université de technologie Belfort-Montbéliard<br>"
									+ "Centre de Recherche en Informatique de Montréal<br>"
									+ "Université de Montréal.</blockquote></body></html>") },
				{
						"ptidej.viewer.ui.about.AboutDialog::RICH_TXT_COPYRIGHT",
						new String(
							"<html><body><p align='center'><b>The Ptidej tool suite, copyright (c) 2000-2007,<br>"
									+ "Université de Montréal,<br>"
									+ "Yann-Gaël Guéhéneuc.<br>"
									+ "All right reserved.</p></b>"
									+ "<p color='red'>This software is made available AS IS, and THE AUTHORS DISCLAIM ALL WARRANTIES,<br>"
									+ "EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES<br>"
									+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND NOT<br>"
									+ "WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN ANY LIABILITY FOR<br>"
									+ "DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS EXPRESSLY<br>"
									+ "DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING NEGLIGENCE)<br>"
									+ "OR STRICT, LIABILITY EVEN IF THE AUTHORS ARE ADVISED OF THE POSSIBILITY<br>"
									+ "OF SUCH DAMAGES.<p><body><html>") },
				{
						"ptidej.viewer.ui.about.AboutDialog::RICH_TXT_TOOLS_SUITE",
						new String("<html><body>" + "- Ptidej UI Viewer v4.0"
								+ "<blockquote>" + "- CPL<br>" + "- EPI<br>"
								+ "- JChoco<br>"
								+ "- Metrical Ptidej Solver<br>" + "- PADL<br>"
								+ "- PADL Analyses<br>"
								+ "- PADL Creator AOL<br>"
								+ "- PADL Creator AspectJ<br>"
								+ "- PADL Creator C++<br>"
								+ "- PADL Creator ClassFile<br>"
								+ "- PADL Micro-pattern Analysis<br>"
								+ "- POM<br>" + "- Ptidej<br>"
								+ "- Ptidej Solver 4<br>" + "- Ptidej UI<br>"
								+ "- Ptidej UI Analyses<br>"
								+ "- Ptidej UI AspectJ<br>"
								+ "- Ptidej UI C++<br>"
								+ "- Ptidej UI Primitives AWT<br>"
								+ "- Ptidej UI Primitives SWT<br>"
								+ "- Ptidej UI Viewer"
								+ "- Ptidej UI Viewer Extensions<br>"
								+ "- Ptidej UI Viewer Standalone Swing<br>"
								+ "- SAD<br>" + "- SAD Rules<br>"
								+ "</blockquote>" + "</body></html>") } };

	public Object[][] getContents() {
		return PtidejResourceBundle.CONTENTS;
	}
}