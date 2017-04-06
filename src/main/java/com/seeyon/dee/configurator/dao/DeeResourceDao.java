package com.seeyon.dee.configurator.dao;

import java.util.List;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

public interface DeeResourceDao extends BaseDao<DeeResourceBean, String> {
	public List<DeeResourceBean> QueryAllDs(String[] params,int startIndex,int count, String orderField) throws SystemException;
	public int dsCount(String[] params) throws SystemException;
	/******字典 ****/
	public List<DeeResourceBean> QueryAllDict(String[] params,int startIndex,int count, String orderField) throws SystemException;
	public int dictCount(String[] params) throws SystemException;
	
	public List<DeeResourceBean> QueryAllJdbcDs() throws SystemException;
	public String getTemplateIdByResourceId(String resourceId) throws SystemException;
}
