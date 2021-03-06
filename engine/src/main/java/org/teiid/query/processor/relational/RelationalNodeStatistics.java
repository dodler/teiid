/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
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

package org.teiid.query.processor.relational;

import java.util.ArrayList;
import java.util.List;


/** 
 * @since 4.2
 */
public class RelationalNodeStatistics {

    //  Statistics
    static final int BATCHCOMPLETE_STOP = 0;
    static final int BLOCKEDEXCEPTION_STOP = 1;
    
    private boolean setNodeStartTime;
    
    // The total amount of rows output by this node
    private long nodeOutputRows;
    
    // Start and End system time for the node
    private long nodeStartTime;
    private long nodeEndTime;
    
    // Start and End system time for each batch
    private long batchStartTime;
    private long batchEndTime;
     
    // The processing time for the node (does not include child processing)
    private long nodeNextBatchProcessingTime;
    
    // the total processing time for the node (indcludes child processing (lastEndTime - firstStartTime)
    private long nodeCumulativeProcessingTime;
    
    // The total processing time of the nextBatch method for this node (includes child processing)
    private long nodeCumulativeNextBatchProcessingTime;
    
    // The total amount of calls to next batch in this node
    private int nodeNextBatchCalls;
    
    // The amount of times a Block or Componenet Exception occurs for this node
    private int nodeBlocks;
    
    public RelationalNodeStatistics() {
        this.setNodeStartTime = false;
    }
    
    public void startBatchTimer() {
        this.batchStartTime = System.currentTimeMillis();
    }
    
    void setBatchStartTime(long batchStartTime) {
		this.batchStartTime = batchStartTime;
	}
    
    public void stopBatchTimer() {
        this.batchEndTime = System.currentTimeMillis();
    }
    
    void setBatchEndTime(long batchEndTime) {
		this.batchEndTime = batchEndTime;
	}
    
    public void collectCumulativeNodeStats(Long rowCount, int stopType) {
        this.nodeNextBatchCalls++;
        if(!this.setNodeStartTime) {
            this.nodeStartTime = this.batchStartTime;
            this.setNodeStartTime = true;
        }
        this.nodeCumulativeNextBatchProcessingTime += (this.batchEndTime - this.batchStartTime);
        switch (stopType) {
            case BATCHCOMPLETE_STOP:
                this.nodeOutputRows += rowCount;
                break;
            case BLOCKEDEXCEPTION_STOP:
                this.nodeBlocks++;
                break;
        }
    }
    
    public void collectNodeStats(RelationalNode[] relationalNodes) {
        // set nodeEndTime to the time gathered at the end of the last batch
        this.nodeEndTime = this.batchEndTime;
        this.nodeCumulativeProcessingTime = this.nodeEndTime - this.nodeStartTime;
        this.nodeNextBatchProcessingTime = this.nodeCumulativeNextBatchProcessingTime;
        for (int i = 0; i < relationalNodes.length; i++) {
        	if (relationalNodes[i] == null) {
        		break;
        	}
            this.nodeNextBatchProcessingTime -= relationalNodes[i].getNodeStatistics().getNodeCumulativeNextBatchProcessingTime();                      
        }
    }
    
    public List<String> getStatisticsList() {
    	ArrayList<String> statisticsList = new ArrayList<String>(6);
    	statisticsList.add("Node Output Rows: " + this.nodeOutputRows); //$NON-NLS-1$
        statisticsList.add("Node Next Batch Process Time: " + this.nodeNextBatchProcessingTime); //$NON-NLS-1$
        statisticsList.add("Node Cumulative Next Batch Process Time: " + this.nodeCumulativeNextBatchProcessingTime); //$NON-NLS-1$
        statisticsList.add("Node Cumulative Process Time: " + this.nodeCumulativeProcessingTime); //$NON-NLS-1$
        statisticsList.add("Node Next Batch Calls: " + this.nodeNextBatchCalls); //$NON-NLS-1$
        statisticsList.add("Node Blocks: " + this.nodeBlocks); //$NON-NLS-1$
        return statisticsList;
    }
    
    /** 
     * @return Returns the nodeBlocks.
     * @since 4.2
     */
    public int getNodeBlocks() {
        return this.nodeBlocks;
    }
    /** 
     * @return Returns the nodeCumulativeNextBatchProcessingTime.
     * @since 4.2
     */
    public long getNodeCumulativeNextBatchProcessingTime() {
        return this.nodeCumulativeNextBatchProcessingTime;
    }
    /** 
     * @return Returns the nodeCumulativeProcessingTime.
     * @since 4.2
     */
    public long getNodeCumulativeProcessingTime() {
        return this.nodeCumulativeProcessingTime;
    }
    /** 
     * @return Returns the nodeEndTime.
     * @since 4.2
     */
    public long getNodeEndTime() {
        return this.nodeEndTime;
    }
    /** 
     * @return Returns the nodeNextBatchCalls.
     * @since 4.2
     */
    public int getNodeNextBatchCalls() {
        return this.nodeNextBatchCalls;
    }
    /** 
     * @return Returns the nodeOutputRows.
     * @since 4.2
     */
    public Long getNodeOutputRows() {
        return this.nodeOutputRows;
    }
    /** 
     * @return Returns the nodeProcessingTime.
     * @since 4.2
     */
    public long getNodeNextBatchProcessingTime() {
        return this.nodeNextBatchProcessingTime;
    }
    /** 
     * @return Returns the nodeStartTime.
     * @since 4.2
     */
    public long getNodeStartTime() {
        return this.nodeStartTime;
    }
    /** 
     * @return Returns the batchEndTime.
     * @since 4.2
     */
    public long getBatchEndTime() {
        return this.batchEndTime;
    }
    /** 
     * @return Returns the batchStartTime.
     * @since 4.2
     */
    public long getBatchStartTime() {
        return this.batchStartTime;
    }
}
