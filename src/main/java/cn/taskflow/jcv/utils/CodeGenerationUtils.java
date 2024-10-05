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
package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.JsonBasicSchema;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.encode.GsonEncoder;
import cn.taskflow.jcv.extension.SchemaOptions;
import cn.taskflow.jcv.extension.SchemaProcess;

/**
 * 代码自动生成
 *
 * @author SHOUSHEN.LUAN
 * @since 2023-04-16
 */
public class CodeGenerationUtils {
    static ThreadLocal<GenerateOptional> THREAD_LOCAL = new ThreadLocal<>();

    public static GenerateOptional getOptional() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        } else {
            return new GenerateOptional(false, false, false);
        }
    }

    /**
     * 生成Schema Java代码
     *
     * @param json
     * @return
     */
    public static String generateSchemaCode(String json) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    public static String generateSchemaCode(String json, GenerateOptional option) {
        try {
            THREAD_LOCAL.set(option);
            JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
            return SchemaCodeGenerator.generate(jsonSchema);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    public static String generateSchemaCode(String json, SchemaProcess option) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json, option);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    public static String generateSchemaCode(String json, SchemaOptions option) {
        return generateSchemaCode(json, option.getSchemaProcess());
    }

    public static String generateSchemaCode(JsonSchema jsonSchema) {
        return SchemaCodeGenerator.generate(jsonSchema);
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
        return SchemaCodeGenerator.generate(jsonSchema);
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
        return GsonEncoder.INSTANCE.decode(paramDefine, JsonBasicSchema.class);
    }
}
