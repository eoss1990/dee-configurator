package com.seeyon.dee.configurator.camel;

import java.io.Serializable;
import java.util.Map;

/**
 * flow item obj
 *
 * @author zhangfb
 */
public class FlowItem implements Serializable {
    private String deeId;

    private String flowId;

    private boolean keepDocument;

    private Map<String, String> params;

    public String getDeeId() {
        return deeId;
    }

    public void setDeeId(String deeId) {
        this.deeId = deeId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public boolean isKeepDocument() {
        return keepDocument;
    }

    public void setKeepDocument(boolean keepDocument) {
        this.keepDocument = keepDocument;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
