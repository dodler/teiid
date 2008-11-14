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

package com.metamatrix.connector.jdbc.mysql;

import java.util.Arrays;
import java.util.List;

import com.metamatrix.connector.jdbc.extension.impl.BasicFunctionModifier;
import com.metamatrix.connector.jdbc.extension.impl.DropFunctionModifier;
import com.metamatrix.data.language.IExpression;
import com.metamatrix.data.language.IFunction;
import com.metamatrix.data.language.ILanguageFactory;
import com.metamatrix.data.language.ILiteral;


/** 
 * @since 4.3
 */
class MySQLConvertModifier extends BasicFunctionModifier {
    
    private static DropFunctionModifier DROP_MODIFIER = new DropFunctionModifier();
    
    private ILanguageFactory langFactory;
    
    MySQLConvertModifier(ILanguageFactory langFactory) {
        this.langFactory = langFactory;
    }

    public List translate(IFunction function) {
        return null;
    }

    public IExpression modify(IFunction function) {
        IExpression[] args = function.getParameters();

        if (args[0] != null && args[0] instanceof ILiteral && ((ILiteral)args[0]).getValue() == null ) {
            if (args[1] != null && args[1] instanceof ILiteral) {
                // This is a convert(null, ...) or cast(null as ...)
                return DROP_MODIFIER.modify(function);
            }
        } 
        
        if (args[1] != null && args[1] instanceof ILiteral) {
            String target = ((String)((ILiteral)args[1]).getValue()).toLowerCase();
            if (target.equals("string")) {  //$NON-NLS-1$ 
                return convertToString(function);
            } else if (target.equals("byte") || //$NON-NLS-1$
                       target.equals("short") || //$NON-NLS-1$
                       target.equals("integer")) {  //$NON-NLS-1$ 
                return convertToNativeType(function, "SIGNED INTEGER"); //$NON-NLS-1$
            } else if (target.equals("long") || //$NON-NLS-1$
                       target.equals("biginteger")) { //$NON-NLS-1$ 
                return convertToNativeType(function, "SIGNED"); //$NON-NLS-1$
            } else if (target.equals("float") || //$NON-NLS-1$
                       target.equals("double") || //$NON-NLS-1$
                       target.equals("bigdecimal")) { //$NON-NLS-1$ 
                return convertToNumeric(function); 
            } else if (target.equals("date")) { //$NON-NLS-1$ 
                return convertToDateTime("DATE", args[0], java.sql.Date.class); //$NON-NLS-1$
            } else if (target.equals("time")) { //$NON-NLS-1$ 
                return convertToDateTime("TIME", args[0], java.sql.Time.class); //$NON-NLS-1$
            } else if (target.equals("timestamp")) { //$NON-NLS-1$ 
                return convertToDateTime("TIMESTAMP", args[0], java.sql.Timestamp.class); //$NON-NLS-1$
            } else if (target.equals("char")) { //$NON-NLS-1$ 
                return convertToNativeType(function, "CHAR (1)"); //$NON-NLS-1$
            } else if (target.equals("boolean")) {  //$NON-NLS-1$ 
                return convertToBoolean(function);
            }
        }
        return DROP_MODIFIER.modify(function); 
    }
    
    private IExpression convertToString(IFunction function) {
        int srcCode = getSrcCode(function);
        switch(srcCode) {
            case BOOLEAN:
                // convert(booleanSrc, string) --> CASE booleanSrc WHEN 1 THEN '1' ELSE '0' END
                List when = Arrays.asList(new IExpression[] {langFactory.createLiteral(new Integer(1), Integer.class)});
                List then = Arrays.asList(new IExpression[] {langFactory.createLiteral("1", String.class)}); //$NON-NLS-1$
                IExpression elseExpr = langFactory.createLiteral("0", String.class); //$NON-NLS-1$
                return langFactory.createCaseExpression(function.getParameters()[0], when, then, elseExpr, String.class);
            case BYTE:
            case SHORT:
            case INTEGER:
            case LONG:
            case BIGINTEGER:
            case FLOAT:
            case DOUBLE:
            case BIGDECIMAL:
                // convert(src, string) --> convert(src, CHAR)
                return convertToNativeType(function, "CHAR"); //$NON-NLS-1$
            case DATE:
                // convert (dateSrc, string) --> date_format(dateSrc, '%Y-%m-%d')
                return convertDateTimeToString(function, "%Y-%m-%d"); //$NON-NLS-1$
            case TIME:
                // convert (timeSrc, string) --> date_format(timeSrc, '%H:%i:%S')
                return convertDateTimeToString(function, "%H:%i:%S"); //$NON-NLS-1$
            case TIMESTAMP:    
                // convert (tsSrc, string) --> date_format(tsSrc, '%Y-%m-%d %H:%i:%S.%f')
                return convertDateTimeToString(function, "%Y-%m-%d %H:%i:%S.%f"); //$NON-NLS-1$
            default:
                return DROP_MODIFIER.modify(function);
        }
    }
    
    private IExpression convertToNativeType(IFunction function, String targetType) {
        IExpression[] args = function.getParameters();
        function.setName("convert"); //$NON-NLS-1$
        args[1] = langFactory.createLiteral(targetType, String.class);
        function.setParameters(args);
        return function;
    }
    
    /**
     * In version 5.1 and after, we can simple use convert(x, DECIMAL), but for backward compatibility we must do (x + 0.0)
     * @param function
     * @return
     * @since 4.3
     */
    private IExpression convertToNumeric(IFunction function) {
        // convert(x, float/double/bigdecimal) --> (x + 0.0)
        return langFactory.createFunction("+", //$NON-NLS-1$
                                          new IExpression[] {function.getParameters()[0],
                                                             langFactory.createLiteral(new Double(0.0), Double.class)},
                                          Double.class);
    }
    
    private IExpression convertToDateTime(String functionName, IExpression value, Class targetType) {
        return langFactory.createFunction(functionName,
                                           new IExpression[] {value},
                                           targetType);
    }
    
    private IExpression convertToBoolean(IFunction function) {
        int srcCode = getSrcCode(function);
        switch(srcCode) {
            case STRING:
                // convert(src, boolean) --> CASE src WHEN 'true' THEN 1 ELSE 0 END
                List when = Arrays.asList(new IExpression[] {langFactory.createLiteral("true", String.class)}); //$NON-NLS-1$
                List then = Arrays.asList(new IExpression[] {langFactory.createLiteral(new Integer(1), Integer.class)});
                IExpression elseExpr = langFactory.createLiteral(new Integer(0), Integer.class);
                return langFactory.createCaseExpression(function.getParameters()[0], when, then, elseExpr, Integer.class); 
            default:
                return DROP_MODIFIER.modify(function);
        }
    }
    
    private IFunction convertDateTimeToString(IFunction function, String format) {
        // convert (date, string) --> date_format(date, format)
        IExpression[] args = function.getParameters();
        function.setName("date_format"); //$NON-NLS-1$
        args[1] = langFactory.createLiteral(format, String.class); 
        function.setParameters(args);
        return function;
    }
    
    private int getSrcCode(IFunction function) {
        IExpression[] args = function.getParameters();
        Class srcType = args[0].getType();
        return ((Integer) typeMap.get(srcType)).intValue();
    }         
}
