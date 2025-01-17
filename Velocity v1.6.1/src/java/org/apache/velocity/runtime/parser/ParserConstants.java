/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package org.apache.velocity.runtime.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LBRACKET = 1;
  /** RegularExpression Id. */
  int RBRACKET = 2;
  /** RegularExpression Id. */
  int COMMA = 3;
  /** RegularExpression Id. */
  int DOUBLEDOT = 4;
  /** RegularExpression Id. */
  int COLON = 5;
  /** RegularExpression Id. */
  int LEFT_CURLEY = 6;
  /** RegularExpression Id. */
  int RIGHT_CURLEY = 7;
  /** RegularExpression Id. */
  int LPAREN = 8;
  /** RegularExpression Id. */
  int RPAREN = 9;
  /** RegularExpression Id. */
  int REFMOD2_RPAREN = 10;
  /** RegularExpression Id. */
  int ESCAPE_DIRECTIVE = 11;
  /** RegularExpression Id. */
  int SET_DIRECTIVE = 12;
  /** RegularExpression Id. */
  int DOLLAR = 13;
  /** RegularExpression Id. */
  int DOLLARBANG = 14;
  /** RegularExpression Id. */
  int HASH = 17;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT_START = 18;
  /** RegularExpression Id. */
  int DOUBLE_ESCAPE = 19;
  /** RegularExpression Id. */
  int ESCAPE = 20;
  /** RegularExpression Id. */
  int TEXT = 21;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 22;
  /** RegularExpression Id. */
  int FORMAL_COMMENT = 23;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 24;
  /** RegularExpression Id. */
  int WHITESPACE = 26;
  /** RegularExpression Id. */
  int STRING_LITERAL = 27;
  /** RegularExpression Id. */
  int TRUE = 28;
  /** RegularExpression Id. */
  int FALSE = 29;
  /** RegularExpression Id. */
  int NEWLINE = 30;
  /** RegularExpression Id. */
  int MINUS = 31;
  /** RegularExpression Id. */
  int PLUS = 32;
  /** RegularExpression Id. */
  int MULTIPLY = 33;
  /** RegularExpression Id. */
  int DIVIDE = 34;
  /** RegularExpression Id. */
  int MODULUS = 35;
  /** RegularExpression Id. */
  int LOGICAL_AND = 36;
  /** RegularExpression Id. */
  int LOGICAL_OR = 37;
  /** RegularExpression Id. */
  int LOGICAL_LT = 38;
  /** RegularExpression Id. */
  int LOGICAL_LE = 39;
  /** RegularExpression Id. */
  int LOGICAL_GT = 40;
  /** RegularExpression Id. */
  int LOGICAL_GE = 41;
  /** RegularExpression Id. */
  int LOGICAL_EQUALS = 42;
  /** RegularExpression Id. */
  int LOGICAL_NOT_EQUALS = 43;
  /** RegularExpression Id. */
  int LOGICAL_NOT = 44;
  /** RegularExpression Id. */
  int EQUALS = 45;
  /** RegularExpression Id. */
  int END = 46;
  /** RegularExpression Id. */
  int IF_DIRECTIVE = 47;
  /** RegularExpression Id. */
  int ELSEIF_DIRECTIVE = 48;
  /** RegularExpression Id. */
  int ELSE_DIRECTIVE = 49;
  /** RegularExpression Id. */
  int STOP_DIRECTIVE = 50;
  /** RegularExpression Id. */
  int DIGIT = 51;
  /** RegularExpression Id. */
  int INTEGER_LITERAL = 52;
  /** RegularExpression Id. */
  int FLOATING_POINT_LITERAL = 53;
  /** RegularExpression Id. */
  int EXPONENT = 54;
  /** RegularExpression Id. */
  int LETTER = 55;
  /** RegularExpression Id. */
  int DIRECTIVE_CHAR = 56;
  /** RegularExpression Id. */
  int WORD = 57;
  /** RegularExpression Id. */
  int BRACKETED_WORD = 58;
  /** RegularExpression Id. */
  int ALPHA_CHAR = 59;
  /** RegularExpression Id. */
  int ALPHANUM_CHAR = 60;
  /** RegularExpression Id. */
  int IDENTIFIER_CHAR = 61;
  /** RegularExpression Id. */
  int IDENTIFIER = 62;
  /** RegularExpression Id. */
  int DOT = 63;
  /** RegularExpression Id. */
  int LCURLY = 64;
  /** RegularExpression Id. */
  int RCURLY = 65;
  /** RegularExpression Id. */
  int REFERENCE_TERMINATOR = 66;
  /** RegularExpression Id. */
  int DIRECTIVE_TERMINATOR = 67;

  /** Lexical state. */
  int DIRECTIVE = 0;
  /** Lexical state. */
  int REFMOD2 = 1;
  /** Lexical state. */
  int REFMODIFIER = 2;
  /** Lexical state. */
  int DEFAULT = 3;
  /** Lexical state. */
  int REFERENCE = 4;
  /** Lexical state. */
  int PRE_DIRECTIVE = 5;
  /** Lexical state. */
  int IN_MULTI_LINE_COMMENT = 6;
  /** Lexical state. */
  int IN_FORMAL_COMMENT = 7;
  /** Lexical state. */
  int IN_SINGLE_LINE_COMMENT = 8;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\"[\"",
    "\"]\"",
    "\",\"",
    "\"..\"",
    "\":\"",
    "\"{\"",
    "\"}\"",
    "\"(\"",
    "<RPAREN>",
    "\")\"",
    "<ESCAPE_DIRECTIVE>",
    "<SET_DIRECTIVE>",
    "<DOLLAR>",
    "<DOLLARBANG>",
    "<token of kind 15>",
    "\"#*\"",
    "\"#\"",
    "\"##\"",
    "\"\\\\\\\\\"",
    "\"\\\\\"",
    "<TEXT>",
    "<SINGLE_LINE_COMMENT>",
    "\"*#\"",
    "\"*#\"",
    "<token of kind 25>",
    "<WHITESPACE>",
    "<STRING_LITERAL>",
    "\"true\"",
    "\"false\"",
    "<NEWLINE>",
    "\"-\"",
    "\"+\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "<LOGICAL_AND>",
    "<LOGICAL_OR>",
    "<LOGICAL_LT>",
    "<LOGICAL_LE>",
    "<LOGICAL_GT>",
    "<LOGICAL_GE>",
    "<LOGICAL_EQUALS>",
    "<LOGICAL_NOT_EQUALS>",
    "<LOGICAL_NOT>",
    "\"=\"",
    "<END>",
    "<IF_DIRECTIVE>",
    "<ELSEIF_DIRECTIVE>",
    "<ELSE_DIRECTIVE>",
    "<STOP_DIRECTIVE>",
    "<DIGIT>",
    "<INTEGER_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<LETTER>",
    "<DIRECTIVE_CHAR>",
    "<WORD>",
    "<BRACKETED_WORD>",
    "<ALPHA_CHAR>",
    "<ALPHANUM_CHAR>",
    "<IDENTIFIER_CHAR>",
    "<IDENTIFIER>",
    "<DOT>",
    "\"{\"",
    "\"}\"",
    "<REFERENCE_TERMINATOR>",
    "<DIRECTIVE_TERMINATOR>",
  };

}
