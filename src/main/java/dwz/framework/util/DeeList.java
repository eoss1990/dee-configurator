package dwz.framework.util;

public class DeeList {
	public String createQueryString(String className,boolean useCount, String orderField, String createTime)
	{
		StringBuilder sb = new StringBuilder();
		sb
		.append(
				useCount ? "select count(obj) "
						: "select obj ")
		.append(
				"from "+className.trim()+" as obj");
		
		if (useCount)
			return sb.toString();
		
		if (orderField != null && orderField.length() > 0) {
			if(orderField.endsWith("_DESC"))
			{
				sb.append(" order by obj."+orderField.trim().substring(0,(orderField.length()-5))+" desc");
			}
			else
			{
				sb.append(" order by obj."+orderField.trim());
			}
		}else
		{
			sb.append(" order by obj."+createTime+" desc");
		}
		
		return sb.toString();
	}
	
	public String createQueryString(String className,boolean useCount, String orderField,String fieldName,String keywords,String createTime)
	{
		StringBuilder sb = new StringBuilder();
		sb
		.append(
				useCount ? "select count(obj) "
						: "select obj ")
		.append(
				"from "+className.trim()+" as obj");
		
		if (fieldName != null && fieldName.length() > 0)
		{
		    if ("flowType.FLOW_TYPE_ID".equals(fieldName)) {
		        if (keywords != null &&
		            !"".equals(keywords) && 
		            !"0".equals(keywords)) {
		            sb.append(" where obj."+fieldName.trim()+" = '"+keywords.trim()+"'");
		        }
		    } else {
		        if (keywords.indexOf("%")!=-1)
		        {
		            StringBuilder key = new StringBuilder(keywords);
		            key.insert(keywords.indexOf("%"), "\\");
		            sb.append(" where obj."+fieldName.trim()+" like '%"+key.toString()+"%'");
		            sb.append(" ESCAPE '\\'");
		            /*				sb.append(" where obj."+fieldName.trim()+" like '%\\%%' ESCAPE '\\'");*/
		        }
		        else
		            sb.append(" where obj."+fieldName.trim()+" like '%"+keywords.trim()+"%'");
		    }
		}
		
		if (useCount)
			return sb.toString();
		
		if (orderField != null && orderField.length() > 0) {
			if(orderField.endsWith("_DESC"))
			{
				sb.append(" order by obj."+orderField.trim().substring(0,(orderField.length()-5))+" desc");
			}
			else
			{
				sb.append(" order by obj."+orderField.trim());
			}
		}
		else
		{
			sb.append(" order by obj."+createTime+" desc");
		}
		return sb.toString();
	}
	
	public static String createQueryStr(String className, boolean useCount, String orderField, String fieldName, String fieldValue,String createTime)
	{
	    StringBuilder sb = new StringBuilder();
	    sb.append(useCount ? "select count(obj) " : "select obj ")
	        .append("from " + className.trim() + " as obj");
	    
	    if (fieldName != null && fieldName.length() > 0) {
	        sb.append(" where obj." + fieldName.trim() + " = '" + fieldValue.trim() + "'");
	    }
	    
	    if (useCount) {
	        return sb.toString();
	    }
	    
	    if (orderField != null && orderField.length() > 0) {
	        if (orderField.endsWith("_DESC")) {
	            sb.append(" order by obj." + orderField.trim().substring(0, (orderField.length() - 5)) + " desc");
	        } else {
	            sb.append(" order by obj." + orderField.trim());
	        }
	    }else
		{
			sb.append(" order by obj."+createTime+" desc");
		}
	    return sb.toString();
	}

}
