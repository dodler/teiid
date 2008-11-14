/*
 * © 2007 Varsity Gateway LLC. All Rights Reserved.
 */

package com.metamatrix.connector.xml.base;

import java.util.List;

import junit.framework.TestCase;

import com.metamatrix.data.api.ConnectorCapabilities;

/**
 * created by JChoate on Jun 27, 2005
 *
 */
public class TestXMLCapabilities extends TestCase {

	
	private XMLCapabilities m_caps = null;
    /**
     * Constructor for XMLCapabilitiesTest.
     * @param arg0
     */
    public TestXMLCapabilities(String arg0) {
        super(arg0);
    }

    public void setUp() {
    	m_caps = new XMLCapabilities();
    	
    }
    
    public void testGetMaxInCriteriaSize() {
        assertEquals(Integer.MAX_VALUE, m_caps.getMaxInCriteriaSize());
    }
    
    public void testGetCapabilitiesScope() {
        assertEquals(ConnectorCapabilities.SCOPE.GLOBAL, m_caps.getCapabilitiesScope());
    }

    public void testSupportsExecutionMode() {
        assertTrue(m_caps.supportsExecutionMode(ConnectorCapabilities.EXECUTION_MODE.SYNCH_QUERY));
        assertFalse(m_caps.supportsExecutionMode(ConnectorCapabilities.EXECUTION_MODE.BATCHED_UPDATES));
        assertFalse(m_caps.supportsExecutionMode(ConnectorCapabilities.EXECUTION_MODE.BULK_INSERT));
        assertFalse(m_caps.supportsExecutionMode(ConnectorCapabilities.EXECUTION_MODE.PROCEDURE));
        assertFalse(m_caps.supportsExecutionMode(ConnectorCapabilities.EXECUTION_MODE.UPDATE));
    }

    public void testSupportsCriteria() {
        assertTrue(m_caps.supportsCriteria());
    }

    public void testSupportsCompareCriteria() {
    	assertTrue(m_caps.supportsCompareCriteria());
    }

    public void testSupportsCompareCriteriaEquals() {
    	assertTrue(m_caps.supportsCompareCriteriaEquals());
    }

    public void testSupportsInCriteria() {
    	assertTrue(m_caps.supportsInCriteria());
    }

    public void testSupportsAndCriteria() {
    	assertTrue(m_caps.supportsAndCriteria());
    }

    public void testXMLCapabilities() {
        XMLCapabilities caps = new XMLCapabilities();
        assertNotNull(caps);
    }

    /*
     * Class under test for List getSupportedFunctions()
     */
    public void testGetSupportedFunctions() {
       List funcs = m_caps.getSupportedFunctions();
    }

    public void testSupportsSelectDistinct() {
        assertFalse(m_caps.supportsSelectDistinct());
    }

    public void testSupportsSelectLiterals() {
    	assertFalse(m_caps.supportsSelectLiterals());
    }

    public void testSupportsAliasedGroup() {
        assertFalse(m_caps.supportsAliasedGroup());
    }

    public void testSupportsJoins() {
       assertFalse(m_caps.supportsJoins());
    }

    public void testSupportsSelfJoins() {
        assertFalse(m_caps.supportsSelfJoins());
    }

    public void testSupportsOuterJoins() {
        assertFalse(m_caps.supportsOuterJoins());
    }

    public void testSupportsFullOuterJoins() {
        assertFalse(m_caps.supportsFullOuterJoins());
    }

    public void testSupportsBetweenCriteria() {
       assertFalse(m_caps.supportsBetweenCriteria());
    }

    public void testSupportsCompareCriteriaNotEquals() {
        assertFalse(m_caps.supportsCompareCriteriaNotEquals());
    }

    public void testSupportsCompareCriteriaLessThan() {
      assertFalse(m_caps.supportsCompareCriteriaLessThan());
    }

