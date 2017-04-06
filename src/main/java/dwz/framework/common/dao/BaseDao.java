package dwz.framework.common.dao;

import java.util.List;

import com.seeyon.v3x.dee.common.db.redo.model.SyncBeanLog;

import dwz.framework.exception.SystemException;

public interface BaseDao<M extends java.io.Serializable, PK extends java.io.Serializable> {

    public PK save(M model) throws SystemException;

    public void batchInsert(List<M> models) throws SystemException;

    public void saveOrUpdate(M model) throws SystemException;

    public void batchSaveOrUpdate(List<M> models) throws SystemException;

    public void update(M model) throws SystemException;

    public void merge(M model) throws SystemException;

    public void delete(PK id) throws SystemException;

    public void deleteObject(M model) throws SystemException;

    public M get(PK id) throws SystemException;

    public int countAll() throws SystemException;

    boolean exists(PK id) throws SystemException;

    public void flush() throws SystemException;

    public void clear() throws SystemException;

    public List<M> listAll() throws SystemException;

    public List<M> listAll(int pageNum, int pageSize, Object... params) throws SystemException;

    public List<M> listAll(final String hql,
            final int pageNum, final int pageSize, final Object[] params) throws SystemException;
    
    public List<SyncBeanLog> listAllLog(final String sql,
            final int pageNum, final int pageSize) throws SystemException;

    public List<M> listAll(final String hql, final Object[] params)throws SystemException;

    public List<M> findAll() throws SystemException;

    public List<M> findByQuery(final String queryStr,
            final int startIndex, final int count) throws SystemException;

    public List<M> findByQuery(final String queryStr,
            final Object[] params, final int startIndex, final int count) throws SystemException;


    public int countByQuery(final String queryStr) throws SystemException;
    
    public int querySqlCount(final String queryStr) throws SystemException;

    public int countByQuery(final String queryStr, final Object[] params) throws SystemException;

    public List<Object> findBySQLQuery(final String queryStr,
            final Object[] params, final int startIndex, final int count) throws SystemException;

    public int countBySQLQuery(final String queryStr, final Object[] params) throws SystemException;
}
