/*
$Id
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;

import java.io.File;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2003.1.5
 */
public class RevisionControllerAction extends AbstractAction {
    String rcmlDirectory = null;
    String backupDirectory = null;
    RevisionController rc = null;
    String username = null;
    String filename = null;

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        PageEnvelope envelope = null;
        Publication publication = null;

        try {
            publication = PublicationFactory.getPublication(objectModel);
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
        }

        //get Parameters for RC
        String publicationPath = publication.getDirectory().getCanonicalPath();
        String servletContextPath = publication.getServletContext().getCanonicalPath();
        RCEnvironment rcEnvironment = new RCEnvironment(servletContextPath);
        rcmlDirectory = rcEnvironment.getRCMLDirectory();
        rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
        backupDirectory = rcEnvironment.getBackupDirectory();
        backupDirectory = publicationPath + File.separator + backupDirectory;

        // Initialize Revision Controller
        rc = new RevisionController(rcmlDirectory, backupDirectory, publicationPath);
        getLogger().debug("revision controller" + rc);

        // /Initialize Revision Controller
        // Get session
        Session session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute("org.apache.lenya.cms.ac.Identity");
        getLogger().debug(".act(): Identity: " + identity);

        String docId = request.getParameter("documentid");
		String authoringPath = File.separator+"content" + File.separator + Publication.AUTHORING_AREA;
        filename = authoringPath + File.separator+docId.replace('/', File.separatorChar);
        getLogger().debug(".act(): publicationAuthPath + docId : " + authoringPath + " : " + docId);
        username = null;

        if (identity != null) {
            username = identity.getUsername();
        } else {
            getLogger().error(".act(): No identity yet");
        }

        getLogger().debug(".act(): Username: " + username);

        return null;
    }
}
