/*
$Id: DocumentBuilder.java,v 1.9 2003/12/04 14:17:21 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;


/**
 * A document builder builds a document from a URL.
 *
 * @author andreas
 *
 */
public interface DocumentBuilder {

    /**
     * Builds a document.
     * 
     * @param publication The publication the document belongs to.
     * @param url The URL of the form /{publication-id}/{area}/{document-id}{language-suffix}.{extension}.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    Document buildDocument(Publication publication, String url)
        throws DocumentBuildException;
    
    /**
     * Builds a collection document.
     * 
     * @param publication The publication the document belongs to.
     * @param url The URL of the form /{publication-id}/{area}/{document-id}{language-suffix}.{extension}.
     * @return A collection.
     * @throws DocumentBuildException when something went wrong.
     */
    Collection buildCollection(Publication publication, String url)
        throws DocumentBuildException;
    
    /**
     * Checks if an URL corresponds to a CMS document.
     * 
     * @param publication The publication the document belongs to.
     * @param url The URL of the form /{publication-id}/...
     * @return A boolean value.
     * @throws DocumentBuildException when something went wrong.
     */    
    boolean isDocument(Publication publication, String url)
        throws DocumentBuildException;
        
    /**
     * Builds an URL corresponding to a cms document from the publication, 
     * the area, the document id and the language
     * 
     * @param publication The publication the document belongs to.
     * @param area The area the document belongs to.
     * @param documentid The document id of the document.
     * @param language The language of the document.
     * @return a String The builded url
     */
    String buildCanonicalUrl(
        Publication publication,
        String area,
        String documentid,
        String language);

    /**
     * Builds an URL corresponding to a cms document from the publication, 
     * the area and the document id
     * 
     * @param publication The publication the document belongs to.
     * @param area The area the document belongs to.
     * @param documentid The document id of the document.
     * @return a String The builded url
     */
    String buildCanonicalUrl(
        Publication publication,
        String area,
        String documentid);
    
    /**
     * Builds a clone of a document for another language. 
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     */
    Document buildLanguageVersion(Document document, String language);
}
