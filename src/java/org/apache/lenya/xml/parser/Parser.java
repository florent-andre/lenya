/*
<License>
 * =======================================================================
 * Copyright (c) 2000 wyona. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 * 
 * 4. The name "wyona" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@wyona.org
 * 
 * 5. Products derived from this software may not be called "wyona"
 *    nor may "wyona" appear in their names without prior written
 *    permission of wyona. 
 * 
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 * 
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY 
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF wyona HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * wyona WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
</License>
 */

package org.wyona.xml.parser;

import java.io.*;
import org.w3c.dom.*;

/**
 * @author Michael Wechner, wyona
 * @version 0.4.16
 */
public interface Parser
     {
     public Document getDocument(String filename) throws Exception;
     public Document getDocument(InputStream is) throws Exception;
     public Document getDocument();
     public Element newElementNode(Document document,String name);
     public Text newTextNode(Document document,String data);
     public Comment newCommentNode(Document document,String data);
     }
