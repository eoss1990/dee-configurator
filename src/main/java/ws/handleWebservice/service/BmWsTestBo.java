package ws.handleWebservice.service;

import java.util.List;

import ws.handleWebservice.bean.ParameterInfo;

import javax.wsdl.Definition;

/**
 * @author zhengtian
 * 
 * @date 2011-8-4 下午10:53:34
 */
@SuppressWarnings("all")
public interface BmWsTestBo {
	/**
	 * 根据方法名称和webserviceUrl得到参数
	 * 
	 * @param methodName
	 * @param webserviceUrl
	 * @return
	 * @throws Exception
	 */
	public List<ParameterInfo> getParamByMethodNameAndWsUrl(String methodName, Definition def) throws Exception;

}
