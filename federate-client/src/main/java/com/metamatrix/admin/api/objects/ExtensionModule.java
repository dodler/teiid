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

package com.metamatrix.admin.api.objects;


/** 
 * An extension module is a library (usually in jar format) that extends
 * the MetaMatrix system in some way.  Classes of a custom connector can
 * be added as an extension module.
 * 
 * <p> The unique identifier pattern for the extension module is generally 
 * the name of the jar file since it applies system wide. Example: <code>MJjdbc.jar</code>
 * @since 4.3
 */
public interface ExtensionModule extends AdminObject {
    
    /**
     * The name of the JAR file type of extension
     * module - this is the only type of
     * extension module that can be searched
     * for Class objects
     */
    public static final String JAR_FILE_TYPE = "JAR File"; //$NON-NLS-1$

    /**
     * The name of the Metadata Keyword type of
     * extension module.
     */
    public static final String METADATA_KEYWORD_TYPE = "Metadata Keyword"; //$NON-NLS-1$

    /**
     * The name of the Metamodel Extension type of
     * extension module.
     */
    public static final String METAMODEL_EXTENSION_TYPE = "Metamodel Extension"; //$NON-NLS-1$

    /**
     * The name of the Function Definition type of
     * extension module.
     */
    public static final String FUNCTION_DEFINITION_TYPE = "Function Definition"; //$NON-NLS-1$
    
    /**
     * The name of the Configuration Model type of
     * extension module.
     */
    public static final String CONFIGURATION_MODEL_TYPE = "Configuration Model"; //$NON-NLS-1$
    
    /**
     * The name of the VDB File type of extension module.
     */
    public static final String VDB_FILE_TYPE = "VDB File"; //$NON-NLS-1$
    
    /**
     * The name of the Keystore File of extension module.
     */
    public static final String KEYSTORE_FILE_TYPE = "Keystore File"; //$NON-NLS-1$

    /**
     * The name of the Miscellaneous File type of extension module.
     */
    public static final String MISC_FILE_TYPE = "Miscellaneous Type"; //$NON-NLS-1$
    
    
    /**
     * @return description
     */
    public String getDescription();

    /**
     * @return byte array of file contents
     */
    public byte[] getFileContents();
    
    /**
     * @return String of the Module Type for this Extension Module
     */
    public String getModuleType();

 
    

}
