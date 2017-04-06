package com.seeyon.dee.configurator.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowSubDao;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("flowSubDao")
public class FlowSubDaoImpl extends BaseDaoImpl<FlowSubBean, String> implements FlowSubDao {
	private static Logger logger = Logger.getLogger(BaseDaoImpl.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public String getFlowIdBySourceId(String resourceId) throws SystemException {
		String sql = "select b.flow_id from dee_resource a left join dee_flow_sub b "
				+ "on a.resource_id = b.resource_id where a.resource_id = '" + resourceId + "'";
		String flowId = "";
		try {
	        Query query = getSession().createSQLQuery(sql);

	        Object obj = query.uniqueResult();
	        if(obj != null){
		        flowId = (String) obj;
	        }
	        return flowId;
	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        throw new SystemException(e);
	    }
	}
}
