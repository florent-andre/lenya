<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<html>
<head>
<link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
</head>
<body>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="parent-child">
<h1>New Editor Project</h1>

<xsl:apply-templates select="exception"/>

<xsl:if test="not(exception)">
<p>
<form action="create">
<input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
<input type="hidden" name="childtype" value="leaf"/>
<input type="hidden" name="doctype" value="EditorProject"/>
<table>
  <tr>
    <td>project id:</td><td><input type="text" name="childid"/> (e.g. xopus)</td>
  </tr>
  <tr>
    <td>project name:</td><td><input type="text" name="childname"/> (e.g. Xopus)</td>
  </tr>
  <tr>
    <td>project home url:</td><td><input type="text" name="project_url"/> (e.g. http://www.xopus.org)</td>
  </tr>
</table>
<input type="submit" value="create"/>&#160;&#160;&#160;<a href="{referer}">CANCEL</a>
</form>
</p>
</xsl:if>
</xsl:template>

<xsl:template match="exception">
<font color="red">EXCEPTION</font><br />
Go <a href="{../referer}">back</a> to page.<br />
<p>
Exception handling isn't very good at the moment. 
For further details please take a look at the log-files
of Cocoon. In most cases it's one of the two possible exceptions:
<ol>
  <li>The id is not allowed to have whitespaces</li>
  <li>The id is already in use</li>
</ol>
Exception handling will be improved in the near future.
</p>
</xsl:template>
 
</xsl:stylesheet>  
