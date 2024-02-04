package io.github.jcv.utils;

import io.github.jcv.codec.GsonEncoder;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.core.JsonBase;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-04-18
 */
public class Encoder {
    /**
     * 序列化参数定义
     *
     * @param jsonSchema
     * @return
     */
    public static String encode(JsonSchema jsonSchema) {
        return GsonEncoder.INSTANCE.encode(jsonSchema);
    }

    /**
     * 根据参数定义反序列化
     *
     * @param paramJson
     * @return
     */
    public static JsonSchema decode(String paramJson) {
        return GsonEncoder.INSTANCE.decode(paramJson, JsonBase.class);
    }
}
