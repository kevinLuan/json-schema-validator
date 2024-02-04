package io.github.jcv.utils;

import io.github.jcv.core.JsonBase;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.encode.GsonEncoder;

/**
 * 代码自动生成
 *
 * @author SHOUSHEN.LUAN
 * @since 2023-04-16
 */
public class CodeGenerator {
    public static String generateCode(String json) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        return JavaCodeGenerator.generateCode(jsonSchema);
    }

    public static String generateCode(JsonSchema jsonSchema) {
        return JavaCodeGenerator.generateCode(jsonSchema);
    }

    /**
     * 生成json示例数据
     *
     * @return
     */
    public static String generateSampleData(JsonSchema jsonSchema) {
        return JsonSchemaCodec.toJsonDataExample(jsonSchema);
    }

    /**
     * 生成参数定义
     *
     * @param jsonData
     * @return
     */
    public static JsonSchema generateParamFromJson(String jsonData) {
        return JsonParser.parseJsonSchema(jsonData);
    }

    /**
     * 生成代码
     *
     * @param jsonSchema 根据参数定义生成代码
     * @return
     */
    public static String generateCodeFromParam(JsonSchema jsonSchema) {
        return JavaCodeGenerator.generateCode(jsonSchema);
    }

    /**
     * 序列化参数定义
     *
     * @param product
     * @return
     */
    public static String serialization(JsonSchema product) {
        return GsonEncoder.INSTANCE.encode(product);
    }

    /**
     * 根据参数定义反序列化
     *
     * @param paramDefine
     * @return
     */
    public static JsonSchema deserialization(String paramDefine) {
        return GsonEncoder.INSTANCE.decode(paramDefine, JsonBase.class);
    }
}
