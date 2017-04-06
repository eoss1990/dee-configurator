package dwz.framework.util;

import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.resource.DbDataSource;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zhangfb
 */
public class DEEClientUtil {
    public static DEEClient newClient() {
        return new DEEClient();
    }

    public static DbDataSource lookupDbDataSource(String id) throws TransformException {
        return DEEClientUtil.lookup(id, DbDataSource.class);
    }

    public static <T> T lookup(String id, Class<T> clazz) throws TransformException {
        try {
            return (T) newClient().lookup(id);
        } catch (Throwable throwable) {
            if (throwable instanceof InvocationTargetException ) {
                throw new TransformException(((InvocationTargetException) throwable).getTargetException());
            } else if (throwable instanceof TransformException) {
                throw (TransformException)throwable;
            } else {
                throw new TransformException(throwable);
            }
        }
    }
}
