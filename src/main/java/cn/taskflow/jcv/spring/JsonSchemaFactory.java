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
package cn.taskflow.jcv.spring;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.validation.Validator;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * JsonSchemaFactory is responsible for managing and providing access to JSON schema definitions.
 * It retrieves JSON schema beans from the Spring application context and provides methods to
 * validate JSON data against these schemas.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
public class JsonSchemaFactory {
    private final Map<String, JsonSchema> schemaMap;

    /**
     * Constructs a JsonSchemaFactory with schemas retrieved from the given application context.
     * The schemas are stored in an unmodifiable map to ensure immutability.
     *
     * @param context the Spring application context from which JSON schema beans are retrieved
     */
    public JsonSchemaFactory(ApplicationContext context) {
        schemaMap = Collections.unmodifiableMap(context.getBeansOfType(JsonSchema.class));
    }

    /**
     * Retrieves an Optional containing the JsonSchema associated with the given schema name.
     * If no schema is found, an empty Optional is returned.
     *
     * @param schemaName the name of the schema to retrieve
     * @return an Optional containing the JsonSchema if found, otherwise an empty Optional
     */
    public Optional<JsonSchema> getSchema(String schemaName) {
        return Optional.ofNullable(schemaMap.get(schemaName));
    }

    /**
     * Validates the provided JSON data against the schema specified by the JsonSchemaValidate annotation.
     * If the schema is not found or validation fails, an IllegalArgumentException is thrown.
     *
     * @param schemaValidate the annotation containing the name of the schema to validate against
     * @param json the JSON data to be validated
     * @throws IllegalArgumentException if the schema is not found or validation fails
     */
    public void validate(JsonSchemaValidate schemaValidate, String json) {
        Optional<JsonSchema> optional = getSchema(schemaValidate.value());
        if (optional.isPresent()) {
            try {
                Validator.fromSchema(optional.get()).validate(json);
            } catch (Exception e) {
                if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
                    throw (IllegalArgumentException) e;
                } else {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        } else {
            throw new IllegalArgumentException(
                String.format("schema:'%s' definition not found", schemaValidate.value()));
        }
    }
}
