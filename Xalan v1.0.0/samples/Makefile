###########################################################
# Basic makefile for Xalan Samples
#
# NOTE: You must have a Servlet SDK in your classpath to 
#       compile the servlet sample!
# You can compile the rest of the samples without it, tho.
#
###########################################################

# Include common targets and variables
include ../make.include

SINCE_FILE = samples.last

SAMPLES_JAR = xalansamples$(JARX)

SRCS = ApplyXPath$(PATHSEP)ApplyXPath$(JAVAX) \
		ApplyXPath$(PATHSEP)XPathAPI$(JAVAX) \
		Extensions$(PATHSEP)IntDate$(JAVAX) \
		Extensions$(PATHSEP)MyCounter$(JAVAX) \
		Pipe$(PATHSEP)Pipe$(JAVAX) \
		PureSAX$(PATHSEP)PureSAX$(JAVAX) \
		Servlet$(PATHSEP)ApplyXSL$(JAVAX) \
		Servlet$(PATHSEP)ApplyXSLException$(JAVAX) \
		Servlet$(PATHSEP)ApplyXSLListener$(JAVAX) \
		Servlet$(PATHSEP)ApplyXSLProperties$(JAVAX) \
		Servlet$(PATHSEP)DefaultApplyXSL$(JAVAX) \
		Servlet$(PATHSEP)DefaultApplyXSLProperties$(JAVAX) \
		SimpleTransform$(PATHSEP)SimpleTransform$(JAVAX) \
		TransformToDom$(PATHSEP)TransformToDom$(JAVAX) \
		UseStylesheetParam$(PATHSEP)UseStylesheetParam$(JAVAX)

			   
# Add other required dirs to the classpath, including the Xalan jar!
XERCES_CLASSPATH := ..$(PATHSEP)..$(PATHSEP)xml-xerces$(PATHSEP)java$(PATHSEP)bin$(PATHSEP)xerces.jar
EXTRA_CLASSPATH := ..$(PATHSEP)$(JARNAME)$(CLPATHSEP)$(XERCES_CLASSPATH)$(CLPATHSEP)..$(PATHSEP)bsf.jar$(CLPATHSEP)$(CLASSPATH)

all: $(SAMPLES_JAR)

$(SAMPLES_JAR): $(SRCS)
	-$(JAVAC) -d . -classpath "$(EXTRA_CLASSPATH)" $?
	$(JAR) $(JARFLAGS) $(SAMPLES_JAR) *

clean::
	$(RM) *.class
	$(RM) $(SINCE_FILE)
	$(RM) $(SAMPLES_JAR)