package com.seeyon.dee.configurator.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;

public class VersionInfoAction extends BaseAction {

    private static final long serialVersionUID = -642012768678491333L;
    
    private static Log log = LogFactory.getLog(VersionInfoAction.class);
    
    private String buildDate;
    private String version;

    public String view() {
        InputStream in = null;
        Properties p = null;
        try {
            in = this.getClass().getResourceAsStream(
                            Constants.CONFIGURATOR_SYSTEM_PROPERTY_FILENAME);
            p = new Properties();
            p.load(in);
            buildDate = (String) p.get("product.build.date");
            version = (String) p.get("product.build.version");
        } catch (FileNotFoundException e) {
            log.error("查看版本号文件异常；" + e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("读取文件异常：" + e.getLocalizedMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("关闭文件异常：" + e.getLocalizedMessage());
                }
            }
        }
        return INPUT;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
