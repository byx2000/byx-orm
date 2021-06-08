package byx.orm.core;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 方法上下文
 * 包含Dao接口方法的信息
 *
 * @author byx
 */
public class MethodContext {
    private final Method method;
    private final Object[] args;

    public MethodContext(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "MethodContext{" +
                "method=" + method +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
