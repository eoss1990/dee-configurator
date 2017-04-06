package com.seeyon.dee.configurator.taglibs.functions;

/**
 * 字符串处理类
 *
 * @author zhangfb
 */
public class StringUtils {
    /**
     * 去掉DEE的强制转换符，<p/>
     * 如：“[[DeeCast_AccountNameToId]]单位名称”，可以转换为“单位名称”
     *
     * @param str 传入字符串
     * @return 转换后的字符串
     */
    public static String dropDeeCast(String str) {
        int start = str.indexOf("[[");
        int end = str.indexOf("]]");
        if (start >= 0 && end > start) {
            return str.substring(end + 2).trim();
        }
        return str;
    }

    public static String createDeeHref(String str) {
        int start = str.indexOf("[[dee_select_");
        int end = str.indexOf("]]");
        if (start >= 0 && end > start) {
            return " readonly onclick='javascript:" + str.substring(2, end).trim() + "(this);' ";
        }
        return "";
    }
}
