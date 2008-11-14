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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.query.QueryParserException;
import com.metamatrix.api.exception.query.QueryPlannerException;
import com.metamatrix.api.exception.query.QueryResolverException;
import com.metamatrix.api.exception.query.QueryValidatorException;
import com.metamatrix.common.application.ApplicationEnvironment;
import com.metamatrix.common.application.ApplicationService;
import com.metamatrix.common.buffer.FakeBufferManager;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.dqp.message.RequestMessage;
import com.metamatrix.dqp.service.AutoGenDataService;
import com.metamatrix.dqp.service.DQPServiceNames;
import com.metamatrix.dqp.service.FakeAbstractService;
import com.metamatrix.dqp.service.FakeAuthorizationService;
import com.metamatrix.dqp.service.FakeVDBService;
import com.metamatrix.dqp.service.MetadataService;
import com.metamatrix.platform.security.api.MetaMatrixSessionID;
import com.metamatrix.query.analysis.AnalysisRecord;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.processor.FakeDataManager;
import com.metamatrix.query.resolver.QueryResolver;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.unittest.FakeMetadataFacade;
import com.metamatrix.query.unittest.FakeMetadataFactory;
import com.metamatrix.query.unittest.FakeMetadataObject;
import com.metamatrix.query.util.ContextProperties;


/** 
 * @since 4.2
 */
public class TestRequest extends TestCase {

    private final static String QUERY = "SELECT * FROM pm1.g1";  //$NON-NLS-1$
    private final static String VDB = "VDB";  //$NON-NLS-1$
    private final static String VDB_VERSION = "1";  //$NON-NLS-1$
    private final static String MODEL = "pm1";  //$NON-NLS-1$
    private final static String BINDING_ID = "1";  //$NON-NLS-1$
    private final static String BINDING_NAME = "BINDING";  //$NON-NLS-1$
    
    /**
     * Constructor for TestRequest.
     * @param name
     */
    public TestRequest(String name) {
        super(name);
    }
    
