<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  
  <xsl:param name="uuid"/>
  
  
  <xsl:template match="col:collection">
    <xsl:choose>
      <xsl:when test="@type = 'children'">
        <ci:include src="cocoon://modules/collection/collectionWithChildren/{$uuid}.xml"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*"/>
          <ci:include src="cocoon://modules/collection/metadata-{$uuid}.xml"/>
          <xsl:apply-templates select="col:document"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="col:document">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <ci:include src="cocoon://modules/collection/metadata-{@uuid}.xml"/>
      <ci:include src="lenya-document:{@uuid}"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>