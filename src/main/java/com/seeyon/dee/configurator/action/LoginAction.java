package com.seeyon.dee.configurator.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.dto.UserPwd;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import com.seeyon.v3x.dee.util.DataChangeUtil;

/**
 * 系统相关action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午01:28:18
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class LoginAction extends BaseAction {
    
    private static final long serialVersionUID = -6199346646110211549L;
    
    private static Logger logger = Logger.getLogger(LoginAction.class.getName());

    private final static String INDEX = "index";
    private final static String LOGIN_FILEPATH = DataChangeUtil.getRealPath("/") + Constants.CONFIGURATOR_LOGIN_PROPERTY_FILENAME;
    
    private UserPwd userPwd;
    private String pwd;
    private String errorMsg;
    
    private int paramType = Constants.PARAMETER_TYPE_SYSTEM;
    
    private Map<Object, Object> systemParams;
    private Map<Object, Object> customParams;
    private List<KeyValue> items;

    /**
     * 密码修改
     * 
     * @return
     */
    public String editPwd() {
        if (userPwd == null) {
            return INPUT;
        }
        
        if (StringUtils.isBlank(userPwd.getOldPwd())) {
            return ajaxForwardError("原密码不能为空！");
        } else if (StringUtils.isBlank(userPwd.getNewPwd()) || StringUtils.isBlank(userPwd.getRnewPwd())) {
            return ajaxForwardError("新密码不能为空！");
        } else if (!userPwd.getNewPwd().equals(userPwd.getRnewPwd())) {
            return ajaxForwardError("两次密码不一致！");
        }
        
        InputStream in = null;
        OutputStream out = null;
        Properties p = null;
        
        try {
            in = new FileInputStream(LOGIN_FILEPATH);
            p = new Properties();
            p.load(in);
            if (p.getProperty("password").equals(this.getMD5Str(userPwd.getOldPwd()))) {
                out = new FileOutputStream(LOGIN_FILEPATH);
                p.setProperty("password", this.getMD5Str(userPwd.getNewPwd()));
                p.store(out, "changePwd");
                out.flush();
                return ajaxForwardSuccess("修改密码成功！");
            } else {
                return ajaxForwardError("原密码输入错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxForwardError("异常！");
        } finally {
            try {
                if (null != in){
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                return ajaxForwardError("异常！");
            }
        }
    }
    
    /**
     * 跳转到首页
     * 
     * @return
     */
    public String welcome() {
        return "welcome";
    }
    
    /**
     * 退出系统
     * 
     * @return
     */
    public String logout() {
        this.request.getSession().invalidate();
        return LOGIN;
    }
    
    /**
     * 登录系统
     * 
     * @return
     */
    public String login() {
        if (StringUtils.isBlank(pwd)) {
            // 密码为空，跳转到首页
            return LOGIN;
        }
        
        InputStream in = null;
        try {
            in = new FileInputStream(LOGIN_FILEPATH);
            Properties p = new Properties();
            p.load(in);

            pwd = getMD5Str(pwd);
            String srcPwd = (String) p.get("password");
            if (checkPassword(srcPwd, pwd)) {
                request.getSession().setAttribute("isLogin", "true");
                return INDEX;
            } else {
                this.errorMsg = "密码不正确！";
                return LOGIN;
            }
        } catch (Exception e) {
            logger.error("Login Exception :" + e.getLocalizedMessage(), e);
            this.errorMsg = "异常！";
            return LOGIN;
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("Login Exception :" + e.getLocalizedMessage(), e);
                this.errorMsg = "异常！";
                return LOGIN;
            }
        }
    }
    
    public String checkLogin() {
        String ret = login();
        if (INDEX == ret) {
            return ajaxForwardSuccess("登录成功！");
        } else {
            return ajaxForwardError("密码错误！");
        }
    }
    
    /**
     * 系统参数设置
     * 
     * @return
     */
    public String paramsSetting() {
        // 系统参数
        String configPath = getParamsConfigPath(Constants.PARAMETER_TYPE_SYSTEM);
        Properties systemProperties = DataChangeUtil.loadProperties(configPath);
        this.systemParams = DataChangeUtil.propToMap(systemProperties);
        
        // 自定义全局参数
        configPath = getParamsConfigPath(Constants.PARAMETER_TYPE_CUSTOM);
        Properties customProperties = DataChangeUtil.loadProperties(configPath);
        this.customParams = DataChangeUtil.propToMap(customProperties);

        return INPUT;
    }
    
    /**
     * 保存系统参数
     * 
     * @return
     */
    public String saveParamsSetting() {
        String configPath = getParamsConfigPath(paramType);
        
        if (configPath == null) {
            return ajaxForwardError("参数错误！");
        }
        
        Properties prop = new Properties();
        for (KeyValue params : (items != null ? items:new ArrayList<KeyValue>())) {
            if (params != null) {
                if (prop.getProperty(params.getKey()) != null) {
                    return ajaxForwardError(Constants.PARAMSCHECKMSG);
                }
                prop.setProperty(params.getKey(), params.getValue());
            }
        }
        
        if (DataChangeUtil.storeProperties(prop, configPath)) {
            return ajaxForwardSuccess("操作成功！");
        } else {
            return ajaxForwardError("异常！");
        }
    }
    
    private Boolean checkPassword(String srcPwd, String pwd) {
        Boolean b = false;
        if (srcPwd.equals(pwd)) {
            b = true;
        }
        return b;
    }
    
    /**
     * 获取MD5加密串
     * 
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String getMD5Str(String str) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }
    
    /**
     * 获取系统参数文件路径
     * 
     * @param paramType
     * @return
     */
    private String getParamsConfigPath(int paramType) {
        String deeHome = DataChangeUtil.getProperty(Constants.DEE_HOME);
        String configPath = null;
        
        if (Constants.PARAMETER_TYPE_SYSTEM == paramType) {
            // 系统参数
            configPath = deeHome + File.separator + "conf" + File.separator + "config.properties";
        } else if (Constants.PARAMETER_TYPE_CUSTOM == paramType) {
            // 自定义全局参数
            configPath = deeHome + File.separator + "conf" + File.separator + "dee-resource.properties";
        }
        
        return configPath;
    }
    
    public UserPwd getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(UserPwd userPwd) {
        this.userPwd = userPwd;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }
    
    public Map<Object, Object> getSystemParams() {
        return systemParams;
    }

    public void setSystemParams(Map<Object, Object> systemParams) {
        this.systemParams = systemParams;
    }

    public Map<Object, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<Object, Object> customParams) {
        this.customParams = customParams;
    }

    public List<KeyValue> getItems() {
        return items;
    }

    public void setItems(List<KeyValue> items) {
        this.items = items;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
