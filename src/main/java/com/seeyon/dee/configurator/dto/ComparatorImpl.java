package com.seeyon.dee.configurator.dto;

import java.util.Comparator;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
/**
 * 适配器sort执行顺序排序
 * @author yangyu
 *
 */
public class ComparatorImpl implements Comparator<FlowSubBean> {

	@Override
	public int compare(FlowSubBean fsb0, FlowSubBean fsb1) {
		// TODO Auto-generated method stub
		int flag=fsb0.getSort()-fsb1.getSort();
		return flag;
	}

}
