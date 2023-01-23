package byx.orm.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 方法上下文，封装Dao接口方法的信息
 */
public class MethodContext {
    private final Method method;
    private final Object[] args;
    private final Map<String, Object> argsMap;

    public MethodContext(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        this.argsMap = argsToMap();
    }

    /**
     * 获取方法对象
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取实参列表
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 获取实参名称和值组成的Map
     */
    public Map<String, Object> getArgsMap() {
        return argsMap;
    }

    private Map<String, Object> argsToMap() {
        Parameter[] parameters = method.getParameters();
        String[] paramNames = Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new);
        Map<String, Object> map = new HashMap<>(10);
        for (int i = 0; i < paramNames.length; ++i) {
            map.put(paramNames[i], args[i]);
        }
        return map;
    }
}
