package io.github.jcv.utils;

import io.github.jcv.json.api.GsonSerialize;
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
        return GsonSerialize.INSTANCE.encode(jsonSchema);
    }

    /**
     * 根据参数定义反序列化
     *
     * @param paramJson
     * @return
     */
    public static JsonSchema decode(String paramJson) {
        return GsonSerialize.INSTANCE.decode(paramJson, JsonBase.class);
    }
}
