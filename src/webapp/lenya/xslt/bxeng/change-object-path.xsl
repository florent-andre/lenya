<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: change-object-path.xsl,v 1.2 2004/03/13 13:09:52 gregor Exp $ -->
    
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    >
  <xsl:param name="documentid" />
  
  
  <xsl:variable name="nodeid">
    <xsl:call-template name="getnodeid">
      <xsl:with-param name="url" select="$documentid"/>
    </xsl:call-template>
  </xsl:variable>
  
  
  <xsl:template match="xhtml:object/@data">
    <xsl:attribute name="data">
      <xsl:value-of select="concat($nodeid, '/', .)" />
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template name="getnodeid">
    <xsl:param name="url" />
    <xsl:variable name="slash">/</xsl:variable>
    <xsl:choose>
      <xsl:when test="contains($url, $slash)">
        <xsl:call-template name="getnodeid">
          <xsl:with-param name="url" select="substring-after($url, $slash)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$url" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>

