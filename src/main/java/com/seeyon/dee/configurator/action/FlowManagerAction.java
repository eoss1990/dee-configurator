package com.seeyon.dee.configurator.action;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.mgr.FlowManagerMgr;
import com.seeyon.v3x.dee.TransformFactory;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.exportflow.ExportFlow;
import com.seeyon.v3x.dee.util.DesUtil;
import com.seeyon.v3x.dee.util.ZipUtil;
import dwz.framework.common.action.BaseAction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
/**
 * 任务管理相关逻辑
 * @author yangyu
 *
 */
public class FlowManagerAction extends BaseAction{
	
	private static final long serialVersionUID = -1184221672354035240L;

	private String ids;
	
	@Autowired
    @Qualifier("flowManager")
	private FlowManagerMgr flowMgr;
	
	private static Log log = LogFactory.getLog(FlowManagerAction.class);
	private String contentType = "application/x-msdownload";
	static final String configFile = "configurator.properties";
	private String enc = "utf-8";
	
	private String flowTypeId;

	/**
	 * 任务列表
	 * @return
	 */
	public String deelist()
	{
	    int pageNum = this.getPageNum() > 0 ? this.getPageNum() - 1 : 0;
        int startIndex = pageNum * getNumPerPage();
        Collection<FlowBean> flowList = null;
        
		try {
			if (StringUtils.isNotBlank(flowTypeId)) {
			    flowList = flowMgr.searchFlowBeanByTypeId(realOrderField(), startIndex, getNumPerPage(), flowTypeId);
			    setTotalCount(flowMgr.searchFlowBeanNum("FlowBean","flowType.FLOW_TYPE_ID",flowTypeId));
			} else {
			    flowList = flowMgr.searchFlowBean(realOrderField(),startIndex, getNumPerPage(),getKeywords());
			    if(getKeywords()==null)
			    	setTotalCount(flowMgr.searchFlowBeanNum("FlowBean","DIS_NAME",""));
			    else
			    	setTotalCount(flowMgr.searchFlowBeanNum("FlowBean","DIS_NAME",getKeywords()));
			}
			ActionContext.getContext().put("flowList", flowList);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return INPUT;
	}
	
	/**
	 * 任务批量删除
	 * @return
	 */
	public String deleteAll()
	{
		String[] flowIds = ids.split(",");
		try {
				for(int i=0;i<flowIds.length;i++)
				{
					flowMgr.delete(flowIds[i]);
				}
				return ajaxForwardSuccess(getText("msg.operation.success"));
			}catch (Exception e) {
				log.error(e.getMessage(), e);
				return ajaxForwardError("删除失败");
			}
			
	}
	
	/**
	 * 任务批量导出
	 */
	public void export()
	{
        String flowids = ids;
        try {
            if (flowids == null || "".equalsIgnoreCase(flowids)) {
                log.error("未取得flow的ID");
                return;
            }
            ExportFlow e = new ExportFlow();
            String xmlStr = e.doReader(flowids).toString();
            if ("".equalsIgnoreCase(xmlStr))
                return;

			String filePath = TransformFactory.getInstance().getConfigFilePath("dee-resource.properties");
			String propFile = URLEncoder.encode("dee-resource.properties", enc);
			copyFile(filePath,propFile,true);
			File pf = new File(propFile);


            String filename = URLEncoder.encode("dee.xml", enc);
            File f = new File(filename);
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(xmlStr);
            fw1.close();
            
            String zipFileName = URLEncoder.encode("dee.drp", enc);
            ZipOutputStream zout = new ZipOutputStream(new File(zipFileName));
            ZipUtil.zip(zout, f, "");
            ZipUtil.zip(zout, pf, "");
            zout.flush();
            zout.close();
          
            String deeNewPath = pf.getAbsolutePath();
            
            
            DesUtil desUtil = new DesUtil("drp_encrypt");
            desUtil.encryptFile(zipFileName,deeNewPath);
            File pf1 = new File(deeNewPath);
            response.reset();
            response.setContentType(contentType);
        
            response.addHeader("Content-Disposition", "attachment; filename="+zipFileName);
            OutputStream responseOS = response.getOutputStream();
        
            FileInputStream in = new FileInputStream(pf1); 
            int len; 
            while ((len = in.read()) != -1) 
            responseOS.write(len);
            
            response.flushBuffer();
            response.setStatus(HttpServletResponse.SC_OK);
            responseOS.close();
            in.close();
            f.delete();
            pf.delete();
            pf1.delete();
            DeleteFolder(deeNewPath);//文件导出后调用方法删除加密后的本地文件
            
        }catch (Exception e) {
            log.error(e.getStackTrace());
        }
		return;
	}
	
	
	//文件导出后调用方法删除加密后的本地文件
	/** 
	 *  根据路径删除指定的目录或文件，无论存在与否 
	 *@param sPath  要删除的目录或文件 
	 *@return 删除成功返回 true，否则返回 false。 
	 */  
	private boolean DeleteFolder(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 判断目录或文件是否存在  
	    if (!file.exists()) {  // 不存在返回 false  
	        return flag;  
	    } else {  
	        // 判断是否为文件  
	        if (file.isFile()) {  // 为文件时调用删除文件方法  
	            return deleteFile(sPath);  
	        } else {  // 为目录时调用删除目录方法  
	            return deleteDirectory(sPath);  
	        }  
	    }  
	}  
	
	
	
	//删除文件夹的方法
	/** 
	 * 删除目录（文件夹）以及目录下的文件 
	 * @param   sPath 被删除目录的文件路径 
	 * @return  目录删除成功返回true，否则返回false 
	 */  
	public boolean deleteDirectory(String sPath) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}

