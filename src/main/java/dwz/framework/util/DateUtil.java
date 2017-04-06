package dwz.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

	public static String getSysTime(){
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_STANDARD);
		return sdf.format(new Date());
		
	}
}
