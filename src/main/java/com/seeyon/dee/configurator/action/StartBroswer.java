package com.seeyon.dee.configurator.action;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.dee.util.Constants;
import com.seeyon.v3x.dee.util.DataChangeUtil;

public class StartBroswer {
	private final static Log log = LogFactory.getLog(StartBroswer.class);
	private final static String LOGIN_FILEPATH = DataChangeUtil
			.getRealPath("/") + Constants.CONFIGURATOR_LOGIN_PROPERTY_FILENAME;

	public void init() {
		String URL = "http://localhost:8085";
		String osName = System.getProperty("os.name");
		Properties p = null;
		InputStream in = null;
		try {
			in = new FileInputStream(LOGIN_FILEPATH);
			p = new Properties();
			p.load(in);
			String url = (String) p.get("url");
			if (StringUtils.isNotBlank(url)) {
				URL = url;
			}
			if (osName.startsWith("Mac OS")) {
				// Mac
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { URL });
			} else if (osName.startsWith("Windows")) {
				// Windows
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + URL);
			} else {
				// assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser != null) {
					Runtime.getRuntime().exec(new String[] { browser, URL });
				}
			}
		} catch (Exception ex) {
			log.error("初始化打开浏览器异常，Exception:" + ex.getLocalizedMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// ignore it
				}
		}
	}
}
