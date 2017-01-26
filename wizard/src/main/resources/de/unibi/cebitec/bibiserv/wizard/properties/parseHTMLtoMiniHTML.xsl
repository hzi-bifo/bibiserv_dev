<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="bibiserv:de.unibi.techfak.bibiserv.cms.microhtml"
    xmlns:minihtml="bibiserv:de.unibi.techfak.bibiserv.cms.minihtml" 
    xmlns:html="http://www.w3.org/1999/xhtml"
    version="1.0">
    
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    
    <!-- 
        
        This xslt-script parses html-code included in any root tag and converts
        it to valid minihtml according to the BiBiServ minihtml-schema.
        
        Please note that some common html-tags are not allowed and are replaced
        with error-comments.
        
        ChangeLog:
        
        2016/12/01 : Change br tag handling and remove all unnecessary templates modes that leeds
        to some errournus transformation depending on used xslt processor. The script assumes 
        that only valid xhtml (!) is transformed to microhtml. Add xhtml namespace and match only on
        xhtml specific tags. (JK)
        
        Author: Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de 
                Jan Krüger - jkrueger(at)CeBiTec.uni-bielefeld.de
    -->

    <!-- This is the template rule for the whole document. -->

    <xsl:template match="/">
        <minihtml:minihtml>
            <xsl:apply-templates/>
        </minihtml:minihtml>
    </xsl:template>

   
    
    <!-- This rule orders XSLT to copy comments to the result tree-->
    
    <xsl:template match="comment()" >
        <xsl:copy/>
    </xsl:template>

    <!-- block-rules -->

    <xsl:template match="html:p" >
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <xsl:template match="html:h4">
        <minihtml:h4>
            <xsl:apply-templates />
        </minihtml:h4>
    </xsl:template>

    <xsl:template match="html:h5" >
        <minihtml:h5>
            <xsl:apply-templates />
        </minihtml:h5>
    </xsl:template>

    <xsl:template match="html:h6">      
        <minihtml:h6>
            <xsl:apply-templates/>
        </minihtml:h6>
    </xsl:template>

    <!-- table rules -->

    <xsl:template match="html:table" >
        <minihtml:table>
            <xsl:copy-of select="@summary"/>
            <xsl:apply-templates/>
        </minihtml:table>
    </xsl:template>

    <xsl:template match="html:caption">
        <minihtml:caption>
            <xsl:apply-templates/>
        </minihtml:caption>
    </xsl:template>

    <xsl:template match="html:thead" >
        <minihtml:thead>
            <xsl:apply-templates/>
        </minihtml:thead>
    </xsl:template>

    <xsl:template match="html:tbody">
        <minihtml:tbody>
            <xsl:apply-templates/>
        </minihtml:tbody>
    </xsl:template>

    <xsl:template match="html:tfoot">
        <minihtml:tfoot>
            <xsl:apply-templates/>
        </minihtml:tfoot>
    </xsl:template>

    <xsl:template match="html:tr">
        <minihtml:tr>
            <xsl:apply-templates/>
        </minihtml:tr>
    </xsl:template>
    
    

    <xsl:template match="html:th">
        <minihtml:th>
            <xsl:copy-of select="@abbr"/>
            <xsl:copy-of select="@axis"/>
            <xsl:copy-of select="@headers"/>
            <xsl:copy-of select="@scope"/>
            <xsl:copy-of select="@rowspan"/>
            <xsl:copy-of select="@nowrap"/>
            <xsl:apply-templates/>
        </minihtml:th>
    </xsl:template>

    <xsl:template match="html:td">
        <minihtml:td>
            <xsl:copy-of select="@abbr"/>
            <xsl:copy-of select="@axis"/>
            <xsl:copy-of select="@headers"/>
            <xsl:copy-of select="@scope"/>
            <xsl:copy-of select="@rowspan"/>
            <xsl:copy-of select="@nowrap"/>
            <xsl:apply-templates/>
        </minihtml:td>
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
            <xsl:apply-templates/>
        </ul>
        
    </xsl:template>
    
    <xsl:template match="html:ol" >
        
        <ol>
            <xsl:copy-of select="@start"/>
            <xsl:apply-templates />
        </ol>
        
    </xsl:template>
    
    <xsl:template match="html:li">
        <li>
            <xsl:copy-of select="@value"/>
            
            <xsl:apply-templates/>
           
        </li>
    </xsl:template>
    
    <!-- This is an override of a built-in template rule to prevent xslt from parsing plain text that is not wrapped in a list entry -->
    
    <xsl:template match="text()|@*" mode="listentries">
        <xsl:comment>ERROR! Content between list-entries is not allowed! Was removed.</xsl:comment>
    </xsl:template>
    
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
    
    <xsl:template match="html:u|html:U" >
        <xsl:comment>ERROR! u-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <em>
            <xsl:apply-templates/>
        </em>
    </xsl:template>
    
    <xsl:template match="html:b|html:B" >
        <xsl:comment>ERROR! b-tags are not allowed in microhtml. Was replaced by strong.</xsl:comment>
        <strong>
            <xsl:apply-templates />
        </strong>
    </xsl:template>
    
    <xsl:template match="html:i|html:I" >
        <xsl:comment>ERROR! i-tags are not allowed in microhtml. Was replaced by em.</xsl:comment>
        <em>
            <xsl:apply-templates/>
        </em>
    </xsl:template>
    


</xsl:stylesheet>
