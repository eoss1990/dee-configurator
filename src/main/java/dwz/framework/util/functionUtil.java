package dwz.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.seeyon.dee.configurator.dto.CodePkgDto;
import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;
import com.seeyon.v3x.dee.common.db.codelib.model.CodePkgBean;


/**   
 *   
 *   @package：dwz.framework.util.functionUtil.java       
 *   @author    chenmeng <br/>    
 *   @create-time   2016年3月18日   上午11:09:02     
 **/
public class functionUtil {
	
	private static final Log log = LogFactory.getLog(functionUtil.class);
	
	private final static String encoding = "UTF-8";
	private static Document document;
	private static List<Map<String, String>> funs = new ArrayList<Map<String,String>>();
	
	
	private functionUtil() {
		
	};
	
	private static void getDocument(){
		if(document != null)
			return;
		InputStream in = null;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			in = classloader.getResourceAsStream("function/function.xml");
			SAXReader reader = new SAXReader();
			InputSource source = new InputSource(in);
			source.setEncoding(encoding);
			document = reader.read(source);
			if (document.getXMLEncoding() == null) {
				document.setXMLEncoding(encoding);
			}
			parseDocument(document);
		} catch (Exception e) {
			log.error("函数库初始化异常："+e.getLocalizedMessage());
		}finally{
			try {
				if(in!=null)
					in.close();
			} catch (IOException e) {
				log.error(e.getLocalizedMessage());
			}
		}
	}
 
	
	private static void parseDocument(Document doc){
		if(doc == null)
			return;
		Map<String,String> fun =null;  
		Element root = doc.getRootElement();
		List<Element> its = root.elements("function");// root.elementIterator();  
		if(its!=null && its.size()>0){
			for(Element element:its){
				List<Element> els =  element.elements();
				if(els!=null && els.size()>0){
					fun =new HashMap<String,String>();  
					for(Element e:els){
						fun.put(e.getName(),e.getText());
						if("isClick".equals(e.getName()) && "true".equals(e.getText())){
							fun.put("icon", "/styles/dee/themes/images/icon_function.png");
						}
					}
				 
					funs.add(fun);
				}
				
			}
		}  
	}

	/**
	 * 根据系统函数id获取系统函数子节点
	 * @param id
	 * @return
	 */
	public static List<Map<String,String>> getFunsByPid(String id){
		if(id == null)
			return null;
		List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
		getDocument();
		for(Map<String,String> m :funs){
			if(m.get("pId")!=null && m.get("pId").equals(id)){
				fs.add(m);
			}
		}
		return fs;
	}
	
	/**
	 * 获取所有系统函数节点
	 * @return
	 */
	public static List<Map<String,String>> getAllFuns(){
		List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
		getDocument();
		for(Map<String,String> m :funs){
			fs.add(m);
		}
		return fs;
	}
 
	
	public static Map<String,String> packageAdapter(CodePkgDto bean){
		if(bean == null)
			return null;
		Map<String,String> m = new HashMap<String, String>();	
		m.put("id", bean.getId());
		if("com.seeyon.dee.codelib".equals(bean.getId())){
			m.put("pId", "2");
		}else{
			m.put("pId", bean.getParentId());
		}
		m.put("isParent", "true");
		m.put("name", bean.getName());
		m.put("content", "");
		m.put("params", "");
		m.put("description", "");
		m.put("type", "user");
		m.put("isClick", "false");
		
		return m;
	}

	public static List<Map<String,String>> packageAdapter(List<CodePkgDto> beans){
		if(beans == null)
			return null;
		List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
		for(CodePkgDto pk:beans){
			Map<String,String> m = packageAdapter(pk);
			fs.add(m);
		}
		return fs;
	}
	
	public static List<Map<String,String>> classAdapter(List<CodeLibBean> beans){
		if(beans == null)
			return null;
		List<Map<String,String>> fs = new ArrayList<Map<String,String>>();
		for(CodeLibBean cs:beans){
			Map<String,String> m = classAdapter(cs);
			fs.add(m);
		}
		return fs;
	}
	
	public static Map<String,String> classAdapter(CodeLibBean bean){
		if(bean == null)
			return null;
		Map<String,String> m = new HashMap<String, String>();	
		m.put("id", bean.getId());
		m.put("pId", bean.getPkgName());
		m.put("isParent", "false");
		m.put("name", bean.getClassName());
		m.put("content", "import "+ bean.getPkgName() +"."+ bean.getClassName()+";\n");
		m.put("params", "");
		m.put("description", bean.getSimpleDesc());
		m.put("type", "user");
		m.put("isClick", "true");
		m.put("icon", "/styles/dee/themes/images/icon_class.png");
		return m;
	}
	
	
	public static String string2Json(String json) {   
		StringBuffer  newstr = new StringBuffer();  
		for (int i=0; i<json.length(); i++) {  
			char c = json.charAt(i);
			switch (c) {       
				case '\"':       
					newstr.append("\\\"");       
					break;       
				case '\\':       
					newstr.append("\\\\");       
					break;       
				case '/':       
					newstr.append("\\/");       
					break;       
				case '\b':       
					newstr.append("\\b");       
	 				break;       
				case '\f':       
	               	newstr.append("\\f");       
	                break;       
				case '\n':       
	                newstr.append("\\n");       
	                break;       
				case '\r':       
		            newstr.append("\\r");       
	                break;       
				case '\t':       
		            newstr.append("\\t");       
	                break;       
				default:       
		        	newstr.append(c);       
		   }  
		}  
		return newstr.toString();       
	 }  
}
