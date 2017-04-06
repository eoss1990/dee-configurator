package com.seeyon.dee.configurator.dao.impl;

import com.seeyon.dee.configurator.dao.CodePkgDao;
import com.seeyon.v3x.dee.common.db.codelib.model.CodePkgBean;
import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangfb
 */
@Repository("codePkgDao")
public class CodePkgDaoImpl extends BaseDaoImpl<CodePkgBean, String> implements CodePkgDao {
    /**
     * 包名是否存在
     *
     * @param name 包名
     * @return true:是，false:不是
     */
    @Override
    public boolean containsName(String name) throws SystemException {
        String hql = "select count(*) from CodePkgBean pb where pb.name=:name";

        Query query = getSession().createQuery(hql);
        query.setParameter("name", name);

        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    @Override
    public List<CodePkgBean> getChildPkg(String parentPkg) throws SystemException {
        String hql = "from CodePkgBean pb where pb.name like :name";

        Query query = getSession().createQuery(hql);
        query.setParameter("name", "" + parentPkg + ".%");

        return query.list();
    }
   
    @Override
    public List<CodePkgBean> getAllPkg() throws SystemException {
    	String hql = "from CodePkgBean  ";
    	Query query = getSession().createQuery(hql);
    	return query.list();
    }

    /**
     * 是否包含子节点
     *
     * @param parentPkg
     * @return
     * @throws SystemException
     */
    @Override
    public boolean containsChildPkg(String parentPkg) throws SystemException {
        String hql = "select count(*) from CodePkgBean pb where pb.name like :name";

        Query query = getSession().createQuery(hql);
        query.setParameter("name", "" + parentPkg + ".%");

        Long count = (Long) query.uniqueResult();
        return count > 0;
    }
}
