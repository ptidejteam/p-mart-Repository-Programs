<?xml version="1.0" standalone='no'?>

<!--
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 -->

<!-- Formatting instructions for XSL Readme page -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:param name="stylebook.project"/>
  
  <!-- Root template - start processing here -->
  <xsl:template match="/">
    <HTML>
      <HEAD>
      </HEAD>
      <BODY>
		    <xsl:apply-templates/>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="s1">
    <xsl:apply-templates select="s2"/>
  </xsl:template>

  <xsl:template match="s3|s4|s5">
    <xsl:apply-templates/>
  </xsl:template>
 
   
  <!-- ================================================================= -->
  <!-- Match P, NOTE, UL, OL... (blocks.ent)                             -->
  <!-- ================================================================= -->
  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="note">
    <table border="0" width="100%">
      <tr>
        <td width="20">&#160;</td>
        <td bgcolor="#88aacc">
          <font size="-1"><i>NOTE: <xsl:apply-templates/></i></font>
        </td>
        <td width="20">&#160;</td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="ul">
    <ul><xsl:apply-templates/></ul>
  </xsl:template>

  <xsl:template match="ol">
    <ol><xsl:apply-templates/></ol>
  </xsl:template>
  
  <xsl:template match="gloss">
    <dl><xsl:apply-templates/></dl>
  </xsl:template>
   <!-- <term> contains a single-word, multi-word or symbolic 
       designation which is regarded as a technical term. --> 
  <xsl:template match="term">
    <dfn><xsl:apply-templates/></dfn>
  </xsl:template>

  <xsl:template match="label" priority="1">
    <dt><xsl:apply-templates/></dt>
  </xsl:template>

  <xsl:template match="item" priority="2">
    <dd>
      <xsl:apply-templates/>
    </dd>
  </xsl:template>

  <xsl:template match="table">
    <p align="center"><table border="0"><xsl:apply-templates/></table></p>
  </xsl:template>

  <xsl:template match="source">
    <table border="0" width="100%">
      <tr>
        <td width="20">&#160;</td>
        <td bgcolor="#88aacc"><pre><xsl:apply-templates/></pre></td>
        <td width="20">&#160;</td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="li">
    <li><xsl:apply-templates/></li>
  </xsl:template>

  <xsl:template match="tr">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

  <xsl:template match="th">
    <td bgcolor="#006699" align="center">
      <font color="#ffffff"><b><xsl:apply-templates/></b></font>
    </td>
  </xsl:template>

  <xsl:template match="td">
    <td bgcolor="#88aacc"><xsl:apply-templates/>&#160;</td>
  </xsl:template>

  <xsl:template match="tn">
    <td>&#160;</td>
  </xsl:template>

  <!-- ================================================================= -->
  <!-- Match LINK, ANCHOR or JUMP and IMG (links.ent)                    -->
  <!-- ================================================================= -->
  <xsl:template match="em">
    <b><xsl:apply-templates/></b>
  </xsl:template>

  <xsl:template match="ref">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="code">
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <xsl:template match="br">
    <br/>
  </xsl:template>


  <!-- ================================================================= -->
  <!-- Match LINK, ANCHOR or JUMP and IMG (links.ent)                    -->
  <!-- ================================================================= -->
  <xsl:template match="link">
    <xsl:if test="string-length(@anchor)=0">
      <xsl:if test="string-length(@idref)=0">
        <xsl:apply-templates/>
      </xsl:if>
      <xsl:if test="string-length(@idref)>0">
        <a href="{@idref}.html"><xsl:apply-templates/></a>
      </xsl:if>
    </xsl:if>

    <xsl:if test="string-length(@anchor)>0">
      <xsl:if test="string-length(@idref)=0">
        <a href="#{@anchor}"><xsl:apply-templates/></a>
      </xsl:if>
      <xsl:if test="string-length(@idref)>0">
        <a href="{@idref}.html#{@anchor}"><xsl:apply-templates/></a>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="anchor">
    <a name="{@name}"><xsl:comment>anchor</xsl:comment></a>
  </xsl:template>

  <xsl:template match="jump">
    <a href="{@href}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="img">
    <img src="images/{@src}" border="0" vspace="4" hspace="4" align="right"/>
  </xsl:template>

  <xsl:template match="resource-ref">
    <xsl:variable name="resourceFile" 
          select="document($stylebook.project)/javadocPackages/resources/@source"/>
    <xsl:variable name="xref" select="@idref"/>
    <xsl:variable name="href"
          select="document($resourceFile)/resources/resource[@id=$xref]/@location"/>
    <xsl:variable name="label"
          select="document($resourceFile)/resources/resource[@id=$xref]/@title"/>
    <A href="{$href}" target="_top"><xsl:value-of select="$label"/></A>
  </xsl:template>

  <xsl:template match="human-resource-ref">
    <xsl:variable name="resourceFile" 
          select="document($stylebook.project)/javadocPackages/resources/@source"/>  
    <xsl:variable name="ref"  select="@idref"/>
    <xsl:variable name="mailto"
          select="document($resourceFile)/resources/human-resource[@id=$ref]/@mailto"/>
   <xsl:variable name="name"
          select="document($resourceFile)/resources/human-resource[@id=$ref]/@name"/>                          
    <A href="mailto:{$mailto}"><xsl:value-of select="$name"/></A>
  </xsl:template>

</xsl:stylesheet>