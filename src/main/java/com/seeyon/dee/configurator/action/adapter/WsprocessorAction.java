package com.seeyon.dee.configurator.action.adapter;

import java.util.*;

import com.ibm.wsdl.PartImpl;
import com.seeyon.dee.configurator.dto.TreeNode;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.WSProcessorBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;
import ws.handleWebservice.bean.ParameterInfo;
import ws.handleWebservice.service.BmWsTestBoImpl;
import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

/**
 * WebService action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-14下午10:50:06
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class WsprocessorAction extends BaseAction {

    private static final long serialVersionUID = -7592660749248084296L;
    
    private static Logger logger = Logger.getLogger(WsprocessorAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String WS_VIEW = "wsprocesser";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private List<KeyValue> items;
    
    private WSProcessorBean wsBean;
    private DeeResourceBean bean;

    private String jsonData;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    /**
     * 保存
     * 
     * @return
     */
    public String save() {
    	if (items == null) {
    		items = new ArrayList<KeyValue>();
    	}

    	if (wsBean == null || bean == null) {
    		return ajaxForwardError("系统错误！");
    	}

    	if (!toDeeResource()) {
    		return ajaxForwardError(Constants.PARAMSCHECKMSG);
    	}

    	if(org.apache.commons.lang.StringUtils.isNotBlank(wsBean.getUsername())&&org.apache.commons.lang.StringUtils.isNotBlank(wsBean.getPassword()))
    	{	
    		try
    		{
    			String[] usrName =  wsBean.getUsername().split(":");
    			String[] pssWord = 	wsBean.getPassword().split(":");
    			if((usrName.length!=1&&usrName.length!=3)||(pssWord.length!=1&&pssWord.length!=3))
    				return ajaxForwardError("用户名密码格式错误！");
    			if((usrName.length==1&&pssWord.length!=1)||(pssWord.length==1&&usrName.length!=1))
    				return ajaxForwardError("用户名密码格式错误！");
    			if(usrName.length==3&&pssWord.length==3&&!usrName[0].equals(pssWord[0]))
    				return ajaxForwardError("用户名密码格式错误！");
    		}catch(Exception e)
    		{
    			logger.error(e.getMessage(), e);
    			return ajaxForwardError("用户名密码格式错误！");
    		}
    	}
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(wsBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(wsBean);
                drb.setDis_name(bean.getDis_name());
                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        }
    }

    public String autoAnalysis(){
        return "autoAnalysis";
    }


     /*
		解析service生成相应带方法与参数的树
 	*/
    public List treeSet(List<Service> listService,Definition def){
        List<Port> ports = new ArrayList<Port>();
        List<TreeNode> tableList = new LinkedList<TreeNode>();
        Port port;
        try{
            for(Service service:listService){
                TreeNode wsNode = new TreeNode();
                String parentId = UuidUtil.uuid();
                wsNode.setNodePId("-1");
                wsNode.setNodeId(parentId);
                wsNode.setNodeName(service.getQName().getLocalPart());
                wsNode.setNodeType("1");
                wsNode.setHasDynChild("true");
                wsNode.setNodeNoCheck("true");
                tableList.add(wsNode);

                Map mapPort = service.getPorts();
                Iterator portIter = mapPort.keySet().iterator();

                while (portIter.hasNext()){
                    Object key = portIter.next();
                    port = (Port) mapPort.get(key);
                    ports.add(port);
                }
                for(int x = 0 ;x < ports.size();x++){
                    List methodName = new ArrayList();
                    Binding binding = ports.get(x).getBinding();
                    PortType portType = binding.getPortType();
                    List operations = portType.getOperations();
                    for(int i = 0 ; i < operations.size() ; i++){
                        Operation operation = (Operation)operations.get(i);
                        methodName.add(operation.getName());
                    }
                    String Id = UuidUtil.uuid();
                    wsNode = new TreeNode();
                    wsNode.setNodePId(parentId);
                    wsNode.setNodeId(Id);
                    wsNode.setNodeName(ports.get(x).getName());
                    wsNode.setNodeType("1");
                    wsNode.setHasDynChild("true");
                    wsNode.setNodeNoCheck("true");
                    tableList.add(wsNode);
                    for(int n = 0 ;n < methodName.size() ; n++){
                        String sonId = UuidUtil.uuid();
                        wsNode = new TreeNode();
                        wsNode.setNodePId(Id);
                        wsNode.setNodeId(sonId);
                        wsNode.setNodeName(methodName.get(n).toString());
                        wsNode.setNodeType("1");
                        wsNode.setHasDynChild("true");
                        wsNode.setNodeNoCheck("true");
                        tableList.add(wsNode);
                        //如果是c#发布的ws
                        if(def.getTypes().getExtensibilityElements().get(0).toString().indexOf("xs") == -1){
                            Operation operation = (Operation)operations.get(n);
                            Map paraMap =  operation.getInput().getMessage().getParts();
                            Iterator paraIter = paraMap.keySet().iterator();
                            while (paraIter.hasNext()){
                                Object key = paraIter.next();
                                PartImpl para =(PartImpl) paraMap.get(key);
                                String grandsonId = UuidUtil.uuid();
                                wsNode = new TreeNode();
                                wsNode.setNodePId(sonId);
                                wsNode.setNodeId(grandsonId);
                                wsNode.setNodeName(para.getName());
                                wsNode.setNodeType("1");
                                wsNode.setHasDynChild("true");
                                wsNode.setNodeNoCheck("true");
                                tableList.add(wsNode);
                            }
                        }
                        else{//如果是axis2方式发布的ws
                            List<ParameterInfo> parameterInfoList =  new BmWsTestBoImpl().getParamByMethodNameAndWsUrl(methodName.get(n).toString(), def);
                            for(int f = 0 ;f < parameterInfoList.size(); f++){
                                String grandsonId = UuidUtil.uuid();
                                wsNode = new TreeNode();
                                wsNode.setNodePId(sonId);
                                wsNode.setNodeId(grandsonId);
                                wsNode.setNodeName(parameterInfoList.get(f).getName());
                                wsNode.setNodeType("1");
                                wsNode.setHasDynChild("true");
                                wsNode.setNodeNoCheck("true");
                                tableList.add(wsNode);
                            }
                        }
                    }
                }
            }
        }catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        return tableList;
    }
    /*
        解析webservice地址或者wsdl文件
     */
    public String analysis(){
        List<TreeNode> tableList;
        List<Port> ports = new ArrayList<Port>();
        List<Object> listjson = new ArrayList<Object>();
        Port port;
        try{
            WSDLFactory factory=WSDLFactory.newInstance();
            WSDLReader reader;
            reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose",true);
            reader.setFeature("javax.wsdl.importDocuments",true);
			/*File file=this.getMyFile();
			if(!file.exists()||file.isDirectory())
				throw new FileNotFoundException();
			FileInputStream fis=new FileInputStream(file);
			byte[] buf = new byte[512];
			StringBuffer sb=new StringBuffer();
			while((fis.read(buf))!=-1){
				sb.append(new String(buf));
				buf=new byte[512];//重新生成，避免和上次读取的数据重复
			}
			fis.close();
			String tmpDirectory = GenerationCfgUtil.getDEEHome() + "/temp.wsdl";
			File f = new File(tmpDirectory);
			FileOutputStream out = new FileOutputStream(f);
			byte bytes[]=new byte[512];
			String msg = sb.toString();
			bytes=msg.getBytes();
			int b =msg.length();
			out.write(bytes,0,b);
			out.close();
			Definition defee =  reader.readWSDL(tmpDirectory);*/
			/*String address = "http://www.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl";*/
                Definition def = reader.readWSDL(address);
            // 解析服务名
            String nameSpace = null;
            Map namespaceMap = def.getNamespaces();
            boolean flag = false;
            Iterator namespaceIter = namespaceMap.keySet().iterator();
            while(namespaceIter.hasNext()){
                Object key = namespaceIter.next( );
                if(key.equals("tns") || key.equals("ns")){
                    nameSpace =(String)namespaceMap.get(key);
                }
            }
            Map serviceMap = def.getServices();
            //解析接口方法名
            Iterator serviceIter = serviceMap.keySet().iterator();
            List<Service> listService = new ArrayList<Service>();
            while(serviceIter.hasNext()){
                Object key = serviceIter.next( );
                listService.add((Service)serviceMap.get(key)) ;
            }

            tableList = treeSet(listService,def);

            for(Service service:listService){
                Map mapPort = service.getPorts();
                Iterator portIter = mapPort.keySet().iterator();

                while (portIter.hasNext()){
                    Object key = portIter.next();
                    port = (Port) mapPort.get(key);
                    ports.add(port);
                }
            }
            /*String wsURL = ports.get(0).getExtensibilityElements().get(0).toString();
            String[] temp = wsURL.split("=");
            String tempUrl = "";
            if(temp.length>3){
                tempUrl = temp[temp.length-2] + "=" + temp[temp.length-1];
            }else {
                tempUrl= temp[temp.length-1];
            }*/
            String tempUrl = def.getDocumentBaseURI().toString().substring(0,def.getDocumentBaseURI().toString().length()-5);
            listjson.add(tempUrl);
            listjson.add(nameSpace);
            listjson.add(tableList);
            net.sf.json.JSON json = JSONSerializer.toJSON(listjson);
            this.jsonData = json.toString();
        }catch(WSDLException e)
        {
            logger.error(e.getMessage(), e);
            return ajaxForwardError(StringEscapeUtils.escapeJavaScript(StringFilter(e.getMessage())));
        } /*catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        return ajaxForwardSuccess("解析成功！");
    }
    
    /**
     * 查看
     * 
     * @return
     */
    public String view() {
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && 
                    bean.getDeeResourceTemplate() != null)
                {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    wsBean = (WSProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return WS_VIEW;
    }
    
    /**
     * 转换
     * 
     * @return true，没有重复项；false，有重复项
     */
    public boolean toDeeResource() {
        boolean exist = true;;
        Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
        
        for (KeyValue item : items) {
			if (item == null) {
				continue;
			}
            if (paraMap.containsKey(item.getKey())) {
                exist = false;
                break;
            }
            paraMap.put(item.getKey(), item.getValue());
        }
        wsBean.setParameter(paraMap);
        
        return exist;
    }
    
    private FlowSubBean getFlowSubBean(DeeResourceBean drb) {
        FlowSubBean sub = new FlowSubBean();
        FlowBean flowBean = new FlowBean();
        flowBean.setFLOW_ID(flowId);
        
        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);
        
        return sub;
    }

    public static String StringFilter(String str){

        return str.replaceAll("'", "").replaceAll("\"", "");
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public WSProcessorBean getWsBean() {
        return wsBean;
    }

    public void setWsBean(WSProcessorBean wsBean) {
        this.wsBean = wsBean;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<KeyValue> getItems() {
        return items;
    }

    public void setItems(List<KeyValue> items) {
        this.items = items;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
