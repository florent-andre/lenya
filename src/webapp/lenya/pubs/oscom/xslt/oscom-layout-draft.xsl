<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/" xmlns:oscom="http://www.oscom.org/2002/oscom">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="oscombarcolor">#ffa500</xsl:variable>
<xsl:variable name="navbarcolor">#ffc36b</xsl:variable>

<xsl:template match="oscom">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org" />
<title>OSCOM - Proposals</title>
<meta http-equiv="Content-Type"
content="text/html; charset=iso-latin-1" />
<meta http-equiv="Content-Language" content="EN" />
<meta name="description" content="@DESCRIPTION@" />
<style type="text/css">
     <xsl:comment>
            BODY {
        font-family: Verdana, Arial, Helvetica, sans-serif;
      }
      H1 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #333333;
        background-color: transparent;
        font-size: 14px;
      }
      H2 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 13px;
      }
      H3 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 12px;
      }
      P, LI, PRE, BLOCKQUOTE,DT,DD {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px;
      }
      TD.topnavi {
        padding: 0px;
        padding-left  : 15px;
        font-size: 11px;
      }
      TD.rightbar {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        border-left: 1px #a0a0a0 dotted;
        padding: 7px;
        padding-bottom: 10px;
        font-size: 11px;
      }
      TD.sitelogoarea {
        padding-left  : 10px;
      }
      TD.content {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px;
        padding: 7px;
        padding-bottom: 10px;
        padding-left  : 15px;
      }
      TD.footer {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        border-top: 1px #FFC36B solid;
        padding: 7px;
        padding-bottom: 10px;
        padding-left  : 15px;
        font-size: 10px;
        color: #a0a0a0;
        background-color: transparent;
      }
      .rightbox {
        border-top: 1px #a0a0a0 dotted;
      }
      .navigationwhite {
        color: #FFFFFF;
        background-color: transparent;
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 12px; 
        font-style: normal; 
        font-weight: normal;
        text-decoration: none;
      }
      .navigationwhite:hover {
        text-decoration: underline;
      }
      .naviselected {
         font-weight: bold;
      }
      .nnbr {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px; 
        background-color: transparent;
        text-decoration: none;
      }
      .nnbr:hover {
        background-color: transparent;
        text-decoration: underline;
      }
      .nnbs {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px; 
        background-color: transparent;
        text-decoration: none;
        font-weight: bolder;
      }
      .nnbe {
        margin-bottom: 7px;
      }
      A {
        color: #663300;
        background-color: transparent;
      }
      .breadcrumb {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #a0a0a0;
        background-color: transparent;
        text-decoration: none;
        font-size: 9px;
      }
      .newsareaheader {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #333333;
        background-color: transparent;
        font-weight: bold;
      }
      .news {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 10px;
        text-decoration: none;
      }
      .news:hover {
        text-decoration: underline;
      }
    </xsl:comment>
    
</style>
</head>
<body bgcolor="#ffffff" text="#000000" leftmargin="0" topmargin="0"
marginwidth="0" marginheight="0">
<!--top row: main navigation-->
<table cellspacing="0" cellpadding="0" border="0" width="770">
<tbody>
<tr bgcolor="#cc0066">
<td width="100%" height="54">
<table cellspacing="0" cellpadding="0" border="0" width="100%" bgcolor="{$oscombarcolor}">
<tbody>
<tr>
<td valign="bottom" class="sitelogoarea"><a
href="http://alpha.oscom.org/"><img
src="oscom-proposals_files/oscom-logo.png" width="329" height="54"
alt="OSCOM - Open Source Content Management" border="0" /></a></td>
<td valign="top" width="100%"><img
src="oscom-proposals_files/spacer.gif" width="250" height="42"
border="0" /></td>
<td valign="middle" width="235">
<!-- We can put topical banners here --><img
src="oscom-proposals_files/spacer.gif" width="1" height="1"
border="0" /></td>
</tr>
</tbody>
</table>
</td>
</tr>

<tr>
<td valign="top" width="100%" bgcolor="{$navbarcolor}" class="topnavi">
<table border="0" cellpadding="0" cellspacing="0">
<tbody>
<tr>
<td height="21" class="topnavigation" align="left"><a
href="http://alpha.oscom.org/"
class="navigationwhite">Home</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="http://alpha.oscom.org/Conferences/"
class="navigationwhite"><span
class="naviselected">Conferences</span></a></td>
<!--<td height="21" width="19" align="left"><img src='/attachment/72c7a5e5939d06b8545b6a41cc703144/c248557c7393e5c5043c5257bf5e133f/spacer.gif' width="19" height="14"></td>
<td height="21" class="topnavigation" align="left"><a href="/matrix/" class="navigationwhite">CMS Matrix</a></td>-->
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="http://alpha.oscom.org/Projects/"
class="navigationwhite">Projects</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="http://alpha.oscom.org/Mailing%20lists/"
class="navigationwhite">Mailing lists</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="http://blog.oscom.org/" class="navigationwhite">Blog</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="http://alpha.oscom.org/Board/"
class="navigationwhite">Board</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
<td height="21" class="topnavigation" align="left"><a
href="/lenya/oscom/matrix/index.html" class="navigationwhite">CMS
Matrix</a></td>
<td height="21" width="19" align="left"><img
src="oscom-proposals_files/spacer.gif" width="19"
height="14" /></td>
</tr>
</tbody>
</table>
</td>
</tr>

<tr>
<td valign="top" width="100%" bgcolor="#ffa500"><img
src="oscom-proposals_files/spacer.gif" width="760" height="1"
border="0" /></td>
</tr>
</tbody>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="770">
<tbody>
<tr>


<td valign="top" align="left" width="150">
&#160;<br />
<!-- @NAVIGATION@ -->
<xsl:apply-templates select="oscom_navigation"/>
<p>
  <br /><br />
</p>
<p align="center">
  <a href="http://alpha.oscom.org/Conferences/Cambridge/">@ADD_BANNER@</a>
</p>
<p>&#160;</p>
</td>



<td valign="top" class="rightbar">
<img src="oscom-proposals_files/spacer.gif" width="1" height="16" border="0" />
</td>



<!--middle column: main content-->
<td valign="top" width="619">
<table width="540" cellpadding="1" cellspacing="0" border="0">
<tbody>
<tr>
<td valign="top" align="left" colspan="2" class="content">
<!-- @CONTENT@ -->
<xsl:call-template name="body"/>
</td>
</tr>
</tbody>
</table>
</td>



</tr>
</tbody>
</table>

<!--bottom row: footer-->
<table width="770" border="0" cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td class="footer" valign="top">&#169;2002-2003 OSCOM.
<!-- @UPDATING_DATE@ --><br />
 <!-- <a href="http://alpha.oscom.org/" class="breadcrumb">@BREADCRUMB_PATH@</a> --> <!--
<a href="http://alpha.oscom.org/" class="breadcrumb">Home</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/" class="breadcrumb">Conferences</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/Cambridge/" class="breadcrumb">Cambridge</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/Cambridge/Proposals/" class="breadcrumb">Proposals</a>
-->
 </td>
<td align="right" valign="top" class="footer">
<!-- Admin interface link --><a href="http://oscom.lenya.org/lenya/oscom/matrix/index.html" class="breadcrumb">Lenya</a><!-- /Admin interface link --></td>
</tr>
</tbody>
</table>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
