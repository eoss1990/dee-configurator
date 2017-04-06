package dwz.framework.common.mgr.impl;

import java.util.List;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.common.pagination.IPageContext;
import dwz.framework.common.pagination.Page;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;

public abstract class BaseMgrImpl<M extends java.io.Serializable, PK extends java.io.Serializable> implements BaseMgr<M, PK> {

	protected BaseDao<M, PK> baseDao;
    
    public abstract void setBaseDao(BaseDao<M, PK> baseDao);
	
	@Override
	public M save(M model) throws SystemException {
		baseDao.save(model);
		return model;
	}

	@Override
	public void batchInsert(List<M> models) throws SystemException {
		baseDao.batchInsert(models);
	}

	@Override
	public void saveOrUpdate(M model) throws SystemException {
		baseDao.saveOrUpdate(model);
	}

	@Override
	public void batchSaveOrUpdate(List<M> models) throws SystemException {
		baseDao.batchSaveOrUpdate(models);
	}

	@Override
	public void update(M model) throws SystemException {
		baseDao.update(model);
	}

	@Override
	public void merge(M model) throws SystemException {
		baseDao.merge(model);
	}

	@Override
	public void delete(PK id) throws SystemException {
		baseDao.delete(id);
	}

	@Override
	public void deleteObject(M model) throws SystemException {
		baseDao.deleteObject(model);
	}

	@Override
	public M get(PK id) throws SystemException {
		return baseDao.get(id);
	}

	@Override
	public int countAll() throws SystemException {
		return baseDao.countAll();
	}

	@Override
	public Page<M> listAll(int pageNum) throws SystemException {
		return listAll(pageNum, IPageContext.DEFAULT_PAGE_SIZE);
	}

	@Override
	public Page<M> listAll(int pageNum, int pageSize, Object... params) throws SystemException {
		Integer count = countAll();
        List<M> items = baseDao.listAll(pageNum, pageSize, params);
        return PageUtil.getPage(pageNum, pageSize, items, count);
	}

	@Override
	public Page<M> listAll(String hql, int pageNum, int pageSize, Object... params) throws SystemException {
		Integer count = countAll();
        List<M> items = baseDao.listAll(hql, pageNum, pageSize, params);
        return PageUtil.getPage(pageNum, pageSize, items, count);
	}

}
