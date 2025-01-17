/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;

import java.io.StringReader;

public class SourceCodeTest extends TestCase {

    public void testSimple() throws Throwable {
        String code = MatchAlgorithmTest.getSampleCode();
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("Foo.java");
        assertEquals("Foo.java", sourceCode.getFileName());
		sourceCode.readSource(new StringReader(code));
        tokenizer.tokenize(sourceCode, new Tokens());

        assertEquals(MatchAlgorithmTest.LINE_1, sourceCode.getSlice(1,1));
        assertEquals(MatchAlgorithmTest.LINE_2, sourceCode.getSlice(2,2));
        assertEquals(MatchAlgorithmTest.LINE_1 + PMD.EOL + MatchAlgorithmTest.LINE_2, sourceCode.getSlice(1,2));
    }
}
