package com.seeyon.dee.configurator.dao.impl;

import com.seeyon.dee.configurator.dao.CodeLibDao;
import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;
import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangfb
 */
@Repository("codeLibDao")
public class CodeLibDaoImpl extends BaseDaoImpl<CodeLibBean, String> implements CodeLibDao {
    /**
     * 包名是否存在
     *
     * @param pkgName 包名
     * @return true：存在，false：不存在
     * @throws SystemException
     */
    @Override
    public boolean existPkg(String pkgName) throws SystemException {
        String hql = "select count(*) from CodeLibBean lb where lb.pkgName = :pkgName";

        Query query = getSession().createQuery(hql);
        query.setParameter("pkgName", pkgName);

        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    /**
     * 包下面的类名是否存在
     *
     * @param pkgName   包名
     * @param className 类名
     * @param id        排除自身
     * @return true：存在，false：不存在
     * @throws SystemException
     */
    @Override
    public boolean existClass(String pkgName, String className, String id) throws SystemException {
        String hql = "select count(*) from CodeLibBean lb where lb.pkgName = :pkgName and lb.className = :className";

        if (StringUtils.isNotBlank(id)) {
            hql += " and lb.id != " + id;
        }

        Query query = getSession().createQuery(hql);
        query.setParameter("pkgName", pkgName);
        query.setParameter("className", className);

        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    @Override
    public List<CodeLibBean> listByPkgName(String likePkgName) throws SystemException {
        String hql = "from CodeLibBean lb where lb.pkgName like :pkgName";

        Query query = getSession().createQuery(hql);
        query.setParameter("pkgName", "" + likePkgName + "%");

        return query.list();
    }
}
