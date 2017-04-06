package com.seeyon.dee.configurator.mgr.impl;

import com.seeyon.dee.configurator.dao.CodeLibDao;
import com.seeyon.dee.configurator.dao.CodePkgDao;
import com.seeyon.dee.configurator.dto.CodeLibDto;
import com.seeyon.dee.configurator.dto.CodePkgDto;
import com.seeyon.dee.configurator.mgr.CodeLibMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;
import com.seeyon.v3x.dee.common.db.codelib.model.CodePkgBean;

import dwz.framework.common.pagination.Page;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;
import dwz.framework.util.functionUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author zhangfb
 */
@Service("codeLibMgr")
public class CodeLibMgrImpl implements CodeLibMgr {
    private static Log log = LogFactory.getLog(CodeLibMgrImpl.class);

    @Autowired
    @Qualifier("codePkgDao")
    CodePkgDao codePkgDao;

    @Autowired
    @Qualifier("codeLibDao")
    CodeLibDao codeLibDao;

    @Override
    public List<CodePkgDto> listPkg() throws SystemException {
        List<CodePkgBean> beans = codePkgDao.findAll();

        // 将系列包名生成树状对象
        List<CodePkgDto> dtos = exchange2Tree(beans);

        // 根据包名排序
        Collections.sort(dtos, new Comparator<CodePkgDto>() {
            @Override
            public int compare(CodePkgDto o1, CodePkgDto o2) {
                if (o1 != null && o1.getName() != null) {
                    return o1.getId().compareTo(o2.getId());
                }
                return 0;
            }
        });

        return dtos;
    }

    @Override
    public void savePkg(String oldPkgName, String newPkgName) throws SystemException {
        if (StringUtils.isBlank(oldPkgName)) {      // 新增操作
            try {
                if (codePkgDao.containsName(newPkgName)) {
                    throw new SystemException("同级节点不允许重名！");
                }
                codePkgDao.save(new CodePkgBean(newPkgName));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (e instanceof SystemException) {
                    throw (SystemException) e;
                }
                throw new SystemException("异常！" + e.getMessage());
            }
        } else {                                    // 修改操作
            try {
                if (oldPkgName.equals(newPkgName)) {
                    return;
                }

                CodePkgBean bean = codePkgDao.get(oldPkgName);
                if (bean == null) {
                    throw new SystemException(oldPkgName + "包不存在，请确认是否已被删除！");
                }

                if (codePkgDao.containsName(newPkgName)) {
                    throw new SystemException("同级节点不允许重名！");
                }

                // 删除旧包和旧包下面的子包，并新增新包、修改新包下的子包
                List<CodePkgBean> newBeans = new ArrayList<CodePkgBean>();
                List<CodePkgBean> oldBeans = codePkgDao.getChildPkg(oldPkgName);
                for (CodePkgBean oldBean : oldBeans) {
                    codePkgDao.deleteObject(oldBean);
                    newBeans.add(new CodePkgBean(oldBean.getName().replace(oldPkgName, newPkgName)));
                }
                codePkgDao.delete(oldPkgName);
                codePkgDao.batchInsert(newBeans);
                codePkgDao.saveOrUpdate(new CodePkgBean(newPkgName));

                // 修改旧包关联的类的包名
                List<CodeLibBean> libBeans = codeLibDao.listByPkgName(oldPkgName);
                for (CodeLibBean libBean : libBeans) {
                    if (oldPkgName.equals(libBean.getPkgName()) ||
                            libBean.getPkgName().startsWith(oldPkgName + ".")) {
                        libBean.setPkgName(libBean.getPkgName().replace(oldPkgName, newPkgName));
                    }
                }
                codeLibDao.batchSaveOrUpdate(libBeans);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                if (e instanceof SystemException) {
                    throw (SystemException) e;
                } else {
                    throw new SystemException("异常！" + e.getMessage());
                }
            }
        }
    }

