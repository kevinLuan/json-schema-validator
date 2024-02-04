package io.github.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonBoolean extends Primitive {
    public JsonBoolean(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static JsonBoolean required(String name, String description) {
        return new JsonBoolean(name, true, DataType.Boolean, description, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @return
     */
    public static JsonBoolean make() {
        return new JsonBoolean("", true, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean ofNullable() {
        return new JsonBoolean("", true, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean optional(String name, String description) {
        return new JsonBoolean(name, false, DataType.Boolean, description, null, null);
    }

}