    public void testSupportsCompareCriteriaLessThanOrEqual() {
        assertFalse(m_caps.supportsCompareCriteriaLessThanOrEqual());
    }

    public void testSupportsCompareCriteriaGreaterThan() {
        assertFalse(m_caps.supportsCompareCriteriaGreaterThan());
    }

    public void testSupportsCompareCriteriaGreaterThanOrEqual() {
        assertFalse(m_caps.supportsCompareCriteriaGreaterThanOrEqual());
    }

    public void testSupportsLikeCriteria() {
    	assertFalse(m_caps.supportsLikeCriteria());
    }

    public void testSupportsLikeCriteriaEscapeCharacter() {
        assertFalse(m_caps.supportsLikeCriteriaEscapeCharacter());
    }

    public void testSupportsInCriteriaSubquery() {
        assertFalse(m_caps.supportsInCriteriaSubquery());
    }

    public void testSupportsIsNullCriteria() {
        assertFalse(m_caps.supportsIsNullCriteria());
    }

    public void testSupportsOrCriteria() {
    	assertFalse(m_caps.supportsOrCriteria());
    }

    public void testSupportsNotCriteria() {
        assertFalse(m_caps.supportsNotCriteria());
    }

    public void testSupportsExistsCriteria() {
        assertFalse(m_caps.supportsExistsCriteria());
    }

    public void testSupportsQuantifiedCompareCriteria() {
        assertFalse(m_caps.supportsQuantifiedCompareCriteria());
    }

    public void testSupportsQuantifiedCompareCriteriaSome() {
    	assertFalse(m_caps.supportsQuantifiedCompareCriteriaSome());
    }

    public void testSupportsQuantifiedCompareCriteriaAll() {
    	assertFalse(m_caps.supportsQuantifiedCompareCriteriaAll());
    }

    public void testSupportsOrderBy() {
    	assertFalse(m_caps.supportsOrderBy());
    }

    public void testSupportsAggregates() {
    	assertFalse(m_caps.supportsAggregates());
    }

    public void testSupportsAggregatesSum() {
        assertFalse(m_caps.supportsAggregatesSum());
    }

    public void testSupportsAggregatesAvg() {
        assertFalse(m_caps.supportsAggregatesAvg());
    }

    public void testSupportsAggregatesMin() {
        assertFalse(m_caps.supportsAggregatesMin());
    }

    public void testSupportsAggregatesMax() {
        assertFalse(m_caps.supportsAggregatesMax());
    }

    public void testSupportsAggregatesCount() {
        assertFalse(m_caps.supportsAggregatesCount());
    }

    public void testSupportsAggregatesCountStar() {
        assertFalse(m_caps.supportsAggregatesCountStar());
    }

    public void testSupportsAggregatesDistinct() {
        assertFalse(m_caps.supportsAggregatesDistinct());
    }

    public void testSupportsScalarSubqueries() {
        assertFalse(m_caps.supportsScalarSubqueries());
    }

    public void testSupportsCorrelatedSubqueries() {
        assertFalse(m_caps.supportsCorrelatedSubqueries());
    }

    public void testSupportsCaseExpressions() {
        assertFalse(m_caps.supportsCaseExpressions());
    }

    public void testSupportsSearchedCaseExpressions() {
        assertFalse(m_caps.supportsSearchedCaseExpressions());
    }

    public void testSupportsScalarFunctions() {
        assertFalse(m_caps.supportsScalarFunctions());
    }

    /*
     * Class under test for java.util.List getSupportedFunctions()
     */

    public void testSupportsXATransactions() {
        assertFalse(m_caps.supportsXATransactions());
    }

    public void testSupportsInlineViews() {
        assertFalse(m_caps.supportsInlineViews());
    }

    public void testSupportsUnionOrderBy() {
        assertFalse(m_caps.supportsUnionOrderBy());
    }

    public void testSupportsUnions() {
        assertFalse(m_caps.supportsUnions());
    }

}
