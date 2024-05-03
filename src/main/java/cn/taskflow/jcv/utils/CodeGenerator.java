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
        return GsonEncoder.INSTANCE.decode(paramDefine, JsonBasicSchema.class);
    }
}
