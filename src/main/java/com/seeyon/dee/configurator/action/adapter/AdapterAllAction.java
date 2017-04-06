package com.seeyon.dee.configurator.action.adapter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;

/**
 * 适配器-action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-17下午02:50:46
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class AdapterAllAction extends BaseAction {
    
    private static final long serialVersionUID = -4222589338368618317L;

    private static Logger logger = Logger.getLogger(AdapterAllAction.class.getName());
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private String resourceId;
    private String flowId;
    private long newDate;
    
    /**
     * edit
     * 
     * @return
     */
    public String edit() {
        try {
            this.newDate = System.currentTimeMillis();
            String tmpId = deeAdapterMgr.getTemplateIdByResourceId(resourceId);
            int iTmpId = Integer.parseInt(tmpId);
            return editTmp(iTmpId);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
    }
   
    
    private String editTmp(int iTmpId) {
        if (iTmpId == DeeResourceEnum.ReaderScript.ordinal() ||
            iTmpId == DeeResourceEnum.ProcessorScript.ordinal() ||
            iTmpId == DeeResourceEnum.WriterScript.ordinal()) {
            return "script";
        } else if (iTmpId == DeeResourceEnum.WSReader.ordinal() ||
                   iTmpId == DeeResourceEnum.WSWriter.ordinal()) {
            return "wsprocessor";
        } else if (iTmpId == DeeResourceEnum.SAPJCOReader.ordinal()) {
            return "sapjco";
        } else if (iTmpId == DeeResourceEnum.SAPJCOWriter.ordinal()) {
        	return "sapjcowriter";
        } else if (iTmpId == DeeResourceEnum.XSLTPROCESSOR.ordinal()) {
            return "xslt";
        } else if (iTmpId == DeeResourceEnum.SAPWSReader.ordinal() ||
        		   iTmpId == DeeResourceEnum.SAPWSWriter.ordinal())
        {
        	return "sapws";
        }
        else if (iTmpId == DeeResourceEnum.JDBCWRITER.ordinal())
	     {
	     	return "jdbcwriter";
	     }
        else if(iTmpId == DeeResourceEnum.JDBCREADER.ordinal()){
            return "jdbcreader";
        }
        else if(iTmpId == DeeResourceEnum.COLUMNMAPPINGPROCESSOR.ordinal()){
            return "mapping_processor";
        } else if (iTmpId == DeeResourceEnum.OrgSyncWriter.ordinal()) {
            return "org_sync";
        } else if (iTmpId == DeeResourceEnum.A8BPMLAUCHFORMCOLWRITER.ordinal()) {
            return "a8source";
        } else if (iTmpId == DeeResourceEnum.A8CommonWSWriter.ordinal()) {
            return "a8commonws";
        }
        else if (iTmpId == DeeResourceEnum.CustomReader.ordinal() || iTmpId == DeeResourceEnum.CustomProcessor.ordinal() ||
        		iTmpId == DeeResourceEnum.CustomWriter.ordinal() ) {
            return "custom";
        } else if (iTmpId == DeeResourceEnum.A8EnumReader.ordinal()) {
            return "a8enumreader";
        } else if (iTmpId == DeeResourceEnum.A8EnumWriter.ordinal()) {
            return "a8enumwriter";
        } else if (iTmpId == DeeResourceEnum.RestReader.ordinal()) {
            return "restprocessor";
        } else if (iTmpId == DeeResourceEnum.RestWriter.ordinal()) {
            return "restprocessor";
        } else if (iTmpId == DeeResourceEnum.A8MsgWriter.ordinal()) {
            return "a8msgwriter";
       } else if (iTmpId == DeeResourceEnum.A8FormWriteBackWriter.ordinal()) {
            return "a8form_writeback_writer";
       } else if (iTmpId == DeeResourceEnum.SRReader.ordinal() ||
               iTmpId == DeeResourceEnum.SRWriter.ordinal()) {
    	   	return "srprocessor";
       }
        return "error";
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public long getNewDate() {
        return newDate;
    }

    public void setNewDate(long newDate) {
        this.newDate = newDate;
    }

}
