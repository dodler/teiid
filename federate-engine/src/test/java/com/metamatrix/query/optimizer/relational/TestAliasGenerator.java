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

package com.metamatrix.query.optimizer.relational;

import junit.framework.TestCase;

import com.metamatrix.api.exception.query.QueryValidatorException;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.resolver.TestResolver;
import com.metamatrix.query.rewriter.QueryRewriter;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.unittest.FakeMetadataFactory;

public class TestAliasGenerator extends TestCase {
    
    private Command helpTest(String sql,
                          String expected, 
                          boolean aliasGroups,
                          QueryMetadataInterface metadata) throws QueryValidatorException {
        Command command = TestResolver.helpResolve(sql, metadata, null);
        QueryRewriter.rewrite(command, null, metadata, null);
        command.acceptVisitor(new AliasGenerator(aliasGroups));
        assertEquals(expected, command.toString());
        return command;
    }

    /**
     * Ensures that views are named with v_ even without metadata
     */
    public void testViewAliasing() throws Exception {
        String sql = "select y.e1 from (select pm1.g1.e1 from pm1.g1) y"; //$NON-NLS-1$
        Query command = (Query)QueryParser.getQueryParser().parseCommand(sql);
        ((ElementSymbol)command.getSelect().getSymbol(0)).setGroupSymbol(new GroupSymbol("y")); //$NON-NLS-1$
        command.acceptVisitor(new AliasGenerator(true));
        assertEquals("SELECT v_0.c_0 FROM (SELECT pm1.g1.e1 AS c_0 FROM pm1.g1 AS g_0) AS v_0", command.toString()); //$NON-NLS-1$
    }
    
    public void testLongOrderByAlias() throws Exception {
        String sql = "select pm1.g1.e1 || pm1.g1.e2 as asfasdfadfasdfasdfadfasdfadsfasdfasdfasdfasdfasdfadfa, pm1.g1.e2 from pm1.g1 order by asfasdfadfasdfasdfadfasdfadsfasdfasdfasdfasdfasdfadfa"; //$NON-NLS-1$
        String expected = "SELECT (g_0.e1 || g_0.e2) AS c_0, g_0.e2 AS c_1 FROM pm1.g1 AS g_0 ORDER BY c_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.example1Cached());
    }
    
    public void testOrderBySymbolName() throws Exception {
        String sql = "select e1 from pm1.g1 order by e1"; //$NON-NLS-1$
        String expected = "SELECT g_0.e1 AS c_0 FROM pm1.g1 AS g_0 ORDER BY c_0"; //$NON-NLS-1$
        Query command = (Query)helpTest(sql, expected, true, FakeMetadataFactory.example1Cached());
        assertEquals(((SingleElementSymbol)command.getOrderBy().getVariables().get(0)).getName(), "e1"); //$NON-NLS-1$
        assertEquals(((SingleElementSymbol)command.getProjectedSymbols().get(0)).getShortName(), "e1"); //$NON-NLS-1$
    }
    
    public void testInlineViewWithSubQuery() throws Exception {
        String sql = "select intnum from (select intnum from bqt1.smallb where intnum in (select intnum a from bqt1.smalla)) b"; //$NON-NLS-1$
        String expected = "SELECT v_0.c_0 FROM (SELECT g_0.intnum AS c_0 FROM bqt1.smallb AS g_0 WHERE g_0.intnum IN (SELECT g_1.intnum FROM bqt1.smalla AS g_1)) AS v_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.exampleBQTCached());
    }
    
    public void testInlineViewOrderBy() throws Exception {
        String sql = "select intnum from (select intnum from bqt1.smallb) b order by b.intnum"; //$NON-NLS-1$
        String expected = "SELECT v_0.c_0 FROM (SELECT g_0.intnum AS c_0 FROM bqt1.smallb AS g_0) AS v_0 ORDER BY c_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.exampleBQTCached());
    }
    
    public void testNestedInlineViewOrderBy() throws Exception {
        String sql = "select * from (select intnum x from (select intnum from bqt1.smallb) b order by x) y order by x"; //$NON-NLS-1$
        String expected = "SELECT v_1.c_0 FROM (SELECT v_0.c_0 FROM (SELECT g_0.intnum AS c_0 FROM bqt1.smallb AS g_0) AS v_0 ORDER BY c_0) AS v_1 ORDER BY c_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.exampleBQTCached());
    }
    
    public void testInlineViewWithOnClause() throws Exception {
        String sql = "select abcd.efg from (select intkey as efg from bqt1.smalla) abcd inner join (select intnum from bqt1.smallb) b on (b.intnum = abcd.efg)"; //$NON-NLS-1$
        String expected = "SELECT v_0.c_0 FROM (SELECT g_0.intkey AS c_0 FROM bqt1.smalla AS g_0) AS v_0 INNER JOIN (SELECT g_1.intnum AS c_0 FROM bqt1.smallb AS g_1) AS v_1 ON v_1.c_0 = v_0.c_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.exampleBQTCached());
    }

    public void testUnionOrderBy() throws Exception {
        String sql = "select e1, e2 as c_0 from pm1.g1 union all select 1, e1 from pm1.g2 order by e1"; //$NON-NLS-1$
        String expected = "SELECT g_1.e1 AS c_0, g_1.e2 AS c_1 FROM pm1.g1 AS g_1 UNION ALL SELECT '1' AS c_0, g_0.e1 AS c_1 FROM pm1.g2 AS g_0 ORDER BY c_0"; //$NON-NLS-1$
        helpTest(sql, expected, true, FakeMetadataFactory.example1Cached());
    }
    
}
