/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.codegen;

import cn.taskflow.jcv.core.JsonBasicSchema;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.encode.GsonEncoder;
import cn.taskflow.jcv.extension.SchemaOptions;
import cn.taskflow.jcv.extension.SchemaRequirementEvaluator;
import cn.taskflow.jcv.utils.JsonParser;
import cn.taskflow.jcv.utils.JsonSchemaCodec;

/**
 * 用于从JSON模式生成Java代码的代码生成实用程序类。
 * 该类提供了将JSON数据转换为Java代码表示的方法，并处理JSON模式的序列化和反序列化。
 *
 * @作者 SHOUSHEN.LUAN
 * @自 2023-04-16
 */
public class CodeGenerationUtils {
    static ThreadLocal<GenerateOptions> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 从ThreadLocal存储中检索当前的GenerateOptional实例。
     * 如果没有实例存在，则返回一个默认实例。
     *
     * @return 当前的GenerateOptional实例，如果没有设置则返回默认实例
     */
    public static GenerateOptions getOptions() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        } else {
            return GenerateOptions.defaultOptions();
        }
    }

    /**
     * 为JSON模式生成Java代码。
     *
     * @param json 表示模式的JSON字符串
     * @return 生成的Java代码作为字符串
     */
    public static String generateSchemaCode(String json) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    public static String generateSchemaCode(Class<?> type, GenerateOptions options) {
        String json = MockDataGenerator.getJsonMock(type, MockOptions.defaultOptions());
        return generateSchemaCode(json, options);
    }

    public static <T> String generateSchemaCode(TypeReference<T> typeRef, GenerateOptions options) {
        String json = MockDataGenerator.getJsonMock(typeRef, MockOptions.defaultOptions());
        return generateSchemaCode(json, options);
    }

    /**
     * 使用特定选项为JSON模式生成Java代码。
     *
     * @param json   表示模式的JSON字符串
     * @param options 指定生成选项的GenerateOptional实例
     * @return 生成的Java代码作为字符串
     */
    public static String generateSchemaCode(String json, GenerateOptions options) {
        try {
            THREAD_LOCAL.set(options);
            JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
            return SchemaCodeGenerator.generate(jsonSchema);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 使用SchemaProcess选项为JSON模式生成Java代码。
     *
     * @param json   表示模式的JSON字符串
     * @param option 指定处理选项的SchemaProcess实例
     * @return 生成的Java代码作为字符串
     */
    public static String generateSchemaCode(String json, SchemaRequirementEvaluator option) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json, option);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * 使用SchemaOptions为JSON模式生成Java代码。
     *
     * @param json   表示模式的JSON字符串
     * @param option 指定选项的SchemaOptions实例
     * @return 生成的Java代码作为字符串
     */
    public static String generateSchemaCode(String json, SchemaOptions option) {
        return generateSchemaCode(json, option.getSchemaProcess());
    }

    /**
     * 从JsonSchema对象生成Java代码。
     *
     * @param jsonSchema JsonSchema对象
     * @return 生成的Java代码作为字符串
     */
    public static String generateSchemaCode(JsonSchema jsonSchema) {
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * 从JsonSchema对象生成JSON示例数据字符串。
     *
     * @param jsonSchema JsonSchema对象
     * @return JSON示例数据作为字符串
     */
    public static String generateSampleData(JsonSchema jsonSchema) {
        return JsonSchemaCodec.toJsonDataExample(jsonSchema);
    }

    /**
     * 从JSON数据生成JsonSchema对象。
     *
     * @param jsonData 表示数据的JSON字符串
     * @return 生成的JsonSchema对象
     */
    public static JsonSchema generateParamFromJson(String jsonData) {
        return JsonParser.parseJsonSchema(jsonData);
    }

    /**
     * 从JsonSchema对象生成Java代码。
     *
     * @param jsonSchema 用于生成代码的JsonSchema对象
     * @return 生成的Java代码作为字符串
     */
    public static String generateCodeFromParam(JsonSchema jsonSchema) {
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * 将JsonSchema对象序列化为JSON字符串。
     *
     * @param product 要序列化的JsonSchema对象
     * @return 序列化的JSON字符串
     */
    public static String serialization(JsonSchema product) {
        return GsonEncoder.INSTANCE.encode(product);
    }

    /**
     * 将JSON字符串反序列化为JsonSchema对象。
     *
     * @param paramDefine 表示模式定义的JSON字符串
     * @return 反序列化的JsonSchema对象
     */
    public static JsonSchema deserialization(String paramDefine) {
        return GsonEncoder.INSTANCE.decode(paramDefine, JsonBasicSchema.class);
    }
}
