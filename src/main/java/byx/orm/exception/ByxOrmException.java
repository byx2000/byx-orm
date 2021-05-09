package byx.orm.exception;

/**
 * ByxOrm异常基类
 *
 * @author byx
 */
public class ByxOrmException extends RuntimeException {
    public ByxOrmException(String msg) {
        super(msg);
    }

    public ByxOrmException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
