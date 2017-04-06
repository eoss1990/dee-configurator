package com.seeyon.dee.configurator.action;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.dto.ComparatorImpl;
import com.seeyon.dee.configurator.dto.ComparatorMeta;
import com.seeyon.dee.configurator.dto.ComparatorMetaSort;
import com.seeyon.dee.configurator.dto.FlowParameter;
import com.seeyon.dee.configurator.dto.MetaData;
import com.seeyon.dee.configurator.mgr.CodeLibMgr;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.v3x.dee.debug.AdapterDebug;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.TransformContext;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCWriterBean;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.datasource.XMLDataSource;
import com.seeyon.v3x.dee.debug.ParamBean;
import com.seeyon.v3x.dee.debug.ScriptDebug;
import com.seeyon.v3x.dee.util.DocumentUtil;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.functionUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 任务基础信息、适配器增删、元数据、任务参数、调试相关逻辑
 *
 * @author yangyu
 */
public class FlowBaseAction extends BaseAction {

    private static final long serialVersionUID = 3029661668158765270L;
    
    @Autowired
    @Qualifier("flowBaseMgr")
    private FlowBaseMgr fbMgr;
    
    @Autowired
    @Qualifier("codeLibMgr")
    private CodeLibMgr codeLibMgr;
    
    
    private DeeAdapterMgr deeAdapterMgr;
    private static Log log = LogFactory.getLog(FlowBaseAction.class);
    private String flow_name;
    private List<MetaData> meta;
    private List<FlowParameter> params;
    private String order;
    private String ids;
    private String console_info;
    private String console_out;
    private String console_args;
    private String console_context;
    private List<String> console_mata;
    private String jsonData;
    private String debugFlag;
    private boolean error = false;

    /**
     * 查找带回任务分类
     *
     * @return
     */
    public String treeBringBack() {
        try {
            Collection<FlowTypeBean> ftList = fbMgr.findAll();
            ActionContext.getContext().put("ftList", ftList);
        } catch (SystemException e) {
            log.error("请检查数据是否存在" + e.getMessage(), e);
        }
        return INPUT;
    }

    private FlowSubBean getFlowSubBean(DeeResourceBean drb, FlowBean flowBean, int sort) {
        FlowSubBean sub = new FlowSubBean();

        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);

