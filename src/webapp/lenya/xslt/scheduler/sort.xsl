<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.wyona.org/2002/sch">
  
  <xsl:template match="sch:tasks">
    <xsl:copy>
      <xsl:for-each select="sch:task">
	<xsl:sort data-type="number" order="ascending" select="sch:trigger/sch:parameter[@name='year']/@value"/>
	<xsl:sort data-type="number" order="ascending" select="sch:trigger/sch:parameter[@name='month']/@value"/>
	<xsl:sort data-type="number" order="ascending" select="sch:trigger/sch:parameter[@name='day']/@value"/>
	<xsl:sort data-type="number" order="ascending" select="sch:trigger/sch:parameter[@name='hour']/@value"/>
	<xsl:sort data-type="number" order="ascending" select="sch:trigger/sch:parameter[@name='minute']/@value"/>

	<xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:copy> 
  </xsl:template>

  <xsl:template match="* | @*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>                                                                                                                             
  
</xsl:stylesheet>
