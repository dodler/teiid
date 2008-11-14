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

package com.metamatrix.query.sql.lang;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.metamatrix.core.util.UnitTestUtil;
import com.metamatrix.query.sql.symbol.AliasSymbol;
import com.metamatrix.query.sql.symbol.AllSymbol;
import com.metamatrix.query.sql.symbol.ElementSymbol;

public class TestSelect extends TestCase {

	// ################################## FRAMEWORK ################################
	
	public TestSelect(String name) { 
		super(name);
	}	
	
	// ################################## TEST HELPERS ################################	

	public static final Select sample1() { 
		List symbols = new ArrayList();
		symbols.add(new ElementSymbol("a")); //$NON-NLS-1$
		symbols.add(new ElementSymbol("b")); //$NON-NLS-1$

	    Select select = new Select();
	    AllSymbol all = new AllSymbol();
	    all.setElementSymbols(symbols);
	    select.addSymbol(all);
	    return select;	
	}

	public static final Select sample2() { 
	    Select select = new Select();
	    select.addSymbol(new ElementSymbol("a"));	 //$NON-NLS-1$
	    select.addSymbol(new ElementSymbol("b"));	 //$NON-NLS-1$
	    select.addSymbol(new ElementSymbol("c")); //$NON-NLS-1$
	    select.addSymbol(new AliasSymbol("Z", new ElementSymbol("ZZ 9 Plural Z Alpha"))); //$NON-NLS-1$ //$NON-NLS-2$
	    return select;	
	}
			
	// ################################## ACTUAL TESTS ################################
	
	public void testGetProjectedNoElements() {    
	    Select select = new Select();
	    select.addSymbol(new AllSymbol());
	    
	    List projectedSymbols = select.getProjectedSymbols();
	    assertEquals("Did not get empty list for select * with no elements: ", new ArrayList(), projectedSymbols); //$NON-NLS-1$
	}

	public void testGetProjectedWithStar() {    
		List symbols = new ArrayList();
		symbols.add(new ElementSymbol("a")); //$NON-NLS-1$
		symbols.add(new ElementSymbol("b")); //$NON-NLS-1$

	    Select select = new Select();
	    AllSymbol all = new AllSymbol();
	    all.setElementSymbols(symbols);
	    select.addSymbol(all);
	    
	    List projectedSymbols = select.getProjectedSymbols();
	    assertEquals("Did not get correct list for select *: ", symbols, projectedSymbols); //$NON-NLS-1$
	}

	public void testSelfEquivalence(){
		Select s1 = sample1();
		int equals = 0;
		UnitTestUtil.helpTestEquivalence(equals, s1, s1);
	}

	public void testEquivalence(){
		Select s1 = sample1();
		Select s1a = sample1();
		int equals = 0;
		UnitTestUtil.helpTestEquivalence(equals, s1, s1a);
	}
	
	public void testNonEquivalence(){
		Select s1 = sample1();
		Select s2 = sample2();
		int equals = -1;
		UnitTestUtil.helpTestEquivalence(equals, s1, s2);
	}

}
