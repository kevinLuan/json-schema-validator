package cn.taskflow.jcv.extension;

import cn.taskflow.jcv.core.DataType;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.Primitive;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public interface SchemaProcess {

    /**
     * 是否必须
     *
     * @param name
     * @return
     */
    boolean isRequired(String name, DataType type, JsonSchema valueSchema);

    boolean isRequired(String name, DataType type, JsonSchema... valueSchemas);

    boolean isRequired(String name, DataType type, Primitive valueSchema);

    boolean isOptional(String name, DataType dataType);
}
