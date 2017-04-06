package com.seeyon.dee.configurator.dto;

import java.util.Comparator;
/**
 * 元数据多表排序
 * @author yangyu
 *
 */
public class ComparatorMeta implements Comparator<MetaData>{

	@Override
	public int compare(MetaData md0, MetaData md1) {
		// TODO Auto-generated method stub
		int flag = md0.getFormName().compareToIgnoreCase(md1.getFormName());
		if(flag==0)
		{
			return md0.getDbFormName().compareToIgnoreCase(md1.getDbFormName());
		}else{
			return flag;
		}  
	}

}
