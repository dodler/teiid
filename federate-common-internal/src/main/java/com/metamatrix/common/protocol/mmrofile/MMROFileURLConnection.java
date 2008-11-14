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

package com.metamatrix.common.protocol.mmrofile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.metamatrix.common.protocol.mmfile.MMFileURLConnection;

/** 
 * Metamatrix's own implementation of the "file:" URL handler. The purpose this
 * handler is to behave the same way as the "mmfile" but, ignore any saves to
 * the permenent stores.
 *  
 * Strings are not externalized because of the fact that we have huge dependencies 
 * with our plugin stuff to eclipse.
 *  
 * @since 5.0
 */
public class MMROFileURLConnection extends MMFileURLConnection {

    public static String PROTOCOL = "mmrofile"; //$NON-NLS-1$

    /**
     * ctor 
     * @param u - URL to open the connection to 
     */
    public MMROFileURLConnection(URL u) throws MalformedURLException, IOException {
        super(u,true);
    }
}