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

package com.metamatrix.dqp.internal.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.query.QueryParserException;
import com.metamatrix.api.exception.query.QueryPlannerException;
import com.metamatrix.api.exception.query.QueryResolverException;
import com.metamatrix.api.exception.query.QueryValidatorException;
import com.metamatrix.common.buffer.BufferManagerFactory;
import com.metamatrix.dqp.internal.process.TestRequest.FakeApplicationEnvironment;
import com.metamatrix.dqp.message.RequestMessage;
import com.metamatrix.platform.security.api.MetaMatrixSessionID;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.optimizer.TestOptimizer;
import com.metamatrix.query.optimizer.capabilities.BasicSourceCapabilities;
import com.metamatrix.query.optimizer.capabilities.CapabilitiesFinder;
import com.metamatrix.query.optimizer.capabilities.DefaultCapabilitiesFinder;
import com.metamatrix.query.optimizer.capabilities.FakeCapabilitiesFinder;
import com.metamatrix.query.optimizer.capabilities.SourceCapabilities.Capability;
import com.metamatrix.query.processor.FakeDataManager;
import com.metamatrix.query.processor.ProcessorDataManager;
import com.metamatrix.query.processor.ProcessorPlan;
import com.metamatrix.query.processor.TestProcessor;
import com.metamatrix.query.unittest.FakeMetadataFacade;
import com.metamatrix.query.unittest.FakeMetadataFactory;
import com.metamatrix.query.unittest.FakeMetadataObject;

public class TestPreparedStatement extends TestCase{
	
	private static final String SESSION_ID = "6";
	
	private static boolean DEBUG = false;
	
	static class TestablePreparedPlanCache extends PreparedPlanCache {
		
		int hitCount;
		
		@Override
		public synchronized PreparedPlan getPreparedPlan(
				String clientConn, String sql,
				boolean isPreparedBatchUpdate) {
			PreparedPlan plan = super.getPreparedPlan(clientConn, sql, isPreparedBatchUpdate);
			if (plan != null && plan.getPlan() != null) {
				hitCount++;
			}
			return plan;
		}
	}
		
	public TestPreparedStatement(String name) { 
		super(name);
	}	        	    
	
	static void helpTestProcessing(String preparedSql, List values, List[] expected, QueryMetadataInterface metadata, boolean callableStatement) throws Exception {
		// Construct data manager with data
        FakeDataManager dataManager = new FakeDataManager();
        TestProcessor.sampleData1(dataManager);    
		
        helpTestProcessing(preparedSql, values, expected, dataManager, metadata, callableStatement);
	}
		
