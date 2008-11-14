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

package com.metamatrix.common.buffer.impl;



/** 
 * Represents a logical grouping of tuple sources managed by the buffer manager.
 * Tuple sources are typically grouped by session, and the groupName is typically a sessionID/connectionID.
 * @since 4.3
 */
class TupleGroupInfo {
    
    private String groupName;
    /** The bytes of memory used by this tuple group*/
    private long memoryUsed;
    
    TupleGroupInfo(String groupName) {
        this.groupName = groupName;
    }
    
    String getGroupName() {
        return groupName;
    }
    
    long getGroupMemoryUsed() {
        // no locking required. See MemoryState.NOTE1
        return memoryUsed;
    }
    
    long reserveMemory(long bytes) {
        // no locking required. See MemoryState.NOTE1
        return memoryUsed += bytes;
    }
    
    long releaseMemory(long bytes) {
        // no locking required. See MemoryState.NOTE1
        return memoryUsed -= bytes;
    }
}
