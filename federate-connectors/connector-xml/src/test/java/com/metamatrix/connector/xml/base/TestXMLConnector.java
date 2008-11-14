/*
 * © 2007 Varsity Gateway LLC. All Rights Reserved.
 */

package com.metamatrix.connector.xml.base;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import com.metamatrix.cdk.api.EnvironmentUtility;
import com.metamatrix.connector.xml.XMLConnectorState;
import com.metamatrix.connector.xml.file.FileConnectorState;
import com.metamatrix.data.api.ConnectorEnvironment;
import com.metamatrix.data.api.SecurityContext;
import com.metamatrix.data.exception.ConnectorException;


public class TestXMLConnector extends TestCase {
    
    private ConnectorEnvironment m_env;
    private SecurityContext m_secCtx;
    
	public TestXMLConnector() {
		super();
	}

	/**
	 * @param arg0
	 */
	public TestXMLConnector(String arg0) {
		super(arg0);
	}
    
    public void setUp() {        
        m_env = ProxyObjectFactory.getDefaultTestConnectorEnvironment();                
        
        m_secCtx = ProxyObjectFactory.getDefaultSecurityContext();        
    }
        
    public void testInitMethod() {
        //init test environment
        XMLConnector connector = new XMLConnector();          
        try {        
        	connector.initialize(m_env);
            assertNotNull("state is null", connector.getState());
            XMLConnectorState state = connector.getState();
            Properties testFileProps = ProxyObjectFactory.getDefaultFileProps();
            assertEquals(state.getMaxMemoryCacheSizeKB(), 
                    Integer.parseInt((String) testFileProps.get(XMLConnectorStateImpl.MAX_MEMORY_CACHE_SIZE)));
            assertEquals(state.getMaxFileCacheSizeKB(), 
                    Integer.parseInt((String) testFileProps.get(XMLConnectorStateImpl.MAX_FILE_CACHE_SIZE)));
            assertEquals(state.getCacheLocation(), (String) testFileProps.get(XMLConnectorStateImpl.FILE_CACHE_LOCATION));
            int expectedTimeout = Integer.parseInt((String) testFileProps.get(XMLConnectorStateImpl.CACHE_TIMEOUT));
            assertEquals(state.getCacheTimeoutSeconds(), expectedTimeout);
            assertNotNull("Logger is null", connector.getLogger());
            assertNotNull("cache is null", connector.getCache());
        } catch (ConnectorException ex) {
        	ex.printStackTrace();
            fail(ex.getMessage());         
        }
        
    }
    
    
    
    public void testStart() {
        XMLConnector connector = new XMLConnector();
        try {
         connector.initialize(m_env);
         connector.start();
        } catch (ConnectorException ex) {
         ex.printStackTrace();
         fail(ex.getMessage());
        }
    }
    
    public void testStop() {
     XMLConnector connector = new XMLConnector();
     
     try {
      connector.initialize(m_env);
      connector.start();
      
     } catch (ConnectorException ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
     }
     assertNotNull(connector);
     connector.stop();
    }
    
    
    public void testGetConnection() {
    	XMLConnector connector = new XMLConnector();
                        
        try {
         connector.initialize(m_env);
         connector.start();
         XMLConnectionImpl conn = (XMLConnectionImpl) connector.getConnection(m_secCtx);
         assertNotNull("XMLConnectionImpl is null", conn);
         
         // is the connector ref set?
         assertEquals(connector, conn.getConnector());
         
         //is the query id set?
         assertEquals(m_secCtx.getRequestIdentifier(), conn.getQueryId());
         
        } catch (ConnectorException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    } 
    
    
    public void testUnsetState() {
        XMLConnector connector = new XMLConnector();
        
        try {
            XMLConnectionImpl conn = (XMLConnectionImpl) connector.getConnection(m_secCtx);
            fail("connector created a connection with unset state");           
        } catch (ConnectorException e) {
            System.out.println("execption successfully thrown");
        }       
    }
    
    public void testInitializeFailure() {
    	XMLConnector connector = new XMLConnector();
    	try {
    		Properties testFileProps = new Properties(); 
    		testFileProps.put(XMLConnectorStateImpl.CACHE_TIMEOUT, new String("5000"));
            testFileProps.put(XMLConnectorStateImpl.MAX_MEMORY_CACHE_SIZE, new String("50"));
            testFileProps.put(XMLConnectorStateImpl.MAX_FILE_CACHE_SIZE, new String("50"));
            testFileProps.put(XMLConnectorStateImpl.CACHE_ENABLED, Boolean.TRUE);
            testFileProps.put(XMLConnectorStateImpl.FILE_CACHE_LOCATION, new String("./test/cache"));
            testFileProps.setProperty(XMLConnectorStateImpl.STATE_CLASS_PROP, "sure.to.Fail");
            
            testFileProps.put(FileConnectorState.FILE_NAME, "state_college.xml");
            String localPath = "test/documents";
            String ccPath = "checkout/XMLConnectorFramework/" + localPath;
            if (new File(localPath).exists()) {
            	testFileProps.put(FileConnectorState.DIRECTORY_PATH, localPath);
            } else {
            	if (new File(ccPath).exists()) {
            		testFileProps.put(FileConnectorState.DIRECTORY_PATH, ccPath);
            	} else {
            		testFileProps.put(FileConnectorState.DIRECTORY_PATH, "");
            	}
            }
    	    ConnectorEnvironment env = EnvironmentUtility.createEnvironment(testFileProps);
    		connector.initialize(env);
    		fail("connector should have failed on get state");
    	} catch (ConnectorException e) {
    		assertTrue(true);
    	}
    }
    
    public void testLoggingInit() {
        XMLConnector connector = new XMLConnector();
        
        try {
            connector.initialize(m_env);
            connector.start();
            assertNotNull(connector.getLogger());
            connector.getLogger().logInfo("Logger is properly initialized");
        } catch (ConnectorException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());            
        }
        
    }
    
    public void testCacheInit() {

        XMLConnector connector = new XMLConnector();
        
        try {
            connector.initialize(m_env);
            connector.start();
            assertNotNull("the cache is null", connector.getCache());
            connector.getLogger().logInfo("cache is properly initialized");
        } catch (ConnectorException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());            
        }                   
    }
}
