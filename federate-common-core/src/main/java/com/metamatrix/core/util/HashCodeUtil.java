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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <P>This class provides utility functions for generating good
 * hash codes.  Hash codes generated with these methods should
 * have a reasonably good distribution when placed in a hash
 * structure such as Hashtable, HashSet, or HashMap.</P>
 *
 * <P>General usage is something like:</P>
 * <PRE>
 * public int hashCode() {
 *     int hc = 0;	// or = super.hashCode();
 *     hc = HashCodeUtil.hashCode(hc, intField);
 *     hc = HashCodeUtil.hashCode(hc, objectField);
 *     // etc, etc
 *     return hc;
 * }
 * </PRE>
 */
public final class HashCodeUtil {

	// Prime number used in improving distribution: 1,000,003
	private static final int PRIME = 1000003;
	
	public static final int hashCode(int previous, boolean x) {
		return (PRIME*previous) + (x ? 1 : 0);		
	}

	public static final int hashCode(int previous, int x) {
		return (PRIME*previous) + x;		
	}

	public static final int hashCode(int previous, long x) {
		// convert to two ints
		return (PRIME*previous) +
			   (int) (PRIME*(x >>> 32) + (x & 0xFFFFFFFF));		
	}

	public static final int hashCode(int previous, float x) {
		return hashCode(previous, (x == 0.0F) ? 0 : Float.floatToIntBits(x));
	}

	public static final int hashCode(int previous, double x) {
		// convert to long
		return hashCode(previous, (x == 0.0) ? 0L : Double.doubleToLongBits(x));
	}

	public static final int hashCode(int previous, Object x) {
		return (x == null) ? (PRIME*previous) : (PRIME*previous) + x.hashCode();
	}

	public static final int hashCode(int previous, Object[] x) {
		if(x == null) {
			return PRIME*previous;
		}
		int hc = 0;
		for(int i=0; i<x.length; i++) {
			hc = hashCode(hc, x[i]);
		}
		return hc;
	}

	/**
	 * Compute a hash code on a large array by walking the list
	 * and combining the hash code at every exponential index:
	 * 1, 2, 4, 8, ...  This has been shown to give a good hash
	 * for good time complexity.  
	 */
	public static final int expHashCode(int previous, Object[] x) {
		if(x == null) {
			return PRIME*previous;
		}
		int hc = (PRIME*previous) + x.length;
		int index = 1;
		int xlen = x.length+1;	// switch to 1-based
		while(index < xlen) {
			hc = hashCode(hc, x[index-1]);
			index = index << 1;		// left shift by 1 to double
		}
		return hc;
	}

	/**
	 * Compute a hash code on a large list by walking the list
	 * and combining the hash code at every exponential index:
	 * 1, 2, 4, 8, ...  This has been shown to give a good hash
	 * for good time complexity.  
	 */	 
	public static final int expHashCode(int previous, List x) {
		if(x == null) {
			return PRIME*previous;
		}
		int hc = (PRIME*previous) + x.size();
		int index = 1;
		int xlen = x.size()+1;	// switch to 1-based
		while(index < xlen) {
			hc = hashCode(hc, x.get(index-1));
			index = index << 1;		// left shift by 1 to double
		}
		return hc;
	}

	/**
	 * Compute a hash code on a large collection by walking the list
	 * and combining the hash code at every exponential index:
	 * 1, 2, 4, 8, ...  This has been shown to give a good hash
	 * for good time complexity.  This uses an iterator to walk
	 * the collection and pull the necessary hash code values.
	 * Slower than a List or array but faster than getting EVERY value.	 
	 */
	public static final int expHashCode(int previous, Collection x) {
		if(x == null || x.size() == 0) {
			return PRIME*previous;
		}
		int size = x.size();				// size of collection
		int hc = (PRIME*previous) + size;	// hash code so far
		int skip = 0;						// skip between samples
		int total = 0;						// collection examined already
		Iterator iter = x.iterator();		// collection iterator
		Object obj = iter.next();			// last iterated object, primed at first
		while(total < size) {
			for(int i=0; i<skip; i++) {		// skip to next sample
				obj = iter.next();
			}
			hc = hashCode(hc, obj);			// add sample to hashcode
			skip = (skip == 0) ? 1 : skip << 1;		// left shift by 1 to double
			total += skip;					// update total
		}
		return hc;
	}

}
