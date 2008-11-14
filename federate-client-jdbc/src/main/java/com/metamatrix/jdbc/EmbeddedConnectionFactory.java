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

package com.metamatrix.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/** 
 * A marker interface for creating connections Embedded DQP. The interface is 
 * defined so that it can be used with DQP class loading.  
 */
public interface EmbeddedConnectionFactory {

    /**
     * Create a Connection to the DQP. This will load a DQP instance if one is not present 
     * @param properties
     * @return Connection to DQP
     * @throws SQLException
     */
    public Connection createConnection(Properties properties) throws SQLException;  
    
    /**
     * Register a Connection lifecycle listener to the this DQP instance  
     * @param listener a listerner for connection related events.
     */
    public void registerConnectionListener(ConnectionListener listener);
    
    /**
     * Shutdown the connection factory, including the DQP and all its existing connections 
     */
    public void shutdown() throws SQLException;
}
