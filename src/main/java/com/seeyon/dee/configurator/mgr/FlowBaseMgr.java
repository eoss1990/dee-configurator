package com.seeyon.dee.configurator.mgr;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.seeyon.dee.configurator.dto.FlowParameter;
import com.seeyon.v3x.dee.bean.JDBCResourceBean;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.exception.SystemException;

public interface FlowBaseMgr {
	
	/**
	 * 查找所有任务
	 * @return Collection<FlowTypeBean>
	 * @throws SystemException
	 */
	public Collection<FlowTypeBean> findAll() throws SystemException;
	
	/**
	 * 保存任务
	 * @param FlowBean
	 * @return uuid
	 * @throws SystemException
	 */
	@Transactional
	public String save(FlowBean FlowBean) throws SystemException;

	/**
	 * 检查名称是否重复
	 * @param flowName
	 * @return boolean
	 * @throws SystemException
	 */
	public boolean checkName(String flowName) throws SystemException;
	
	/**
     * 查询所有任务名
     *
     * @return list
     * @throws SystemException
     */
	
	public List<String> findAllDisName(String name) throws SystemException;
	
	/**
	 * 根据主键查找任务
	 * @param uuid
	 * @return FlowBean
	 * @throws SystemException
	 */
	
	
	public FlowBean getFlow(String uuid) throws SystemException;
	
	/**
     * 根据ID查找适配器
     * @param uuid
     * @return FlowBean
     * @throws SystemException
     */
	public JDBCResourceBean getResourceById(String inid) throws SystemException;
	
	/**
	 * 更新任务
	 * @param FlowBean
	 * @throws SystemException
	 */
	@Transactional
	public void update(FlowBean FlowBean) throws SystemException;
	
	/**
	 * 根据flowId查询该任务下的所有适配器
	 * @param flowId
	 * @return
	 */
	public List<DeeResourceBean> findResourceList(String flowId);

	/**
	 * 根据mapId查询该任务下的映射配置
	 * @param mapId
	 * @return
	 */
	public  List<DeeResourceBean> findMappingById(String mapId);
	
	/**
	 * 保存或修改任务参数
	 * @param params
	 * @param uuid
	 * @throws SystemException
	 */
	@Transactional
	public void saveOrUpdateParams(List<FlowParameter> params,String uuid) throws SystemException;
	
	/**
	 * 根据flowId查询该任务下的所有任务参数
	 * @param flowId
	 * @return List<ParameterBean>
	 * @throws SystemException
	 */
	public List<ParameterBean> findParamsByFlowId(String flowId) throws SystemException;
	
	/**
	 * 拖动更新适配器执行顺序
	 * @param start
	 * @param end
	 * @param flowId
	 * @param resId
	 * @param proName
	 * @throws SystemException
	 */
	@Transactional
	public void updateOrder(int start,int end,String flowId,String resId,String proName) throws SystemException;
	
	/**
	 * 删除适配器
	 * @param flowId
	 * @param resId
	 * @throws SystemException
	 */
	@Transactional
	public void deleteRes(String flowId,String resId) throws SystemException;
	
	/**
	 * 获取任务分类
	 * @param flowTypeId
	 * @return
	 * @throws SystemException
	 */
	public FlowTypeBean getFlowTypeBean(String flowTypeId) throws SystemException;
	
	public String checkProcessOnly(String[] idsArray) throws SystemException;

    public void copyAndSavePara(ParameterBean parabcopy) throws SystemException;
	
}