    static void helpTestProcessing(String preparedSql, List values, List[] expected, ProcessorDataManager dataManager, QueryMetadataInterface metadata, boolean callableStatement) throws Exception { 
        TestablePreparedPlanCache prepPlan = new TestablePreparedPlanCache();
        //Create plan
        ProcessorPlan plan = TestPreparedStatement.helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), metadata, prepPlan, SESSION_ID, callableStatement, false);

        // Run query
        TestProcessor.helpProcess(plan, dataManager, expected);
        
        //test cached plan
    	plan = TestPreparedStatement.helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), metadata, prepPlan, SESSION_ID, callableStatement, false);
    	
        //make sure the plan is only created once
        assertEquals("should reuse the plan", 1, prepPlan.hitCount); //$NON-NLS-1$
                
        // Run query again
        TestProcessor.helpProcess(plan, dataManager, expected);
        
        //get the plan again with a new connection
        assertNotNull(TestPreparedStatement.helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), metadata, prepPlan, "7", callableStatement, false)); //$NON-NLS-1$

        assertEquals("new connection should not have used the same plan", 1, prepPlan.hitCount); //$NON-NLS-1$
	}
    	    
    public void testWhere() throws Exception { 
        // Create query 
        String preparedSql = "SELECT pm1.g1.e1, e2, pm1.g1.e3 as a, e4 as b FROM pm1.g1 WHERE e2=?"; //$NON-NLS-1$
        
        // Create expected results
        List[] expected = new List[] { 
            Arrays.asList(new Object[] { "a",   new Integer(0),     Boolean.FALSE,  new Double(2.0) }), //$NON-NLS-1$
            Arrays.asList(new Object[] { "a",   new Integer(0),     Boolean.FALSE,  new Double(2.0) }) //$NON-NLS-1$
        };    
    
		List values = new ArrayList();
		values.add(new Short((short)0));
        
		helpTestProcessing(preparedSql, values, expected, FakeMetadataFactory.example1Cached(), false);
	}
    
    public void testFunctionWithReferencePushDown() throws Exception { 
        // Create query 
        String preparedSql = "SELECT pm1.g1.e1 FROM pm1.g1, pm1.g2 WHERE pm1.g1.e1 = pm1.g2.e1 and pm1.g1.e2+2=?"; //$NON-NLS-1$
        
        //Create plan
        FakeCapabilitiesFinder capFinder = new FakeCapabilitiesFinder();
        BasicSourceCapabilities caps = new BasicSourceCapabilities();
        caps.setCapabilitySupport(Capability.QUERY_WHERE, true);
        caps.setCapabilitySupport(Capability.QUERY_WHERE_COMPARE, true);
        caps.setCapabilitySupport(Capability.QUERY_WHERE_COMPARE_EQ, true);
        caps.setCapabilitySupport(Capability.QUERY_WHERE_COMPARE_GT, true);        
        caps.setCapabilitySupport(Capability.QUERY_WHERE_NOT, true);    
        caps.setCapabilitySupport(Capability.QUERY_WHERE_IN, true);
        caps.setCapabilitySupport(Capability.QUERY_FROM_JOIN, true);
        caps.setCapabilitySupport(Capability.QUERY_FROM_JOIN_OUTER, true);
        caps.setCapabilitySupport(Capability.QUERY_FROM_JOIN_OUTER_FULL, true);
        caps.setCapabilitySupport(Capability.QUERY_FROM_JOIN_SELFJOIN, true);
        caps.setCapabilitySupport(Capability.QUERY_FROM_GROUP_ALIAS, true);
        caps.setCapabilitySupport(Capability.QUERY_ORDERBY, true);
        caps.setCapabilitySupport(Capability.FUNCTION, true);
        caps.setFunctionSupport("+", false); //$NON-NLS-1$
        caps.setFunctionSupport("convert", true); //$NON-NLS-1$
        capFinder.addCapabilities("pm1", caps); //$NON-NLS-1$
        
        FakeMetadataFacade metadata = FakeMetadataFactory.example1();
        FakeMetadataObject bqt1 = metadata.getStore().findObject("pm1", FakeMetadataObject.MODEL); //$NON-NLS-1$  
        bqt1.putProperty(FakeMetadataObject.Props.OUTER_JOIN, Boolean.TRUE);
        bqt1.putProperty(FakeMetadataObject.Props.JOIN, Boolean.TRUE);
        
        List values = new ArrayList();
        values.add(new Integer(0));

        ProcessorPlan plan = helpGetProcessorPlan(preparedSql, values, capFinder, metadata, new PreparedPlanCache(), SESSION_ID, false, false);
        
        TestOptimizer.checkNodeTypes(plan, TestOptimizer.FULL_PUSHDOWN);  
    }
    
	private ProcessorPlan helpGetProcessorPlan(String preparedSql, List values, PreparedPlanCache prepPlanCache)
			throws MetaMatrixComponentException, QueryParserException,
			QueryResolverException, QueryValidatorException,
			QueryPlannerException {    	
		return helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), FakeMetadataFactory.example1Cached(), prepPlanCache, SESSION_ID, false, false);
    }
	
	private ProcessorPlan helpGetProcessorPlan(String preparedSql, List values,
			PreparedPlanCache prepPlanCache, String conn)
			throws MetaMatrixComponentException, QueryParserException,
			QueryResolverException, QueryValidatorException,
			QueryPlannerException {
		return helpGetProcessorPlan(preparedSql, values,
				new DefaultCapabilitiesFinder(), FakeMetadataFactory
						.example1Cached(), prepPlanCache, conn, false, false);
	}

	static ProcessorPlan helpGetProcessorPlan(String preparedSql, List values,
			CapabilitiesFinder capFinder, QueryMetadataInterface metadata, PreparedPlanCache prepPlanCache, String conn, boolean callableStatement, boolean limitResults)
			throws MetaMatrixComponentException, QueryParserException,
			QueryResolverException, QueryValidatorException,
			QueryPlannerException {
        
        //Create Request
        RequestMessage request = new RequestMessage();
        request.setCommand(preparedSql);
        request.setPreparedStatement(true);
        request.setCallableStatement(callableStatement);
        request.setParameterValues(values);
        if (limitResults) {
        	request.setRowLimit(1);
        }

        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName("example1");
        workContext.setVdbVersion("1");
        workContext.setSessionId(new MetaMatrixSessionID(conn));
        
        PreparedStatementRequest serverRequest = new PreparedStatementRequest(prepPlanCache) {
        	@Override
        	protected void createProcessor()
        			throws MetaMatrixComponentException {
        		//don't bother
        	}
        };
        FakeApplicationEnvironment env = new FakeApplicationEnvironment(metadata, "example1", "1", "pm1", "1", "BINDING");
        serverRequest.initialize(request, env, BufferManagerFactory.getStandaloneBufferManager(), null, new HashMap(), null, DEBUG, null, workContext);
        serverRequest.setMetadata(capFinder, metadata, null);
        serverRequest.processRequest();
        
        ProcessorPlan plan = serverRequest.processPlan;
        assertNotNull(plan);
		return plan;
	}
	
	public void testValidateCorrectValues() throws Exception {
        // Create query 
        String preparedSql = "SELECT pm1.g1.e1, e2, pm1.g1.e3 as a, e4 as b FROM pm1.g1 WHERE pm1.g1.e1=?"; //$NON-NLS-1$
        
		List values = new ArrayList();
		values.add("a"); //$NON-NLS-1$
		
        //Create plan
        helpGetProcessorPlan(preparedSql, values, new PreparedPlanCache());
	}	

	/** SELECT pm1.g1.e1 FROM pm1.g1 WHERE pm1.g1.e2 IN (SELECT pm1.g2.e2 FROM pm1.g2 WHERE pm1.g2.e1 = ?)*/
	public void testWithSubquery() throws Exception {
		// Create query 
		String preparedSql = "SELECT pm1.g1.e1 FROM pm1.g1 WHERE pm1.g1.e2 IN (SELECT pm1.g2.e2 FROM pm1.g2 WHERE pm1.g2.e1 = ?)"; //$NON-NLS-1$
        
		List values = new ArrayList();
		values.add("a"); //$NON-NLS-1$
		
        //Create plan
        helpGetProcessorPlan(preparedSql, values, new PreparedPlanCache());
	}	

	/** SELECT pm1.g1.e1 FROM pm1.g1 WHERE pm1.g1.e1 = ? AND pm1.g1.e2 IN (SELECT pm1.g2.e2 FROM pm1.g2 WHERE pm1.g2.e1 = ?) */
	public void testWithSubquery2() throws Exception {
		// Create query 
		String preparedSql = "SELECT pm1.g1.e1 FROM pm1.g1 WHERE pm1.g1.e1 = ? AND pm1.g1.e2 IN (SELECT pm1.g2.e2 FROM pm1.g2 WHERE pm1.g2.e1 = ?)"; //$NON-NLS-1$
                
		List values = new ArrayList();
		values.add("d"); //$NON-NLS-1$
		values.add("c"); //$NON-NLS-1$
				
        //Create plan
        helpGetProcessorPlan(preparedSql, values, new PreparedPlanCache());
	}	

	/** SELECT X.e1 FROM (SELECT pm1.g2.e1 FROM pm1.g2 WHERE pm1.g2.e1 = ?) as X */
	public void testWithSubquery3() throws Exception {
		// Create query 
		String preparedSql = "SELECT X.e1 FROM (SELECT pm1.g2.e1 FROM pm1.g2 WHERE pm1.g2.e1 = ?) as X"; //$NON-NLS-1$
        
		//Create Request
		List values = new ArrayList();
		values.add("d"); //$NON-NLS-1$
		
        //Create plan
        helpGetProcessorPlan(preparedSql, values, new PreparedPlanCache());
	}	
	
	public void testValidateWrongValues() throws Exception {
		// Create query 
	    String preparedSql = "SELECT pm1.g1.e1, e2, pm1.g1.e3 as a, e4 as b FROM pm1.g1 WHERE pm1.g1.e2=?"; //$NON-NLS-1$
	    TestablePreparedPlanCache prepCache = new TestablePreparedPlanCache();

	    //wrong type
		try{         	        
			List values = new ArrayList();
			values.add("x"); //$NON-NLS-1$
			
	        //Create plan
	        helpGetProcessorPlan(preparedSql, values, prepCache, SESSION_ID);
	        fail();
		}catch(QueryResolverException qe){
            assertEquals("Error converting parameter number 1 with value \"x\" to expected type integer.", qe.getMessage()); //$NON-NLS-1$
    	}    	
    	assertEquals(0, prepCache.hitCount);
    	
    	//test cached plan
    	try{	        
			List values = new ArrayList();
			values.add("a"); //$NON-NLS-1$
			values.add("b"); //$NON-NLS-1$			
			helpGetProcessorPlan(preparedSql, values, prepCache, SESSION_ID);
			fail();
	   	}catch(QueryResolverException qe){
	   	    assertEquals("The number of bound values '2' does not match the number of parameters '1' in the prepared statement.", qe.getMessage()); //$NON-NLS-1$
    	}    	
    	
    	assertEquals(1, prepCache.hitCount);  
    	
    	//wrong number of values
		try{         
			List values = new ArrayList();
			values.add("a"); //$NON-NLS-1$
			values.add(new Integer(0));
			helpGetProcessorPlan(preparedSql, values, prepCache);
			fail();
		}catch(QueryResolverException qe){
			assertEquals("The number of bound values '2' does not match the number of parameters '1' in the prepared statement.", qe.getMessage()); //$NON-NLS-1$
    	}    	
    	
	}	
    
    public void testResolveParameterValues() throws Exception {
        // Create query 
        String preparedSql = "SELECT pm1.g1.e1, e2, pm1.g1.e3 as a, e4 as b FROM pm1.g1 WHERE pm1.g1.e2=?"; //$NON-NLS-1$
        
        List values = new ArrayList();
        //values.add("a");
        values.add("0"); //$NON-NLS-1$
        
		helpGetProcessorPlan(preparedSql, values, new PreparedPlanCache());
    }
    
    /**
     * TODO: there may be other ways of handling this situation in the future
     */
    public void testLimitNoCache() throws Exception {
        // Create query 
        String preparedSql = "SELECT pm1.g1.e1, e2, pm1.g1.e3 as a, e4 as b FROM pm1.g1 WHERE pm1.g1.e2=?"; //$NON-NLS-1$
        
        List values = new ArrayList();
        values.add("0"); //$NON-NLS-1$
        
        TestablePreparedPlanCache planCache = new TestablePreparedPlanCache();
        
		helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), FakeMetadataFactory.example1Cached(), planCache, SESSION_ID, false, true);

		helpGetProcessorPlan(preparedSql, values, new DefaultCapabilitiesFinder(), FakeMetadataFactory.example1Cached(), planCache, SESSION_ID, false, true);
		//make sure the plan wasn't reused
		assertEquals(0, planCache.hitCount);
    }

}
