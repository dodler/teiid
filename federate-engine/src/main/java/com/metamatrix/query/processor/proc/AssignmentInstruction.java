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

package com.metamatrix.query.processor.proc;

import java.util.Map;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.MetaMatrixProcessingException;
import com.metamatrix.common.log.LogManager;
import com.metamatrix.query.sql.util.VariableContext;
import com.metamatrix.query.util.LogConstants;

/**
 * <p> This instruction updates the current variable context with a value for the Variable
 * defined using a DeclareInstruction, the vriable value is obtained by either processing
 * a expression or a command(stored as a processplan). The Processing of the command is
 * expected to result in 1 column, 1 row tuple.</p>
 */
public class AssignmentInstruction extends AbstractAssignmentInstruction {
    	
	public AssignmentInstruction() {
	}

    public String toString() {
        return "ASSIGNMENT INSTRUCTION:"; //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.query.processor.program.ProgramInstruction#clone()
     */
    public Object clone() {
        AssignmentInstruction clone = new AssignmentInstruction();
        this.cloneState(clone);
        return clone;
    }

    /** 
     * @see com.metamatrix.query.processor.proc.AbstractAssignmentInstruction#processValue(java.lang.Object)
     */
    protected void processValue(Object value, VariableContext varContext) throws MetaMatrixComponentException,
                                             MetaMatrixProcessingException {
        varContext.setValue(getVariable(), value);
        LogManager.logTrace(LogConstants.CTX_QUERY_PLANNER,
                            new Object[] {this.toString() + " The variable " //$NON-NLS-1$
                                          + getVariable() + " in the variablecontext is updated with the value :", value}); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.query.processor.proc.AbstractAssignmentInstruction#getDescriptionProperties(java.util.Map)
     */
    protected void getDescriptionProperties(Map props) {
        props.put(PROP_TYPE, "ASSIGNMENT"); //$NON-NLS-1$
    }
       
}