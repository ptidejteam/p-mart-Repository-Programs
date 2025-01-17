package ptidej.statement.creator.classfiles.conditionals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.classfile.Visitor;
import padl.kernel.IConstituentOfOperation;
import padl.kernel.impl.StatementFactory;
import util.io.Output;

/**
 * @author St�phane Vaucher
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2006/03/09
 */
public class BCELInstructionFinder implements Visitor {
	private static final Pattern INSTRUCTION_PATTERN =
		Pattern.compile("^\\d+: *");
	private BCEL2PADLAdaptor adaptor;
	private JavaClass currentClass;
	private final Map instructionMap = new HashMap();

	/**
	 * @return Returns the adaptor.
	 */
	public BCEL2PADLAdaptor getAdaptor() {
		return this.adaptor;
	}
	public IConstituentOfOperation[] getConditionalInstructions(
		final String aClassId,
		final String aMethodId) {

		final Map methodMap = this.methodMap(aClassId);
		final List conditionalInstructions = (List) methodMap.get(aMethodId);
		if (conditionalInstructions == null) {
			Output.getInstance().errorOutput().print("Method: ");
			Output.getInstance().errorOutput().print(aClassId);
			Output.getInstance().errorOutput().print('.');
			Output.getInstance().errorOutput().print(aMethodId);
			Output.getInstance().errorOutput().println(
				" does not have instructions?!");
			return new IConstituentOfOperation[0];
		}
		else {
			final IConstituentOfOperation[] arrayOfConditionalInstructions =
				new IConstituentOfOperation[conditionalInstructions.size()];
			conditionalInstructions.toArray(arrayOfConditionalInstructions);
			return arrayOfConditionalInstructions;
		}
	}
	private boolean isInstruction(final String aLine) {
		final Matcher matcher = INSTRUCTION_PATTERN.matcher(aLine);
		return matcher.find();
	}
	private Map methodMap(final String aClassId) {
		Map methodMap = (Map) this.instructionMap.get(aClassId);

		if (methodMap == null) {
			methodMap = new HashMap();
			this.instructionMap.put(aClassId, methodMap);
		}

		return methodMap;
	}
	/**
	 * @param adaptor The adaptor to set.
	 */
	public void setAdaptor(final BCEL2PADLAdaptor anAdaptor) {
		this.adaptor = anAdaptor;
	}
	public String toString() {
		return this.instructionMap.toString();
	}
	public void visitCode(final Code aCode) {
	}
	public void visitCodeException(final CodeException aCodeException) {
	}
	public void visitConstantClass(final ConstantClass aConstantClass) {
	}
	public void visitConstantDouble(final ConstantDouble aConstantDouble) {
	}
	public void visitConstantFieldref(final ConstantFieldref aConstantFieldref) {
	}
	public void visitConstantFloat(final ConstantFloat aConstantFloat) {
	}
	public void visitConstantInteger(final ConstantInteger aConstantInteger) {
	}
	public void visitConstantInterfaceMethodref(
		final ConstantInterfaceMethodref aConstantInterfaceMethodref) {
	}
	public void visitConstantLong(final ConstantLong aConstantLong) {
	}
	public void visitConstantMethodref(
		final ConstantMethodref aConstantMethodref) {
	}
	public void visitConstantNameAndType(
		final ConstantNameAndType aConstantNameAndType) {
	}
	public void visitConstantPool(final ConstantPool aConstantPool) {
	}
	public void visitConstantString(final ConstantString aConstantString) {
	}
	public void visitConstantUtf8(final ConstantUtf8 aConstantUtf8) {
	}
	public void visitConstantValue(final ConstantValue aConstantValue) {
	}
	public void visitDeprecated(final Deprecated aDeprecated) {
	}
	public void visitExceptionTable(final ExceptionTable aExceptionTable) {
	}
	public void visitField(final Field aField) {
	}
	public void visitInnerClass(final InnerClass anInnerClass) {
	}
	public void visitInnerClasses(final InnerClasses someInnerClasses) {
	}
	public void visitJavaClass(final JavaClass aClass) {
		this.currentClass = aClass;
		final Method[] methods = aClass.getMethods();

		for (int i = 0; i < methods.length; i++) {
			this.visitMethod(methods[i]);
		}
	}
	public void visitLineNumber(final LineNumber aLineNumber) {
	}
	public void visitLineNumberTable(final LineNumberTable aLineNumberTable) {
	}
	public void visitLocalVariable(final LocalVariable aLocalVariable) {
	}
	public void visitLocalVariableTable(
		final LocalVariableTable aLocalVariableTable) {
	}
	public void visitMethod(final Method aMethod) {
		// Yann 2008/10/01: The Trick!
		// This is were the instances of ConditionalInstruction are being
		// instantiated according to the content of the method body...
		final List conditionalInstructions = new ArrayList();
		final String ckey = this.adaptor.adapt(this.currentClass);
		final String mkey = this.adaptor.adapt(this.currentClass, aMethod);
		final Map methodMap = this.methodMap(ckey);
		try {
			final Code code = aMethod.getCode();
			if (code != null) {
				final String codeText = code.toString();
				final BufferedReader reader =
					new BufferedReader(new StringReader(codeText));

				String line = reader.readLine();
				do {
					if (this.isInstruction(line)) {
						if (line.indexOf("if") > -1) {
							conditionalInstructions
								.add(((StatementFactory) StatementFactory
									.getInstance()).createIfInstruction());
						}
						else if (line.indexOf("switch") > -1) {
							final int parenthesisIndex = line.indexOf('(');
							final int range =
								line.substring(parenthesisIndex).split(",").length;
							conditionalInstructions
								.add(((StatementFactory) StatementFactory
									.getInstance())
									.createSwitchInstruction(range));
						}
					}
					line = reader.readLine();
				}
				while (line != null);
			}
		}
		catch (final IOException ioe) {
			ioe.printStackTrace(Output.getInstance().errorOutput());
		}
		methodMap.put(mkey, conditionalInstructions);
	}
	public void visitSignature(final Signature arg0) {
	}
	public void visitSourceFile(final SourceFile aSourceFile) {
	}
	public void visitStackMap(final StackMap aStackMap) {
	}
	public void visitStackMapEntry(final StackMapEntry aStackMapEntry) {
	}
	public void visitSynthetic(final Synthetic aSynthetic) {
	}
	public void visitUnknown(final Unknown aUnknown) {
	}
}
