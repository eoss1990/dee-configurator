package dwz.framework.common.mgr;

import java.util.List;

import dwz.framework.common.pagination.Page;
import dwz.framework.exception.SystemException;

public interface BaseMgr<M extends java.io.Serializable, PK extends java.io.Serializable> {

    public M save(M model) throws SystemException;
    
    public void batchInsert(List<M> models) throws SystemException;

    public void saveOrUpdate(M model) throws SystemException;
    
    public void batchSaveOrUpdate(List<M> models) throws SystemException;

    public void update(M model) throws SystemException;

    public void merge(M model) throws SystemException;

    public void delete(PK id) throws SystemException;

    public void deleteObject(M model) throws SystemException;

    public M get(PK id) throws SystemException;

    public int countAll() throws SystemException;

    public Page<M> listAll(int pageNum) throws SystemException;

    public Page<M> listAll(int pageNum, int pageSize, Object... params) throws SystemException;
    
    public Page<M> listAll(String hql, int pageNum, int pageSize, Object... params) throws SystemException;
}
