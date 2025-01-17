 /*
 * Created on Apr 12, 2004
 * Created by Olivier Chalouhi
 * Modified Apr 13, 2004 by Alon Rohter
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 * 
 */
 
package org.gudy.azureus2.core3.util;

import java.nio.ByteBuffer;


/**
 * SHA-1 message digest class.
 */
public final class SHA1 {

  private int h0,h1,h2,h3,h4;
  private ByteBuffer finalBuffer;
  
  private ByteBuffer saveBuffer;
  private int s0,s1,s2,s3,s4;
  
  long length;
  long saveLength;
  
  
  /**
   * Create a new SHA-1 message digest hasher.
   */
  public SHA1() {
    finalBuffer = ByteBuffer.allocateDirect(64);
    finalBuffer.position(0);
    finalBuffer.limit(64);
    
    saveBuffer = ByteBuffer.allocateDirect(64);
    saveBuffer.position(0);
    saveBuffer.limit(64);
    
    reset();
  }
  
  
  private void transform(ByteBuffer M) {
    int w0 , w1 , w2 , w3 ,  w4 , w5 , w6 , w7 , w8 , w9 ,
    w10, w11, w12, w13, w14, w15;
    
    int a,b,c,d,e;
    
    w0 = M.getInt();
    w1 = M.getInt();
    w2 = M.getInt();
    w3 = M.getInt();
    w4 = M.getInt();
    w5 = M.getInt();
    w6 = M.getInt();
    w7 = M.getInt();
    w8 = M.getInt();
    w9 = M.getInt();
    w10 = M.getInt();
    w11 = M.getInt();
    w12 = M.getInt();
    w13 = M.getInt();
    w14 = M.getInt();
    w15 = M.getInt();
    
    a = h0 ; b = h1 ; c = h2 ; d = h3 ; e = h4;
    e += ((a << 5) | ( a >>> 27)) + w0 + ((b & c) | ((~b ) & d)) + 0x5A827999 ;
    b = (b << 30) | (b >>> 2) ;
    d += ((e << 5) | ( e >>> 27)) + w1 + ((a & b) | ((~a ) & c)) + 0x5A827999 ;
    a = (a << 30) | (a >>> 2) ;
    c += ((d << 5) | ( d >>> 27)) + w2 + ((e & a) | ((~e ) & b)) + 0x5A827999 ;
    e = (e << 30) | (e >>> 2) ;
    b += ((c << 5) | ( c >>> 27)) + w3 + ((d & e) | ((~d ) & a)) + 0x5A827999 ;
    d = (d << 30) | (d >>> 2) ;
    a += ((b << 5) | ( b >>> 27)) + w4 + ((c & d) | ((~c ) & e)) + 0x5A827999 ;
    c = (c << 30) | (c >>> 2) ;
    e += ((a << 5) | ( a >>> 27)) + w5 + ((b & c) | ((~b ) & d)) + 0x5A827999 ;
    b = (b << 30) | (b >>> 2) ;
    d += ((e << 5) | ( e >>> 27)) + w6 + ((a & b) | ((~a ) & c)) + 0x5A827999 ;
    a = (a << 30) | (a >>> 2) ;
    c += ((d << 5) | ( d >>> 27)) + w7 + ((e & a) | ((~e ) & b)) + 0x5A827999 ;
    e = (e << 30) | (e >>> 2) ;
    b += ((c << 5) | ( c >>> 27)) + w8 + ((d & e) | ((~d ) & a)) + 0x5A827999 ;
    d = (d << 30) | (d >>> 2) ;
    a += ((b << 5) | ( b >>> 27)) + w9 + ((c & d) | ((~c ) & e)) + 0x5A827999 ;
    c = (c << 30) | (c >>> 2) ;
    e += ((a << 5) | ( a >>> 27)) + w10 + ((b & c) | ((~b ) & d)) + 0x5A827999 ;
    b = (b << 30) | (b >>> 2) ;
    d += ((e << 5) | ( e >>> 27)) + w11 + ((a & b) | ((~a ) & c)) + 0x5A827999 ;
    a = (a << 30) | (a >>> 2) ;
    c += ((d << 5) | ( d >>> 27)) + w12 + ((e & a) | ((~e ) & b)) + 0x5A827999 ;
    e = (e << 30) | (e >>> 2) ;
    b += ((c << 5) | ( c >>> 27)) + w13 + ((d & e) | ((~d ) & a)) + 0x5A827999 ;
    d = (d << 30) | (d >>> 2) ;
    a += ((b << 5) | ( b >>> 27)) + w14 + ((c & d) | ((~c ) & e)) + 0x5A827999 ;
    c = (c << 30) | (c >>> 2) ;
    e += ((a << 5) | ( a >>> 27)) + w15 + ((b & c) | ((~b ) & d)) + 0x5A827999 ;
    b = (b << 30) | (b >>> 2) ;
    w0 = w13 ^ w8 ^ w2 ^ w0; w0 = (w0 << 1) | (w0 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w0 + ((a & b) | ((~a ) & c)) + 0x5A827999 ;
    a = (a << 30) | (a >>> 2) ;
    w1 = w14 ^ w9 ^ w3 ^ w1; w1 = (w1 << 1) | (w1 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w1 + ((e & a) | ((~e ) & b)) + 0x5A827999 ;
    e = (e << 30) | (e >>> 2) ;
    w2 = w15 ^ w10 ^ w4 ^ w2; w2 = (w2 << 1) | (w2 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w2 + ((d & e) | ((~d ) & a)) + 0x5A827999 ;
    d = (d << 30) | (d >>> 2) ;
    w3 = w0 ^ w11 ^ w5 ^ w3; w3 = (w3 << 1) | (w3 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w3 + ((c & d) | ((~c ) & e)) + 0x5A827999 ;
    c = (c << 30) | (c >>> 2) ;
    w4 = w1 ^ w12 ^ w6 ^ w4; w4 = (w4 << 1) | (w4 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w4 + (b ^ c ^ d) + 0x6ED9EBA1 ;
    b = (b << 30) | (b >>> 2) ;
    w5 = w2 ^ w13 ^ w7 ^ w5; w5 = (w5 << 1) | (w5 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w5 + (a ^ b ^ c) + 0x6ED9EBA1 ;
    a = (a << 30) | (a >>> 2) ;
    w6 = w3 ^ w14 ^ w8 ^ w6; w6 = (w6 << 1) | (w6 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w6 + (e ^ a ^ b) + 0x6ED9EBA1 ;
    e = (e << 30) | (e >>> 2) ;
    w7 = w4 ^ w15 ^ w9 ^ w7; w7 = (w7 << 1) | (w7 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w7 + (d ^ e ^ a) + 0x6ED9EBA1 ;
    d = (d << 30) | (d >>> 2) ;
    w8 = w5 ^ w0 ^ w10 ^ w8; w8 = (w8 << 1) | (w8 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w8 + (c ^ d ^ e) + 0x6ED9EBA1 ;
    c = (c << 30) | (c >>> 2) ;
    w9 = w6 ^ w1 ^ w11 ^ w9; w9 = (w9 << 1) | (w9 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w9 + (b ^ c ^ d) + 0x6ED9EBA1 ;
    b = (b << 30) | (b >>> 2) ;
    w10 = w7 ^ w2 ^ w12 ^ w10; w10 = (w10 << 1) | (w10 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w10 + (a ^ b ^ c) + 0x6ED9EBA1 ;
    a = (a << 30) | (a >>> 2) ;
    w11 = w8 ^ w3 ^ w13 ^ w11; w11 = (w11 << 1) | (w11 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w11 + (e ^ a ^ b) + 0x6ED9EBA1 ;
    e = (e << 30) | (e >>> 2) ;
    w12 = w9 ^ w4 ^ w14 ^ w12; w12 = (w12 << 1) | (w12 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w12 + (d ^ e ^ a) + 0x6ED9EBA1 ;
    d = (d << 30) | (d >>> 2) ;
    w13 = w10 ^ w5 ^ w15 ^ w13; w13 = (w13 << 1) | (w13 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w13 + (c ^ d ^ e) + 0x6ED9EBA1 ;
    c = (c << 30) | (c >>> 2) ;
    w14 = w11 ^ w6 ^ w0 ^ w14; w14 = (w14 << 1) | (w14 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w14 + (b ^ c ^ d) + 0x6ED9EBA1 ;
    b = (b << 30) | (b >>> 2) ;
    w15 = w12 ^ w7 ^ w1 ^ w15; w15 = (w15 << 1) | (w15 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w15 + (a ^ b ^ c) + 0x6ED9EBA1 ;
    a = (a << 30) | (a >>> 2) ;
    w0 = w13 ^ w8 ^ w2 ^ w0; w0 = (w0 << 1) | (w0 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w0 + (e ^ a ^ b) + 0x6ED9EBA1 ;
    e = (e << 30) | (e >>> 2) ;
    w1 = w14 ^ w9 ^ w3 ^ w1; w1 = (w1 << 1) | (w1 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w1 + (d ^ e ^ a) + 0x6ED9EBA1 ;
    d = (d << 30) | (d >>> 2) ;
    w2 = w15 ^ w10 ^ w4 ^ w2; w2 = (w2 << 1) | (w2 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w2 + (c ^ d ^ e) + 0x6ED9EBA1 ;
    c = (c << 30) | (c >>> 2) ;
    w3 = w0 ^ w11 ^ w5 ^ w3; w3 = (w3 << 1) | (w3 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w3 + (b ^ c ^ d) + 0x6ED9EBA1 ;
    b = (b << 30) | (b >>> 2) ;
    w4 = w1 ^ w12 ^ w6 ^ w4; w4 = (w4 << 1) | (w4 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w4 + (a ^ b ^ c) + 0x6ED9EBA1 ;
    a = (a << 30) | (a >>> 2) ;
    w5 = w2 ^ w13 ^ w7 ^ w5; w5 = (w5 << 1) | (w5 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w5 + (e ^ a ^ b) + 0x6ED9EBA1 ;
    e = (e << 30) | (e >>> 2) ;
    w6 = w3 ^ w14 ^ w8 ^ w6; w6 = (w6 << 1) | (w6 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w6 + (d ^ e ^ a) + 0x6ED9EBA1 ;
    d = (d << 30) | (d >>> 2) ;
    w7 = w4 ^ w15 ^ w9 ^ w7; w7 = (w7 << 1) | (w7 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w7 + (c ^ d ^ e) + 0x6ED9EBA1 ;
    c = (c << 30) | (c >>> 2) ;
    w8 = w5 ^ w0 ^ w10 ^ w8; w8 = (w8 << 1) | (w8 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w8 + ((b & c) | (b & d) | (c & d)) + 0x8F1BBCDC ;
    b = (b << 30) | (b >>> 2) ;
    w9 = w6 ^ w1 ^ w11 ^ w9; w9 = (w9 << 1) | (w9 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w9 + ((a & b) | (a & c) | (b & c)) + 0x8F1BBCDC ;
    a = (a << 30) | (a >>> 2) ;
    w10 = w7 ^ w2 ^ w12 ^ w10; w10 = (w10 << 1) | (w10 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w10 + ((e & a) | (e & b) | (a & b)) + 0x8F1BBCDC ;
    e = (e << 30) | (e >>> 2) ;
    w11 = w8 ^ w3 ^ w13 ^ w11; w11 = (w11 << 1) | (w11 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w11 + ((d & e) | (d & a) | (e & a)) + 0x8F1BBCDC ;
    d = (d << 30) | (d >>> 2) ;
    w12 = w9 ^ w4 ^ w14 ^ w12; w12 = (w12 << 1) | (w12 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w12 + ((c & d) | (c & e) | (d & e)) + 0x8F1BBCDC ;
    c = (c << 30) | (c >>> 2) ;
    w13 = w10 ^ w5 ^ w15 ^ w13; w13 = (w13 << 1) | (w13 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w13 + ((b & c) | (b & d) | (c & d)) + 0x8F1BBCDC ;
    b = (b << 30) | (b >>> 2) ;
    w14 = w11 ^ w6 ^ w0 ^ w14; w14 = (w14 << 1) | (w14 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w14 + ((a & b) | (a & c) | (b & c)) + 0x8F1BBCDC ;
    a = (a << 30) | (a >>> 2) ;
    w15 = w12 ^ w7 ^ w1 ^ w15; w15 = (w15 << 1) | (w15 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w15 + ((e & a) | (e & b) | (a & b)) + 0x8F1BBCDC ;
    e = (e << 30) | (e >>> 2) ;
    w0 = w13 ^ w8 ^ w2 ^ w0; w0 = (w0 << 1) | (w0 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w0 + ((d & e) | (d & a) | (e & a)) + 0x8F1BBCDC ;
    d = (d << 30) | (d >>> 2) ;
    w1 = w14 ^ w9 ^ w3 ^ w1; w1 = (w1 << 1) | (w1 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w1 + ((c & d) | (c & e) | (d & e)) + 0x8F1BBCDC ;
    c = (c << 30) | (c >>> 2) ;
    w2 = w15 ^ w10 ^ w4 ^ w2; w2 = (w2 << 1) | (w2 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w2 + ((b & c) | (b & d) | (c & d)) + 0x8F1BBCDC ;
    b = (b << 30) | (b >>> 2) ;
    w3 = w0 ^ w11 ^ w5 ^ w3; w3 = (w3 << 1) | (w3 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w3 + ((a & b) | (a & c) | (b & c)) + 0x8F1BBCDC ;
    a = (a << 30) | (a >>> 2) ;
    w4 = w1 ^ w12 ^ w6 ^ w4; w4 = (w4 << 1) | (w4 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w4 + ((e & a) | (e & b) | (a & b)) + 0x8F1BBCDC ;
    e = (e << 30) | (e >>> 2) ;
    w5 = w2 ^ w13 ^ w7 ^ w5; w5 = (w5 << 1) | (w5 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w5 + ((d & e) | (d & a) | (e & a)) + 0x8F1BBCDC ;
    d = (d << 30) | (d >>> 2) ;
    w6 = w3 ^ w14 ^ w8 ^ w6; w6 = (w6 << 1) | (w6 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w6 + ((c & d) | (c & e) | (d & e)) + 0x8F1BBCDC ;
    c = (c << 30) | (c >>> 2) ;
    w7 = w4 ^ w15 ^ w9 ^ w7; w7 = (w7 << 1) | (w7 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w7 + ((b & c) | (b & d) | (c & d)) + 0x8F1BBCDC ;
    b = (b << 30) | (b >>> 2) ;
    w8 = w5 ^ w0 ^ w10 ^ w8; w8 = (w8 << 1) | (w8 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w8 + ((a & b) | (a & c) | (b & c)) + 0x8F1BBCDC ;
    a = (a << 30) | (a >>> 2) ;
    w9 = w6 ^ w1 ^ w11 ^ w9; w9 = (w9 << 1) | (w9 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w9 + ((e & a) | (e & b) | (a & b)) + 0x8F1BBCDC ;
    e = (e << 30) | (e >>> 2) ;
    w10 = w7 ^ w2 ^ w12 ^ w10; w10 = (w10 << 1) | (w10 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w10 + ((d & e) | (d & a) | (e & a)) + 0x8F1BBCDC ;
    d = (d << 30) | (d >>> 2) ;
    w11 = w8 ^ w3 ^ w13 ^ w11; w11 = (w11 << 1) | (w11 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w11 + ((c & d) | (c & e) | (d & e)) + 0x8F1BBCDC ;
    c = (c << 30) | (c >>> 2) ;
    w12 = w9 ^ w4 ^ w14 ^ w12; w12 = (w12 << 1) | (w12 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w12 + (b ^ c ^ d) + 0xCA62C1D6 ;
    b = (b << 30) | (b >>> 2) ;
    w13 = w10 ^ w5 ^ w15 ^ w13; w13 = (w13 << 1) | (w13 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w13 + (a ^ b ^ c) + 0xCA62C1D6 ;
    a = (a << 30) | (a >>> 2) ;
    w14 = w11 ^ w6 ^ w0 ^ w14; w14 = (w14 << 1) | (w14 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w14 + (e ^ a ^ b) + 0xCA62C1D6 ;
    e = (e << 30) | (e >>> 2) ;
    w15 = w12 ^ w7 ^ w1 ^ w15; w15 = (w15 << 1) | (w15 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w15 + (d ^ e ^ a) + 0xCA62C1D6 ;
    d = (d << 30) | (d >>> 2) ;
    w0 = w13 ^ w8 ^ w2 ^ w0; w0 = (w0 << 1) | (w0 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w0 + (c ^ d ^ e) + 0xCA62C1D6 ;
    c = (c << 30) | (c >>> 2) ;
    w1 = w14 ^ w9 ^ w3 ^ w1; w1 = (w1 << 1) | (w1 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w1 + (b ^ c ^ d) + 0xCA62C1D6 ;
    b = (b << 30) | (b >>> 2) ;
    w2 = w15 ^ w10 ^ w4 ^ w2; w2 = (w2 << 1) | (w2 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w2 + (a ^ b ^ c) + 0xCA62C1D6 ;
    a = (a << 30) | (a >>> 2) ;
    w3 = w0 ^ w11 ^ w5 ^ w3; w3 = (w3 << 1) | (w3 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w3 + (e ^ a ^ b) + 0xCA62C1D6 ;
    e = (e << 30) | (e >>> 2) ;
    w4 = w1 ^ w12 ^ w6 ^ w4; w4 = (w4 << 1) | (w4 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w4 + (d ^ e ^ a) + 0xCA62C1D6 ;
    d = (d << 30) | (d >>> 2) ;
    w5 = w2 ^ w13 ^ w7 ^ w5; w5 = (w5 << 1) | (w5 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w5 + (c ^ d ^ e) + 0xCA62C1D6 ;
    c = (c << 30) | (c >>> 2) ;
    w6 = w3 ^ w14 ^ w8 ^ w6; w6 = (w6 << 1) | (w6 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w6 + (b ^ c ^ d) + 0xCA62C1D6 ;
    b = (b << 30) | (b >>> 2) ;
    w7 = w4 ^ w15 ^ w9 ^ w7; w7 = (w7 << 1) | (w7 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w7 + (a ^ b ^ c) + 0xCA62C1D6 ;
    a = (a << 30) | (a >>> 2) ;
    w8 = w5 ^ w0 ^ w10 ^ w8; w8 = (w8 << 1) | (w8 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w8 + (e ^ a ^ b) + 0xCA62C1D6 ;
    e = (e << 30) | (e >>> 2) ;
    w9 = w6 ^ w1 ^ w11 ^ w9; w9 = (w9 << 1) | (w9 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w9 + (d ^ e ^ a) + 0xCA62C1D6 ;
    d = (d << 30) | (d >>> 2) ;
    w10 = w7 ^ w2 ^ w12 ^ w10; w10 = (w10 << 1) | (w10 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w10 + (c ^ d ^ e) + 0xCA62C1D6 ;
    c = (c << 30) | (c >>> 2) ;
    w11 = w8 ^ w3 ^ w13 ^ w11; w11 = (w11 << 1) | (w11 >>> 31) ;
    e += ((a << 5) | ( a >>> 27)) + w11 + (b ^ c ^ d) + 0xCA62C1D6 ;
    b = (b << 30) | (b >>> 2) ;
    w12 = w9 ^ w4 ^ w14 ^ w12; w12 = (w12 << 1) | (w12 >>> 31) ;
    d += ((e << 5) | ( e >>> 27)) + w12 + (a ^ b ^ c) + 0xCA62C1D6 ;
    a = (a << 30) | (a >>> 2) ;
    w13 = w10 ^ w5 ^ w15 ^ w13; w13 = (w13 << 1) | (w13 >>> 31) ;
    c += ((d << 5) | ( d >>> 27)) + w13 + (e ^ a ^ b) + 0xCA62C1D6 ;
    e = (e << 30) | (e >>> 2) ;
    w14 = w11 ^ w6 ^ w0 ^ w14; w14 = (w14 << 1) | (w14 >>> 31) ;
    b += ((c << 5) | ( c >>> 27)) + w14 + (d ^ e ^ a) + 0xCA62C1D6 ;
    d = (d << 30) | (d >>> 2) ;
    w15 = w12 ^ w7 ^ w1 ^ w15; w15 = (w15 << 1) | (w15 >>> 31) ;
    a += ((b << 5) | ( b >>> 27)) + w15 + (c ^ d ^ e) + 0xCA62C1D6 ;
    c = (c << 30) | (c >>> 2) ;

    h0 += a;
    h1 += b;
    h2 += c;
    h3 += d;
    h4 += e;
  }
  
  
  private void completeFinalBuffer(ByteBuffer buffer) {
    if(finalBuffer.position() == 0) 
      return;
    
    while(buffer.remaining() > 0 && finalBuffer.remaining() > 0) {
      finalBuffer.put(buffer.get());
    }
    
    if(finalBuffer.remaining() == 0) {
      finalBuffer.position(0);
      transform(finalBuffer);
      finalBuffer.position(0);
    }
  }
  
  
  
  /**
   * Resets the SHA-1 to initial state for a new message digest calculation.
   * Must be called before starting a new hash calculation.
   */
  public void reset() {
    h0 = 0x67452301;
    h1 = 0xEFCDAB89;
    h2 = 0x98BADCFE;
    h3 = 0x10325476;    
    h4 = 0xC3D2E1F0;    
    
    length = 0;
    
    finalBuffer.clear();
  }
  
  
  /**
   * Starts or continues a SHA-1 message digest calculation.
   * Only the remaining bytes of the given ByteBuffer are used.
   * @param buffer input data
   */
  public void update(ByteBuffer buffer) {
    length += buffer.remaining();
    //Save current position to leave given buffer unchanged
    int position = buffer.position();
    
    //Complete the final buffer if needed
    completeFinalBuffer(buffer);
    
    while(buffer.remaining() >= 64) {
      transform(buffer);
    }
    
    if(buffer.remaining() != 0) {
      finalBuffer.put(buffer);
    }
    
    buffer.position(position);
  }
  
  
  /**
   * Finishes the SHA-1 message digest calculation.
   * @return 20-byte hash result
   */
  public byte[] digest() {
    byte[] result = new byte[20];
    
    finalBuffer.put((byte)0x80);
    if(finalBuffer.remaining() < 8) {
      while(finalBuffer.remaining() > 0) {
        finalBuffer.put((byte)0);
      }
      finalBuffer.position(0);
      transform(finalBuffer);
      finalBuffer.position(0);
    }
    
    while(finalBuffer.remaining() > 8) {
      finalBuffer.put((byte)0);
    }
    
    finalBuffer.putLong(length << 3);
    finalBuffer.position(0);
    transform(finalBuffer);
    
    finalBuffer.position(0);
    finalBuffer.putInt(h0);
    finalBuffer.putInt(h1);
    finalBuffer.putInt(h2);
    finalBuffer.putInt(h3);
    finalBuffer.putInt(h4);    
    finalBuffer.position(0);
    
    for(int i  = 0 ; i < 20 ; i++) {
     result[i] = finalBuffer.get(); 
    }
    
    return result;
  }
  
  
  /**
   * Finishes the SHA-1 message digest calculation, by first performing a final update
   * from the given input buffer, then completing the calculation as with digest().
   * @param buffer input data
   * @return 20-byte hash result
   */
  public byte[] digest(ByteBuffer buffer) {
    update( buffer );
    return digest();
  }
  
  
  
  /**
   * Save the current digest state.
   * This allows the resuming of a SHA-1 calculation, even after a digest calculation
   * is finished with digest().
   */
  public void saveState() {
    s0=h0;
    s1=h1;
    s2=h2;
    s3=h3;
    s4=h4;
    
    saveLength = length;
    
    int position = finalBuffer.position();
    
    finalBuffer.position(0);
    finalBuffer.limit(position);
    
    saveBuffer.clear();
    saveBuffer.put(finalBuffer);
    saveBuffer.flip();
    
    finalBuffer.limit(64);
    finalBuffer.position( position );
  }
  
  
 /**
  * Restore the digest to its previously-saved state.
  */
  public void restoreState() {
    h0=s0;
    h1=s1;
    h2=s2;
    h3=s3;
    h4=s4;
    
    length = saveLength;
    
    finalBuffer.clear();
    finalBuffer.put(saveBuffer);
  }
 
  
}
