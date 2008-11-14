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

package com.metamatrix.toolbox.ui.widget;

import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.metamatrix.common.util.ApplicationInfo;
import com.metamatrix.toolbox.ui.widget.util.BrowserControl;
import com.metamatrix.toolbox.ToolboxPlugin;

/**
 * @since 2.0
 */
public class AboutPanel extends DialogPanel {
    //############################################################################################################################
    //# Constructors                                                                                                             #
    //############################################################################################################################
    
    /**
     * @since 2.0
     */
    public AboutPanel(final String licenseId, final String licenseVersion) {
        initializeAboutPanel(licenseId, licenseVersion);
    }
    
    //############################################################################################################################
    //# Methods                                                                                                                  #
    //############################################################################################################################
    
    /**
     * @since 2.0
     */
    protected ButtonWidget createCancelButton() {
        return null;
    }
    
    /**
     * @since 2.0
     */
    protected void initializeAboutPanel(final String licenseId, final String licenseVersion) {
        final ApplicationInfo info = ApplicationInfo.getInstance();
        final String url = info.getMainComponent().getAboutURL();
        
        final SplashPanel panel = new SplashPanel(licenseId, licenseVersion);
        final JLabel label = new JLabel("<html><a href='" + url + "'>" + url + "</a></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        label.addMouseListener(new MouseAdapter() {
        	Cursor prevCursor = null;
            public void mouseClicked(final MouseEvent event) {
                BrowserControl.displayURL(url);
            }
            public void mouseEntered(MouseEvent theEvent) {
            	prevCursor = AboutPanel.this.getCursor();
            	AboutPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent theEvent) {
            	AboutPanel.this.setCursor(prevCursor);
            }
        });
        JPanel pnl = new JPanel();
        pnl.setOpaque(false);
        pnl.add(label);
        panel.add(pnl);
        setContent(panel);
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final JTextArea box = new JTextArea(info.toString());
                box.setLineWrap(false);
                box.setEditable(false);
                final DialogPanel panel = new DialogPanel(new JScrollPane(box)) {
                    protected ButtonWidget createCancelButton() {
                        return null;
                    }
                };
                panel.addNavigationSpacer(SpacerWidget.createHorizontalExpandableSpacer());
                DialogWindow.show(AboutPanel.this, ToolboxPlugin.Util.getString("AboutPanel.Application_Build_Details_5"), panel); //$NON-NLS-1$
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.ALT_MASK), WHEN_IN_FOCUSED_WINDOW);
    }
}
