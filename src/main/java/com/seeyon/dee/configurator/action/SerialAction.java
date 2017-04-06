package com.seeyon.dee.configurator.action;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.dee.common.base.util.AuthorizeUtil;
import com.seeyon.v3x.dee.common.base.util.Encipher;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import com.seeyon.v3x.dee.util.DataChangeUtil;

/**
 * 授权Action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-31下午04:08:06
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class SerialAction extends BaseAction {

	private static final long serialVersionUID = 6113828385432884535L;
	
	private static Log log = LogFactory.getLog(SerialAction.class);
	
	private static final String contentType = "application/x-msdownload";
	private static final String REGFLAG_SUCCESS = "true";
	private static final String REGFLAG_FAILURE = "false";
	private final static String filePath = DataChangeUtil.getRealPath("/") +
			"config" + Constants.SERIAL_PROPERTY_FILENAME;
	
	private String AuthorizeDate;
	private String Deadline;
	private String SerialNumber;
	private String keyCode;
	private String regFlag = REGFLAG_FAILURE;
	
	private String errorMsg;

	/**
	 * 获取授权码文件
	 */
	public void getKeycode() {
		if (StringUtils.isBlank(AuthorizeDate) || StringUtils.isBlank(Deadline)) {
			errorMsg = "请输入授权日期和期限！";
			return;
		}
		
		String encode_authorizeDate = Encipher.Encode(AuthorizeDate);
		String encode_deadline = Encipher.Encode(Deadline);
		keyCode = AuthorizeUtil.getK(encode_authorizeDate, encode_deadline);

		Properties property = new Properties();
		FileOutputStream out = null;
		FileInputStream in = null;
		try {
		    in = new FileInputStream(filePath);
		    property.load(in);
		    property.setProperty(Constants.PROPERTYKEY_KEYCODE, keyCode);
		    property.setProperty(Constants.PROPERTYKEY_AUTHORIZEDATE, encode_authorizeDate);
		    property.setProperty(Constants.PROPERTYKEY_DEADLINE, encode_deadline);

		    out = new FileOutputStream(filePath);
		    property.store(out, "KEYCODE");
		    out.flush();

		    DataChangeUtil.renderStream(response, contentType, new FileInputStream(filePath),
		            "Content-Disposition", "attachment; filename=\"dee.key\"");
		} catch (Exception e) {
		    log.error("读取授权文件失败:" + e.getLocalizedMessage(), e);
		} finally {
		    try {
		        if (null != in) in.close();
		        if (null != out) out.close();
		    } catch (IOException e) {
		        log.error(e.getMessage(), e);
		    }
		}
	}

	/**
	 * 注册授权
	 * 
	 * @return
	 */
	public String doSerial() {
		Properties property = new Properties();
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try {
		    in = new FileInputStream(filePath);
            property.load(in);
            keyCode = property.getProperty(Constants.PROPERTYKEY_KEYCODE);
            
		    if (StringUtils.isNotBlank(keyCode) &&
                AuthorizeUtil.getS(keyCode, "250").equals(SerialNumber)) {
		        
	            property.setProperty(Constants.PROPERTYKEY_SERIALNUMBER, SerialNumber);
	            
	            out = new FileOutputStream(filePath);
	            property.store(out, "SERIAL");
	            out.flush();
		        return ajaxForwardSuccess("成功！");
		    } else  {
		        return ajaxForwardError("授权码错误！");
		    }
		} catch (IOException e) {
			log.error("保存识别码失败:" + e.getLocalizedMessage(), e);
			return ajaxForwardError(e.getMessage());
		} finally {
			try {
				if (null != in) in.close();
				if (null != out) out.close();
			} catch (IOException e) {
			    log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 打开注册授权页面
	 * 
	 * @return
	 */
	public String viewReg() {
		InputStream in = null;
		regFlag = REGFLAG_FAILURE;
		String error = "";
		try {
			in = new FileInputStream(filePath);
			Properties property = new Properties();
			property.load(in);
			// 判断授权码是否与识别码匹配
			keyCode = property.getProperty(Constants.PROPERTYKEY_KEYCODE);
			SerialNumber = property.getProperty(Constants.PROPERTYKEY_SERIALNUMBER);
			String encode_authorizeDate = property.getProperty(Constants.PROPERTYKEY_AUTHORIZEDATE);
			if (StringUtils.isBlank(encode_authorizeDate)) {
			    AuthorizeDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
			} else {
			    AuthorizeDate = Encipher.Decode(encode_authorizeDate);
			}
			String encode_deadline = property.getProperty(Constants.PROPERTYKEY_DEADLINE);
			Deadline = Encipher.Decode(encode_deadline);
			
            String logfilesPath = DataChangeUtil.getRealPath("/") + "../../../../logs";
            File logsPath = new File(logfilesPath);
            Date lastDate = null;
            if (logsPath.exists() && logsPath.isDirectory()) {
                File[] logs = logsPath.listFiles();
                for (File file : logs) {
                    if (lastDate == null)
                        lastDate = new Date(file.lastModified());
                    else
                        lastDate = lastDate.getTime() > file.lastModified() ? lastDate
                                : new Date(file.lastModified());
                }
            }
            if (null != lastDate && new Date().before(lastDate)) {
                error += "请勿修改系统时间！";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date begin = dateFormat.parse(AuthorizeDate);
                    Calendar calBegin = Calendar.getInstance();
                    calBegin.setTime(begin);
                    Calendar calNow = Calendar.getInstance();
                    if (calNow.before(calBegin)) {
                        error += "未进入授权期限！";
                    }
                    calBegin.add(Calendar.MONTH, Integer.parseInt(Deadline));
                    if (calNow.after(calBegin)) {
                        error += "授权已过期，请重新注册！";
                    }
                } catch (ParseException e) {
                    error += "授权日期错误！";
                }
            }
			
			if ("".equals(error) &&
			    StringUtils.isNotBlank(keyCode) &&
			    AuthorizeUtil.getK(encode_authorizeDate, encode_deadline).equals(keyCode) &&
			    AuthorizeUtil.getS(keyCode, "250").equals(SerialNumber)) {
			    regFlag = REGFLAG_SUCCESS;
			}
		} catch (IOException e) {
			log.error("读取授权文件失败:" + e.getLocalizedMessage(), e);
		} finally {
			try {
				if (null != in) in.close();
			} catch (IOException e) {
			    log.error(e.getMessage(), e);
			}
		}
		return INPUT;
	}

    public String getAuthorizeDate() {
        return AuthorizeDate;
    }

    public void setAuthorizeDate(String authorizeDate) {
        AuthorizeDate = authorizeDate;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getRegFlag() {
        return regFlag;
    }

    public void setRegFlag(String regFlag) {
        this.regFlag = regFlag;
    }
}
