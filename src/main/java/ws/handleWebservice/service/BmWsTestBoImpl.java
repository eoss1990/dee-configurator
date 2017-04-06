package ws.handleWebservice.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import ws.handleWebservice.util.WsdlUtil;
import ws.handleWebservice.bean.ParameterInfo;

import javax.wsdl.Definition;
import javax.wsdl.xml.WSDLWriter;


/**
 * @author zhengtian
 * 
 * @date 2011-8-4 下午10:53:53
 */
@SuppressWarnings("all")
public class BmWsTestBoImpl implements BmWsTestBo {
	private Logger logger = Logger.getLogger(BmWsTestBoImpl.class);

	/**
	 * 将webserviceUrl路径转为合法的路径
	 * 
	 * @param webserviceUrl
	 * @return
	 * @throws Exception
	 */
	/*private String getWebserviceUrl(String webserviceUrl) throws Exception {
		if (StringUtils.isNotEmpty(webserviceUrl)) {
			// 判断url里面是否存在wsdl后缀
			if (webserviceUrl.indexOf("?") >= 0) {
				if (!webserviceUrl.endsWith("wsdl")) {
					webserviceUrl = new StringBuilder(webserviceUrl).append("wsdl").toString();
				}
			} else {
				webserviceUrl = new StringBuilder(webserviceUrl).append("?wsdl").toString();
			}
			return webserviceUrl;
		} else {
			return "";
		}
	}*/

	/**
	 * 根据方法名称和webserviceUrl得到参数
	 * 
	 * @param methodName
	 * @param webserviceUrl
	 * @return
	 * @throws Exception
	 */
	public List<ParameterInfo> getParamByMethodNameAndWsUrl(String methodName, Definition def) throws Exception {
		try {

			WSDLWriter writer =WsdlUtil.getWsdlFactory().newWSDLWriter();
			Document document = writer.getDocument(def);

			// 返回结果
			List<ParameterInfo> inputParamList = new ArrayList<ParameterInfo>();
			// 解析参数
			StringBuilder xpathBuilder = new StringBuilder();
			WsdlUtil.getInputParam(inputParamList, document, methodName, xpathBuilder, null, false);
			return inputParamList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将字符串的输入参数转换为List<ParameterInfo>
	 * 
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	private List<ParameterInfo> convertStrToListParam(String paramStr) throws Exception {
		List<ParameterInfo> result = new ArrayList<ParameterInfo>();
		try {
			JSONArray jsonArray = JSONArray.fromObject(paramStr);
			convertStrToListParam(result, jsonArray);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * 循环递归进行转换
	 * 
	 * @param paramList
	 * @param jsonArray
	 * @throws Exception
	 */
	private void convertStrToListParam(List<ParameterInfo> paramList, JSONArray jsonArray) throws Exception {
		try {
			for (int i = 0; i < jsonArray.size(); i++) {
				Object obj = jsonArray.get(i);
				String name = PropertyUtils.getProperty(obj, "name") == null ? "" : PropertyUtils.getProperty(obj, "name").toString();
				String value = PropertyUtils.getProperty(obj, "value") == null ? "" : PropertyUtils.getProperty(obj, "value").toString();
				String type = PropertyUtils.getProperty(obj, "type") == null ? "" : PropertyUtils.getProperty(obj, "type").toString();

				String childrenStr = PropertyUtils.getProperty(obj, "children") == null ? "" : PropertyUtils.getProperty(obj, "children").toString();
				JSONArray children = null;
				if (StringUtils.isNotEmpty(childrenStr)) {
					children = JSONArray.fromObject(childrenStr);
				}

				ParameterInfo param = new ParameterInfo();
				PropertyUtils.setProperty(param, "name", name);
				PropertyUtils.setProperty(param, "value", value);
				PropertyUtils.setProperty(param, "type", type);

				// 递归子参数
				if (StringUtils.isNotEmpty(childrenStr) && children.size() > 0) {
					convertStrToListParam(param.getChildren(), children);
				}
				paramList.add(param);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
