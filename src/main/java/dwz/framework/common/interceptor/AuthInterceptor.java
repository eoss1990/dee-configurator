package dwz.framework.common.interceptor;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class AuthInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 3848261977272444173L;

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        String isLogin = (String)ServletActionContext.getRequest().getSession().getAttribute("isLogin");
        if ("true".equals(isLogin)) {
            return actionInvocation.invoke();   // 调用拦截器责任链
        } else {
            return Action.LOGIN;                // 返回到登录页面
        }
    }

}
