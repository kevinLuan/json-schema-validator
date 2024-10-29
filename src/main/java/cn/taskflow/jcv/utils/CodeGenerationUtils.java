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
 * Code generation utility class for generating Java code from JSON schemas.
 * This class provides methods to convert JSON data into Java code representations
 * and to handle serialization and deserialization of JSON schemas.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2023-04-16
 */
public class CodeGenerationUtils {
    // ThreadLocal to store GenerateOptional instances for thread-safe operations
    static ThreadLocal<GenerateOptional> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Retrieves the current GenerateOptional instance from the ThreadLocal storage.
     * If no instance is present, a default instance is returned.
     *
     * @return the current GenerateOptional instance or a default one if none is set
     */
    public static GenerateOptional getOptional() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        } else {
            return new GenerateOptional(false, false, false);
        }
    }

    /**
     * Generates Java code for a JSON schema.
     *
     * @param json the JSON string representing the schema
     * @return the generated Java code as a String
     */
    public static String generateSchemaCode(String json) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * Generates Java code for a JSON schema with specific options.
     *
     * @param json the JSON string representing the schema
     * @param option the GenerateOptional instance specifying generation options
     * @return the generated Java code as a String
     */
    public static String generateSchemaCode(String json, GenerateOptional option) {
        try {
            THREAD_LOCAL.set(option);
            JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
            return SchemaCodeGenerator.generate(jsonSchema);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * Generates Java code for a JSON schema using a SchemaProcess option.
     *
     * @param json the JSON string representing the schema
     * @param option the SchemaProcess instance specifying processing options
     * @return the generated Java code as a String
     */
    public static String generateSchemaCode(String json, SchemaProcess option) {
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json, option);
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * Generates Java code for a JSON schema using SchemaOptions.
     *
     * @param json the JSON string representing the schema
     * @param option the SchemaOptions instance specifying options
     * @return the generated Java code as a String
     */
    public static String generateSchemaCode(String json, SchemaOptions option) {
        return generateSchemaCode(json, option.getSchemaProcess());
    }

    /**
     * Generates Java code from a JsonSchema object.
     *
     * @param jsonSchema the JsonSchema object
     * @return the generated Java code as a String
     */
    public static String generateSchemaCode(JsonSchema jsonSchema) {
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * Generates a JSON example data string from a JsonSchema object.
     *
     * @param jsonSchema the JsonSchema object
     * @return the JSON example data as a String
     */
    public static String generateSampleData(JsonSchema jsonSchema) {
        return JsonSchemaCodec.toJsonDataExample(jsonSchema);
    }

    /**
     * Generates a JsonSchema object from JSON data.
     *
     * @param jsonData the JSON string representing the data
     * @return the generated JsonSchema object
     */
    public static JsonSchema generateParamFromJson(String jsonData) {
        return JsonParser.parseJsonSchema(jsonData);
    }

    /**
     * Generates Java code from a JsonSchema object.
     *
     * @param jsonSchema the JsonSchema object used to generate code
     * @return the generated Java code as a String
     */
    public static String generateCodeFromParam(JsonSchema jsonSchema) {
        return SchemaCodeGenerator.generate(jsonSchema);
    }

    /**
     * Serializes a JsonSchema object into a JSON string.
     *
     * @param product the JsonSchema object to serialize
     * @return the serialized JSON string
     */
    public static String serialization(JsonSchema product) {
        return GsonEncoder.INSTANCE.encode(product);
    }

    /**
     * Deserializes a JSON string into a JsonSchema object.
     *
     * @param paramDefine the JSON string representing the schema definition
     * @return the deserialized JsonSchema object
     */
    public static JsonSchema deserialization(String paramDefine) {
        return GsonEncoder.INSTANCE.decode(paramDefine, JsonBasicSchema.class);
    }
}
