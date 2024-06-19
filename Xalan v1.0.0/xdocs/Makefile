#
# Makefile for Xalan documentation
#
# Uses a specific version of Stylebook currently to create the hand-written
# HTML docs and some of the input docs to the javadoc API files
# 
# NOTE: documentation (only) will build only with JDK 1.2.2 or higher
# You can compile the src code with JDK 1.1.x and the doc with 1.2.x if:
#    - you have both installed
#    - your default environment is set for JDK 1.1.x
#    - you change the JAVA12 and JAVADOC12 variables from ../make.include to 
#      point directly to the 1.2.x executables
#    Example: on WinXX platforms, paths are like JAVA12=c:/jdk1.2.2/bin/java.exe
#

include ../make.include

# Add other required dirs to the classpath
XERCES_CLASSPATH := ..$(PATHSEP)..$(PATHSEP)xml-xerces$(PATHSEP)java$(PATHSEP)bin$(PATHSEP)xerces.jar
BSF_CLASSPATH := ..$(PATHSEP)bsf.jar$(CLPATHSEP)..$(PATHSEP)bsfengines.jar
XALAN_CLASSPATH := ..$(PATHSEP)$(JARNAME)

# Until we update to use Cocoon, we have a hard-coded version of Stylebook that works with this Xalan
# Also hack in a simple doclet used for javadoc later on
STYLEBOOOK_CLASSPATH := xalanjdoc.jar$(CLPATHSEP)stylebook-1.0-b2.jar
EXTRA_CLASSPATH := $(XERCES_CLASSPATH)$(CLPATHSEP)$(XALAN_CLASSPATH)$(CLPATHSEP)$(BSF_CLASSPATH)$(CLPATHSEP)$(STYLEBOOOK_CLASSPATH)$(CLPATHSEP)$(CLASSPATH)
STYLEBOOK := org.apache.stylebook.StyleBook

# Miscellaneous utility variables
OUTPUTDIR := ../build
SRCDIR    := ../src
JAVADOCARGFILE := javadocArgs.txt

all: getxmlsite makesbook makejavadoc

# Untar-gz the subset of xml-site files we require to build
# Note that these get left in the xdocs directory even after a clean
.PHONY: getxmlsite
getxmlsite:
	echo Untar-gzing the xml-site subset files
	-$(UNTARGZ) xml-site-style.tar.gz

# Note these paths should work on WinXX or Unix-land
makesbook: getxmlsite
	echo Setting up for stylebook into $(OUTPUTDIR)
	-mkdir $(OUTPUTDIR)
	-mkdir $(OUTPUTDIR)/docs
	-mkdir $(OUTPUTDIR)/docs/resources
	-$(CP) style/resources/* $(OUTPUTDIR)/docs/resources
	echo Making stylebook into $(OUTPUTDIR)
	$(JAVA12) -classpath "$(EXTRA_CLASSPATH)" $(STYLEBOOK) "targetDirectory=$(OUTPUTDIR)/docs/" sources/xalanLocal.xml style

#To build javadoc from the xdocs directory
makejavadoc: getxmlsite
	echo Setting up for javadoc1.2 into $(OUTPUTDIR)
	-mkdir $(OUTPUTDIR)
	-mkdir $(OUTPUTDIR)/docs
	-mkdir $(OUTPUTDIR)/docs/apidocs
	$(JAVA12) -classpath "$(EXTRA_CLASSPATH)" $(STYLEBOOK) "loaderConfig=sbk:/style/loaderjdoc.xml" targetDirectory=$(SRCDIR) sources/javadocPackages.xml style
	echo -doclet xalanjdoc.Standard -docletpath xalanjdoc.jar -public -overview $(SRCDIR)/javadocOverview.html -sourcepath $(SRCDIR) -group XSLT_Packages "org.apache.xalan.xslt*" -group XPATH_Packages "org.apache.xalan.xpath*" org.apache.xalan.xpath org.apache.xalan.xpath.xdom org.apache.xalan.xpath.dtm org.apache.xalan.xpath.xml org.apache.xalan.xslt org.apache.xalan.xslt.trace org.apache.xalan.xslt.client org.apache.xalan.xslt.extensions -windowtitle Xalan -d $(OUTPUTDIR)/docs/apidocs > $(JAVADOCARGFILE)
	echo Making javadoc1.2 into $(OUTPUTDIR)
	$(JAVADOC12) -classpath "$(EXTRA_CLASSPATH)" @$(JAVADOCARGFILE)

.PHONY: clean
# In theory we should also clean out the getxmlsite-generated files,
clean::
	$(RM-RF) $(OUTPUTDIR)/docs/*
	$(RM) $(JAVADOCARGFILE)