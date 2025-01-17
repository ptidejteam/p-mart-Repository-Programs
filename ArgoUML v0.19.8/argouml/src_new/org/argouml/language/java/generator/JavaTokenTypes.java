// $ANTLR 2.7.2a2 (20020112-1): "../src_new/org/argouml/language/java/generator/java.g" -> "JavaLexer.java"$

	package org.argouml.language.java.generator;

	import java.util.Vector;

public interface JavaTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int BLOCK = 4;
	int MODIFIERS = 5;
	int OBJBLOCK = 6;
	int SLIST = 7;
	int CTOR_DEF = 8;
	int METHOD_DEF = 9;
	int VARIABLE_DEF = 10;
	int INSTANCE_INIT = 11;
	int STATIC_INIT = 12;
	int TYPE = 13;
	int CLASS_DEF = 14;
	int INTERFACE_DEF = 15;
	int PACKAGE_DEF = 16;
	int ARRAY_DECLARATOR = 17;
	int EXTENDS_CLAUSE = 18;
	int IMPLEMENTS_CLAUSE = 19;
	int PARAMETERS = 20;
	int PARAMETER_DEF = 21;
	int LABELED_STAT = 22;
	int TYPECAST = 23;
	int INDEX_OP = 24;
	int POST_INC = 25;
	int POST_DEC = 26;
	int METHOD_CALL = 27;
	int EXPR = 28;
	int ARRAY_INIT = 29;
	int IMPORT = 30;
	int UNARY_MINUS = 31;
	int UNARY_PLUS = 32;
	int CASE_GROUP = 33;
	int ELIST = 34;
	int FOR_INIT = 35;
	int FOR_CONDITION = 36;
	int FOR_ITERATOR = 37;
	int EMPTY_STAT = 38;
	int FINAL = 39;
	int ABSTRACT = 40;
	int LITERAL_package = 41;
	int SEMI = 42;
	int LITERAL_import = 43;
	int JAVADOC = 44;
	int LBRACK = 45;
	int RBRACK = 46;
	int LITERAL_void = 47;
	int LITERAL_boolean = 48;
	int LITERAL_byte = 49;
	int LITERAL_char = 50;
	int LITERAL_short = 51;
	int LITERAL_int = 52;
	int LITERAL_float = 53;
	int LITERAL_long = 54;
	int LITERAL_double = 55;
	int IDENT = 56;
	int DOT = 57;
	int STAR = 58;
	int LITERAL_private = 59;
	int LITERAL_public = 60;
	int LITERAL_protected = 61;
	int LITERAL_static = 62;
	int LITERAL_transient = 63;
	int LITERAL_native = 64;
	int LITERAL_synchronized = 65;
	int LITERAL_volatile = 66;
	int LITERAL_strictfp = 67;
	int LITERAL_class = 68;
	int LITERAL_extends = 69;
	int LITERAL_interface = 70;
	int LCURLY = 71;
	int RCURLY = 72;
	int COMMA = 73;
	int LITERAL_implements = 74;
	int LPAREN = 75;
	int RPAREN = 76;
	int ASSIGN = 77;
	int LITERAL_throws = 78;
	int COLON = 79;
	int LITERAL_if = 80;
	int LITERAL_else = 81;
	int LITERAL_for = 82;
	int LITERAL_while = 83;
	int LITERAL_do = 84;
	int LITERAL_break = 85;
	int LITERAL_continue = 86;
	int LITERAL_return = 87;
	int LITERAL_switch = 88;
	int LITERAL_throw = 89;
	int LITERAL_case = 90;
	int LITERAL_default = 91;
	int LITERAL_try = 92;
	int LITERAL_finally = 93;
	int LITERAL_catch = 94;
	int PLUS_ASSIGN = 95;
	int MINUS_ASSIGN = 96;
	int STAR_ASSIGN = 97;
	int DIV_ASSIGN = 98;
	int MOD_ASSIGN = 99;
	int SR_ASSIGN = 100;
	int BSR_ASSIGN = 101;
	int SL_ASSIGN = 102;
	int BAND_ASSIGN = 103;
	int BXOR_ASSIGN = 104;
	int BOR_ASSIGN = 105;
	int QUESTION = 106;
	int LOR = 107;
	int LAND = 108;
	int BOR = 109;
	int BXOR = 110;
	int BAND = 111;
	int NOT_EQUAL = 112;
	int EQUAL = 113;
	int LT = 114;
	int GT = 115;
	int LE = 116;
	int GE = 117;
	int LITERAL_instanceof = 118;
	int SL = 119;
	int SR = 120;
	int BSR = 121;
	int PLUS = 122;
	int MINUS = 123;
	int DIV = 124;
	int MOD = 125;
	int INC = 126;
	int DEC = 127;
	int BNOT = 128;
	int LNOT = 129;
	int LITERAL_this = 130;
	int LITERAL_super = 131;
	int LITERAL_true = 132;
	int LITERAL_false = 133;
	int LITERAL_null = 134;
	int LITERAL_new = 135;
	int NUM_INT = 136;
	int CHAR_LITERAL = 137;
	int STRING_LITERAL = 138;
	int NUM_FLOAT = 139;
	int WS = 140;
	int SL_COMMENT = 141;
	int ML_COMMENT = 142;
	int ESC = 143;
	int HEX_DIGIT = 144;
	int VOCAB = 145;
	int EXPONENT = 146;
	int FLOAT_SUFFIX = 147;
}
