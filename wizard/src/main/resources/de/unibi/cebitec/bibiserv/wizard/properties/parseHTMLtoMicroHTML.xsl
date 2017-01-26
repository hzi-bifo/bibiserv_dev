<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="bibiserv:de.unibi.techfak.bibiserv.cms.microhtml"  
    xmlns:html="http://www.w3.org/1999/xhtml"
    version="1.0">
    
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    
    <!-- 
        
        This xslt-script parses html-code included in any /html/body tag and 
        converts it to valid microhtml according to the BiBiServ microhtml-schema.
        
        Please note that some common html-tags are not allowed and are replaced
        with error-comments.
              
        ChangeLog:
        
        2016/11/30 : Change br tag handling and remove all unnecessary templates modes that leeds
        to some errournus transformation depending on used xslt processor. The script assumes 
        that only valid xhtml (!) is transformed to microhtml. Add xhtml namespace and match only on
        xhtml specific tags. (JK)
        
        Author: Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de 
                Jan Krüger - jkrueger(at)CeBiTec.uni-bielefeld.de
    
    -->

    <!-- This is the template rule for the whole document. -->

    <xsl:template match="/">
        <microhtml>
            <xsl:apply-templates/>
        </microhtml>
    </xsl:template>
    
    

    <xsl:template match="html:body">        
            <xsl:apply-templates/>
    </xsl:template>
    
    <!-- This rule orders XSLT to copy comments to the result tree-->
    
    <xsl:template match="comment()">
        <xsl:copy/>
    </xsl:template>

    <!-- block-rules -->

    <xsl:template match="html:p">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <!-- inline-rules -->

    <xsl:template match="html:a">
        <a>
            <xsl:copy-of select="@href"/>
            <xsl:copy-of select="@hreflang"/>
            <xsl:copy-of select="@name"/>
            <xsl:apply-templates/>
        </a>
    </xsl:template>

    <xsl:template match="html:img" >
        <img>
            <xsl:copy-of select="@href"/>
            <xsl:copy-of select="@hreflang"/>
            <xsl:copy-of select="@name"/>
        </img>
    </xsl:template>

    <xsl:template match="html:br">
        <br/>
    </xsl:template>

    <!-- List-rules -->

    <xsl:template match="html:ul" >
        
        <ul>
            <xsl:apply-templates mode="listentries"/>
        </ul>
        
    </xsl:template>

    <xsl:template match="html:ol" >
        
        <ol>
            <xsl:copy-of select="@start"/>
            <xsl:apply-templates mode="listentries"/>
        </ol>
        
    </xsl:template>

    <xsl:template match="html:li" mode="listentries">
        <li>
            <xsl:copy-of select="@value"/>
            
            <xsl:apply-templates />
            
        </li>
    </xsl:template>
    
    <!-- table -->
    <xsl:template match="html:table">
        <xsl:comment>Table is not supported by microhtml!</xsl:comment>
        <xsl:apply-templates mode="table"/>
    </xsl:template>
    
    <!-- This is an override of a built-in template rule to prevent xslt from parsing plaint text from unsupported tags (table) -->    
    <xsl:template match="text()|@*" mode="table"/>    
    

    <!-- basic-phrase-rules -->

    <xsl:template match="html:em" >
        <em>
            <xsl:apply-templates />
        </em>
    </xsl:template>

    <xsl:template match="html:strong" >
        <strong>
            <xsl:apply-templates />
        </strong>
    </xsl:template>

    <xsl:template match="html:code" >
        <code>
            <xsl:apply-templates />
        </code>
    </xsl:template>

    <xsl:template match="html:q" >
        <q>
            <xsl:copy-of select="@cite"/>
            <xsl:apply-templates />
        </q>
    </xsl:template>

    <xsl:template match="html:samp" >
        <samp>
            <xsl:apply-templates />
        </samp>
    </xsl:template>

    <xsl:template match="html:cite" >
        <cite>
            <xsl:apply-templates />
        </cite>
    </xsl:template>

    <!-- extra-phrase-rules -->

    <xsl:template match="html:sub" >
        <sub>
            <xsl:apply-templates />
        </sub>
    </xsl:template>

    <xsl:template match="html:sup" >
        <sup>
            <xsl:apply-templates />
        </sup>
    </xsl:template>

    <!-- These rules are additions to include some other common html-tags in html -> microhtml conversion -->

    <xsl:template match="html:u" >
        <xsl:comment>ERROR! u-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <em>
            <xsl:apply-templates/>
        </em>
    </xsl:template>

    <xsl:template match="html:b" >
        <xsl:comment>ERROR! b-tags are not allowed in microhtml. Was replaced by strong.</xsl:comment>
        <strong>
            <xsl:apply-templates />
        </strong>
    </xsl:template>

    <xsl:template match="html:i" >
        <xsl:comment>ERROR! i-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <em>
            <xsl:apply-templates/>
        </em>
    </xsl:template>


    
</xsl:stylesheet>
