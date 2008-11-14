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

package com.metamatrix.common.application.basic;

import java.util.*;

import com.metamatrix.common.application.ApplicationEnvironment;
import com.metamatrix.common.application.ApplicationService;

/**
 */
public class BasicEnvironment implements ApplicationEnvironment {

    private Properties props;
    private Map services = new HashMap();

    public void setApplicationProperties(Properties props) {
        this.props = props;
    }

    /* 
     * @see com.metamatrix.common.application.ApplicationEnvironment#getApplicationProperties()
     */
    public Properties getApplicationProperties() {
        if(this.props == null) { 
            return new Properties();
        }
        return this.props;
    }

    /* 
     * @see com.metamatrix.common.application.ApplicationEnvironment#bindService(java.lang.String, com.metamatrix.common.application.ApplicationService)
     */
    public void bindService(String type, ApplicationService service) {
        this.services.put(type, service);
    }

    /* 
     * @see com.metamatrix.common.application.ApplicationEnvironment#unbindService(java.lang.String)
     */
    public void unbindService(String type) {
        this.services.remove(type);
    }

    /* 
     * @see com.metamatrix.common.application.ApplicationEnvironment#findService(java.lang.String)
     */
    public ApplicationService findService(String type) {
        return (ApplicationService) this.services.get(type);
    }

}
