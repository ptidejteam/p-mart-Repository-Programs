<?xml version = '1.0' encoding = 'ISO-8859-1' ?>
<!-- Original Contribution by Dave Carlson (dcarlson@ontogenics.com) -->
<!-- Modified by Roy Feldman (roy@truehorizon.com) -->
<!-- Please send all corrections or additions to the MDR mail list users@mdr.netbeans.org -->
<xsl:stylesheet 
    xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:saxon = "http://icl.com/saxon"
    xmlns:date="http://xml.apache.org/xalan/java/java.util.Date"
    extension-element-prefixes = "xalan saxon date"
    exclude-result-prefixes = "xalan saxon date"
    version = "1.0" >

<!--
  <xsl:output method="xml" indent="yes" 
  		xalan:indent-amount="2" saxon:indent-spaces="2"/>
  <xsl:strip-space elements="*"/>
-->
  <xsl:preserve-space elements="*"/>
               
<!-- _______________________________________________________________ -->
<!--                                                                 -->
<!--    Create the key indexes                                       -->
<!-- _______________________________________________________________ -->

  <xsl:key name="xmi.id" match="*" use="@xmi.id"/>
  
  
<!-- _______________________________________________________________ -->
<!--                                                                 -->
<!--    Main templates                                               -->
<!-- _______________________________________________________________ -->

  <xsl:template match="/">
    <xsl:text>&#xa;</xsl:text>
    <xsl:variable name="now" select="date:new()"/>
    <xsl:comment> XMIClean'ed on:  <xsl:value-of select="date:toString($now)"/> </xsl:comment> 
    <xsl:text>&#xa;</xsl:text>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="XMI.content">
    <XMI.content>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:call-template name="writeTaggedValues-uml1.3"/>
    </XMI.content>
  </xsl:template>
  
  
  <!-- 
    * Skip all TaggedValue elements and process them at the end.
    * (all)
   -->
  <xsl:template match="Foundation.Extension_Mechanisms.TaggedValue" >
    <!--
  	<xsl:message>Skipping TaggedValue: <xsl:value-of select="Foundation.Extension_Mechanisms.TaggedValue.tag"/></xsl:message>
    -->
  </xsl:template>
     
  <xsl:include href="WriteNormalizedTaggedValues.xsl"/>
  
</xsl:stylesheet>
