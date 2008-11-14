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

package com.metamatrix.query.sql.proc;

import junit.framework.*;
import com.metamatrix.core.util.UnitTestUtil;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.lang.*;

/**
 *
 * @author gchadalavadaDec 9, 2002
 */
public class TestCommandStatement  extends TestCase {

	// ################################## FRAMEWORK ################################
	
	public TestCommandStatement(String name) { 
		super(name);
	}
	
	// ################################## TEST HELPERS ################################	

	public static final CommandStatement sample1() {
		QueryParser parser = new QueryParser();		
		try {
			Query query = (Query) parser.parseCommand("Select x from y"); //$NON-NLS-1$
			return new CommandStatement(query);
		} catch(Exception e) { return null;}
	}

	public static final CommandStatement sample2() { 
		QueryParser parser = new QueryParser();		
		try {
			Update update = (Update) parser.parseCommand("UPDATE x SET x = 'y'"); //$NON-NLS-1$
			return new CommandStatement(update);
		} catch(Exception e) { return null;}
	}

	// ################################## ACTUAL TESTS ################################
	
	public void testSelfEquivalence(){
		CommandStatement s1 = sample1();
		int equals = 0;
		UnitTestUtil.helpTestEquivalence(equals, s1, s1);
	}

	public void testEquivalence(){
		CommandStatement s1 = sample1();
		CommandStatement s1a = sample1();
		int equals = 0;
		UnitTestUtil.helpTestEquivalence(equals, s1, s1a);
	}
	
	public void testNonEquivalence(){
		CommandStatement s1 = sample1();
		CommandStatement s2 = sample2();
		int equals = -1;
		UnitTestUtil.helpTestEquivalence(equals, s1, s2);
	}
}
