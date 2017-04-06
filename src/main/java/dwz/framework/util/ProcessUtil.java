package dwz.framework.util;

public class ProcessUtil {

	public static String toString(String resourceId,String statusCode,int sort,String flowId)
	{		
		StringBuffer sb = new StringBuffer(resourceId);
		sb.append(",");
		sb.append(statusCode);
		sb.append(",");
		sb.append(sort);
		sb.append(",");
		sb.append(flowId);
		return sb.toString();
	}
}

