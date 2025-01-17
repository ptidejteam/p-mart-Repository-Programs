<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"                
                xmlns:sql="org.apache.xalan.lib.sql.XConnection"                
                extension-element-prefixes="sql">
                
<xsl:output method="html" indent="yes"/>
<xsl:param name="driver" select="'org.enhydra.instantdb.jdbc.idbDriver'"/>
<xsl:param name="datasource" select="'jdbc:idb:../../instantdb/sample.prp'"/>
<!-- Build an invalid query --><xsl:param name="query" select="'SELECT * FROM import1X'"/>  
  <xsl:template match="/">
  <!-- 1. Make the connection -->    
  <xsl:variable name="db" select="sql:new($driver, $datasource)"/>
  <!--2. Execute the query -->		
  <xsl:variable name="table" select='sql:query($db, $query)'/>       	
  <xsl:apply-templates select="$table/row-set" /> 		
  <xsl:apply-templates select="$table/ext-error" />        
  <!-- 3. Close the connection -->    
  <xsl:value-of select="sql:close($db)"/>  
  </xsl:template>
  
  <xsl:template match="row-set">    
  <HTML>
    <HEAD>
       <TITLE>List of products</TITLE>
    </HEAD>      
    <BODY>
      <TABLE border="1">
        <TR>             
          <xsl:for-each select="/row-set/column-header">               
            <TH><xsl:value-of select="@column-label"/></TH>             
          </xsl:for-each>          
        </TR>          
        <xsl:apply-templates select="/row-set/row"/>        
      </TABLE>      
    </BODY>    
  </HTML>
  </xsl:template>
  
  <xsl:template match="row">  
    <TR><xsl:apply-templates select="col"/></TR>
  </xsl:template>
  
  <xsl:template match="col">  
    <TD><xsl:value-of select="text()"/></TD>
  </xsl:template>
  
  <xsl:template match="/ext-error">	
  <xsl:text>Woops, an error occured: </xsl:text>	
  <xsl:apply-templates select="/ext-error/exception-info/message" />	
    <xsl:text> -- SQL Error Code: </xsl:text>	
    <xsl:apply-templates select="/ext-error/sql-error/error-code"/>
  </xsl:template>
</xsl:stylesheet>