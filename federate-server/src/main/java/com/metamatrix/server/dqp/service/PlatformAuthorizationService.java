/*
 * JBoss, Home of Professional Open Source.
 * Copyright (C) 2008 Red Hat, Inc.
 * Copyright (C) 2000-2007 MetaMatrix, Inc.
 * Licensed to Red Hat, Inc. under one or more contributor 
 * license agreements.  See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package com.metamatrix.server.dqp.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.security.AuthorizationMgmtException;
import com.metamatrix.api.exception.security.InvalidSessionException;
import com.metamatrix.api.exception.security.InvalidUserException;
import com.metamatrix.api.exception.security.MembershipServiceException;
import com.metamatrix.common.application.ApplicationEnvironment;
import com.metamatrix.common.application.exception.ApplicationInitializationException;
import com.metamatrix.common.application.exception.ApplicationLifecycleException;
import com.metamatrix.common.config.CurrentConfiguration;
import com.metamatrix.dqp.internal.process.DQPWorkContext;
import com.metamatrix.dqp.service.AuthorizationService;
import com.metamatrix.platform.security.api.AuthorizationActions;
import com.metamatrix.platform.security.api.AuthorizationPermission;
import com.metamatrix.platform.security.api.AuthorizationRealm;
import com.metamatrix.platform.security.api.BasicAuthorizationPermission;
import com.metamatrix.platform.security.api.BasicAuthorizationPermissionFactory;
import com.metamatrix.platform.security.api.SessionToken;
import com.metamatrix.platform.security.api.StandardAuthorizationActions;
import com.metamatrix.platform.security.api.service.AuthorizationServiceInterface;
import com.metamatrix.platform.security.api.service.AuthorizationServicePropertyNames;
import com.metamatrix.platform.security.api.service.SessionServiceInterface;
import com.metamatrix.platform.security.util.RolePermissionFactory;
import com.metamatrix.platform.service.api.exception.ServiceException;
import com.metamatrix.platform.util.ProductInfoConstants;
import com.metamatrix.server.ServerPlugin;
import com.metamatrix.server.util.ServerAuditContexts;

/**
 */
public class PlatformAuthorizationService implements AuthorizationService {

    // Permission factory is reusable and threadsafe
    private static final BasicAuthorizationPermissionFactory PERMISSION_FACTORY = new BasicAuthorizationPermissionFactory();

    // Flag for whether entitlements are checked or not
    static boolean USE_ENTITLEMENTS = Boolean.valueOf(CurrentConfiguration.getProperty(AuthorizationServicePropertyNames.DATA_ACCESS_AUTHORIZATION_ENABLED)).booleanValue();

    private AuthorizationServiceInterface authInterface;
    private SessionServiceInterface sessionInterface;

    public PlatformAuthorizationService(AuthorizationServiceInterface authInterface, SessionServiceInterface sessionInterface) {
        this.authInterface = authInterface;
        this.sessionInterface = sessionInterface;
    }

    /*
     * @see com.metamatrix.common.application.ApplicationService#initialize(java.util.Properties)
     */
    public void initialize(Properties props) throws ApplicationInitializationException {
    }

    /*
     * @see com.metamatrix.common.application.ApplicationService#start(com.metamatrix.common.application.ApplicationEnvironment)
     */
    public void start(ApplicationEnvironment environment) throws ApplicationLifecycleException {
    }

    /*
     * @see com.metamatrix.common.application.ApplicationService#bind()
     */
    public void bind() throws ApplicationLifecycleException {
    }

    /*
     * @see com.metamatrix.common.application.ApplicationService#unbind()
     */
    public void unbind() throws ApplicationLifecycleException {
    }

    /*
     * @see com.metamatrix.common.application.ApplicationService#stop()
     */
    public void stop() throws ApplicationLifecycleException {
    }

