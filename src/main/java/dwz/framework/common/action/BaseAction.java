package dwz.framework.common.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware, ServletContextAware {
	
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -823790409467802488L;
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext context;
	
	/**
	 * "json"字符串
	 */
	protected String JSON = "json";
	protected String JSON_PAGE = "json";
	
	protected static final String OPERATION_DONE = "operationDone";
	/** ajax返回码，200--成功，300--失败 */
	private int statusCode = 200;
	/** ajax返回信息 */
	private String message = null;
	/** ajax返回的uuid */
	private String uuid = null;
	
	/**
	 * Acion代理对象
	 */
	private ActionProxy proxy;
	
	/**
	 * 分页
	 */
	public final static int PAGE_SHOW_COUNT = 20;
	private int pageNum = 1;			// 第几页
	private int numPerPage = 0;			// 每页记录数
	private String orderField;		// order field
	private String orderDirection;	// asc or desc
	private String keywords;        // 关键字
    private int totalCount = 0;     // 总记录数
	
	//......
	private String _;
	
	public BaseAction() {
		super();
		this.proxy = ActionContext.getContext().getActionInvocation().getProxy();
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
	
	public String getOperationDone() {
		if (this.isAjax() || request.getParameter("ajax") != null) {
			return "ajaxDone";
		} else {
			return "alert";
		}
	}

	public ActionProxy getProxy() {
		return proxy;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getNumPerPage() {
		return numPerPage > 0 ? numPerPage : PAGE_SHOW_COUNT;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	
	public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
	
	public boolean isAjax() {
		if (request != null && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")))
			return true;
		return false;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	private String ajaxForward(int statusCode) {
		this.statusCode = statusCode;
		return OPERATION_DONE;
	}
	
	protected String ajaxForwardSuccess(String message) {
		this.message = message;
		return ajaxForward(200);
	}
	
	protected String ajaxForwardSuccess(String message, String uuid) {
        this.message = message;
        this.uuid = uuid;
        return ajaxForward(200);
    }
	
	protected String ajaxForwardError(String message) {
		this.message = message;
		return ajaxForward(300);
	}

	public String get_() {
		return _;
	}

	public void set_(String _) {
		this._ = _;
	}
	
	public String realOrderField(){
		if ("desc".equalsIgnoreCase(orderDirection))
			return orderField+"_DESC";
		return orderField;
	}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
