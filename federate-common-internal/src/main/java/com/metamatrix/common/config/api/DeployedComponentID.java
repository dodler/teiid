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

package com.metamatrix.common.config.api;

import com.metamatrix.common.namedobject.IDVerifier;
import com.metamatrix.core.util.Assertion;

public class DeployedComponentID extends ComponentObjectID {

    private final ConfigurationID configID;
    private final HostID hostID;
    private final VMComponentDefnID vmID;
    private final ServiceComponentDefnID serviceID;
    private final ProductServiceConfigID pscID;

    /**
     * Instantiate a VM Deployed Component ID 
     */
    public DeployedComponentID(String name, ConfigurationID configId, HostID hostId, VMComponentDefnID vmId) {
        super(DeployedComponentID.createDeployedName(name, configId, hostId, vmId));
        this.configID = configId;
        this.hostID = hostId;
        this.vmID = vmId;
        this.serviceID = null;
        this.pscID = null;
    }

    /**
     * Instantiate a Service or Connector Binding deployed service, that incorporates the
     * PSC name into it
     */
    public DeployedComponentID(String name, ConfigurationID configId, HostID hostId, VMComponentDefnID vmId, ProductServiceConfigID pscID, ServiceComponentDefnID serviceId) {
        super(DeployedComponentID.createDeployedName(name, configId, hostId, vmId, pscID, serviceId));
        this.configID = configId;
        this.hostID = hostId;
        this.vmID = vmId;
        this.serviceID = serviceId;
        this.pscID = pscID;
    }

    /**
     * Responsible for creating the structuring VM id for this deployed component
     */
    private static final String createDeployedName(String name, ConfigurationID configID, HostID hostID, VMComponentDefnID vmComponentID) {
		Assertion.isNotNull(configID);
		Assertion.isNotNull(name);
		Assertion.isNotNull(hostID);
		Assertion.isNotNull(vmComponentID);

        StringBuffer sb = new StringBuffer(configID.getName());
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(hostID.getName());
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(vmComponentID.getName());

        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(name);

        return sb.toString();

    }

    /**
     * Responsible for creating the structuring Service id for this deployed component
     */
    private static final String createDeployedName(String name, ConfigurationID configID, HostID hostID, VMComponentDefnID vmComponentID, ProductServiceConfigID pscID, ServiceComponentDefnID serviceComponentID) {
		Assertion.isNotNull(configID);
		Assertion.isNotNull(pscID);
		Assertion.isNotNull(hostID);
		Assertion.isNotNull(vmComponentID);
		Assertion.isNotNull(serviceComponentID);
        
  
        StringBuffer sb = new StringBuffer(configID.getName());
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(hostID.getName());
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(vmComponentID.getName());
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(pscID.getName());
        
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(serviceComponentID.getName());
 
        
        sb.append(IDVerifier.DELIMITER_CHARACTER);
        sb.append(name);


        return sb.toString();
    }

    public ConfigurationID getConfigID() {
        return configID;
    }

    public HostID getHostID() {
        return hostID;
    }
    public VMComponentDefnID getVMID() {
        return vmID;
    }
    public ServiceComponentDefnID getServiceID() {
        return serviceID;
    }
    public ProductServiceConfigID getPscID() {
        return pscID;
    }
}

