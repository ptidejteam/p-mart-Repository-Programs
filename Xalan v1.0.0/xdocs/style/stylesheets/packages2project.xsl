<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0" version="1.0">
  <xsl:template match="javadocPackages">
    <project>
      <xsl:apply-templates/>      
    </project>
  </xsl:template>

<!-- ********************************************************************** -->
<!-- CREATE THE TARGET HTML -->
<!-- ********************************************************************** -->

  <xsl:template match="home|package">
    <create source="{@source}" target="{/javadocPackages/@target}/{@target}" producer="parser" printer="html">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/package2html.xsl"/>
      </processor>
    </create>
  </xsl:template>

</xsl:stylesheet>