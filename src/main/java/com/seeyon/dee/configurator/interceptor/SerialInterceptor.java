package com.seeyon.dee.configurator.interceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.seeyon.v3x.dee.common.base.util.AuthorizeUtil;
import com.seeyon.v3x.dee.common.base.util.Encipher;

import com.seeyon.v3x.dee.util.Constants;
import com.seeyon.v3x.dee.util.DataChangeUtil;

public class SerialInterceptor extends MethodFilterInterceptor {
	private static final long serialVersionUID = 4085963428017959650L;
	private final static String filePath = DataChangeUtil.getRealPath("/") +
			"config" + Constants.SERIAL_PROPERTY_FILENAME;

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
		/*ActionContext actionContext = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(StrutsStatics.HTTP_REQUEST);
		String errorMsg = "";
		InputStream in = new FileInputStream(filePath);
		Properties property = new Properties();
		property.load(in);
		in.close();

		// 判断授权码是否与识别码匹配
		String keycode = property.getProperty(Constants.PROPERTYKEY_KEYCODE);
		String SerialNumber = property
				.getProperty(Constants.PROPERTYKEY_SERIALNUMBER);
		String encode_authorizeDate = property
				.getProperty(Constants.PROPERTYKEY_AUTHORIZEDATE);
		String authorizeDate = Encipher.Decode(encode_authorizeDate);
		String encode_deadline = property
				.getProperty(Constants.PROPERTYKEY_DEADLINE);
		String deadline = Encipher.Decode(encode_deadline);

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
			errorMsg += "请勿修改系统时间！";
		} else {
			// System.out.println(keycode);
			// System.out.println(SerialNumber);
			if (keycode == null
					|| !AuthorizeUtil.getK(encode_authorizeDate,
							encode_deadline).equals(keycode)
					|| !AuthorizeUtil.getS(keycode, "250").equals(SerialNumber)) {
				errorMsg += "识别码与授权码不匹配！";
			}
			// 判断授权是否过期
			if (authorizeDate == null || "".equals(authorizeDate.trim())
					|| deadline == null || "".equals(deadline.trim())) {
				errorMsg += "授权日期和期限为空！";
				request.setAttribute(Constants.REQUEST_KEY_EXPIRED, true);
			} else {
				// System.out.println(authorizeDate);
				// System.out.println(deadline);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date begin = df.parse(authorizeDate);
					Calendar calBegin = Calendar.getInstance();
					calBegin.setTime(begin);
					Calendar calNow = Calendar.getInstance();
					if (calNow.before(calBegin)) {
						// 授权期限前
						errorMsg += "未进入授权期限！";
					}
					calBegin.add(Calendar.MONTH, Integer.parseInt(deadline));
					if (calNow.after(calBegin)) {
						// 已过期
						errorMsg += "授权已过期，请重新注册！";
						request.setAttribute(Constants.REQUEST_KEY_EXPIRED,
								true);
					}
				} catch (ParseException e) {
					// 注册日期错误
					errorMsg += "授权日期错误！";
				}
			}
		}
		//
		if (!errorMsg.trim().equals("")) {
			request.setAttribute(Constants.REQUEST_MESSAGE_ERROR, errorMsg);
			request.setAttribute(
					Constants.REQUEST_KEY_AUTHORIZEDATE,
					authorizeDate = null == encode_authorizeDate
							|| "".equals(encode_authorizeDate.trim()) ? (new SimpleDateFormat(
							"yyyy-MM-dd")).format(new Date(System
							.currentTimeMillis())) : Encipher
							.Decode(encode_authorizeDate));
			request.setAttribute(Constants.REQUEST_KEY_DEADLINE, deadline);
			request.setAttribute(Constants.REQUEST_KEY_KEYCODE, keycode);
			request.setAttribute(Constants.REQUEST_KEY_SERIALNUMBER,
					SerialNumber);
            return "serial";
		}*/
		return invocation.invoke();
	}
}
