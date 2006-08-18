<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: xhtml2xhtml.xsl 201776 2005-06-25 18:25:26Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xhtml lenya"
    >

<xsl:param name="rendertype" select="''"/>
<xsl:param name="nodeid"/>
   <!-- this template converts the object tag to img (for compatiblity with older browsers 
    for more, see http://www.xml.com/pub/a/2003/07/02/dive.html -->
   <xsl:template name="object2img">
      <img border="0">
        <xsl:attribute name="src">
          <xsl:choose>
            <xsl:when test="not(starts-with(@data, '/'))">
              <xsl:value-of select="$nodeid"/>/<xsl:value-of select="@data"/>
            </xsl:when>
            <xsl:otherwise>            
              <xsl:value-of select="@data"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="alt">
          <!-- the overwritten title (stored in @name) has precedence over dc:title -->
          <xsl:choose>
            <xsl:when test="@name != ''">
              <xsl:value-of select="@name"/>
            </xsl:when>
            <xsl:when test="@title != ''">
              <xsl:value-of select="@title"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="dc:metadata/dc:title"/>                    
            </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
         <xsl:if test="string(@height)">
          <xsl:attribute name="height">
            <xsl:value-of select="@height"/>
          </xsl:attribute>
        </xsl:if> 
        <xsl:if test="string(@width)">
          <xsl:attribute name="width">
            <xsl:value-of select="@width"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@class">
          <xsl:attribute name="class">
            <xsl:value-of select="@class"/>
          </xsl:attribute>
        </xsl:if>         
      </img>
   </xsl:template>
  
  <xsl:template match="xhtml:object" priority="3">
    <xsl:choose>
      <xsl:when test="@href != ''">
        <a href="{@href}">
          <xsl:call-template name="object2img"/>
        </a>
      </xsl:when>
      <xsl:when test="@type = 'application/x-shockwave-flash'">
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">
            <param name="movie" value="{$nodeid}/{@data}"/>
        </object>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="object2img"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>  

</xsl:stylesheet> 