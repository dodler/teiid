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

/*
 * Date: Sep 25, 2003
 * Time: 4:37:23 PM
 */
package com.metamatrix.server.connector.service;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.comm.api.ResultsReceiver;
import com.metamatrix.data.exception.ConnectorException;
import com.metamatrix.dqp.internal.datamgr.ConnectorID;
import com.metamatrix.dqp.internal.process.DQPWorkContext;
import com.metamatrix.dqp.message.AtomicRequestID;
import com.metamatrix.dqp.message.AtomicRequestMessage;
import com.metamatrix.dqp.message.AtomicResultsMessage;
import com.metamatrix.dqp.message.RequestID;
import com.metamatrix.platform.security.api.service.SecureService;
import com.metamatrix.platform.service.api.ServiceInterface;
import com.metamatrix.platform.service.api.exception.ServiceException;
import com.metamatrix.query.optimizer.capabilities.SourceCapabilities;

/**
 * Interface ConnectorServiceInterface.
 *
 */
public interface ConnectorServiceInterface extends ServiceInterface, SecureService {
    static final String SERVICE_NAME = "ConnectorService"; //$NON-NLS-1$

    /**
     * Get the <code>ConnectorID</code> of the conector we're servicing.
     * @return The <code>ConnectorID</code>.
     */
    ConnectorID getConnectorID() throws ServiceException, RemoteException;
    
    public SourceCapabilities getCapabilities(RequestID requestId, Serializable executionPayload, DQPWorkContext message) throws ConnectorException;
    
    /**
     * Execute the given request on a <code>Connector</code>.
     * @param request
     * @return
     */
    void executeRequest(AtomicRequestMessage request, ResultsReceiver<AtomicResultsMessage> resultListener) 
        throws MetaMatrixComponentException;
    
	void cancelRequest(AtomicRequestID request) throws MetaMatrixComponentException;

	void closeRequest(AtomicRequestID request) throws MetaMatrixComponentException;
	
	void requestBatch(AtomicRequestID request) throws MetaMatrixComponentException;

}
