<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:sql="org.apache.xalan.lib.sql.XConnection"
                extension-element-prefixes="sql">

<xsl:output method="html" indent="yes"/>

<!-- Build a Parameter Type Query -->
<xsl:param name="q1" select="'SELECT * FROM import1 where ProductID = ?'"/>

<!-- Build a CSV list of parameter types -->
<xsl:param name="q1type" select="int" />

<!-- Pull out connection information from the Document Source -->
<xsl:param name="cinfo" select="//DBINFO" />

<xsl:template match="/">
    <xsl:variable name="db" select="sql:new($cinfo)"/>
    <HTML>
      <HEAD>
        <TITLE>List of products</TITLE>
      </HEAD>
      <BODY>
        <TABLE border="1">
          <xsl:variable name="qparam" select="//QUERY"/>
          <xsl:value-of select="sql:addParameterFromElement($db, $qparam)"/>
          <xsl:variable name="table" select='sql:pquery($db, $q1, $q1type )'/>
          <TR>
             <xsl:for-each select="$table/row-set/column-header">
               <TH><xsl:value-of select="@column-label"/></TH>
             </xsl:for-each>
          </TR>
          <xsl:apply-templates select="$table/row-set/row"/>
        </TABLE>
      </BODY>
    </HTML>
    <xsl:value-of select="sql:close($db)"/>
</xsl:template>

<xsl:template match="row">
  <TR><xsl:apply-templates select="col"/></TR>
</xsl:template>

<xsl:template match="col">
  <TD><xsl:value-of select="text()"/></TD>
</xsl:template>

</xsl:stylesheet>