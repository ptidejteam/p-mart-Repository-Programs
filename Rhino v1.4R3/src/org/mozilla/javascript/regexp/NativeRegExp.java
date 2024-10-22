/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public License
 * Version 1.0 (the "NPL"); you may not use this file except in
 * compliance with the NPL.  You may obtain a copy of the NPL at
 * http://www.mozilla.org/NPL/
 *
 * Software distributed under the NPL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the NPL
 * for the specific language governing rights and limitations under the
 * NPL.
 *
 * The Initial Developer of this code under the NPL is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-1999 Netscape Communications Corporation.  All Rights
 * Reserved.
 */

package org.mozilla.javascript.regexp;

import org.mozilla.javascript.*;
import java.lang.reflect.Method;

/**
 * This class implements the RegExp native object.
 *
 * Revision History:
 * Implementation in C by Brendan Eich
 * Initial port to Java by Norris Boyd from jsregexp.c version 1.36
 * Merged up to version 1.38, which included Unicode support.
 * Merged bug fixes in version 1.39.
 * Merged JSFUN13_BRANCH changes up to 1.32.2.13
 *
 * @author Brendan Eich
 * @author Norris Boyd
 */
public class NativeRegExp extends ScriptableObject implements Function {

    public static final int GLOB = 0x1;       // 'g' flag: global
    public static final int FOLD = 0x2;       // 'i' flag: fold
    public static final int MULTILINE = 0x4;  // 'm' flag: multiline

    private static final boolean debug = false;

    public static void init(Scriptable scope)
        throws PropertyException
    {
        NativeRegExp proto = new NativeRegExp();
        proto.setParentScope(scope);
        proto.setPrototype(getObjectPrototype(scope));

        String[] fns = { "compile", "toString", "exec", "test" };
        proto.defineFunctionProperties(fns, NativeRegExp.class,
                                       ScriptableObject.DONTENUM);

        String[] props =     
                { "lastIndex", "source", "global", "ignoreCase", "multiline" };
        int[] propAttrs =    
                {  ScriptableObject.PERMANENT,
                   ScriptableObject.PERMANENT |  ScriptableObject.READONLY,
                   ScriptableObject.PERMANENT |  ScriptableObject.READONLY,
                   ScriptableObject.PERMANENT |  ScriptableObject.READONLY,
                   ScriptableObject.PERMANENT |  ScriptableObject.READONLY };
        for (int i=0; i < props.length; i++) {
            proto.defineProperty(props[i], NativeRegExp.class, propAttrs[i]);
        }

        Scriptable ctor = NativeRegExpCtor.init(scope);
        ctor.put("prototype", ctor, proto);
    }

    public NativeRegExp(Scriptable scope, String source, String global) {
        init(scope, source, global);
    }

    public void init(Scriptable scope, String source, String global) {
        this.source = source;
        flags = 0;
        if (global != null) {
            for (int i=0; i < global.length(); i++) {
                char c = global.charAt(i);
                if (c == 'g') {
                    flags |= GLOB;
                } else if (c == 'i') {
                    flags |= FOLD;
                } else if (c == 'm') {
                    flags |= MULTILINE;
                } else {
                    Object[] errArgs = { new Character(c) };
                    throw Context.reportRuntimeError(ScriptRuntime.getMessage
                                                     ("msg.invalid.re.flag",
                                                      errArgs));
                }
            }
        }

        CompilerState state = new CompilerState(source, flags);
        this.ren = parseRegExp(state);
        if (ren == null) return;
        RENode end = new RENode(state, REOP_END, null);
        setNext(state, ren, end);
        if (debug)
            dumpRegExp(state, ren);
        this.lastIndex = 0;
        this.parenCount = state.parenCount;
        this.flags = flags;
/*
        anchorRegExp(state, ren);
        optimizeRegExp(state, ren);

        if (debug)
            dumpRegExp(state, ren);

        this.program = new byte[state.progLength];
        this.lastIndex = 0;
        this.parenCount = state.parenCount;
        this.flags = flags;

        state.progLength = 0;
        emitRegExp(state, ren);

        if (debug) {
            for (int i=0; i < program.length; i++) {
                int b = (program[i] & 0xff);
                System.err.print(b);
                if ((i > 0 && i % 8 == 0) || i == program.length-1)
                    System.err.println();
                else
                    System.err.print(", ");
            }
            System.err.println();
        }
*/
        scope = getTopLevelScope(scope);
        setPrototype(getClassPrototype(scope, "RegExp"));
        setParentScope(scope);
    }

    public String getClassName() {
        return "RegExp";
    }

