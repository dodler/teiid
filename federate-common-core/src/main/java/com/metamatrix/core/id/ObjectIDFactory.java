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

package com.metamatrix.core.id;


/**
 * The ObjectIDDescriptor defines the interface that is used to describe a particular type of 
 * {@link ObjectID}.
 */
public interface ObjectIDFactory {
    
    /**
     * Return the protocol for the type of ObjectID described by this object.
     * @return the protocol
     */
    String getProtocol();
    
    /**
     * Return the description for the type of ObjectID described by this object.
     * @return the description
     */
    String getDescription();
    
    /**
     * Create a new GlobalUID instance using this protocol.
     * @return the new instance
     */
    ObjectID create();

    /**
     * Return whether the specified ObjectID instance is valid.  Only ObjectID instances
     * that are for this protocol will be passed in.
     * @param id the ID that is to be validated, and which is never null
     * @return true if the instance is valid for this protocol, or false if
     * it is not valid.
     */
    boolean validate(ObjectID id);
    /**
     * Attempt to convert the specified string to the appropriate ObjectID instance.
     * @param value the stringified id (the result of {@link ObjectID#toString()}),
     * and should never null or zero length
     * @return the ObjectID instance for the stringified ID if this factory is able
     * to parse the string, or null if the factory is unaware of the specified format.
     * @throws InvalidIDException if the parser is aware of this protocol, but it is of the wrong
     * format for this type of ObjectID.
     */
    ObjectID stringToObject(String value) throws InvalidIDException;

    /**
     * Attempt to convert the specified string to the appropriate ObjectID instance.
     * This method is called by the {@link IDGenerator#stringToObject(String)} method, which
     * must process the protocol to determine the correct parser to use.  As such, it guarantees
     * that the parser that receives this call can assume that the protocol was equal to the
     * protocol returned by the parser's {@link ObjectIDDescriptor#getProtocol()}.
     * @param value the stringified id with the protocol and ObjectID.DELIMITER already
     * removed, and should never null or zero length
     * @return the ObjectID instance for the stringified ID if this factory is able
     * to parse the string, or null if the factory is unaware of the specified format.
     * @throws InvalidIDException if the parser is aware of this protocol, but it is of the wrong
     * format for this type of ObjectID.
     */
    ObjectID stringWithoutProtocolToObject(String value) throws InvalidIDException;

}
