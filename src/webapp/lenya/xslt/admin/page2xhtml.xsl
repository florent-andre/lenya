<?xml version="1.0"?>

<!-- $Id: page2xhtml.xsl,v 1.5 2003/12/05 20:55:55 gregor Exp $ -->

<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	  xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:xhtml="http://www.w3.org/1999/xhtml"
      xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
	  >
	  
	  
<xsl:param name="contextprefix"/>

<xsl:template match="/cmsbody">
    <html>
      <head>
        <title><xsl:value-of select="page:page/page:title"/></title>
        <link rel="stylesheet" type="text/css"
          href="{$contextprefix}/lenya/css/admin.css" title="admin css"/>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
</xsl:template>


<xsl:template match="page:page">
  <table class="lenya-body" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td class="lenya-sidebar">
        <div class="lenya-sidebar-heading">Administration</div>
        <xsl:copy-of select="/cmsbody/xhtml:div[@id = 'menu']"/>
      </td>
      <td class="lenya-content">
        <div style="margin-bottom: 10px">
          <h1><xsl:apply-templates select="page:title/node()"/></h1>
        </div>
        <xsl:apply-templates select="page:body/node()"/>
      </td>
    </tr>
  </table>
</xsl:template>


<!-- do not copy menu -->
<xsl:template match="xhtml:div[@id = 'menu']"/>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
