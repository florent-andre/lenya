/*
$Id: AccreditableManager.java,v 1.4 2003/10/31 15:16:45 andreas Exp $
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

import org.apache.avalon.framework.component.Component;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.IPRangeManager;
import org.apache.lenya.cms.ac.ItemManagerListener;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.UserManager;


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface AccreditableManager extends Component {
    
    String ROLE = AccreditableManager.class.getName();
    
    /**
     * Returns the user manager of this access controller.
     * @return A user manager.
     * @throws AccessControlException when something went wrong.
     */
    UserManager getUserManager() throws AccessControlException;

    /**
     * Returns the group manager of this access controller.
     * @return A group manager.
     * @throws AccessControlException when something went wrong.
     */
    GroupManager getGroupManager() throws AccessControlException;

    /**
     * Returns the role manager of this access controller.
     * @return A role manager.
     * @throws AccessControlException when something went wrong.
     */
    RoleManager getRoleManager() throws AccessControlException;

    /**
     * Returns the IP range manager of this access controller.
     * @return An IP range manager.
     * @throws AccessControlException when something went wrong.
     */
    IPRangeManager getIPRangeManager() throws AccessControlException;

    /**
     * Attaches an item manager listener to this accreditable manager.
     * @param listener An item manager listener.
     */
    void addItemManagerListener(ItemManagerListener listener);
    
    /**
     * Removes an item manager listener from this accreditable manager.
     * @param listener An item manager listener.
     */
    void removeItemManagerListener(ItemManagerListener listener);
    
}
