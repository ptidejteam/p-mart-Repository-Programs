<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0">

  <xsl:param name="label"/>

  <xsl:template match="/">
    <project>
      <xsl:apply-templates/>
    </project>
  </xsl:template>

  <xsl:template match="img">
    <resource source="{@src}" target="images/{@src}"/>
  </xsl:template>

  <xsl:template match="node()">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>