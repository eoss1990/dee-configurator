package com.seeyon.dee.configurator.camel;

import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.Parameters;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * flow context.
 *
 * @author zhangfb
 */
public class FlowContext implements Serializable {
    /**
     * item list
     */
    private List<FlowItem> items;

    /**
     * executed item list
     */
    private List<FlowItem> executedItems;

    /**
     * context document
     */
    private Document document;

    /**
     * context parameters
     */
    private Parameters parameters;

    public FlowContext() {
        // when initializing, items and executed items should be new java.lang.Object.
        items = new ArrayList<FlowItem>();
        executedItems = new ArrayList<FlowItem>();
    }

    /**
     * gain the newest item of the current dee machine.
     *
     * @param deeId id of dee
     * @return newest item
     */
    public FlowItem currentFlowItem(String deeId) {
        for (FlowItem item : items) {
            boolean inExecuted = false;
            for (FlowItem executedItem : executedItems) {
                if (item == executedItem) {
                    inExecuted = true;
                    break;
                }
            }
            if (!inExecuted) {
                if (StringUtils.isBlank(item.getDeeId()) || deeId.equals(item.getDeeId())) {
                    return item;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * change the given item to executed item list.
     *
     * @param item given item
     */
    public void itemToExecuted(FlowItem item) {
        if (null != item) {
            executedItems.add(item);
        }
    }

    /**
     * gain dee is end.
     *
     * @return true or false
     */
    public boolean deeIsEnd(String deeId) {
        // FlowItem item = currentFlowItem(deeId);
        return items.size() <= executedItems.size();
    }

    /**
     * if the "parameters" is existed, then return it, or create a new one.
     *
     * @return
     */
    public Parameters getNewParameters() {
        if (null == parameters) {
            return new Parameters();
        }
        return parameters;
    }

    public List<FlowItem> getItems() {
        return items;
    }

    public void setItems(List<FlowItem> items) {
        this.items = items;
    }

    public List<FlowItem> getExecutedItems() {
        return executedItems;
    }

    public void setExecutedItems(List<FlowItem> executedItems) {
        this.executedItems = executedItems;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
