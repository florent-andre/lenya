<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page2html.xsl
    Created on : November 20, 2002, 4:17 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://www.lenya.org/2003/cms-page"
    >

<xsl:param name="context-prefix"/>

<xsl:template match="/page:page">
  <html>
    <head>
      <title><xsl:value-of select="page:title"/></title>
      <link rel="stylesheet" type="text/css"
        href="{$context-prefix}/lenya/css/default.css" title="default css"/>
    </head>
    <body>
    
      <table width="100%" border="0" cellpadding="10" cellspacing="0">
        <tr>
          <td class="lenya-header">
            <h1><xsl:value-of select="page:title"/></h1>
          </td>
          <td class="lenya-project-logo">
            <img src="{$context-prefix}/lenya/images/project-logo-small.png"/>
          </td>
        </tr>
      </table>
      <div class="lenya-page-subtitle">
        Open Source Content Management System
      </div>
      <table class="lenya-body" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <xsl:variable name="main-div" select="page:body/xhtml:div[@class != 'lenya-sidebar']"/>
          <xsl:choose>
            <xsl:when test="$main-div">
              <td class="{$main-div/@class}">
                <xsl:copy-of select="$main-div/node()"/>
              </td>
            </xsl:when>
            <xsl:otherwise>
              <td class="lenya-content">
                <xsl:copy-of select="page:body/node()[local-name() != 'div'][not(@class = 'lenya-sidebar')]"/>
              </td>
            </xsl:otherwise>
          </xsl:choose>
          <td class="lenya-sidebar">
            <xsl:copy-of select="//xhtml:div[@class = 'lenya-sidebar']/node()"/>
          </td>
        </tr>
      </table>
<!--    
      <div>
        
      </div>
      <div class="lenya-header">
        
      </div>
      <div class="lenya-body">
        <xsl:copy-of select="page:body/*"/>
      </div>
-->      
    </body>
  </html>
</xsl:template>


</xsl:stylesheet> 