        return sub;
    }

    /**
     * 替换resourcecode
     *
     * @return
     */
    public String replaceResourceCodeId(String resourceCode, String uuid) {
        if(resourceCode.contains("ColumnMapping")){
            String uId = UuidUtil.uuid();
            final String nameEqual2 = "ref=\"";
            int starts = resourceCode.indexOf(nameEqual2);
            int ends  =  resourceCode.indexOf("\"", starts + nameEqual2.length());
            StringBuilder sb2 = new StringBuilder();
            String startStr2 = resourceCode.substring(0, starts);
            String endStr2 = resourceCode.substring(ends, resourceCode.length());
            resourceCode = sb2.append(startStr2 + nameEqual2 + uId + endStr2).toString() + ":" + uId;
        }
        final String nameEqual = "name=\"";
        int start = resourceCode.indexOf(nameEqual);
        int end = resourceCode.indexOf("\"", start + nameEqual.length());
        StringBuilder sb = new StringBuilder();

        String startStr = resourceCode.substring(0, start);
        String endStr = resourceCode.substring(end, resourceCode.length());
        String rtString = sb.append(startStr + nameEqual + uuid + endStr).toString();

        return rtString;
    }


    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * 任务批量复制
     *
     * @return
     */

    public String copyAll() {
        String[] flowIds = ids.split(",");  //复制后的任务命名
        final String bakSuffix = "-副本";
        for (String flowid : flowIds) {
            try {
                FlowBean fb = (FlowBean) BeanUtils.cloneBean(fbMgr.getFlow(flowid));
                String dis_name = fb.getDIS_NAME() + bakSuffix;
                List<String> filterDisNames = new ArrayList<String>();
                List<String> disNames = fbMgr.findAllDisName(dis_name);
                for (String tmp : disNames) {
                    if ((tmp.length() - dis_name.length()) <= 4) {
                        filterDisNames.add(tmp);
                    }
                }
                List<Integer> sortNums = new ArrayList<Integer>();
                for (int lname = 0; lname < filterDisNames.size(); lname++) {
                    if (filterDisNames.get(lname).length() >= 3) {
                        String sortName = filterDisNames.get(lname);

                        if (sortName.length() != dis_name.length()) {
                            String left = String.valueOf(sortName.charAt(dis_name.length()));
                            String right = String.valueOf(sortName.charAt(sortName.length() - 1));
                            if (left.endsWith("(") && right.endsWith(")")) {
                                String sortNum = sortName.substring(
                                        dis_name.length() + 1,
                                        sortName.length() - 1);
                                if (isNumeric(sortNum)) {
                                    sortNums.add(Integer.valueOf(sortNum));
                                }
                            }

                        }

                    }
                }
                Collections.sort(sortNums);

                boolean flag = true;
                for (int lname = 0; lname < filterDisNames.size(); lname++) {
                    if (filterDisNames.get(lname).equals(dis_name)) {
                        if (sortNums.size() == 0 || sortNums.get(0) > 1) {
                            dis_name += "(1)";
                            flag = false;
                        }
                        if (flag) {
                            for (int j = 0; j < sortNums.size() - 1; j++) {
                                if (sortNums.get(j + 1) - sortNums.get(j) > 1) {
                                    dis_name += "(" + (sortNums.get(j) + 1) + ")";
                                    flag = false;
                                    break;
                                }
                            }
                        }

                        if (flag && sortNums.size() > 0) {
                            dis_name += "(" + (sortNums.get(sortNums.size() - 1) + 1) + ")";
                        }
                    }
                }

                String flow_id = saveFlowBean(dis_name, fb); //保存任务
                saveAdapter(flowid, fb);  //保存适配器
                savePara(flowid, flow_id);  //保存参数

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ajaxForwardError("任务复制失败！");
            }
        }

        return ajaxForwardSuccess("任务复制成功！");

    }

    /*
     * 保存任务
     * 
     * */
    public String saveFlowBean(String dis_name, FlowBean fb) {
        String flow_id = "";
        try {
            fb.setFlowSubs(null);
            fb.setSchedules(null);
            fb.setDIS_NAME(dis_name);
            fb.setFLOW_ID(null);
            fb.setFLOW_NAME(null);
            fb.setCREATE_TIME(time());
            flow_id = fbMgr.save(fb);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return flow_id;
    }


    /*
     * 复制后保存参数
     * 
     * 
     * */
    public void savePara(String flowid, String flow_id) {
        try {
            List<ParameterBean> paraList = fbMgr.findParamsByFlowId(flowid);

            for (ParameterBean parab : paraList) {
                ParameterBean parabcopy = (ParameterBean) BeanUtils
                        .cloneBean(parab);
                FlowBean flowBean = new FlowBean();
                flowBean.setFLOW_ID(flow_id);
                parabcopy.setFlowBean(flowBean);
                parabcopy.setPARA_ID(UuidUtil.uuid());
                fbMgr.copyAndSavePara(parabcopy);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }


    }

    /*
     * 复制后新增适配器并保存
     * 
     * */
    public void saveAdapter(String flowId, FlowBean fb) {

        List<DeeResourceBean> drblist = fbMgr.findResourceList(flowId);
        try {
            for (DeeResourceBean drb : drblist) {
                String uuid = UuidUtil.uuid();
                DeeResourceBean drbcopy = (DeeResourceBean) BeanUtils
                        .cloneBean(drb);
                DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
                tmpBean.setResource_template_id(drb
                        .getDeeResourceTemplate().getResource_template_id());
                drbcopy.setDeeResourceTemplate(tmpBean);
                String rcode = replaceResourceCodeId(
                        drbcopy.getResource_code(), uuid);
                String ref_id = "";
                String resourceCode = "";
                if (rcode.contains("ColumnMapping") && rcode.contains(":")){
                    ref_id = rcode.split(":")[1];
                    resourceCode = rcode.split(":")[0];
                    List<DeeResourceBean> drbList = fbMgr.findMappingById(drb.getRef_id());
                    for (DeeResourceBean drbForMapping : drbList) {
                        DeeResourceBean deeResourceBean =  (DeeResourceBean)BeanUtils.cloneBean(drbForMapping);
                        deeResourceBean.setResource_id(ref_id);
                        deeResourceBean.setResource_name(ref_id);
                        deeResourceBean.setDis_name("字段映射"+ref_id);
                        String resource_code = replaceResourceCodeId(deeResourceBean.getResource_code(), ref_id);
                        deeResourceBean.setResource_code(resource_code);
                        deeAdapterMgr.saveMappingInfo(deeResourceBean);
                    }
                }
                if(!ref_id.equals("")){
                    drbcopy.setRef_id(ref_id);
                    rcode = resourceCode;
                }
                drbcopy.setResource_code(rcode);
                drbcopy.setResource_id(uuid);
                drbcopy.setResource_name(uuid);
                drbcopy.setCreate_time(time());
                if(!ref_id.equals("")){
                    FlowSubBean flowSubBean = getFlowSubBean(drbcopy, fb, drbcopy.getFlowSub().getSort());
                    drbcopy.setFlowSub(flowSubBean);
                }
                deeAdapterMgr.copynsaveAdapter(
                        drbcopy,
                        getFlowSubBean(drbcopy, fb, drbcopy.getFlowSub()
                                .getSort()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 回显任务基础信息
     *
     * @return
     */
    public String edit() {
        try {
            String uuid = request.getParameter("uid");
            if (uuid != null && !uuid.equals("")) {
                FlowBean fb = fbMgr.getFlow(uuid);
                List<FlowSubBean> fsSource = new ArrayList<FlowSubBean>();
                List<FlowSubBean> fsExchange = new ArrayList<FlowSubBean>();
                List<FlowSubBean> fsTarget = new ArrayList<FlowSubBean>();
                for (FlowSubBean fsb : fb.getFlowSubs()) {
                    if (fsb.getSort() >= 1000 && fsb.getSort() < 2000) {
                        fsSource.add(fsb);
                    } else if (fsb.getSort() >= 2000 && fsb.getSort() < 3000) {
                        fsExchange.add(fsb);
                    } else {
                        fsTarget.add(fsb);
                    }
                }

                ComparatorImpl comparator = new ComparatorImpl();
                Collections.sort(fsSource, comparator);
                Collections.sort(fsExchange, comparator);
                Collections.sort(fsTarget, comparator);

                ActionContext.getContext().put("flowBean", fb);
                ActionContext.getContext().put("fsSource", fsSource);
                ActionContext.getContext().put("fsExchange", fsExchange);
                ActionContext.getContext().put("fsTarget", fsTarget);
            } else {
                String flowTypeId = request.getParameter("flowTypeId");
                FlowTypeBean ftb = fbMgr.getFlowTypeBean(flowTypeId);
                FlowBean fb = new FlowBean();
                fb.setFlowType(ftb);
                ActionContext.getContext().put("flowBean", fb);
            }
        } catch (SystemException e) {
            log.error("请检查数据是否存在" + e.getMessage(), e);
        }
        return INPUT;
    }

    /**
     * 保存或更新任务基础信息
     *
     * @return
     */
    public String update() {
        String uuid = request.getParameter("uid");
        String FlowTypeId = request.getParameter("flow_type_id");
        String listener_name = request.getParameter("listener");
        if (StringUtils.isBlank(listener_name)) {
            listener_name = "-1";
        }
        try {
            if (uuid != null && !uuid.equals("")) {
                FlowBean fb = fbMgr.getFlow(uuid);
                if (!fb.getDIS_NAME().trim().equals(flow_name.trim())) {
                    if (!fbMgr.checkName(flow_name.trim())) {
                        return ajaxForwardError("任务名称重复，请重填！");
                    } else {
                        fb.setDIS_NAME(flow_name.trim());
                        fb.setCREATE_TIME(time());
                        fb.setEXT1(listener_name);
                        FlowTypeBean flowTypeBean = new FlowTypeBean();
                        flowTypeBean.setFLOW_TYPE_ID(FlowTypeId);
                        fb.setFlowType(flowTypeBean);
                        fbMgr.update(fb);
                        return ajaxForwardSuccess(getText("msg.operation.success"));
                    }
                } else {
                    fb.setCREATE_TIME(time());
                    fb.setEXT1(listener_name);
                    FlowTypeBean flowTypeBean = new FlowTypeBean();
                    flowTypeBean.setFLOW_TYPE_ID(FlowTypeId);
                    fb.setFlowType(flowTypeBean);
                    fbMgr.update(fb);
                    return ajaxForwardSuccess(getText("msg.operation.success"));
                }
            } else {
                if (!fbMgr.checkName(flow_name.trim())) {
                    return ajaxForwardError("任务名称重复，请重填！");
                } else {
                    FlowBean fb = new FlowBean();
                    fb.setEXETYPE_ID("0");
                    fb.setDIS_NAME(flow_name.trim());
                    fb.setEXT1(listener_name);
                    fb.setCREATE_TIME(time());
                    fb.setMODULE_IDS("10000");

                    FlowTypeBean flowTypeBean = new FlowTypeBean();
                    flowTypeBean.setFLOW_TYPE_ID(FlowTypeId);
                    fb.setFlowType(flowTypeBean);

                    uuid = fbMgr.save(fb);
                    ActionContext.getContext().put("uuid", uuid);
                    return ajaxForwardSuccess(getText("msg.operation.success"));
                }
            }
        } catch (Exception e) {
            log.error("请检查数据是否存在" + e.getMessage(), e);
            return ajaxForwardError("基础信息保存失败");
        }


    }

    /**
     * 回显元数据
     *
     * @return
     * @throws SystemException
     */
    public String editMetaData() throws SystemException {
        String uuid = request.getParameter("uuid");

        if (uuid == null || "".equals(uuid))
            return INPUT;

        FlowBean fb = fbMgr.getFlow(uuid);
        if (fb == null)
            return ajaxForwardError("基础信息不存在！");

        if (fb.getFLOW_META() == null || "".equals(fb.getFLOW_META()))
            return INPUT;

        try {

            List<MetaData> metaList = new ArrayList<MetaData>();
            metaList = parseMeta(fb);
            
            //排序
            Collections.sort(metaList,new ComparatorMetaSort());
            int max = 0;
            //获取最大值
            if(metaList.size() > 0){
            	max = Integer.parseInt(metaList.get(0).getOrderNo());
            	for (int j = 1; j < metaList.size(); j++) {
            		int num = Integer.parseInt(metaList.get(j).getOrderNo());
                	if (num > max ) {
                       max = num;
                	}
                }
            }
            
            //排序
            Collections.sort(metaList,new ComparatorMetaSort());
            
            if (metaList.size() > 0) {
                ActionContext.getContext().put("metaList", metaList);
                ActionContext.getContext().put("metaSize", metaList.size() >= max ? metaList.size() : max);
            }else{
            	ActionContext.getContext().put("metaSize", 1);
            }
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }
        return INPUT;

    }

    /**
     * 解析关联主表字段
     *
     * @return
     * @throws SystemException
     */
    private Map getUnionField(List<Element> tbList) {
        Map mp = new LinkedHashMap();
        String relFields = "";
        for (Element tb : tbList) {
            //解析关联主表字段
            if ("master".equals(tb.attributeValue("tabletype"))
                    && tb.attributeValue("toRelMasterField") != null
                    && !"".equals(tb.attributeValue("toRelMasterField"))) {
                relFields = tb.attributeValue("toRelMasterField");
            }
        }
        if (relFields == null)
            return mp;
        String[] unField = relFields.split("\\|");
        for (String unf : unField) {
            if (unf == null)
                continue;
            String[] fields = unf.split("=");
            if (fields[0] == null || "".equals(fields[0])
                    || fields[1] == null || "".equals(fields[1]))
                continue;
            //（从表表名.字段）对应主表字段
            mp.put(fields[1], fields[0]);
        }
        return mp;
    }

    /**
     * 回显任务参数
     *
     * @return
     * @throws SystemException
     */
    public String editParameters() throws SystemException {
        String uuid = request.getParameter("uuid");
        if (uuid != null && !uuid.equals("")) {
            FlowBean fb = fbMgr.getFlow(uuid);
            if (fb != null) {
                List<ParameterBean> pbList = fbMgr.findParamsByFlowId(uuid);
                ActionContext.getContext().put("pbList", pbList);
            } else {
                return ajaxForwardError("基础信息不存在！");
            }
        }
        return INPUT;
    }

    /**
     * 回显调试页面
     *
     * @return
     */
    public String debug() {
        try {
            String flowId = request.getParameter("flowId");
            String document = request.getParameter("document");
            List<ParameterBean> paraList = fbMgr.findParamsByFlowId(flowId);
            ActionContext.getContext().put("flowId", flowId);
            ActionContext.getContext().put("params", paraList);
            ActionContext.getContext().put("document", document);

        } catch (SystemException e) {
            log.error("打开debug失败：" + e.getMessage(), e);
        }
        return INPUT;
    }

    /**
     * 调试并将调试结果返回调试页面
     *
     * @return
     */
    public String debugRes() {
        String flowId = request.getParameter("flowId");
        String input = request.getParameter("document");
        String result = "";
        boolean hasError = false;

        List<String> list = new ArrayList<String>();
        StringBuffer sbmata = new StringBuffer();
        try {
            String xml = com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil.getInstance().getAllFlowsCfg();
            String deeHome = GenerationCfgUtil.getDEEHome();
            ;
            String workDir = deeHome + "/conf/";
            File file = new File(workDir);
            if (!file.exists()) file.mkdirs();
            file = new File(workDir + "dee.xml");
            if (!file.exists()) file.createNewFile();

            java.io.FileWriter fstream = new FileWriter(file);
            //BufferedWriter writer = new BufferedWriter(fstream);
            //writer.write(new String(xml.getBytes(),"UTF-8"));
            //writer.close();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(xml.getBytes("utf-8"));
            fos.close();
        } catch (Exception e) {
            result += "io出错：\n" + e.getMessage() + "\n";
        }

        Parameters p = new Parameters();
        if (params != null && params.size() > 0) {
            for (FlowParameter para : params) {
                String value = para.getDefaultValue();
                if (value == null) continue;
                p.add(para.getParaName(), value);
            }
        }
        p.add("Paging_pageNumber", Integer.valueOf(request.getParameter("Parameter_Paging_pageNumber")));
        p.add("Paging_pageSize", Integer.valueOf(request.getParameter("Parameter_Paging_pageSize")));

        // 取输入Document
        Document document = null;
        if (input != null && !input.trim().isEmpty()) {
            try {
                document = (Document) new XMLDataSource(input).parse();
            } catch (Throwable e) {
                hasError = true;
                result += "解析输入Document出错：\n" + e.getMessage() + "\n";
            }
        }

        com.seeyon.v3x.dee.Document doc = null;
        TransformContext ctx = null;
        String ctxText = "";
        String columnText = "";
        String metaText = "";
        if (!hasError) {
            // 取Controller并刷新上下文，读取插件XML
            try {
                DEEClient client = new DEEClient();
                client.refreshContext();
                String mata = "";
                doc = client.execute(flowId, p);
                FlowBean fb = fbMgr.getFlow(flowId);
                String flowname = fb.getDIS_NAME();
                List<com.seeyon.v3x.dee.Document.Element> elementList = doc.getRootElement().getChildren();

                for (int lsize = 0; lsize < elementList.size(); lsize++) {
                    String tdname = elementList.get(lsize).getName();
                    StringBuffer sb = new StringBuffer();
                    sb.append(flowname);
                    sb.append(",");
                    if (elementList.get(lsize).getChildren().size() != 0) {

                        sb.append(tdname);
                        sb.append(",");
                    }

                    try {
                        int size = elementList.get(lsize).getChildren().get(0).getChildren().size();

                        if (size > 0) {
                            for (int j = 0; j < size; j++) {
                                sb.append(elementList.get(lsize).getChildren().get(0).getChildren().get(j).getName());
                                if (j < size - 1) {
                                    sb.append(",");
                                } else {
                                    sb.append(".");
                                }


                            }
                            sbmata.append(sb.toString());
                        }


                    } catch (Exception e) {
                    }

                }
                list.add(sbmata.toString());
                result += "输出Document：\n" + doc.toString() +"\n";

                ctx = doc.getContext();
            } catch (InvocationTargetException e) {
                hasError = true;
                Throwable target = e.getTargetException();
                String errorMsg = target.getMessage() == null ? target.toString() : target.getMessage();
                result += errorMsg;
            } catch (Throwable e) {
                hasError = true;
                String errorMsg = e.getMessage() == null ? e.toString() : e.getMessage();
                result += errorMsg;
            }

            try
            {
                Map<String,String> metaMap = metaCheck(flowId,doc);
                if (metaMap != null && metaMap.size()>0)
                {
                    metaText += "元数据配置中：\n";
                    for (String key : metaMap.keySet()) {
                        metaText += key + ",";
                    }

                    metaText += " 字段无映射;\n";
                    result += metaText;
                }

                if (ctx != null) {
                    if (ctx.getParameters().getValue("columnMappingInfo") != null) {
                        columnText += "映射配置中：\n";
                        Map<String, String> colMapList = (Map)ctx.getParameters().getValue("columnMappingInfo");

                        for (String key : colMapList.keySet()) {
                            columnText += key + ",";
                        }

                        columnText += " 源字段无映射;\n";
                        ctx.getParameters().remove("columnMappingInfo");
                        result += columnText;
                    }

                    ctxText += "[";
                    for (String key : ctx.getAttributeNames()) {
                        ctxText += key + ":" + ctx.getAttribute(key) + ",";
                    }
                    ctxText += "]";
                }

            }catch (Exception e)
            {
                hasError = true;
                String errorMsg = e.getMessage() == null ? e.toString() : e.getMessage();
                result += "校验映射配置或元数据出错：\n" + errorMsg;
            }

        }

        //将适配器的调试状态通过json传回前台
        Map<String,String> adapterDes = AdapterDebug.getInstance().getAdapterMap();
        if (adapterDes!=null){
            if (!adapterDes.isEmpty()){
                JSONObject json = JSONObject.fromObject(adapterDes);
                this.setJsonData(json.toString());
            }
        }

        //将记录下来的参数通过json传回前台
        List scriptList = ScriptDebug.getInstance().getParam();
        if(scriptList!=null){
            if (!scriptList.isEmpty())
                this.setDebugFlag("T");
        }


        try {
            this.setError(hasError);
            this.setConsole_mata(list);
            this.setConsole_info(StringEscapeUtils.escapeJavaScript(StringFilter(result)));
            this.setConsole_out(StringEscapeUtils.escapeJavaScript(doc != null ? StringFilter(doc.toString()) : ""));
            if (ctx != null) {
                this.setConsole_args(StringEscapeUtils.escapeJavaScript(doc != null ? StringFilter(ctx.getParameters().toString()) : ""));
            }
            this.setConsole_context(StringEscapeUtils.escapeJavaScript(StringFilter(ctxText)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return ajaxForwardSuccess("操作成功！");
    }

    /**
     * 保存或更新元数据
     *
     * @return
     * @throws SystemException
     */
    public String saveMetadata() throws SystemException {
        String uuid = request.getParameter("uuid");
        FlowBean flow = fbMgr.getFlow(uuid);
        if (flow != null && flow.getFLOW_ID() != null && !flow.getFLOW_ID().equals("")) {
            try {
                StringBuilder sb = new StringBuilder();
                if (meta != null) {
//                    ComparatorMeta comparator = new ComparatorMeta();
                    List<MetaData> md = new ArrayList<MetaData>();
                    for (int i = 0; i < meta.size(); i++) {
                        if (meta.get(i) != null)
                            md.add(meta.get(i));

                    }

                    if(meta.size() > 1){
                        for (int i = 0; i < meta.size(); i++)  {
                            if (meta.get(i) == null || meta.get(i).getFormName() == null ||meta.get(i).getName() == null) continue;
                            for (int j = i + 1; j < meta.size(); j++) {
                                if (meta.get(j) == null || meta.get(j).getFormName() == null||meta.get(j).getName() == null) continue;
                                if (meta.get(i).getFormName().equals(meta.get(j).getFormName()) && meta.get(i).getName().equals(meta.get(j).getName())) {
                                    return ajaxForwardError("同数据集下不能存在相同字段名，请确认修改！");
                                }
                            }
                        }
                    }

                    Collections.sort(md,new  ComparatorMeta());


                    String relMasterField = "";
                    for (MetaData m : md) {
                        if (m != null && !"".equals(m.getRefMainField()) && !"master".equals(m.getFormType())) {
                            relMasterField += m.getRefMainField() + "=" + m.getDbFormName() + "." + m.getName() + "|";
                        }
                    }
                    for (int i = 0; i < md.size(); i++) {
/*						if(i==0)
                        {
							sb.append("<StaticDataSource>").append("<App name=\"").append(request.getParameter("formName"));
							sb.append("\">").append("<TableList>").append("<Table name=\"").append(meta.get(i).getDbFormName());
							sb.append("\" display=\"").append(meta.get(i).getFormDisName()).append("\" tabletype=\"");
							sb.append(meta.get(i).getFormType()).append("\">");
						}
						if(i>0)
						{
							if(!meta.get(i).getDbFormName().equalsIgnoreCase(meta.get(i-1).getDbFormName()))
							{
								sb.append("</Table>");
								sb.append("<Table name=\"").append(meta.get(i).getDbFormName());
								sb.append("\" display=\"").append(meta.get(i).getFormDisName()).append("\" tabletype=\"");
								sb.append(meta.get(i).getFormType()).append("\">");
							}
						}
							sb.append("<Field id=\"").append(meta.get(i).getId()).append("\" name=\"");
							sb.append(meta.get(i).getName()).append("\" display=\"");
							sb.append(meta.get(i).getDisplay()).append("\" fieldtype=\"");
							sb.append(meta.get(i).getFieldType()).append("\" fieldlength=\"");
							sb.append(meta.get(i).getFieldLength()).append("\" is_null=\"");
							sb.append(meta.get(i).getIsNull()).append("\" is_primary=\"");
							sb.append(meta.get(i).getIsPrimary()).append("\"/>");
							
						if(i==meta.size()-1)
						{
							sb.append("</Table>").append("</TableList>").append("</App>").append("</StaticDataSource>");
						}*/

                        if (i == 0) {
                            sb.append("<StaticDataSource>").append("<App name=\"").append(md.get(i).getFormName());
                            sb.append("\">").append("<TableList>").append("<Table name=\"").append(md.get(i).getDbFormName());
                            sb.append("\" display=\"").append(md.get(i).getFormDisName());
                            sb.append("\" toRelMasterField=\"");
                            if ("master".equals(md.get(i).getFormType()))
                                sb.append(relMasterField);
                            sb.append("\" tabletype=\"").append(md.get(i).getFormType()).append("\">");
                        }
                        if (i > 0) {
                            if (!md.get(i).getFormName().equalsIgnoreCase(md.get(i - 1).getFormName())) {
                                sb.append("</Table>").append("</TableList>").append("</App>");
                                sb.append("<App name=\"").append(md.get(i).getFormName());
                                sb.append("\">").append("<TableList>").append("<Table name=\"").append(md.get(i).getDbFormName());
                                sb.append("\" display=\"").append(md.get(i).getFormDisName());
                                sb.append("\" toRelMasterField=\"");
                                if ("master".equals(md.get(i).getFormType()))
                                    sb.append(relMasterField);
                                sb.append("\" tabletype=\"").append(md.get(i).getFormType()).append("\">");
                            } else {
                                if (!md.get(i).getDbFormName().equalsIgnoreCase(md.get(i - 1).getDbFormName())) {
                                    sb.append("</Table>");
                                    sb.append("<Table name=\"").append(md.get(i).getDbFormName());
                                    sb.append("\" display=\"").append(md.get(i).getFormDisName());
                                    sb.append("\" toRelMasterField=\"");
                                    if ("master".equals(md.get(i).getFormType()))
                                        sb.append(relMasterField);
                                    sb.append("\" tabletype=\"").append(md.get(i).getFormType()).append("\">");
                                }
                            }
                        }

                        sb.append("<Field id=\"").append(md.get(i).getName()).append("\" name=\"");
                        sb.append(md.get(i).getName()).append("\" display=\"");
                        sb.append(md.get(i).getDisplay()).append("\" fieldtype=\"");
                        sb.append(md.get(i).getFieldType()).append("\" fieldlength=\"");
                        sb.append(md.get(i).getFieldLength()).append("\" is_null=\"");
                        sb.append(md.get(i).getIsNull()).append("\" is_primary=\"");
                        sb.append(md.get(i).getIsPrimary()).append("\" orderNo=\"");//增加排序号
                        sb.append(md.get(i).getOrderNo()).append("\"/>");

                        if (i == md.size() - 1) {
                            sb.append("</Table>").append("</TableList>").append("</App>").append("</StaticDataSource>");
                        }

                    }
                }

                if (sb.toString() != null && !sb.toString().equals("")) {
                    String fMeta = sb.toString().toLowerCase();
                    String fKey = "";
                    int eInt = 0;
                    boolean fFlag = false;
                    List<String> bL = getWriterKey(uuid);
                    if (bL != null && bL.size() > 0) {
                        while (fMeta.indexOf("<table ") > -1) {
                            fMeta = fMeta.substring(fMeta.indexOf("<table "));
                            fMeta = fMeta.substring(fMeta.indexOf("\"") + 1);
                            eInt = fMeta.indexOf("\"");
                            fKey = fMeta.substring(0, eInt);
                            for (String b : bL) {
                                if (b != null && b.equalsIgnoreCase(fKey)) {
                                    fFlag = true;
                                    break;
                                }
                            }
                            if (!fFlag) {
                                return ajaxForwardError("输出元数据中表名与writer中输入不一致！");
                            }
                        }
                    }
                }

                flow.setFLOW_META(sb.toString());
                fbMgr.update(flow);
                return ajaxForwardSuccess(getText("msg.operation.success"));


            } catch (Exception e) {
                log.error("元数据保存失败：" + e.getMessage(), e);
                return ajaxForwardError("元数据保存失败！");
            }
        } else
            return ajaxForwardError("请先保存基础信息！");
    }

    private List<String> getWriterKey(String flowId) {
        List<String> sList = new ArrayList<String>();
        if (flowId != null && !"".equalsIgnoreCase(flowId)) {
            List<DeeResourceBean> resourceList;
            try {
                resourceList = fbMgr.findResourceList(flowId);
                for (DeeResourceBean drBean : resourceList) {
                    if (drBean != null
                            && "2".equalsIgnoreCase(drBean
                            .getResource_template_id())) {
                        JDBCWriterBean writerbean = (JDBCWriterBean) new ConvertDeeResourceBean(
                                drBean).getResource();
                        Iterator it = writerbean.getMap().keySet().iterator();
                        while (it.hasNext()) {
                            sList.add((String) it.next());
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return sList;
    }

    /**
     * 保存或更新任务参数
     *
     * @return
     */
    public String saveParams() {
        try {
            String uuid = request.getParameter("uuid");
            FlowBean flow = fbMgr.getFlow(uuid);
            if (flow != null && flow.getFLOW_ID() != null && !flow.getFLOW_ID().equals("")) {
                if (params != null) {
                    Map<String, String> paraMap = new HashMap<String, String>();
                    for (FlowParameter fp : params) {
                        if (fp == null)
                            continue;
                        if (paraMap.containsKey(fp.getParaName()) && StringUtils.isNotBlank(fp.getParaName()))
                            return ajaxForwardError("参数名称重复！");
                        if (paraMap.containsValue(fp.getDisName()) && StringUtils.isNotBlank(fp.getDisName()))
                            return ajaxForwardError("参数显示名称重复！");
                        paraMap.put(fp.getParaName(), fp.getDisName());
                    }
                }
                fbMgr.saveOrUpdateParams(params, uuid);
                return ajaxForwardSuccess(getText("msg.operation.success"));
            } else
                return ajaxForwardError("请先保存基础信息！");
        } catch (SystemException e) {
            log.error("任务参数保存失败：" + e.getMessage(), e);
            return ajaxForwardError("任务参数保存失败保存失败！");
        }

    }

    /**
     * 获取系统时间
     *
     * @return 系统时间
     */
    public String time() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 改变适配器执行顺序
     *
     * @return
     */
    public String changeOrder() {
        try {
            System.out.println(order);
            JSONObject jsonobj = JSONObject.fromObject(order);
            int start = jsonobj.getInt("start");
            int end = jsonobj.getInt("end");
            String flowId = jsonobj.getString("flowId");
            String resId = jsonobj.getString("resId");
            String proName = jsonobj.getString("proName");
            fbMgr.updateOrder(start, end, flowId, resId, proName);
            return ajaxForwardSuccess(getText("msg.operation.success"));
        } catch (SystemException e) {
            log.error("拖动排序失败：" + e.getMessage(), e);
            return ajaxForwardError("拖动排序失败");
        }

    }

    /**
     * 删除适配器
     *
     * @return
     */
    public String deleteResource() {
        try {
            JSONObject jsonobj = JSONObject.fromObject(order);
            String flowId = jsonobj.getString("flowId");
            String resId = jsonobj.getString("resId");
            fbMgr.deleteRes(flowId, resId);
            return ajaxForwardSuccess(getText("msg.operation.success"));
        } catch (SystemException e) {
            log.error("删除适配器失败：" + e.getMessage(), e);
            return ajaxForwardError("删除适配器失败");
        }
    }

    public void checkProcess() {
        String ids = request.getParameter("ids");
        String[] idsArray = ids.split(",");
        try {
            response.getWriter().write(fbMgr.checkProcessOnly(idsArray));

        } catch (SystemException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    
    /**
     * 系统定义方法异步获取(取消使用）
     * @param request
     * @param response
     * @return
     */
    public  void getSystemMethods(){
    	response.setContentType("text/html;charset=utf-8");
    	String id = request.getParameter("id");
    	String type = request.getParameter("type");
     
    	String jsons = "";
    	if(StringUtils.isBlank(id)){
    		id = "0";
    	}
    	if(StringUtils.isBlank(type)){
    		type = "system";
    	}
    	List<Map<String,String>> fs = null;
    	//如果是系统函数或者根节点，调用
    	if("system".equals(type)){
    		fs = functionUtil.getFunsByPid(id);
    	}
    	
    	//如果是用户自定义函数,调用
    	if("user".equals(type)){
    		try {
				fs = codeLibMgr.getUserPackAndClass(id);
			} catch (SystemException e) {
				 log.error(e.getMessage());
			}
    	}
    	if(fs!=null){
			JSONArray js = new JSONArray();
			js.addAll(fs);
			jsons = js.toString();
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(jsons);	
		} catch (IOException e) {
			 log.error(e.getMessage());
		}
		
    }

    /**
     * 过滤特殊字符串'"
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String StringFilter(String str) {
        return str.replaceAll("'", "").replaceAll("\"", "");
    }

    public Map metaCheck(String flowId , com.seeyon.v3x.dee.Document document) throws Exception{
        Map metaMap = new HashMap();
        FlowBean fb = fbMgr.getFlow(flowId);
        if (fb == null)
            return null;

        if (fb.getFLOW_META() == null || "".equals(fb.getFLOW_META()))
            return null;

        if (document != null && document.getRootElement().getChildren().size()>0) {
        	com.seeyon.v3x.dee.Document root = new XMLDataSource( DocumentUtil.toXML(document).toLowerCase()).parse();
            List<MetaData> metaList = parseMeta(fb);
            for (MetaData md : metaList) {
                com.seeyon.v3x.dee.Document.Element table= root.getRootElement().getChild(md.getDbFormName().toLowerCase());
                if (table != null && table.getChildren().size()>0)
                {
                    if (table.getChildren().get(0).getChild(md.getId().toLowerCase())==null)
                        metaMap.put(md.getDbFormName()+"/"+md.getId() ,"");
                }
                else
                {
                    metaMap.put(md.getDbFormName()+"/"+md.getId() ,"");
                }
            }
        }
        return metaMap;
    }

    private List<MetaData> parseMeta(FlowBean fb) throws DocumentException {
        Document document = DocumentHelper.parseText(fb.getFLOW_META());
        Element root = document.getRootElement();
        List<MetaData> metaList = new ArrayList<MetaData>();
        List<Element> apps = root.selectNodes("/StaticDataSource/App");
        MetaData meta = null;
        for (Element app : apps) {
            if (app == null)
                continue;
            List<Element> tbLists = app.elements("TableList");
            int index = 1;
            for (Element tbList : tbLists) {
                if (tbList == null)
                    continue;
                List<Element> tbs = tbList.elements("Table");
                //解析关联主表字段
                Map relMasterMap = getUnionField(tbs);
                for (Element tb : tbs) {
                    if (tb == null)
                        continue;
                    List<Element> fields = tb.elements("Field");
                    for (Element field : fields) {
                        if (field == null)
                            continue;
                        meta = new MetaData();
                        meta.setFormName(app.attributeValue("name"));
                        meta.setDbFormName(tb.attributeValue("name"));
                        meta.setFormDisName(tb.attributeValue("display"));
                        meta.setFormType(tb.attributeValue("tabletype"));
                        //将字段名作为字段ID
                        meta.setId(field.attributeValue("name"));
                        //获取表名.字段名
                        String tbFieldName = tb.attributeValue("name") + "." + field.attributeValue("name");
                        String toRefMasterField = (String) relMasterMap.get(tbFieldName);
                        //设置从表关联主表字段
                        meta.setRefMainField(toRefMasterField == null ? "" : toRefMasterField);

                        meta.setName(field.attributeValue("name"));
                        meta.setDisplay(field.attributeValue("display"));
                        meta.setFieldType(field.attributeValue("fieldtype"));
                        meta.setFieldLength(field.attributeValue("fieldlength"));
                        meta.setIsNull(field.attributeValue("is_null"));
                        meta.setIsPrimary(field.attributeValue("is_primary"));
                        meta.setOrderNo(field.attributeValue("orderNo")!=null && !"".equals(field.attributeValue("orderNo"))?field.attributeValue("orderNo"): index + "");
                        metaList.add(meta);
                        index++;
                    }

                }
            }
        }

        return metaList;
    }

    public String scriptDebug()
    {
        List<ParamBean> dataList = new ArrayList<ParamBean>();
        List<ParamBean> scriptList = ScriptDebug.getInstance().getParam();
        try{
            for (ParamBean pb : scriptList)
            {
                pb.setAdapterName(deeAdapterMgr.getAdapterNameById(pb.getAdapterName()));
                dataList.add(pb);
            }

        }catch (Exception e)
        {
            log.error("参数调试获取适配器名称异常：" + e.getMessage(), e);
        }
        ActionContext.getContext().put("scriptList", dataList);
        ScriptDebug.getInstance().getParam().clear();
        return INPUT;
    }

    public String getFlow_name() {
        return flow_name;
    }

    public void setFlow_name(String flow_name) {
        this.flow_name = flow_name;
    }

    public List<MetaData> getMeta() {
        return meta;
    }

    public void setMeta(List<MetaData> meta) {
        this.meta = meta;
    }

    public List<FlowParameter> getParams() {
        return params;
    }

    public void setParams(List<FlowParameter> params) {
        this.params = params;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getConsole_info() {
        return console_info;
    }

    public void setConsole_info(String console_info) {
        this.console_info = console_info;
    }

    public String getConsole_out() {
        return console_out;
    }

    public void setConsole_out(String console_out) {
        this.console_out = console_out;
    }

    public String getConsole_args() {
        return console_args;
    }

    public void setConsole_args(String console_args) {
        this.console_args = console_args;
    }

    public String getConsole_context() {
        return console_context;
    }

    public void setConsole_context(String console_context) {
        this.console_context = console_context;
    }

    public List<String> getConsole_mata() {
        return console_mata;
    }

    public void setConsole_mata(List<String> console_mata) {
        this.console_mata = console_mata;
    }

    public String getIds() {
        return ids;
    }


    public void setIds(String ids) {
        this.ids = ids;
    }

    public DeeAdapterMgr getDeeAdapterMgr() {
        return deeAdapterMgr;
    }

    public void setDeeAdapterMgr(DeeAdapterMgr deeAdapterMgr) {
        this.deeAdapterMgr = deeAdapterMgr;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getDebugFlag() {
        return debugFlag;
    }

    public void setDebugFlag(String debugFlag) {
        this.debugFlag = debugFlag;
    }
}
