package com.seeyon.dee.configurator.action;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.dto.CodeLibDto;
import com.seeyon.dee.configurator.dto.CodePkgDto;
import com.seeyon.dee.configurator.mgr.CodeLibMgr;
import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.script.ScriptRunner;
import dwz.framework.common.action.BaseAction;
import dwz.framework.common.pagination.Page;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 用户代码库action
 *
 * @author zhangfb
 */
public class CodeLibAction extends BaseAction {
    private static Log log = LogFactory.getLog(CodeLibAction.class);

    private CodeLibDto codeLibDto;

    private String className = "";

    private String pkgName = "";

    private String jsonData;

    private CodePkgDto pkg;

    private String oldPkgName;

    private String newPkgName;

    @Autowired
    @Qualifier("codeLibMgr")
    private CodeLibMgr codeLibMgr;

    /**
     * 查询所有包名
     *
     * @return json字符串
     */
    public String listPkg() {
        try {
            jsonData = JSONSerializer.toJSON(codeLibMgr.listPkg()).toString();
            return ajaxForwardSuccess("success");
        } catch (SystemException e) {
            log.error(e.getLocalizedMessage(), e);
            return ajaxForwardError("error");
        }
    }

    /**
     * 新增或修改包名
     *
     * @return 成功或失败
     */
    public String savePkg() {
        try {
            codeLibMgr.savePkg(oldPkgName, newPkgName);
            GenerationCfgUtil.getInstance().generationCodeLib();
            return ajaxForwardSuccess("保存成功");
        } catch (SystemException e) {
            return ajaxForwardError(e.getMessage());
        } catch (Exception e) {
            return ajaxForwardError("异常！" + e.getMessage());
        }
    }

    /**
     * 删除包名
     *
     * @return 成功或失败
     */
    public String deletePkg() {
        try {
            codeLibMgr.deletePkg(pkgName);
            GenerationCfgUtil.getInstance().generationCodeLib();
            return ajaxForwardSuccess("删除成功");
        } catch (SystemException e) {
            return ajaxForwardError(e.getMessage());
        } catch (Exception e) {
            return ajaxForwardError("异常！" + e.getMessage());
        }
    }

    /**
     * 加载list页面
     *
     * @return
     */
    public String list() {
        return INPUT;
    }

    /**
     * 加载list页面的内容列表
     *
     * @return
     */
    public String listClass() {
        try {
            String where = "1=1 and obj.className like '%" + className.trim().replaceAll("%", "\\\\%").replaceAll("_", "\\\\_") + "%'";
            if (StringUtils.isNotBlank(pkgName)) {
                where += " and obj.pkgName='" + pkgName + "'";
            }

            String hql = PageUtil.createHql(CodeLibBean.class, where, "className", "asc");
            String hqlCount = PageUtil.createHqlCount(CodeLibBean.class, where);

            Page<CodeLibBean> codeLibPage = codeLibMgr.listClass(hql, hqlCount, getPageNum(), getNumPerPage(), null);
            ActionContext.getContext().put("codeLibPage", codeLibPage);
        } catch (Exception e) {
            log.error("获取数据出错：" + e.getLocalizedMessage(), e);
        }
        return INPUT;
    }

    /**
     * 保存类
     *
     * @return
     */
    public String saveClass() {
        try {
            codeLibMgr.saveClass(codeLibDto);
            GenerationCfgUtil.getInstance().generationCodeLib();
        } catch (SystemException e) {
            log.error("代码库修改失败：" + e.getLocalizedMessage(), e);
            return ajaxForwardError(e.getMessage());
        } catch (Exception e) {
            log.error("代码库新增失败：" + e.getLocalizedMessage(), e);
            return ajaxForwardError("新增失败：" + e);
        }
        return ajaxForwardSuccess("操作成功");
    }

    /**
     * 查看类
     *
     * @return
     */
    public String viewClass() {
        try {
            String[] ids = (String[]) ActionContext.getContext().getParameters().get("uid");
            CodeLibBean codeLibBean = codeLibMgr.getClassBydId(ids[0]);
            ActionContext.getContext().put("codeLibBean", codeLibBean);
        } catch (Exception e) {
            log.error("代码库获取异常：" + e.getLocalizedMessage(), e);
        }
        return "edit";
    }

    /**
     * 删除类
     *
     * @return
     */
    public String deleteClass() {
        try {
            String[] ids = (String[]) ActionContext.getContext().getParameters().get("ids");
            if (ids.length > 0 && ids[0] != null) {
                String[] tmpIds = ids[0].split(",");
                codeLibMgr.deleteClass(tmpIds);
                GenerationCfgUtil.getInstance().generationCodeLib();
                return ajaxForwardSuccess("删除成功！");
            }
            return ajaxForwardError("删除失败：没有获取到待删除的ID：" + ids);
        } catch (Exception e) {
            log.error("删除失败：" + e.getLocalizedMessage(), e);
            return ajaxForwardError("删除失败：" + e.getLocalizedMessage());
        }
    }

    /**
     * 校验类
     *
     * @return
     */
    public String compileClass() {
        try {
            String codeText = "package " + codeLibDto.getPkgName() + ";\n\n" + codeLibDto.getCode();

            Object obj = new ScriptRunner().complie(codeText);
            if (obj == null) {
                return ajaxForwardError("校验失败：null");

            }
            //编译完后，将缓存清除重新加载
            GenerationCfgUtil.getInstance().refreshScript();
            String fullClassName = codeLibDto.getPkgName() + "." + codeLibDto.getClassName();
            if (fullClassName.equals(((Class) obj).getName())) {
                return ajaxForwardSuccess("校验成功！");
            } else {
                return ajaxForwardError("校验失败：类名不一致！");
            }
        } catch (Exception e) {
            log.error("校验失败：" + e.getLocalizedMessage(), e);
            String msg = StringEscapeUtils.escapeHtml(e.getMessage().replaceAll("[\\t\\n\\r]", ""));
            return ajaxForwardError("校验失败：" + msg);
        }
    }

    public CodeLibDto getCodeLibDto() {
        return codeLibDto;
    }

    public void setCodeLibDto(CodeLibDto codeLibDto) {
        this.codeLibDto = codeLibDto;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public CodePkgDto getPkg() {
        return pkg;
    }

    public void setPkg(CodePkgDto pkg) {
        this.pkg = pkg;
    }

    public String getOldPkgName() {
        return oldPkgName;
    }

    public void setOldPkgName(String oldPkgName) {
        this.oldPkgName = oldPkgName;
    }

    public String getNewPkgName() {
        return newPkgName;
    }

    public void setNewPkgName(String newPkgName) {
        this.newPkgName = newPkgName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
