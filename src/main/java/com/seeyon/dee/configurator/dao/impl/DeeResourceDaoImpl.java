package com.seeyon.dee.configurator.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.DeeResourceDao;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("deeResourceDao")
public class DeeResourceDaoImpl extends BaseDaoImpl<DeeResourceBean, String> implements DeeResourceDao {

	/**
	 * 查询数据源列表（带分页）
	 * by dkywolf 20130506
	 * @return
	 * @throws SystemException 
	 */
	@Override
	public List<DeeResourceBean> QueryAllDs(String[] params,int startIndex, int count, String orderField) throws SystemException {
		StringBuilder sb = new StringBuilder();
		sb.append("from DeeResourceBean as obj where obj.deeResourceTemplate.resourceType.type_id=? and obj.dis_name like ?");
		if(params[1].contains("%")){
			sb.append(" ESCAPE '!'");
			String newStr = params[1];
			int len = newStr.indexOf("%");
			String strEnd = newStr.substring(len);
			String strBegin = newStr.substring(0, len);
			params[1] = "%"+strBegin+"!"+strEnd+"%";
		}
		else{
			params[1] = "%" + params[1] + "%";
		}
		if (orderField != null && orderField.length() > 0) {
			String subs = orderField.substring((orderField.length()-5),orderField.length());
			if(subs.trim().equals("_DESC")){
				sb.append(" order by obj."+orderField.trim().toLowerCase().substring(0,(orderField.length()-5))+" desc");
			}
			else{
				sb.append(" order by obj."+orderField.trim().toLowerCase());
			}
		}
		return (List<DeeResourceBean>) findByQuery(sb.toString(), params, startIndex, count);
	}


	@Override
	public int dsCount(String[] params) throws SystemException {
		// TODO Auto-generated method stub
		String hqlStr = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resourceType.type_id=? and obj.dis_name like ?";
		if(params[1].contains("%")){
			hqlStr +=" ESCAPE '!'";
			String newStr = params[1];
			int len = newStr.indexOf("%");
			String strEnd = newStr.substring(len);
			String strBegin = newStr.substring(0, len);
			params[1] = "%"+strBegin+"!"+strEnd+"%";
		}
		else{
			params[1] = "%" + params[1] + "%";
		}
		int count = this.countByQuery(hqlStr, params);
		return count;
	}


	/**
	 * 根据条件查询
	 * by dkywolf 20130506
	 * @return
	 * @throws SystemException 
	 */
	@Override
	public List<DeeResourceBean> QueryAllJdbcDs() throws SystemException {
		// TODO Auto-generated method stub
		String hql = "from DeeResourceBean as obj where obj.deeResourceTemplate.resourceType.type_id = ? order by obj.create_time desc";
		String[] params = {"3"};
		return this.listAll(hql, params);
	}


	/**
	 * 根据条件查询字典
	 * by dkywolf 20130506
	 * @return
	 * @throws SystemException 
	 */
	@Override
	public List<DeeResourceBean> QueryAllDict(String[] params, int startIndex,
			int count, String orderField) throws SystemException {
		StringBuilder sb = new StringBuilder();
		sb.append("from DeeResourceBean as obj where obj.deeResourceTemplate.resource_template_id in('12','13','29') and obj.dis_name like ?");
		if(params[0].contains("%")){
			sb.append(" ESCAPE '!'");
			String newStr = params[0];
			int len = newStr.indexOf("%");
			String strEnd = newStr.substring(len);
			String strBegin = newStr.substring(0, len);
			params[0] = "%"+strBegin+"!"+strEnd+"%";
		}
		else{
			params[0] = "%" + params[0] + "%";
		}
		if (orderField != null && orderField.length() > 0) {
			String subs = orderField.substring((orderField.length()-5),orderField.length());
			if(subs.trim().equals("_DESC")){
				sb.append(" order by obj."+orderField.trim().toLowerCase().substring(0,(orderField.length()-5))+" desc");
			}
			else{
				sb.append(" order by obj."+orderField.trim().toLowerCase());
			}
		}
		return (List<DeeResourceBean>) findByQuery(sb.toString(), params, startIndex, count);
	}


	/**
	 * 根据条件查询字典记录
	 * by dkywolf 20130506
	 * @return
	 * @throws SystemException 
	 */
	@Override
	public int dictCount(String[] params) throws SystemException {
		// TODO Auto-generated method stub
		String hqlStr = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resource_template_id in('12','13','29') and obj.dis_name like ?";
		if(params[0].contains("%")){
			hqlStr +=" ESCAPE '!'";
			String newStr = params[0];
			int len = newStr.indexOf("%");
			String strEnd = newStr.substring(len);
			String strBegin = newStr.substring(0, len);
			params[0] = "%"+strBegin+"!"+strEnd+"%";
		}
		else{
			params[0] = "%" + params[0] + "%";
		}
		int count = this.countByQuery(hqlStr, params);
		return count;
	}


    @Override
    public String getTemplateIdByResourceId(String resourceId) throws SystemException {
        String sql = "select resource_template_id from dee_resource where resource_id =?";
        try {
            Query query = getSession().createSQLQuery(sql);
            query.setParameter(0, resourceId);
            return (String)query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException(e);
        }
    }

}
