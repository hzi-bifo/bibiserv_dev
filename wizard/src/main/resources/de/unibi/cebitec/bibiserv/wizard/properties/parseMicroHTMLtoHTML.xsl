<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:microhtml="bibiserv:de.unibi.techfak.bibiserv.cms.microhtml" version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>

    <!-- This is the template rule for the whole document. -->

    <xsl:template match="/*">
        <xsl:element name="html" namespace="">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="microhtml:p">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <!-- This rule translates br tags in the microhtml-tree back to comments that signal,
    that a br should really be forced at this point.-->
    
    <xsl:template match="microhtml:br">
        <xsl:comment>br</xsl:comment>
    </xsl:template>
    
     <!-- This rule translates hr tags in the microhtml-tree back to comments that signal,
    that a br should really be forced at this point.-->
    
    <xsl:template match="microhtml:hr">
        <xsl:comment>hr</xsl:comment>
    </xsl:template>
    
    <xsl:template match="microhtml:a">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@href, @hreflang, @name"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="microhtml:img">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@src, @alt, @name, @longdesc"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <!-- List-rules -->
    
    <xsl:template match="microhtml:ul">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates mode="listContent"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:ol">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@start"/>
            <xsl:apply-templates mode="listContent"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:li" mode="listContent">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@value"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:em">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:strong">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:code">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:q">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@cite"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:samp">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:cite">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:sub">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="microhtml:sup">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
     <!-- This last rule orders XSLT to copy comments to the result tree-->
    
    <xsl:template match="comment()">
        <xsl:copy/>
    </xsl:template>
    
</xsl:stylesheet>
