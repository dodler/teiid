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

package com.metamatrix.admin.objects;

import java.io.Serializable;

import com.metamatrix.admin.AdminPlugin;
import com.metamatrix.admin.api.objects.AdminStatus;


/** 
 * Simple Implementation of MMStatus. 
 * @since 4.3
 */
public class MMAdminStatus implements AdminStatus, Serializable {

    private int code = AdminStatus.CODE_UNKNOWN;
    private String message = null;
    
    
    /**
     * Construct a new MMAdminStatus 
     * @param code
     * @param messageKey Key of the status message in i18n.properties.
     * @since 4.3
     */
    public MMAdminStatus(int code, String messageKey) {
        this.code = code;
        
        this.message = AdminPlugin.Util.getString(messageKey);
    }
    
    /**
     * Construct a new MMAdminStatus 
     * @param code
     * @param messageKey Key of the status message in i18n.properties.
     * @param value Value to substitute into the internationalized message.
     * @since 4.3
     */
    public MMAdminStatus(int code, String messageKey, Object value) {
        this.code = code;
        
        this.message = AdminPlugin.Util.getString(messageKey, value);
    }

    /**
     * Construct a new MMAdminStatus 
     * @param code
     * @param messageKey Key of the status message in i18n.properties.
     * @param values Values to substitute into the internationalized message.
     * @since 4.3
     */
    public MMAdminStatus(int code, String messageKey, Object[] values) {
        this.code = code;
        
        this.message = AdminPlugin.Util.getString(messageKey, values);
    }

    
    
    /** 
     * @see com.metamatrix.admin.api.objects.AdminStatus#getCode()
     * @since 4.3
     */
    public int getCode() {
        return code;
    }

    /** 
     * @see com.metamatrix.admin.api.objects.AdminStatus#getMessage()
     * @since 4.3
     */
    public String getMessage() {
        return message;
    }
    
    
    /**
     * @see java.lang.Object#toString()
     * @since 4.3
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(AdminPlugin.Util.getString("MMAdminStatus.MMAdminStatus"));  //$NON-NLS-1$
        result.append(AdminPlugin.Util.getString("MMAdminStatus.Code")).append(getCode()); //$NON-NLS-1$
        result.append(AdminPlugin.Util.getString("MMAdminStatus.Message")).append(getMessage()); //$NON-NLS-1$
        return result.toString();
    }

}
