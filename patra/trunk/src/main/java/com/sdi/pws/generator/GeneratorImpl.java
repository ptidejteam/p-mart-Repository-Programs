/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.generator;

import com.sdi.crypto.sha.SHA;

import java.util.Random;

public class GeneratorImpl
implements Generator
{
    private int length = 8;
    private boolean isReadable = false;
    private boolean isMixedCase = true;
    private boolean isNumbersIncluded = true;
    private boolean isPunctuationIncluded = false;
    private byte[] entropy = new byte[]{2, 5, 3, 1, 2, 6, 7, 4, 5, 6 , 7, 5, 4};
    private StringBuilder alphabet = null;
    private double alphabetQuality = 0.0;

    private static final String charUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String charLow =  "abcdefghijklmnopqrstuvwxyz";
    private static final String vowels = "aeiou";
    private static final String numbers = "0123456789";
    private static final String punctuation = "<>\\/,;?=+_-()!#$%&*{}";
    private static final Random rnd = new Random(System.currentTimeMillis());

    public GeneratorImpl()
    {
        calculateAlphabet();
    }

    private void calculateAlphabet()
    {
        if(isReadable)
        {
            // Only lower case letters are used ...
            // The quality decreases fast!
            alphabet = new StringBuilder(charLow);
            alphabet.append(vowels).append(vowels); // Increase chanse to have vowels.
            alphabetQuality = Math.log(charLow.length())/Math.log(Math.E);
        }
        else
        {
            // Compose the alphabet.
            // The quality depends on the options.
            alphabet = new StringBuilder(charLow);
            if(isMixedCase) alphabet.append(charUp);
            if(isNumbersIncluded) alphabet.append(numbers);
            if(isPunctuationIncluded) alphabet.append(punctuation);
            alphabetQuality = Math.log(alphabet.length())/Math.log(Math.E);
        }
    }

    public String generate()
    {
        String lResult = null;
        SHA lSha = new SHA();
        byte[] lRndbuf = new byte[100];
        rnd.nextBytes(lRndbuf);

        lSha.update(lRndbuf, 0, lRndbuf.length);
        int lIters;

        if(isReadable)
        {
            // Note: we calculate one more than we have to because we need the
            // extra info if we need to reduce random bytes to a readable string;
            lIters = length * 3;
        }
        else
        {
            lIters = (int) Math.round(Math.ceil( (double) length / 20.0));
        }

        byte[] lPwdCandidate = new byte[lIters * 20];

        while((lResult == null) || (lResult.length() < length))
        {
            for(int i = 0; i < lIters; i++)
            {
                lSha.update(entropy[i % entropy.length]);
                lSha.update((byte) i);
                lSha.update((byte) rnd.nextInt());
                byte[] lDigest = lSha.digest();
                System.arraycopy(lDigest, 0, lPwdCandidate, i*20, lDigest.length);
            }

            // Translate the password bytes into a string.
            StringBuilder lPwdBuf = new StringBuilder();
            for(int i = 0; i < lPwdCandidate.length; i++)
            {
                int lSrc = lPwdCandidate[i] % alphabet.length();
                if(lSrc < 0) lSrc *= -1;
                char lPwdChar = alphabet.charAt(lSrc);
                lPwdBuf.append(lPwdChar);
            }

            // Do some further processing if the password has to be readable.
            if(isReadable)
            {
                String lOldTrial = null;
                String lNewTrial = lPwdBuf.toString();

                while(!lNewTrial.equals(lOldTrial))
                {
                    lOldTrial = lNewTrial;
                    lNewTrial = lNewTrial.replaceAll("([aeiou][aeiou])[aeiou]", "$1");
                    lNewTrial = lNewTrial.replaceAll("([^aeiou])[^aeiou]", "$1");

                    // Ad hoc adjustments.
                    lNewTrial = lNewTrial.replaceAll("ii", "i");
                    lNewTrial = lNewTrial.replaceAll("uo", "u");
                    lNewTrial = lNewTrial.replaceAll("ue", "u");
                    lNewTrial = lNewTrial.replaceAll("eo", "e");
                    lNewTrial = lNewTrial.replaceAll("ua", "a");
                }

                lResult =  lNewTrial;
            }
            else
            {
                lResult = lPwdBuf.toString();
            }
        }

        return lResult.substring(0, length);
    }

    public double getQuality()
    {
        return length * alphabetQuality;
    }

    public int getQualityCategory()
    {
        final double lQual = this.getQuality();
        final long lNrBytes = Math.round(lQual / 8);

        int lNrBytes2;
        if(lNrBytes > 11) lNrBytes2 = 11;
        else lNrBytes2 = (int) lNrBytes;

        return lNrBytes2;
    }

    // Getters and setters of password generating parameters.

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public boolean isReadable()
    {
        return isReadable;
    }

    public void setReadable(boolean readable)
    {
        isReadable = readable;
        calculateAlphabet();
    }

    public boolean isMixedCase()
    {
        return isMixedCase;
    }

    public void setMixedCase(boolean mixedCase)
    {
        isMixedCase = mixedCase;
        calculateAlphabet();
    }

    public boolean isNumbersIncluded()
    {
        return isNumbersIncluded;
    }

    public void setNumbersIncluded(boolean numbersIncluded)
    {
        isNumbersIncluded = numbersIncluded;
        calculateAlphabet();
    }

    public boolean isPunctuationIncluded()
    {
        return isPunctuationIncluded;
    }

    public void setPunctuationIncluded(boolean symbolsIncluded)
    {
        isPunctuationIncluded = symbolsIncluded;
        calculateAlphabet();
    }

    public byte[] getEntropy()
    {
        return entropy;
    }

    public void setEntropy(byte[] entropy)
    {
        this.entropy = entropy;
    }
}
