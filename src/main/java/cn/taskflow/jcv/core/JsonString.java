package cn.taskflow.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonString extends Primitive {
    public JsonString(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static JsonString required(String name, String description) {
        return new JsonString(name, true, DataType.String, description, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @return
     */
    public static JsonString make() {
        return new JsonString("", true, DataType.String, null, null, null);
    }

    public static JsonString ofNullable() {
        return new JsonString("", true, DataType.String, null, null, null);
    }


    public static JsonString optional(String name, String description) {
        return new JsonString(name, false, DataType.String, description, null, null);
    }

}
