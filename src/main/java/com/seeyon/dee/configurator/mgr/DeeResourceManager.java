package com.seeyon.dee.configurator.mgr;

import java.util.List;

import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.exception.SystemException;

public interface DeeResourceManager extends BaseMgr<DeeResourceBean,String> {
	/*****************数据源**************************/
	public List<DeeResourceBean> findAllDs(String[] params,int startIndex, int count, String orderField)  throws SystemException;
	public int dsCount(String[] params) throws SystemException;
	public DeeResourceBean getDrByDrId(String drId) throws SystemException;
	public int deleteByDrId(String drId) throws SystemException;
	public Boolean isHasSameDisName(String drId,String disName) throws SystemException;
	public String isQuoteByFlow(String drId) throws SystemException;
	public void saveDatasource(DeeResourceBean drb) throws SystemException;
	public void modifyDatasource(DeeResourceBean drb) throws SystemException;
	public Boolean testConnect(DeeResourceBean drb) throws Exception;
	public Boolean isA8Meta(DeeResourceBean drb) throws Exception;
	public List<DeeResourceBean> findAllJdbcDs() throws SystemException;
	/*****************字典**************************/
	public List<DeeResourceBean> findAllDict(String keyWords,int startIndex, int count, String orderField)  throws SystemException;
	public int dictCount(String keyWords) throws SystemException;
	public Boolean isHasSameDisNameOnDict(String drId,String disName) throws SystemException;
	public List<KeyValue> findAllDictDs() throws SystemException;
}
