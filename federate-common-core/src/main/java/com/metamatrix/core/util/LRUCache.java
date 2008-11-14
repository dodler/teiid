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

package com.metamatrix.core.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * This class replaces a verbose legacy implementation of LRUCaching.
 * However technically this is an eldest first purging policy.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * Default amount of space in the cache
     */
    protected static final int DEFAULT_SPACELIMIT = 100;
	
	protected int maxSize;
	
    /**
     * Creates a new cache.  Size of cache is defined by 
     * <code>DEFAULT_SPACELIMIT</code>.
     */
    public LRUCache() {
        this(DEFAULT_SPACELIMIT);
    }
	
	public LRUCache(int maxSize) {
		super(16, .75f, true);
		this.maxSize = maxSize;
	}
	
	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > maxSize;
	}

	public int getSpaceLimit() {
		return maxSize;
	}

}