    public Object call(Context cx, Scriptable scope, Scriptable thisObj,
                       Object[] args)
    {
        return execSub(cx, this, args, scope, false, this);
    }

    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        return (Scriptable) call(cx, scope, null, args);
    }

    public static Scriptable compile(Context cx, Scriptable thisVal,
                                     Object[] args, Function funObj)
    {
        NativeRegExp thisObj = (NativeRegExp) thisVal; // XXX check cast
        String s = args.length == 0 ? "" : ScriptRuntime.toString(args[0]);
        String global = args.length > 1 ? ScriptRuntime.toString(args[1])
                                        : null;
        thisObj.init(funObj, s, global);
        return thisObj;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('/');
        buf.append(source);
        buf.append('/');
        if ((flags & GLOB) != 0)
            buf.append('g');
        if ((flags & FOLD) != 0)
            buf.append('i');
        if ((flags & MULTILINE) != 0)
            buf.append('m');
	    return buf.toString();
    }

    /**
     * "lastIndex" property of RegExp instances (defined in prototype)
     */
    public int getLastIndex() {
        return lastIndex;
    }

    /**
     * "lastIndex" property of RegExp instances (defined in prototype)
     */
    public void setLastIndex(int i) {
        lastIndex = i;
    }

    /**
     * "source" property of RegExp instances (defined in prototype)
     */
    public String getSource() {
        return source;
    }

    /**
     * "global" property of RegExp instances (defined in prototype)
     */
    public boolean getGlobal() {
        return (flags & GLOB) != 0;
    }

    /**
     * "ignoreCase" property of RegExp instances (defined in prototype)
     */
    public boolean getIgnoreCase() {
        return (flags & FOLD) != 0;
    }

    /**
     * "multiline" property of RegExp instances (defined in prototype)
     */
    public boolean getMultiline() {
        return (flags & MULTILINE) != 0;
    }

    public NativeRegExp() {
    }
    
    private static RegExpImpl getImpl(Context cx) {
        return (RegExpImpl) ScriptRuntime.getRegExpProxy(cx);
    }

    private static Object execSub(Context cx, Scriptable thisObj,
                                  Object[] args, Scriptable scopeObj,
                                  boolean test, Function funObj)
    {
        if (!(thisObj instanceof NativeRegExp)) {
            Object[] errArgs = { ((NativeFunction) funObj).js_getName() };
            throw Context.reportRuntimeError(
                ScriptRuntime.getMessage("msg.incompat.call", errArgs));
        }
        NativeRegExp re = (NativeRegExp) thisObj;
        String str;
        if (args.length == 0) {
            str = getImpl(cx).input;
            if (str == null) {
                Object[] errArgs = { re.toString() };
                throw Context.reportRuntimeError(ScriptRuntime.getMessage
                                                 ("msg.no.re.input.for",
                                                  errArgs));
            }
        } else {
            str = ScriptRuntime.toString(args[0]);
        }
        int i;
        if ((re.flags & GLOB) != 0) {
            i = re.lastIndex;
        } else {
            i = 0;
        }
        int indexp[] = { i };
        Object rval = re.executeRegExp(scopeObj, str, indexp, test);
        if ((re.flags & GLOB) != 0)
            re.lastIndex = (rval == null) ? 0 : indexp[0];
        return rval;
    }

    public static Object exec(Context cx, Scriptable thisObj,
                              Object[] args, Function funObj)
    {
        return execSub(cx, thisObj, args, funObj, false, funObj);
    }

    public static Object test(Context cx, Scriptable thisObj,
                              Object[] args, Function funObj)
    {
        Object rval = execSub(cx, thisObj, args, funObj, true, funObj);
        if (rval == null || !rval.equals(Boolean.TRUE))
            rval = Boolean.FALSE;
        return rval;
    }


    static final int JS_BITS_PER_BYTE = 8;

    private static final byte REOP_EMPTY      = 0;  /* match rest of input against rest of r.e. */
    private static final byte REOP_ALT        = 1;  /* alternative subexpressions in kid and next */
    private static final byte REOP_BOL        = 2;  /* beginning of input (or line if multiline) */
    private static final byte REOP_EOL        = 3;  /* end of input (or line if multiline) */
    private static final byte REOP_WBDRY      = 4;  /* match "" at word boundary */
    private static final byte REOP_WNONBDRY   = 5;  /* match "" at word non-boundary */
    private static final byte REOP_QUANT      = 6;  /* quantified atom: atom{1,2} */
    private static final byte REOP_STAR       = 7;  /* zero or more occurrences of kid */
    private static final byte REOP_PLUS       = 8;  /* one or more occurrences of kid */
    private static final byte REOP_OPT        = 9;  /* optional subexpression in kid */
    private static final byte REOP_LPAREN     = 10; /* left paren bytecode: kid is u.num'th sub-regexp */
    private static final byte REOP_RPAREN     = 11; /* right paren bytecode */
    private static final byte REOP_DOT        = 12; /* stands for any character */
    private static final byte REOP_CCLASS     = 13; /* character class: [a-f] */
    private static final byte REOP_DIGIT      = 14; /* match a digit char: [0-9] */
    private static final byte REOP_NONDIGIT   = 15; /* match a non-digit char: [^0-9] */
    private static final byte REOP_ALNUM      = 16; /* match an alphanumeric char: [0-9a-z_A-Z] */
    private static final byte REOP_NONALNUM   = 17; /* match a non-alphanumeric char: [^0-9a-z_A-Z] */
    private static final byte REOP_SPACE      = 18; /* match a whitespace char */
    private static final byte REOP_NONSPACE   = 19; /* match a non-whitespace char */
    private static final byte REOP_BACKREF    = 20; /* back-reference (e.g., \1) to a parenthetical */
    private static final byte REOP_FLAT       = 21; /* match a flat string */
    private static final byte REOP_FLAT1      = 22; /* match a single char */
    private static final byte REOP_JUMP       = 23; /* for deoptimized closure loops */
    private static final byte REOP_DOTSTAR    = 24; /* optimize .* to use a single opcode */
    private static final byte REOP_ANCHOR     = 25; /* like .* but skips left context to unanchored r.e. */
    private static final byte REOP_EOLONLY    = 26; /* $ not preceded by any pattern */
    private static final byte REOP_UCFLAT     = 27; /* flat Unicode string; len immediate counts chars */
    private static final byte REOP_UCFLAT1    = 28; /* single Unicode char */
    private static final byte REOP_UCCLASS    = 29; /* Unicode character class, vector of chars to match */
    private static final byte REOP_NUCCLASS   = 30; /* negated Unicode character class */
    private static final byte REOP_BACKREFi   = 31; /* case-independent REOP_BACKREF */
    private static final byte REOP_FLATi      = 32; /* case-independent REOP_FLAT */
    private static final byte REOP_FLAT1i     = 33; /* case-independent REOP_FLAT1 */
    private static final byte REOP_UCFLATi    = 34; /* case-independent REOP_UCFLAT */
    private static final byte REOP_UCFLAT1i   = 35; /* case-independent REOP_UCFLAT1 */
    private static final byte REOP_ANCHOR1    = 36; /* first-char discriminating REOP_ANCHOR */
    private static final byte REOP_NCCLASS    = 37; /* negated 8-bit character class */
    private static final byte REOP_DOTSTARMIN = 38; /* ungreedy version of REOP_DOTSTAR */
    private static final byte REOP_LPARENNON  = 39; /* non-capturing version of REOP_LPAREN */
    private static final byte REOP_RPARENNON  = 40; /* non-capturing version of REOP_RPAREN */
    private static final byte REOP_ASSERT     = 41; /* zero width positive lookahead assertion */
    private static final byte REOP_ASSERT_NOT = 42; /* zero width negative lookahead assertion */
    private static final byte REOP_END        = 43;

    /* maximum length of FLAT string */
    private static final int REOP_FLATLEN_MAX = 255;

    /* not thread safe, used only for debugging */
    private static int level;

    private static String[] reopname = null;
    static {
        if (debug) {
            String a[] = {
                "empty",
                "alt",
                "bol",
                "eol",
                "wbdry",
                "wnonbdry",
                "quant",
                "star",
                "plus",
                "opt",
                "lparen",
                "rparen",
                "dot",
                "cclass",
                "digit",
                "nondigit",
                "alnum",
                "nonalnum",
                "space",
                "nonspace",
                "backref",
                "flat",
                "flat1",
                "jump",
                "dotstar",
                "anchor",
                "eolonly",
                "ucflat",
                "ucflat1",
                "ucclass",
                "nucclass",
                "backrefi",
                "flati",
                "flat1i",
                "ucflati",
                "ucflat1i",
                "anchor1",
                "ncclass",
                "dotstar_min",
                "lparen_non",
                "rparen_non",
                "end"
            };
            reopname = a;
        }
    }

    private String getPrintableString(String str) {
        if (debug) {
            StringBuffer buf = new StringBuffer(str.length());
            for (int i = 0; i < str.length(); i++) {
                int c = str.charAt(i);
                if ((c < 0x20) || (c > 0x7F)) {
                    if (c == '\n')
                        buf.append("\\n");
                    else
                        buf.append("\\u" + Integer.toHexString(c));
                }
                else
                    buf.append((char)c);
            }
            return buf.toString();
        } else {
            return "";
        }
    }

    private void dumpRegExp(CompilerState state, RENode ren) {
        if (debug) {
            if (level == 0)
                System.out.print("level offset  flags  description\n");
            level++;
            do {
                char[] source = ren.s != null ? ren.s : state.source;
                System.out.print(level);
                System.out.print(" ");
                System.out.print(ren.offset);
                System.out.print(" " +
                    ((ren.flags & RENode.ANCHORED) != 0 ? "A" : "-") +
                    ((ren.flags & RENode.SINGLE)   != 0 ? "S" : "-") +
                    ((ren.flags & RENode.NONEMPTY) != 0 ? "F" : "-") + // F for full
                    ((ren.flags & RENode.ISNEXT)   != 0 ? "N" : "-") + // N for next
                    ((ren.flags & RENode.GOODNEXT) != 0 ? "G" : "-") +
                    ((ren.flags & RENode.ISJOIN)   != 0 ? "J" : "-") +
                    ((ren.flags & RENode.MINIMAL)  != 0 ? "M" : "-") +
                    "  " +
                    reopname[ren.op]);

                switch (ren.op) {
                  case REOP_ALT:
                    System.out.print(" ");
                    System.out.println(ren.next.offset);
                    dumpRegExp(state, (RENode) ren.kid);
                    break;

                  case REOP_STAR:
                  case REOP_PLUS:
                  case REOP_OPT:
                  case REOP_ANCHOR1:
            	  case REOP_ASSERT:
            	  case REOP_ASSERT_NOT:
                    System.out.println();
                    dumpRegExp(state, (RENode) ren.kid);
                    break;

                  case REOP_QUANT:
                    System.out.print(" next ");
                    System.out.print(ren.next.offset);
                    System.out.print(" min ");
                    System.out.print(ren.min);
                    System.out.print(" max ");
                    System.out.println(ren.max);
                    dumpRegExp(state, (RENode) ren.kid);
                    break;

                  case REOP_LPAREN:
                    System.out.print(" num ");
                    System.out.println(ren.num);
                    dumpRegExp(state, (RENode) ren.kid);
                    break;

            	  case REOP_LPARENNON:
                    System.out.println();
                    dumpRegExp(state, (RENode) ren.kid);
            	    break;

                  case REOP_BACKREF:
                  case REOP_RPAREN:
                    System.out.print(" num ");
                    System.out.println(ren.num);
                    break;

                  case REOP_CCLASS:
                  {
                    int index = ((Integer) ren.kid).intValue();
                    int index2 = ren.kid2;
                    int len = index2 - index;
                    System.out.print(" [");
                    System.out.print(getPrintableString(
                                    new String(source, index, len)));
                    System.out.println("]");
                    break;
                  }

                  case REOP_FLAT:
                  {
                    int index = ((Integer) ren.kid).intValue();
                    int index2 = ren.kid2;
                    int len = index2 - index;
                    System.out.print(" ");
                    System.out.print(getPrintableString(
                                    new String(source, index, len)));
                    System.out.print(" (");
                    System.out.print(len);
                    System.out.println(")");
                    break;
                  }

                  case REOP_FLAT1:
                    System.out.print(" ");
                    System.out.print(ren.chr);
                    System.out.print(" ('\\");
                    System.out.print(Integer.toString(ren.chr, 8));
                    System.out.println("')");
                    break;

                  case REOP_JUMP:
                    System.out.print(" ");
                    System.out.println(ren.next.offset);
                    break;

                  case REOP_UCFLAT:
                  {
                    int index = ((Integer) ren.kid).intValue();
                    int len = ren.kid2 - index;
                    for (int i = 0; i < len; i++)
                        System.out.print("\\u" + 
                            Integer.toHexString(source[index+i]));
                    System.out.println();
                    break;
                  }

                  case REOP_UCFLAT1:
                    System.out.print("\\u" + 
                        Integer.toHexString(ren.chr));
                    System.out.println();
                    break;

                  case REOP_UCCLASS:
                  {
                    int index = ((Integer) ren.kid).intValue();
                    int len = ren.kid2 - index;
                    System.out.print(" [");
                    for (int i = 0; i < len; i++)
                        System.out.print("\\u" + 
                            Integer.toHexString(source[index+i]));
                    System.out.println("]");
                    break;
                  }

                  default:
                    System.out.println();
                    break;
                }

                if ((ren.flags & RENode.GOODNEXT) == 0)
                    break;
            } while ((ren = ren.next) != null);
            level--;
        }
    }

    private void fixNext(CompilerState state, RENode ren1, RENode ren2,
                         RENode oldnext)
    {
        boolean goodnext;
        RENode next, kid, ren;

        goodnext = ren2 != null && (ren2.flags & RENode.ISNEXT) == 0;

        /*
         * Find the final node in a list of alternatives, or concatenations, or
         * even a concatenation of alternatives followed by non-alternatives (e.g.
         * ((x|y)z)w where ((x|y)z) is ren1 and w is ren2).
         */
        for (; (next = ren1.next) != null && next != oldnext; ren1 = next) {
            if (ren1.op == REOP_ALT) {
                /* Find the end of this alternative's operand list. */
                kid = (RENode) ren1.kid;
                if (kid.op == REOP_JUMP)
                    continue;
                for (ren = kid; ren.next != null; ren = ren.next) {
                    if (ren.op == REOP_ALT)
                        throw new RuntimeException("REOP_ALT not expected");
                }

                /* Append a jump node to all but the last alternative. */
                ren.next = new RENode(state, REOP_JUMP, null);
                ren.next.flags |= RENode.ISNEXT;
                ren.flags |= RENode.GOODNEXT;

                /* Recur to fix all descendent nested alternatives. */
                fixNext(state, kid, ren2, oldnext);
            }
        }

        /*
         * Now ren1 points to the last alternative, or to the final node on a
         * concatenation list.  Set its next link to ren2, flagging a join point
         * if appropriate.
         */
        if (ren2 != null) {
            if ((ren2.flags & RENode.ISNEXT) == 0)
                ren2.flags |= RENode.ISNEXT;
            else
                ren2.flags |= RENode.ISJOIN;
        }
        ren1.next = ren2;
        if (goodnext)
            ren1.flags |= RENode.GOODNEXT;

        /*
         * The following ops have a kid subtree through which to recur.  Here is
         * where we fix the next links under the final ALT node's kid.
         */
        switch (ren1.op) {
          case REOP_ALT:
          case REOP_QUANT:
          case REOP_STAR:
          case REOP_PLUS:
          case REOP_OPT:
          case REOP_LPAREN:
          case REOP_LPARENNON:
            fixNext(state, (RENode) ren1.kid, ren2, oldnext);
            break;
          default:;
        }
    }

    private void setNext(CompilerState state, RENode ren1, RENode ren2) {
        fixNext(state, ren1, ren2, null);
    }

    /*
     * Top-down regular expression grammar, based closely on Perl 4.
     *
     *  regexp:     altern                  A regular expression is one or more
     *              altern '|' regexp       alternatives separated by vertical bar.
     */
    private RENode parseRegExp(CompilerState state) {
        RENode ren = parseAltern(state);
        if (ren == null)
            return null;
        char[] source = state.source;
        int index = state.index;
        if (index < source.length && source[index] == '|') {
            RENode kid = ren;
            ren = new RENode(state, REOP_ALT, kid);
            if (ren == null)
                return null;
            ren.flags = (byte) (kid.flags & (RENode.ANCHORED | RENode.NONEMPTY));
            RENode ren1 = ren;
            do {
                /* (balance: */
                state.index = ++index;
                if (index < source.length && (source[index] == '|' ||
                                              source[index] == ')'))
                {
                    kid = new RENode(state, REOP_EMPTY, null);
                } else {
                    kid = parseAltern(state);
                    index = state.index;
                }
                if (kid == null)
                    return null;
                RENode ren2 = new RENode(state, REOP_ALT, kid);
                if (ren2 == null)
                    return null;
                ren1.next = ren2;
                ren1.flags |= RENode.GOODNEXT;
                ren2.flags = (byte) ((kid.flags & (RENode.ANCHORED |
                                                   RENode.NONEMPTY))
                                     | RENode.ISNEXT);
                ren1 = ren2;
            } while (index < source.length && source[index] == '|');
        }
        return ren;
    }

    /*
     *  altern:     item                    An alternative is one or more items,
     *              item altern             concatenated together.
     */
    private RENode parseAltern(CompilerState state) {
        RENode ren = parseItem(state);
        if (ren == null)
            return null;
        RENode ren1 = ren;
        int flags = 0;
        char[] source = state.source;
        int index = state.index;
        char c;
        /* (balance: */
        while (index != source.length && (c = source[index]) != '|' &&
               c != ')')
        {
            RENode ren2 = parseItem(state);
            if (ren2 == null)
                return null;
            setNext(state, ren1, ren2);
            flags |= ren2.flags;
            ren1 = ren2;
            index = state.index;
        }

        /*
        * Propagate NONEMPTY to the front of a concatenation list, so that the
        * first alternative in (^a|b) is considered non-empty.  The first node
        * in a list may match the empty string (as ^ does), but if the list is
        * non-empty, then the first node's NONEMPTY flag must be set.
        */
        ren.flags |= flags & RENode.NONEMPTY;
        return ren;
    }

    /*
     *  item:       assertion               An item is either an assertion or
     *              quantatom               a quantified atom.
     *
     *  assertion:  '^'                     Assertions match beginning of string
     *                                      (or line if the class static property
     *                                      RegExp.multiline is true).
     *              '$'                     End of string (or line if the class
     *                                      static property RegExp.multiline is
     *                                      true).
     *              '\b'                    Word boundary (between \w and \W).
     *              '\B'                    Word non-boundary.
     */
    RENode parseItem(CompilerState state) {
        RENode ren;
        byte op;

        char[] source = state.source;
        int index = state.index;
        switch (index < source.length ? source[index] : '\0') {
          case '^':
            state.index = index + 1;
            ren = new RENode(state, REOP_BOL, null);
            ren.flags |= RENode.ANCHORED;
            return ren;

          case '$':
            state.index = index + 1;
            return new RENode(state,
                             (index == state.indexBegin ||
                              ((source[index-1] == '(' ||
                                source[index-1] == '|') && /*balance)*/
                               (index - 1 == state.indexBegin ||
                                source[index-2] != '\\')))
                             ? REOP_EOLONLY
                             : REOP_EOL,
                             null);

          case '\\':
            switch (++index < source.length ? source[index] : '\0') {
              case 'b':
                op = REOP_WBDRY;
                break;
              case 'B':
                op = REOP_WNONBDRY;
                break;
              default:
                return parseQuantAtom(state);
            }

            /*
             * Word boundaries and non-boundaries are flagged as non-empty
             * so they will be prefixed by an anchoring node.
             */
            state.index = index + 1;
            ren = new RENode(state, op, null);
            ren.flags |= RENode.NONEMPTY;
            return ren;

          default:;
        }
        return parseQuantAtom(state);
    }

    /*
     *  quantatom:  atom                    An unquantified atom.
     *              quantatom '{' n ',' m '}'
     *                                      Atom must occur between n and m times.
     *              quantatom '{' n ',' '}' Atom must occur at least n times.
     *              quantatom '{' n '}'     Atom must occur exactly n times.
     *              quantatom '*'           Zero or more times (same as {0,}).
     *              quantatom '+'           One or more times (same as {1,}).
     *              quantatom '?'           Zero or one time (same as {0,1}).
     *
     *              any of which can be optionally followed by '?' for ungreedy
     */
    RENode parseQuantAtom(CompilerState state) {
        RENode ren = parseAtom(state);
        if (ren == null)
            return null;

        int up;
        char c;
        RENode ren2;
        int min, max;
        char[] source = state.source;
        int index = state.index;
    loop:
        while (index < source.length) {
            switch (source[index]) {
              case '{':
                if (++index == source.length || !isDigit(c = source[index])) {
                    reportError("msg.bad.quant",
                                String.valueOf(source[state.index]));
                    return null;
                }
                min = unDigit(c);
                while (++index < source.length && isDigit(c = source[index])) {
                    min = 10 * min + unDigit(c);
                    if ((min >> 16) != 0) {
                        reportError("msg.overlarge.max", tail(source, index));
                        return null;
                    }
                }
                if (source[index] == ',') {
                    up = ++index;
                    if (isDigit(source[index])) {
                        max = unDigit(source[index]);
                        while (isDigit(c = source[++index])) {
                            max = 10 * max + unDigit(c);
                            if ((max >> 16) != 0) {
                                reportError("msg.overlarge.max",
                                            String.valueOf(source[up]));
                                return null;
                            }
                        }
                        if (max == 0) {
                            reportError("msg.zero.quant",
                                        tail(source, state.index));
                            return null;
                        }
                        if (min > max) {
                            reportError("msg.max.lt.min", tail(source, up));
                            return null;
                        }
                    } else {
                        /* 0 means no upper bound. */
                        max = 0;
                    }
                } else {
                    /* Exactly n times. */
                    if (min == 0) {
                        reportError("msg.zero.quant",
                                    tail(source, state.index));
                        return null;
                    }
                    max = min;
                }
                if (source[index] != '}') {
                    reportError("msg.unterm.quant",
                                String.valueOf(source[state.index]));
                    return null;
                }
                index++;

                ren2 = new RENode(state, REOP_QUANT, ren);
                if (min > 0 && (ren.flags & RENode.NONEMPTY) != 0)
                    ren2.flags |= RENode.NONEMPTY;
                ren2.min = (short) min;
                ren2.max = (short) max;
                ren = ren2;
                break;

              case '*':
                if ((ren.flags & RENode.NONEMPTY) == 0) {
                    reportError("msg.re.empty", "*");
                    return null;
                }
                index++;
                ren = new RENode(state, REOP_STAR, ren);
                break;

              case '+':
                if ((ren.flags & RENode.NONEMPTY) == 0) {
                    reportError("msg.re.empty", "+");
                    return null;
                }
                index++;
                ren2 = new RENode(state, REOP_PLUS, ren);
                if ((ren.flags & RENode.NONEMPTY) != 0)
                    ren2.flags |= RENode.NONEMPTY;
                ren = ren2;
                break;

              case '?':
                index++;
                ren = new RENode(state, REOP_OPT, ren);
                break;

              default:
                break loop;
            }
            if ((index < source.length) && (source[index] == '?')) {
                ren.flags |= RENode.MINIMAL;
                index++;
            }
        }

        state.index = index;
        return ren;
    }

    /*
     *  atom:       '(' regexp ')'          A parenthesized regexp (what matched
     *                                      can be addressed using a backreference,
     *                                      see '\' n below).
     *              '.'                     Matches any char except '\n'.
     *              '[' classlist ']'       A character class.
     *              '[' '^' classlist ']'   A negated character class.
     *              '\f'                    Form Feed.
     *              '\n'                    Newline (Line Feed).
     *              '\r'                    Carriage Return.
     *              '\t'                    Horizontal Tab.
     *              '\v'                    Vertical Tab.
     *              '\d'                    A digit (same as [0-9]).
     *              '\D'                    A non-digit.
     *              '\w'                    A word character, [0-9a-z_A-Z].
     *              '\W'                    A non-word character.
     *              '\s'                    A whitespace character, [ \b\f\n\r\t\v].
     *              '\S'                    A non-whitespace character.
     *              '\' n                   A backreference to the nth (n decimal
     *                                      and positive) parenthesized expression.
     *              '\' octal               An octal escape sequence (octal must be
     *                                      two or three digits long, unless it is
     *                                      0 for the null character).
     *              '\x' hex                A hex escape (hex must be two digits).
     *              '\c' ctrl               A control character, ctrl is a letter.
     *              '\' literalatomchar     Any character except one of the above
     *                                      that follow '\' in an atom.
     *              otheratomchar           Any character not first among the other
     *                                      atom right-hand sides.
     */
    static final String metachars    = "|^${*+?().[\\";
    static final String closurechars = "{*+?";

    RENode parseAtom(CompilerState state) {
        int num = 0, len;
        RENode ren = null;
        RENode ren2;
        char c;
        byte op;

        boolean skipCommon = false;
        boolean doFlat = false;

        char[] source = state.source;
        int index = state.index;
        int ocp = index;
        if (index == source.length) {
            state.index = index;
            return new RENode(state, REOP_EMPTY, null);
        }
        switch (source[index]) {
            /* handle /|a/ by returning an empty node for the leftside */
          case '|':
            return new RENode(state, REOP_EMPTY, null);

          case '(':
          
          
            op = REOP_END;
            if (source[index + 1] == '?') {
                switch (source[index + 2]) {
                    case ':' :
                        op = REOP_LPARENNON;
                        break;
                    case '=' :
                        op = REOP_ASSERT;
                        break;
                    case '!' :
                        op = REOP_ASSERT_NOT;
                        break;
                }
            }
            if (op == REOP_END) {
                op = REOP_LPAREN;
                num = state.parenCount++;      /* \1 is numbered 0, etc. */
                state.index = index + 1;
            }
            else
                state.index = index + 3;
            ren2 = parseRegExp(state);
            if (ren2 == null)
                return null;
            index = state.index;
            if (index >= source.length || source[index] != ')') {
                reportError("msg.unterm.paren", tail(source, ocp));
                return null;
            }
            index++;
            ren = new RENode(state, op, ren2);
            ren.flags = (byte) (ren2.flags & (RENode.ANCHORED |
                                              RENode.NONEMPTY));
            ren.num = num;
            if ((op == REOP_LPAREN) || (op == REOP_LPARENNON)) {
                /* Assume RPAREN ops immediately succeed LPAREN ops */
                ren2 = new RENode(state, (byte)(op + 1), null);
                setNext(state, ren, ren2);
                ren2.num = num;
            }
            break;

          case '.':
            ++index;
            op = REOP_DOT;
            if ((index < source.length) && (source[index] == '*')) {
    	        index++;
                op = REOP_DOTSTAR;
                if ((index < source.length) && (source[index] == '?')) {
                    index++;
                    op = REOP_DOTSTARMIN;
                }
            }
            ren = new RENode(state, op, null);
            if (ren.op == REOP_DOT)
                ren.flags = RENode.SINGLE | RENode.NONEMPTY;
            break;

          case '[':
            /* A char class must have at least one char in it. */
            if (++index == source.length) {
                reportError("msg.unterm.class", tail(source, ocp));
                return null;
            }
            c = source[index];
            ren = new RENode(state, REOP_CCLASS, new Integer(index));

            /* A negated class must have at least one char in it after the ^. */
            if (c == '^' && ++index == source.length) {
                reportError("msg.unterm.class", tail(source, ocp));
                return null;
            }

            for (;;) {
                if (++index == source.length) {
                    reportError("msg.unterm.paren", tail(source, ocp));
                    return null;
                }
                c = source[index];
                if (c == ']')
                    break;
                if (c == '\\' && index+1 != source.length)
                    index++;
            }
            ren.kid2 = index++;

            /* Since we rule out [] and [^], we can set the non-empty flag. */
            ren.flags = RENode.SINGLE | RENode.NONEMPTY;
            break;

          case '\\':
            if (++index == source.length) {
                Context.reportError(ScriptRuntime.getMessage("msg.trail.backslash",
                                                       null));
                return null;
            }
            c = source[index];
            switch (c) {
              case 'f':
              case 'n':
              case 'r':
              case 't':
              case 'v':
                c = getEscape(c);
                ren = new RENode(state, REOP_FLAT1, null);
                break;
              case 'd':
                ren = new RENode(state, REOP_DIGIT, null);
                break;
              case 'D':
                ren = new RENode(state, REOP_NONDIGIT, null);
                break;
              case 'w':
                ren = new RENode(state, REOP_ALNUM, null);
                break;
              case 'W':
                ren = new RENode(state, REOP_NONALNUM, null);
                break;
              case 's':
                ren = new RENode(state, REOP_SPACE, null);
                break;
              case 'S':
                ren = new RENode(state, REOP_NONSPACE, null);
                break;

              case '0':
                state.index = index;
                num = doOctal(state);
                index = state.index;
                ren = new RENode(state, REOP_FLAT1, null);
                c = (char) num;
                break;

              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                num = unDigit(c);
                while (++index < source.length && isDigit(c = source[index]))
                    num = 10 * num - unDigit(c);
                if (num > 9 || num > state.parenCount) {
                    state.index = ocp;
                    num = doOctal(state);
                    index = state.index;
                    ren = new RENode(state, REOP_FLAT1, null);
                    c = (char) num;
                    break;
                }
                index--;
                ren = new RENode(state, REOP_BACKREF, null);
                ren.num = num - 1;       /* \1 is numbered 0, etc. */

                /* Avoid common chr- and flags-setting code after switch. */
                ren.flags = RENode.NONEMPTY;
                skipCommon = true;
                break;

              case 'x':
                ocp = index;
                if (++index < source.length && isHex(c = source[index])) {
                    num = unHex(c);
                    if (++index < source.length && isHex(c = source[index])) {
                        num <<= 4;
                        num += unHex(c);
                    } else {
                        index--;       /* back up so index points to last hex char */
                    }
                } else {
            		index = ocp;	/* \xZZ is xZZ (Perl does \0ZZ!) */
            		num = 'x';
                }
                ren = new RENode(state, REOP_FLAT1, null);
                c = (char)num;
                break;

              case 'c':
                c = source[++index];
                if (!('A' <= c && c <= 'Z') && !('a' <= c && c <= 'z')) {
                    index -= 2;
                    ocp = index;
                    doFlat = true;
                    skipCommon = true;
                    break;
                }
                c = Character.toUpperCase(c);
                c = (char) (c ^ 64); // JS_TOCTRL
                ren = new RENode(state, REOP_FLAT1, null);
                break;

              case 'u':
                if (index+4 < source.length &&
                    isHex(source[index+1]) && isHex(source[index+2]) &&
                    isHex(source[index+3]) && isHex(source[index+4]))
                {
                    num = (((((unHex(source[index+1]) << 4) +
                                unHex(source[index+2])) << 4) +
                               unHex(source[index+3])) << 4) +
                              unHex(source[index+4]);
                    c = (char) num;
                    index += 4;
                    ren = new RENode(state, REOP_FLAT1, null);
                    break;
                }

                /* Unlike Perl \\xZZ, we take \\uZZZ to be literal-u then ZZZ. */
                ocp = index;
                doFlat = true;
                skipCommon = true;
                break;

             default:
                ocp = index;
                doFlat = true;
                skipCommon = true;
                break;
            }

            /* Common chr- and flags-setting code for escape opcodes. */
            if (ren != null && !skipCommon) {
                ren.chr = c;
                ren.flags = RENode.SINGLE | RENode.NONEMPTY;
            }
            skipCommon = false;

            if (!doFlat) {
                /* Skip to next unparsed char. */
                index++;
                break;
            }

            /* fall through since doFlat was true */
            doFlat = false;

          default:
            while (++index != source.length &&
                   metachars.indexOf(source[index]) == -1)
                ;
            len = (int)(index - ocp);
            if (index != source.length && len > 1 &&
                closurechars.indexOf(source[index]) != -1)
            {
                index--;
                len--;
            }
            if (len > REOP_FLATLEN_MAX) {
                len = REOP_FLATLEN_MAX;
                index = ocp + len;
            }
            ren = new RENode(state, len == 1 ? REOP_FLAT1 : REOP_FLAT,
                             new Integer(ocp));
            ren.flags = RENode.NONEMPTY;
            if (len > 1) {
                ren.kid2 = index;
            } else {
                ren.flags |= RENode.SINGLE;
                ren.chr = source[ocp];
            }
            break;
        }

        state.index = index;
        return ren;
    }

    private int doOctal(CompilerState state) {
        char[] source = state.source;
        int index = state.index;
        int num = 0;
        char c;
        while (++index < source.length && '0' <= (c = source[index]) &&
               c <= '7')
        {
            num = 8 * num + (int)(c - '0');
            if (num > 0377) {
                reportError("msg.overlarge.octal", tail(source, state.index));
                num = 0;
                break;
            }
        }
        index--;
        state.index = index;
        return num;
    }

    static char getEscape(char c) {
        switch (c) {
          case 'b':
            return '\b';
          case 'f':
            return '\f';
          case 'n':
            return '\n';
          case 'r':
            return '\r';
          case 't':
            return '\t';
          case 'v':
            return (char) 11; // '\v' is not vtab in Java
        }
        throw new RuntimeException();
    }

    static public boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    static int unDigit(char c) {
        return c - '0';
    }

    static boolean isHex(char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
               ('A' <= c && c <= 'F');
    }

    static int unHex(char c) {
        if ('0' <= c && c <= '9')
            return c - '0';
        return 10 + Character.toLowerCase(c) - 'a';
    }

    static boolean isWord(char c) {
        return Character.isLetter(c) || isDigit(c) || c == '_';
    }

    private String tail(char[] a, int i) {
        return new String(a, i, a.length - i);
    }

    private static boolean matchChar(int flags, char c, char c2)
    {
        if (c == c2)
            return true;
        else
            if ((flags & FOLD) != 0) {
                c = Character.toUpperCase(c);
                c2 = Character.toUpperCase(c2);
                return c == c2 ||
                       Character.toLowerCase(c) == Character.toLowerCase(c2);
            }
            else
                return false;
    }
    
    int matchGreedyKid(MatchState state, RENode ren, RENode stop,
                            int kidCount, int maxKid,
                            char[] input, int index, int previousKid)
    {
        // assume that kids have been matched as required, 
        // see if the rest of the input matches
        int match = matchRENodes(state, ren.next, stop, input, index);
        // for non-greedy matching, return as soon as the rest matches
        if ((match != -1) && ((ren.flags & RENode.MINIMAL) != 0))
            return match;
        if ((maxKid != 0) && (kidCount >= maxKid))  {
            // not allowed any more kids, return whatever we have
            return match;
        }
        // try taking another child in any case
        int kidMatch = matchRENodes(state, (RENode)ren.kid, 
                                                ren.next, input, index);
        if (kidMatch == -1) {
            // no more kids available, return with as far as we got
            if ((match != -1) && (previousKid != -1))
                // rerun last kid if match was a success
                matchRENodes(state, (RENode)ren.kid, 
                                        ren.next, input, previousKid);
            return matchRENodes(state, ren.next, stop, input, index);   
        }
        // go deeper to see how that looks
        int deeper = matchGreedyKid(state, ren, stop, ++kidCount, maxKid,
                                                    input, kidMatch, index);                    
        if (match != -1) { // if we could match here, see if deeper was better
            if (deeper == -1) { // couldn't do better, reset lastKid
                if (previousKid != -1)
                    matchRENodes(state, (RENode)ren.kid, ren.next,
                                                        input, previousKid);
                return matchRENodes(state, ren.next, stop, input, index);
            }
            else // deeper was better, return it
                return deeper;
        }
        else { // couldn't complete from here so take another child
            return deeper;
        }
    }

    int matchRENodes(MatchState state, RENode ren, RENode stop, 
                                                char[] input, int index)
    {
        while ((ren != stop) && (ren != null))
        {
            switch (ren.op) {
                case REOP_EMPTY:
                    break;
                case REOP_ALT: {
                        if (ren.next.op != REOP_ALT) {
                            ren = (RENode)ren.kid;
                            continue;
                        }
                        else {
                            int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                            stop, input, index);
                            if (kidMatch != -1) return kidMatch;
                        }
                    }
                    break;
                case REOP_QUANT: {
                        int num;
                        int lastKid = -1;
                        for (num = 0; num < ren.min; num++) {
                            int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                            if (kidMatch == -1)
                                return -1;
                            else {
                                lastKid = index;
                                index = kidMatch;
                            }
                        }
                        return matchGreedyKid(state, ren, stop, num, ren.max,
                                                        input, index, lastKid);
                    }
                case REOP_PLUS: {
                        int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                        if (kidMatch != -1)
                            return matchGreedyKid(state, ren, stop, 1, 0,
                                                        input, kidMatch, index);
                        else
                            return -1;
                    }
                case REOP_STAR:
                    return matchGreedyKid(state, ren, stop,
                                                        0, 0, input, index, -1);
                case REOP_OPT: {
                        int saveNum = state.parenCount;
                        if (((ren.flags & RENode.MINIMAL) != 0)) {
                            int restMatch = matchRENodes(state, ren.next,
                                                        stop, input, index);
                            if (restMatch != -1) return restMatch;
                        }
                        int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                        if (kidMatch == -1)
                            break;
                        else {
                            int restMatch = matchRENodes(state, ren.next,
                                                        stop, input, kidMatch);
                            if (restMatch == -1) {
                                // need to undo the result of running the kid
                                state.parenCount = saveNum;
                                break;
                            }
                            else
                                return restMatch;
                        }
/*
                        int saveNum = state.parenCount;
                        int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                        if (kidMatch == -1)
                            break;
                        else {
                            int restMatch = matchRENodes(state, ren.next,
                                                        stop, input, kidMatch);
                            if (restMatch == -1) {
                                // need to undo the result of running the kid
                                state.parenCount = saveNum;
                                break;
                            }
                            else
                                return restMatch;
                        }
*/
                     }
                case REOP_LPARENNON:
                    ren = (RENode)ren.kid;
                    continue;
                case REOP_RPARENNON:
                    break;
                case REOP_LPAREN: {
                        int num = ren.num;
                        ren = (RENode)ren.kid;
                        SubString parsub = state.parens[num];
                        if (parsub == null) {
                            parsub = state.parens[num] = new SubString();
                            parsub.charArray = input;
                        }
                        parsub.index = index;
                        if (num >= state.parenCount)
                            state.parenCount = num + 1;
                        continue;
                    }
                case REOP_RPAREN: {
                        int num = ren.num;
                        SubString parsub = state.parens[num];
                        if (parsub == null)
                                throw new RuntimeException("Paren problem");
                        parsub.length = index - parsub.index;
                        break;
                    }
                case REOP_ASSERT: {
                        int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                        if (kidMatch == -1) return -1;
                        break;
                    }
                case REOP_ASSERT_NOT: {
                        int kidMatch = matchRENodes(state, (RENode)ren.kid,
                                                        ren.next, input, index);
                        if (kidMatch != -1) return -1;
                        break;
                    }
                    
                case REOP_BACKREF: {
                        int num = ren.num;
                        SubString parsub = state.parens[num];
                        if (parsub == null)
                            parsub = state.parens[num] = new SubString();
                        int length = parsub.length;
                        if ((input.length - index) < length)
                            return -1;
                        else {
                            for (int i = 0; i < length; i++, index++) {
                                if (!matchChar(state.flags, input[index],
                                            parsub.charArray[parsub.index + i]))
                                    return -1;
                            }
                        }
                    }
                    break;
                case REOP_CCLASS:
                    if (index < input.length) {
                        if (ren.bitmap == null) {
                            char[] source = (ren.s != null) 
                                                ? ren.s 
                                                : this.source.toCharArray();
                            ren.buildBitmap(source, ((state.flags & FOLD) != 0));
                        }
                        char c = input[index];
                        int b = (c >>> 3);
                        if (b >= ren.bmsize) {
                            if (ren.kid2 == -1) // a ^ class
                                index++;
                            else
                                return -1;
                        } else {
                            int bit = c & 7;
                            bit = 1 << bit;
                            if ((ren.bitmap[b] & bit) != 0)
                                index++;
                            else
                                return -1;
                        }
                    }
                    else
                        return -1;
                    break;
                case REOP_DOT:
                    if ((index < input.length) && (input[index] != '\n'))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_DOTSTARMIN: {
                        int cp2;
                        for (cp2 = index; cp2 < input.length; cp2++) {
                            int cp3 = matchRENodes(state, ren.next,
                                                            stop, input, cp2);
                            if (cp3 != -1) return cp3;
            		        if (input[cp2] == '\n')
            		            return -1;
                        }
                        return -1;
                    }
                case REOP_DOTSTAR: {
                        int cp2;
                        for (cp2 = index; cp2 < input.length; cp2++)
                            if (input[cp2] == '\n')
                                break;
                        while (cp2 >= index) {
                            int cp3 = matchRENodes(state, ren.next,
                                                            stop, input, cp2);
                            if (cp3 != -1)
                                return cp3;
                            cp2--;
                        }
                        return -1;
                    }
                case REOP_WBDRY:
                    if (((index == 0) || !isWord(input[index-1]))
                          ^ ((index >= input.length) || !isWord(input[index])))
                        ; // leave index 
                    else
                        return -1;
                    break;
                  case REOP_WNONBDRY:
                    if (((index == 0) || !isWord(input[index-1]))
                          ^ ((index < input.length) && isWord(input[index])))
                        ; // leave index 
                    else
                        return -1;
                    break;
                case REOP_EOLONLY:
                case REOP_EOL: {
                        if (index == input.length)
                            ; // leave index;
                        else {
                            Context cx = Context.getCurrentContext();
                            RegExpImpl reImpl = getImpl(cx);
                            if ((reImpl.multiline)
                                        || ((state.flags & MULTILINE) != 0))
                                if (input[index] == '\n')
                                    ;// leave index
                                else
                                    return -1;
                            else
                                return -1;
                        }
                    }
                    break;
                case REOP_BOL: {
                        Context cx = Context.getCurrentContext();
                        RegExpImpl reImpl = getImpl(cx);
                        if (index != 0) {
                            if ((index < input.length)
                                 && (reImpl.multiline
                                     || ((state.flags & MULTILINE) != 0))) {
                                if (input[index - 1] == '\n') {
                                    break;
                                }
                            }
                            return -1;
                        }
                        // leave index
                    }
                    break;
                case REOP_DIGIT:
                    if ((index < input.length) && isDigit(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_NONDIGIT:
                    if ((index < input.length) && !isDigit(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_ALNUM:
                    if ((index < input.length) && isWord(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_NONALNUM:
                    if ((index < input.length) && !isWord(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_SPACE:
                    if ((index < input.length)
                                    && Character.isWhitespace(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_NONSPACE:
                    if ((index < input.length)
                                    && !Character.isWhitespace(input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_FLAT1:
                    if ((index < input.length)
                              && matchChar(state.flags, ren.chr, input[index]))
                        index++;
                    else
                        return -1;
                    break;
                case REOP_FLAT: {
                        char[] source = (ren.s != null)
                                                ? ren.s
                                                : this.source.toCharArray();
                        int start = ((Integer)ren.kid).intValue();
                        int length = ren.kid2 - start;
                        if ((input.length - index) < length)
                            return -1;
                        else {
                            for (int i = 0; i < length; i++, index++) {
                                if (!matchChar(state.flags, input[index],
                                                            source[start + i]))
                                    return -1;
                            }
                        }
                    }
                    break;
                case REOP_JUMP:
                    break;
                case REOP_END:
                    break;
                default :
                    throw new RuntimeException("Unsupported by node matcher");
            }
            ren = ren.next;
        }
        return index;
    }

    int matchRegExp(MatchState state, RENode ren, char[] input, int index)
    {
        // have to include the position beyond the last character
        // in order to detect end-of-input/line condition
        for (int i = index; i <= input.length; i++) {            
            state.skipped = i - index;
            state.parenCount = 0;
            int result = matchRENodes(state, ren, null, input, i);
            if (result != -1)
                return result;
        }
        return -1;
    }

    /*
     * indexp is assumed to be an array of length 1
     */
    Object executeRegExp(Scriptable scopeObj, String str, int indexp[], 
                         boolean test)
    {
        NativeRegExp re = this;
        Context cx = Context.getCurrentContext();
        RegExpImpl res = getImpl(cx);

        /*
         * Initialize a CompilerState to minimize recursive argument traffic.
         */
        MatchState state = new MatchState();
        state.anchoring = false;
        state.flags = re.flags;

        char[] charArray = str.toCharArray();
        int start = indexp[0];
        if (start > charArray.length)
            start = charArray.length;
        int index = start;
        state.cpbegin = 0;
        state.cpend = charArray.length;
        state.start = start;
        state.skipped = 0;

        state.parenCount = 0;
        state.maybeParens = new SubString[re.parenCount];
        state.parens = new SubString[re.parenCount];
        // We allocate the elements of "parens" and "maybeParens" lazily in
        // the Java port since we don't have arenas.

        /*
         * Call the recursive matcher to do the real work.  Return null on mismatch
         * whether testing or not.  On match, return an extended Array object.
         */
        index = matchRegExp(state, ren, charArray, index);
        if (index == -1) {
            return null;
        }
        int i = index - state.cpbegin;
        indexp[0] = i;
        int matchlen = i - (start + state.skipped);
        int ep = index; 
        index -= matchlen;
        Object result;
        Scriptable obj;

        if (test) {
            /*
             * Testing for a match and updating cx.regExpImpl: don't allocate
             * an array object, do return true.
             */
            result = Boolean.TRUE;
            obj = null;
        } else {
            /*
             * The array returned on match has element 0 bound to the matched
             * string, elements 1 through state.parenCount bound to the paren
             * matches, an index property telling the length of the left context,
             * and an input property referring to the input string.
             */
            Scriptable scope = getTopLevelScope(scopeObj);
            result = ScriptRuntime.newObject(cx, scope, "Array", null);
            obj = (Scriptable) result;

            String matchstr = new String(charArray, index, matchlen);
            obj.put(0, obj, matchstr);
        }

        if (state.parenCount > re.parenCount)
            throw new RuntimeException();
        if (state.parenCount == 0) {
            res.parens.setSize(0);
            res.lastParen = SubString.emptySubString;
        } else {
            SubString parsub = null;
            int num;
            res.parens.setSize(state.parenCount);
            for (num = 0; num < state.parenCount; num++) {
                parsub = state.parens[num];
                res.parens.setElementAt(parsub, num);
                if (test)
                    continue;
                String parstr = parsub == null ? "": parsub.toString();
                obj.put(num+1, obj, parstr);
            }
            res.lastParen = parsub;
        }

        if (!test) {
            /*
             * Define the index and input properties last for better for/in loop
             * order (so they come after the elements).
             */
            obj.put("index", obj, new Integer(start + state.skipped));
            obj.put("input", obj, str);
        }

        if (res.lastMatch == null) {
            res.lastMatch = new SubString();
            res.leftContext = new SubString();
            res.rightContext = new SubString();
        }
        res.lastMatch.charArray = charArray;
        res.lastMatch.index = index;
        res.lastMatch.length = matchlen;

        res.leftContext.charArray = charArray;
        if (cx.getLanguageVersion() == Context.VERSION_1_2) {
            /*
             * JS1.2 emulated Perl4.0.1.8 (patch level 36) for global regexps used
             * in scalar contexts, and unintentionally for the string.match "list"
             * psuedo-context.  On "hi there bye", the following would result:
             *
             * Language     while(/ /g){print("$`");}   s/ /$`/g
             * perl4.036    "hi", "there"               "hihitherehi therebye"
             * perl5        "hi", "hi there"            "hihitherehi therebye"
             * js1.2        "hi", "there"               "hihitheretherebye"
             *
             * Insofar as JS1.2 always defined $` as "left context from the last
             * match" for global regexps, it was more consistent than perl4.
             */
            res.leftContext.index = start;
            res.leftContext.length = state.skipped;
        } else {
            /*
             * For JS1.3 and ECMAv2, emulate Perl5 exactly:
             *
             * js1.3        "hi", "hi there"            "hihitherehi therebye"
             */
            res.leftContext.index = 0;
            res.leftContext.length = start + state.skipped;
        }

        res.rightContext.charArray = charArray;
        res.rightContext.index = ep;
        res.rightContext.length = state.cpend - ep;

        return result;
    }

    public byte getFlags() {
        return flags;
    }

    private void reportError(String msg, String arg) {
        Object[] args = { arg };
        String message = ScriptRuntime.getMessage(msg, args);
        Context.reportRuntimeError(message);
    }

    private String source;      /* locked source string, sans // */
    private int lastIndex;      /* index after last match, for //g iterator */
    private int parenCount;     /* number of parenthesized submatches */
    private byte flags;         /* flags  */
    private byte[] program;     /* regular expression bytecode */

    RENode ren;
}

class CompilerState {
    CompilerState(String source, int flags) {
        this.source = source.toCharArray();
        this.flags = flags;
    }
    char[]      source;
    int         indexBegin;
    int         index;
    int         flags;
    int         parenCount;
    int         progLength;
    byte[]      prog;
}


class RENode {
    public static final int ANCHORED = 0x01;    /* anchored at the front */
    public static final int SINGLE   = 0x02;    /* matches a single char */
    public static final int NONEMPTY = 0x04;    /* does not match empty string */
    public static final int ISNEXT   = 0x08;    /* ren is next after at least one node */
    public static final int GOODNEXT = 0x10;    /* ren.next is a tree-like edge in the graph */
    public static final int ISJOIN   = 0x20;    /* ren is a join point in the graph */
    public static final int REALLOK  = 0x40;    /* REOP_FLAT owns tempPool space to realloc */
    public static final int MINIMAL  = 0x80;    /* un-greedy matching for ? * + {} */

    RENode(CompilerState state, byte op, Object kid) {
        this.op = op;
        this.kid = kid;
    }
    
    private void calcBMSize(char[] s, int index, int cp2, boolean fold)
    {
        char maxc = 0;
        while (index < cp2) {
            char c = s[index++];
            if (c == '\\') {
                if (index + 5 <= cp2 && s[index] == 'u'
                    && NativeRegExp.isHex(s[index+1]) 
                    && NativeRegExp.isHex(s[index+2])
                    && NativeRegExp.isHex(s[index+3]) 
                    && NativeRegExp.isHex(s[index+4]))
                {
                    int x = (((((NativeRegExp.unHex(s[index+0]) << 4) +
                                    NativeRegExp.unHex(s[index+1])) << 4) +
                                    NativeRegExp.unHex(s[index+2])) << 4) +
                                    NativeRegExp.unHex(s[index+3]);
                    c = (char) x;
                    index += 5;
                } else {
                    /*
                     * Octal and hex escapes can't be > 255.  Skip this
                     * backslash and let the loop pass over the remaining
                     * escape sequence as if it were text to match.
                     */
                    if (maxc < 255) maxc = 255;
                    continue;
                }
            }
            if (fold) {
                /*
                 * Don't assume that lowercase are above uppercase, or
                 * that c is either even when c has upper and lowercase
                 * versions.
                 */
                char c2;
                if ((c2 = Character.toUpperCase(c)) > maxc)
                    maxc = c2;
                if ((c2 = Character.toLowerCase(c2)) > maxc)
                    maxc = c2;
            }
            if (c > maxc)
                maxc = c;
        }
        bmsize = (short)((maxc + NativeRegExp.JS_BITS_PER_BYTE) 
                                        / NativeRegExp.JS_BITS_PER_BYTE);
    }
    
    private void matchBit(char c, int fill) {
        int i = (c) >> 3;
        byte b = (byte) (c & 7);
        b = (byte) (1 << b);
        if (fill != 0)
            bitmap[i] &= ~b;
        else
            bitmap[i] |= b;
    }

    private void checkRange(char lastc, int fill) {
        matchBit(lastc, fill);
        matchBit('-', fill);
    }

    void buildBitmap(char[] s, boolean fold)
    {
        int index = ((Integer) kid).intValue();
        int end = kid2;
        byte fill = 0;
        int i,n,ocp;
        
        boolean not = false;
        kid2 = 0;
        if (s[index] == '^') {
            not = true;
            kid2 = -1;
            index++;
        }
        
        calcBMSize(s, index, end, fold);
        bitmap = new byte[bmsize];
        if (not) {
            fill = (byte)0xff;
            for (i = 0; i < bmsize; i++)
                bitmap[i] = (byte)0xff;
            bitmap[0] = (byte)0xfe;
        }
        int nchars = bmsize * NativeRegExp.JS_BITS_PER_BYTE;
        char lastc = (char)nchars;
        boolean inrange = false;
        
        while (index < end) {
            char c = s[index++];
            if (c == '\\') {
                c = s[index++];
                switch (c) {
                  case 'b':
                  case 'f':
                  case 'n':
                  case 'r':
                  case 't':
                  case 'v':
                    c = NativeRegExp.getEscape(c);
                    break;

                  case 'd':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = '0'; c <= '9'; c++)
                        matchBit(c, fill);
                    continue;

                  case 'D':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = 0; c < '0'; c++)
                        matchBit(c, fill);
                    for (c = '9' + 1; c < nchars; c++)
                        matchBit(c, fill);
                    continue;

                  case 'w':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = 0; c < nchars; c++)
                        if (NativeRegExp.isWord(c))
                            matchBit(c, fill);
                    continue;

                  case 'W':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = 0; c < nchars; c++)
                        if (!NativeRegExp.isWord(c))
                            matchBit(c, fill);
                    continue;

                  case 's':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = 0; c < nchars; c++)
                        if (Character.isWhitespace(c))
                            matchBit(c, fill);
                    continue;

                  case 'S':
                    if (inrange)
                        checkRange(lastc, fill);
                    lastc = (char) nchars;
                    for (c = 0; c < nchars; c++)
                        if (!Character.isWhitespace(c))
                            matchBit(c, fill);
                    continue;

                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                    n = NativeRegExp.unDigit(c);
                    ocp = index - 2;
                    c = s[index];
                    if ('0' <= c && c <= '7') {
                        index++;
                        n = 8 * n + NativeRegExp.unDigit(c);

                        c = s[index];
                        if ('0' <= c && c <= '7') {
                            index++;
                            i = 8 * n + NativeRegExp.unDigit(c);
                            if (i <= 0377)
                                n = i;
                            else
                                index--;
                        }
                    }
                    c = (char) n;
                    break;

                  case 'x':
                    ocp = index;
                    if (index < s.length &&
                        NativeRegExp.isHex(c = s[index++]))
                    {
                        n = NativeRegExp.unHex(c);
                        if (index < s.length &&
                            NativeRegExp.isHex(c = s[index++]))
                        {
                            n <<= 4;
                            n += NativeRegExp.unHex(c);
                        }
                    } else {
                        index = ocp;	/* \xZZ is xZZ (Perl does \0ZZ!) */
                        n = 'x';
                    }
                    c = (char) n;
                    break;

                  case 'u':
                    if (s.length > index+3
                            && NativeRegExp.isHex(s[index+0])
                            && NativeRegExp.isHex(s[index+1]) 
                            && NativeRegExp.isHex(s[index+2])
                            && NativeRegExp.isHex(s[index+3])) {
                        n = (((((NativeRegExp.unHex(s[index+0]) << 4) +
                                NativeRegExp.unHex(s[index+1])) << 4) +
                                NativeRegExp.unHex(s[index+2])) << 4) +
                                NativeRegExp.unHex(s[index+3]);
                        c = (char) n;
                        index += 4;
                    }
                    break;

                  case 'c':
                    c = s[index++];
                    c = Character.toUpperCase(c);
                    c = (char) (c ^ 64); // JS_TOCTRL
                    break;
                }
            }

            if (inrange) {
                if (lastc > c) {
                    throw Context.reportRuntimeError
                        (ScriptRuntime.getMessage("msg.bad.range",
                                            null));
                }
                inrange = false;
            } else {
                // Set lastc so we match just c's bit in the for loop.
                lastc = c;

                // [balance:
                if (index + 1 < end && s[index] == '-' &&
                    s[index+1] != ']')
                {
                    index++;
                    inrange = true;
                    continue;
                }
            }

            // Match characters in the range [lastc, c].
            for (; lastc <= c; lastc++) {
                matchBit(lastc, fill);
                if (fold) {
                    /*
                     * Must do both upper and lower for Turkish dotless i,
                     * Georgian, etc.
                     */
                    char foldc = Character.toUpperCase(lastc);
                    matchBit(foldc, fill);
                    foldc = Character.toLowerCase(foldc);
                    matchBit(foldc, fill);
                }
            }
            lastc = c;
        }
    }
    byte            op;         /* packed r.e. op bytecode */
    byte            flags;      /* flags, see below */
    short           offset;     /* bytecode offset */
    RENode          next;       /* next in concatenation order */
    Object          kid;        /* first operand */
    int             kid2;       /* second operand */
    int             num;        /* could be a number */
    char            chr;        /* or a char */
    short           min,max;    /* or a range */
    short           kidlen;     /* length of string at kid, in chars */
    short           bmsize;     /* bitmap size, based on max char code */
    char[]          s;          /* if null, use state.source */
    byte[]          bitmap;     /* cclass bitmap */
}


class MatchState {
    boolean     anchoring;              /* true if multiline anchoring ^/$ */
    int         pcend;                  /* pc limit (fencepost) */
    int         cpbegin, cpend;         /* cp base address and limit */
    int         start;                  /* offset from cpbegin to start at */
    int         skipped;                /* chars skipped anchoring this r.e. */
    byte        flags;                  /* pennants  */
    int         parenCount;             /* number of paren substring matches */
    SubString[] maybeParens;            /* possible paren substring pointers */
    SubString[] parens;                 /* certain paren substring matches */
}

