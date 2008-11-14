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

package com.metamatrix.query.sql.visitor;

import java.util.Map;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.navigator.DeepPreOrderNavigator;
import com.metamatrix.query.sql.symbol.Symbol;

/**
 * <p> This class is used to update LanguageObjects by replacing the virtual elements/
 * groups present in them with their physical counterparts. It is currently used only
 * to visit Insert/Delete/Update objects and parts of those objects.</p>
 */
public class StaticSymbolMappingVisitor extends AbstractSymbolMappingVisitor {

	private Map symbolMap; // Map between virtual elements/groups and their physical elements

    /**
     * <p> This constructor initialises this object by setting the symbolMap and
     * passing in the command object that is being visited.</p>
     * @param symbolMap A map of virtual elements/groups to their physical counterparts
     */
    public StaticSymbolMappingVisitor(Map symbolMap) {                
        super();
        
        Assertion.isNotNull(symbolMap);
        this.symbolMap = symbolMap;		
    }

    /*
     * @see AbstractSymbolMappingVisitor#getMappedSymbol(Symbol)
     */
    protected Symbol getMappedSymbol(Symbol symbol) {
        return (Symbol) this.symbolMap.get(symbol);
    }
    
    public static void mapSymbols(LanguageObject obj, Map symbolMap) {
        if (obj == null || symbolMap.isEmpty()) {
            return;
        }
        StaticSymbolMappingVisitor ssmv = new StaticSymbolMappingVisitor(symbolMap);
        DeepPreOrderNavigator.doVisit(obj, ssmv);
    }
    
}
