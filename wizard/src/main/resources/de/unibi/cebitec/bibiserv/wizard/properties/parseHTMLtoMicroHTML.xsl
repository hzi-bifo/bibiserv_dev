<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:microhtml="bibiserv:de.unibi.techfak.bibiserv.cms.microhtml" version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    
    <!-- 
        
        This xslt-script parses html-code included in any root tag and converts
        it to valid microhtml according to the BiBiServ microhtml-schema.
        
        Please note that some common html-tags are not allowed and are replaced
        with error-comments.
        
        Please also note the specific way to handle br-tags in this script:
        
        Although linebreaks are allowed in microhtml, usually they are not needed.
        All br-tags are therefore replaced by comments advising to include p-tags
        here (insertparagraph). These comments have to be replaced by
        
        </microhtml:p> <microhtml:p>
        
        This is no well-formed xml-conversion and therefore not possible in xslt.
        
        If you wish to use this scripts br-handling and replace all brs by
        valid paragraphs, use post processing code that performs the following
        replacements:
        
        replace the insertparagraph-comment by: </microhtml:p> <microhtml:p>
        replace the startparagraph-comment by: <microhtml:p>
        replace the endparagraph-comment by: </microhtml:p>
        
        The result will be a valid microhtml-document.
        
        Author: Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de 
    
    -->

    <!-- This is the template rule for the whole document. -->

    <xsl:template match="/*">
        <microhtml:microhtml>
            <xsl:comment>startparagraph</xsl:comment>
            <xsl:apply-templates mode="flow"/>
            <xsl:comment>endparagraph</xsl:comment>
        </microhtml:microhtml>
    </xsl:template>
    
    <!-- This rule orders XSLT to copy comments to the result tree-->
    
    <xsl:template match="comment()" mode="flow block inline">
        <xsl:copy/>
    </xsl:template>

    <!-- block-rules -->

    <xsl:template match="p" mode="block flow">
        <xsl:comment>endparagraph</xsl:comment>
        <microhtml:p>
            <xsl:apply-templates mode="inline"/>
        </microhtml:p>
        <xsl:comment>startparagraph</xsl:comment>
    </xsl:template>

    <!-- inline-rules -->

    <xsl:template match="a" mode="flow inline">
        <microhtml:a>
            <xsl:copy-of select="@href, @hreflang, @name"/>
            <xsl:apply-templates mode="phrase"/>
        </microhtml:a>
    </xsl:template>

    <xsl:template match="img" mode="flow inline">
        <microhtml:img>
            <xsl:copy-of select="@src, @alt, @name, @longdesc"/>
        </microhtml:img>
    </xsl:template>

    <!-- List-rules -->

    <xsl:template match="ul" mode="flow block">
        <xsl:comment>endparagraph</xsl:comment>
        <microhtml:ul>
            <xsl:apply-templates mode="listentries"/>
        </microhtml:ul>
        <xsl:comment>startparagraph</xsl:comment>
    </xsl:template>

    <xsl:template match="ol" mode="flow block">
        <xsl:comment>endparagraph</xsl:comment>
        <microhtml:ol>
            <xsl:copy-of select="@start"/>
            <xsl:apply-templates mode="listentries"/>
        </microhtml:ol>
        <xsl:comment>startparagraph</xsl:comment>
    </xsl:template>

    <xsl:template match="li" mode="listentries">
        <microhtml:li>
            <xsl:copy-of select="@value"/>
            <xsl:comment>startparagraph</xsl:comment>
            <xsl:apply-templates mode="flow"/>
            <xsl:comment>endparagraph</xsl:comment>
        </microhtml:li>
    </xsl:template>

    <!-- This is an override of a built-in template rule to prevent xslt from parsing plain text that is not wrapped in a list entry -->

    <xsl:template match="text()|@*" mode="listentries">
        <xsl:comment>ERROR! Content between list-entries is not allowed! Was removed.</xsl:comment>
    </xsl:template>

    <!-- basic-phrase-rules -->

    <xsl:template match="em" mode="inline phrase phrase.basic">
        <microhtml:em>
            <xsl:apply-templates mode="#current"/>
        </microhtml:em>
    </xsl:template>

    <xsl:template match="strong" mode="inline phrase phrase.basic">
        <microhtml:strong>
            <xsl:apply-templates mode="#current"/>
        </microhtml:strong>
    </xsl:template>

    <xsl:template match="code" mode="inline phrase phrase.basic">
        <microhtml:code>
            <xsl:apply-templates mode="#current"/>
        </microhtml:code>
    </xsl:template>

    <xsl:template match="q" mode="inline phrase phrase.basic">
        <microhtml:q>
            <xsl:copy-of select="@cite"/>
            <xsl:apply-templates mode="#current"/>
        </microhtml:q>
    </xsl:template>

    <xsl:template match="samp" mode="inline phrase phrase.basic">
        <microhtml:samp>
            <xsl:apply-templates mode="#current"/>
        </microhtml:samp>
    </xsl:template>

    <xsl:template match="cite" mode="inline phrase phrase.basic">
        <microhtml:cite>
            <xsl:apply-templates mode="#current"/>
        </microhtml:cite>
    </xsl:template>

    <!-- extra-phrase-rules -->

    <xsl:template match="sub" mode="inline phrase phrase.extra">
        <microhtml:sub>
            <xsl:apply-templates mode="#current"/>
        </microhtml:sub>
    </xsl:template>

    <xsl:template match="sup" mode="inline phrase phrase.extra">
        <microhtml:sup>
            <xsl:apply-templates mode="#current"/>
        </microhtml:sup>
    </xsl:template>

    <!-- These rules are additions to include some other common html-tags in html -> microhtml conversion -->

    <xsl:template match="u|U" mode="inline phrase phrase.basic">
        <xsl:comment>ERROR! u-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <microhtml:em>
            <xsl:apply-templates mode="#current"/>
        </microhtml:em>
    </xsl:template>

    <xsl:template match="b|B" mode="inline phrase phrase.basic">
        <xsl:comment>ERROR! b-tags are not allowed in microhtml. Was replaced by strong.</xsl:comment>
        <microhtml:strong>
            <xsl:apply-templates mode="#current"/>
        </microhtml:strong>
    </xsl:template>

    <xsl:template match="i|I" mode="inline phrase phrase.basic">
        <xsl:comment>ERROR! i-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <microhtml:em>
            <xsl:apply-templates mode="#current"/>
        </microhtml:em>
    </xsl:template>

    <xsl:template match="br" mode="flow block">
        <xsl:comment>ERROR! br/-tags are not allowed in microhtml. Was replaced by paragraph.</xsl:comment>
        <xsl:comment>insertparagraph</xsl:comment>
    </xsl:template>

    <!-- Please note! The following set of rules are just the same as before (phrase), but they are used only in flow mode -->

    <!-- basic-phrase-rules -->

    <xsl:template match="em" mode="flow">
        <microhtml:em>
            <xsl:apply-templates mode="inline"/>
        </microhtml:em>
    </xsl:template>

    <xsl:template match="strong" mode="flow">
        <microhtml:strong>
            <xsl:apply-templates mode="inline"/>
        </microhtml:strong>
    </xsl:template>

    <xsl:template match="code" mode="flow">
        <microhtml:code>
            <xsl:apply-templates mode="inline"/>
        </microhtml:code>
    </xsl:template>

    <xsl:template match="q" mode="flow">
        <microhtml:q>
            <xsl:copy-of select="@cite"/>
            <xsl:apply-templates mode="inline"/>
        </microhtml:q>
    </xsl:template>

    <xsl:template match="samp" mode="flow">
        <microhtml:samp>
            <xsl:apply-templates mode="inline"/>
        </microhtml:samp>
    </xsl:template>

    <xsl:template match="cite" mode="flow">
        <microhtml:cite>
            <xsl:apply-templates mode="inline"/>
        </microhtml:cite>
    </xsl:template>

    <!-- extra-phrase-rules -->

    <xsl:template match="sub" mode="flow">
        <microhtml:sub>
            <xsl:apply-templates mode="inline"/>
        </microhtml:sub>
    </xsl:template>

    <xsl:template match="sup" mode="flow">
        <microhtml:sup>
            <xsl:apply-templates mode="inline"/>
        </microhtml:sup>
    </xsl:template>

    <!-- These rules are additions to include some other common html-tags in html -> microhtml conversion -->

    <xsl:template match="u|U" mode="flow">
        <xsl:comment>ERROR! u-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <microhtml:em>
            <xsl:apply-templates mode="inline"/>
        </microhtml:em>
    </xsl:template>

    <xsl:template match="b|B" mode="flow">
        <xsl:comment>ERROR! b-tags are not allowed in microhtml. Was replaced by strong.</xsl:comment>
        <microhtml:strong>
            <xsl:apply-templates mode="inline"/>
        </microhtml:strong>
    </xsl:template>

    <xsl:template match="i|I" mode="flow">
        <xsl:comment>ERROR! i-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <microhtml:strong>
            <xsl:apply-templates mode="inline"/>
        </microhtml:strong>
    </xsl:template>

</xsl:stylesheet>
