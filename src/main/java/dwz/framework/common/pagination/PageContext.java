package dwz.framework.common.pagination;

import java.util.List;

/**
 * 动态分页实现，每次查询返回一页记录.
 * @author charice59
 *
 * @param <E>
 */
public class PageContext<E> implements IPageContext<E>{
	
	private List<E> items;		// 当前页包含的记录列表
	private int totalCount;		// 总记录数
	private int pageSize;		// 每页显示记录数
	
	public PageContext(int totalCount, int pageSize, List<E> items) {
		this.totalCount = totalCount;
		this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
		this.items = items;
	}

	/**
     * 返回 Page对象
     * 
     * @param index 当前页码
     * @return
     */
	public Page<E> getPage(int index) {
		Page<E> page = new Page<E>();
		page.setContext(this);
		
		int pageCount = getPageCount();
		int index2 = index > pageCount ? pageCount : index;
		
		page.setHasNext(index2 < pageCount);
		page.setHasPrev(index2 > 1);
		page.setIndex(index2);
		page.setItems(items);
		
		return page;
	}

	/**
	 * 计算总页数
	 * 
	 * @return
	 */
	 public int getPageCount() {
		 int div = totalCount / pageSize;
		 int result = (totalCount % pageSize == 0) ? div : div + 1;

		 return result;
	 }

	 public int getTotalCount() {
		 return this.totalCount;
	 }

	 public int getPageSize() {
		 return this.pageSize;
	 }
}
