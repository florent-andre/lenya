<?xml version="1.0"?>

<!--
 $Id: content.xsl,v 1.3 2003/09/22 17:15:27 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Delete Trash</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Delete the Trash</div>
      <div class="lenya-box-body">
        <form method="get" action="index.html">
          <input type="hidden" name="lenya.usecase" value="deleteTrash"/>
          <input type="hidden" name="lenya.step" value="deleteTrash"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <p>
 	      Do you really want to delete the trash ?
          </p>
          <input type="submit" value="Delete trash"/>
          &#160;
          <input type="button" onClick="location.href='{$request-uri}/../';" value="Cancel"/>
        </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
  
