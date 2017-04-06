package com.seeyon.dee.configurator.action;

import com.seeyon.dee.configurator.dao.SyncDao;
import com.seeyon.dee.configurator.dto.AdapterTree;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.dee.configurator.mgr.RedoMgr;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.db.redo.dao.DEESyncDAO;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBeanLog;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.context.AdapterKeyName;
import com.seeyon.v3x.dee.debug.AdapterBeanLog;
import com.seeyon.v3x.dee.util.FileUtil;
import dwz.framework.common.action.BaseAction;
import dwz.framework.common.pagination.Page;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class RedoAction extends BaseAction {

    private static final long serialVersionUID = 2203193246289391856L;
    
    private static Logger logger = Logger.getLogger(RedoAction.class.getName());

    @Autowired
    @Qualifier("redoMgr")
    private RedoMgr redoMgr;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    @Autowired
    @Qualifier("flowBaseMgr")
    private FlowBaseMgr flowBaseMgr;
    
    private Page<SyncBeanLog> syncPage;
    
    private Page<RedoBean> redoPage;
    
    private String sync_id;
    private String flowName = "";
    private String state = "";
    private String startDate = "";
    private String endDate = "";
    private String jsonData;

    private String redo_id;
    private RedoBean redoBean;
    private List<TreeNode> docCodeList;
    

    public void test(){

        try {
            Class.forName("dddd");
        } catch (ClassNotFoundException e) {
            //不可以打印堆栈
            logger.error(e);

            //可以打印堆栈
            logger.error(e.getMessage(),e);
            e.printStackTrace();
        }

    }

    /**
     * 重发后刷新同步列表
     * 
     * @return
     * @throws IOException 
     */
  /*  public void refreshSync() throws IOException{
        SyncBean syncBean = new SyncBean();
        syncBean=redoMgr.selectSync(sync_id);
        ArrayList<SyncBean> list=new ArrayList<SyncBean>();
        list.add(syncBean);
       
        PrintWriter out = response.getWriter();
        out.print(syncBean.getSync_state());
       
    }
    */
    /**
     * 获取同步列表
     * 
     * @return
     */
    public String syncList() {
        String where = "obj.flow.DIS_NAME like '%" + flowName.replaceAll("%", "\\\\%") + "%'";
        String hql = PageUtil.createHql(SyncBean.class, where,
                StringUtils.isEmpty(getOrderField())?"sync_time":getOrderField(), 
                StringUtils.isEmpty(getOrderDirection())?"desc":getOrderDirection());
        String hqlCount = PageUtil.createHqlCount(SyncBean.class, where);
//        try {
//            this.syncPage = redoMgr.listSync(hql, hqlCount, getPageNum(), getNumPerPage(), null);
//        } catch (SystemException e) {
//            logger.error(e.getMessage(), e);
//        }
        return INPUT;
    }
    
    /**
     * 获取异常信息列表
     * 
     * @return
     */
    public String syncListLog() {
        String sql = "select a.flow_id,b.dis_name,a.sync_id,a.sync_state,a.sync_time,a.exec_time,c.redo_id,"
        		+ "c.counter,d.form_flow_name,d.operate_person,d.flow_action from "
        		+ "dee_sync_history a left join dee_flow b on a.flow_id = b.flow_id left join "
        		+ "dee_redo c on a.sync_id = c.sync_id left join form_flow_history d on "
        		+ "a.sync_id = d.flow_sync_id where b.dis_name like '%" + 
        		flowName.replaceAll("%", "\\\\%") + "%'";
        String sqlCount = "select count(*) from dee_sync_history a left join dee_flow b on "
        		+ "a.flow_id = b.flow_id left join dee_redo c on a.sync_id = c.sync_id left "
        		+ "join form_flow_history d on a.sync_id = d.flow_sync_id where b.dis_name like '%" 
        		+ flowName.replaceAll("%", "\\\\%") + "%'";
        if(!"".equals(state)){
        	sql = sql + " and a.sync_state=" + state + " ";
        	sqlCount = sqlCount + " and a.sync_state=" + state + " ";
        }
        if(!"".equals(startDate) && !"".equals(endDate)){
        	sql = sql + " and a.sync_time between '" + startDate.replace("T", " ") + "' and '" + endDate.replace("T", " ") + "' ";
        	sqlCount = sqlCount + " and a.sync_time between '" + startDate.replace("T", " ") + "' and '" + endDate.replace("T", " ") + "' ";
        }
        if("".equals(startDate) && !"".equals(endDate)){
        	sql = sql + " and a.sync_time < '" + endDate.replace("T", " ") + "' ";
        	sqlCount = sqlCount + " and a.sync_time between '" + startDate.replace("T", " ") + "' and '" + endDate.replace("T", " ") + "' ";
        }
        if(!"".equals(startDate) && "".equals(endDate)){
        	sql = sql + " and a.sync_time > '" + startDate.replace("T", " ") + "' ";
        	sqlCount = sqlCount + " and a.sync_time between '" + startDate.replace("T", " ") + "' and '" + endDate.replace("T", " ") + "' ";
        }
        sql = sql + " order by a.sync_time desc";
        try {
            this.syncPage = redoMgr.listSyncLog(sql, sqlCount, getPageNum(), getNumPerPage());
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        return INPUT;
    }
    
    /**
     * 重发后获取新的重发信息
     * 
     * @return
     */
    public String findNewRedo(){
        try {
            redoBean = redoMgr.get(redo_id);
            JsonConfig jsonConfig = new JsonConfig();  
            jsonConfig.setJsonPropertyFilter( new PropertyFilter(){  
               public boolean apply( Object source, String name, Object value ){  
                  // return true to skip name
                  if (source instanceof Parameters || source instanceof SyncBean) {
                      return true;
                  }
                  return false;
               }
            });  
            JSON json = JSONSerializer.toJSON(redoBean, jsonConfig);
            this.jsonData = json.toString();
           

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ajaxForwardSuccess("");

    }
    /**
     * 获取重发列表
     * 
     * @return
     */
    public String redoList() {
        try {
            List<RedoBean> results = new ArrayList<RedoBean>();
            List<RedoBean> redoList = redoMgr.listRedoBySync(sync_id);
            
            for (int i = 0; i < redoList.size(); i++) {
                redoList.get(i).setSync_id(sync_id);
                int index = i;
                int start = (getPageNum()-1) * getNumPerPage();
                int end = getPageNum() * getNumPerPage() - 1;
                if (index >= start && index <= end) {
                    results.add(redoList.get(i));
                }
            }

            this.redoPage = PageUtil.getPage(getPageNum(), getNumPerPage(),
                    results, redoList.size());
            
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }

        return INPUT;
    }

    /**
     * 删除重发记录
     * 
     * @return
     */
    public String delRedo() {
        try {
            if (redoMgr.delRedo(redo_id) > 0) {
                return this.ajaxForwardSuccess("删除数据成功!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.ajaxForwardError("删除数据异常!");
        }
        return this.ajaxForwardError("删除记录不存在!");
    }
    
    /**
     * 删除同步记录
     * 
     * @return
     */
    public String delSync() {
        try {
            if (redoMgr.delSync(sync_id) > 0) {
                return this.ajaxForwardSuccess("删除数据成功!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.ajaxForwardError("删除数据异常!");
        }
        return this.ajaxForwardError("删除记录不存在");
    }
    
    /**
     * 执行重发
     * 
     * @return
     */
    public String executeRedo() {
    	int i = 0;
    	String errorIds = "";
        try {
        	DEEClient client = new DEEClient();
            GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
            client.refreshContext();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }        
        String[] idArray = redo_id.split(",");
        for (String id : idArray) {
        	try {
				redoMgr.executeRedo(id);
			} catch (Exception e) {
				if(i == 0){
					errorIds = id;
				}else{
					errorIds = errorIds + "," + id;
				}
				i++;
				logger.error(e.getMessage(), e);
			}
        }
        if(i == 0){
            return ajaxForwardSuccess("重发任务成功！" + ";" + errorIds);
        }else{
            return ajaxForwardError("重发任务失败！失败数量：" + i + ";" + errorIds);
        }
    }
    
    /**
     * 查看重发详情
     * 
     * @return
     */
    public String redoDetail() {
        List<Object> listjson = new ArrayList<Object>();
        try {
            this.redoBean = redoMgr.get(redo_id);
            this.docCodeList = parseDocCode(redoBean.getDoc_code());
            listjson.add(redoBean.getDoc_code());
            listjson.add(redoBean.getSyncBean().getFlow().getDIS_NAME());
            listjson.add(docCodeList);

            JSON json = JSONSerializer.toJSON(listjson);

            this.jsonData = json.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ajaxForwardSuccess("");
    }
    
    /**
     * 获取任务适配器下拉树
     * 
     * @return
     */
    public String getTaskTree() {
        AdapterKeyName adapterKeyName = AdapterKeyName.getInstance();
        List<Object> listjson = new ArrayList<Object>();
    	List<AdapterTree> list = new ArrayList<AdapterTree>();
        try {
        	String[] strs = sync_id.split(",");
        	String id = strs[1];
        	String flowId = strs[0];
        	String path = "";
        	path = adapterKeyName.getDeeHome() + "flowLogs" + File.separator + adapterKeyName.getFlowMap().get(flowId) + "_" + flowId + File.separator;
        	Properties prop = new Properties();
            try {
                path = path + id + ".properties";
                File file = new File(path);
                if (!file.exists()){
            		this.jsonData = null;
                	return ajaxForwardSuccess("");
                }
                InputStream fis = new FileInputStream(file);
                BufferedReader bf = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
                prop.load(bf);
                fis.close();
            	AdapterTree root = new AdapterTree();
            	root.setNodeId("0");
            	String flowName = adapterKeyName.getFlowMap().get(flowId);
            	root.setNodeName(flowName);
            	root.setNodePId("-1");
            	root.setState("-1");
            	list.add(root);
            	AdapterTree star = new AdapterTree();
            	star.setNodeId("startData");
            	star.setNodeName("来源数据");
            	star.setNodePId("0");
            	star.setState("-1");
            	star.setStrIds(sync_id);
            	list.add(star);
            	Map<Integer, AdapterTree> map = new HashMap<Integer, AdapterTree>();
            	Enumeration enums = prop.propertyNames();
            	while(enums.hasMoreElements()){
            	    String key = (String)enums.nextElement();
            	    if(key.contains("_") && key.contains(".state")){
                		AdapterTree at = new AdapterTree();
                		at.setNodeId(key);
                		at.setNodeName(key.split(",")[1].replace(".state", ""));
                		at.setNodePId("0");
                		at.setState(prop.getProperty(key));
                		at.setStrIds(sync_id);
                		map.put(Integer.parseInt(key.split(",")[0].replace("adapter_", "")), at);
            	    }
            	}
            	for(int i = 1; i <= map.size(); i++){
            		list.add(map.get(i));
            	}
            	AdapterTree end = new AdapterTree();
            	end.setNodeId("endData");
            	end.setNodeName("输出数据");
            	end.setNodePId("0");
            	end.setState("-1");
            	end.setStrIds(sync_id);
        		list.add(end);
            } catch (Exception e) {
    			e.printStackTrace();
    		}
            listjson.add(list);
            JSON json = JSONSerializer.toJSON(listjson);
            this.jsonData = json.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ajaxForwardSuccess("");
    }
    
    /**
     * 获取任务适配器详细信息
     * 
     * @return
     */
    public String getAdapterDetail() {
        AdapterKeyName adapterKeyName = AdapterKeyName.getInstance();
        List<Object> listjson = new ArrayList<Object>();
    	List<AdapterBeanLog> list = new ArrayList<AdapterBeanLog>();
    	String[] strs = sync_id.split(",");
    	if(!(strs.length == 3 || strs.length == 4)){
    		this.jsonData = null;
    		return ajaxForwardSuccess("");
    	}
    	String id = strs[1];
    	String adapterName = strs[2];
    	String flowId = strs[0];
    	String path = "";
    	path = adapterKeyName.getDeeHome() + "flowLogs" + File.separator + adapterKeyName.getFlowMap().get(flowId) + "_" + flowId + File.separator;
    	Properties prop = new Properties();
        try {
            path = path + id + ".properties";
            File file = new File(path);
            if (!file.exists()){
        		this.jsonData = null;
            	return ajaxForwardSuccess("");
            }
            InputStream fis = new FileInputStream(file);
            BufferedReader bf = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
            prop.load(bf);
            fis.close();
			if (adapterName.contains("_")) {
                String name = adapterName + "," + strs[3].substring(0, strs[3].length() - 6);
				AdapterBeanLog ab = new AdapterBeanLog(adapterName, 1, prop.getProperty(name + ".data"), prop.getProperty(name + ".parm"));
				list.add(ab);
			} else {
				AdapterBeanLog ab = new AdapterBeanLog(adapterName, 1, prop.getProperty(adapterName + ".data"),
						prop.getProperty(adapterName + ".parm"));
				list.add(ab);
			}
			listjson.add(list);
            JSON json = JSONSerializer.toJSON(listjson);
            this.jsonData = json.toString();
        } catch (Exception e) {
			e.printStackTrace();
		}
    	return ajaxForwardSuccess("");
    }
    
    @SuppressWarnings("rawtypes")
    private List<TreeNode> parseDocCode(String doc_code) throws Exception {
        Document document = null;
        
        if (doc_code.indexOf("<") < 0) {    // 对象序列化字符串
            ByteArrayInputStream bis = new ByteArrayInputStream(doc_code.getBytes());
            ObjectInputStream ois = new ObjectInputStream(bis);
            document = (Document) ois.readObject();
            ois.close();
            bis.close();
        } else {    // xml字符串
            SAXReader reader = new SAXReader();
            InputStream inputStream = new ByteArrayInputStream(doc_code.getBytes("UTF-8"));
            document = reader.read(inputStream);
        }
        
        Element root = document.getRootElement();
        List<TreeNode> list = new ArrayList<TreeNode>();
        
        int index = 1;
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            TreeNode node = new TreeNode();
            node.setNodeId(index+"");
            node.setNodePId("0");
            node.setNodeName(element.getName());
            list.add(node);
            int index1 = 1;
            for (Iterator j = element.elementIterator(); j.hasNext();) {
                Element element1 = (Element)j.next();
                TreeNode node1 = new TreeNode();
                node1.setNodeId(index+"-"+index1);
                node1.setNodePId(node.getNodeId());
                node1.setNodeName(element1.getName());
                list.add(node1);
                int index2 = 1;
                for (Iterator k = element1.elementIterator(); k.hasNext();) {
                    Element element2 = (Element)k.next();
                    TreeNode node2 = new TreeNode();
                    node2.setNodeId(index+"-"+index1+"-"+index2);
                    node2.setNodePId(node1.getNodeId());
                    StringBuffer buffer = new StringBuffer();
                    for (int dIndex=0,length=element2.getName().length(); dIndex<40-length*2; dIndex++) {
                        buffer.append("-");
                    }
                    node2.setNodeName(element2.getName() + buffer + element2.getStringValue());
                    node2.setNodeName(node2.getNodeName().replaceAll("[\\t\\n\\r]", ""));
                    list.add(node2);
                    index2++;
                }
                index1++;
            }
            index++;
        }
        return list;
    }
    
    /**
     * 过滤特殊字符串'"
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String StringFilter(String str){   

        return str.replaceAll("'", "").replaceAll("\"", "");
    }   

    public Page<SyncBeanLog> getSyncPage() {
        return syncPage;
    }

    public void setSyncPage(Page<SyncBeanLog> syncPage) {
        this.syncPage = syncPage;
    }

    public String getSync_id() {
        return sync_id;
    }

    public void setSync_id(String sync_id) {
        this.sync_id = sync_id;
    }
    
    public String getRedo_id() {
        return redo_id;
    }

    public void setRedo_id(String redo_id) {
        this.redo_id = redo_id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public RedoBean getRedoBean() {
        return redoBean;
    }

    public void setRedoBean(RedoBean redoBean) {
        this.redoBean = redoBean;
    }

    public List<TreeNode> getDocCodeList() {
        return docCodeList;
    }

    public void setDocCodeList(List<TreeNode> docCodeList) {
        this.docCodeList = docCodeList;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
    
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Page<RedoBean> getRedoPage() {
        return redoPage;
    }

    public void setRedoPage(Page<RedoBean> redoPage) {
        this.redoPage = redoPage;
    }
}
