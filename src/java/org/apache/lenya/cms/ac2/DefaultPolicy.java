/*
$Id: DefaultPolicy.java,v 1.3 2003/07/11 13:24:37 andreas Exp $
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
package org.apache.lenya.cms.ac2;

import org.apache.lenya.cms.ac.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * A DefaultPolicy is the own policy of a certain URL (not merged).
 *
 * @author andreas
 */
public class DefaultPolicy implements Policy {
    private Set credentials = new HashSet();

    /**
     * Adds a credential to this policy.
     * @param credential A credential.
     */
    public void addCredential(Credential credential) {
        assert credential != null;
        assert !credentials.contains(credential);
        credentials.add(credential);
    }

    /**
     * Returns the credentials of this policy.
     * @return An array of credentials.
     */
    public Credential[] getCredentials() {
        return (Credential[]) credentials.toArray(new Credential[credentials.size()]);
    }

    /**
     * @see org.apache.lenya.cms.ac2.Policy#getRoles(org.apache.lenya.cms.ac2.Identity)
     */
    public Role[] getRoles(Identity identity) {
        Accreditable[] accreditables = identity.getAccreditables();
        Credential[] credentials = getCredentials();

        Set roles = new HashSet();

        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            for (int accrIndex = 0; accrIndex < accreditables.length; accrIndex++) {
                Credential credential = credentials[credIndex];
                Accreditable accreditable = accreditables[accrIndex];

                if (credential.getAccreditable().equals(accreditable)) {
                    roles.addAll(Arrays.asList(credential.getRoles()));
                }
            }
        }

        return (Role[]) roles.toArray(new Role[roles.size()]);
    }
}
