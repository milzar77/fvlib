<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output omit-xml-declaration="yes" method="xhtml" encoding="ISO-8859-1" indent="yes" />
<xsl:template match="/">
<html>
<xsl:for-each select="./root/*">
	[<xsl:value-of select="." />]
<xsl:text disable-output-escaping="yes"><![CDATA[
]]></xsl:text>
</xsl:for-each>
</html>
</xsl:template>
</xsl:stylesheet>