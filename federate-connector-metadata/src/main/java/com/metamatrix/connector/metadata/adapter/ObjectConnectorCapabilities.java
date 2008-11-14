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

package com.metamatrix.connector.metadata.adapter;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.data.api.ConnectorCapabilities;
import com.metamatrix.data.basic.BasicConnectorCapabilities;

/**
 * Describes the capabilities of the object connector, which are few.
 */
public class ObjectConnectorCapabilities extends BasicConnectorCapabilities {

    private static ObjectConnectorCapabilities INSTANCE = new ObjectConnectorCapabilities(); 

    public static ConnectorCapabilities getInstance() {
        return INSTANCE;
    }

    private ObjectConnectorCapabilities() {
    }

    /* 
     * @see com.metamatrix.data.ConnectorCapabilities#supportsExecutionMode(int)
     */
    public boolean supportsExecutionMode(final int executionMode) {
        switch(executionMode) {
            case ConnectorCapabilities.EXECUTION_MODE.SYNCH_QUERY:
            case ConnectorCapabilities.EXECUTION_MODE.PROCEDURE:
                return true;
            default:
                return false;
        }
    }    

    /* 
     * @see com.metamatrix.data.api.ConnectorCapabilities#supportsAndCriteria()
     */
    public boolean supportsAndCriteria() {
        return true;
    }

    /* 
     * @see com.metamatrix.data.api.ConnectorCapabilities#supportsCompareCriteria()
     */
    public boolean supportsCompareCriteria() {
        return true;
    }

    /* 
     * @see com.metamatrix.data.api.ConnectorCapabilities#supportsCompareCriteriaEquals()
     */
    public boolean supportsCompareCriteriaEquals() {
        return true;
    }

    /** 
     * @see com.metamatrix.data.basic.BasicConnectorCapabilities#supportsLikeCriteria()
     * @since 4.3
     */
    public boolean supportsLikeCriteria() {
        return true;
    }

    /* 
     * @see com.metamatrix.data.api.ConnectorCapabilities#supportsCriteria()
     */
    public boolean supportsCriteria() {
        return true;
    }

    /** 
     * @see com.metamatrix.data.basic.BasicConnectorCapabilities#supportsScalarFunctions()
     * @since 4.3
     */
    public boolean supportsScalarFunctions() {
        return true;
    }

    /** 
     * @see com.metamatrix.data.basic.BasicConnectorCapabilities#getSupportedFunctions()
     * @since 4.3
     */
    public List getSupportedFunctions() {
        List supportedFunctions = new ArrayList();
        List superFunctions = super.getSupportedFunctions();
        if(superFunctions != null) {
            supportedFunctions.addAll(superFunctions);
        }
        supportedFunctions.add("UPPER"); //$NON-NLS-1$
        supportedFunctions.add("LOWER"); //$NON-NLS-1$
        supportedFunctions.add("UCASE"); //$NON-NLS-1$
        supportedFunctions.add("LCASE"); //$NON-NLS-1$
        return supportedFunctions;
    }
    
    /** 
     * @see com.metamatrix.data.basic.BasicConnectorCapabilities#supportsLikeCriteriaEscapeCharacter()
     * @since 5.0
     */
    public boolean supportsLikeCriteriaEscapeCharacter() {
        return true;
    }    
    
}