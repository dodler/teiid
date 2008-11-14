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

import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.ExpressionSymbol;

import junit.framework.TestCase;

public class TestExpressionSymbol extends TestCase {

    
    public void testExpressionHashCode() {
        Expression expr1 = new Constant(new Integer(1));
        Expression expr2 = new Constant(new Integer(2));
        ExpressionSymbol symbol1 = new ExpressionSymbol("foo", expr1); //$NON-NLS-1$
        ExpressionSymbol symbol2 = new ExpressionSymbol("bar", expr2); //$NON-NLS-1$
        
        assertFalse(symbol1.hashCode() == symbol2.hashCode());
    }
    
    public void testExpressionHashCode1() {
        Expression expr1 = new Constant(new Integer(1));
        Expression expr2 = new Constant(new Integer(1));
        ExpressionSymbol symbol1 = new ExpressionSymbol("foo", expr1); //$NON-NLS-1$
        ExpressionSymbol symbol2 = new ExpressionSymbol("bar", expr2); //$NON-NLS-1$
        
        assertTrue(symbol1.hashCode() == symbol2.hashCode());
    }

    
}
