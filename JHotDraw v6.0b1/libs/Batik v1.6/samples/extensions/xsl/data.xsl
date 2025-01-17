<?xml version="1.0" encoding="utf-8"?>

<!-- ====================================================================== 
     Copyright 2002 The Apache Software Foundation
     
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
     
         http://www.apache.org/licenses/LICENSE-2.0
     
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     
     See the License for the specific language governing permissions and
     limitations under the License.
     ====================================================================== -->

<!-- ========================================================================= -->
<!-- Courtesy of Max Froumentin. This document is referenced by data.xml       -->
<!--                                                                           -->
<!-- @author vincent.hardy@eng.sun.com                                         -->
<!-- @version $Id: data.xsl,v 1.1 2006/08/16 02:27:29 guehene Exp $                                                             -->
<!-- ========================================================================= -->
<?xml-stylesheet href="" type="text/xsl"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink" 
                version="1.0">

  <xsl:template match="/">
    <svg viewBox="-40 -40 80 80" width="1024" height="768" preserveAspectRatio="xMidYMid slice">
      <defs>
        <linearGradient id="gradient" gradientUnits="userSpaceOnUse"
          x1="-100" y1="-100" x2="100" y2="100">
          <stop offset="0" stop-color="crimson"/>
          <stop offset="0.8" stop-color="gold"/>
          <stop offset="1" stop-color="yellow" />
        </linearGradient>

        <linearGradient id="strokeGradient" gradientUnits="userSpaceOnUse"
          x1="100" y1="100" x2="-100" y2="-100">
          <stop offset=".2" stop-color="gold"/>
          <stop offset="1"  stop-color="rgb(128,0,0)" />
        </linearGradient>
      </defs>

      <g id="spiral">
        <xsl:call-template name="draw-primitive">
          <xsl:with-param name="depth" select="data[position()=1]/@depth"/>
        </xsl:call-template>
      </g>
    </svg>
  </xsl:template>

  <xsl:template name="draw-primitive">
    <xsl:param name="depth" select="0"/>
    <xsl:if test="$depth > 0">
      <g transform="scale(.8, .8) rotate(10)">
        <rect x="-100" y="-100" width="200" height="200" fill="url(#gradient)" stroke="none" stroke-width="5"/>
        <xsl:call-template name="draw-primitive">
          <xsl:with-param name="depth" select="$depth - 1"/>
        </xsl:call-template>            
      </g>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