    public void testGetSchemasForValidation() throws Exception {
        FakeMetadataFacade metadata = FakeMetadataFactory.example1();
        FakeMetadataObject doc1 = metadata.getStore().findObject("xmltest.doc1", FakeMetadataObject.GROUP);
        List<String> schemas = Arrays.asList("a.xsd", "b.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
        doc1.putProperty(FakeMetadataObject.Props.XML_SCHEMAS, schemas);
        RequestMessage message = new RequestMessage();
        message.setValidationMode(true);
        message.setCommandStr("select * from xmltest.doc1"); //$NON-NLS-1$
        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName(VDB); 
        workContext.setVdbVersion(VDB_VERSION); 
        workContext.setSessionId(new MetaMatrixSessionID(5));
        FakeApplicationEnvironment environment = 
            new FakeApplicationEnvironment(metadata, VDB, VDB_VERSION, MODEL, BINDING_ID, BINDING_NAME);        
        Request request = helpProcessMessage(environment, message, null, workContext);
        assertEquals(schemas, request.schemas);
    }


    /**
     * Test Request.validateEntitlement().  
     * Make sure that this can be called both before and after metadata is initialized. 
     * See defect 17209.
     * @throws Exception
     * @since 4.2
     */
    public void testValidateEntitlement() throws Exception {
        QueryMetadataInterface metadata = FakeMetadataFactory.example1Cached();
        
        
        Request request = new Request();
        Command command = QueryParser.getQueryParser().parseCommand(QUERY);
        QueryResolver.resolveCommand(command, Collections.EMPTY_MAP, true, metadata, AnalysisRecord.createNonRecordingRecord());
        
        RequestMessage message = new RequestMessage();
        message.setCommand(command);
        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName(VDB); 
        workContext.setVdbVersion(VDB_VERSION); 
        workContext.setSessionId(new MetaMatrixSessionID(5));
        FakeApplicationEnvironment environment = 
            new FakeApplicationEnvironment(metadata, VDB, VDB_VERSION, MODEL, BINDING_ID, BINDING_NAME);        
        
        request.initialize(message, environment, null, null, null, null, false, null, workContext);
        request.initMetadata();
        request.validateEntitlement(command);
    }
    
    
    /**
     * Test Request.processRequest().
     * Test processing the same query twice, and make sure that doesn't cause problems.  
     * See defect 17209.
     * @throws Exception
     * @since 4.2
     */
    public void testProcessRequest() throws Exception {
        QueryMetadataInterface metadata = FakeMetadataFactory.example1Cached();
        
        FakeApplicationEnvironment environment = 
            new FakeApplicationEnvironment(metadata, VDB, VDB_VERSION, MODEL, BINDING_ID, BINDING_NAME);        

        
        //Try before plan is cached.
        //If this doesn't throw an exception, assume it was successful.
        RequestMessage message = new RequestMessage(QUERY);
        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName(VDB); 
        workContext.setVdbVersion(VDB_VERSION); 
        workContext.setSessionId(new MetaMatrixSessionID(5));

        helpProcessMessage(environment, message, null, workContext);
        
        //Try again, now that plan is already cached.
        //If this doesn't throw an exception, assume it was successful.        
        message = new RequestMessage(QUERY);
        helpProcessMessage(environment, message, null, workContext);
    }
    
    public void testCommandContext() throws Exception {
        QueryMetadataInterface metadata = FakeMetadataFactory.example1Cached();
        
        FakeApplicationEnvironment environment = 
            new FakeApplicationEnvironment(metadata, VDB, VDB_VERSION, MODEL, BINDING_ID, BINDING_NAME);        

        
        //Try before plan is cached.
        //If this doesn't throw an exception, assume it was successful.
        RequestMessage message = new RequestMessage(QUERY);
        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName(VDB); 
        workContext.setVdbVersion(VDB_VERSION); 
        workContext.setSessionId(new MetaMatrixSessionID(5));
        Request request = helpProcessMessage(environment, message, null, workContext);
        assertEquals("5", request.context.getEnvironmentProperties().get(ContextProperties.SESSION_ID));
    }

    private Request helpProcessMessage(FakeApplicationEnvironment environment,
                                    RequestMessage message, PreparedPlanCache cache, DQPWorkContext workContext) throws QueryValidatorException,
                                                           QueryParserException,
                                                           QueryResolverException,
                                                           MetaMatrixComponentException,
                                                           QueryPlannerException {
        Request request = null;
        if (cache != null) {
        	request = new PreparedStatementRequest(cache);
        } else {
        	request = new Request();
        }
        request.initialize(message, environment, new FakeBufferManager(),
				new FakeDataManager(), new HashMap(), null, false, null,
				workContext);
        
        request.processRequest();
        return request;
    }
    
    

    
    /**
     * Test PreparedStatementRequest.processRequest().  
     * Test processing the same query twice, and make sure that doesn't cause problems.  
     * @throws Exception
     * @since 4.2
     */
    public void testProcessRequestPreparedStatement() throws Exception {
        QueryMetadataInterface metadata = FakeMetadataFactory.example1Cached();
        PreparedPlanCache cache = new PreparedPlanCache();
        FakeApplicationEnvironment environment = 
            new FakeApplicationEnvironment(metadata, VDB, VDB_VERSION, MODEL, BINDING_ID, BINDING_NAME);        
        

        //Try before plan is cached.
        //If this doesn't throw an exception, assume it was successful.
        RequestMessage message = new RequestMessage(QUERY);
        DQPWorkContext workContext = new DQPWorkContext();
        workContext.setVdbName(VDB); 
        workContext.setVdbVersion(VDB_VERSION); 
        workContext.setSessionId(new MetaMatrixSessionID(5));
        message.setPreparedStatement(true);
        message.setParameterValues(new ArrayList());
        
        helpProcessMessage(environment, message, cache, workContext);
        
        //Try again, now that plan is already cached.
        //If this doesn't throw an exception, assume it was successful.
        message = new RequestMessage(QUERY);
        message.setPreparedStatement(true);
        message.setParameterValues(new ArrayList());

        helpProcessMessage(environment, message, cache, workContext);
    }
    
    
    /**Fake ApplicationEnvironment that always returns the same metadata*/
    public static final class FakeApplicationEnvironment implements ApplicationEnvironment {
        private QueryMetadataInterface metadata;
        
        private FakeVDBService fakeVDBService;
        
        public FakeApplicationEnvironment(QueryMetadataInterface metadata, String vdbname, String version, String model, 
                                          String bindingID, String bindingName) {
            this.metadata = metadata; 
            
            fakeVDBService = new FakeVDBService();
            fakeVDBService.addModel(vdbname, version, model, ModelInfo.PUBLIC, false);
            fakeVDBService.addBinding(vdbname, version, model, bindingID, bindingName);
        }
        
        
        public Properties getApplicationProperties() {
            return null;
        }

        public void bindService(String type, ApplicationService service) {
        }

        public void unbindService(String type) {
        }

        public ApplicationService findService(String type) {
            if (type == DQPServiceNames.METADATA_SERVICE) {
                return new FakeMetadataService(metadata);
            } else if (type == DQPServiceNames.VDB_SERVICE) {
                return fakeVDBService;
            } else if (type == DQPServiceNames.DATA_SERVICE) {
                return new AutoGenDataService();
            } else if (type == DQPServiceNames.AUTHORIZATION_SERVICE) {
                return new FakeAuthorizationService(true);
            }
            
            return null;
        }
    }
    
    /**Fake MetadataService that always returns the same metadata*/
    public static final class FakeMetadataService extends FakeAbstractService implements MetadataService {
        private QueryMetadataInterface metadata;
        

        public FakeMetadataService(QueryMetadataInterface metadata) {
            this.metadata = metadata;            
        }

        public synchronized void addVdb() {
        }

        public synchronized QueryMetadataInterface lookupMetadata(String vdbName, String vdbVersion) {
            return metadata;
        }
    }
    
}
