/*
 * $Id: DefaultAccessController.java,v 1.2 2004/02/05 08:50:57 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.PolicyManager;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultAccessController
    extends AbstractLogEnabled
    implements AccessController, Configurable, Serviceable, Disposable, ItemManagerListener {

    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String ACCREDITABLE_MANAGER_ELEMENT = "accreditable-manager";
    protected static final String POLICY_MANAGER_ELEMENT = "policy-manager";

    private ServiceSelector accreditableManagerSelector;
    private AccreditableManager accreditableManager;

    private ServiceSelector authorizerSelector;
    private Map authorizers = new HashMap();
    private List authorizerKeys = new ArrayList();

    private ServiceSelector policyManagerSelector;
    private PolicyManager policyManager;

    private Authenticator authenticator;

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authenticate(org.apache.cocoon.environment.Request)
     */
    public boolean authenticate(Request request) throws AccessControlException {

        assert request != null;
        boolean authenticated = getAuthenticator().authenticate(getAccreditableManager(), request);

        return authenticated;
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {

        assert request != null;

        boolean authorized = false;

        getLogger().debug("=========================================================");
        getLogger().debug("Beginning authorization.");

        if (hasAuthorizers()) {
            Authorizer[] authorizers = getAuthorizers();
            int i = 0;
            authorized = true;

            while ((i < authorizers.length) && authorized) {

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("---------------------------------------------------------");
                    getLogger().debug("Invoking authorizer [" + authorizers[i] + "]");
                }

                if (authorizers[i] instanceof PolicyAuthorizer) {
                    PolicyAuthorizer authorizer = (PolicyAuthorizer) authorizers[i];
                    authorizer.setAccreditableManager(accreditableManager);
                    authorizer.setPolicyManager(policyManager);
                }

                authorized = authorized && authorizers[i].authorize(request);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                        "Authorizer [" + authorizers[i] + "] returned [" + authorized + "]");
                }

                i++;
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("=========================================================");
            getLogger().debug("Authorization complete, result: [" + authorized + "]");
            getLogger().debug("=========================================================");
        }

        return authorized;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {

        try {
            setupAccreditableManager(conf);
            setupAuthorizers(conf);
            setupPolicyManager(conf);
            setupAuthenticator();
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Configuration failed: ", e);
        }
    }

    /**
     * Configures or parameterizes a component, depending on the implementation
     * as Configurable or Parameterizable.
     * @param component The component.
     * @param configuration The configuration to use.
     * @throws ConfigurationException when an error occurs during configuration.
     * @throws ParameterException when an error occurs during parameterization.
     */
    public static void configureOrParameterize(Component component, Configuration configuration)
        throws ConfigurationException, ParameterException {
        if (component instanceof Configurable) {
            ((Configurable) component).configure(configuration);
        }
        if (component instanceof Parameterizable) {
            Parameters parameters = Parameters.fromConfiguration(configuration);
            ((Parameterizable) component).parameterize(parameters);
        }
    }

    /**
     * Creates the accreditable manager.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupAccreditableManager(Configuration configuration)
        throws ConfigurationException, ServiceException, ParameterException {

        Configuration accreditableManagerConfiguration =
            configuration.getChild(ACCREDITABLE_MANAGER_ELEMENT, false);
        if (accreditableManagerConfiguration != null) {
            String accreditableManagerType =
                accreditableManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("AccreditableManager type: [" + accreditableManagerType + "]");
            }

            accreditableManagerSelector =
                (ServiceSelector) manager.lookup(AccreditableManager.ROLE + "Selector");
            accreditableManager =
                (AccreditableManager) accreditableManagerSelector.select(accreditableManagerType);
            accreditableManager.addItemManagerListener(this);
            configureOrParameterize(accreditableManager, accreditableManagerConfiguration);
        }
    }

    /**
     * Creates the authorizers.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupAuthorizers(Configuration configuration)
        throws ServiceException, ConfigurationException, ParameterException {
        Configuration[] authorizerConfigurations = configuration.getChildren(AUTHORIZER_ELEMENT);
        if (authorizerConfigurations.length > 0) {
            authorizerSelector = (ServiceSelector) manager.lookup(Authorizer.ROLE + "Selector");

            for (int i = 0; i < authorizerConfigurations.length; i++) {
                String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Adding authorizer [" + type + "]");
                }

                Authorizer authorizer = (Authorizer) authorizerSelector.select(type);
                authorizerKeys.add(type);
                authorizers.put(type, authorizer);
                configureOrParameterize(authorizer, authorizerConfigurations[i]);
            }
        }
    }

    /**
     * Creates the policy manager.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupPolicyManager(Configuration configuration)
        throws ServiceException, ConfigurationException, ParameterException {
        Configuration policyManagerConfiguration =
            configuration.getChild(POLICY_MANAGER_ELEMENT, false);
        if (policyManagerConfiguration != null) {
            String policyManagerType = policyManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding policy manager type: [" + policyManagerType + "]");
            }
            policyManagerSelector =
                (ServiceSelector) manager.lookup(PolicyManager.ROLE + "Selector");
            policyManager = (PolicyManager) policyManagerSelector.select(policyManagerType);
            configureOrParameterize(policyManager, policyManagerConfiguration);
        }
    }

    /**
     * Sets up the authenticator.
     * 
     * @throws ServiceException when something went wrong.
     */
    protected void setupAuthenticator() throws ServiceException {
        authenticator = (Authenticator) manager.lookup(Authenticator.ROLE);
    }

    private ServiceManager manager;

    /**
     * Set the global component manager.
     * 
     * @param manager The global component manager
     * @exception ComponentException
     * @throws ServiceException when something went wrong.
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the service manager.
     * 
     * @return A service manager.
     */
    protected ServiceManager getManager() {
        return manager;
    }

    /**
     * Returns the authorizers of this action.
     * 
     * @return An array of authorizers.
     */
    public Authorizer[] getAuthorizers() {

        Authorizer[] authorizerArray = new Authorizer[authorizers.size()];
        for (int i = 0; i < authorizers.size(); i++) {
            String key = (String) authorizerKeys.get(i);
            authorizerArray[i] = (Authorizer) authorizers.get(key);
        }

        return authorizerArray;
    }

    /**
     * Returns if this action has authorizers.
     * 
     * @return A boolean value.
     */
    protected boolean hasAuthorizers() {
        return !authorizers.isEmpty();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {

        if (accreditableManagerSelector != null) {
            if (accreditableManager != null) {
                accreditableManager.removeItemManagerListener(this);
                accreditableManagerSelector.release(accreditableManager);
            }
            getManager().release(accreditableManagerSelector);
        }

        if (policyManagerSelector != null) {
            if (policyManager != null) {
                policyManagerSelector.release(policyManager);
            }
            getManager().release(policyManagerSelector);
        }

        if (authorizerSelector != null) {
            Authorizer[] authorizers = getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                authorizerSelector.release(authorizers[i]);
            }
            getManager().release(authorizerSelector);
        }

        if (authenticator != null) {
            getManager().release(authenticator);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this +"]");
        }
    }

    /**
     * Returns the accreditable manager.
     * 
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    /**
     * Returns the policy manager.
     * 
     * @return A policy manager.
     */
    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    /**
     * Returns the authenticator.
     * 
     * @return The authenticator.
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Checks if this identity was initialized by this access controller.
     * 
     * @param identity An identity.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    public boolean ownsIdenity(Identity identity) throws AccessControlException {
        return identity.belongsTo(getAccreditableManager());
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#setupIdentity(org.apache.cocoon.environment.Request)
     */
    public void setupIdentity(Request request) throws AccessControlException {
        Session session = request.getSession(true);
        if (!hasValidIdentity(session)) {
            Identity identity = new Identity();
            String remoteAddress = request.getRemoteAddr();

            Machine machine = new Machine(remoteAddress);
            IPRange[] ranges = accreditableManager.getIPRangeManager().getIPRanges();
            for (int i = 0; i < ranges.length; i++) {
                if (ranges[i].contains(machine)) {
                    machine.addIPRange(ranges[i]);
                }
            }

            identity.addIdentifiable(machine);
            session.setAttribute(Identity.class.getName(), identity);
        }
    }

    /**
     * Checks if the session contains an identity that is not null and belongs to the used access
     * controller.
     * 
     * @param session The current session.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    protected boolean hasValidIdentity(Session session) throws AccessControlException {
        boolean valid = true;
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        if (identity == null || !ownsIdenity(identity)) {
            valid = false;
        }
        return valid;
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManagerListener#itemAdded(org.apache.lenya.cms.ac.Item)
     */
    public void itemAdded(Item item) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "]");
        }
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManagerListener#itemRemoved(org.apache.lenya.cms.ac.Item)
     */
    public void itemRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "]");
            getLogger().debug("Notifying policy manager");
        }
        getPolicyManager().accreditableRemoved(getAccreditableManager(), (Accreditable) item);
    }

}
