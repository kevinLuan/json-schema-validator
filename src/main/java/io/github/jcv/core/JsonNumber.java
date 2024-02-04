package io.github.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonNumber extends Primitive {
    public JsonNumber(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static JsonNumber required(String name, String description) {
        return new JsonNumber(name, true, DataType.Number, description, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @return
     */
    public static JsonNumber make() {
        return new JsonNumber("", true, DataType.Number, null, null, null);
    }

    public static JsonNumber ofNullable() {
        return new JsonNumber("", false, DataType.Number, null, null, null);
    }

    public static JsonNumber optional(String name, String description) {
        return new JsonNumber(name, false, DataType.Number, description, null, null);
    }

}
