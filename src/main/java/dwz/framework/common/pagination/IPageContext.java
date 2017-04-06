package dwz.framework.common.pagination;


/**
 * 分页上下文，用于计算Page
 *
 */
public interface IPageContext<E> {
    
    /**
     * 默认设定每页显示记录数为10
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 计算总页数
     * 
     * @return
     */
    public int getPageCount();
    

    /**
     * 返回 Page对象
     * 
     * @param index 当前页码
     * @return
     */
    public Page<E> getPage(int index);
    
    /**
     * 每页显示的记录数
     * 
     * @return
     */
    public int getPageSize();
    
    /**
     * 计算总记录数
     * 
     * @return
     */
    public int getTotalCount();
    
}
