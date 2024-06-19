/*
 * (c) Copyright 2001-2004 Yann-Gaël Guéhéneuc,
 * University of Montréal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package dpl;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/04/21
 */
public interface XMLConstants {
	String XMLHEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";

	String ORIGIN_XML_FILE = "rsc/xml/Design Pattern List.xml";
	String ORIGIN_XML_FILE_LOG = "rsc/xml/Design Pattern List.log";
	String TARGET_XML_FILE = "rsc/xml/Metric Design Pattern List.xml";
	String TARGET_XML_FILE_LOG = "rsc/xml/Metric Design Pattern List.log";
	String FOUNDATION_XML_FILE = "rsc/xml/Quality Pattern List.xml";
	String FOUNDATION_XML_FILE_LOG = "rsc/xml/Quality Pattern List.log";
	String OBJECTIVE_XML_FILE = "rsc/xml/Metric Design Pattern List.xml";
	String OBJECTIVE_XML_FILE_LOG = "rsc/xml/Metric Design Pattern List.log";
	String GOAL_XML_FILE = "rsc/xml/Metric Quality Design Pattern List.xml";
	String GOAL_XML_FILE_LOG = "rsc/xml/Metric Quality Design Pattern List.log";
	String TARGET_TAB_FILE = "rsc/xml/Metrics.tab.txt";
	String STATISTICS_XML_FILE_LOG = "rsc/xml/Design Pattern List.log";

	String ROLE_STRUCTURE = "roleStructure";
	String PROPERTIES = "properties";
	String PROPERTY = "property";
	String RELATIONS = "relations";
	String RELATION = "relation";
	String TARGET = "target";
	String DESIGN_PATTERNS = "designPatterns";
	String DESIGN_PATTERN = "designPattern";
	String TYPE = "type";
	String PROGRAM = "program";
	String NAME = "name";
	String MICRO_ARCHITECTURES = "microArchitectures";
	String MICRO_ARCHITECTURE = "microArchitecture";
	String ENTITY = "entity";
	String NUMBER = "number";
	String ROLES = "roles";
	String COMMENT = "comment";
	String TEXT = "#text";
	String VALUE = "value";
	String ROLE_KIND = "roleKind";
	String KIND = "kind";

	String KIND_CLASS = "Class";
	String KIND_ABSTRACT_CLASS = "AbstractClass";
	String KIND_INTERFACE = "Interface";
	String KIND_GHOST = "Ghost";
	
	String CBO = "CBO";
}