    @Override
    public void deletePkg(String pkgName) throws SystemException {
        if (StringUtils.isBlank(pkgName)) {
            throw new SystemException("删除的包名不能为空！");
        }

        try {
            CodePkgBean bean = codePkgDao.get(pkgName);

            // 包不存在，说明已被删除，正常返回
            if (bean == null) {
                return;
            }

            // 校验类是否引用了该包
            if (codeLibDao.existPkg(pkgName)) {
                throw new SystemException("请先删除包下面的类！");
            }

            // 校验是否包含子包
            if (codePkgDao.containsChildPkg(pkgName)) {
                throw new SystemException("请先删除子包！");
            }

            codePkgDao.delete(pkgName);
        } catch (SystemException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<CodeLibBean> listClass(String hql, String hqlCount, int pageNum, int numPerPage, Object[] params) throws SystemException {
        Integer count = codeLibDao.countByQuery(hqlCount);
        List<CodeLibBean> items = codeLibDao.listAll(hql, pageNum, numPerPage, params);
        return PageUtil.getPage(pageNum, numPerPage, items, count);
    }

    @Override
    public void saveClass(CodeLibDto dto) throws SystemException {
        if (StringUtils.isBlank(dto.getPkgName())) {
            throw new SystemException("包名不能为空");
        }

        if (!Pattern.matches("^[A-Z].*?", dto.getClassName())) {
            throw new SystemException("类名首字母必须大写！");
        }

        if (!"com.seeyon.dee.codelib".equals(dto.getPkgName()) &&
                !dto.getPkgName().startsWith("com.seeyon.dee.codelib.")) {
            throw new SystemException("包名必须在com.seeyon.dee.codelib下！");
        }

        if (StringUtils.isBlank(dto.getId())) {
            insertCodeLib(dto);
        } else {
            updateCodeLib(dto);
        }
    }

    @Override
    public CodeLibBean getClassBydId(String id) throws SystemException {
        return codeLibDao.get(id);
    }

    @Override
    public int deleteClass(String[] ids) throws SystemException {
        int count = 0;
        for (String id : ids) {
            if (id != null && codeLibDao.get(id) != null) {
                count += 1;
                codeLibDao.delete(id);
            }
        }
        return count;
    }

    private void insertCodeLib(CodeLibDto dto) throws SystemException {
        boolean isContain = codeLibDao.existClass(dto.getPkgName(), dto.getClassName(), null);
        if (isContain) {
            throw new SystemException(dto.getPkgName() + "." + dto.getClassName() + "已存在！");
        }

        CodeLibBean libBean = new CodeLibBean();
        libBean.setId(UuidUtil.uuid());
        libBean.setPkgName(dto.getPkgName());
        libBean.setClassName(dto.getClassName());
        libBean.setSimpleDesc(dto.getSimpleDesc());
        libBean.setCode(dto.getCode());
        libBean.setCreateTime(now());
        libBean.setModifyTime(libBean.getCreateTime());
        codeLibDao.save(libBean);
    }

    private void updateCodeLib(CodeLibDto dto) throws SystemException {
        boolean exist = codeLibDao.existClass(dto.getPkgName(), dto.getClassName(), dto.getId());
        if (exist) {
            throw new SystemException(dto.getPkgName() + "." + dto.getClassName() + "已存在！");
        }
        CodeLibBean libBean = codeLibDao.get(dto.getId());
        libBean.setId(dto.getId());
        libBean.setPkgName(dto.getPkgName());
        libBean.setClassName(dto.getClassName());
        libBean.setSimpleDesc(dto.getSimpleDesc());
        libBean.setCode(dto.getCode());
        libBean.setModifyTime(now());
        codeLibDao.update(libBean);
    }

    /**
     * 将包名列表转换成树
     *
     * @param beans 包名列表
     * @return 树
     */
    private List<CodePkgDto> exchange2Tree(List<CodePkgBean> beans) {
        if (beans == null) {
            return new ArrayList<CodePkgDto>();
        }

        List<String> list = new ArrayList<String>();
        for (CodePkgBean bean : beans) {
            list.add(bean.getName());
        }

        return exchange2PkgDto(list);
    }

    /**
     * 将包名列表转换成树
     *
     * @param list 包名列表
     * @return 树
     */
    private List<CodePkgDto> exchange2PkgDto(List<String> list) {
        final String constCodeLib = "com.seeyon.dee.codelib";

        Set<CodePkgDto> results = new HashSet<CodePkgDto>();
        results.add(new CodePkgDto(constCodeLib, constCodeLib, ""));

        for (String item : list) {
            StringBuilder sb = new StringBuilder(item);
            String[] array = sb.delete(0, constCodeLib.length() + 1).toString().split("\\.");
            String pkgName = constCodeLib;
            for (String node : array) {
                if (StringUtils.isNotBlank(node)) {
                    String parentPkg = pkgName;
                    pkgName = parentPkg + "." + node;
                    results.add(new CodePkgDto(pkgName, node, parentPkg));
                }
            }
        }

        return new ArrayList<CodePkgDto>(results);
    }
    
 

    /**
     * 获取系统时间
     *
     * @return 系统时间
     */
    private String now() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    
    
    /**
     * 按 父id 获取用户自定义函数节点下的包和类
     * @param pid
     * @return
     * @throws SystemException 
     */
    public List<Map<String, String>> getUserPackAndClass(String pid) throws SystemException {
    	if(pid == null || "".equals(pid))
    		return null;
    	List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
    	List<CodePkgBean> rs = null;
    	List<CodeLibBean> cls = null;
    	List<CodePkgDto> dtos = null;
    	String hql = "from CodeLibBean as obj where  obj.pkgName=?  order by obj.className asc";
    	if("2".equals(pid)){//如果节点是用户代码库，获取用户代码库本身的根节点
    		String str = "com.seeyon.dee.codelib";
    		//获取包
    		rs = new ArrayList<CodePkgBean>();
    		CodePkgBean root = codePkgDao.get(str);
    		rs.add(root);
    		
    		// 将系列包名生成树状对象
        	dtos = exchange2Tree(rs);
    		
    	}else{
    		//获取包
    		rs = codePkgDao.getChildPkg(pid);
    		//获取类
    		Object[] params = new Object[]{pid};
    		cls = codeLibDao.listAll(hql, params);
    		CodePkgDto d = null;
    		// 将系列包名生成树状对象
    		if(rs.size() > 0){
    			dtos = exchange2Tree(rs);
            	for(CodePkgDto dto:dtos){
            		if( dto.getId().equals(pid) ){
            			d = dto;
            			break;
            		}
            	}
            	dtos.remove(d);
    		}
    	}
    	
    	List<Map<String, String>> ps = functionUtil.packageAdapter(dtos);
    	if(ps!=null)
    		fs.addAll(ps);
    	List<Map<String, String>> cs = functionUtil.classAdapter(cls);
    	if(cs!=null)
    		fs.addAll(cs);
    	return fs;
    }
    
    /**
     * 获取所有用户函数
     * @return
     * @throws SystemException
     */
    public List<Map<String, String>> getAllUserPackAndClass()
    		throws SystemException {
    	List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
    	List<CodeLibBean> cls = null;
    	List<CodePkgBean> rs = null; 
    	List<CodePkgDto> dtos = null;
    	
    	//获取所有包名
    	rs = codePkgDao.getAllPkg();
    	//获取所有函数
    	cls = codeLibDao.listAll();
    	if(rs.size() > 0){
			dtos = exchange2Tree(rs);
		}
    	
    	List<Map<String, String>> ps = functionUtil.packageAdapter(dtos);
    	if(ps!=null)
    		fs.addAll(ps);
    	List<Map<String, String>> cs = functionUtil.classAdapter(cls);
    	if(cs!=null)
    		fs.addAll(cs);
    	
    	return fs;
    }
}
