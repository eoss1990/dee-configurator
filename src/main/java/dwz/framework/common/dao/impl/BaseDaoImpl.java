package dwz.framework.common.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.common.db.redo.model.SyncBeanLog;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;


public abstract class BaseDaoImpl<M extends java.io.Serializable, PK extends java.io.Serializable> implements BaseDao<M, PK> {

    private static Logger logger = Logger.getLogger(BaseDaoImpl.class.getName());

    private final Class<M> entityClass;
    private final String HQL_COUNT_ALL;
    private final String HQL_LIST_ALL;
    private final static int BATCH_OPERATION_SIZE = 20;

    private String pkName = null;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        this.entityClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] fields = this.entityClass.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                this.pkName = f.getName();
                break;
            }
        }

        HQL_COUNT_ALL = " select count(*) from " + this.entityClass.getSimpleName();
        HQL_LIST_ALL = "from " + this.entityClass.getSimpleName() + " order by " + pkName + " desc";
    }

    @SuppressWarnings("unchecked")
    @Override
    public PK save(M model) throws SystemException {
        try {
            return (PK) getSession().save(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void batchInsert(List<M> models) throws SystemException {
        for (int i = 0; i < models.size(); i++) {
            this.save(models.get(i));
            if ((i + 1) % BATCH_OPERATION_SIZE == 0) {
                try {
                    getSession().flush();
                    getSession().clear();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new SystemException(e);
                }
            }
        }
    }

    @Override
    public void saveOrUpdate(M model) throws SystemException {
        try {
            getSession().saveOrUpdate(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void batchSaveOrUpdate(List<M> models) throws SystemException {
        for (int i = 0; i < models.size(); i++) {
            this.saveOrUpdate(models.get(i));
            if ((i + 1) % BATCH_OPERATION_SIZE == 0) {
                try {
                    getSession().flush();
                    getSession().clear();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new SystemException(e);
                }
            }
        }
    }

    @Override
    public void update(M model) throws SystemException {
        try {
            getSession().update(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void merge(M model) throws SystemException {
        try {
            getSession().merge(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void delete(PK id) throws SystemException {
        try {
            getSession().delete(this.get(id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void deleteObject(M model) throws SystemException {
        try {
            getSession().delete(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public M get(PK id) throws SystemException {
        try {
            return (M) getSession().get(this.entityClass, id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public int countAll() throws SystemException {
        return countByQuery(HQL_COUNT_ALL);
    }

    @Override
    public boolean exists(PK id) throws SystemException {
        return get(id) != null;
    }

    @Override
    public void flush() throws SystemException {
        try {
            getSession().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public void clear() throws SystemException {
        try {
            getSession().clear();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public List<M> listAll() throws SystemException {
        return this.list(HQL_LIST_ALL);
    }

    @Override
    public List<M> listAll(int pageNum, int pageSize, Object... params)
            throws SystemException {
        return this.listAll(HQL_LIST_ALL, pageNum, pageSize, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<M> listAll(String hql, int pageNum, int pageSize,
                           Object[] params) throws SystemException {
        try {
            Query query = getSession().createQuery(hql);
            setParameters(query, params);
            if (pageNum > -1 && pageSize > -1) {
                query.setMaxResults(pageSize);
                int start = PageUtil.getPageStart(pageNum, pageSize);
                if (start != 0) {
                    query.setFirstResult(start);
                }
            }
            if (pageNum < 0) {
                query.setFirstResult(0);
            }

            List<M> results = query.list();

            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SyncBeanLog> listAllLog(String sql, int pageNum, int pageSize) throws SystemException {
        try {
            Query query = getSession().createSQLQuery(sql);
            if (pageNum > -1 && pageSize > -1) {
                query.setMaxResults(pageSize);
                int start = PageUtil.getPageStart(pageNum, pageSize);
                if (start != 0) {
                    query.setFirstResult(start);
                }
            }
            if (pageNum < 0) {
                query.setFirstResult(0);
            }

            List<Object[]> results = query.list();
            List<SyncBeanLog> beanList = new ArrayList<SyncBeanLog>();
            for (Object[] obj : results) {
                SyncBeanLog bean = new SyncBeanLog();
                bean.setFlow_id((String) obj[0]);
                bean.setFlow_dis_name((String) obj[1]);
                bean.setSync_id((String) obj[2]);
                bean.setSync_state((Integer) obj[3]);
                bean.setSync_time((String) obj[4]);
                bean.setExec_time(obj[5] == null ? 0 : (Integer) obj[5]);
                bean.setRedo_id((String) obj[6]);
                bean.setCounter(obj[7] == null ? 0 : (Integer) obj[7]);
                beanList.add(bean);
            }
            return beanList;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<M> listAll(String hql, Object[] params)
            throws SystemException {
        try {
            Query query = getSession().createQuery(hql);
            setParameters(query, params);
            List<M> list = query.list();

            return list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }

    }

    @Override
    public List<M> findByQuery(String hql, int startIndex, int count) throws SystemException {
        return this.findByQuery(hql, null, startIndex, count);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<M> findByQuery(String hql, Object[] params, int startIndex, int count) throws SystemException {
        try {
            Query query = getSession().createQuery(hql);
            setParameters(query, params);
            if (startIndex >= 0 && count > 0) {
                query.setFirstResult(startIndex).setMaxResults(count);
            }
            List<M> results = query.list();

            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @Override
    public int countByQuery(String hql) throws SystemException {
        return countByQuery(hql, null);
    }

    @Override
    public int querySqlCount(String sql) throws SystemException {
        Query query = getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        BigInteger big = (BigInteger) obj;
        return big.intValue();
    }

    @Override
    public int countByQuery(String hql, Object[] params) throws SystemException {
        Long total = aggregate(hql, params);
        return total.intValue();
    }

    /**
     * 获取session
     *
     * @return org.hibernate.Session
     */
    public Session getSession() throws SystemException {
        try {
            return sessionFactory.getCurrentSession();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T aggregate(final String hql, final Object... paramlist) throws SystemException {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);

        try {
            return (T) query.uniqueResult();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    private void setParameters(Query query, Object[] paramlist) throws SystemException {
        if (paramlist != null) {
            for (int i = 0; i < paramlist.length; i++) {
                try {
                    if (paramlist[i] instanceof Date) {
                        query.setTimestamp(i, (Date) paramlist[i]);
                    } else {
                        query.setParameter(i, paramlist[i]);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new SystemException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> list(final String hql, final Object... paramlist) throws SystemException {
        Query query = getSession().createQuery(hql);

        setParameters(query, paramlist);
        try {
            List<T> results = query.list();
            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<M> findAll() throws SystemException {

        Package thePackage = entityClass.getPackage();
        String packageName = "";
        if (thePackage != null) {
            packageName = thePackage.getName() + ".";
        } else {
            packageName = "";
        }

        String clazzName = entityClass.getSimpleName();
        clazzName = packageName + clazzName;
        final String listHql = "from " + clazzName;

        try {
            Query query = getSession().createQuery(listHql);
            Object o = query.list();
            return (List) o;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    public List<Object> findBySQLQuery(final String queryStr,
                                       final Object[] params, final int startIndex, final int count) throws SystemException {

        Query query = getSession().createSQLQuery(queryStr);
        if (startIndex >= 0 && count > 0) {
            query.setFirstResult(startIndex).setMaxResults(count);
        }
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
        }
        try {
            Object o = query.list();
            return (List) o;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    public int countBySQLQuery(final String queryStr, final Object[] params) throws SystemException {

        Query query = getSession().createSQLQuery(queryStr);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
        }

        try {
            Object o = query.list();
            List<Number> nums = (List<Number>) o;
            if (nums != null && nums.size() > 0)
                return nums.iterator().next().intValue();
            return 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

}
