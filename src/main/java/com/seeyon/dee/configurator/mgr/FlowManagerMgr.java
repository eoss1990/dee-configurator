package com.seeyon.dee.configurator.mgr;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.exception.SystemException;

public interface FlowManagerMgr {
	
	/**
	 * 新增任务
	 * @param FB
	 * @throws SystemException
	 */
	@Transactional
	public void addFlowBean(FlowBean FB) throws SystemException;
	
	/**
	 * 任务列表
	 * @param orderField
	 * @param startIndex
	 * @param count
	 * @param keyWords
	 * @return Collection<FlowBean>
	 * @throws SystemException
	 */
	public Collection<FlowBean> searchFlowBean(String orderField,int startIndex, int count ,String keyWords) throws SystemException;

	public Collection<FlowBean> searchFlowBeanByTypeId(String orderField, int startIndex, int numPerPage, String flowTypeId) throws SystemException;
	
	/**
	 * 查询任务总数量
	 * @return Integer
	 * @throws SystemException
	 */
	public Integer searchFlowBeanNum(String className,String fieldName, String fieldValue) throws SystemException;
	
	/**
	 * 根据uuid查询任务
	 * @param uuid
	 * @return FlowBean
	 * @throws SystemException
	 */
	public FlowBean getFlowBean(String uuid) throws SystemException;
	
	/**
	 * 更新任务
	 * @param flowBean
	 * @throws SystemException
	 */
	@Transactional
	public void updateFlowBean(FlowBean flowBean) throws SystemException;
	
	/**
	 * 删除flowBean
	 * @param flowBean
	 * @throws SystemException
	 */
	@Transactional
	public void deleteFlowBean(FlowBean flowBean) throws SystemException;
	
	/**
	 * 根据uuid删除任务以及关联表
	 * @param id
	 * @throws SystemException
	 */
	@Transactional
	public void delete(String id) throws SystemException;
	
	/**
	 * 查询所有任务
	 * @return List<FlowBean>
	 * @throws SystemException
	 */
	public List<FlowBean> findAll() throws SystemException;

}
