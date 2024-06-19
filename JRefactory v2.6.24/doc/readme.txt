This document was divided into 3 documents.

If you intend to use this software from the command line
look at commandline.txt.

If you intend to use this software with Elixir IDE, look at
elixir.txt.

If you intend to use this software with JBuilder, look at
jbuilder.txt.


The license is in license.txt


*Note*:  The parser has been upgraded to handle Java 1.2's
strictfp keyword.  A side effect of this is that the
JDK.stub file is incorrect.  If you don't like the annoying
error warnings on startup, delete JDK.stub in <your home>/.refactory
and restart your application.


If you would like to extend this software, please look at the
javadoc comments in the source code.  Expand the files in
source.jar, and then run the following two commands:

java org.acm.seguin.tools.build.JavadocBuilder package.lst .
javadoc -author -version -d c:\web\doc @package.lst *.java

Thanks for trying this software!

Chris
