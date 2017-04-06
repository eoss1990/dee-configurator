package dwz.framework.common.pagination;

import java.util.List;

import org.springframework.util.StringUtils;

public class PageUtil {
	
	private static final String ASC = "asc";
	private static final String DESC = "desc";

	/**
	 * 获取开始序号
	 * @param pageNum 页码，从1开始
	 * @param pageSize 每页条数
	 * @return
	 */
	public static int getPageStart(int pageNum, int pageSize) {
		return (pageNum - 1) * pageSize;
	}

	/**
	 * 获取开始序号
	 * @param pageNum 页码，从1开始
	 * @param pageSize 每页条数
	 * @param totalCount 总条数
	 * @return
	 */
	public static int getPageStart(int pageNum, int pageSize, int totalCount) {
		int start = getPageStart(pageNum, pageSize);
		if (start >= totalCount) {
			start = 0;
		}
		return start;
	}

	/**
	 * 构造分页对象
	 * 
	 * @param pageNum 当前页码
	 * @param pageSize 每页记录数
	 * @param items 记录列表
	 * @param totalCount 总记录数
	 * @return
	 */
	public static <E> Page<E> getPage(int pageNum, int pageSize, List<E> items, int totalCount) {
		IPageContext<E> pageContext = new PageContext<E>(totalCount, pageSize, items);
		return pageContext.getPage(pageNum);
	}
	
	/**
	 * 创建hql语句
	 * 
	 * @param clazz domain对象
	 * @param orderField 排序列
	 * @param orderDirection 排序方向(desc or asc)
	 * @return
	 */
	public static String createHql(Class clazz, String where, String orderField, String orderDirection) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("from ").append(clazz.getSimpleName()).append(" as obj");
		if (StringUtils.hasText(where)) {
		    buffer.append(" where ").append(where).append(" ");
		}
		if (StringUtils.hasText(orderField)) {
			buffer.append(" order by obj.").append(orderField).append(" ");
			if (DESC.equals(orderDirection)) {
				buffer.append(DESC);
			} else {
				buffer.append(ASC);
			}
		}

		return buffer.toString();
	}
	
	/**
	 * 创建hql记录数语句
	 * 
	 * @param clazz domain对象
	 * @param orderField 排序列
	 * @param orderDirection 排序方向(desc or asc)
	 * @return
	 */
	public static String createHqlCount(Class clazz, String where) {
	    StringBuffer buffer = new StringBuffer();
	    
	    buffer.append("select count(*) from ").append(clazz.getSimpleName()).append(" as obj");
	    if (StringUtils.hasText(where)) {
	        buffer.append(" where ").append(where).append(" ");
	    }
	    
	    return buffer.toString();
	}

}
