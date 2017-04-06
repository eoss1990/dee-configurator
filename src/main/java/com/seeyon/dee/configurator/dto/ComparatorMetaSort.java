package com.seeyon.dee.configurator.dto;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 元数据多表排序
 * @author yangyu
 *
 */
public class ComparatorMetaSort implements Comparator<MetaData>{

	private static Log log = LogFactory.getLog(ComparatorMetaSort.class);
	
	public int compare(MetaData o1, MetaData o2) {
		int index1 = 0;
		int index2 = 0;
		try{
			index1 = Integer.parseInt(o1.getOrderNo());
			index2 = Integer.parseInt(o2.getOrderNo());	
		}catch(Exception e){
			log.error(e.getMessage());
		}
		if(index1 > index2){
			return 1;
		}else if(index1 == index2){
			return 0;
		}else {
			return -1;
		}
	}
	
 

}
