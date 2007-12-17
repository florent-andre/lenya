/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.UserReference;

/**
 * The anonymous authenticator authenticates to an anonymous user with no password (you just have to
 * add a user named 'anonymous' with an arbitrary password and the permissions you'd like via the
 * admin screen). This is useful in conjunction with client certificates.
 * @version $Id: UserAuthenticator.java 43241 2004-08-16 16:36:57Z andreas $
 */
public class AnonymousAuthenticator extends AbstractLogEnabled implements Authenticator {

    /**
     * @see org.apache.lenya.ac.Authenticator#authenticate(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.cocoon.environment.Request, ErrorHandler)
     */
    public boolean authenticate(AccreditableManager accreditableManager, Request request,
            ErrorHandler handler) throws AccessControlException {

        String username = "anonymous";

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authenticating username [" + username + "]");
        }

        Identity identity = (Identity) request.getSession(false).getAttribute(
                Identity.class.getName());

        UserManager userManager = accreditableManager.getUserManager();

        boolean authenticated = false;
        if (userManager.contains(username)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("User [" + username + "] authenticated.");
            }

            UserReference oldUser = identity.getUserReference();
            if (oldUser != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Removing user [" + oldUser + "] from identity.");
                }
                identity.removeIdentifiable(oldUser);
            }
            identity.addIdentifiable(new UserReference(username, userManager.getId()));
            authenticated = true;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("No such user: [" + username + "]");
                getLogger().debug("User [" + username + "] not authenticated.");
            }
        }
        return authenticated;
    }

    public String getLoginUri(Request request) {
        return request.getRequestURI() + "?lenya.usecase=login&lenya.step=showscreen";
    }

    public String getTargetUri(Request request) {
        return request.getRequestURI();
    }
}
