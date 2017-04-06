package dwz.framework.exception;

public class SystemException extends Exception {

    /** ... */
    private static final long serialVersionUID = 6034595526168142888L;

    public SystemException() {
        super();
    }

    public SystemException(String s) {
        super(s);
    }

    public SystemException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SystemException(Throwable throwable) {
        super(throwable);
    }
}
