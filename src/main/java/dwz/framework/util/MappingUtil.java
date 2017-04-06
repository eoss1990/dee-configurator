package dwz.framework.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MappingUtil {
	private final static Log log = LogFactory.getLog(MappingUtil.class);
	  //求两个数组各自的差集
    @SuppressWarnings("unchecked")
	public static List[] minus(String[] arr1, String[] arr2) {       
        List<String> arr1List = new LinkedList<String>();     
        LinkedList<String> history = new LinkedList<String>();       
        LinkedList<String> arr2List = new LinkedList<String>();       
        for (String str : arr1) {      
           if (!arr1List.contains(str)) {       
                arr1List.add(str);       
           } 
        }
        for (String str : arr2) {       
            if (arr1List.contains(str)) {       
                history.add(str); //交集     
                arr1List.remove(str); //差集 去除相同的 
           } else {  //差集 如果不包含短数组值      
                if (!history.contains(str)) {// 如果交集里也没有      
                   arr2List.add(str);
                } 
            }       
        }
        
        List[] result = new List[2];  
        result[0] = arr1List;
        result[1] = arr2List;
        return result;  
   }
    /**
     * @description 将数组转换成字符串
     * @date 2012-1-17
     * @author liuls
     * @param array 数组对象
     * @return 特殊格式的串
     */
    public static String array2String(String[] array) {
    	Set <String>set = new HashSet<String>();
        for (int i = 0; i < array.length; i++) {
        	if(array[i]!=null){
        		set.add(array[i].trim());
        	}
        }
        String temp = set.toString();
        if(temp!=null){ //去掉空格，去掉中括号
        	temp= temp.replace("[","").replace("]","").replaceAll(" ", "");
        }
        return temp;
    }
    
    public static String getNull2Str(String inputStr){
		return inputStr == null?"":inputStr;
	}

    
    @SuppressWarnings("unchecked")
	public static void main(String[] args) { 
		 String[] arr1 = {"abc", "aaaa", "abc"};       
         String[] arr2 = {"abc", "cc", "df", "d", "abc"};       
		 List[] result_minus = minus(arr1, arr2);       
	     log.info("求差集的结果如下：");       
	     for (List list : result_minus) {       
	           log.info(list.toString());       
	     }     
	 }


}
