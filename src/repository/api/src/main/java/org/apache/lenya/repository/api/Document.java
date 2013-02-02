/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.lenya.repository.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * A document.
 * 
 * NOTE : the actual version is a copy and remove of Document api version 2.1.x
 * comments are still here for thinkings about geting or removing some signatures
 * TODO : move this interface to his own document module api definition
 * 
 * TODO !!! rename this document interface to information ! or data
 * and a document is an aggregation of
 * * data(s)
 * * metadata(s)
 * * business rules (data transformation)
 * * make this class generic to allow data of any kind
 */
public interface Document {
	//do we still extends MetaDataOwner ?
    
    /**
     * The document namespace URI.
     */
	//FLO : do we keep it ? if yes in static public
    String NAMESPACE = "http://apache.org/cocoon/lenya/document/1.0";
    
    /**
     * The default namespace prefix.
     */
  //FLO : do we keep it ? if yes in static public
    String DEFAULT_PREFIX = "lenya";
    
    /**
     * The transactionable type for document objects.
     */
  //FLO : do we keep it ? Used fo ?
    String TRANSACTIONABLE_TYPE = "document";
    
    /**
     * <code>DOCUMENT_META_SUFFIX</code> The suffix for document meta Uris
     */
  //FLO : do we keep it ? Or put it in metadata object
    final String DOCUMENT_META_SUFFIX = ".meta";
    
    /**
     * Returns the date at which point the requested document is considered expired
     * @return a string in RFC 1123 date format
     * @throws DocumentException if an error occurs.
     */
    //FLO : may be more metadata information no ?
    Date getExpires() throws DocumentException;

    /**
     * Returns the document name of this document.
     * @return the document-name of this document.
     */
    String getName();
    
    /**
     * Returns the language of this document.
     * Each document has one language associated to it. 
     * @return A string denoting the language.
     */
    //FLO : more metadata related no ?
    String getLanguage();

    /**
     * Returns all the languages this document is available in.
     * A document has one associated language (@see Document#getLanguage)
     * but there are possibly a number of other languages for which a 
     * document with the same document-uuid is also available in. 
     * 
     * @return An array of strings denoting the languages.
     * 
     * @throws DocumentException if an error occurs
     */
    //FLO : more metadata related no ?
    String[] getLanguages() throws DocumentException;

    /**
     * Returns the date of the last modification of this document.
     * @return A date denoting the date of the last modification.
     * @throws DocumentException if an error occurs.
     */
    //FLO : more metadata related no ?
    long getLastModified() throws DocumentException;
    
    /**
     * Returns the UUID.
     * @return A string.
     */
    String getUUID();
    
    /**
     * Check if a document exists with the given document-uuid and the given area
     * independently of the given language.
     * 
     * @return true if a document with the given document-uuid and area exists, false otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    //more metadata level
    boolean existsInAnyLanguage() throws DocumentException;
        
    /**
     * @return The output stream to write the document content to.
     */
    OutputStream getOutputStream();
    
    /**
     * @return The resource type of this document (formerly known as doctype)
     * @throws DocumentException if an error occurs.
     */
    //FLO : put it at document level
    //ResourceType getResourceType() throws DocumentException;
    
    /**
     * @param resourceType The resource type of this document.
     */
    //void setResourceType(ResourceType resourceType);
    
    /**
     * Sets the mime type of this document.
     * @param mimeType The mime type.
     * @throws DocumentException if an error occurs.
     */
    void setMimeType(String mimeType) throws DocumentException;
    
    /**
     * @return The mime type of this document.
     * @throws DocumentException if an error occurs.
     */
    String getMimeType() throws DocumentException;
    
    /**
     * @return The content length of the document.
     * @throws DocumentException if an error occurs.
     */
    long getContentLength() throws DocumentException;
    
    /**
     * Checks if a certain translation (language version) of this document exists.
     * @param language The language.
     * @return A boolean value.
     */
    //FLO : metadatalevel
    //boolean existsTranslation(String language);
    
    /**
     * Returns a certain translation (language version) of this document.
     * @param language The language.
     * @return A document.
     * @throws DocumentException if the language version doesn't exist.
     */
    //Document getTranslation(String language) throws DocumentException;

    /**
     * Checks if a version of this document exist.
     * @param area The area.
     * @param language The language.
     * @return A boolean value.
     */
    boolean existsVersion(String area, String language);
    
    /**
     * Returns a specific version of the document.
     * @param area The area.
     * @param language The language.
     * @return A document.
     * @throws DocumentException if the area version doesn't exist.
     */
    Document getVersion(String area, String language) throws DocumentException;
    
    /**
     * @return The input stream to obtain the document's content.
     */
    InputStream getInputStream();
    
    /**
     * @return The revision number of this document.
     */
    int getRevisionNumber();
}