    /*
     */
    public Collection getInaccessibleResources(String connectionID, int action, Collection resources, int context)
        throws MetaMatrixComponentException {
        SessionToken token = DQPWorkContext.getWorkContext().getSessionToken();
        AuthorizationRealm realm = getRealm(token);
        AuthorizationActions actions = getActions(action);
        Collection permissions = createPermissions(realm, resources, actions);
        String auditContext = getAuditContext(context);
        Collection inaccessableResources = Collections.EMPTY_LIST;
        try {
            inaccessableResources = this.authInterface.getInaccessibleResources(token, auditContext, permissions);
        } catch (InvalidSessionException e) {
            throw new MetaMatrixComponentException(e, ServerPlugin.Util.getString("PlatformAuthorizationService.Invalid_session")); //$NON-NLS-1$
        } catch (AuthorizationMgmtException e) {
            throw new MetaMatrixComponentException(e);
        } catch(RemoteException e) {
            throw new MetaMatrixComponentException(e);
        }

        // Convert inaccessable resources from auth permissions to string resource names
        Collection inaccessableResourceNames = Collections.EMPTY_LIST;
        if ( inaccessableResources != null && inaccessableResources.size() > 0 ) {
            inaccessableResourceNames = new ArrayList();
            for ( Iterator permItr = inaccessableResources.iterator(); permItr.hasNext(); ) {
                AuthorizationPermission permission = (AuthorizationPermission) permItr.next();
                inaccessableResourceNames.add(permission.getResourceName());
            }
        }
        return inaccessableResourceNames;
    }
    
    public boolean hasRole(String connectionID, String roleType, String roleName) throws MetaMatrixComponentException {
        SessionToken token = DQPWorkContext.getWorkContext().getSessionToken();
        
        AuthorizationRealm realm = null;
        
        if (ADMIN_ROLE.equalsIgnoreCase(roleType)) {
            realm = RolePermissionFactory.getRealm();
        } else if (DATA_ROLE.equalsIgnoreCase(roleType)){
            realm = getRealm(token);
        } else {
            return false;
        }
        
        try {
            return authInterface.hasPolicy(token, realm, roleName);
        } catch (AuthorizationMgmtException err) {
            throw new MetaMatrixComponentException(err);
        } catch (InvalidUserException err) {
            throw new MetaMatrixComponentException(err);
        } catch (MembershipServiceException err) {
            throw new MetaMatrixComponentException(err);
        } catch (ServiceException err) {
            throw new MetaMatrixComponentException(err);
        } catch(RemoteException e) {
            throw new MetaMatrixComponentException(e);
        }
    }

    /**
     * Determine whether entitlements checking is enabled on the server.
     *
     * @return <code>true</code> iff server-side entitlements checking is enabled.
     */
    public boolean checkingEntitlements() {
        return USE_ENTITLEMENTS;
    }

    /**
     * Create realm based on token
     * @param token Used to find info about this session
     * @return Realm to use (based on vdb name and version)
     */
    private AuthorizationRealm getRealm(SessionToken token) {
        return
            new AuthorizationRealm(
                token.getProductInfo(ProductInfoConstants.VIRTUAL_DB),
                token.getProductInfo(ProductInfoConstants.VDB_VERSION));
    }

    private AuthorizationActions getActions(int actionCode) {
        switch(actionCode) {
            case AuthorizationService.ACTION_READ: return StandardAuthorizationActions.DATA_READ;
            case AuthorizationService.ACTION_CREATE: return StandardAuthorizationActions.DATA_CREATE;
            case AuthorizationService.ACTION_UPDATE: return StandardAuthorizationActions.DATA_UPDATE;
            case AuthorizationService.ACTION_DELETE: return StandardAuthorizationActions.DATA_DELETE;
            default: return StandardAuthorizationActions.DATA_READ;
        }
    }

    /**
     * Take a list of resources (Strings) and create a list of permissions
     * suitable for sending to the authorization service.
     * @param realm Realm to use
     * @param resources Collection of String, listing resources
     * @param actions Actions to check for
     * @return Collection of BasicAuthorizationPermission
     */
    private Collection createPermissions(AuthorizationRealm realm, Collection resources, AuthorizationActions actions) {
        List permissions = new ArrayList(resources.size());
        Iterator iter = resources.iterator();
        while(iter.hasNext()) {
            String resource = (String) iter.next();

            BasicAuthorizationPermission permission =
                (BasicAuthorizationPermission) PERMISSION_FACTORY.create(resource, realm, actions);

            permissions.add(permission);
        }
        return permissions;
    }

    private String getAuditContext(int auditCode) {
        switch(auditCode) {
            case AuthorizationService.CONTEXT_QUERY:    return ServerAuditContexts.CTX_QUERY;
            case AuthorizationService.CONTEXT_INSERT:   return ServerAuditContexts.CTX_INSERT;
            case AuthorizationService.CONTEXT_UPDATE:   return ServerAuditContexts.CTX_UPDATE;
            case AuthorizationService.CONTEXT_DELETE:   return ServerAuditContexts.CTX_DELETE;
            case AuthorizationService.CONTEXT_PROCEDURE:    return ServerAuditContexts.CTX_PROCEDURE;
            default: return ServerAuditContexts.CTX_QUERY;
        }
    }

}