	/**
	 * 复制单个文件
	 *
	 * @param srcFileName
	 *            待复制的文件名
	 * @param destFileName
	 *            目标文件名
	 * @param overlay
	 *            如果目标文件存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 */
	private boolean copyFile(String srcFileName, String destFileName,
								   boolean overlay) {
		File srcFile = new File(srcFileName);

		// 判断源文件是否存在
		if (!srcFile.exists()) {
			log.error("源文件：" + srcFileName + "不存在！");
			return false;
		} else if (!srcFile.isFile()) {
			log.error("复制文件失败，源文件：" + srcFileName + "不是一个文件！");
			return false;
		}

		// 判断目标文件是否存在
		File destFile = new File(destFileName);
		if (destFile.exists()) {
			// 如果目标文件存在并允许覆盖
			if (overlay) {
				// 删除已经存在的目标文件，无论目标文件是目录还是单个文件
				new File(destFileName).delete();
			}
		} else {
			//如果是相对路径，无需判断如下
			// 如果目标文件所在目录不存在，则创建目录
//			if (!destFile.getParentFile().exists()) {
//				// 目标文件所在目录不存在
//				if (!destFile.getParentFile().mkdirs()) {
//					// 复制文件失败：创建目标文件所在目录失败
//					return false;
//				}
//			}
		}

		// 复制文件
		int byteread = 0; // 读取的字节数
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	//删除文件的方法
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	private boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  
	
	private String getPropFileContext(String content) {
		StringBuffer sb = new StringBuffer();
		try {
			Document doc = DocumentHelper.parseText(content);
			if(doc!=null){
				Element ps = doc.getRootElement().element("dee_flow_parameter");
				if(ps!=null){
					List<Element> rows = ps.elements();
					if(rows!=null && rows.size() > 0){
						for(Element row : rows){
							sb.append("\n").append(row.element("PARA_NAME").getText()).append("=").append(row.element("PARA_VALUE").getText());
						}
					}
				}
			}
			
		} catch (Exception e) {
			log.error("设置资源文件错误：", e); 
		}
		return sb.toString();
	}
	
	private String getPrefix() {
		URL u = this.getClass().getClassLoader().getResource(configFile);
		String f = u.getFile();

		try {
			Properties p = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream(f));
			p.load(in);
			return p.getProperty("resource.export.filter");

		} catch (Exception e) {
			log.error("获取配置文件错误：", e);
		}
		return "";

	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

    public String getFlowTypeId() {
        return flowTypeId;
    }

    public void setFlowTypeId(String flowTypeId) {
        this.flowTypeId = flowTypeId;
    }
	
    
    
}
