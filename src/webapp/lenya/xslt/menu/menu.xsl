<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="menu">
<html>

<head>
<title>Wyona</title>
<script type="text/javascript" src="/wyona-cms/wyona/menu/menu.js">
</script>

<link type="text/css" rel="stylesheet" href="/wyona-cms/wyona/menu/menu.css" />
<script type="text/javascript" src="/wyona-cms/wyona/menu/netscape.js">
</script>
</head>

<body bgcolor="#ffffff" leftmargin="0" marginwidth="0" topmargin="0" marginheight="0">


<noscript>
<div style="color:#FF9900; padding:4px; font:14px arial; background:#000000">
<b>This site requires Javascript to be turned on.</b>
</div>
</noscript>



<table width="600" border="0" cellspacing="0" cellpadding="0">
<!--
<table width="800" border="0" cellspacing="0" cellpadding="0">
-->
<tr>
<td background="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="13" height="4">
<img src="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="13" height="4" /></td>
<td background="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="200" height="4">
<img src="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="200" height="4" /></td>
<td background="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" height="4" width="286">
<img src="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="286" height="4" /></td>
<td background="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" height="4" width="97">
<img src="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="97" height="4" /></td>
<td background="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" height="4" width="4"><img
src="/wyona-cms/wyona/menu/images/frame-bg_oben.gif" width="4" height="4" /></td>
</tr>

<tr>
<td rowspan="2" valign="top" align="right" background="/wyona-cms/wyona/menu/images/grau-bg.gif">
  <img src="/wyona-cms/wyona/menu/images/blau_anfang_oben.gif" />
</td>
<td background="/wyona-cms/wyona/menu/images/grau-bg2.gif">
  <a href="">
    <img border="0" src="/wyona-cms/wyona/menu/images/admin_inactive.gif" />
  </a>
  <img src="/wyona-cms/wyona/menu/images/authoring_active.gif" />
  <a target="_blank" href="{$context_prefix}{live_uri}">
    <img border="0" src="/wyona-cms/wyona/menu/images/live_inactive.gif" />
  </a>
</td>

<td align="right" colspan="2" background="/wyona-cms/wyona/menu/images/grau-bg2.gif">
<font color="#ffffff" size="-2" face="verdana">
  User Id: <b><xsl:value-of select="current_username"/></b> | Server Time: <b><xsl:value-of select="server_time"/></b> &#160;&#160;&#160;
</font>
<!--</td><td valign="top" background="/wyona-cms/wyona/menu/images/grau-bg.gif"><img src="/wyona-cms/wyona/menu/images/wyona_oben.gif">--></td>
<td background="/wyona-cms/wyona/menu/images/grau-bg.gif" height="4" width="2"><img
src="/wyona-cms/wyona/menu/images/grau-bg.gif" width="2" height="4" /></td>
</tr>

<tr>
<td colspan="2" background="/wyona-cms/wyona/menu/images/unten.gif"><img border="0"
src="/wyona-cms/wyona/menu/images/unten.gif" /></td>
<td valign="top" rowspan="2" colspan="2" width="101"
background="/wyona-cms/wyona/menu/images/grau-bg.gif"><img border="0"
src="/wyona-cms/wyona/menu/images/wyona_unten.gif" width="101" /></td>
</tr>

<tr valign="top">
  <td background="/wyona-cms/wyona/menu/images/menu-bg.gif">
    <img border="0" src="/wyona-cms/wyona/menu/images/menu_bg_anfang2.gif" />
  </td>
  <td colspan="2" width="482" valign="top" background="/wyona-cms/wyona/menu/images/menu-bg.gif">
  <div id="navTop">
  <div id="navTopBG">
    <xsl:apply-templates select="menus/menu" mode="nav"/>
  </div>
  </div>
  </td>
</tr>
</table>

<xsl:apply-templates select="menus/menu" mode="menu"/>

<script type="text/javascript">
 initialize(); 
</script>
</body>
</html>
</xsl:template>









<xsl:template match="menu" mode="nav">
  <div style="float:left; width:1px"><img src="/wyona-cms/ethz-mat/scratchpad/menus/images/grau.gif" width="1" height="21" /></div>

<div style="float:left; width:10px">&#160;</div>

<div id="nav{@label}" class="click" style="float:left; width:46px">
<!--
<div id="nav{@label}" class="click" style="float:left" width="20">
-->
<!--
<img src="/wyona-cms/ethz-mat/scratchpad/menus/images/pixel.gif" height="4" /><br />
<img src="/wyona-cms/ethz-mat/scratchpad/menus/images/file.gif" />
-->
<font size="-1" face="verdana"><b>&#160;<xsl:value-of select="@name"/></b></font>
</div>

  <div style="float:left; width:46px">&#160;</div>
</xsl:template>







<xsl:template match="menu" mode="menu">
  <div id="menu{@label}" class="menuOutline">
  <div class="menuBg">
  <xsl:for-each select="block">
          <xsl:for-each select="item">
            <xsl:choose>
              <xsl:when test="@href">
                <a class="mI"><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute><xsl:value-of select="."/></a>
              </xsl:when>
              <xsl:otherwise>
                <span class="mI"><xsl:value-of select="."/></span>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

    <xsl:if test="position() != last()">
      <div style="height: 2px; background-color: #EEEEEE; background-repeat: repeat; background-attachment: scroll; background-image: url('/wyona-cms/ethz-mat/scratchpad/menus/images/dotted.gif'); background-position: 0% 50%">
      <div style="background:#EEEEEE"><img src="/wyona-cms/ethz-mat/scratchpad/menus/images/pixel.gif" height="1" alt="" />
      </div>
      </div>
    </xsl:if>
  </xsl:for-each>
  </div>
  </div>
</xsl:template>





</xsl:stylesheet>

